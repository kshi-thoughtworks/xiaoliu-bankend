package cn.cloudstime.bean;

import cn.cloudstime.global.Global;

public class BusinessUserBalanceBean {
	
	private String code;
	
	private Long balance;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}
	
	/**
	 * 充流量扣费/商户充值
	 * type:1 扣费  type:2 充值  type:3 回滚
	 * @param type
	 */
	public synchronized Long transaction(Integer type,Long price)
	{
		
		if(Global.BUSINESS_USER_ALERT_BALANCE.get(Global.HEAD_BUSINESS_USER_ALERT_BALANCE+this.code)<=this.balance)
		{
			//未完成
			//把预警信息发送到日志队列
		}
		
		
		if(type==1)
		{
			
			if(this.balance>=price)
			{
				this.balance=this.balance-price;
				return this.balance;
			}
			else
			{
				return -1L;
			}
		}
		else if(type==2||type==3)
		{
			System.out.println("充值： "+price);
					
			this.balance=this.balance+price;
			//把记录发送到日志队列
			//未完成
			return this.balance;
		}
		else
		{
			return -1L;
		}
		
		
		
		
		
	}
	
	

}
