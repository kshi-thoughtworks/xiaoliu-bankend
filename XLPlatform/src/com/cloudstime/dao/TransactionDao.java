package com.cloudstime.dao;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;

public class TransactionDao {
	
	
	
	/**
	 * 匹配通道
	 * @param request
	 * @return
	 */
	public JSONObject findChannel(JSONObject request)
	{

		List<Map<String,Object>> list= Global.jdbcTemplate.queryForList("select t1.code as set_code, t2.platform_id, t3.name,t3.interface_flag,t5.set_id,t5.discount as ddiscount,t2.discount as tdiscount, CONVERT(t1.price*t5.discount,SIGNED) as dprice,CONVERT(t1.price*t2.discount,SIGNED) as tprice  from sets t1,tp_set t2,platform t3,area t4,ep_set t5 where t1.id = t2.set_id and t2.platform_id = t3.id"+ 
				" and t1.recharge_area = t4.code "+
				" and t1.operators = '"+String.valueOf(request.get("operator_code"))+"' and t1.flow_type="+String.valueOf(request.get("flowType"))+" and t1.expiry_date="+String.valueOf(request.get("expiryDate"))+" and t1.enable_rule="+String.valueOf(request.get("enableRule"))+" and t1.business_type="+String.valueOf(request.get("businessType"))+" and t1.flow_value = '"+String.valueOf(request.get("flowValue"))+"' and (t4.name like '%"+String.valueOf(request.get("area"))+"%' or t4.code = '1000') and t3.useable=1"+
				" and t1.id=t5.set_id and t5.enterprise_id=(select e.id from enterprise e,business_user b where e.code=b.enterprise_code and b.code='"+String.valueOf(request.get("business_user_code"))+"')"+
				" order by t1.price * t2.discount,t1.recharge_area desc,t1.id");
		
		JSONObject channels=new JSONObject();
		
		for(int i=0;i<list.size();i++)
		{
			JSONObject channel=new JSONObject();
			
			channel.put("platform_id", Integer.parseInt(list.get(i).get("platform_id").toString()));
			channel.put("name", list.get(i).get("name").toString());
			channel.put("set_code", list.get(i).get("set_code").toString());
			channel.put("interface_flag", Integer.valueOf(list.get(i).get("interface_flag").toString()));
			channel.put("set_id", Integer.valueOf(list.get(i).get("set_id").toString()));
			channel.put("ddiscount", Float.valueOf(list.get(i).get("ddiscount").toString()));
			channel.put("tdiscount", Float.valueOf(list.get(i).get("tdiscount").toString()));
			channel.put("dprice", Long.parseLong(list.get(i).get("dprice").toString()));
			channel.put("tprice", Long.parseLong(list.get(i).get("tprice").toString()));
		    
			channels.put("channel_"+i, channel);
		}
		
		return channels;
	}
	
	
	/**
	 * 初始化编号规则
	 * @return
	 */
	public JSONObject loadNumberRule()
	{
		List<Map<String,Object>> list= Global.jdbcTemplate.queryForList("select * from number_rule");
		
		JSONObject rules=new JSONObject();
		
		for(int i=0;i<list.size();i++)
		{
			JSONObject rule=new JSONObject();
			
			rule.put("index_length", Integer.parseInt(list.get(i).get("index_length").toString()));
			rule.put("index_min", Integer.parseInt(list.get(i).get("index_min").toString()));
			rule.put("index_max", Integer.parseInt(list.get(i).get("index_max").toString()));
			rule.put("type", list.get(i).get("name").toString());
			rule.put("name", list.get(i).get("name").toString());
			rule.put("time_format", list.get(i).get("time_format").toString());
			
		    
			rules.put(list.get(i).get("name").toString(), rule);
		}
		
		return rules;
	
	}
	
