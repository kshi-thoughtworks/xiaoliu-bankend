package cn.cloudstime.core;

import cn.cloudstime.bean.BusinessUserBalanceBean;
import cn.cloudstime.global.Global;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

public class Init {
	public static void initBusinessUser() {
		
		System.out.println("加载用户信息");
		try {
			List list = Global.jdbcTemplate.queryForList(
					"select bu.`code`,bu.state,b.balance,bu.alert_balance from business_user bu,balance b where bu.id=b.business_user_id");
			
			System.out.println("加载用户信息2");
			if (list == null) {
				return;
			}
			for (int i = 0; i < list.size(); i++) {
				Global.BUSINESS_USER_STATE.put(Global.HEAD_BUSINESS_USER_STATE + ((Map) list.get(i)).get("code"),
						Integer.valueOf(Integer.parseInt(((Map) list.get(i)).get("state").toString())));

				BusinessUserBalanceBean bu = new BusinessUserBalanceBean();
				bu.setCode(((Map) list.get(i)).get("code").toString());
				bu.setBalance(Long.valueOf(Long.parseLong(((Map) list.get(i)).get("balance").toString())));
				Global.BUSINESS_USER_BALANCE.put(Global.HEAD_BUSINESS_USER_BALANCE + ((Map) list.get(i)).get("code"),
						bu);

				Global.BUSINESS_USER_ALERT_BALANCE.put(
						Global.HEAD_BUSINESS_USER_ALERT_BALANCE + ((Map) list.get(i)).get("code"),
						Long.valueOf(Long.parseLong(((Map) list.get(i)).get("alert_balance").toString())));
				System.out.println("用户信息加载完毕");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initPlatform() {
		try {
			List list = Global.jdbcTemplate
					.queryForList("select id,useable,user_code,user_key from platform where is_deleted is null");
			if (list == null) {
				return;
			}
			for (int i = 0; i < list.size(); i++) {
				JSONObject platform = new JSONObject();

				platform.put("id", Integer.parseInt(((Map) list.get(i)).get("id").toString()));
				platform.put("useable", Integer.parseInt(((Map) list.get(i)).get("useable").toString()));
				platform.put("user_code", ((Map) list.get(i)).get("user_code").toString());
				platform.put("user_key", ((Map) list.get(i)).get("user_key").toString());

				Global.PLATFORM_INFO.put(
						Global.HEAD_PLATFORM_INFO + Integer.parseInt(((Map) list.get(i)).get("id").toString()),
						platform);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initNumberRule() {
	}
}