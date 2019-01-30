package com.cyyun.fm.search.bean;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/** 
 * 搜索枚举类
 * @author SuoLiang
 * @date   2016年7月19日
 */
public enum CloudSearchShowEnum {

	BAIDUWEBPAGE("1","百度网页"),
	
	BAIDUNEWS("2","百度新闻"),
	
	SOUGOUWEIXIN("3","搜狗微信"),
	
	SOUGOUNEWS("4","搜狗新闻"),
	
	QIHOOSEARCH("5","360搜索"),

	ZHONGSOU("6","中搜"),
	
	CHINASO("7","中国搜索");

	private String code;
	
	private String name;
	
	private CloudSearchShowEnum(String code,String name){
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/** 根据枚举代码得到对应的汉字 */
	public static String parseCode(String code) {
		for (CloudSearchShowEnum c : CloudSearchShowEnum.values()) {
			if (c.getCode().equals(code)) {
				return c.getName();
			}
		}
		return "";
	}
	
	
	/***
	 * 得到该枚举类的map集合
	 * @return
	 */
	public static Map<String, String> getSearchShowMap(){
		Map<String, String> map = new TreeMap<String, String>(
				new Comparator<String>() {
					public int compare(String obj1, String obj2) {
						Integer num1 = Integer.valueOf(obj1);
						Integer num2 = Integer.valueOf(obj2);
						if (num1 == null || num2 == null) {
							return 0;
						}
						/** 升序排序 */
						return num1.compareTo(num2);
					}
				});
		for(CloudSearchShowEnum o:CloudSearchShowEnum.values()){
			map.put(o.getCode(), o.getName());
		}
		return map;
	}
	
	public static void main(String[] args) {
		Map<String, String> categoryMap = CloudSearchShowEnum.getSearchShowMap();
		System.out.println(categoryMap.get(CloudSearchShowEnum.BAIDUWEBPAGE.getCode()));
	}
	
}
