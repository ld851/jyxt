package com.jzsec.gupiao.entity;

public class Market {

	private String marketcode; //0-股票
	private String status; //0-闭市；1-开市
	private String period; //1-集合竞价（接受申报）；2-集合竞价撮合时段（不接受交易申报）；3-连续竞价时段
	public String getMarketcode() {
		return marketcode;
	}
	public void setMarketcode(String marketcode) {
		this.marketcode = marketcode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
}
