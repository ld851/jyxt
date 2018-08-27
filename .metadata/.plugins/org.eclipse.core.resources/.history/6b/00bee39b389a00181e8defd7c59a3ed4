package com.jzsec.webclient.service;

import java.text.DateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jzsec.webclient.dao.ISerialNoDao;
import com.jzsec.webclient.entity.SerialNo;

@Service
public class SerialNoGenerater {

	@Autowired
	private ISerialNoDao sd;
	public int getNextCustid() {
		return 1;
	}
	
	//todo:为了提高并发效率，可将序列号放在内存数据库，如redis中
	public synchronized String getNextOrderid(String orderdate) {
		
		SerialNo sno = sd.getSerialNoWithDate("orderid", orderdate);
		int curr = sno.getCurrno();
		sno.setCurrno(curr + 1);
		sd.UpdateSerialNo(sno);
		return "GP" + String.format("%06d", curr);
	}
}
