package cn.cloudstime.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import redis.clients.jedis.Transaction;

import cn.cloudstime.core.Init;
import cn.cloudstime.core.thread.ChannelFailLogThread;
import cn.cloudstime.core.thread.LogThread;
import cn.cloudstime.core.thread.NotifyThread;
import cn.cloudstime.core.thread.OutputThread;
import cn.cloudstime.core.thread.ResponseThread;
import cn.cloudstime.core.thread.TransactionThread;
import cn.cloudstime.global.Global;


public class Main {
	
	public static void main(String[] args) {
		
		try {
			
			System.out.println("加载配置文件");
			
			Global.factory=new ClassPathXmlApplicationContext("ApplicationContext.xml");
			
			Global.jdbcTemplate= (JdbcTemplate) Global.factory.getBean("jdbcTemplate");
		
		Main.class.getClassLoader().getResource("log4j.properties");
		
		
		
		Init.initBusinessUser();
		Init.initPlatform();
		
		System.out.println("缓存内容加载完毕");
		
		
		TransactionThread transaction=new TransactionThread();
		transaction.run();
		
		
		System.out.println("线程数量："+Global.THREAD_POOL.size());
		
		OutputThread output=new OutputThread();
		output.run();
		
		System.out.println("线程数量："+Global.THREAD_POOL.size());
		
		LogThread log=new LogThread();
		log.run();
		
		System.out.println("线程数量："+Global.THREAD_POOL.size());
		
		NotifyThread notify=new NotifyThread();
		notify.run();
		
		System.out.println("线程数量："+Global.THREAD_POOL.size());
		
		ResponseThread response=new ResponseThread();
		response.run();
		
		System.out.println("线程数量："+Global.THREAD_POOL.size());
		
		ChannelFailLogThread channellog=new ChannelFailLogThread();
		channellog.run();
		
		System.out.println("线程数量："+Global.THREAD_POOL.size());
		
		
		
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

}
