package com.cyyun.base.util;

/**
 * postgresql关键字转义
 * @author yaodw
 *
 */
public class CyyunSqlUtil {

	public static String dealSql(String sql) {
		if (sql != null) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < sql.length(); i++) {
				char ch = sql.charAt(i);
				if (ch == '%' || ch == '\\' || ch == '_') {
					buffer.append("\\" + ch);
				} else {
					buffer.append(ch);
				}
			}
			return buffer.toString();
		} else {
			return null;
		}
	}
	
	public static void main(String[] args) {
		String str = "\\";
		System.out.println(CyyunSqlUtil.dealSql(str));
	}
}
