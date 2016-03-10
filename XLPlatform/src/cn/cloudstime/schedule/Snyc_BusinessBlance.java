package cn.cloudstime.schedule;

import com.cloudstime.dao.TransactionDao;

import cn.cloudstime.bean.BusinessUserBalanceBean;
import cn.cloudstime.global.Global;

public class Snyc_BusinessBlance {
	
	public void syncBalance()
	{
		
		for (String key : Global.BUSINESS_USER_BALANCE.keySet()) {
			  BusinessUserBalanceBean bubb=Global.BUSINESS_USER_BALANCE.get(key);
			  
			
			  TransactionDao dao=new TransactionDao();
			  dao.updateBalance(bubb.getBalance(), bubb.getCode());
			  }

	}

}
