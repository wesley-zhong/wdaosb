package com.wd.erp.asbrpc.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wd.erp.asbrpc.bean.AosbRequest;
import com.wd.erp.asbrpc.bean.AosbResponse;
import com.wd.erp.asbrpc.bean.AsbRequestData;
import com.wd.erp.asbrpc.bean.AsbXmlData;
import com.wd.erp.asbrpc.bean.DetailsItem;
import com.wd.erp.asbrpc.bean.Header;
import com.wd.erp.asbrpc.bean.WdRpcData;
import com.wd.erp.asbrpc.config.AsbConfig;
import com.wd.erp.asbrpc.utils.AresHttpClient;
import com.wd.erp.asbrpc.utils.AsbEncode;
import com.wd.erp.asbrpc.utils.CapitalizedPropertyNamingStrategy;
import com.wd.erp.asbrpc.utils.DBUtils;
import com.wd.erp.asbrpc.utils.TimeUtil;

@Component
public class WdRpcService {
	
	@Inject
	private DataSource dataSource;
	
	@Inject
	private AsbConfig asbConfig;
	
	
	private ObjectMapper objectMapper  = new ObjectMapper();
	
	public void  sendRpcData() throws Exception{
		objectMapper.setPropertyNamingStrategy(new CapitalizedPropertyNamingStrategy());
		String sql = "select top 6 * from SEOutStock_TranRecordView";
		AsbRequestData rpcData = this.getAsbData(sql);
		String jsonData = objectMapper.writeValueAsString(rpcData);		
		System.out.println("data = " + jsonData );
		
		String changeData = asbConfig.getAppSecret() + jsonData + asbConfig.getAppSecret();
		String md5Data    = AsbEncode.md5(changeData);
		String base64Data = AsbEncode.base64(md5Data);
		String sign =   AsbEncode.urlEncode(base64Data);
		AosbRequest  httpRequest = new AosbRequest();
		httpRequest.setAppkey(asbConfig.getAppKey());
		httpRequest.setApptoken(asbConfig.getApptoken());
		httpRequest.setTimestamp(TimeUtil.getNowDate());
		httpRequest.setClient_customerid(asbConfig.getClientCustomerId());
		httpRequest.setData(jsonData);
		httpRequest.setSign(sign);
		httpRequest.setMethod("putSOData");
		httpRequest.setClient_db("FLUXWMS");
		httpRequest.setFormat("JSON");
		httpRequest.setMessageid("SO");
		String result = AresHttpClient.sendHttpPost(asbConfig.getUrl(), httpRequest);
		if(result != null){
			AosbResponse reponse = objectMapper.readValue(result.getBytes(), AosbResponse.class);
			onResponse(reponse);
		}
	}
	
	private AsbRequestData getAsbData(String sql) {
		AsbRequestData requestData = new AsbRequestData();
		AsbXmlData asbXmlData = new AsbXmlData();
		List<Header> headList = new ArrayList<Header>();
		
		requestData.setXmldata(asbXmlData);
		asbXmlData.setHeader(headList);
		try {
			PreparedStatement pstmt = dataSource.getConnection().prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Header header = DBUtils.parseObj(rs, Header.class);
				String fInterID = rs.getString("FInterID");
				List<DetailsItem> detailItemList  = getDetailbyOrder(fInterID);
				header.setDetailsItem(detailItemList);
				headList.add(header);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return requestData;
	}
	
	private List<DetailsItem> getDetailbyOrder(String fInterID) {
		List<DetailsItem> ditailsItemList = new ArrayList<DetailsItem>();
		String sql = "select * from SEOutStock_TranRecordEntryView where FInterID = '" + fInterID + "'";
		try {
			PreparedStatement pstmt = dataSource.getConnection()
					.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
	
			while (rs.next()) {
				DetailsItem detailsItem = DBUtils.parseObj(rs, DetailsItem.class);
				ditailsItemList.add(detailsItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ditailsItemList;
	}
	
	
	private void onResponse(AosbResponse response){
		System.out.println("response = " + response.getResult().getReturnCode() +" dec = "+ response.getResult().getReturnDesc());
		// do some thing when finish http request 
	}
	
	public void print(){
		System.out.println("========== "+ asbConfig.getAppKey() + " tomcat  " + dataSource.getName());
	}
}
