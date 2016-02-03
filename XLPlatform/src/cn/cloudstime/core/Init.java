package cn.cloudstime.core;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cloudstime.dao.TransactionDao;

import cn.cloudstime.bean.BusinessUserBalanceBean;
import cn.cloudstime.global.Global;

public class Init {
	
	
	
	/**
	 * init BusinessUser
	 * 1、state
	 * 2、money
	 */
	public static void initBusinessUser()
	{
		try {
		    List<Map<String,Object>> list= Global.jdbcTemplate.queryForList("select bu.`code`,bu.state,b.balance,bu.alert_balance from business_user bu,balance b where bu.id=b.business_user_id");
		    if(list==null)
		    {
		    	return ;
		    }
		    for(int i=0;i<list.size();i++)
		    {
		    	 //{STATE_B_CODE:STATE}
		    		Global.BUSINESS_USER_STATE.put(Global.HEAD_BUSINESS_USER_STATE+list.get(i).get("code"), Integer.parseInt(list.get(i).get("state").toString()));
		    	 //{BLANCE_B_CODE:BLANCE}
		    		BusinessUserBalanceBean bu=new BusinessUserBalanceBean();
		    		bu.setCode(list.get(i).get("code").toString());
		    		bu.setBalance(Long.parseLong(list.get(i).get("balance").toString()));
		    		Global.BUSINESS_USER_BALANCE.put(Global.HEAD_BUSINESS_USER_BALANCE+list.get(i).get("code"), bu);
		    	 //{ALERT_BALANCE_B_CODE:STATE}
		    		Global.BUSINESS_USER_ALERT_BALANCE.put(Global.HEAD_BUSINESS_USER_ALERT_BALANCE+list.get(i).get("code"),  Long.parseLong(list.get(i).get("alert_balance").toString()));
		    }
	    
		} catch (Exception e) {
			e.printStackTrace();
			//未完成
			//向错误日志队列写“INIT_BUSINESS_USER_ERROR”
		}
	 
	    
	}
	
	public static void initPlatform()
	{
		try {
			List<Map<String,Object>> list= Global.jdbcTemplate.queryForList("select id,useable,user_code,user_key from platform where is_deleted is null");
			if(list==null)
		    {
		    	return ;
		    }
		    for(int i=0;i<list.size();i++)
		    {
		    	JSONObject platform=new JSONObject();
		    	
		    	platform.put("id", Integer.parseInt(list.get(i).get("id").toString()));
		    	platform.put("useable", Integer.parseInt(list.get(i).get("useable").toString()));
		    	platform.put("user_code", list.get(i).get("user_code").toString());
		    	platform.put("user_key", list.get(i).get("user_key").toString());
		    	
		    	 Global.PLATFORM_INFO.put(Global.HEAD_PLATFORM_INFO+Integer.parseInt(list.get(i).get("id").toString()), platform);
		    }
		} catch (Exception e) {
			e.printStackTrace();
			//未完成
			//向错误日志队列写“INIT_PLATFORM_STATE_ERROR”
		}
	}
	
	//加载流水号规则
	public static void initNumberRule()
	{
		//未完成
	}
	  

}
