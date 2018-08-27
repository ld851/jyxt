package com.jzsec.gupiao.entity;

import org.springframework.stereotype.Component;

@Component
public class HQShow {

	private final int level = 5;
	//从低到高
	private double[]sell;
	
	//从高到低
	private double[]buy;
	
	//对应价格的委托量
	private double[] sellvolume;
	
	private double[] buyvolume;

	
	private double currprice;
	private double openprice;

	public HQShow() {
		sell = new double[level];
		buy = new double[level];
		sellvolume = new double[level];
		buyvolume = new double[level];
		currprice = 0.00;
		openprice = 0.00;
	}

	public double[] getSell() {
		return sell;
	}

	public void setSell(double[] sell) {
		this.sell = sell;
	}

	public double[] getBuy() {
		return buy;
	}

	public void setBuy(double[] buy) {
		this.buy = buy;
	}

	public double[] getSellvolume() {
		return sellvolume;
	}

	public void setSellvolume(double[] sellvolume) {
		this.sellvolume = sellvolume;
	}

	public double[] getBuyvolume() {
		return buyvolume;
	}

	public void setBuyvolume(double[] buyvolume) {
		this.buyvolume = buyvolume;
	}

	public double getCurrprice() {
		return currprice;
	}

	public void setCurrprice(double currprice) {
		this.currprice = currprice;
	}

	public double getOpenprice() {
		return openprice;
	}

	public void setOpenprice(double openprice) {
		this.openprice = openprice;
	}

	public int getLevel() {
		return level;
	}
	
}
