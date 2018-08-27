package com.ld.webclient.service;





import org.springframework.kafka.core.KafkaTemplate;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;



public class OrderProducer implements Runnable{
	
	private JedisPool jedisPool;
	private KafkaTemplate<String,String> kafka;

	
	public void run() {
		Jedis jedis=jedisPool.getResource();
		double l;
		try{
		while(true) {
			if((l=jedis.dzsetLen("order"))>0)
			for(int i=(int)l;i>0;i--){
			kafka.send("order",jedis.dzsetPopMax("order").get(0).getElement());
			}
			try {
				Thread.sleep(1000);
			}catch(InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
				jedis.close();
			
		}
	}
	public OrderProducer(JedisPool jedisPool,KafkaTemplate kafka){
		this.jedisPool = jedisPool;
		this.kafka = kafka;
	}
	
}
