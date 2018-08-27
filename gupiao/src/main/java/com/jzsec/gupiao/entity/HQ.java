package com.jzsec.gupiao.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class HQ {
	
	@Autowired
	private HQShow hqs;
	
	private Gson gson = new GsonBuilder().create();
	
	private double openprice;
	private double currprice;
	
	private double notlimitedbuyqty; //市价买单
	private double notlimitedsellqty; //市价卖单
	//private Map<Double,Double>buyst = new HashMap<Double,Double>();
	private TreeMap<Double,Double>buyst = new TreeMap<Double,Double>();
	private TreeMap<Double,Double>sellst = new TreeMap<Double,Double>();

	public Map<Double, Double> getBuyst() {
		return buyst;
	}
	public Map<Double, Double> getSellst() {
		return sellst;
	}

	public double getNotlimitedbuyqty() {
		return notlimitedbuyqty;
	}
	public void setNotlimitedbuyqty(double notlimitedbuyqty) {
		this.notlimitedbuyqty = notlimitedbuyqty;
	}
	public double getNotlimitedsellqty() {
		return notlimitedsellqty;
	}
	public void setNotlimitedsellqty(double notlimitedsellqty) {
		this.notlimitedsellqty = notlimitedsellqty;
	}
	public double getOpenprice() {
		return openprice;
	}
	public void setOpenprice(double openprice) {
		this.openprice = openprice;
	}
	public double getCurrprice() {
		return currprice;
	}
	public void setCurrprice(double currprice) {
		this.currprice = currprice;
	}
	
	//新添加一条委托单行情记录
	public void addHQ(Order o) {
		//限价
		double qty = 0.00; //该价格委托量
		if (o.getOrdertype() == 0) {
			if(o.getBsflag().equals("0B")) {
				if(buyst.get(o.getOrderprice()) != null ) {
					qty = buyst.get(o.getOrderprice());
				}
				buyst.put(o.getOrderprice(), qty + o.getOrderqty() - o.getMatchqty());
			}else {
				if(sellst.get(o.getOrderprice()) != null ) {
					qty = sellst.get(o.getOrderprice());
				}
				sellst.put(o.getOrderprice(), qty + o.getOrderqty() - o.getMatchqty());				
			}
		}else {
			//市价
			if(o.getBsflag().equals("0B")) {
				notlimitedbuyqty = notlimitedbuyqty + o.getOrderqty() - o.getMatchqty();
			}else {
				notlimitedsellqty = notlimitedsellqty + o.getOrderqty() - o.getMatchqty();
			}			
		}
	}

	//委托单成交dealqty
	public void updateHQ(Order o, double dealqty) {
		if(o.getOrdertype() == 1 && o.getBsflag().equals("0B")) {
			notlimitedbuyqty = notlimitedbuyqty - dealqty;
		}else if(o.getOrdertype() == 1 && o.getBsflag().equals("0S")) {
			notlimitedsellqty = notlimitedsellqty - dealqty;
		}
		if(o.getOrdertype() == 0 && o.getBsflag().equals("0B")) {
			double tmp = buyst.get(o.getOrderprice()) - dealqty;
			if(tmp > 0)
				buyst.put(o.getOrderprice(), tmp);
			else
				buyst.remove(o.getOrderprice());
		}
		if(o.getOrdertype() == 0 && o.getBsflag().equals("0S")) {
			double tmp = sellst.get(o.getOrderprice()) - dealqty;
			if(tmp > 0)
				sellst.put(o.getOrderprice(), tmp);
			else
				sellst.remove(o.getOrderprice());
		}	
	}
	
	public void clearHQ() {
		buyst.clear();
		sellst.clear();
	}
	
	//转化行情信息为jason串
	public String convert() {
		hqs.setCurrprice(currprice);
		hqs.setOpenprice(openprice);
		
		//获取最高的5个买价：从高到低
		NavigableMap<Double,Double> ns = buyst.descendingMap();
		int i = 0;
		for(Map.Entry<Double,Double>entry: ns.entrySet()) {
			hqs.getBuy()[i] = entry.getKey();
			hqs.getBuyvolume()[i++] = entry.getValue();
			if(i > 4)break;
		}
		//获取最低的5个卖价:从低到高
		i = 0;
		for(Map.Entry<Double,Double>entry: sellst.entrySet()) {
			hqs.getSell()[i] = entry.getKey();
			hqs.getSellvolume()[i++] = entry.getValue();
			if(i > 4)break;
		}
		
		return gson.toJson(hqs);
		
	}
}

