package com.cyyun.fm.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface ArticleVoInter {

	public static final Logger logger = LoggerFactory.getLogger(ArticleVoInter.class);
	/**
	 * 获取地域名称
	 * @return
	 */
	public String getAreanames();
	/**
	 * 获取行业名称
	 * @return
	 */
	public String getIndustrynames();
	
	/**
	 * 获取作者名称
	 */
	public String getAuthorName();
	/**
	 * 
	 */
	public String getSentiments();
	/**
	 * 高亮显示
	 */
	public String showTitleLight();
	public String showContentLight();
	
//	public String showAbConentLight();
	
}
