package com.wd.erp.asbrpc.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.wd.erp.asbrpc.bean.AosbRequest;

public class AresHttpClient {

	
	public static String  sendHttpPost(String url, AosbRequest request) {
		System.out.println("url =  " + url);
		RequestConfig defaultRequestConfig = RequestConfig.custom()
			    .setSocketTimeout(300000)
			    .setConnectTimeout(200000)
			    .setConnectionRequestTimeout(300000)
			    .setStaleConnectionCheckEnabled(true)
			    .build();
		
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(defaultRequestConfig); 
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build(); 
        HttpPost httpPost = new HttpPost(url); 
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        try{
        	UrlEncodedFormEntity formEntity  = createFormParamsFromObj(request);
        	httpPost.setEntity(formEntity);
            CloseableHttpResponse response =  closeableHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();  
            if (entity != null) {  
                String responseContent = EntityUtils.toString(entity, "UTF-8"); 
                return AsbEncode.urlDecode(responseContent);
            }    	
        }catch(Exception e){
        	e.printStackTrace();
        }
        return null;
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
