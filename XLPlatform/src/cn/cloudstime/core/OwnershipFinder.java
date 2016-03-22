package cn.cloudstime.core;

import cn.cloudstime.global.Global;
import com.cloudstime.dao.TransactionDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONObject;

public class OwnershipFinder {
	public JSONObject findOwnership(JSONObject request) {
		try {
			JSONObject ownsership = new JSONObject();
			
			//物联网卡特殊处理
			if("2".equals(request.getString("businessType")))
			{
				JSONObject obj=new JSONObject();
				obj.put("province", "全国");
				obj.put("supplier", "其他");
				return obj;
			}	
				

			TransactionDao dao = new TransactionDao();

			ownsership = dao.findOwnershipByLocal(request);

			if (!ownsership.has("province"))
			{
			return findOwnershipFromNet(request);
			}
			else
			{
				return ownsership;
			}
		} catch (IOException e) {
			e.printStackTrace();

			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public JSONObject findOwnershipFromNet(JSONObject request) throws IOException {
		URL url = new URL(
				"http://apis.baidu.com/apistore/mobilenumber/mobilenumber?phone=" + request.getString("phone"));
		URLConnection connection = url.openConnection();

		connection.setDoOutput(true);

		connection.addRequestProperty("apikey", Global.BAIDU_PHONE_OWNERSHIP_KEY);

		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf8");
		out.flush();
		out.close();

		String sCurrentLine = "";
		String sTotalString = "";

		InputStream l_urlStream = connection.getInputStream();
		BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
		while ((sCurrentLine = l_reader.readLine()) != null) {
			sTotalString = sTotalString + sCurrentLine + "/r/n";
		}

		JSONObject info = new JSONObject(sTotalString);

		JSONObject area_info = info.getJSONObject("retData");

		return area_info;
	}
}