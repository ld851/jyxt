package com.ld.webclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ld.webclient.App;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MsgSender {

	@Autowired
	private KafkaTemplate kafka;
	
	private final Logger logger = LoggerFactory.getLogger(App.class);
	
	public boolean sendMessage(String topic, String msg)
	{
		try {
			kafka.send(topic,msg);
			return true;
		}catch(Exception e) {
			logger.error(e.toString());
			return false;
		}
	}
}
