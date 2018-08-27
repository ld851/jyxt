package com.jzsec.gupiao.dao;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.jzsec.gupiao.entity.*;

@Repository
public interface IOrderDao {
	//通过主键查询
	Order queryByKey(@Param("orderdate") String orderdate, @Param("orderid") String orderid);
	//通过客户号查询
	List<Order> getOrdersByCustid(String custid);
	
	//通过订单日期查询
	List<Order>getOrdersByDate(String orderdate);
	
	//新增
	boolean addOrder(Order or);
	
	//修改，根据日期 + 订单编号
	boolean updateOrder(Order or);
	
	//查询限价买委托
	List<Order>getBuyOrders(String orderdate);
	//查询限价卖委托
	List<Order>getSellOrders(String orderdate);
	
	//查询竞价后未完成订单
	List<Order>getUnCompleteOrders(String orderdate);
}
