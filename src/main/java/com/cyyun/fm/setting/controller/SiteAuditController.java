package com.cyyun.fm.setting.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.base.service.bean.WebsiteBean;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.customer.service.bean.CustomerBean;
import com.cyyun.fm.service.bean.CustSiteBean;
import com.cyyun.fm.setting.bean.CustsiteParam;
import com.cyyun.fm.setting.bean.SiteView;
/**
 * 站点审核控制器
 * @author LIUJUNWU
 *
 */
@Controller()
@RequestMapping("setting")
public class SiteAuditController extends BaseController {
	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;

	@Autowired
	private SiteAuditControllerSupport support;
	/**
	 * 加载首页
	 * @param messageBoardBean
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = { "siteAudit/index" })
	public ModelAndView siteAuditIndex(SiteView condition, Integer pageNo, Integer pageSize) {
		try {
			if (pageNo == null) {
				pageNo = PAGE_NO;
			}
			if (pageSize == null) {
				pageSize = PAGE_SIZE;
			}
			condition.setPageNo(pageNo);
			
			condition.setPageSize(pageSize);
			condition.setNeedPaging(true);
			PageInfoBean<CustSiteBean> custSitePageInfo = support.queryBriefByPage(condition);
			
			return view("/localsetting/siteAuditInfo").addObject("pageInfo", custSitePageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载站点列表失败");
		}
	}
	/**
	 * 查询
	 * @param condition
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = { "siteAudit/queryList" })
	public ModelAndView queryList(SiteView condition, Integer pageNo, Integer pageSize) {
		try {
			if (pageNo == null) {
				pageNo = PAGE_NO;
			}
			if (pageSize == null) {
				pageSize = PAGE_SIZE;
			}
			List<CustomerBean> customerList = new ArrayList<CustomerBean>();
			if ((condition.getCustomerName() != null && condition.getCustomerName() != "")) {
				CustomerBean customerBean = new CustomerBean();
				customerBean.setName(CyyunSqlUtil.dealSql(condition.getCustomerName()));
				customerList = support.queryCostomer(customerBean);
			}
			if (customerList != null) {
				for (CustomerBean customerItem : customerList) {
					condition.getCustIds().add(customerItem.getId());
				}
			}else{//界面输入的客户名称关键字无对应客户
				PageInfoBean<CustSiteBean> custSitePageInfo = new PageInfoBean<CustSiteBean>();
				return view("/localsetting/siteAudit-paging-site").addObject("pageInfo", custSitePageInfo);
			}
			
			condition.setPageNo(pageNo);

			condition.setPageSize(pageSize);
			condition.setNeedPaging(true);
			PageInfoBean<CustSiteBean> custSitePageInfo = support.queryBriefByPage(condition);

			return view("/localsetting/siteAudit-paging-site").addObject("pageInfo", custSitePageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询站点列表失败");
		}
	}
	/**
	 * 审核需求
	 * @param 
	 * @return
	 */
	@RequestMapping(value = { "siteAudit/needAudit" })
	@ResponseBody
	public MessageBean needAudit(CustsiteParam custsiteParam) {
		try {
			support.updateCustsite(custsiteParam);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "审核需求操作成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "审核需求操作失败！");
		}
	}	
	/**
	 * 关联Fid
	 * @param 
	 * @return
	 */
	@RequestMapping(value = { "siteAudit/boundFid" })
	@ResponseBody
	public MessageBean boundFid(CustsiteParam custsiteParam) {
		try {
			WebsiteBean websiteBean=support.checkFID(custsiteParam);//得到website对象
			custsiteParam.setMediaType(websiteBean.getStyle());//媒体类型赋值
			custsiteParam.setProcStatus(1);//状态改为已处理
			support.updateCustsite(custsiteParam);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "关联Fid操作成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "关联Fid操作失败！");
		}
	}	
	/**
	 * 检查FID是否存在
	 * @param 
	 * @return
	 */
	@RequestMapping(value = { "siteAudit/checkFID" })
	@ResponseBody
	public MessageBean checkFID(CustsiteParam custsiteParam) {
		try {
			Boolean flag = false;
			if (custsiteParam.getSpiderSiteId()!=null) {
				WebsiteBean websiteBean=support.checkFID(custsiteParam);
				if (websiteBean!=null) {
					flag=true;
				}
			}
			return buildMessage(MESSAGE_TYPE_SUCCESS, "检查FID是否存在操作成功！",flag);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检查FID是否存在操作失败！");
		}
	}	
	
}
