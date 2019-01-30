package com.cyyun.fm.search.bean;

import java.util.HashMap;
import java.util.Map;

/** 
 * 搜索URL枚举类
 * @author SuoLiang
 * @date   2016年7月19日
 */
public enum CloudSearchUrlEnum {

	BAIDUWEBPAGE("1","http://www.baidu.com/s?wd="),
	
	BAIDUNEWS("2","http://news.baidu.com/ns?word="),
	
	SOUGOUWEIXIN("3","http://weixin.sogou.com/weixin?query="),
	
	SOUGOUNEWS("4","http://news.sogou.com/news?query="),
	
	QIHOOSEARCH("5","http://www.so.com/s?q="),

	ZHONGSOU("6","http://www.zhongsou.com/third.cgi?w="),
	
	CHINASO("7","http://www.chinaso.com/search/pagesearch.htm?q=");
	
	private String code;
	
	private String name;
	
	private CloudSearchUrlEnum(String code,String name){
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
		for (CloudSearchUrlEnum c : CloudSearchUrlEnum.values()) {
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
	public static Map<String, String> getSearchUrlMap(){
		Map<String, String> map=new HashMap<String, String>();
		for(CloudSearchUrlEnum o:CloudSearchUrlEnum.values()){
			map.put(o.getCode(), o.getName());
		}
		return map;
	}
	
	public static void main(String[] args) {
		Map<String, String> categoryMap = CloudSearchUrlEnum.getSearchUrlMap();
		System.out.println(categoryMap.get(CloudSearchUrlEnum.BAIDUWEBPAGE.getCode()));
	}
	
}
