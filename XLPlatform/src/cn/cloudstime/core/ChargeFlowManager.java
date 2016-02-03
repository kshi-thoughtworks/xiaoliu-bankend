package cn.cloudstime.core;

import java.sql.Date;

import org.json.JSONObject;

import com.cloudstime.dao.TransactionDao;

import cn.cloudstime.bean.BusinessUserBalanceBean;
import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;


public class ChargeFlowManager {
	
	public boolean ChrageFlow(JSONObject request)
	{
	    //给请求添加流水号
		System.out.println("处理请求");
		
		request.put("transaction_code", SysOrderNoFinder.getOrderNo());
		//向日志队列写入
		 request.put("transaction_state", 1);
		 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
		 
		 
//		 //核对重复订单
//		 TransactionDao dao=new TransactionDao();
//		 
//		 if(!dao.checkBusinessOrderNo(request))
//		 {
//			 request.put("transaction_state", 3);
//			 request.put("transaction_error_code", Global.EXCEPTION_BUSINESS_ORDERNO_REPEAT);
//			 request.put("transaction_error_info", Global.EXCEPTION_MAP.get(Global.EXCEPTION_BUSINESS_ORDERNO_REPEAT));
//			 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
//			//向RESPONSE_QUEUE写入,STATE:BUSINESS STATE FALSE;
//			 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
//			 return false;
//		 }
//		
		
		
		//核对商户状态
		 if(!Global.BUSINESS_USER_STATE.containsKey(Global.HEAD_BUSINESS_USER_STATE+request.getString("business_user_code"))||Global.BUSINESS_USER_STATE.get(Global.HEAD_BUSINESS_USER_STATE+request.get("business_user_code").toString())==0)
		 {
			 request.put("transaction_state", 3);
			 request.put("transaction_error_code", Global.EXCEPTION_CODE_BUSINESS_USER_STATE_ERROR);
			 request.put("transaction_error_info", Global.EXCEPTION_MAP.get(Global.EXCEPTION_CODE_BUSINESS_USER_STATE_ERROR));
			 //向日志队列写入
			 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
			 //向RESPONSE_QUEUE写入,STATE:BUSINESS STATE FALSE;
			 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
			 return false;
		 }
		 //获取归属地
		 OwnershipFinder ownershipFinder=new OwnershipFinder();
		 JSONObject ownership =ownershipFinder.findOwnership(request);
		 if(ownership==null||ownership.getString("province")==null||ownership.getString("province")=="")
		 {
			 //向日志队列写入
			 request.put("transaction_state", 3);
			 request.put("transaction_error_code", Global.EXCEPTION_CODE_GET_OWNERSHIP_ERROR);
			 request.put("transaction_error_info", Global.EXCEPTION_MAP.get(Global.EXCEPTION_CODE_GET_OWNERSHIP_ERROR));
			 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
			 //向RESPONSE_QUEUE写入,STATE:OWNERSHIP  FALSE;
			 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
			 return false;
		 }
		 else
		 {
			 	request.put("area", ownership.getString("province"));

				if("移动".equals(ownership.getString("supplier"))||"中国移动".equals(ownership.getString("supplier")))
				{
					request.put("operator_code", "CM");
				}
				else if("联通".equals(ownership.getString("supplier"))||"中国联通".equals(ownership.getString("supplier")))
				{
					request.put("operator_code", "CU");
				}
				else if("电信".equals(ownership.getString("supplier"))||"中国电信".equals(ownership.getString("supplier")))
				{
					request.put("operator_code", "CT");
				}
		 }
		 
		 //获取通道
		 JSONObject channels=new JSONObject();
		 ChannelFinder cf=new ChannelFinder();
		 channels=cf.findChannel(request);
		 
		 if(channels==null||channels.length()==0)
		 {
			 System.out.println(request);
			 System.out.println("无可用通道");
			//向日志队列写入
			 request.put("transaction_state", 3);
			 request.put("transaction_error_code", Global.EXCEPTION_CODE_CHANNEL_NULL_ERROR);
			 request.put("transaction_error_info", Global.EXCEPTION_MAP.get(Global.EXCEPTION_CODE_CHANNEL_NULL_ERROR));
			 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
			//向RESPONSE_QUEUE写入,STATE:CHANNEL_NONE;
			 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
			return false;
		 }
		 else
		 {
			 
			 request.put("channels", channels);
			 
			 request.put("target_channel_index", 0);
			 
			 if(channels.has("channel_"+0))
			 {
				 //扣费
       		 BusinessUserBalanceBean bubb=Global.BUSINESS_USER_BALANCE.get(Global.HEAD_BUSINESS_USER_BALANCE+request.getString("business_user_code"));
       		 
       		 Long balance=bubb.transaction(1, ((JSONObject)channels.get("channel_0")).getLong("dprice"));
       		 
       		 if(balance>-1L)
       		 {
       			 request.put("balance", balance);
       			 request.put("deduction", ((JSONObject)channels.get("channel_0")).getLong("dprice"));
       			 request.put("set_code", ((JSONObject)channels.get("channel_0")).getString("set_code"));
       			 request.put("interface_flag", ((JSONObject)channels.get("channel_0")).getInt("interface_flag"));
       			 request.put("platform_id", ((JSONObject)channels.get("channel_0")).getInt("platform_id"));
       			 request.put("platform_name", ((JSONObject)channels.get("channel_0")).getString("name"));
       			 request.put("tprice", ((JSONObject)channels.get("channel_0")).getLong("tprice"));
       			 //发送到发送队列
       			 JedisUtil.lpush(Global.OUPPUT_QUEUE, request.toString());
       			
       		 }
       		 else
       		 {
       			 request.put("balance",Global.BUSINESS_USER_BALANCE.get(Global.HEAD_BUSINESS_USER_BALANCE+request.getString("business_user_code")).getBalance());
      			 request.put("deduction", ((JSONObject)channels.get("channel_0")).getLong("dprice"));
      			 request.put("set_code", ((JSONObject)channels.get("channel_0")).getString("set_code"));
      			 request.put("interface_flag", ((JSONObject)channels.get("channel_0")).getInt("interface_flag"));
      			 request.put("platform_id", ((JSONObject)channels.get("channel_0")).getInt("platform_id"));
      			 request.put("platform_name", ((JSONObject)channels.get("channel_0")).getString("name"));
      			 request.put("tprice", ((JSONObject)channels.get("channel_0")).getLong("tprice"));
       			 //写日志
       			request.put("transaction_state", 3);
       			request.put("transaction_error_code", Global.EXCEPTION_CODE_BUSINESS_BALANCE_NOTENOUGH_ERROR);
       			request.put("transaction_error_info", Global.EXCEPTION_MAP.get(Global.EXCEPTION_CODE_BUSINESS_BALANCE_NOTENOUGH_ERROR));
       			JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
       			//发送到通知队列 
       			
       		    JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
       		    return false;
       		 }
       		 
       		 
			 }
			 
   		 
   		 return true;
		 }
	}

}
