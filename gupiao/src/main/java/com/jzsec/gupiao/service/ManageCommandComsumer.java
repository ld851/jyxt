package com.jzsec.gupiao.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

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
import com.jzsec.gupiao.dao.ICustomerDao;
import com.jzsec.gupiao.dao.IMarketDao;
import com.jzsec.gupiao.dao.IOrderDao;
import com.jzsec.gupiao.entity.Customer;
import com.jzsec.gupiao.entity.ManageCommand;
import com.jzsec.gupiao.entity.Market;
import com.jzsec.gupiao.entity.Order;

@Configuration
@EnableKafka
public class ManageCommandComsumer {
	
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
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, "managehandler"); //组别
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return propsMap;
    }

    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<String, String>(consumerConfigs());
    }
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory1() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1); //设置消费者线程
        factory.getContainerProperties().setPollTimeout(1500);//设置查询时间
        return factory;
    }
    @Bean
    public ManageListener listener1() {
        return new ManageListener();
    }
}


class ManageListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Gson gson = new GsonBuilder().create();
	@Resource
	private IOrderDao orderDao;
	
	@Resource 
	private IMarketDao marketDao;
	
	@Resource
	private ICustomerDao customerDao;
	
	@Autowired
	private OrderMgt mgt;

	@KafkaListener(topics = {"manage"})
	public void processMsg(String content) {
		ManageCommand obj = gson.fromJson(content, ManageCommand.class);
		if(obj == null) {
			logger.error("command error: null");
			return;
		}

		//集合竞价 命令格式如：{"type":"JHJJ","parameter":"0"}
		if(("JHJJ").equals(obj.getType())){	
			//修改市场状态为集合竞价撮合阶段
			Market m = marketDao.queryByMarketID(obj.getParameter());
			m.setPeriod("2");
			marketDao.updateMarket(m);
			mgt.jHJJ();
			
		}else if(("LXJJ").equals(obj.getType())){
			//修改市场状态为连续竞价撮合阶段 {"type":"LXJJ","parameter":"0"}
			Market m = marketDao.queryByMarketID(obj.getParameter());
			m.setPeriod("3");
			mgt.loadUncompletedOrder("");
			marketDao.updateMarket(m);		
		}else if(("ADDUSER").equals(obj.getType())){
			//增加客户 {"type":"ADDUSER","parameter":'{"custid":"2222","custname":"liming","fundavl":100000}'}
			String custinfo = obj.getParameter();
			Customer c = gson.fromJson(custinfo, Customer.class);
			//c.setCustid(1111);
			customerDao.addCustomer(c);
		} else {
			logger.error("unkown command:" + obj.getType());
		}

	}
}
