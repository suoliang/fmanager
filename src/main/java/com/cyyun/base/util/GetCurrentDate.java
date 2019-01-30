package com.cyyun.base.util;

import java.util.Date;

/** 
 * @author  SuoLiang  
 * @version 2016年8月17日
 */
public class GetCurrentDate {

	public static String yyyyMMddHHmmss() {
		return DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern).format(new Date());
	}
	
}
