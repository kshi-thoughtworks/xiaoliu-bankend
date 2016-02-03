package cn.cloudstime.core.thread;

import org.json.JSONObject;

import com.cloudstime.dao.TransactionDao;

import cn.cloudstime.global.Global;
import cn.cloudstime.main.ECUtils;
import cn.cloudstime.util.JedisUtil;
import ecinterface.adc.NGEC;

public class LogThread {

	public void run()
	{
		for(int i = 0; i < Global.lOG_THREAD_COUNT; ++i) {  
            Thread thread = new Thread() {  
                public void run() {  
                	while(true)
                	{
                		while(true)
                    	{
                    		String input="";
                         try {
                        	input=JedisUtil.rpop(Global.LOG_QUEUE);
    					 } catch (Exception e) {
    						//e.printStackTrace();
    						continue;
    					}
                    	 
                    	 if(input==null||input=="")
                    	 {
                    		try {
    								Thread.sleep(Global.lOG_THREAD_SLEEPTIME);
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

                    		 try {
							
                    		 if(request.getInt("transaction_type")==1)
                    		 {
                    			 
							
	                    		 if(request.getInt("transaction_state")==1)
	                    		 {
	                    			 TransactionDao dao=new TransactionDao();
	                    			 dao.transactionLogforState_D(request);
	
	                    		 }
	                    		 else if(request.getInt("transaction_state")==2)
	                    		 {
	                    			 
	                    			 TransactionDao dao=new TransactionDao();
	                    			 dao.transactionLogforState_F(request);
	                    		 }
	                    		 else if(request.getInt("transaction_state")==3)
	                    		 {
	                    			 TransactionDao dao=new TransactionDao();
	                    			 dao.transactionLogforState_T(request);
	                    		 }
                    		 }
                    		 else if(request.getInt("transaction_type")==2)
                    		 {
                    			 Global.jdbcTemplate.execute("update recharge_log set recharge_flow_number='"+"流水号"+"' where id="+request.getInt("recharge_log_id"));
                    		 }
                    		 
                    		 } catch (Exception e) {
 								e.printStackTrace();
 								System.out.println(request);
 								//把请求送回LOG队列
 								//JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
 							}
                    		 
                    		 
                    	 }
                    	 }
                		
                		
                	}
                	
                }  
            };  
            thread.start();  
        } 
	}
}
