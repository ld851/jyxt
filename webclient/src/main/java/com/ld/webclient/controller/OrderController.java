package com.ld.webclient.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jzsec.gupiao.entity.Order;
import com.ld.webclient.service.MsgSender;
import com.ld.webclient.service.SerialNoGenerater;
import com.ld.webclient.util.JsonResponse;

@Controller
public class OrderController {

	@Autowired
	private MsgSender msender;
	@Autowired
	private SerialNoGenerater sng;
	
	private Gson gson = new GsonBuilder().create();
	
	@ResponseBody
	@RequestMapping(value = "/order", method= RequestMethod.POST)
	public String order(Order order)
	{
		
		Date dt = new Date();
		DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
		DateFormat dftime = new SimpleDateFormat("HHmmss");

		order.setCustid("1000001");
		order.setOrderid(sng.getNextOrderid(dfdate.format(dt)));
		order.setCanceled(false);
		
		order.setOrderdate(dfdate.format(dt));
		order.setOrdertime(dftime.format(dt));
		order.setStatus("1");
		order.setMatchqty(0.00);
		
		JsonResponse jr = new JsonResponse();
		
		if(msender.sendMessage("order", gson.toJson(order))) {
			jr.setSuccess("true");
		}else {
			jr.setSuccess("false");
		}
		return gson.toJson(jr);
	}
	
	@RequestMapping(value = {"/order","/"})
	public String beforeorder()
	{
		return "index";
	}
	
	@RequestMapping(value = {"/login"})
	public String login()
	{
		return "login";
	}

}
