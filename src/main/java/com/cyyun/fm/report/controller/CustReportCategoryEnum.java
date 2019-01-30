package com.cyyun.fm.report.controller;

import java.util.HashMap;
import java.util.Map;

/** 
 * @author  SuoLiang  
 * @version 2016年4月7日
 */
public enum CustReportCategoryEnum {

	DAILY("1","日报"),
	
	WEEKLY("2","周报"),
	
	MONTHLY("3","月报"),
	
	QUARTERLY("4","季报"),
	
	SEMIANNUAL("5","半年报"),
	
	ANNUAL("6","年报"),
	
	CUSTOM("7","定制报");
	
	private String code;
	
	private String name;
	
	private CustReportCategoryEnum(String code,String name){
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
		for (CustReportCategoryEnum c : CustReportCategoryEnum.values()) {
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
	public static Map<String, String> getCustReportCategoryMap(){
		Map<String, String> map=new HashMap<String, String>();
		for(CustReportCategoryEnum o:CustReportCategoryEnum.values()){
			map.put(o.getCode(), o.getName());
		}
		return map;
	}
	
	public static void main(String[] args) {
		Map<String, String> categoryMap = CustReportCategoryEnum.getCustReportCategoryMap();
		System.out.println(categoryMap.get(CustReportCategoryEnum.DAILY.getCode()));
	}
	
}
