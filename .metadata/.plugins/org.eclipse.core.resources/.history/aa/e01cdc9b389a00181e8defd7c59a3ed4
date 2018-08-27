package com.jzsec.webclient.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.jzsec.gupiao.entity.Order;
import com.jzsec.webclient.entity.SerialNo;

@Repository
public interface ISerialNoDao {

	SerialNo getSerialNoWithDate(@Param("serialtype")String serialtype, @Param("orderdate")String orderdate);
	SerialNo getSerialNo(String serialtype);
	boolean UpdateSerialNo(SerialNo or);	
}
