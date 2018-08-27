package com.jzsec.webclient.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import com.jzsec.gupiao.entity.HQShow;



@Configuration
@EnableKafka
public class HqConsumer {
	
	@Value("${kafka.server}")
	private String server;
	
	@Autowired
	ApplicationContext context;
	
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<String, Object>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        
        //是否自动周期性提交已经拉取到消费端的消息offset
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "6000");
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, "hqconsumer1"); //组别
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
        factory.getContainerProperties().setPollTimeout(1000);//设置查询时间
        return factory;
    }
    @Bean
    public HQListener listener(ApplicationContext atx) {
        return new HQListener(atx);
    }
}

class HQListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Gson gson = new GsonBuilder().create();
	private ApplicationContext context;

	@KafkaListener(topics = {"hq"})
	public void processMsg(String content) {
		HQShow obj = gson.fromJson(content, HQShow.class);
		HQShow hqcontainer = context.getBean(HQShow.class);	
		BeanUtils.copyProperties(obj, hqcontainer);
	}
	public HQListener(ApplicationContext atx) {
		context = atx;
	}
}
