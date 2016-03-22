package cn.cloudstime.core;

import com.cloudstime.dao.TransactionDao;
import org.json.JSONObject;

public class ChannelFinder {
	public JSONObject findChannel(JSONObject request) {
		try {
			
			if("2".equals(request.getString("businessType")))
			{
				JSONObject channels=new JSONObject();
				
				JSONObject channel=new JSONObject();
				
				channel.put("platform_id", 7);
				channel.put("name", "物联网卡");
				channel.put("set_code", request.getString("set_code"));
				channel.put("interface_flag", 5);
				channel.put("set_id", request.getString("set_id"));
				channel.put("ddiscount", Float.valueOf(request.getString("ddiscount")));
				channel.put("tdiscount", Float.valueOf(request.getString("tdiscount")));
				channel.put("dprice", new Double(request.getString("dprice")).longValue());
				channel.put("tprice", new Double(request.getString("tprice")).longValue());
			    
				channels.put("channel_0", channel);
				
				return channels;
			}
			
			
			
			
			TransactionDao dao = new TransactionDao();

			return dao.findChannel(request);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}