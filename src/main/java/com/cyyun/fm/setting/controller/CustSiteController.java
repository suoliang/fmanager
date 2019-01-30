/**
 *
 */
package com.cyyun.fm.setting.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.base.util.CyyunStringUtils;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.base.BaseException;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.fm.service.CustDataCategoryService;
import com.cyyun.fm.service.CustSiteService;
import com.cyyun.fm.service.bean.CustDataBean;
import com.cyyun.fm.service.bean.CustDataCategoryBean;
import com.cyyun.fm.service.bean.CustDataCategoryQueryBean;
import com.cyyun.fm.service.bean.CustDataQueryBean;
import com.cyyun.fm.service.bean.CustSiteBean;
import com.cyyun.fm.service.bean.CustSiteQueryBean;
import com.cyyun.fm.service.exception.CustDataCategoryServiceException;
import com.cyyun.fm.setting.bean.SiteView;

/**
 * @author cyyun
 *
 */
@Controller
@RequestMapping("/setting")
public class CustSiteController extends BaseController {

	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;

	@Autowired
	private CustSiteService custSiteService;

	@Autowired
	private CustDataCategoryService custDataCategoryService;

	@Autowired
	private CustSiteSupport support;

	@RequestMapping(value = { "addSiteUrl" })
	public String addSiteUrl(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		return "/localsetting/site/addListSite";
	}

	@RequestMapping(value = { "addSite" })
	public String addSite(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("siteName");
		//站点名称 
		String siteaPlace = request.getParameter("siteaPlace");
		//备注
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId

		CustSiteBean cs = new CustSiteBean();
		cs.setName(name);//名称
		cs.setUrl(siteaPlace);//地址
		cs.setCustId(CustId);
		cs.setCreaterId(FMContext.getCurrent().getUserId());
		cs.setSpiderSiteId(0);
		//	 cs.setSpiderSiteId(2);
		java.sql.Timestamp t = new java.sql.Timestamp(System.currentTimeMillis());
		cs.setCreateTime(t);
		cs.setUpdateTime(t);
		custSiteService.addSite(cs);

		//		//新增完后跳转到主页面开始
		CustSiteQueryBean csqb = new CustSiteQueryBean();
		try {
			PageInfoBean<CustSiteBean> sitePageInfo = custSiteService.querySite(csqb);//分页查询结果
			//			queryCustDataList = custDataCategoryService.queryCustDataBeanListByCondition(query);
			List<SiteView> siteView = ListBuilder.newArrayList();
			CustDataCategoryQueryBean cdc = new CustDataCategoryQueryBean();
			for (CustSiteBean site : sitePageInfo.getData()) {
				cdc.setType(3);
				cdc.setCustId(CustId);
				cdc.setDataId(site.getId());
				List<CustDataCategoryBean> categoryList = custDataCategoryService.queryCustDataCategoryBeanListByDataIdAndType(cdc);//站点分类、类型、来源
				SiteView view = BeanUtil.copy(site, SiteView.class);//将源对象的数据复制到目标对象。site:源,SiteView.class：目标
				String type = "";
				for (CustDataCategoryBean category : categoryList) {
					type += "," + category.getName();
				}
				view.setType(type);
				view.setName(site.getName());
				view.setUrl(site.getUrl());
				siteView.add(view);
			}
			PageInfoBean<SiteView> pageInfo = new PageInfoBean<SiteView>(siteView, sitePageInfo.getTotalRecords(), sitePageInfo.getPageSize(), sitePageInfo.getCurrentPage());
			request.setAttribute("sitePageInfo", pageInfo);
		} catch (CustDataCategoryServiceException e) {
			e.printStackTrace();
		}

		//新增完后重定向到主页面结束
		return "redirect:/setting/querySiteListByPage.htm";
	}

	@RequestMapping(value = { "listSite" })
	public String listSite(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		CustDataQueryBean query = new CustDataQueryBean();

		PageInfoBean<CustSiteBean> pageInfo = null;//site
		SiteView csqy = new SiteView();

		try {
			pageInfo = support.querySiteByPage(csqy, PAGE_NO, PAGE_SIZE);
			List<CustDataBean> queryCustDataList = custDataCategoryService.queryCustDataBeanListByCondition(query);//查询站点分类表-韩先锋

			request.setAttribute("listSite", pageInfo);
			request.setAttribute("queryCustDataList", queryCustDataList);

		} catch (BaseException e) {
			e.printStackTrace();
		}
		return "/localsetting/site/listSite";
	}

