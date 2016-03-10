package cn.cloudstime.core.thread;

import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;
import com.cloudstime.dao.TransactionDao;
import java.io.PrintStream;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

public class ChannelFailLogThread {
	public void run() {
		for (int i = 0; i < Global.CHANNELFAIL_lOG_THREAD_COUNT.intValue(); i++) {
			Thread thread = new Thread() {
				public void run() {
					while (Global.THREAD_STOP) {
						String input = "";
						try {
							input = JedisUtil.rpop(Global.CHANNELFAIL_QUEUE);
						} catch (Exception e) {
							continue;
						}

						if ((input == null) || (input.equals(""))) {
							try {
								Thread.sleep(Global.CHANNELFAIL_lOG_THREAD_SLEEPTIME.longValue());
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							JSONObject request = new JSONObject(input);
							try {
								TransactionDao dao = new TransactionDao();
								dao.channelFailLog(request);
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println(request);
							}
						}
					}
				}
			};
			Global.THREAD_POOL.add(thread);
			thread.start();
		}
	}
}