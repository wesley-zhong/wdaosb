package com.wd.erp.asbrpc.bean;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ResponseOrderBean {
	@JsonProperty("DOCNO")
	private String DOCNO;

	@JsonIgnore
	public String getDOCNO() {
		return DOCNO;
	}

	@JsonIgnore
	public void setDOCNO(String dOCNO) {
		DOCNO = dOCNO;
	}

	@JsonIgnore
	public String getOrderNo() {
		return OrderNo;
	}

	@JsonIgnore
	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	@JsonIgnore
	public String getOrderType() {
		return OrderType;
	}

	@JsonIgnore
	public void setOrderType(String orderType) {
		OrderType = orderType;
	}

	@JsonIgnore
	public String getCustomerID() {
		return CustomerID;
	}

	@JsonIgnore
	public void setCustomerID(String customerID) {
		CustomerID = customerID;
	}

	@JsonIgnore
	public String getWarehouseID() {
		return WarehouseID;
	}

	@JsonIgnore
	public void setWarehouseID(String warehouseID) {
		WarehouseID = warehouseID;
	}

	@JsonIgnore
	public String getErrorcode() {
		return errorcode;
	}

	@JsonIgnore
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	@JsonProperty("OrderNo")
	private String OrderNo;

	@JsonProperty("OrderType")
	private String OrderType;

	@JsonProperty("CustomerID")
	private String CustomerID;

	@JsonProperty("WarehouseID")
	private String WarehouseID;

	@JsonProperty("errorcode")
	private String errorcode;
	
	private String errordescr;

	public String getErrordescr() {
		return errordescr;
	}

	public void setErrordescr(String errordescr) {
		this.errordescr = errordescr;
	}
}
