package cn.cloudstime.global;

import cn.cloudstime.bean.BusinessUserBalanceBean;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class Global {
	public static Map<String, BusinessUserBalanceBean> BUSINESS_USER_BALANCE = new HashMap();

	public static Map<String, Integer> BUSINESS_USER_STATE = new HashMap();

	public static Map<String, Long> BUSINESS_USER_ALERT_BALANCE = new HashMap();

	public static Map<String, JSONObject> PLATFORM_INFO = new HashMap();

	public static String HEAD_BUSINESS_USER_BALANCE = "BU_BALANCE_";

	public static String HEAD_BUSINESS_USER_STATE = "BU_STATE_";

	public static String HEAD_BUSINESS_USER_ALERT_BALANCE = "BU_ALERT_";

	public static String HEAD_PLATFORM_INFO = "PT_INFO_";

	public static String HEAD_NOTIFY_INFO = "NOTIFY_";

	public static Map<String, JSONObject> NUMBER_RULES = new HashMap();

	public static String HEAD_NUMBER_RULES = "RULE_";

	public static ApplicationContext factory = new ClassPathXmlApplicationContext("ApplicationContext.xml");

	public static JdbcTemplate jdbcTemplate = (JdbcTemplate) factory.getBean("jdbcTemplate");

	public static Integer TRANSACTION_THREAD_COUNT = Integer.valueOf(100);

	public static Integer OUTPUT_THREAD_COUNT = Integer.valueOf(100);

	public static Integer RESPONSE_THREAD_COUNT = Integer.valueOf(100);

	public static Integer lOG_THREAD_COUNT = Integer.valueOf(100);

	public static Integer EXCEPTION_THREAD_COUNT = Integer.valueOf(10);

	public static Integer NOTIFY_THREAD_COUNT = Integer.valueOf(50);

	public static Long TRANSACTION_THREAD_SLEEPTIME = Long.valueOf(200L);

	public static Long OUTPUT_THREAD_SLEEPTIME = Long.valueOf(200L);

	public static Long RESPONSE_THREAD_SLEEPTIME = Long.valueOf(200L);

	public static Long lOG_THREAD_SLEEPTIME = Long.valueOf(200L);

	public static Long EXCEPTION_THREAD_SLEEPTIME = Long.valueOf(200L);

	public static Long NOTIFY_THREAD_SLEEPTIME = Long.valueOf(200L);

	public static String REQUEST_QUEUE = "REQUEST_QUEUE";

	public static String OUPPUT_QUEUE = "OUTPUT_QUEUE";

	public static String RESPONSE_QUEUE = "RESPONSE_QUEUE";

	public static String LOG_QUEUE = "LOG_QUEUE";

	public static String EXCEPTION_QUEUE = "EXCEPTION_QUEUE";

	public static String NOTIFY_QUEUE = "NOTIFY_QUEUE";

	public static String BAIDU_PHONE_OWNERSHIP_KEY = "9d7cfc9acba0606e959b3505c5fe06a0";

	public static String REDIS_HOST_IP = "10.174.10.9";

	public static String EXCEPTION_CODE_BUSINESS_USER_STATE_ERROR = "6001";

	public static String EXCEPTION_CODE_GET_OWNERSHIP_ERROR = "6002";

	public static String EXCEPTION_CODE_CHANNEL_NULL_ERROR = "6003";

	public static String EXCEPTION_CODE_BUSINESS_BALANCE_NOTENOUGH_ERROR = "6004";

	public static String EXCEPTION_BUSINESS_ORDERNO_REPEAT = "6005";

	public static String EXCEPTION_CHANNEL_OUTPUT_ERROR = "6006";

	public static Map<String, String> EXCEPTION_MAP = new HashMap();

	public static Integer CURRENT_ORDER_NO = Integer.valueOf(1);

	static {
		EXCEPTION_MAP.put(EXCEPTION_CODE_BUSINESS_USER_STATE_ERROR, "商户账户冻结，无法充值");
		EXCEPTION_MAP.put(EXCEPTION_CODE_GET_OWNERSHIP_ERROR, "解析归属地失败，无法充值");
		EXCEPTION_MAP.put(EXCEPTION_CODE_CHANNEL_NULL_ERROR, "暂无可用通道，无法充值");
		EXCEPTION_MAP.put(EXCEPTION_CODE_BUSINESS_BALANCE_NOTENOUGH_ERROR, "商户余额不足，无法充值");
		EXCEPTION_MAP.put(EXCEPTION_BUSINESS_ORDERNO_REPEAT, "订单号重复");
		EXCEPTION_MAP.put(EXCEPTION_CHANNEL_OUTPUT_ERROR, "第三方接口请求失败，无法充值");
	}
}