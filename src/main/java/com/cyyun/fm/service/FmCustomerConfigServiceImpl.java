package com.cyyun.fm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyyun.base.constant.ArticleConstants;
import com.cyyun.base.filter.FMContext;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;

@Service
public class FmCustomerConfigServiceImpl implements FmCustomerConfigService{

	@Autowired
	protected CustomerConfigService customerConfigService;
	
	/**
	 * 获取客户文章状态
	 */
	@Override
	public String getArticleStage() {
		if(getCustConfig().getViewMonitorData()){
			return ArticleConstants.ArticleStage.PUTONG;
		}
		return ArticleConstants.ArticleStage.GUIDANG;
	}

	@Override
	public CustConfigBean getCustConfig() {
		return customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
	}
}
