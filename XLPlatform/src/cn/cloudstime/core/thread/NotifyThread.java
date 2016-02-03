package cn.cloudstime.core.thread;

import org.json.JSONObject;

import cn.cloudstime.core.OutputManager;
import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;

public class NotifyThread {
	

	public void run()
	{
		for(int i = 0; i < Global.NOTIFY_THREAD_COUNT; ++i) {  
            Thread thread = new Thread() {  
                public void run() {  
                	while(true)
                	{
                		while(true)
                    	{
                    		String input="";
                         try {
                        	input=JedisUtil.rpop(Global.NOTIFY_QUEUE);
    					 } catch (Exception e) {
    						e.printStackTrace();
    						continue;
    					}
                    	 
                    	 if(input==null||input=="")
                    	 {
                    		try {
    								Thread.sleep(Global.NOTIFY_THREAD_SLEEPTIME);
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
                    		 JSONObject result=new JSONObject(input);
                    		 
                    		 System.out.println(result);
                    		 JSONObject request=new JSONObject(JedisUtil.get(Global.HEAD_NOTIFY_INFO+result.getString("transaction_code")));
                    		 
                    		 System.out.println(request);
                    		 JedisUtil.del(Global.HEAD_NOTIFY_INFO+result.getString("transaction_code"));
                    		 
                    		 JSONObject channels=request.getJSONObject("channels");
                    		 
                    		 if(result.getInt("state")==0)
                    		 {
                    			
                    			//发送请求到扣费单元将消费回滚
        						 request.put("transaction_type", 3);
        						 request.put("price", ((JSONObject)channels.get("channel_0")).getLong("dprice"));
        						 JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString()); 
        						 
        						 request.put("transaction_error_info", result.get("retmsg"));
        						 
        						 request.put("target_channel_index", request.getInt("target_channel_index")+1);
    							 //通道递归
        						 
        						 request.put("transaction_type", 1);
        						 OutputManager out=new  OutputManager();
    							 out.output(request);
                    			 
                    		 }
                    		 else
                    		 {
                    			 JSONObject channel=channels.getJSONObject("channel_"+request.getInt("target_channel_index"));
                    			 //记录日志成功交易
            					 request.put("transaction_state", 2);
            					 request.put("platform_name", channel.getString("name"));
            					 
            					 JedisUtil.lpush(Global.LOG_QUEUE, request.toString());
            					 //发送到response
            					 JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
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
