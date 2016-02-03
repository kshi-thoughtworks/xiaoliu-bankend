package cn.cloudstime.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import com.cloudstime.dao.TransactionDao;

import cn.cloudstime.global.Global;

/**
 * 归属地查询
 * 先使用本地归属地信息表，无结果使用百度API
 * @author qicheng
 *
 */
public class OwnershipFinder {

	
	public JSONObject findOwnership(JSONObject request)
	{
		
		//{supplier:supplier,province:province}
		try {
			JSONObject ownsership=new JSONObject();
			
			TransactionDao dao=new TransactionDao();
			
			ownsership=dao.findOwnershipByLocal(request);
			
			if(!ownsership.has("province"))
			{
				ownsership=findOwnershipFromNet(request);
			}
			
			return ownsership;
			
		} catch (IOException e){
			e.printStackTrace();
			//未完成
			//向日志写"findOwnershipFromNet_FAIL"
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			//未完成
			//向日志写"findOwnershipFromLocal_FAIL"
			return null;
		}
		
		
	}
	
	
	
	
	public JSONObject findOwnershipFromNet(JSONObject request) throws IOException
	{
		 
        URL url = new URL("http://apis.baidu.com/apistore/mobilenumber/mobilenumber?phone="+request.getString("phone"));   
        URLConnection connection = url.openConnection();   
        
        connection.setDoOutput(true);  
        
        connection.addRequestProperty("apikey", Global.BAIDU_PHONE_OWNERSHIP_KEY);
       
       
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf8");   
        out.flush();   
        out.close();   
        
        String sCurrentLine;   
        String sTotalString;   
        sCurrentLine = "";   
        sTotalString = "";   
        InputStream l_urlStream;   
        l_urlStream = connection.getInputStream();
        BufferedReader l_reader = new BufferedReader(new InputStreamReader(   
                l_urlStream));   
        while ((sCurrentLine = l_reader.readLine()) != null) {   
            sTotalString += sCurrentLine + "/r/n";   
  
        }   
        JSONObject info= new JSONObject(sTotalString);
        
        JSONObject area_info= info.getJSONObject("retData");
        
        return area_info;
	}
}
