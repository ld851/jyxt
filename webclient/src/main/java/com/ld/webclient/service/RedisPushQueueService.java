package com.ld.webclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ld.webclient.App;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
@Service
public class RedisPushQueueService {
	private final Logger logger = LoggerFactory.getLogger(RedisPushQueueService.class);
	@Autowired
	private JedisPool jedisPool;
	public boolean push(String key,double score1,double score2,String name){
		Jedis jedis = null;
		
		
		try{
			jedis=jedisPool.getResource();
			if(jedis.dzsetInsert(key, score1, score2, name)>0){
				return true;
			}
			else {
				logger.error("faile to insert redis dzset***");
				return false;
				}
		}catch (Exception e){
			logger.error(e.toString());
			return false;
		}
		finally{
			if(jedis!=null)
				jedis.close();
		}
	}

}
