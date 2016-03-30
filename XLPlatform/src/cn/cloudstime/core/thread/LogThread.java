package cn.cloudstime.core.thread;

import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;
import com.cloudstime.dao.TransactionDao;
import java.io.PrintStream;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

public class LogThread {
	public void run() {
		for (int i = 0; i < Global.lOG_THREAD_COUNT.intValue(); i++) {
			Thread thread = new Thread() {
				public void run() {
					while (Global.THREAD_STOP) {
						String input = "";
						try {
							input = JedisUtil.rpop(Global.LOG_QUEUE);
						} catch (Exception e) {
							continue;
						}

						if ((input == null) || (input.equals(""))) {
							try {
								Thread.sleep(Global.lOG_THREAD_SLEEPTIME.longValue());
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							JSONObject request = new JSONObject(input);
							try {
								if (request.getInt("transaction_type") == 1) {
									if (request.getInt("transaction_state") == 1) {
										TransactionDao dao = new TransactionDao();
										dao.transactionLogforState_D(request);
									} else if (request.getInt("transaction_state") == 2) {
										TransactionDao dao = new TransactionDao();
										dao.transactionLogforState_F(request);
										if(request.getLong("tprice")>request.getLong("dprice"))
										{
											dao.transactionLogforLoss(request);
										}
										
									} else if (request.getInt("transaction_state") == 3) {
										TransactionDao dao = new TransactionDao();
										dao.transactionLogforState_T(request);
									}
								} else if (request.getInt("transaction_type") == 2) {
									Global.jdbcTemplate
											.execute("update recharge_log set recharge_flow_number='"+request.getString("transaction_code")+"' where id="
													+ request.getInt("recharge_log_id"));
								}else if(request.getInt("transaction_type") == 3)
								{
									TransactionDao dao = new TransactionDao();
									dao.transactionLogforState_T(request);
								}
									
								
								
								
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