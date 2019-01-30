package com.cyyun.fm.service;

import java.util.List;

import com.cyyun.base.service.bean.ArticleStatisticBean;
import com.cyyun.fm.service.bean.StatCustTopicBean;

public interface StatCustTopicService {

	/**
	 * 概况分析专题分布统计
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<StatCustTopicBean> searchStatCustTopic(String startDate, String endDate, Integer limit) throws Exception;
	
	public List<ArticleStatisticBean> countArticleInCustopic(String startDate, String endDate) throws Exception;
}
