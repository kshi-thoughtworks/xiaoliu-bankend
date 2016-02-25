package cn.cloudstime.bean;

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
		((Long) Global.BUSINESS_USER_ALERT_BALANCE.get(Global.HEAD_BUSINESS_USER_ALERT_BALANCE + this.code))
				.longValue();
		this.balance.longValue();

		if (type.intValue() == 1) {
			if (this.balance.longValue() >= price.longValue()) {
				this.balance = Long.valueOf(this.balance.longValue() - price.longValue());
				return this.balance;
			}

			return Long.valueOf(-1L);
		}

		if ((type.intValue() == 2) || (type.intValue() == 3)) {
			System.out.println("充值： " + price);

			this.balance = Long.valueOf(this.balance.longValue() + price.longValue());

			return this.balance;
		}

		return Long.valueOf(-1L);
	}
}