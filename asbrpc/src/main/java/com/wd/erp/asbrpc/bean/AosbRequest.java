package com.wd.erp.asbrpc.bean;

import lombok.Data;

@Data
public class AosbRequest {
private String	method;
private String	client_customerid;
private String	client_db;
private String	messageid;
private String	apptoken;
private String	appkey;
private String	sign;
private String	timestamp;
private String	format;
private String	data;
}
