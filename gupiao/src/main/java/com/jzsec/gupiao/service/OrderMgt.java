package com.jzsec.gupiao.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jzsec.gupiao.dao.IMatchDao;
import com.jzsec.gupiao.dao.IOrderDao;
import com.jzsec.gupiao.entity.HQ;
import com.jzsec.gupiao.entity.Match;
import com.jzsec.gupiao.entity.Order;

@Service
public class OrderMgt {

	@Autowired
	private IOrderDao orderDao;
	
	@Autowired
	private IMatchDao matchDao;
	
	@Autowired
	private OrderQueue orderQ;
	
	@Autowired
	private HQ hq;
	
	private HashSet<Double>st = new HashSet<Double>();
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//集合竞价
	@Transactional
	public boolean jHJJ() {
		
		Date dt = new Date();
		DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
		DateFormat dftime = new SimpleDateFormat("HHmmss");
		
		Match m = new Match();
		m.setMatchdate(dfdate.format(dt));
		m.setMatchtime(dftime.format(dt));
		//1，确定成交价格
		List<Order> list =  orderDao.getOrdersByDate(dfdate.format(dt));
		
		st.clear();
		for(Iterator<Order>t = list.iterator(); t.hasNext();) {
			Order o = t.next();
			if(!o.getBsflag().equals("0C") && !o.isCanceled()){
				st.add(o.getOrderprice());
			}
		}
		
		double max_sum = 0.00; //在所有价格下的最大成交额
		//在最大成交量下的价格列表
		ArrayList<Double>pricelist = new ArrayList<Double>();
		for(Iterator<Double> it = st.iterator(); it.hasNext();){
			double test_price = it.next();
			double buy_sum = 0.00; 
			double sell_sum = 0.00;
			for(int i = 0; i < list.size(); i++){
				//买入价格 大于等于 该价格的都可以成交
				if(list.get(i).getOrdertype()==0 &&  !list.get(i).isCanceled() && list.get(i).getBsflag().equals("0B") && list.get(i).getOrderprice() >= test_price)
					buy_sum += list.get(i).getOrderqty();
				//卖出价格 小于等于 该价格的都可以成交
				if(list.get(i).getOrdertype()==0 && !list.get(i).isCanceled() && list.get(i).getBsflag().equals("0S") && list.get(i).getOrderprice() <= test_price)
					sell_sum += list.get(i).getOrderqty();
			}
			//该价格的成交额
			double curr_sum = buy_sum >= sell_sum ? sell_sum : buy_sum;
			
			if (curr_sum > max_sum){
				//新的最大成交额
				max_sum = curr_sum;
				pricelist.clear();
				pricelist.add(test_price);
			}else if(curr_sum == max_sum) {
				pricelist.add(test_price);
			}else {
				
			}
		}
		
		//以最大交易额平均价格为开盘价（上交所）
		double avgprice = 0;
		for(double mprice : pricelist) {
			avgprice = avgprice + mprice;
		}
		avgprice = avgprice / pricelist.size();
		hq.setOpenprice(avgprice);
		hq.setCurrprice(avgprice);
		
		m.setMatchprice(avgprice); //确定的集合竞价价格
		//2，逐笔成交
		//所有买委托按照从高到低，价格相同则按照时间先后排列，依次成交
		double remains = max_sum; //剩余成交额 
		List<Order> buyorders = orderDao.getBuyOrders(dfdate.format(dt));
		for(Iterator<Order>it = buyorders.iterator(); it.hasNext() && remains > 0;) {

			Order o= it.next();
			if (remains >= o.getOrderqty()) {
				remains = remains - o.getOrderqty();
				o.setMatchqty(o.getOrderqty());
				o.setStatus("2");
				m.setMatchqty(o.getOrderqty());
			}else {
				o.setMatchqty(remains);
				remains = 0;
				o.setStatus("3");
				m.setMatchqty(remains);
			}
			orderDao.updateOrder(o);
			BeanUtils.copyProperties(o, m);
			matchDao.addMatch(m);
			
		}
		
		//所有卖委托按照价格从低到高，价格相同则按照时间先后，依次成交
		remains = max_sum; //剩余成交额 
		List<Order> sellorders = orderDao.getSellOrders(dfdate.format(dt));
		for(Iterator<Order>it = sellorders.iterator(); it.hasNext() && remains > 0;) {

			Order o= it.next();
			if (remains >= o.getOrderqty()) {
				remains = remains - o.getOrderqty();
				o.setMatchqty(o.getOrderqty());
				o.setStatus("2");
				m.setMatchqty(o.getOrderqty());
			}else {
				o.setMatchqty(remains);
				remains = 0;
				o.setStatus("3");
				m.setMatchqty(remains);
			}
			orderDao.updateOrder(o);
			BeanUtils.copyProperties(o, m);
			matchDao.addMatch(m);
		}
		
		return true;
	}
	@Transactional
	public boolean lXJJ(Order o)
	{
		dealorder(o);
		return true;
	}

