package cn.cloudstime.core;

import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

import cn.cloudstime.global.Global;
import cn.cloudstime.main.ECUtils;
import cn.cloudstime.main.FJUtils;
import cn.cloudstime.main.LMUtils;
import cn.cloudstime.main.MXUtils;
import cn.cloudstime.util.JedisUtil;
import ecinterface.adc.NGEC;

public class OutputManager {
	
	public boolean output(JSONObject request)
	{
		
		JSONObject channels=request.getJSONObject("channels");
		
		if(channels.has("channel_"+request.getInt("target_channel_index")))
		{
			JSONObject channel=channels.getJSONObject("channel_"+request.getInt("target_channel_index"));
			
			if(channel.getInt("interface_flag")==1)
			 {
				System.out.println("广东出口");
				NGEC  ngec=null;
				 //未完成
				 //第一个参数要替换为流水号
				try {
					ngec=ECUtils.EC0001(request.getString("transaction_code"), "2000473393", "llzyc1", "v5VF7yabChZymsqrLuyrIYdKHnL1eYEt", request.getString("phone"), request.getInt("flowValue"));
					 
				} catch (Exception e) {
					e.printStackTrace();
					ngec=null;
				}
				
				if(ngec!=null&&"0000".equals(ngec.getResponse().getRspCode()))
				 {
					 //记录日志成功交易
					 request.put("transaction_state", 2);
					 request.put("platform_name", channel.getString("name"));
					 
					 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
					 //发送到response
					 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
					 
				 }
				 else
				 {
					 
						 if(channels.has("channel_"+(request.getInt("target_channel_index")+1)))
						 {
							 request.put("target_channel_index", request.getInt("target_channel_index")+1);
							 //通道递归
							 this.output(request);
						 }
						 else
						 {
							 if(ngec==null)
							 {
								//记录日志失败交易
								 request.put("transaction_error_code",Global.EXCEPTION_CHANNEL_OUTPUT_ERROR);
								 request.put("transaction_state", 3);
								 request.put("transaction_error_info",Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
								 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
								 //发送到response
								 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
							 }
							 else{
							 
								//记录日志失败交易
								 request.put("transaction_error_code", Global.EXCEPTION_CHANNEL_OUTPUT_ERROR);
								 request.put("transaction_state", 3);
								 request.put("transaction_error_info", ngec.getResponse().getRspDesc());
								 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
								 //发送到response
								 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
							 }
							 //发送请求到扣费单元将消费回滚
							 request.put("transaction_type", 3);
							 request.put("price", ((JSONObject)channels.get("channel_0")).getLong("dprice"));
							 JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
							 return false;
						 }
					 
					
				 }
				 
				 
			}
			else if(channel.getInt("interface_flag")==2)
			{
				System.out.println("MX出口");
				String result=null;
				
				try {
					 result=MXUtils.order("10004", "XxIob4VXAAW9oDz8yJbNZvf2", request.getString("transaction_code"), request.getString("phone"), request.getInt("flowValue"), 0, "http://recharge.service.azurenet.cn/notify/miaoxun", "");
				} catch (Exception e) {
					result=null;
				}
				
				
				if(result!=null)
				{
					JSONObject object=new JSONObject(result);
					System.out.println("MXresult:"+object);
					
					if(object.getInt("retCode")==0)
					{
						
						System.out.println("MXsucces");
//						 //记录日志成功交易
//						 request.put("transaction_state", 2);
//						 request.put("platform_name", channel.getString("name"));
//						 
//						 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
//						 //发送到response
//						 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
						 
						//异步通知接口在成功请求后放入set  等待回掉处理
						 
						 JedisUtil.set(Global.HEAD_NOTIFY_INFO+request.getString("transaction_code"), request.toString());
						 
					}
					else
					{
						
						//发送请求到扣费单元将消费回滚
						 request.put("transaction_type", 3);
						 request.put("price", ((JSONObject)channels.get("channel_0")).getLong("dprice"));
						 JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
						 
						 request.put("transaction_type", 1);
						
						
						if(channels.has("channel_"+(request.getInt("target_channel_index")+1)))
						 {
							 request.put("target_channel_index", request.getInt("target_channel_index")+1);
							 //通道递归
							 this.output(request);
						 }
						 else
						 {
							 
							//记录日志失败交易
							 request.put("transaction_error_code", Global.EXCEPTION_CHANNEL_OUTPUT_ERROR);
							 request.put("transaction_state", 3);
							 request.put("transaction_error_info", object.getString("retMsg"));
							 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
							 //发送到response
							 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
							 
							 
							 return false;
						 }
					}
					
					
				}
				else
				{
					//发送请求到扣费单元将消费回滚
					 request.put("transaction_type", 3);
					 request.put("price", ((JSONObject)channels.get("channel_0")).getLong("dprice"));
					 JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
					 
					 request.put("transaction_type", 1);
					
					
					
					if(channels.has("channel_"+(request.getInt("target_channel_index")+1)))
					 {
						 request.put("target_channel_index", request.getInt("target_channel_index")+1);
						 //通道递归
						 this.output(request);
					 }
					 else
					 {
						 
						//记录日志失败交易
						 request.put("transaction_error_code", Global.EXCEPTION_CHANNEL_OUTPUT_ERROR);
						 request.put("transaction_state", 3);
						 request.put("transaction_error_info",Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
						 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
						 //发送到response
						 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
						 
						 
						 return false;
					 }
				}
				
				
				
				
			}
			else if(channel.getInt("interface_flag")==3)
			{
				String result1=null;
				
				try {
					
					
					String id="cztest01";
					System.out.println(request.getString("transaction_code")+"||"+request.getString("phone")+"||"+request.getInt("flowValue"));	
					result1=FJUtils.order("cztest01", "11", "cztest01", "2642124", request.getString("phone"), "1", request.getInt("flowValue"));
					//result1=FJUtils.order(id, String.valueOf(System.currentTimeMillis()+String.valueOf(Math.random()*100)),id, request.getString("transaction_code"), request.getString("phone"), "1", request.getInt("flowValue"));
					System.out.println(result1);	
				} catch (Exception e) {
					result1=null;
				}
				if(result1==null)
				{
					//记录日志失败交易
					 request.put("transaction_error_code",Global.EXCEPTION_CHANNEL_OUTPUT_ERROR);
					 request.put("transaction_state", 3);
					 request.put("transaction_error_info",Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
					 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
					 
					 //发送请求到扣费单元将消费回滚
					 request.put("transaction_type", 3);
					 request.put("price", ((JSONObject)channels.get("channel_0")).getLong("dprice"));
					 JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
					 return false;
				}
				else
				{
					JSONObject object1=new JSONObject(result1);
					
					if("0000".equals(object1.getString("result")))
					{
						 //记录日志成功交易
						 request.put("transaction_state", 2);
						 request.put("platform_name", channel.getString("name"));
						 
						 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
						 //发送到response
						 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
					}
					else
					{
						if(channels.has("channel_"+(request.getInt("target_channel_index")+1)))
						 {
							 request.put("target_channel_index", request.getInt("target_channel_index")+1);
							 //通道递归
							 this.output(request);
						 }
						 else
						 {
							 
							//记录日志失败交易
							 request.put("transaction_error_code", Global.EXCEPTION_CHANNEL_OUTPUT_ERROR);
							 request.put("transaction_state", 3);
							 request.put("transaction_error_info", object1.getString("desc"));
							 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
							 
							 //发送请求到扣费单元将消费回滚
							 request.put("transaction_type", 3);
							 request.put("price", ((JSONObject)channels.get("channel_0")).getLong("dprice"));
							 JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
							 return false;
						 }
					}
				}
				
				
			}
			else if(channel.getInt("interface_flag")==4)
			{
				System.out.println("ML出口");
				
				String result2=null;
				
				try {
					result2=LMUtils.order("zDUuumTAGX", "GI1LzYLzV60G7qGN", request.getString("phone"), String.valueOf(request.getInt("flowValue")), request.getString("operator_code"),request.getString("transaction_code"));
				} catch (Exception e) {
					result2=null;
				}
				
				
				if(result2!=null)
				{
					JSONObject object2=new JSONObject(result2);
					
					if("000".equals(object2.getString("code")))
					{
						System.out.println("MLsucces");
//						 //记录日志成功交易
//						 request.put("transaction_state", 2);
//						 request.put("platform_name", channel.getString("name"));
//						 
//						 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
//						 //发送到response
//						 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
						 
						//异步通知接口在成功请求后放入set  等待回掉处理
						 
						 JedisUtil.set(Global.HEAD_NOTIFY_INFO+request.getString("transaction_code"), request.toString());
						 
					}
					else
					{
						System.out.println("MLfail");
						
						//发送请求到扣费单元将消费回滚
						 request.put("transaction_type", 3);
						 request.put("price", ((JSONObject)channels.get("channel_0")).getLong("dprice"));
						 JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
						 
						 request.put("transaction_type", 1);
						
						
						if(channels.has("channel_"+(request.getInt("target_channel_index")+1)))
						 {
							System.out.println("nextChannel");
							 request.put("target_channel_index", request.getInt("target_channel_index")+1);
							 //通道递归
							 this.output(request);
						 }
						 else
						 {
							 
							 System.out.println("SendtoLOG");
							//记录日志失败交易
							 request.put("transaction_error_code", Global.EXCEPTION_CHANNEL_OUTPUT_ERROR);
							 request.put("transaction_state", 3);
							 
							 if(!request.has("transaction_error_info"))
							 {
							 request.put("transaction_error_info",Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
							 
							 }
							 
							 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
							 //发送到response
							 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
							 
							 
							 return false;
						 }
					}
					
					
				}
				else
				{
					//发送请求到扣费单元将消费回滚
					 request.put("transaction_type", 3);
					 request.put("price", ((JSONObject)channels.get("channel_0")).getLong("dprice"));
					 JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
					 
					 request.put("transaction_type", 1);
					
					
					
					if(channels.has("channel_"+(request.getInt("target_channel_index")+1)))
					 {
						 request.put("target_channel_index", request.getInt("target_channel_index")+1);
						 //通道递归
						 this.output(request);
					 }
					 else
					 {
						 
						//记录日志失败交易
						 request.put("transaction_error_code", Global.EXCEPTION_CHANNEL_OUTPUT_ERROR);
						 request.put("transaction_state", 3);
						 request.put("transaction_error_info",Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
						 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
						 //发送到response
						 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
						 
						 
						 return false;
					 }
				}
				
				
				
				
			}
				
				
			
			
			
			
		}
		else
		{
			//记录日志失败交易
			 request.put("transaction_error_code", Global.EXCEPTION_CHANNEL_OUTPUT_ERROR);
			 request.put("transaction_state", 3);
			 request.put("transaction_error_info",Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
			 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
			 //发送到response
			 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
		}
		
		
		return true;
	}

}
