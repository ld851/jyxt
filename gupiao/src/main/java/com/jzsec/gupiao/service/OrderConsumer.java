package com.jzsec.gupiao.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jzsec.gupiao.dao.IMarketDao;
import com.jzsec.gupiao.dao.IOrderDao;
import com.jzsec.gupiao.entity.Market;
import com.jzsec.gupiao.entity.Order;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

@Configuration
@EnableKafka
public class OrderConsumer {
	
	@Value("${kafka.server}")
	private String server;
	
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<String, Object>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        
        //是否自动周期性提交已经拉取到消费端的消息offset
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "6000");
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, "orderhandler"); //组别
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return propsMap;
    }

    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<String, String>(consumerConfigs());
    }
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1); //设置消费者线程
        factory.getContainerProperties().setPollTimeout(1500);//设置查询时间
        return factory;
    }
    @Bean
    public OrderListener listener() {
        return new OrderListener();
    }
}


class OrderListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Gson gson = new GsonBuilder().create();
	@Resource
	private IOrderDao orderDao;
	
	@Resource 
	private IMarketDao marketDao;
	
	@Autowired
	private OrderMgt mgr;

	@KafkaListener(topics = {"order"})
	public void processMsg(String content) {
		Order obj = gson.fromJson(content, Order.class);
		Market m = marketDao.queryByMarketID("0");
		//开市且连续竞价
		if(m.getStatus().equals("1") && m.getPeriod().equals("3"))
		{
			//接受市价委托和限价委托	
			orderDao.addOrder(obj);
			//参与连续竞价
			mgr.lXJJ(obj);
			
		}else if(m.getStatus().equals("1") && m.getPeriod().equals("1"))
		{
			//集合竞价待所有委托收到后统一撮合
			//集合竞价规则校验
			if(obj.getOrdertype() == 1) {
				//此时不允许市价委托
				obj.setStatus("4");
			}
			orderDao.addOrder(obj);
		}else if(m.getStatus().equals("1") && m.getPeriod().equals("2"))
		{
			//集合竞价撮合阶段，停止接受申报，设置为废单
			obj.setStatus("4");
			orderDao.addOrder(obj);
		}
	}
}