	private void dealorder(Order o) {
		double remains = 0.0;
		if (o.getBsflag().equals("0B")) {
			if (orderQ.isEmptySellOrder()) {
				addOrderWithHq(o);
			}else {
				Order sellorder = orderQ.getFirstSellOrder();
				while(sellorder != null) {
					remains = dealPairOrder(o,sellorder);
					if(remains <= 0)
						break;
					sellorder = orderQ.getFirstSellOrder();
				}
				//退出时，仍有未成交部分
				if(remains > 0) {
					addOrderWithHq(o);
				}
			}
		}
		if(o.getBsflag().equals("0S")) {
			if(orderQ.isEmptyBuyOrder()) {
				addOrderWithHq(o);
			}else {
				Order buyorder = orderQ.getFirstBuyOrder();
				while(buyorder != null) {
					remains = dealPairOrder(o,buyorder);
					if(remains <= 0)
						break;
					buyorder = orderQ.getFirstBuyOrder();
				}
				if(remains > 0) {
					addOrderWithHq(o);
				}
				
			}
		}

	}


	//成交处理委托单，成交数量为dealqty
	private void dealOneOrder(Order o, double dealqty) {
		Date dt = new Date();
		DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
		DateFormat dftime = new SimpleDateFormat("HHmmss");
		Match m = new Match();
		m.setMatchdate(dfdate.format(dt));
		m.setMatchtime(dftime.format(dt));
		
		if(o.getOrderqty() < dealqty) {
			logger.error("error: the final deal quantity is greater than the orderqty");
			return;
		}
		//1,更新数据库委托表
		//设置成交数量
		o.setMatchqty(o.getMatchqty() + dealqty);
		//设置状态
		if(o.getOrderqty() > o.getMatchqty()) {
			o.setStatus("3");
		}else {
			o.setStatus("2");
		}
		orderDao.updateOrder(o);
		//2,登记成交表
		BeanUtils.copyProperties(o, m);
		//仅登记本次成交量
		m.setMatchqty(dealqty);
		m.setMatchprice(hq.getCurrprice());
		matchDao.addMatch(m);
		
	}
	/**
	//处理一对订单，orderA为新来的，orderB为原来的
	返回A中还未成交量，为负值表示B中还未成交部分数量（a,A已经完全成交；b,A已经无法在成交）
	**/
	private double dealPairOrder(Order orderA, Order orderB) {
		double remainsA = orderA.getOrderqty() - orderA.getMatchqty();
		double remainsB = orderB.getOrderqty() - orderB.getMatchqty();
		//可成交数量
		double candeal = remainsA >= remainsB ? remainsB : remainsA;
		
		//如果orderA是市价委托，orderB是市价委托，则以当前价成交
		if(orderA.getOrdertype() == 1 && orderB.getOrdertype() == 1) {}
		//如果orderA是市价委托，orderB是限价委托，则以orderB价格为最新成交价格
		if(orderA.getOrdertype() == 1 && orderB.getOrdertype() == 0) {
			hq.setCurrprice(orderB.getOrderprice());
		}
		//如果orderA是限价委托，orderB是市价委托，则以orderA为最新价格
		if(orderA.getOrdertype() == 0 && orderB.getOrdertype() == 1) {
			hq.setCurrprice(orderA.getOrderprice());
		}
		//如果orderA是限价委托，orderB是限价委托，则以先来的orderB为最新成交价
		if(orderA.getOrdertype() == 0 && orderB.getOrdertype() == 0) {
			//卖单价格高于买单价格，则返回退出
			if(orderA.getBsflag().equals("0B") && orderB.getBsflag().equals("0S")) {
				//成交不了，插入委托队列
				if (orderA.getOrderprice() < orderB.getOrderprice()) {
					addOrderWithHq(orderA);
					return (-orderB.getOrderqty());
				}
			}
			if(orderA.getBsflag().equals("0S") && orderB.getBsflag().equals("0B")) {
				//成交不了，插入委托队列
				if (orderA.getOrderprice() > orderB.getOrderprice()) {
					addOrderWithHq(orderA);
					return (-orderB.getOrderqty());
				}
			}
			hq.setCurrprice(orderB.getOrderprice());
		}
		
		//修改委托行情信息中的委托量
		hq.updateHQ(orderB, candeal);
		dealOneOrder(orderA, candeal);	
		dealOneOrder(orderB, candeal);
		
		//如果orderB全成，从委托队列中删除orderB
		if(remainsA >= remainsB) {
			if(orderB.getBsflag().equals("0B")) {
				orderQ.removeFirstBuyOrder();
			}else {
				orderQ.removeFirstSellOrder();			
			}	
		}
		return (remainsA - remainsB);
	}
	
	public void loadUncompletedOrder(String orderdate) {
		Date dt = new Date();
		DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
		if("".equals(orderdate)) {
			orderdate = dfdate.format(dt);
		}
		orderQ.clearOrderQueue();
		hq.clearHQ();
		//将未完成的订单加入到撮合队列
		List<Order>uncompleteorders = orderDao.getUnCompleteOrders(orderdate);
		for(Iterator<Order>it = uncompleteorders.iterator(); it.hasNext();) {
				Order o = it.next();
				addOrderWithHq(o);
		}
	}
	
	//同时增加委托队列和行情队列
	private void addOrderWithHq(Order o) {
		orderQ.addOrder(o);
		hq.addHQ(o);
	}
}
