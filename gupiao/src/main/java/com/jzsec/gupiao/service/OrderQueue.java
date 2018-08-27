package com.jzsec.gupiao.service;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jzsec.gupiao.entity.HQ;
import com.jzsec.gupiao.entity.Order;

@Component
public class OrderQueue {

	@Autowired
	private HQ hq;
	
	private LinkedList<Order>buylist;
	private LinkedList<Order>selllist;
	
	public OrderQueue() {
		buylist = new LinkedList<Order>();
		selllist = new LinkedList<Order>(); 
	}
	
	public Order getFirstBuyOrder() {
		try {
			return buylist.getFirst();
		}catch(NoSuchElementException  e) {
			return null;
		}
	}
	public void removeFirstBuyOrder() {
		buylist.removeFirst();
	}
	public Order getFirstSellOrder() {
		try {
			return selllist.getFirst();
		}catch(NoSuchElementException  e) {
			return null;
		}
	}
	public void removeFirstSellOrder() {
		selllist.removeFirst();
	}
	public boolean isEmptyBuyOrder() {
		return buylist.isEmpty();
	}
	public boolean isEmptySellOrder() {
		return selllist.isEmpty();
	}
	//市价在前，价格高低，时间先后
	private void addBuyOrder(Order o) {
		if(o.getOrdertype() == 0){
			Iterator<Order> it;
			boolean inserted = false;
			for(it = buylist.iterator(); it.hasNext();)
			{
				Order tmp = it.next();
				if(tmp.getOrderprice() < o.getOrderprice()) {
					buylist.add(buylist.indexOf(tmp), o);
					inserted = true;
					//插入完成则退出
					break;
				}
			}
			if(!inserted) {
				//插入最后
				buylist.addLast(o);
			}

		} else if(o.getOrdertype() == 1) {
			Iterator<Order> it;
			boolean inserted = false;
			for(it = buylist.iterator(); it.hasNext();)
			{
				Order tmp = it.next();
				if(tmp.getOrdertype() == 0) {
					buylist.add(buylist.indexOf(tmp), o);
					inserted = true;
					//插入完成则退出
					break;
				}
			}
			if(!inserted) {
				//插入最后
				buylist.addLast(o);
			}
		}
	}
	//市价在前，价格低高，时间先后
	private void addSellOrder(Order o) {

		if(o.getOrdertype() == 0){
			Iterator<Order> it;
			boolean inserted = false;
			for(it = selllist.iterator(); it.hasNext();)
			{
				Order tmp = it.next();
				if(tmp.getOrderprice() > o.getOrderprice()) {
					selllist.add(selllist.indexOf(tmp), o);
					inserted = true;
					//插入完成则退出
					break;
				}
			}
			if(!inserted) {
				//插入最后
				selllist.addLast(o);
			}
		}else if (o.getOrdertype() == 1){
			Iterator<Order> it;
			boolean inserted = false;
			for(it = selllist.iterator(); it.hasNext();)
			{
				Order tmp = it.next();
				if(tmp.getOrdertype() == 0) {
					selllist.add(selllist.indexOf(tmp), o);
					inserted = true;
					//插入完成则退出
					break;
				}
			}
			if(!inserted) {
				//插入最后
				selllist.addLast(o);
			}				
		}
	}
	
	public void addOrder(Order o) {
		if (o.getBsflag().equals("0B")) {
			addBuyOrder(o);
		}else {
			addSellOrder(o);
		}
	}
	
	public void clearOrderQueue() {
		buylist.clear();
		selllist.clear();
	}
}
