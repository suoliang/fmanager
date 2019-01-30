package com.cyyun.fm.service;

import java.util.List;

import com.cyyun.base.service.bean.CustTopicBean;

public interface FmCustTopicService {

	/**
	 * 查询当前客户下所有可用专题
	 * @return
	 */
	public List<CustTopicBean> queryCurrentCustAllTopics() throws Exception;
	/**
	 * 根据当前topicId查询专题树
	 * @param topicId
	 * @return
	 */
	public String[] queryCustTopicTree(Integer topicId) throws Exception;
}
