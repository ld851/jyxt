package com.jzsec.gupiao.dao;

import org.springframework.stereotype.Repository;

import com.jzsec.gupiao.entity.Customer;
import com.jzsec.gupiao.entity.Order;

@Repository
public interface ICustomerDao {

	boolean addCustomer(Customer c);
	boolean updateCustomer(Customer c);
	Customer queryCustomerById(String custid);
}
