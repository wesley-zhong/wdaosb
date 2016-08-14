package com.wd.erp.asbrpc;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.xbean.spring.context.FileSystemXmlApplicationContext;
import org.springframework.context.ApplicationContext;

/**
 * Hello world!
 *
 */
public class WdRpcApp 
{
    public static void main( String[] args )
    {
    	ApplicationContext ac = new FileSystemXmlApplicationContext("src/main/sqlserver-dal-context.xml");
    	DataSource dataSource = (DataSource)ac.getBean("dataSource");
        System.out.println( "Hello World! " + dataSource.getName() );
    }
}
