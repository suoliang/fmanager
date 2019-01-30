package com.cyyun.base.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 
 * @author yaodw
 *
 */
public class CyyunDateUtils {

	public final static String DATEFORMAT = "yyyy-MM-dd";
	
	public final static String DATEFORMATLONG = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 往前推一个月
	 * @param date
	 * @return
	 */
	public static Date forwardOneMonthToDate(Date date){
		Calendar calendar = Calendar.getInstance();// 日历对象
		calendar.setTime(date);// 设置当前日期
		calendar.add(Calendar.MONTH, -1);// 月份减一
		return calendar.getTime();
	}
	/**
	 * 往前推一个月
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String forwardOneMonthToString(Date date, String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(forwardOneMonthToDate(date));
	}
	
	public static Date parseDate(String date,String dateFormat){
		try {
			return new SimpleDateFormat(dateFormat).parse(date);
		} catch (ParseException e) {
			
		}
		return null;
	}
	
	// 格式化日期
	public static String formatDate(Date date) {
		return DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.DatePattern).format(date);
	}
	
	// 格式化日期
	public static String formatDate(Date date,String format) {
		return new SimpleDateFormat(format).format(date);
	}
	
	// 格式化日期
	public static String formatDate(String date,String format) throws ParseException {
		return new SimpleDateFormat(format).format(new SimpleDateFormat(DATEFORMAT).parse(date));
	}
}
