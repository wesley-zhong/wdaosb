package com.wd.erp.asbrpc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AosbResponse {
	
	@JsonProperty("return")
	private Result  result;
	
	@Data
	public static class Result{
      private String   returnCode;
      private String  returnDesc;
      private String  returnFlag;
      private String  resultInfo;
	}

}
