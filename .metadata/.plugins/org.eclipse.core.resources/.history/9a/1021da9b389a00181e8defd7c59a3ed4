package com.jzsec.webclient;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jzsec.gupiao.entity.HQShow;
import com.jzsec.gupiao.entity.Order;
import com.jzsec.webclient.service.MsgSender;



@SpringBootApplication
@MapperScan("com.jzsec.webclient.dao")
public class App
{
	
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
        
    }
    
    
    @Bean
    public HQShow hq() {
    	//内存行情
    	return new HQShow();
    }
    
}