	/**
	 * 跳转到站点分页页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "querySiteListByPage" })
	public String querySiteListByPage(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setAttribute("sitePageInfo", querySiteListPageDatas(request));
		} catch (CustDataCategoryServiceException e) {
			e.printStackTrace();
		}
		return "/localsetting/site/listSite";
	}
	/**
	 * 根据条件查询站点分页数据
	 * @param request
	 * @return
	 * @throws CustDataCategoryServiceException
	 */
	@SuppressWarnings("unchecked")
	private PageInfoBean<SiteView> querySiteListPageDatas(HttpServletRequest request) throws CustDataCategoryServiceException{
		CustSiteQueryBean custSiteQueryBean = new CustSiteQueryBean();
		Map<String, String> map = CyyunStringUtils.request2Map(request
				.getParameterMap());
		custSiteQueryBean.setCreaterId(CyyunStringUtils.integerValueOf(map
				.get("inputselect")));
		Integer custId = FMContext.getCurrent().getCustomerId();// 得到CustId
		custSiteQueryBean.setName(map.get("site_name"));
		custSiteQueryBean.setCustId(custId);
		custSiteQueryBean.setPageNo(CyyunStringUtils.integerValueOf(map
				.get("currentPage")));
		PageInfoBean<CustSiteBean> sitePageInfo = custSiteService
				.querySite(custSiteQueryBean);// 分页查询结果
		List<SiteView> siteViewList = ListBuilder.newArrayList();
		CustDataCategoryQueryBean custDataCategoryQueryBean = new CustDataCategoryQueryBean();
		for (CustSiteBean site : sitePageInfo.getData()) {
			custDataCategoryQueryBean.setType(3);
			custDataCategoryQueryBean.setCustId(custId);
			custDataCategoryQueryBean.setDataId(site.getId());// 站点分类的id
			List<CustDataCategoryBean> categoryList = custDataCategoryService
					.queryCustDataCategoryBeanListByDataIdAndType(custDataCategoryQueryBean);// 站点分类、类型、来源
			SiteView siteView = BeanUtil.copy(site, SiteView.class);//将源对象的数据复制到目标对象。site:源,SiteView.class：目标
			if (CollectionUtils.isNotEmpty(categoryList)) {
				String[] types = new String[categoryList.size()];
				for (int i = 0; i < categoryList.size(); i++) {
					types[i] = categoryList.get(i).getName();
				}
				siteView.setType(StringUtils.join(types, ","));
			}
			siteView.setName(site.getName());
			siteView.setUrl(site.getUrl());
			siteView.setMediaType(site.getType());
			siteView.setProcStatusStrAndClassIfication(site.getProcStatus());
			siteViewList.add(siteView);
		}
		PageInfoBean<SiteView> pageInfo = new PageInfoBean<SiteView>(
				siteViewList, sitePageInfo.getTotalRecords(),
				sitePageInfo.getPageSize(), sitePageInfo.getCurrentPage());
		return pageInfo;
	}
	
	public static void main(String[] args) {
		String[] types = new String[]{"1","2","3"};
		System.out.println(StringUtils.join(types, ","));
		String str = "";
		System.out.println(Integer.valueOf(str));
	}
	
	/**
	 * 返回带有数据的站点列表页
	 * @param request
	 * @return
	 */
	@RequestMapping(value={"/forwardListSite"})
	@ResponseBody
	public ModelAndView listSite(HttpServletRequest request){
		try {
			return view("/localsetting/site/sitepageList-list-site").addObject("sitePageInfo", querySiteListPageDatas(request));
		} catch (CustDataCategoryServiceException e) {
			e.printStackTrace();
			return message(MESSAGE_TYPE_ERROR, "加载站点数据失败");
		}
	}

