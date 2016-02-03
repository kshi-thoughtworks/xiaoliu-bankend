package cn.cloudstime.core.thread;


import org.json.JSONObject;

import cn.cloudstime.bean.BusinessUserBalanceBean;
import cn.cloudstime.core.ChargeBusinessUser;
import cn.cloudstime.core.ChargeFlowManager;
import cn.cloudstime.core.ChargeFlowRollbackManager;
import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;

public class TransactionThread {
	
	
	public void run()
	{
		
		System.out.println("启动交易线程");
		for(int i=0;i<Global.TRANSACTION_THREAD_COUNT;i++)
		{
			Thread thread = new Thread() {  
                public void run() { 
                	while(true)
                	{
                		String input="";
                     try {
                    	input=JedisUtil.rpop(Global.REQUEST_QUEUE);
					 } catch (Exception e) {
						e.printStackTrace();
						continue;
					}
                	
                	 if(input==null||input=="")
                	 {
                		try {
                			 
								Thread.sleep(Global.TRANSACTION_THREAD_SLEEPTIME);
								continue;
							} catch (Exception e) {
								e.printStackTrace();
								//未完成
								//向错误日志队列写“TRANSACTION_TREAD_SLEEP_ERROR”
								continue;
							}
                	 }
                	 else
                	 {
                		 JSONObject request=new JSONObject(input);
                		 //未完成
                		 //核对请求的类型transaction_type，是充值（2）还是充流量（1）还是回滚（3）
                		 
                		 if(request.getInt("transaction_type")==1)
                		 {
                			 ChargeFlowManager chargeFlow=new ChargeFlowManager();
                			 if(!chargeFlow.ChrageFlow(request))
                			 {
                				 continue;
                			 }
                		 }
                		 else if(request.getInt("transaction_type")==2)
                		 {
                			 ChargeBusinessUser chargeBu=new ChargeBusinessUser();
                			 if(!chargeBu.charge(request))
                			 {
                				 continue;
                			 }
                		 }
                		 else if(request.getInt("transaction_type")==3)
                		 {
                			 ChargeFlowRollbackManager chargeRb=new ChargeFlowRollbackManager();
                			 if(!chargeRb.rollback(request))
                			 {
                				 continue;
                			 }
                		 }else
                		 {
                			 System.out.println("请求的交易类型有错误！");
                			 continue;
                		 }
                			
                		 
                	 }
                	 
                	
                	
                	}
                	
                }  
            };  
            thread.start();  
		}
	}

}
