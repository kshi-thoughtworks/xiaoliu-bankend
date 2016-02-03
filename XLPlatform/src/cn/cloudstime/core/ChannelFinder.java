package cn.cloudstime.core;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.cloudstime.dao.TransactionDao;

import redis.clients.jedis.Transaction;

import cn.cloudstime.global.Global;

public class ChannelFinder {
	
	
	public JSONObject findChannel(JSONObject request)
	{
		try {
			
		TransactionDao dao=new TransactionDao();
		
		return dao.findChannel(request);
		
		} catch (Exception e) {
			e.printStackTrace();
			//未完成
			//向错误日志队列写“FIND_CHANNEL_ERROR”
			return null;
		}
		
		
		
	}

}
