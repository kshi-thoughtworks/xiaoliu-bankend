package cn.cloudstime.core;

import com.cloudstime.dao.TransactionDao;
import org.json.JSONObject;

public class ChannelFinder {
	public JSONObject findChannel(JSONObject request) {
		try {
			TransactionDao dao = new TransactionDao();

			return dao.findChannel(request);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}