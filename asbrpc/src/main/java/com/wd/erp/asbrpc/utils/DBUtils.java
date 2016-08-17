package com.wd.erp.asbrpc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.SQLServerException;

public class DBUtils {
	public static <T> T parseObj (ResultSet rs, Class<T> objClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		T obj = objClass.newInstance();
		Field [] fields = objClass.getDeclaredFields();
	
		for(int i = 0 ; i < fields.length; ++i){
			Field field = fields[i];
			String fieldValue = null;
			try{
			 fieldValue = rs.getString(field.getName());
			}catch(SQLServerException e){
				e.printStackTrace();
			}catch(SQLException e){
				e.printStackTrace();
			}
			if(fieldValue == null) 
				continue;
			String setMethodName = "set" + field.getName();
			Method setMethod = objClass.getMethod(setMethodName, String.class);
			setMethod.invoke(obj, fieldValue);
		}
		return obj;
	}
}
