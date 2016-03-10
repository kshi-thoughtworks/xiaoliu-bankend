package cn.cloudstime.core.thread;

import cn.cloudstime.core.OutputManager;
import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;
import org.json.JSONObject;

public class OutputThread {
	public void run() {
		for (int i = 0; i < Global.OUTPUT_THREAD_COUNT.intValue(); i++) {
			Thread thread = new Thread() {
				public void run() {
					while (Global.THREAD_STOP) {
						String input = "";
						try {
							input = JedisUtil.rpop(Global.OUPPUT_QUEUE);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}

						if ((input == null) || (input == "")) {
							try {
								Thread.sleep(Global.OUTPUT_THREAD_SLEEPTIME.longValue());
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							JSONObject request = new JSONObject(input);
							OutputManager outputManager = new OutputManager();
							outputManager.output(request);
						}
					}
				}
			};
			Global.THREAD_POOL.add(thread);
			thread.start();
		}
	}
}