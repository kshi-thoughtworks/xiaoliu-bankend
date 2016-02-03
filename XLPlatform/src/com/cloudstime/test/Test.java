package com.cloudstime.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;

import com.cloudstime.dao.TransactionDao;

import cn.cloudstime.global.Global;
import cn.cloudstime.main.FJUtils;
import cn.cloudstime.util.MD5Utils;

public class Test {
	
	public static void main(String[] args) {
		//JSONObject j=new JSONObject();
		//System.out.println(FJUtils.order("cztest01", "1", "cztest01", "123456781", "18210328639", "1", 20));
	
		 //���?������
		 JSONObject request=new JSONObject();
		 
		 request.put("phone", "18210328639");
		 request.put("extendss", "0");
		 request.put("orderNo", "123123");
		 request.put("transaction_error_info", "呵呵");
		 request.put("transaction_code", "0000");
		 request.put("phone", "18210328639");
		 request.put("phone", "18210328639");
		 request.put("phone", "18210328639");
		 request.put("phone", "18210328639");
		 request.put("phone", "18210328639");
		 
		 
		 SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
		 
		 
		 
		 String business_type="MNT";
		 String card_no=request.getString("phone");
		 String extendss=request.has("extendss")?request.getString("extendss"):"";
		 String notify_id=UUID.randomUUID().toString();
		 String notify_time=sdf.format(new Date());
		 String notify_type="trade_status_sync";
		 String order_no=request.getString("orderNo");
		 String status_desc=request.has("transaction_error_info")?request.getString("transaction_error_info"):"success";
		 String trade_no=request.getString("transaction_code");
		 Integer trade_status=request.has("transaction_error_code")?request.getInt("transaction_error_code"):0;                   		
		 TransactionDao dao=new TransactionDao();
		 String key="AAAAAAAA";
		 
		 
		 
		 //ǩ��ǰ
		 String str="business_type="+business_type+"&card_no="+card_no+"&extendss="+
		 extendss+"&notify_id="+notify_id+"&notify_time="+notify_time+
		 "&notify_type="+notify_type+"&order_no="+order_no+"&status_desc="+status_desc+
		 "&trade_no="+trade_no+"&trade_status"+trade_status+"&key="+MD5Utils.MD5(key);
		 String sign = MD5Utils.MD5(str);
		 
		 JSONObject obj=new JSONObject();
		 obj.put("business_type", business_type);
		 obj.put("card_no", card_no);
		 obj.put("extendss", extendss);
		 obj.put("notify_id", notify_id);
		 obj.put("notify_time", notify_time);
		 obj.put("notify_type", notify_type);
		 obj.put("order_no", order_no);
		 obj.put("status_desc", status_desc);
		 obj.put("trade_no", trade_no);
		 obj.put("trade_status", trade_status);
		 obj.put("sign", sign);
		 obj.put("sign_type", "MD5");
		 
		 try {
			 
   		 
   		HttpClient client = new HttpClient();
		JSONObject json = new JSONObject();
		PostMethod post = new PostMethod();
		post.setURI(new URI("http://101.201.152.32:8080/cloudstime/notify"));
		post.setRequestHeader("Content-type", "application/json;charset=UTF-8");
		StringRequestEntity entity = new StringRequestEntity(obj.toString());
		post.setRequestEntity(entity);
		client.executeMethod(post);
		String result = post.getResponseBodyAsString();
		System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
