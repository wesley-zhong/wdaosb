package com.wd.erp.asbrpc.bean;

import java.util.List;

import lombok.Data;

@Data
public class Header {
	private String OrderNo;

	private String OrderType;

	private String OrderTime;

	private String ExpectedShipmentTime1;

	private String RequiredDeliveryTime;

	private String CustomerID;

	private String SOReference2;

	private String SOReference3;

	private String SOReference4;

	private String SOReference5;

	private String ConsigneeID;

	private String ConsigneeName;

	private String C_Address1;

	private String C_Address2;

	private String C_Address3;

	private String UserDefine1;

	private String UserDefine2;

	private String UserDefine3;

	private String UserDefine4;

	private String UserDefine5;

	private String Notes;

	private String H_EDI_02;

	private String H_EDI_03;

	private String H_EDI_04;

	private String H_EDI_05;

	private String H_EDI_06;

	private String H_EDI_07;

	private String H_EDI_08;

	private String H_EDI_09;

	private String H_EDI_10;

	private String UserDefine6;

	private String WarehouseID;

	private String FollowUp;
	private List<DetailsItem> detailsItem;
}