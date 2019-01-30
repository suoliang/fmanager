package com.cyyun.base.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
/**
 * 创建SimpleDateFormat工具类
 * 目的：为每个线程缓存一个SimpleDateFormat实例，提高性能
 * @author LIUJUNWU
 * 
 */
public class DateFormatFactory {

	private static final Map<DatePattern, ThreadLocal<DateFormat>> pattern2ThreadLocal;

	static {
		DatePattern[] patterns = DatePattern.values();
		int len = patterns.length;
		pattern2ThreadLocal = new HashMap<DatePattern, ThreadLocal<DateFormat>>(len);

		for (int i = 0; i < len; i++) {
			DatePattern datePattern = patterns[i];
			final String pattern = datePattern.pattern;

			pattern2ThreadLocal.put(datePattern, new ThreadLocal<DateFormat>() {
				@Override
				protected DateFormat initialValue() {
					return new SimpleDateFormat(pattern);
				}
			});
		}
	}

	//获取SimpleDateFormat
	public static SimpleDateFormat getDateFormat(DatePattern pattern) {
		ThreadLocal<DateFormat> threadDateFormat = pattern2ThreadLocal.get(pattern);
		//不需要判断threadDateFormat是否为空
		return (SimpleDateFormat) threadDateFormat.get();
	}
	
	public enum DatePattern {

		TimePattern("yyyy-MM-dd HH:mm:ss"),
		
		TimePattern1("yyyy-MM-dd HH:mm"),
		
		TimePatternHour("yyyy-MM-dd HH"),
		
		TimePattern2("yyyyMMddhhmmsss"),
		
		TimePattern3("HHddss"),

		DatePattern("yyyy-MM-dd"),
		
		DatePattern1("yyyyMMdd");

		public String pattern;

		private DatePattern(String pattern) {
			this.pattern = pattern;
		}

	}
	

}
