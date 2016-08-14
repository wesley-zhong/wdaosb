package com.wd.erp.asbrpc.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wd.erp.asbrpc.bean.AosbRequest;

public class AresHttpClient {
	
	public static void callRpcMethod(String url, Object obj, String rpcService, String method) throws ClientProtocolException, IOException{
	
		ObjectMapper objectMapper = new ObjectMapper();
		AosbRequest request = new AosbRequest();
		//request.setRpcService(rpcService);
		//request.setRpcMethod(method);
	//	request.setData(objectMapper.writeValueAsBytes(obj));
		
		
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create(); 
	    //HttpClient  
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build(); 
        HttpPost httpPost = new HttpPost(url); 
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new ByteArrayEntity(objectMapper.writeValueAsBytes(request)));
        CloseableHttpResponse response =  closeableHttpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();  
        if (entity != null) {  
            System.out.println("--------------------------------------");  
            System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));  
            System.out.println("--------------------------------------");  
        }
        response.close();
	}
	

}
