package com.cyyun.fm.setting.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.service.WebsiteService;
import com.cyyun.base.service.bean.WebsiteBean;
import com.cyyun.base.service.exception.WebsiteServiceException;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.customer.service.CustomerService;
import com.cyyun.customer.service.bean.CustomerBean;
import com.cyyun.customer.service.bean.CustomerQueryBean;
import com.cyyun.fm.service.CustSiteService;
import com.cyyun.fm.service.bean.CustSiteBean;
import com.cyyun.fm.service.bean.CustSiteQueryBean;
import com.cyyun.fm.setting.bean.CustsiteParam;
import com.cyyun.fm.setting.bean.SiteView;
/**
 * 站点审核支持
 * @author LIUJUNWU
 *
 */
@Component
public class SiteAuditControllerSupport {
	@Autowired
	private CustSiteService fmCustSiteService;
	@Autowired
	private CustomerService CustomerService;
	@Autowired
	private WebsiteService websiteService;
	
	
	
	public PageInfoBean<CustSiteBean> queryBriefByPage(SiteView condition) {
		CustSiteQueryBean custSiteQueryBean=new CustSiteQueryBean();
		custSiteQueryBean=BeanUtil.copy(condition, CustSiteQueryBean.class);
		PageInfoBean<CustSiteBean> pageInfo = fmCustSiteService.queryCustSitePage(custSiteQueryBean);
		return pageInfo;
	}
	
	public List<CustomerBean> queryCostomer(CustomerBean customerBean){
		CustomerQueryBean customerQueryBean=new CustomerQueryBean();
		customerQueryBean=BeanUtil.copy(customerBean, CustomerQueryBean.class);
		customerQueryBean.setNeedPaging(false);
		List<CustomerBean> customerBeanList = CustomerService.queryCustomersByPage(customerQueryBean).getData();
		return customerBeanList;
	}
	
	public void updateCustsite(CustsiteParam custsiteParam){
		CustSiteQueryBean custSiteQueryBean=new CustSiteQueryBean();
		custSiteQueryBean=BeanUtil.copy(custsiteParam, CustSiteQueryBean.class);
		fmCustSiteService.updateCustSiteById(custSiteQueryBean);
	}
	
	public WebsiteBean checkFID(CustsiteParam custsiteParam) throws WebsiteServiceException{
		return websiteService.findWebsiteById(custsiteParam.getSpiderSiteId());
	}
	
}