	/**
	 * 充值中日志
	 * @param request
	 * @return
	 */
	public boolean transactionLogforState_D(JSONObject request)
	{
		Global.jdbcTemplate.execute("INSERT INTO transaction_log( transaction_code, bu_code,"+ 
			      "deduction, balance, update_time, "+
			      "operators, set_code, operators_area, "+
			      "recharge_area, business_type, flow_type, "+
			      "flow_value, expiry_date, add_able, "+
			      "enable_rule, price, phone_number, "+
			      "status, return_msg,bu_transaction_code,platform_name) " +
		 		" VALUES (" +
		 		" '"+request.getString("transaction_code")+"'," +
		 		" '"+request.getString("business_user_code")+"'," +
		 		" null," +
		 		" null," +
		 		" SYSDATE()," +
		 		" null," +
		 		" null," +
		 		" null," +
		 		" null," +
		 		" "+Integer.valueOf(request.getString("businessType"))+"," +
		 		" "+Integer.valueOf(request.getString("flowType"))+"," +
		 		" "+Integer.valueOf(request.getString("flowValue"))+"," +
		 		" "+Integer.valueOf(request.getString("expiryDate"))+"," +
		 		" 1," +
		 		" "+Integer.valueOf(request.getString("enableRule"))+"," +
		 		" null," +
		 		" '"+request.getString("phone")+"'," +
		 		" -1," +
		 		" '充值中'," +
		 		" '"+request.getString("orderNo")+"'," +
				" '')");
		
		return true;
	}
	
