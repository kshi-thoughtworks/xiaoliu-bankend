package cn.cloudstime.core.thread;

import cn.cloudstime.core.OutputManager;
import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;
import java.io.PrintStream;
import org.json.JSONObject;

public class NotifyThread {
	public void run() {
		for (int i = 0; i < Global.NOTIFY_THREAD_COUNT.intValue(); i++) {
			Thread thread = new Thread() {
				public void run() {
					while (true) {
						String input = "";
						try {
							input = JedisUtil.rpop(Global.NOTIFY_QUEUE);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}

						if ((input == null) || (input == "")) {
							try {
								Thread.sleep(Global.NOTIFY_THREAD_SLEEPTIME.longValue());
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							JSONObject request;
							JSONObject result = new JSONObject(input);
							try {
								System.out.println(result);
								request = new JSONObject(
										JedisUtil.get(Global.HEAD_NOTIFY_INFO + result.getString("transaction_code")));

								System.out.println(request);
								JedisUtil.del(Global.HEAD_NOTIFY_INFO + result.getString("transaction_code"));
							} catch (Exception e) {
								e.printStackTrace();
								continue;
							}
							JSONObject channels = request.getJSONObject("channels");

							if (result.getInt("state") == 0) {
								request.put("transaction_type", 3);
								request.put("price", ((JSONObject) channels.get("channel_0")).getLong("dprice"));
								JedisUtil.lpush(Global.REQUEST_QUEUE, request.toString());

								request.put("transaction_error_info", result.get("retmsg"));

								request.put("target_channel_index", request.getInt("target_channel_index") + 1);

								request.put("transaction_type", 1);
								OutputManager out = new OutputManager();
								out.output(request);
							} else {
								JSONObject channel = channels
										.getJSONObject("channel_" + request.getInt("target_channel_index"));

								request.put("transaction_state", 2);
								request.put("platform_name", channel.getString("name"));

								JedisUtil.lpush(Global.LOG_QUEUE, request.toString());

								JedisUtil.lpush(Global.RESPONSE_QUEUE, request.toString());
							}
						}
					}
				}
			};
			thread.start();
		}
	}
}