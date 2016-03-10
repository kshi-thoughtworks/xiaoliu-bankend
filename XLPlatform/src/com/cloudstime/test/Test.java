package com.cloudstime.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;



import cn.cloudstime.util.MD5Utils;

public class Test {
	
	public static void main(String[] args) {
		
		
//		HttpMethodParams params=new HttpMethodParams();
//		params.setParameter("username", "khcs");
//		params.setParameter("password", MD5Utils.MD5("khcs"+MD5Utils.MD5("19n4ypgs")));
//		params.setParameter("mobile", "18210328639");
//		params.setParameter("content", "短信测试");
		
//		 JSONObject obj=new JSONObject();
//		 obj.put("username", "khcs");
//		 obj.put("password", MD5Utils.MD5("khcs"+MD5Utils.MD5("19n4ypgs")));
//		 obj.put("mobile", "18210328639");
//		 obj.put("content", "短信测试");
//		 obj.put("dstime", "");
//		 obj.put("ext", "");
//		 obj.put("msgid ", "");
//		 obj.put("msgfmt ", "");
//		 
//		 
//		 
//		 try {
//			 
//   		 
//  		HttpClient client = new HttpClient();
//		JSONObject json = new JSONObject();
//		PostMethod post = new PostMethod();
//		post.setURI(new URI("http://www.jc-chn.cn/smsSend.do"));
//		post.setRequestHeader("Content-type", "application/json;charset=UTF-8");
//		//post.setParams(params);
//		StringRequestEntity entity = new StringRequestEntity(obj.toString());
//		post.setRequestEntity(entity);
//		client.executeMethod(post);
//		String result = post.getResponseBodyAsString();
//		System.out.println(result);
			
//			 GetMethod get=new GetMethod();
//			 get.setURI(new URI("http://www.jc-chn.cn/smsSend.do?username=khcs&password="+MD5Utils.MD5("khcs"+MD5Utils.MD5("19n4ypgs"))+"&mobile=18210328639&content=短信测试&dstime&ext=&msgid=&msgfmt="));
//
//			 client.executeMethod(get);
//			 
//			 System.out.println(get.getResponseBodyAsString());
//			
			 
//			 String content = "小流之家，您好，您在小流之家的余额已不足3000元。"; 
//				StringBuffer sb = new StringBuffer("http://www.jc-chn.cn/smsSend.do?");
//				sb.append("username=khcs");
//				sb.append("&password="+MD5Utils.MD5("khcs"+MD5Utils.MD5("19n4ypgs")));
//				sb.append("&mobile=18210328639");
//				sb.append("&content="+URLEncoder.encode(content,"utf-8"));
//				sb.append("&dstime=");
//				URL url = new URL(sb.toString());
//				URLConnection conn = url.openConnection();
//				conn.setRequestProperty("accept", "*/*");
//				conn.setRequestProperty("connection", "Keep-Alive");
//				conn.connect(); 
//				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//				String line = "";
//				String result = "";
//				while ((line=in.readLine())!=null){
//					result += line + "\n";
//				}
//			in.close();
//			System.out.println(result);
//
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		testyzx();
	}
	
	
	public static void testyzx()
	{
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
			
			
			Date d=new Date();
			String time=sdf.format(d);
			
			
			HttpClient client = new HttpClient();
			JSONObject obj = new JSONObject();
			
			obj.put("appId", "623f4256803640f3ad117e17a59c6788");
			obj.put("templateId", "21303");
			obj.put("to", "18210328639");
			obj.put("param", "2016-03-09 15:50:00,1000");
			
			
			
			JSONObject o=new JSONObject();
			o.put("templateSMS", obj);
			
			
			PostMethod post = new PostMethod();
			
			String url="https://api.ucpaas.com/2014-06-30/Accounts/5bec8cde0e12b388312bfbdb7f75887f/Messages/templateSMS?sig="+MD5Utils.MD5("5bec8cde0e12b388312bfbdb7f75887f"+"60cc5c3d77b097cd8e9f3adfbd4ed290"+time).toUpperCase();
			
			System.out.println(url);
			
			post.setURI(new URI(url));
			post.setRequestHeader("Accept", "application/json");
			post.setRequestHeader("Content-type", "application/json;charset=UTF-8");
			post.setRequestHeader("Authorization", new String(Base64.encode(("5bec8cde0e12b388312bfbdb7f75887f"+":"+time).getBytes())));
			
			System.out.println(new String(Base64.encode(("5bec8cde0e12b388312bfbdb7f75887f"+":"+time).getBytes())));
			
			
			
			StringRequestEntity entity = new StringRequestEntity(o.toString());
			post.setRequestEntity(entity);
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

}