	/**
	 * 站点分类自动补全后台方法
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return 
	 * @return
	 */
	@RequestMapping(value = { "siteAutoComplete" })
	@ResponseBody
	public MessageBean siteAutoComplete(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		String keywords = request.getParameter("keywords");
		out.put("type", 3);//3表示站点的分类
		try {
			CustDataCategoryQueryBean query = new CustDataCategoryQueryBean();
			query.setType(3);

			if (StringUtils.isNotEmpty(keywords)) {
				query.setName(keywords);
			}
			PageInfoBean<CustDataCategoryBean> page = custDataCategoryService.queryCustDataCategoryPageList(query);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "站点分类自动提示成功", page.getData());
		} catch (CustDataCategoryServiceException e) {
			return buildMessage(MESSAGE_TYPE_ERROR, "站点分类自动提示失败");
		}
	}

	/**
	 * ajax查询
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 */
	@RequestMapping(value = { "queryAjax" })
	public String queryAjax(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		String name = request.getParameter("site_name");
		String valueStr = request.getParameter("inputselect");//处理状态
		String Classification = request.getParameter("site_Classification");// 站点分类
		String MediaType = request.getParameter("MediaType");
		
		//当有条件的时候执行的方法
		if(name!="" || valueStr!="" || Classification!="" || MediaType!=""){
			
			if (MediaType == null || MediaType.equals("")) {
				MediaType = "0";
			}
			Integer MediaTypeInt = Integer.valueOf(MediaType);

			CustSiteQueryBean csqb = new CustSiteQueryBean();
			if (valueStr != null && !valueStr.equals("")) {
				int value = Integer.valueOf(valueStr);
				csqb.setProcStatus(value);//处理状态
			}
			Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
			csqb.setName(CyyunSqlUtil.dealSql(name));
			csqb.setCustId(CustId);
			csqb.setMediaType(MediaTypeInt);//媒体类型

			String currentPage = request.getParameter("currentPage");
			if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPage)) {
				csqb.setPageNo(Integer.valueOf(currentPage));
			}
			try {
				PageInfoBean<CustSiteBean> sitePageInfo = custSiteService.querySite(csqb);//分页查询结果
				//queryCustDataList = custDataCategoryService.queryCustDataBeanListByCondition(query);
				List<SiteView> siteView = ListBuilder.newArrayList();
				CustDataCategoryQueryBean cdc = new CustDataCategoryQueryBean();
				for (CustSiteBean site : sitePageInfo.getData()) {
					cdc.setType(3);
					cdc.setCustId(CustId);
					cdc.setDataId(site.getId());
					cdc.setClassification(CyyunSqlUtil.dealSql(Classification));

					List<CustDataCategoryBean> categoryList = custDataCategoryService.queryCustDataCategoryBeanListByDataIdAndType(cdc);//站点分类、类型、来源
					//根据条件查到内容时，返回数据。
					if (categoryList.size() > 0) {
						SiteView view = BeanUtil.copy(site, SiteView.class);//将源对象的数据复制到目标对象。site:源,SiteView.class：目标
						String types = "";
						for (CustDataCategoryBean category : categoryList) {
							types += "，" + category.getName();
						}
						if (types != null && !types.equals("")) {
							types = types.substring(1);
						}

						view.setType(types);
						view.setName(site.getName());
						view.setUrl(site.getUrl());

						view.setMediaType(site.getType());

						if (site.getProcStatus() == 0) {
							view.setProcStatuStr("未处理");
							view.setClassification("删除");
						} else if(site.getProcStatus() == 1){
							view.setProcStatuStr("已处理");
							view.setClassification("");
						}else if(site.getProcStatus() == 2){
							view.setProcStatuStr("处理中");
							view.setClassification("");
						}
						else {
							view.setProcStatuStr("暂不处理");
							view.setClassification("");
						}
						siteView.add(view);
					}else{//没有查询到分类数据时
						//如果有分类查询条件，此处会把空的分类也返回过来，所有需要在下面判断 categoryList.size() > 0
						if(Classification!=null && !Classification.equals("")){
							//只返回符合分类结果的数据
							if (categoryList.size() > 0) {
								SiteView view = BeanUtil.copy(site, SiteView.class);//将源对象的数据复制到目标对象。site:源,SiteView.class：目标
								String types = "";
								for (CustDataCategoryBean category : categoryList) {
									types += "，" + category.getName();
								}
								if (types != null && !types.equals("")) {
									types = types.substring(1);
								}

								view.setType(types);
								view.setName(site.getName());
								view.setUrl(site.getUrl());

								view.setMediaType(site.getType());

								if (site.getProcStatus() == 0) {
									view.setProcStatuStr("未处理");
									view.setClassification("删除");
								} else if(site.getProcStatus() == 1){
									view.setProcStatuStr("已处理");
									view.setClassification("");
								}else if(site.getProcStatus() == 2){
									view.setProcStatuStr("处理中");
									view.setClassification("");
								}
								else {
									view.setProcStatuStr("暂不处理");
									view.setClassification("");
								}
								siteView.add(view);
							}
						}else{
							//如果没有分类查询条件
							SiteView view = BeanUtil.copy(site, SiteView.class);//将源对象的数据复制到目标对象。site:源,SiteView.class：目标
							view.setName(site.getName());
							view.setUrl(site.getUrl());
							view.setMediaType(site.getType());
							if (site.getProcStatus() == 0) {
								view.setProcStatuStr("未处理");
								view.setClassification("删除");
							} else if(site.getProcStatus() == 1){
								view.setProcStatuStr("已处理");
								view.setClassification("");
							}else if(site.getProcStatus() == 2){
								view.setProcStatuStr("处理中");
								view.setClassification("");
							}
							else {
								view.setProcStatuStr("暂不处理");
								view.setClassification("");
							}
							siteView.add(view);
						}
						
					}
				}
				int totalRecords = (Classification.trim().equals("")?sitePageInfo.getTotalRecords():siteView.size());
				PageInfoBean<SiteView> pageInfo = new PageInfoBean<SiteView>(siteView,totalRecords , sitePageInfo.getPageSize(), sitePageInfo.getCurrentPage());
				request.setAttribute("sitePageInfo", pageInfo);
			} catch (CustDataCategoryServiceException e) {
			}
			
		}else{//当所有的条件都为空时，执行的方法。
			CustSiteQueryBean csqb = new CustSiteQueryBean();
			if (valueStr != null && !valueStr.equals("")) {
				int value = Integer.valueOf(valueStr);
				if (value == 3) {
				}
				csqb.setCreaterId(value);//来源：包括系统创建和自定义
			}
			Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
			csqb.setName(name);
			csqb.setCustId(CustId);

			String currentPage = request.getParameter("currentPage");
			if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPage)) {
				csqb.setPageNo(Integer.valueOf(currentPage));
			}
			try {
				PageInfoBean<CustSiteBean> sitePageInfo = custSiteService.querySite(csqb);//分页查询结果
				//				queryCustDataList = custDataCategoryService.queryCustDataBeanListByCondition(query);
				List<SiteView> siteView = ListBuilder.newArrayList();
				CustDataCategoryQueryBean cdc = new CustDataCategoryQueryBean();
				for (CustSiteBean site : sitePageInfo.getData()) {
					cdc.setType(3);
					cdc.setCustId(CustId);
					cdc.setDataId(site.getId());//站点分类的id
					List<CustDataCategoryBean> categoryList = custDataCategoryService.queryCustDataCategoryBeanListByDataIdAndType(cdc);//站点分类、类型、来源
					SiteView view = BeanUtil.copy(site, SiteView.class);//将源对象的数据复制到目标对象。site:源,SiteView.class：目标
					String types = "";
					for (CustDataCategoryBean category : categoryList) {
						types += "，" + category.getName();
						//					view.setSiteType(category.getName());
					}
					if (types != null && !types.equals("")) {
						types = types.substring(1);
					}
					view.setType(types);
					view.setName(site.getName());
					view.setUrl(site.getUrl());
					view.setMediaType(site.getType());
					if (site.getProcStatus() == 0) {
						view.setProcStatuStr("未处理");
						view.setClassification("删除");
					} else if(site.getProcStatus() == 1){
						view.setProcStatuStr("已处理");
						view.setClassification("");
					}else if(site.getProcStatus() == 2){
						view.setProcStatuStr("处理中");
						view.setClassification("");
					}
					else {
						view.setProcStatuStr("暂不处理");
						view.setClassification("");
					}
					
					siteView.add(view);
				}
				int totalRecords = (Classification.trim().equals("")?sitePageInfo.getTotalRecords():siteView.size());
				PageInfoBean<SiteView> pageInfo = new PageInfoBean<SiteView>(siteView,totalRecords , sitePageInfo.getPageSize(), sitePageInfo.getCurrentPage());
				request.setAttribute("sitePageInfo", pageInfo);
			} catch (CustDataCategoryServiceException e) {
				e.printStackTrace();
			}
			
		}
		
		return "/localsetting/site/listSite";
	}

	
	
	
	
	/**
	 * queryAjaxRefresh
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 */
	@RequestMapping(value = { "queryAjaxRefresh" })
	public String queryAjaxRefresh(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		String name = request.getParameter("site_name");
		String valueStr = request.getParameter("inputselect");
		String Classification = request.getParameter("site_Classification");
		String MediaType = request.getParameter("MediaType");
		if (MediaType == null || MediaType.equals("")) {
			MediaType = "0";
		}
		Integer MediaTypeInt = Integer.valueOf(MediaType);

		CustSiteQueryBean csqb = new CustSiteQueryBean();
		if (valueStr != null && !valueStr.equals("")) {
			int value = Integer.valueOf(valueStr);
			csqb.setCreaterId(value);//来源：包括系统创建和自定义
		}
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		csqb.setName(name);
		csqb.setCustId(CustId);
		csqb.setMediaType(MediaTypeInt);//媒体类型

		String currentPage = request.getParameter("currentPage");
		if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPage)) {
			csqb.setPageNo(Integer.valueOf(currentPage));
		}
		try {
			PageInfoBean<CustSiteBean> sitePageInfo = custSiteService.querySite(csqb);//分页查询结果
			//queryCustDataList = custDataCategoryService.queryCustDataBeanListByCondition(query);
			List<SiteView> siteView = ListBuilder.newArrayList();
			CustDataCategoryQueryBean cdc = new CustDataCategoryQueryBean();
			for (CustSiteBean site : sitePageInfo.getData()) {
				cdc.setType(3);
				cdc.setCustId(CustId);
				cdc.setDataId(site.getId());
				cdc.setClassification(Classification);

				List<CustDataCategoryBean> categoryList = custDataCategoryService.queryCustDataCategoryBeanListByDataIdAndType(cdc);//站点分类、类型、来源
				//根据条件查到内容时，返回数据。
					SiteView view = BeanUtil.copy(site, SiteView.class);//将源对象的数据复制到目标对象。site:源,SiteView.class：目标
					String types = "";
					for (CustDataCategoryBean category : categoryList) {
						types += "，" + category.getName();
					}
					if (types != null && !types.equals("")) {
						types = types.substring(1);
					}
					view.setType(types);
					view.setName(site.getName());
					view.setUrl(site.getUrl());
					view.setMediaType(site.getType());
					if (site.getProcStatus() == 0) {
						view.setProcStatuStr("未处理");
						view.setClassification("删除");
					} else if(site.getProcStatus() == 1){
						view.setProcStatuStr("已处理");
						view.setClassification("");
					}else if(site.getProcStatus() == 2){
						view.setProcStatuStr("处理中");
						view.setClassification("");
					}
					else {
						view.setProcStatuStr("暂不处理");
						view.setClassification("");
					}
					siteView.add(view);
			}
			PageInfoBean<SiteView> pageInfo = new PageInfoBean<SiteView>(siteView, sitePageInfo.getTotalRecords(), sitePageInfo.getPageSize(), sitePageInfo.getCurrentPage());
			request.setAttribute("sitePageInfo", pageInfo);
		} catch (CustDataCategoryServiceException e) {
		}
		return "/localsetting/site/sitepageList-paging-site";
	}
	
	
	
	
	
	
	@RequestMapping(value = { "delete" })
	public void delete(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		int a = Integer.valueOf(request.getParameter("id"));
		
		CustSiteQueryBean csqb = new CustSiteQueryBean();
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		csqb.setCustId(CustId);
		PageInfoBean<CustSiteBean> b = custSiteService.querySite(csqb);
		List<CustSiteBean> c = b.getData();
		for (CustSiteBean custSiteBean : c) {
			if(custSiteBean.getId()==a){
				//1.先删除分类表中对应的站点的数据
				try {
					custDataCategoryService.deleteCustDataCategoryByDataId(custSiteBean.getId());
				} catch (CustDataCategoryServiceException e) {
					log.error("删除分类时，删除站点分类的数据失败！", e);
				}
				
			}
		}
		custSiteService.delete(a);
	}

	@RequestMapping(value = { "category" })
	public String category(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws CustDataCategoryServiceException {
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		return "/localsetting/site/Transit";
	}

	@RequestMapping(value = { "setCategory" })
	@ResponseBody
	public MessageBean setCustDataCategoryByDataId(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		Map<String, Object> data = MapBuilder.newHashMap();
		String id = request.getParameter("dataId");
		String type = request.getParameter("type");
		Integer custId = FMContext.getCurrent().getCustomerId();
		try {
			CustDataCategoryQueryBean catQuery = new CustDataCategoryQueryBean();
			catQuery.setCustId(custId); //客户ID
			catQuery.setType(Integer.parseInt(type)); //数据分类类型

			List<CustDataCategoryBean> catList = this.custDataCategoryService.queryCustDataCategoryBeanListByCondition(catQuery);
			data.put("catList", catList);

			CustDataQueryBean custdataQuery = new CustDataQueryBean();
			custdataQuery.setType(Integer.parseInt(type));
			custdataQuery.setDataId(Integer.parseInt(id));
			custdataQuery.setCustId(custId); //客户ID

			List<CustDataBean> custdataList = this.custDataCategoryService.queryCustDataBeanListByCondition(custdataQuery);
			data.put("custdataList", custdataList);

			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功检索网页板块配置", data);
		} catch (Exception e) {
			return buildMessage(MESSAGE_TYPE_ERROR, "检索网页板块配置失败");
		}
	}

	/**
	 * function:提交事件分类设置
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 */
	@RequestMapping(value = { "submitSetCategory" })
	@ResponseBody
	public MessageBean submitCustDataCategoryByDataId(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		String id = request.getParameter("dataId");
		String type = request.getParameter("type");
		String catIds = request.getParameter("catIds");

		Integer custId = FMContext.getCurrent().getCustomerId();
		try {
			this.custDataCategoryService.addCustDataListForDataId(Integer.parseInt(id), Integer.parseInt(type), catIds, custId);

			Map<String, Object> data = MapBuilder.newHashMap();
			CustDataCategoryQueryBean catQuery = new CustDataCategoryQueryBean();
			catQuery.setCustId(custId); //客户ID
			catQuery.setType(Integer.parseInt(type)); //数据分类类型

			List<CustDataCategoryBean> catList = this.custDataCategoryService.queryCustDataCategoryBeanListByCondition(catQuery);
			data.put("catList", catList);

			CustDataQueryBean custdataQuery = new CustDataQueryBean();
			custdataQuery.setType(Integer.parseInt(type));
			custdataQuery.setDataId(Integer.parseInt(id));
			custdataQuery.setCustId(custId); //客户ID

			List<CustDataBean> custdataList = this.custDataCategoryService.queryCustDataBeanListByCondition(custdataQuery);
			data.put("custdataList", custdataList);

			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功检索网页板块配置", data);
		} catch (Exception e) {
			return buildMessage(MESSAGE_TYPE_ERROR, "检索网页板块配置失败");
		}
	}

	/**
	 * 新增站点时查询是否已经存在站点名称或者站点地址
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 */
	@RequestMapping(value = { "queryRepeatSite" })
	@ResponseBody
	public String queryRepeatSite(HttpServletRequest request, HttpServletResponse response, ModelMap out) {

		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		String siteName = request.getParameter("siteName");

		String url = request.getParameter("siteaPlace");

		CustSiteQueryBean csqb = new CustSiteQueryBean();
		csqb.setName(siteName);
		csqb.setCustId(CustId);
		csqb.setUrl(url);

		PageInfoBean<CustSiteBean> sitePageInfo = custSiteService.queryRepeatSite(csqb);//分页查询结果
		
		if (sitePageInfo.getData().size() == 0) {
			return "2";
		} else {
			return "1";
		}
	}

	/**
	 * 新增站点时查询是否已经存在站点名称或者站点地址
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 */
	@RequestMapping(value = { "queryRepeatSiteUrl" })
	@ResponseBody
	public String queryRepeatSiteUrl(HttpServletRequest request, HttpServletResponse response, ModelMap out) {

		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		String siteaPlace = request.getParameter("siteaPlace");

		CustSiteQueryBean csqb = new CustSiteQueryBean();
		csqb.setUrl(siteaPlace);
		csqb.setCustId(CustId);

		PageInfoBean<CustSiteBean> sitePageInfo = custSiteService.queryRepeatSiteUrl(csqb);//分页查询结果
		if (sitePageInfo.getData().size() == 0) {
			return "2";
		} else {
			return "1";
		}
	}

}
