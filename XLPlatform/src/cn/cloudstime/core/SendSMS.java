package cn.cloudstime.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;

import com.cloudstime.dao.TransactionDao;

import cn.cloudstime.util.MD5Utils;

public class SendSMS {
	

	private TransactionDao dao=new TransactionDao();
	
	
	
	public boolean send(String bu_code,Long balance)
	{
		
		if(smsPlatform_ml(bu_code,balance))
		{
			return true;
		}
		else
		{
			return smsPlatform_yzx(bu_code,balance);
		}
		
		
	}
	
	
	public boolean smsPlatform_ml(String bu_code,Long balance)
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		
		String date=sdf.format(new Date());
		
		String bl=getNumber(balance/10000);
		
		String phone=dao.findPhoneByBuCode(bu_code);
		
		
		try {
			String content = "截止到"+date+"您的账户余额是"+bl+"元，请确保您的账户余额能保证业务的正常运行。"; 
			StringBuffer sb = new StringBuffer("http://www.jc-chn.cn/smsSend.do?");
			sb.append("username=khcs");
			sb.append("&password="+MD5Utils.MD5("khcs"+MD5Utils.MD5("19n4ypgs")));
			sb.append("&mobile="+phone);
			sb.append("&content="+URLEncoder.encode(content,"utf-8"));
			sb.append("&dstime=");
			URL url = new URL(sb.toString());
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.connect(); 
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = "";
			String result = "";
			while ((line=in.readLine())!=null){
				result += line + "\n";
			}
		    in.close();
		    
		    System.out.println(result);
		    
		    
		    Long res=Long.parseLong(result.trim());
		    
		    if(res>0L)
		    {
		    	return true;
		    }
		    else
		    {
		    	return false;
		    }
		    
		    
		    
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		 
		
	}
	
	public boolean smsPlatform_yzx(String bu_code,Long balance)
	{
		
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
			
			SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
			
			
			String bl=getNumber(balance/10000);
			
			String phone=dao.findPhoneByBuCode(bu_code);
			
			Date d=new Date();
			String time=sdf.format(d);
			
			String time1=sdf1.format(d);
			
			
			HttpClient client = new HttpClient();
			JSONObject obj = new JSONObject();
			
			obj.put("appId", "623f4256803640f3ad117e17a59c6788");
			obj.put("templateId", "21303");
			obj.put("to", phone);
			obj.put("param", time1+","+bl);
			
			
			
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
			
			JSONObject res=new JSONObject(result);
			
			if("000000".equals(res.getJSONObject("resp").getString("respCode")))
			{
				return true;
			}
			else
			{
				return false;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private  String getNumber(float number){  
        DecimalFormat df = new DecimalFormat("#.####");  
        
        return df.format(number);  
    }  

}
