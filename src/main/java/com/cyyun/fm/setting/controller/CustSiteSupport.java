package com.cyyun.fm.setting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.common.core.base.BaseException;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.service.CustDataCategoryService;
import com.cyyun.fm.service.CustSiteService;
import com.cyyun.fm.service.bean.CustSiteBean;
import com.cyyun.fm.service.bean.CustSiteQueryBean;
import com.cyyun.fm.service.bean.QueryConditionBean;
import com.cyyun.fm.setting.bean.SiteView;

/**
 * <h3>搜索控制器支持</h3>
 * 
 * @author zhangfei
 * @version 1.0.0
 */
@Component
public class CustSiteSupport {

	@Autowired
	private CustDataCategoryService custDataCategoryService;

	@Autowired
	private CustSiteService custSiteService;

	PageInfoBean<CustSiteBean> querySiteByPage(SiteView condition, Integer currentpage, Integer pagesize) throws ArticleServiceException {
		CustSiteQueryBean csqb = new CustSiteQueryBean();

		PageInfoBean<CustSiteBean> pageInfo = (PageInfoBean<CustSiteBean>) custSiteService.querySite(csqb);

		return pageInfo;

	}
}
