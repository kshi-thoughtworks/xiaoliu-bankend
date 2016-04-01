package cn.cloudstime.core;

import cn.cloudstime.bean.BusinessUserBalanceBean;
import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;
import java.io.PrintStream;
import java.util.Map;
import org.json.JSONObject;

public class ChargeFlowManager {
	public boolean ChrageFlow(JSONObject request) {
		System.out.println("处理请求"+request);

		request.put("transaction_code", SysOrderNoFinder.getOrderNo());

		request.put("transaction_state", 1);
		JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
		
		
		

		if ((!Global.BUSINESS_USER_STATE
				.containsKey(Global.HEAD_BUSINESS_USER_STATE + request.getString("business_user_code")))
				|| (((Integer) Global.BUSINESS_USER_STATE
						.get(Global.HEAD_BUSINESS_USER_STATE + request.get("business_user_code").toString()))
								.intValue() == 0)) {
			request.put("transaction_state", 3);
			request.put("transaction_error_code", Global.EXCEPTION_CODE_BUSINESS_USER_STATE_ERROR);
			request.put("transaction_error_info",
					Global.EXCEPTION_MAP.get(Global.EXCEPTION_CODE_BUSINESS_USER_STATE_ERROR));

			JedisUtil.lpush(Global.LOG_QUEUE, request.toString());

			JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
			return false;
		}

		OwnershipFinder ownershipFinder = new OwnershipFinder();
		JSONObject ownership = ownershipFinder.findOwnership(request);
		if ((ownership == null) || (ownership.getString("province") == null)
				|| (ownership.getString("province") == "")) {
			request.put("transaction_state", 3);
			request.put("transaction_error_code", Global.EXCEPTION_CODE_GET_OWNERSHIP_ERROR);
			request.put("transaction_error_info", Global.EXCEPTION_MAP.get(Global.EXCEPTION_CODE_GET_OWNERSHIP_ERROR));
			JedisUtil.lpush(Global.LOG_QUEUE, request.toString());

			JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
			return false;
		}

		request.put("area", ownership.getString("province"));

		if (("移动".equals(ownership.getString("supplier"))) || ("中国移动".equals(ownership.getString("supplier")))) {
			request.put("operator_code", "CM");
		} else if (("联通".equals(ownership.getString("supplier"))) || ("中国联通".equals(ownership.getString("supplier")))) {
			request.put("operator_code", "CU");
		} else if (("电信".equals(ownership.getString("supplier"))) || ("中国电信".equals(ownership.getString("supplier")))) {
			request.put("operator_code", "CT");
		}else if (("其他".equals(ownership.getString("supplier")))) {
			request.put("operator_code", "OT");
		}

		JSONObject channels = new JSONObject();
		ChannelFinder cf = new ChannelFinder();
		channels = cf.findChannel(request);

		if ((channels == null) || (channels.length() == 0)) {
			System.out.println(request);
			System.out.println("无可用通道");

			request.put("transaction_state", 3);
			request.put("transaction_error_code", Global.EXCEPTION_CODE_CHANNEL_NULL_ERROR);
			request.put("transaction_error_info", Global.EXCEPTION_MAP.get(Global.EXCEPTION_CODE_CHANNEL_NULL_ERROR));
			JedisUtil.lpush(Global.LOG_QUEUE, request.toString());

			JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
			return false;
		}

		request.put("channels", channels);

		request.put("target_channel_index", 0);

		if (channels.has("channel_0")) {
			BusinessUserBalanceBean bubb = (BusinessUserBalanceBean) Global.BUSINESS_USER_BALANCE
					.get(Global.HEAD_BUSINESS_USER_BALANCE + request.getString("business_user_code"));

			bubb.transaction(Integer.valueOf(1),
					Long.valueOf(((JSONObject) channels.get("channel_0")).getLong("dprice")),request);
		
		}

		return true;
	}
}