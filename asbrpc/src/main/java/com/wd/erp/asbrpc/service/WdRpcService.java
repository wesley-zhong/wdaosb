package com.wd.erp.asbrpc.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.inject.Inject;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wd.erp.asbrpc.bean.AosbRequest;
import com.wd.erp.asbrpc.bean.AosbResponse;
import com.wd.erp.asbrpc.bean.WdRpcData;
import com.wd.erp.asbrpc.config.AsbConfig;
import com.wd.erp.asbrpc.utils.AresHttpClient;
import com.wd.erp.asbrpc.utils.AsbEncode;
import com.wd.erp.asbrpc.utils.TimeUtil;

@Component
public class WdRpcService {
	
	@Inject
	private DataSource dataSource;
	
	@Inject
	private AsbConfig asbConfig;
	
	
	private ObjectMapper objectMapper  = new ObjectMapper();
	public void  sendRpcData() throws Exception{
		String sql = "select * from sometable";
		WdRpcData rpcData = this.getErpData(sql);
		String jsonData = objectMapper.writeValueAsString(rpcData);
		String changeData = asbConfig.getAppSecret() + jsonData + asbConfig.getAppSecret();
		String md5Data = AsbEncode.md5(changeData);
		String base64Data = AsbEncode.base64(md5Data);
		String sign =   AsbEncode.urlEncode(base64Data);
		AosbRequest  httpRequest = new AosbRequest();
		httpRequest.setAppkey(asbConfig.getAppKey());
		httpRequest.setApptoken(asbConfig.getApptoken());
		httpRequest.setClient_customerid(asbConfig.getClientCustomerId());
		httpRequest.setData(jsonData);
		httpRequest.setSign(sign);
		String nowDate = TimeUtil.getNowDate();
		httpRequest.setTimestamp(nowDate);
		System.out.println("now time = " + nowDate);
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
	
	
	//get all the data from table to send	
	private WdRpcData getErpData(String sql){
		WdRpcData wdData = new WdRpcData();
//		try{
//			PreparedStatement pstmt = dataSource.getConnection().prepareStatement(sql);
//			ResultSet rs = pstmt.executeQuery();
//			int rol = rs.getMetaData().getColumnCount();
//			while(rs.next()){
//				 wdData.setSomeFiled(rs.getString("someFiled"));
//				 /*
//				  * to do set all the file of  wdData
//				  */			
//			}	
//		}catch (Exception e) {
//			e.printStackTrace();
//			// TODO: handle exception
//		} 
		  return wdData;
	}
	
	
	private void onResponse(AosbResponse response){
		System.out.println("response = " + response.getResult().getReturnCode() +" dec = "+ response.getResult().getReturnDesc());
		// do some thing when finish http request 
	}
	
	public void print(){
		System.out.println("========== "+ asbConfig.getAppKey() + " tomcat  " + dataSource.getName());
	}
}
