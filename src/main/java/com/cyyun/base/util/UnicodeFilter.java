package com.cyyun.base.util;


/**
 * <h3>工具类</h3>
 * 
 * @author LIUJUNWU
 * @version 1.0.0
 * 
 */
public class UnicodeFilter {
	/**
	 * 过滤 “&#00000;”格式的特殊字符（过滤范围以下三个范围之外的）
	 * Unicode中文范围 19968 - 40869
	 * Unicode数字、字母和部分符号范围 31 - 128
	 * 标点符号范围 65280 - 65519
	 * @param str
	 * @return
	 * @throws Exception 
	 */
//	public static String filter(String str){
//		if (str == null || str.length() == 0) {
//			return str;
//		}
//		int in;
//		char[] ch = new char[] {};
//		String st = "";
//		StringBuffer sb=new StringBuffer();
//		for (int i = 0; i < str.length(); i++) {
//			in = str.charAt(i);
//			st = Integer.toHexString(in);
//			Integer r = Integer.parseInt(st, 16);
//			if (r<200 || 19968<r && r<40869 || 65280<r && r<65519) {
//				ch = str.toCharArray();
////				try{
//					for (int j = 0; j < ch.length; j++) {
//						if (i == j) {
////							System.err.println("添加字符："+String.valueOf(ch[j])+"-----"+r);
//							sb.append(ch[j]);
//						}
//					}
////				}catch(Exception e){
////					System.err.println("报错啦————————"+count+"————————————————"+temp);
////					throw new Exception(e);
////				}
//			}
//		}
//		return sb.toString();
//	}
	public static String filter(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		int in;
		String st = "";
		StringBuffer sb = new StringBuffer();
		char[] ch = new char[] {};
		ch = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] != '\0') {
				in = String.valueOf(ch[i]).charAt(0);
				st = Integer.toHexString(in);
				Integer r = Integer.parseInt(st, 16);

				if ((31 < r && r <= 128) || (19968 <= r && r <= 40869) || (65280 <= r && r <= 65519)) {
					//					System.err.println("尼玛================================="+ch[i]+"------"+r);
					sb.append(ch[i]);
				}
			}
		}
		return sb.toString();
	}


	public static void main(String[] args) throws Exception {
//		String f="中";
//		f=f.replaceAll("\\{|\\)", "");
//		f=f.replaceAll("\\，", "");
//		System.err.println(f);
//		System.err.println(filter("1"));
		System.err.println(Integer.parseInt(Integer.toHexString(String.valueOf("一").charAt(0)),16));
//		Integer r = Integer.parseInt("9FBF", 16);
//		Integer r = Integer.parseInt("9FBF", 16);
//		if (60<r && r<125 || 19968<r && r<40869 || 65280<r && r<65519) {
//		System.err.println(r);
//		}
	}
}
