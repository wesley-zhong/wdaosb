package com.wd.erp.asbrpc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	
	 public static String getNowDate() {
		  Date currentTime = new Date();
		  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  return formatter.format(currentTime);		
		 }
}
