package cn.cloudstime.core.thread;

import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;
import cn.cloudstime.util.MD5Utils;
import com.cloudstime.dao.TransactionDao;
import java.io.BufferedReader;
import java.io.IOException;
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

public class ResponseThread {
	public void run() {
		for (int i = 0; i < Global.RESPONSE_THREAD_COUNT.intValue(); i++) {
			Thread thread = new Thread() {
				public void run() {
					while (Global.THREAD_STOP) {
						String input = "";
						try {
							input = JedisUtil.rpop(Global.RESPONSE_QUEUE);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}

						if ((input == null) || (input == "")) {
							try {
								Thread.sleep(Global.RESPONSE_THREAD_SLEEPTIME.longValue());
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							JSONObject request = new JSONObject(input);
							
							if("".equals(request.getString("notifyUrl"))||request.getString("notifyUrl")==null)
							{
								continue;
							}
							
							
							

							if ((!request.has("response_count")) || (request.getInt("response_count") <= 3)) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

								String business_type = "1";
								String card_no = request.getString("phone");
								String extendss = request.has("extends") ? request.getString("extends") : "";
								String notify_id = UUID.randomUUID().toString();
								String notify_time = sdf.format(new Date());
								String notify_type = "trade_status_sync";
								String order_no = request.getString("orderNo");
								String status_desc = request.has("transaction_error_info")
										? request.getString("transaction_error_info") : "充值成功";
								String trade_no = request.getString("transaction_code");
								Integer trade_status = Integer.valueOf(request.has("transaction_error_code")
										? request.getInt("transaction_error_code") : 1);
								TransactionDao dao = new TransactionDao();
								String key = dao.findBusinessKey(request).getString("user_key");

								String extStr = "";

								String traStr = "";

								if ((extendss == null) || ("".equals(extendss))) {
									extStr = "&extendss=" + extendss;
								}

								if (trade_status.intValue() == 1) {
									traStr = "&trade_no=" + trade_no;
								}

								String str = "business_type=" + business_type + "&card_no=" + card_no + extStr
										+ "&notify_id=" + notify_id + "&notify_time=" + notify_time + "&notify_type="
										+ notify_type + "&order_no=" + order_no + "&status_desc=" + status_desc + traStr
										+ "&trade_status" + trade_status + "&key=" + MD5Utils.MD5(key);

								String sign = MD5Utils.MD5(str);

								JSONObject obj = new JSONObject();
								obj.put("business_type", business_type);
								obj.put("card_no", card_no);
								if ((extendss != null) && (!"".equals(extendss))) {
									obj.put("extends", extendss);
								}
								obj.put("notify_id", notify_id);
								obj.put("notify_time", notify_time);
								obj.put("notify_type", notify_type);
								obj.put("order_no", order_no);
								obj.put("status_desc", status_desc);
								if (trade_status.intValue() == 1) {
									obj.put("trade_no", trade_no);
								}
								obj.put("trade_status", trade_status);
								obj.put("sign", sign);
								obj.put("sign_type", "MD5");
								try {
									HttpClient client = new HttpClient();
									JSONObject json = new JSONObject();
									PostMethod post = new PostMethod();
									post.setURI(new URI(request.getString("notifyUrl")));
									post.setRequestHeader("Content-type", "application/json;charset=UTF-8");
									StringRequestEntity entity = new StringRequestEntity(obj.toString());
									post.setRequestEntity(entity);
									client.executeMethod(post);
									String str1 = post.getResponseBodyAsString();
								} catch (Exception e) {
									e.printStackTrace();
									if (request.has("response_count")) {
										request.put("response_count", request.getInt("response_count") + 1);
									} else {
										request.put("response_count", 1);
									}

									JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
								}
							}
						}
					}
				}
			};
			Global.THREAD_POOL.add(thread);
			thread.start();
		}
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