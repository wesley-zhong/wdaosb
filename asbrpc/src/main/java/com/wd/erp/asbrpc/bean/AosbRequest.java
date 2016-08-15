package com.wd.erp.asbrpc.bean;

import lombok.Data;

@Data
public class AosbRequest {
	public String method;
	public String client_customerid;
	public String client_db;
	public String messageid;
	public String apptoken;
	public String appkey;
	public String sign;
	public String timestamp;
	public String format;
	public String data;
}
