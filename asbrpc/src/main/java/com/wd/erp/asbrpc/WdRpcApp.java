package com.wd.erp.asbrpc;


import org.apache.xbean.spring.context.FileSystemXmlApplicationContext;
import org.springframework.context.ApplicationContext;

import com.wd.erp.asbrpc.service.WdRpcService;
import com.wd.erp.asbrpc.utils.AsbEncode;
import com.wd.erp.asbrpc.utils.TimeUtil;

/**
 *  wd rpc main app
 *
 */
public class WdRpcApp 
{

    private static ApplicationContext ac;

	public static void main( String[] args ) throws Exception
    {
		
		String strNow = TimeUtil.getNowDate();
		System.out.println(strNow + " base encdoe = " + AsbEncode.urlEncode(strNow));
		
		
//    	ac = new FileSystemXmlApplicationContext(new String[]{"src/main/applicationContext.xml","src/main/sqlserver-dal-context.xml"});
//    	WdRpcService wdRpcService = (WdRpcService)ac.getBean(WdRpcService.class);
//    	wdRpcService.sendRpcData();
    }
}
