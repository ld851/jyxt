package com.jzsec.gupiao.entity;

public class Order {

	public String getStkcode() {
		return stkcode;
	}
	public void setStkcode(String stkcode) {
		this.stkcode = stkcode;
	}
	public String getBsflag() {
		return bsflag;
	}
	public void setBsflag(String bsflag) {
		this.bsflag = bsflag;
	}
	public double getOrderprice() {
		return orderprice;
	}
	public void setOrderprice(double orderprice) {
		this.orderprice = orderprice;
	}
	public double getOrderqty() {
		return orderqty;
	}
	public void setOrderqty(double orderqty) {
		this.orderqty = orderqty;
	}
	public String getCustid() {
		return custid;
	}
	public void setCustid(String custid) {
		this.custid = custid;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public boolean isCanceled() {
		return canceled;
	}
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	public String getCancelorderid() {
		return cancelorderid;
	}
	public void setCancelorderid(String cancelorderid) {
		this.cancelorderid = cancelorderid;
	}
	public int getOrdertype() {
		return ordertype;
	}
	public void setOrdertype(int ordertype) {
		this.ordertype = ordertype;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getOrderdate() {
		return orderdate;
	}
	public void setOrderdate(String orderdate) {
		this.orderdate = orderdate;
	}
	public String getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getMatchqty() {
		return matchqty;
	}
	public void setMatchqty(double matchqty) {
		this.matchqty = matchqty;
	}
	private String stkcode;
	private String bsflag; //0B,0S,0C
	private double orderprice;
	private double orderqty;
	private String custid;
	private String orderid; //在一天内唯一
	private boolean canceled;
	private String cancelorderid; //待撤销的订单ID
	private int ordertype; //0-限价委托；1-市价委托
	private String addr; //下单地址
	private String orderdate;
	private String ordertime;
	private String status; //订单状态 1-已报；2-已成；3-部成；4-废单；5-撤单
	private double matchqty; //成交数量

	public Order(){}//无参构造函数

}
