package cn.cloudstime.core;

import cn.cloudstime.global.Global;
import cn.cloudstime.main.CardUtil;
import cn.cloudstime.main.ECUtils;
import cn.cloudstime.main.FJUtils;
import cn.cloudstime.main.LMUtils;
import cn.cloudstime.main.MXUtils;
import cn.cloudstime.util.JedisUtil;
import ecinterface.adc.NGEC;
import ecinterface.adc.Response;

import java.awt.CardLayout;
import java.io.PrintStream;
import java.util.Map;
import org.json.JSONObject;

public class OutputManager {
	public boolean output(JSONObject request) {
		
//		System.out.println("出口:"+request);
//		
//		
//		return true;
		
		JSONObject channels = request.getJSONObject("channels");

		if (channels.has("channel_" + request.getInt("target_channel_index"))) {
			JSONObject channel = channels.getJSONObject("channel_" + request.getInt("target_channel_index"));
			request.put("platform_name", channel.get("name"));

			if (channel.getInt("interface_flag") == 1) {
				System.out.println("广东出口");
				NGEC ngec = null;
				try {
					ngec = ECUtils.EC0001(request.getString("transaction_code"), "2000702804", "1",
							"4LM0NLN1r3OEASfEIL3i2YdKHnL1eYEt", request.getString("phone"),
							Integer.valueOf(request.getInt("flowValue")));
				} catch (Exception e) {
					e.printStackTrace();
					ngec = null;
				}

				if ((ngec != null) && ("0000".equals(ngec.getResponse().getRspCode()))) {
					
					channel_output_success_sync(request,channel);
					
				}  else {
					
					if(has_next_channel(request, channels))
					{
						channel_output_fail_channellog(request,ngec.getResponse().getRspCode(),3,ngec.getResponse().getRspDesc());
						try_next_channel(request, channels);
					}
					else
					{
					
						if (ngec == null) {
							
							channel_output_fail_transactionlog(request,Global.EXCEPTION_CHANNEL_OUTPUT_ERROR,3,Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
							channel_output_fail_channellog(request,"",3,"");
						} else {
							
							channel_output_fail_transactionlog(request,Global.EXCEPTION_CHANNEL_OUTPUT_ERROR,3,Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
							
							channel_output_fail_channellog(request,ngec.getResponse().getRspCode(),3,ngec.getResponse().getRspDesc());
							
						}
						
						refund(request, channels);
					}
					
	
					return false;
				}

			} else if (channel.getInt("interface_flag") == 2) {
				System.out.println("MX出口");
				String result = null;
				try {
					result = MXUtils.order("10004", "XxIob4VXAAW9oDz8yJbNZvf2", request.getString("transaction_code"),
							request.getString("phone"), Integer.valueOf(request.getInt("flowValue")),
							Integer.valueOf(0), "http://recharge.service.azurenet.cn/notify/miaoxun", "");
				} catch (Exception e) {
					result = null;
				}

				if (result != null) {
					JSONObject object = new JSONObject(result);
					System.out.println("MXresult:" + object);

					if (object.getInt("retCode") == 0) {
						
						channel_output_success_asyc(request);
						
					} else {
						
						if(has_next_channel(request, channels))
						{
							channel_output_fail_channellog(request,String.valueOf(object.getInt("retCode")),3, object.getString("retMsg"));
							try_next_channel(request, channels);
						}
						else{
							channel_output_fail_transactionlog(request,Global.EXCEPTION_CHANNEL_OUTPUT_ERROR,3,Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
							
							channel_output_fail_channellog(request,String.valueOf(object.getInt("retCode")),3, object.getString("retMsg"));
							
							refund(request, channels);
						}
						
						return false;
						

					}

				} else {
					
						if(has_next_channel(request, channels))
						{
							channel_output_fail_channellog(request,"",3, "");
							try_next_channel(request, channels);
						}
						else
						{
							channel_output_fail_transactionlog(request,Global.EXCEPTION_CHANNEL_OUTPUT_ERROR,3,Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
							channel_output_fail_channellog(request,"",3, "");
							refund(request, channels);
						}
						return false;

				}

//			} else if (channel.getInt("interface_flag") == 3) {
//				String result1 = null;
//				try {
//					String id = "cztest01";
//					System.out.println(request.getString("transaction_code") + "||" + request.getString("phone") + "||"
//							+ request.getInt("flowValue"));
//					result1 = FJUtils.order("cztest01", "11", "cztest01", "2642124", request.getString("phone"), "1",
//							request.getInt("flowValue"));
//
//					System.out.println(result1);
//				} catch (Exception e) {
//					result1 = null;
//				}
//				if (result1 == null) {
//					request.put("transaction_error_code", Global.EXCEPTION_CHANNEL_OUTPUT_ERROR);
//					request.put("transaction_state", 3);
//					request.put("transaction_error_info",
//							Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
//					JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
//
//					request.put("transaction_type", 3);
//					request.put("price", ((JSONObject) channels.get("channel_0")).getLong("dprice"));
//					JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
//					return false;
//				}
//
//				JSONObject object1 = new JSONObject(result1);
//
//				if ("0000".equals(object1.getString("result"))) {
//					request.put("transaction_state", 2);
//					request.put("platform_name", channel.getString("name"));
//
//					JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
//
//					JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
//				}  else {
//					
//					if (channels.has("channel_" + (request.getInt("target_channel_index") + 1))) {
//						request.put("target_channel_index", request.getInt("target_channel_index") + 1);
//
//						output(request);
//					}
//					
//					
//					request.put("transaction_error_code", Global.EXCEPTION_CHANNEL_OUTPUT_ERROR);
//					request.put("transaction_state", 3);
//					request.put("transaction_error_info", object1.getString("desc"));
//					JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
//
//					request.put("transaction_type", 3);
//					request.put("price", ((JSONObject) channels.get("channel_0")).getLong("dprice"));
//					JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
//					return false;
//				}
//
//			} 
			}else if (channel.getInt("interface_flag") == 4) {
				System.out.println("ML出口");

				String result2 = null;
				try {
					result2 = LMUtils.order("zDUuumTAGX", "GI1LzYLzV60G7qGN", request.getString("phone"),
							String.valueOf(request.getInt("flowValue")), request.getString("operator_code"),
							request.getString("transaction_code"));
				} catch (Exception e) {
				    e.printStackTrace();
					result2 = null;
				}

				if (result2 != null) {
					JSONObject object2 = new JSONObject(result2);

					if ("000".equals(object2.getString("code"))) {
						
						channel_output_success_asyc(request);
						
					} 
					else 
					{
						System.out.println("MLfail");
						
						if(has_next_channel(request, channels))
						{
							channel_output_fail_channellog(request,String.valueOf(object2.getString("code")),3, "");
							try_next_channel(request, channels);
						}
						else
						{
							channel_output_fail_transactionlog(request,Global.EXCEPTION_CHANNEL_OUTPUT_ERROR,3,Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
							
							channel_output_fail_channellog(request,String.valueOf(object2.getString("code")),3, "");
							
							refund(request, channels);
						}

					}

				} else {
					
					System.out.println("MLfail"+"出口:"+has_next_channel(request, channels));
					
					
					if(has_next_channel(request, channels))
					{
						channel_output_fail_channellog(request,"",3, "");
						try_next_channel(request, channels);
					}
					else
					{
						channel_output_fail_transactionlog(request,Global.EXCEPTION_CHANNEL_OUTPUT_ERROR,3,Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
						
						channel_output_fail_channellog(request,"",3, "");
						
						refund(request, channels);
					}

				}

			}else if (channel.getInt("interface_flag") == 5) {
				System.out.println("WLWK出口");

				String result3 = null;
				try {
					result3 =CardUtil.order("100861405", "21vianet100861405", request.getString("transaction_code"), request.getString("phone"), Integer.valueOf(request.getString("expiryDate")));
				} catch (Exception e) {
					
					result3 = null;
				}

				if (result3 != null) {
					JSONObject object3 = new JSONObject(result3);

					if ("0000".equals(object3.getString("status"))) {
						
						channel_output_success_asyc(request);
						
					} 
					else 
					{
						System.out.println("WLWKfail");
						
						if(has_next_channel(request, channels))
						{
							channel_output_fail_channellog(request,String.valueOf(object3.getString("status")),3, object3.getString("message"));
							try_next_channel(request, channels);
						}
						else
						{
							
							System.out.println("记录错误日志");
							channel_output_fail_transactionlog(request,Global.EXCEPTION_CHANNEL_OUTPUT_ERROR,3,Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
							
							channel_output_fail_channellog(request,String.valueOf(object3.getString("status")),3, object3.getString("message"));
							
							refund(request, channels);
						}

					}

				} else {
					if(has_next_channel(request, channels))
					{
						channel_output_fail_channellog(request,"",3, "");
						try_next_channel(request, channels);
					}
					else
					{
						channel_output_fail_transactionlog(request,Global.EXCEPTION_CHANNEL_OUTPUT_ERROR,3,Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
						
						channel_output_fail_channellog(request,"",3, "");
						
						refund(request, channels);
					}

				}

			}

		} else {
			
			channel_output_fail_transactionlog(request,Global.EXCEPTION_CHANNEL_OUTPUT_ERROR,3,Global.EXCEPTION_MAP.get(Global.EXCEPTION_CHANNEL_OUTPUT_ERROR));
			
		}

		return true;
	}
	
	/**
	 * 第三方同步接口，返回成功后调用
	 * @param request
	 */
	private void channel_output_success_sync(JSONObject request,JSONObject channel)
	{
		request.put("transaction_state", 2);
		request.put("platform_name", channel.getString("name"));

		JedisUtil.lpush(Global.LOG_QUEUE, request.toString());

		JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
	}
	
	/**
	 * 第三方异步接口，返回成功后调用
	 * @param request
	 */
	private void channel_output_success_asyc(JSONObject request)
	{
		JedisUtil.set(Global.HEAD_NOTIFY_INFO + request.getString("transaction_code"),request.toString());
	}
	
	
	/**
	 * 尝试下一通道,如果没有下一通道进行退款操作
	 * @param request
	 * @param channels
	 */
	private void try_next_channel(JSONObject request,JSONObject channels)
	{
		 
			//JedisUtil.lpush(Global.CHANNELFAIL_QUEUE, request.toString());
			request.put("target_channel_index", request.getInt("target_channel_index") + 1);

			output(request);
			
		
	}
	
	private void refund(JSONObject request,JSONObject channels)
	{
			request.put("transaction_type", 3);
			request.put("price", ((JSONObject) channels.get("channel_0")).getLong("dprice"));
			JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());
	}
	
	/**
	 * 判断是否存在下一通道
	 * @param request
	 * @param channels
	 * @return
	 */
	private boolean has_next_channel(JSONObject request,JSONObject channels)
	{
		if (channels.has("channel_" + (request.getInt("target_channel_index") + 1))) {
			return true;
		    }
		else
		{
			return false;
		}
	}
	
	/**
	 * 通道调用失败执行_记录交易日志
	 * @param request
	 */
	private void channel_output_fail_transactionlog(JSONObject request,String error_code,Integer state,String error_info)
	{
		request.put("transaction_error_code", error_code);
		request.put("transaction_state", state);
		request.put("transaction_error_info", error_info);
		
		System.out.println("出口异常："+request);
		JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
		JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
	}
	
	/**
	 * 通道调用失败执行_记录通道失败日志
	 * @param request
	 */
	private void channel_output_fail_channellog(JSONObject request,String error_code,Integer state,String error_info)
	{
		request.put("transaction_error_code", error_code);
		request.put("transaction_state", state);
		request.put("transaction_error_info", error_info);
		JedisUtil.lpush(Global.CHANNELFAIL_QUEUE, request.toString());
	}
	
	
	
	
	
	
	
}