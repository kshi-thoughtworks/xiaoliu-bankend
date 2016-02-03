package cn.cloudstime.core;

import org.json.JSONObject;

import cn.cloudstime.bean.BusinessUserBalanceBean;
import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;

public class ChargeBusinessUser {
	
	public boolean charge(JSONObject request)
	{
		BusinessUserBalanceBean bubb=Global.BUSINESS_USER_BALANCE.get(Global.HEAD_BUSINESS_USER_BALANCE+request.get("business_user_code"));
		
		if(bubb.transaction(2, request.getLong("price"))>-1L)
		{
			JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
		}
		else
		{
			JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
			return false;
		}
		
		return true;
	}

}
