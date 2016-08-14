package com.wd.erp.asbrpc.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.inject.Inject;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wd.erp.asbrpc.bean.WdRpcData;

@Component
public class WdRpcService {
	
	@Inject
	private DataSource dataSource;
	
	private ObjectMapper objectMapper  = new ObjectMapper();
	public void  sendRpcData(){
		
	}
	
	
	//get all the data from table to send
	
	private WdRpcData getErpcData(){
		WdRpcData wdData = new WdRpcData();
		String selectSql = "select * from sometable";
		try{
			PreparedStatement pstmt = dataSource.getConnection().prepareStatement(selectSql);
			ResultSet rs = pstmt.executeQuery();
			int rol = rs.getMetaData().getColumnCount();
			while(rs.next()){
				 wdData.setSomeFiled(rs.getString("someFiled"));
				 /*
				  * to do set all the file of  wdData
				  */
				
			}	
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		} 
		  return wdData;
	}
	

	
	
	private int sendAsbRpc(WdRpcData  wdRpcData) throws JsonProcessingException{
		//String sendData = objectMapper.writeValueAsString(wdRpcData);
		return 0;
			
	}
	
	

}
