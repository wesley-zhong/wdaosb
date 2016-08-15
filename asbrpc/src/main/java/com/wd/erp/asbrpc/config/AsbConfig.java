package com.wd.erp.asbrpc.config;

import lombok.Data;

import org.springframework.stereotype.Component;

@Component
@Data
public class AsbConfig {
	private String appKey;
	private String appSecret;
	private String apptoken;
	private String url;
}
