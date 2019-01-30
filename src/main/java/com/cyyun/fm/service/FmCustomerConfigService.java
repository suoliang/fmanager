package com.cyyun.fm.service;

import com.cyyun.customer.service.bean.CustConfigBean;

public interface FmCustomerConfigService {

	/**
	 * 获得客户配置文章状态
	 * @return
	 */
	public String getArticleStage();
	/**
	 * 获得具体客户配置
	 * @return
	 */
	public CustConfigBean getCustConfig();
}
