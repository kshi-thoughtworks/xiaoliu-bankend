package cn.cloudstime.core;

import cn.cloudstime.global.Global;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SysOrderNoFinder {
	public static String getOrderNo() {
		String type = "BT";

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

		String time = df.format(new Date());

		String num = getNumber();

		return type + time + num;
	}

	public static synchronized String getNumber() {
		String num = "";
		if (Global.CURRENT_ORDER_NO.intValue() < 10) {
			num = "000" + Global.CURRENT_ORDER_NO;
		}

		if ((Global.CURRENT_ORDER_NO.intValue() >= 10) && (Global.CURRENT_ORDER_NO.intValue() < 100)) {
			num = "00" + Global.CURRENT_ORDER_NO;
		}

		if ((Global.CURRENT_ORDER_NO.intValue() >= 100) && (Global.CURRENT_ORDER_NO.intValue() < 1000)) {
			num = "0" + Global.CURRENT_ORDER_NO;
		}

		if (Global.CURRENT_ORDER_NO.intValue() >= 1000) {
			num = Global.CURRENT_ORDER_NO + "";
		}

		if (Global.CURRENT_ORDER_NO.intValue() == 8888) {
			Global.CURRENT_ORDER_NO = Integer.valueOf(1);
		} else {
			Global.CURRENT_ORDER_NO = Integer.valueOf(Global.CURRENT_ORDER_NO.intValue() + 1);
		}

		return num;
	}
}