package com.wd.erp.asbrpc.utils;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;

public class CapitalizedPropertyNamingStrategy extends PropertyNamingStrategyBase {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = -7428685998086978387L;
	private static  Set<String> notransLateSet = new HashSet<String>();
	
	static {
		notransLateSet.add("xmldata");
		notransLateSet.add("header");
		notransLateSet.add("detailsItem"); 
	}

	@Override
    public String translate(String propertyName) {
		if(notransLateSet.contains(propertyName))
			return propertyName;
        String name = propertyName.replaceAll("^\\w", propertyName.toUpperCase().substring(0,1));
        return name;
    }
 
}