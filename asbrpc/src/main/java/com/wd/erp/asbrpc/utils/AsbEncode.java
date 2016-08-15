package com.wd.erp.asbrpc.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Base64;

public class AsbEncode {

	public static String md5(String msg) throws Exception {
	    MessageDigest md = MessageDigest.getInstance("MD5"); 
	    md.update(msg.getBytes());    
	    byte[] mdbytes = md.digest();
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < mdbytes.length; i++) {
	        sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    return sb.toString();
	}
	
	public static String base64(String msg) throws UnsupportedEncodingException{
		return  Base64.getEncoder().encodeToString(msg.getBytes("utf-8")).toUpperCase();	
	}
	
	public static String urlEncode(String msg) throws UnsupportedEncodingException{
		return  Base64.getUrlEncoder().encodeToString(msg.getBytes("utf-8"));
	}

}
