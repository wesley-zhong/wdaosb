package com.wd.erp.asbrpc.bean;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AosbResponse {
	private String returnCode;
	private String returnDesc;
	private String returnFlag;
	private List<ResponseOrderBean> resultInfo = new ArrayList<ResponseOrderBean>();
}
