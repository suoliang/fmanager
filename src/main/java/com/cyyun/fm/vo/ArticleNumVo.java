package com.cyyun.fm.vo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cyyun.base.service.bean.ArticleBean;

/** 
 * @author  SuoLiang  
 * @version 2016年9月19日
 */
public class ArticleNumVo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 按媒体趋势统计
	 */
	private Map<Integer, Long> countStyleArticleMap;
	/**
	 * 按正负面统计
	 */
	private Map<Integer, Long> countSentimentArticleMap;		 
	/**
	 * 按专题统计
	 */
	private Map<Integer, Long> countTopicArticleMap;
	
	private LinkedHashMap<String, ArticleBean> articleBeanMap;
	
	private List<String> weiboGuidList;
	
	
	
	public List<String> getWeiboGuidList() {
		return weiboGuidList;
	}

	public void setWeiboGuidList(List<String> weiboGuidList) {
		this.weiboGuidList = weiboGuidList;
	}

	public LinkedHashMap<String, ArticleBean> getArticleBeanMap() {
		return articleBeanMap;
	}

	public void setArticleBeanMap(LinkedHashMap<String, ArticleBean> articleBeanMap) {
		this.articleBeanMap = articleBeanMap;
	}

	public Map<Integer, Long> getCountStyleArticleMap() {
		return countStyleArticleMap;
	}
	
	public void setCountStyleArticleMap(Map<Integer, Long> countStyleArticleMap) {
		this.countStyleArticleMap = countStyleArticleMap;
	}
	
	public Map<Integer, Long> getCountSentimentArticleMap() {
		return countSentimentArticleMap;
	}
	
	public void setCountSentimentArticleMap(
			Map<Integer, Long> countSentimentArticleMap) {
		this.countSentimentArticleMap = countSentimentArticleMap;
	}
	
	public Map<Integer, Long> getCountTopicArticleMap() {
		return countTopicArticleMap;
	}
	
	public void setCountTopicArticleMap(Map<Integer, Long> countTopicArticleMap) {
		this.countTopicArticleMap = countTopicArticleMap;
	}
	
}
