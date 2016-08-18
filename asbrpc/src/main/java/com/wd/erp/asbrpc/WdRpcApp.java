package com.wd.erp.asbrpc;


import org.apache.xbean.spring.context.FileSystemXmlApplicationContext;
import org.springframework.context.ApplicationContext;

import com.wd.erp.asbrpc.service.WdRpcService;

/**
 *  wd rpc main app
 *
 */
public class WdRpcApp 
{
    private static ApplicationContext ac;
	public static void main( String[] args ) throws Exception
    {	
    	ac = new FileSystemXmlApplicationContext(new String[]{"classpath*:applicationContext.xml","classpath*:sqlserver-dal-context.xml"});
    	WdRpcService wdRpcService = (WdRpcService)ac.getBean(WdRpcService.class);
    	wdRpcService.sendRpcData();
    }
}
