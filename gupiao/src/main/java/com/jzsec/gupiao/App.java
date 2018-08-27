package com.jzsec.gupiao;


import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.Banner;

import com.jzsec.gupiao.entity.HQ;
import com.jzsec.gupiao.entity.Order;
import com.jzsec.gupiao.service.HQProducer;
import com.jzsec.gupiao.dao.*;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.jzsec.gupiao.dao")
public class App implements CommandLineRunner
{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private KafkaTemplate kafka;
	@Autowired
	private HQ hq;
	
	private final CountDownLatch latch = new CountDownLatch(2);
	
	private Gson gson = new GsonBuilder().create();

	@Resource
	IOrderDao orderDao;
    public static void main( String[] args )throws Exception
    {
        SpringApplication app = new SpringApplication(App.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
    
    @Override
	public void run(String... arg0) throws Exception {
    	new Thread(new HQProducer(kafka,hq)).start();
	}
    

    
    //@KafkaListener(topics = "order")
    public void processMsg(String content)
    {
    	System.out.println(content);
    	latch.countDown();
    	Order obj = gson.fromJson(content, Order.class);
    	orderDao.addOrder(obj);
    }
}

