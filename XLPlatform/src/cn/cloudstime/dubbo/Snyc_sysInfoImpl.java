package cn.cloudstime.dubbo;

import java.util.Map;

import org.json.JSONObject;

import cn.cloudstime.bean.BusinessUserBalanceBean;
import cn.cloudstime.global.Global;
import cn.cloudstime.service.Snyc_sysInfo;

public class Snyc_sysInfoImpl implements Snyc_sysInfo {

	@Override
	public boolean snycBusinessUser(Map<String, Object> bussinessUser) {
		
		try {
			JSONObject j_bussinessUser=new JSONObject(bussinessUser);
			if(Global.BUSINESS_USER_STATE.containsKey(Global.HEAD_BUSINESS_USER_STATE+j_bussinessUser.getString("business_user_code")))
			{
				Global.BUSINESS_USER_STATE.put(Global.HEAD_BUSINESS_USER_STATE+j_bussinessUser.getString("business_user_code"), j_bussinessUser.getInt("state"));
				Global.BUSINESS_USER_ALERT_BALANCE.put(Global.HEAD_BUSINESS_USER_ALERT_BALANCE+j_bussinessUser.getString("business_user_code"), j_bussinessUser.getLong("alert_balance"));
			}
			else
			{
				Global.BUSINESS_USER_STATE.put(Global.HEAD_BUSINESS_USER_STATE+j_bussinessUser.getString("business_user_code"), j_bussinessUser.getInt("state"));
				Global.BUSINESS_USER_ALERT_BALANCE.put(Global.HEAD_BUSINESS_USER_ALERT_BALANCE+j_bussinessUser.getString("business_user_code"), j_bussinessUser.getLong("alert_balance"));
				BusinessUserBalanceBean bubb=new BusinessUserBalanceBean();
				bubb.setCode(j_bussinessUser.getString("business_user_code"));
				bubb.setBalance(j_bussinessUser.getLong("balance"));
				Global.BUSINESS_USER_BALANCE.put(j_bussinessUser.getString("business_user_code"), bubb);
			}
			System.out.println(j_bussinessUser);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
	}

	@Override
	public boolean snycPlatform(Map<String, Object> platform) {

		
		try {
			JSONObject j_platform=new JSONObject(platform);
			Global.PLATFORM_INFO.put(Global.HEAD_PLATFORM_INFO+j_platform.getInt("id"), j_platform);
			System.out.println(j_platform);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
	}

}
