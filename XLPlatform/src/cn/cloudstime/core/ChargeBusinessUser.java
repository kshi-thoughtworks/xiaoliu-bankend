package cn.cloudstime.core;

import cn.cloudstime.bean.BusinessUserBalanceBean;
import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;
import java.util.Map;
import org.json.JSONObject;

public class ChargeBusinessUser {
	public boolean charge(JSONObject request) {
		
		request.put("transaction_code", SysOrderNoFinder.getOrderNo());
		BusinessUserBalanceBean bubb = (BusinessUserBalanceBean) Global.BUSINESS_USER_BALANCE
				.get(Global.HEAD_BUSINESS_USER_BALANCE + request.get("business_user_code"));

		if (bubb.transaction(Integer.valueOf(2), Long.valueOf(request.getLong("price"))).longValue() > -1L) {
			JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
		} else {
			JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
			return false;
		}

		return true;
	}
}