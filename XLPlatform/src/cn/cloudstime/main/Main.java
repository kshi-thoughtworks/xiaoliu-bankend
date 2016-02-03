package cn.cloudstime.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import redis.clients.jedis.Transaction;

import cn.cloudstime.core.Init;
import cn.cloudstime.core.thread.LogThread;
import cn.cloudstime.core.thread.NotifyThread;
import cn.cloudstime.core.thread.OutputThread;
import cn.cloudstime.core.thread.ResponseThread;
import cn.cloudstime.core.thread.TransactionThread;
import cn.cloudstime.global.Global;


public class Main {
	
	public static void main(String[] args) {
		
		Main.class.getClassLoader().getResource("log4j.properties");
		
		Init.initBusinessUser();
		Init.initPlatform();
		
		
		TransactionThread transaction=new TransactionThread();
		transaction.run();
		
		OutputThread output=new OutputThread();
		output.run();
		
		LogThread log=new LogThread();
		log.run();
		
		NotifyThread notify=new NotifyThread();
		notify.run();
		
		ResponseThread response=new ResponseThread();
		response.run();
		
		
		
		
		
		
	}

}
