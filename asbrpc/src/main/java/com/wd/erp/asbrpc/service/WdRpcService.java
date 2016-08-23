package com.wd.erp.asbrpc.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;




import com.fasterxml.jackson.databind.ObjectMapper;
import com.wd.erp.asbrpc.bean.AosbRequest;
import com.wd.erp.asbrpc.bean.AosbResponse;
import com.wd.erp.asbrpc.bean.AsbRequestData;
import com.wd.erp.asbrpc.bean.AsbXmlData;
import com.wd.erp.asbrpc.bean.DetailsItem;
import com.wd.erp.asbrpc.bean.Header;
import com.wd.erp.asbrpc.bean.ResponseOrderBean;
import com.wd.erp.asbrpc.config.AsbConfig;
import com.wd.erp.asbrpc.utils.AresFileReader;
import com.wd.erp.asbrpc.utils.AresHttpClient;
import com.wd.erp.asbrpc.utils.AsbEncode;
import com.wd.erp.asbrpc.utils.CapitalizedPropertyNamingStrategy;
import com.wd.erp.asbrpc.utils.DBUtils;
import com.wd.erp.asbrpc.utils.TimeUtil;

@Component
public class WdRpcService {
	private static int PAGE_COUNT = 100;
	
	@Inject
	private DataSource dataSource;
	
	@Inject
	private AsbConfig asbConfig;
	
	private Logger logger = LoggerFactory.getLogger(WdRpcService.class);
	
	private ObjectMapper objectMapper  = new ObjectMapper();
	
	public void  sendRpcData() throws Exception{
		objectMapper.setPropertyNamingStrategy(new CapitalizedPropertyNamingStrategy());

		String sql = "select * from SEOutStock_TranRecordView ";
		AsbRequestData rpcData = this.getAsbData(sql);
		String jsonData = objectMapper.writeValueAsString(rpcData);		
		logger.info("data ={} " , jsonData );
		
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
			logger.info(" response = {} ", result);
			ObjectMapper robjectMapper  = new ObjectMapper();
			AosbResponse reponse = robjectMapper.readValue(result.getBytes(), AosbResponse.class);
			onResponse(reponse, rpcData);
		}
	}
	
	
	public void sendRpcDataPage() {
		try {
			objectMapper
					.setPropertyNamingStrategy(new CapitalizedPropertyNamingStrategy());
			String sql = "select  * from SEOutStock_TranRecordView";

			List<AsbRequestData> rqeustDataPages = this.getAsbPageData(sql);
			for (AsbRequestData rpcData : rqeustDataPages) {
				String jsonData = objectMapper.writeValueAsString(rpcData);
				String changeData = asbConfig.getAppSecret() + jsonData
						+ asbConfig.getAppSecret();
				logger.info("wd data={}",changeData);
		
				//String utf8ChangeData = new String(changeData.getBytes("UTF-8"));
				String md5Data = AsbEncode.md5(changeData);
				System.out.println("md5 = " + md5Data + "len = "+ changeData.length());
				String base64Data = AsbEncode.base64(md5Data);
				System.out.println("base64 = " + base64Data);
				String sign = AsbEncode.urlEncode(base64Data);
				System.out.println("sign == " + sign);
				AosbRequest httpRequest = new AosbRequest();
				httpRequest.setAppkey(asbConfig.getAppKey());
				httpRequest.setApptoken(asbConfig.getApptoken());
				httpRequest.setTimestamp(TimeUtil.getNowDate());
				httpRequest.setClient_customerid(asbConfig
						.getClientCustomerId());
				httpRequest.setData(jsonData);
				httpRequest.setSign(sign);
				httpRequest.setMethod("putSOData");
				httpRequest.setClient_db("FLUXWMS");
				httpRequest.setFormat("JSON");
				httpRequest.setMessageid("SO");

				String result = null;
				while (result == null) { // for network error
					result = AresHttpClient.sendHttpPost(asbConfig.getUrl(),
							httpRequest);
					if (result != null) {
						logger.info(" response = {} ", result);
						ObjectMapper robjectMapper = new ObjectMapper();
						AosbResponse reponse = robjectMapper.readValue(
								result.getBytes("utf-8"), AosbResponse.class);
						onResponse(reponse, rpcData);
					} else {
						logger.info("net work error will reconnected");
						Thread.sleep(10000);
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		logger.info("sucess!");
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
			logger.error(e.toString());		
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
			logger.error(e.getMessage());
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
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			pstmt.close();
		}
		return ditailsItemList;
	}
	
	
	private void onResponse(AosbResponse response, AsbRequestData  request) throws SQLException{
		///System.out.println("response = " + response.getResult().getReturnCode() +" dec = "+ response.getResult().getReturnDesc());
		// do some thing when finish http request 
		StringBuilder sb = new StringBuilder("insert into SEOutStock_TranRecord(FBillNo,FEntryID,FType,FInfo,FDate) values");
		for(ResponseOrderBean  responseBean : response.getResultInfo()){
			sb.append("(");
			sb.append("'" + responseBean.getOrderNo() + "',");
			sb.append("'0',");
			if(responseBean.getErrorcode().equals("0"))
			  sb.append(0 +",");
			else
				sb.append(1 +",");
			sb.append("'" + responseBean.getErrorcode() +" " + responseBean.getErrordescr() +"'," + "'" +  TimeUtil.getNowDate() +"'),");
		}
		
	//	System.out.println("sql = "+ sb.toString());
		
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(sb.substring(0, sb.length() - 1));
		boolean  rs = pstmt.execute();
		
		logger.info(" rtt ={} sql = {} update count ={}", rs, sb.toString() ,pstmt.getUpdateCount());	
	}
	
	public void print(){
		System.out.println("========== "+ asbConfig.getAppKey() + " tomcat  " + dataSource.getName());
	}
}
