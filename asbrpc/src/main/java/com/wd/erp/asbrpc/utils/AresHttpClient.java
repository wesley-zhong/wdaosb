package com.wd.erp.asbrpc.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wd.erp.asbrpc.bean.AosbRequest;

public class AresHttpClient {

	
	public static void sendHttpPost(String url, AosbRequest request) {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create(); 
	    //HttpClient  
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build(); 
        HttpPost httpPost = new HttpPost(url); 
        httpPost.setHeader("Content-Type", "application/json");
        try{
        	UrlEncodedFormEntity formEntity  = createFormParamsFromObj(request);
        	httpPost.setEntity(formEntity);
        	//System.out.println("================  body = " + httpPost.get)
            CloseableHttpResponse response =  closeableHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();  
            if (entity != null) {  
                System.out.println("--------------------------------------");  
                System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));  
                System.out.println("--------------------------------------");  
            }
        	
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	public static UrlEncodedFormEntity  createFormParamsFromObj(AosbRequest request) throws IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException{
		   List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
		   Field[] fileds = AosbRequest.class.getDeclaredFields();
		   for(int i = 0 ; i < fileds.length; ++i){
			   Field field = fileds[i];
			   String fieldName = field.getName();
			   String value = (String)field.get(request);
			   formparams.add(new BasicNameValuePair(fieldName, value));
		   }
		  return new UrlEncodedFormEntity(formparams, "UTF-8");    
	}
}
