package cn.cloudstime.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.cloudstime.global.Global;

public class SysOrderNoFinder {
	
	
	/**
	 * 获取订单流水号
	 * @return
	 */
	public static  String getOrderNo()
	{
		
		String type="BT";
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		
		String time=df.format(new Date());
		
		String num=getNumber();
		
		
		
		
		return  type+time+num;
	}
	
	public static synchronized String getNumber()
	{
		String num="";
		if(Global.CURRENT_ORDER_NO<10)
		{
			num="000"+Global.CURRENT_ORDER_NO;
			
		}
		
		if(Global.CURRENT_ORDER_NO>=10&&Global.CURRENT_ORDER_NO<100)
		{
			num="00"+Global.CURRENT_ORDER_NO;
		}
		
		if(Global.CURRENT_ORDER_NO>=100&&Global.CURRENT_ORDER_NO<1000)
		{
			num="0"+Global.CURRENT_ORDER_NO;
		}
		
		if(Global.CURRENT_ORDER_NO>=1000)
		{
			num=""+Global.CURRENT_ORDER_NO;
		}
		
		
		if(Global.CURRENT_ORDER_NO==8888)
		{
			Global.CURRENT_ORDER_NO=1;
		}
		else
		{
			Global.CURRENT_ORDER_NO=Global.CURRENT_ORDER_NO+1;
		}
		
		return num;
	}

}
