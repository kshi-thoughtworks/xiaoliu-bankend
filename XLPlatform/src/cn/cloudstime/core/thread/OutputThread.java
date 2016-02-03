package cn.cloudstime.core.thread;

import org.json.JSONObject;

import cn.cloudstime.core.OutputManager;
import cn.cloudstime.global.Global;
import cn.cloudstime.main.ECUtils;
import cn.cloudstime.util.JedisUtil;
import ecinterface.adc.NGEC;


public class OutputThread {
	
	public void run()
	{
		for(int i = 0; i < Global.OUTPUT_THREAD_COUNT; ++i) {  
            Thread thread = new Thread() {  
                public void run() {  
                	while(true)
                	{
                		while(true)
                    	{
                    		String input="";
                         try {
                        	input=JedisUtil.rpop(Global.OUPPUT_QUEUE);
    					 } catch (Exception e) {
    						e.printStackTrace();
    						continue;
    					}
                    	 
                    	 if(input==null||input=="")
                    	 {
                    		try {
    								Thread.sleep(Global.OUTPUT_THREAD_SLEEPTIME);
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
                    		 OutputManager outputManager=new OutputManager();
                    		 outputManager.output(request);
                    	 }
                    	 }
                		
                		
                	}
                	
                }  
            };  
            thread.start();  
        } 
	}

}
