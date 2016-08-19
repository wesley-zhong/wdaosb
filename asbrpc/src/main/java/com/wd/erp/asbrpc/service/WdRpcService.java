package com.wd.erp.asbrpc.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.wd.erp.asbrpc.config.AsbConfig;
import com.wd.erp.asbrpc.utils.AresHttpClient;
import com.wd.erp.asbrpc.utils.AsbEncode;
import com.wd.erp.asbrpc.utils.CapitalizedPropertyNamingStrategy;
import com.wd.erp.asbrpc.utils.DBUtils;
import com.wd.erp.asbrpc.utils.TimeUtil;

@Component
public class WdRpcService {
	private static int PAGE_COUNT = 10;
	
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
		System.out.println("md5 = "+ md5Data);
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
			System.out.println(" response =  " + result);
			ObjectMapper robjectMapper  = new ObjectMapper();
			AosbResponse reponse = robjectMapper.readValue(result.getBytes(), AosbResponse.class);
			onResponse(reponse, rpcData);
		}
	}
	
	
	public  void  sendRpcDataPage() throws Exception{
		objectMapper.setPropertyNamingStrategy(new CapitalizedPropertyNamingStrategy());
		String sql = "select  * from SEOutStock_TranRecordView";
		
		List<AsbRequestData> rqeustDataPages = this.getAsbPageData(sql);
		for(AsbRequestData  rpcData :rqeustDataPages){
			String jsonData   = objectMapper.writeValueAsString(rpcData);		
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
			
			String result = null;
			while(result == null){ //for network error
				result = AresHttpClient.sendHttpPost(asbConfig.getUrl(), httpRequest);
				if(result != null){
					System.out.println(" response =  " + result);
					AosbResponse reponse = objectMapper.readValue(result.getBytes(), AosbResponse.class);
					onResponse(reponse, rpcData);
				}
				else{
					System.out.println("net work error will reconnected");
					Thread.sleep(10000);
				}
			}	
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
	
	private List<AsbRequestData> getAsbPageData(String sql) throws SQLException{		
		List<AsbRequestData>  asbRequestDataList = new ArrayList<AsbRequestData>();
		PreparedStatement pstmt = null;
		try{
			 pstmt = dataSource.getConnection().prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				asbRequestDataList.add(getAsbPageData(rs));
			}
			
		}catch(Exception e){
			
		}
		finally{
		  if(pstmt != null)
			  pstmt.close();
		}
		return asbRequestDataList;
	}
	
	private AsbRequestData getAsbPageData(ResultSet rs) {
		AsbRequestData requestData = new AsbRequestData();
		AsbXmlData asbXmlData = new AsbXmlData();
		List<Header> headList = new ArrayList<Header>();
		requestData.setXmldata(asbXmlData);
		asbXmlData.setHeader(headList);
		try {
			for (int i = 0;  ; ++i) {
				Header header = DBUtils.parseObj(rs, Header.class);
				String fInterID = rs.getString("FInterID");
				List<DetailsItem> detailItemList = getDetailbyOrder(fInterID);
				header.setDetailsItem(detailItemList);
				headList.add(header);
				if(i == PAGE_COUNT - 1)
					return requestData;
				if (!rs.next())
					return requestData;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return requestData;

	}
	
	private List<DetailsItem> getDetailbyOrder(String fInterID) throws SQLException {
		List<DetailsItem> ditailsItemList = new ArrayList<DetailsItem>();
		String sql = "select * from SEOutStock_TranRecordEntryView where FInterID = '" + fInterID + "'";
		PreparedStatement pstmt = null;
		try {
		    pstmt = dataSource.getConnection().prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();	
			while (rs.next()) {
				DetailsItem detailsItem = DBUtils.parseObj(rs, DetailsItem.class);
				ditailsItemList.add(detailsItem);
			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			pstmt.close();
		}
		return ditailsItemList;
	}
	
	
	private void onResponse(AosbResponse response, AsbRequestData  request){
		///System.out.println("response = " + response.getResult().getReturnCode() +" dec = "+ response.getResult().getReturnDesc());
		// do some thing when finish http request 
		StringBuilder sb = new StringBuilder("insert into SEOutStock_TranRecord(FBillNo,FEntryID,FType,FInfo,FDate) values");
		for(Header header :request.getXmldata().getHeader()){
			sb.append("(");
			sb.append("'" + header.getOrderNo() + "'" + ",");
			sb.append(")
		}
		
	}
	
	public void print(){
		System.out.println("========== "+ asbConfig.getAppKey() + " tomcat  " + dataSource.getName());
	}
}