	/**
	 * 充值失败日志
	 * @param request
	 * @return
	 */
	public boolean transactionLogforState_T(JSONObject request)
	{
		
		System.err.println(request);
		
		try {
			List<Map<String,Object>> list= Global.jdbcTemplate.queryForList("" +
					"select * from sets where code='"+request.getString("set_code")+"'");
			
			
			 if("6004".equals(request.getString("transaction_error_code")))
			{
				
				
				Global.jdbcTemplate.execute("update transaction_log set " +
						"transaction_code ='"+request.getString("transaction_code")+"'," +
						"bu_code ='"+request.getString("business_user_code")+"'," +
						"deduction ="+request.getLong("deduction")+"," +
						"balance = "+request.getLong("balance")+"," +
						//"update_time = SYSDATE()," +
						"operators  ='"+request.getString("operator_code")+"'," +
						"set_code = '"+request.getString("set_code")+"'," +
						"operators_area = '"+list.get(0).get("area_code").toString()+"'," +
						"recharge_area = '"+list.get(0).get("recharge_area").toString()+"'," +
						"business_type = "+Integer.valueOf(request.getString("businessType"))+"," +
						"flow_type = "+Integer.valueOf(request.getString("flowType"))+"," +
						"flow_value ="+Integer.valueOf(request.getString("flowValue"))+"," +
						"expiry_date = "+Integer.valueOf(request.getString("expiryDate"))+"," +
						"add_able = "+1+"," +
						"enable_rule = "+Integer.valueOf(request.getString("enableRule"))+"," +
						"price = "+request.getLong("tprice")+"," +
						"phone_number = '"+request.getString("phone")+"'," +
						"status =0," +
						"return_msg ='"+request.getString("transaction_error_info")+"'," +
						"bu_transaction_code ='"+request.getString("orderNo")+"'," +
						"platform_name ='"+request.getString("platform_name")+"'" +
						" where transaction_code='"+request.getString("transaction_code")+"'");
				
			}
			 else
			 {
				 Global.jdbcTemplate.execute("update transaction_log set " +
							"transaction_code ='"+request.getString("transaction_code")+"'," +
							"bu_code ='"+request.getString("business_user_code")+"'," +
							"deduction ="+request.getLong("deduction")+"," +
							"balance = "+request.getLong("balance")+"," +
							//"update_time = SYSDATE()," +
							"operators  ='"+request.getString("operator_code")+"'," +
							"set_code = '"+request.getString("set_code")+"'," +
							"operators_area = '"+list.get(0).get("area_code").toString()+"'," +
							"recharge_area = '"+list.get(0).get("recharge_area").toString()+"'," +
							"business_type = "+Integer.valueOf(request.getString("businessType"))+"," +
							"flow_type = "+Integer.valueOf(request.getString("flowType"))+"," +
							"flow_value ="+Integer.valueOf(request.getString("flowValue"))+"," +
							"expiry_date = "+Integer.valueOf(request.getString("expiryDate"))+"," +
							"add_able = "+1+"," +
							"enable_rule = "+Integer.valueOf(request.getString("enableRule"))+"," +
							"price = "+request.getLong("tprice")+"," +
							"phone_number = '"+request.getString("phone")+"'," +
							"status =0," +
							"return_msg ='"+request.getString("transaction_error_info")+"'," +
							"bu_transaction_code ='"+request.getString("orderNo")+"'" +
							//"platform_name ='"+request.getString("platform_name")+"'" +
							" where transaction_code='"+request.getString("transaction_code")+"'");
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		return true;
	}
	
	/**
	 * 充值成功日志
	 * @param request
	 * @return
	 */
	public boolean transactionLogforState_F(JSONObject request)
	{
		List<Map<String,Object>> list= Global.jdbcTemplate.queryForList("" +
				"select * from sets where code='"+request.getString("set_code")+"'");
		
		if(list!=null)
		{
			
			Global.jdbcTemplate.execute("update transaction_log set " +
					"transaction_code ='"+request.getString("transaction_code")+"'," +
					"bu_code ='"+request.getString("business_user_code")+"'," +
					"deduction ="+request.getLong("deduction")+"," +
					"balance = "+request.getLong("balance")+"," +
					//"update_time = SYSDATE()," +
					"operators  ='"+request.getString("operator_code")+"'," +
					"set_code = '"+request.getString("set_code")+"'," +
					"operators_area = '"+list.get(0).get("area_code").toString()+"'," +
					"recharge_area = '"+list.get(0).get("recharge_area").toString()+"'," +
					"business_type = "+Integer.valueOf(request.getString("businessType"))+"," +
					"flow_type = "+Integer.valueOf(request.getString("flowType"))+"," +
					"flow_value ="+Integer.valueOf(request.getString("flowValue"))+"," +
					"expiry_date = "+Integer.valueOf(request.getString("expiryDate"))+"," +
					"add_able = "+1+"," +
					"enable_rule = "+Integer.valueOf(request.getString("enableRule"))+"," +
					"price = "+request.getLong("tprice")+"," +
					"phone_number = '"+request.getString("phone")+"'," +
					"status =1," +
					"return_msg ='充值成功'," +
					"bu_transaction_code ='"+request.getString("orderNo")+"'," +
					"platform_name ='"+request.getString("platform_name")+"'" +
					" where transaction_code='"+request.getString("transaction_code")+"'");
			
//			Global.jdbcTemplate.execute("INSERT INTO transaction_log( transaction_code, bu_code,"+ 
//				      "deduction, balance, update_time, "+
//				      "operators, set_code, operators_area, "+
//				      "recharge_area, business_type, flow_type, "+
//				      "flow_value, expiry_date, add_able, "+
//				      "enable_rule, price, phone_number, "+
//				      "status, return_msg,bu_transaction_code,platform_name) " +
//			 		" VALUES (" +
//			 		" '"+request.getString("transaction_code")+"'," +
//			 		" '"+request.getString("business_user_code")+"'," +
//			 		" "+request.getLong("deduction")+"," +
//			 		" "+request.getLong("balance")+"," +
//			 		" SYSDATE()," +
//			 		" '"+request.getString("operator_code")+"'," +
//			 		" '"+request.getString("set_code")+"'," +
//			 		" '"+list.get(0).get("area_code").toString()+"'," +
//			 		" '"+list.get(0).get("recharge_area").toString()+"'," +
//			 		" "+Integer.valueOf(list.get(0).get("business_type").toString())+"," +
//			 		" "+Integer.valueOf(list.get(0).get("flow_type").toString())+"," +
//			 		" "+Integer.valueOf(list.get(0).get("flow_value").toString())+"," +
//			 		" "+Integer.valueOf(list.get(0).get("expiry_date").toString())+"," +
//			 		" "+Integer.valueOf(list.get(0).get("add_able").toString())+"," +
//			 		" "+Integer.valueOf(list.get(0).get("enable_rule").toString())+"," +
//			 		" "+request.getLong("tprice")+"," +
//			 		" "+request.getString("phone")+"," +
//			 		" 1," +
//			 		" '充值成功'," +
//			 		" '"+request.getString("orderNo")+"'," +
//					" '"+request.getString("platform_name")+"')");
			return true;
		}
		
		return false;
		
		
		
		
		
		
		
	}
	
	
	/**
	 * 亏损日志
	 * @param request
	 * @return
	 */
	public boolean transactionLogforLoss(JSONObject request)
	{
		List<Map<String,Object>> list= Global.jdbcTemplate.queryForList("" +
				"select * from sets where code='"+request.getString("set_code")+"'");
		
		if(list!=null&&list.size()==1)
		{
		Global.jdbcTemplate.execute("INSERT INTO transaction_log( transaction_code, bu_code,"+ 
			      "deduction, balance, update_time, "+
			      "operators, set_code, operators_area, "+
			      "recharge_area, business_type, flow_type, "+
			      "flow_value, expiry_date, add_able, "+
			      "enable_rule, price, phone_number, "+
			      "status, return_msg,bu_transaction_code,platform_name) " +
		 		" VALUES (" +
		 		" '"+request.getString("transaction_code")+"'," +
		 		" '"+request.getString("business_user_code")+"'," +
		 		" "+request.getLong("deduction")+"," +
		 		" "+request.getLong("balance")+"," +
		 		" SYSDATE()," +
		 		" '"+request.getString("operator_code")+"'," +
		 		" '"+request.getString("set_code")+"'," +
		 		" '"+list.get(0).get("area_code").toString()+"'," +
		 		" '"+list.get(0).get("recharge_area").toString()+"'," +
		 		" "+Integer.valueOf(request.getString("businessType"))+"," +
		 		" "+Integer.valueOf(request.getString("flowType"))+"," +
		 		" "+Integer.valueOf(request.getString("flowValue"))+"," +
		 		" "+Integer.valueOf(request.getString("expiryDate"))+"," +
		 		" "+1+"," +
		 		" "+Integer.valueOf(request.getString("enableRule"))+"," +
		 		" "+request.getLong("tprice")+"," +
		 		" '"+request.getString("phone")+"'," +
		 		" 1," +
		 		" '充值成功'," +
		 		" '"+request.getString("orderNo")+"'," +
				" '"+request.getString("platform_name")+"')");
		
		}
		return true;
	}
	
	
	/**
	 * 通道失败
	 * @param request
	 * @return
	 */
	public boolean channelFailLog(JSONObject request)
	{
		
		Global.jdbcTemplate.execute("insert into channel_log(result_code,result_message,order_id,channel,error_time) values" +
				"('"+request.getString("transaction_error_code")+"','"+request.getString("transaction_error_info")+"','"+request.getString("transaction_code")+"','"+request.getString("platform_name")+"',SYSDATE())");
		
		return true;
	}
	
	
	public void updateBalance(Long balance,String business_user_code)
	{
		Global.jdbcTemplate.execute("" +
				"update balance set balance="+balance+","+
				"update_time=SYSDATE()" +
				" where business_user_id=(select id from business_user where state=1 and code='"+business_user_code+"')");
	}
	
	
	public boolean checkBusinessOrderNo(JSONObject request)
	{
		try {
			Global.jdbcTemplate.execute("insert into check_orderno values('"+request.getString("business_user_code")+"','"+request.getString("orderNo")+"')");
			
		} catch (Exception e) {
			
			return false;
		}
		
		return true;
		
	}
	
	
	
	public JSONObject findOwnershipByLocal(JSONObject request)
	{
		
		
		String phonearea=request.getString("phone").substring(0, 7);
		
//		List<Map<String,Object>> list= Global.jdbcTemplate.queryForList("" +
//				"select province,carrier from phone_belong_third where  phone='"+phonearea+"'");
//		
//		JSONObject obj=null;
//		if(list!=null&&list.size()>0)
//		{
//			obj=new JSONObject();
//			obj.put("province", list.get(0).get("province"));
//			obj.put("supplier", list.get(0).get("carrier"));
//		}
		
		
		
		JSONObject result=new JSONObject(JedisUtil.get(phonearea));
		
		
		JSONObject obj=new JSONObject();
		obj.put("province", result.get("province"));
		obj.put("supplier", result.get("carrier"));
		
		
		
		
		return obj;
	}
	
	
	public JSONObject findBusinessKey(JSONObject request)
	{
		
		
		List<Map<String,Object>> list= Global.jdbcTemplate.queryForList("" +
				"select user_key from business_user where  code='"+request.getString("business_user_code")+"'");
		
		JSONObject obj=null;
		if(list!=null||list.size()>0)
		{
			obj=new JSONObject();
			obj.put("user_key", list.get(0).get("user_key"));
		}
		
		return obj;
	}
	
	
	public String findPhoneByBuCode(String bucode)
	{
		List<Map<String,Object>> list= Global.jdbcTemplate.queryForList("" +
				"select sms_number from business_user where  code='"+bucode+"'");
		
		if(list!=null||list.size()>0)
		{
			return String.valueOf(list.get(0).get("sms_number"));
		}
		else
		{
			return "";
		}
		
	}
	

}
