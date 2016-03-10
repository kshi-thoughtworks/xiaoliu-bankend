package cn.cloudstime.core.thread;

import cn.cloudstime.core.ChargeBusinessUser;
import cn.cloudstime.core.ChargeFlowManager;
import cn.cloudstime.core.ChargeFlowRollbackManager;
import cn.cloudstime.global.Global;
import cn.cloudstime.util.JedisUtil;
import java.io.PrintStream;
import org.json.JSONObject;

public class TransactionThread {
	public void run() {
		System.out.println("启动交易线程");
		for (int i = 0; i < Global.TRANSACTION_THREAD_COUNT.intValue(); i++) {
			Thread thread = new Thread() {
				public void run() {
					while (Global.THREAD_STOP) {
						String input = "";
						try {
							input = JedisUtil.rpop(Global.REQUEST_QUEUE);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}

						if ((input == null) || (input == "")) {
							try {
								Thread.sleep(Global.TRANSACTION_THREAD_SLEEPTIME.longValue());
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							JSONObject request = new JSONObject(input);

							if (request.getInt("transaction_type") == 1) {
								ChargeFlowManager chargeFlow = new ChargeFlowManager();
								if (chargeFlow.ChrageFlow(request))
									;
							} else if (request.getInt("transaction_type") == 2) {
								ChargeBusinessUser chargeBu = new ChargeBusinessUser();
								if (chargeBu.charge(request))
									;
							} else if (request.getInt("transaction_type") == 3) {
								ChargeFlowRollbackManager chargeRb = new ChargeFlowRollbackManager();
								if (chargeRb.rollback(request))
									;
							} else {
								System.out.println("请求的交易类型有错误！");
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