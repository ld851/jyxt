package com.jzsec.gupiao.entity;

public class Match {

	private int sno; //成交序号
	private String matchdate; //成交日期
	private String matchtime; //成交时间
	private String orderdate; //申报日期
	private String orderid;
	private double matchprice;
	private double matchqty;
	private String custid;
	public int getSno() {
		return sno;
	}
	public void setSno(int sno) {
		this.sno = sno;
	}
	public String getMatchdate() {
		return matchdate;
	}
	public void setMatchdate(String matchdate) {
		this.matchdate = matchdate;
	}
	public String getMatchtime() {
		return matchtime;
	}
	public void setMatchtime(String matchtime) {
		this.matchtime = matchtime;
	}
	public String getOrderdate() {
		return orderdate;
	}
	public void setOrderdate(String orderdate) {
		this.orderdate = orderdate;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public double getMatchprice() {
		return matchprice;
	}
	public void setMatchprice(double matchprice) {
		this.matchprice = matchprice;
	}
	public double getMatchqty() {
		return matchqty;
	}
	public void setMatchqty(double matchqty) {
		this.matchqty = matchqty;
	}
	public String getCustid() {
		return custid;
	}
	public void setCustid(String custid) {
		this.custid = custid;
	}
	
}
