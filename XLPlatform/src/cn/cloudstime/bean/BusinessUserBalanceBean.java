package cn.cloudstime.bean;

import org.json.JSONObject;

import cn.cloudstime.core.SendSMS;
import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;

import com.vianet.module.recharge.pay.api.service.IPayService;
import com.vianet.module.recharge.pay.api.vo.BalanceVO;
import com.vianet.module.recharge.pay.api.vo.PayVO;
import com.vianet.module.recharge.pay.api.vo.request.PayRequest;
import com.vianet.module.recharge.pay.api.vo.res.PayResultResponse;

public class BusinessUserBalanceBean {
	private String code;
	private Long balance;

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getBalance() {
		return this.balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public  boolean transaction(Integer type, Long price,JSONObject request) {
		
		JSONObject channels=request.has("channels")?request.getJSONObject("channels"):null;
		
		
		IPayService payService =(IPayService)Global.factory.getBean("payService");
		
		try {
			
			//获取balanceID
			BalanceVO balanceVO = payService.getUserBalance(this.code, request.has("balance_type")?request.getInt("balance_type"):1);
			
			if (null == balanceVO) {
				//商户不存在或已冻结
				

				//JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
				System.out.println(request.getString("transaction_code")+":充值接口调用失败");
				
				if(type.intValue() == 1)
				{
					//支付失败处理
					request.put("balance",-1L);
					request.put("deduction", ((JSONObject) channels.get("channel_0")).getLong("dprice"));
					request.put("set_code", ((JSONObject) channels.get("channel_0")).getString("set_code"));
					request.put("interface_flag", ((JSONObject) channels.get("channel_0")).getInt("interface_flag"));
					request.put("platform_id", ((JSONObject) channels.get("channel_0")).getInt("platform_id"));
					request.put("platform_name", ((JSONObject) channels.get("channel_0")).getString("name"));
					request.put("tprice", ((JSONObject) channels.get("channel_0")).getLong("tprice"));

					request.put("transaction_state", 3);
					request.put("transaction_error_code", Global.EXCEPTION_IPAY_INTERFACE_ERROR);
					request.put("transaction_error_info",
							Global.EXCEPTION_MAP.get(Global.EXCEPTION_IPAY_INTERFACE_ERROR));
					JedisUtil.lpush(Global.LOG_QUEUE, request.toString());

					JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
				}
				
				return false;
			}
			
			

			if (type.intValue() == 1) {
				System.out.println("充流量扣款:"+request.getString("transaction_code")+"："+price);
				//支付扣款
				PayRequest req = new PayRequest();
				req.setBalanceId(balanceVO.getBalanceId());
				req.setMathType(2);	//2:支付扣款
				req.setPrice(price);	//扣款费用100
				req.setTradeOrderno(request.getString("transaction_code"));

				PayResultResponse payResult = payService.pay(req);
				PayVO payVO = payResult.getResult();

				if ("0000000".equals(payResult.getRetCode())){
					//支付成功处理
					request.put("balance", payVO.getNewBalance());
					request.put("deduction", ((JSONObject) channels.get("channel_0")).getLong("dprice"));
					request.put("set_code", ((JSONObject) channels.get("channel_0")).getString("set_code"));
					request.put("interface_flag", ((JSONObject) channels.get("channel_0")).getInt("interface_flag"));
					request.put("platform_id", ((JSONObject) channels.get("channel_0")).getInt("platform_id"));
					request.put("platform_name", ((JSONObject) channels.get("channel_0")).getString("name"));
					request.put("tprice", ((JSONObject) channels.get("channel_0")).getLong("tprice"));
					request.put("dprice", ((JSONObject) channels.get("channel_0")).getLong("dprice"));

					JedisUtil.lpush(Global.OUPPUT_QUEUE, request.toString());
					
					
					//第一次达到告警余额通知处理
					if (payVO.getNewBalance().longValue() <= balanceVO.getAlertBalance().intValue()
							&& payVO.getOldBalance().longValue() > balanceVO.getAlertBalance().intValue()) {
						//发生告警短信
						SendSMS sms=new SendSMS();
						sms.send(this.code, payVO.getNewBalance().longValue());
					}
					
					return true;
					
				}else{
					//支付失败处理
					request.put("balance",payVO.getNewBalance()==null?-1L:payVO.getNewBalance());
					request.put("deduction", ((JSONObject) channels.get("channel_0")).getLong("dprice"));
					request.put("set_code", ((JSONObject) channels.get("channel_0")).getString("set_code"));
					request.put("interface_flag", ((JSONObject) channels.get("channel_0")).getInt("interface_flag"));
					request.put("platform_id", ((JSONObject) channels.get("channel_0")).getInt("platform_id"));
					request.put("platform_name", ((JSONObject) channels.get("channel_0")).getString("name"));
					request.put("tprice", ((JSONObject) channels.get("channel_0")).getLong("tprice"));

					request.put("transaction_state", 3);
					request.put("transaction_error_code", payResult.getRetCode());
					request.put("transaction_error_info",
							Global.EXCEPTION_MAP.get(payResult.getRetCode()));
					JedisUtil.lpush(Global.LOG_QUEUE, request.toString());

					JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
					
					return false;
					
				}

			}

			if ((type.intValue() == 2) || (type.intValue() == 3)) {
				//支付扣款
				PayRequest req = new PayRequest();
				req.setBalanceId(balanceVO.getBalanceId());
				
				System.out.println("充值/退款:"+request.getString("transaction_code")+"："+price);
				
				if(price>=0)
				{
					req.setMathType(1);
				}
				else
				{
					req.setMathType(2);
				}
				
				
				req.setPrice(Math.abs(price));
				
				req.setTradeOrderno(request.getString("transaction_code"));

				PayResultResponse payResult = payService.pay(req);
				PayVO payVO = payResult.getResult();

				if ("0000000".equals(payResult.getRetCode())){
					
					System.out.println(request.getString("transaction_code")+":"+payResult.getRetCode());
				
					return true;
				}else{
					System.out.println(request.getString("transaction_code")+":"+payResult.getRetCode());
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			System.out.println(request.getString("transaction_code")+":充值接口调用失败");
			
			
			if(type.intValue()==1)
			{
			
			//支付失败处理
			request.put("balance",-1L);
			request.put("deduction", ((JSONObject) channels.get("channel_0")).getLong("dprice"));
			request.put("set_code", ((JSONObject) channels.get("channel_0")).getString("set_code"));
			request.put("interface_flag", ((JSONObject) channels.get("channel_0")).getInt("interface_flag"));
			request.put("platform_id", ((JSONObject) channels.get("channel_0")).getInt("platform_id"));
			request.put("platform_name", ((JSONObject) channels.get("channel_0")).getString("name"));
			request.put("tprice", ((JSONObject) channels.get("channel_0")).getLong("tprice"));

			request.put("transaction_state", 3);
			request.put("transaction_error_code", Global.EXCEPTION_IPAY_INTERFACE_ERROR);
			request.put("transaction_error_info",
					Global.EXCEPTION_MAP.get(Global.EXCEPTION_IPAY_INTERFACE_ERROR));
			JedisUtil.lpush(Global.LOG_QUEUE, request.toString());

			JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
			}
			
			return false;
			
			
		}
		
		
		return false;
		
		

	}
}