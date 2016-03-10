package cn.cloudstime.bean;

import cn.cloudstime.core.SendSMS;
import cn.cloudstime.global.Global;
import java.io.PrintStream;
import java.util.Map;

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

	public synchronized Long transaction(Integer type, Long price) {
		
		Long alert=((Long) Global.BUSINESS_USER_ALERT_BALANCE.get(Global.HEAD_BUSINESS_USER_ALERT_BALANCE + this.code))
				.longValue();
		
		Long blance=this.balance.longValue();
		
		Long result=blance-alert;
		
		

		if (type.intValue() == 1) {
			if (this.balance.longValue() >= price.longValue()) {
				this.balance = Long.valueOf(this.balance.longValue() - price.longValue());
				
				//扣费前余额大于警戒，扣费后余额小于警戒,发送短信
				if(result>=0L&&this.balance<alert)
				{
					SendSMS sms=new SendSMS();
					sms.send(this.code,this.balance);
				}
				
				
				
				return this.balance;
			}

			return Long.valueOf(-1L);
		}

		if ((type.intValue() == 2) || (type.intValue() == 3)) {
			System.out.println("退款： " + price);

			this.balance = Long.valueOf(this.balance.longValue() + price.longValue());
			
			return this.balance;
		}

		return Long.valueOf(-1L);
	}
}