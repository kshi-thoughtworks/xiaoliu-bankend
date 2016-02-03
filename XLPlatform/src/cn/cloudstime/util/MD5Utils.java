package cn.cloudstime.util;

import java.security.MessageDigest;

public class MD5Utils {
	public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) {
        System.out.println(MD5Utils.MD5("service=1&input_charset=utf-8&partner=2088000000000100&card_no=13810067362&traffic_amount=10&range=1&expiry_month=1&enable_rule=1&business_type=1&notify_url=www.baidu.com&order_no=1&order_time=1&key=aee9e38cb4d40ec2794542567539b4c8"));
        System.out.println(MD5Utils.MD5("service=1&input_charset=utf-8&partner=2088000000000100&card_no=13810067362&traffic_amount=10&range=1&expiry_month=1&enable_rule=1&business_type=1&notify_url=www.baidu.com&order_no=1&order_time=1&aee9e38cb4d40ec2794542567539b4c8"));
    }
}
