package com.jzsec.gupiao.entity;

public class Customer {

	private int custid;
	private String custname;
	private String idtype;
	private String idno;
	private double fundavl;
	
	public int getCustid() {
		return custid;
	}
	public void setCustid(int custid) {
		this.custid = custid;
	}
	public String getCustname() {
		return custname;
	}
	public void setCustname(String custname) {
		this.custname = custname;
	}
	public String getIdtype() {
		return idtype;
	}
	public void setIdtype(String idtype) {
		this.idtype = idtype;
	}
	public String getIdno() {
		return idno;
	}
	public void setIdno(String idno) {
		this.idno = idno;
	}
	public double getFundavl() {
		return fundavl;
	}
	public void setFundavl(double fundavl) {
		this.fundavl = fundavl;
	}

}
