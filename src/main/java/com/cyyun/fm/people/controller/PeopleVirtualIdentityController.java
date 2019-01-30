package com.cyyun.fm.people.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.fm.people.bean.virtualView;
import com.cyyun.fm.service.CustSiteService;
import com.cyyun.fm.service.bean.CustSiteBean;
import com.cyyun.fm.service.bean.CustSiteQueryBean;
import com.cyyun.process.service.PeopleService;
import com.cyyun.process.service.bean.VirtualIdentityBean;
import com.cyyun.process.service.bean.VirtualIdentityQueryBean;
import com.cyyun.process.service.exception.ServiceExcepiton;

/**
 * <h3>人物虚拟身份</h3>
 * 
 * @author ZHANGFEI
 * @version 1.0.0
 */
@Controller
@RequestMapping("/people")
public class PeopleVirtualIdentityController extends BaseController {
	@Autowired
	private PeopleService peopleService;
	@Autowired
	private CustSiteService custSiteService;
	/**
	 * 页面初始化方法
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("VirtualIdentity")
	public String VirtualIdentity(HttpServletRequest request, HttpServletResponse response) {
		VirtualIdentityQueryBean virtualPeopleQuery = new VirtualIdentityQueryBean();
		PageInfoBean<virtualView> pageInfo = new PageInfoBean<virtualView>(null, null);
		PageInfoBean<CustSiteBean> virtualSelectSite = new PageInfoBean<CustSiteBean>(null, null);
		try {
			//排序
			virtualPeopleQuery.setOrderByClause("create_time desc");
			//当前页
			String currentPage = request.getParameter("currentPage");
			if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPage)) {
				virtualPeopleQuery.setPageNo(Integer.valueOf(currentPage));
			}
			//每页显示条数
			virtualPeopleQuery.setPageSize(20);
			virtualPeopleQuery.setCustId(FMContext.getCurrent().getCustomerId());//加cid过滤
			virtualPeopleQuery.setUserCreateFlag("all");
			//是否分页
			virtualPeopleQuery.setNeedPaging(true);
			PageInfoBean<VirtualIdentityBean> virtualPeople = peopleService.queryVirtualIdentityPage(virtualPeopleQuery);
			if(currentPage!=null && !currentPage.equals("")){
				virtualPeople.setPageNo(Integer.valueOf(currentPage));
			}
			List<VirtualIdentityBean> data = virtualPeople.getData();
			List<virtualView> dataview = ListBuilder.newArrayList();
			for (VirtualIdentityBean virtualIdentityBean : data) {
				virtualView view = BeanUtil.copy(virtualIdentityBean, virtualView.class);
				CustSiteQueryBean csqb = new CustSiteQueryBean();
				csqb.setId(virtualIdentityBean.getSiteId());//站点ID
				Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
				csqb.setCustId(CustId);
				PageInfoBean<CustSiteBean> virtualSite = custSiteService.querySite(csqb);//查询一条数据中的站点属性
				List<CustSiteBean> virtualSiteData = virtualSite.getData();
				view.setId(virtualIdentityBean.getId());//虚拟人物id
				for (CustSiteBean custSiteBean : virtualSiteData) {
					view.setSiteIdName(custSiteBean.getName());//通过id将站点记录查询出来
					view.setMediaType(custSiteBean.getType());//媒体类型
				}
				view.setNickname(virtualIdentityBean.getNickname());//昵称
				view.setContent(virtualIdentityBean.getContent());//描述
				view.setSiteId(virtualIdentityBean.getSiteId());
				
				if (virtualIdentityBean.getStatus() == 0) {
					view.setStatus("停止维护");
					view.setStatusId(0);
				} else {
					view.setStatus("维护中");
					view.setStatusId(1);
				}
				if (virtualIdentityBean.getCreaterId() == 0) {
					view.setCreaterId("系统授权");
					view.setOperationas("");
				} else {
					view.setCreaterId("自定义");
					view.setOperationas("修改");
				}
				dataview.add(view);//返回数据
			}
			pageInfo = new PageInfoBean<virtualView>(dataview, virtualPeople.getTotalRecords(), virtualPeople.getPageSize(),virtualPeople.getPageNo());
			CustSiteQueryBean csqb = new CustSiteQueryBean();
			Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
			csqb.setCustId(CustId);
			virtualSelectSite = custSiteService.querySite(csqb);//查询所有站点名字，在页面的选择站点中使用。
			
		} catch (ServiceExcepiton e) {
			System.out.println("查询任务虚拟身份失败！");
		}
		request.setAttribute("queryRestList", virtualSelectSite.getData());
		request.setAttribute("queryRest", pageInfo);
		return "/people/index";
	}
	/**
	 * ajax查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("VirtualIdentityAjax")
	public ModelAndView VirtualIdentityAjax(HttpServletRequest request, HttpServletResponse response) {
		VirtualIdentityQueryBean virtualPeopleQuery = new VirtualIdentityQueryBean();
		
		String currentPage = request.getParameter("currentPage");//当前页
		String virtualIdentityQuery = request.getParameter("virtualIdentityQuery");
		String virtualSelectSiteQuery = request.getParameter("virtualSelectSiteQuery");
		String status_id = request.getParameter("status_id");//维护状态标识
		String create_id = request.getParameter("create_id");//来源
		//来源
		if(create_id!=null && !create_id.equals("")){
			if(Integer.valueOf(create_id)==0){
				virtualPeopleQuery.setUserCreateFlag("false");
//				list.add(44);
//				list.add(55);
//				list.add(64);
//				list.add(40);
//				virtualPeopleQuery.setIds(list);
			}
			else if(Integer.valueOf(create_id)==1){
				virtualPeopleQuery.setUserCreateFlag("true");
			}else{
				virtualPeopleQuery.setUserCreateFlag("all");
			}
			virtualPeopleQuery.setCreaterId(Integer.valueOf(create_id));
		}
		//状态
		if(status_id!=null && !status_id.equals("")){
			virtualPeopleQuery.setStatus(Short.valueOf(status_id));
		}
		//选择站点查询参数
		if(virtualSelectSiteQuery!=null && !virtualSelectSiteQuery.equals("")){
			virtualPeopleQuery.setSiteId(Integer.valueOf(virtualSelectSiteQuery));
		}
		virtualPeopleQuery.setNickname(CyyunSqlUtil.dealSql(virtualIdentityQuery));
		virtualPeopleQuery.setOrderByClause("create_time desc");
		if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPage)) {
			virtualPeopleQuery.setPageNo(Integer.valueOf(currentPage));
		}
		virtualPeopleQuery.setPageSize(20);
		virtualPeopleQuery.setNeedPaging(true);
		virtualPeopleQuery.setCustId(FMContext.getCurrent().getCustomerId());//加cid过滤
		PageInfoBean<virtualView> pageInfo = new PageInfoBean<virtualView>(null, null);
		try {
			PageInfoBean<VirtualIdentityBean> virtualPeople = peopleService.queryVirtualIdentityPage(virtualPeopleQuery);
			
			if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPage)) {
				virtualPeople.setPageNo(Integer.valueOf(currentPage));
			}
			List<VirtualIdentityBean> data = virtualPeople.getData();
			List<virtualView> dataview = ListBuilder.newArrayList();
			for (VirtualIdentityBean virtualIdentityBean : data) {
				virtualView view = BeanUtil.copy(virtualIdentityBean, virtualView.class);
				CustSiteQueryBean csqb = new CustSiteQueryBean();
				csqb.setId(virtualIdentityBean.getSiteId());//站点ID
				Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
				csqb.setCustId(CustId);
				PageInfoBean<CustSiteBean> virtualSite = custSiteService.querySite(csqb);//分类查询站点
				List<CustSiteBean> virtualSiteData = virtualSite.getData();
				for (CustSiteBean custSiteBean : virtualSiteData) {
					view.setSiteIdName(custSiteBean.getName());
					view.setMediaType(custSiteBean.getType());//媒体类型
				}
				view.setNickname(virtualIdentityBean.getNickname());//昵称
				view.setContent(virtualIdentityBean.getContent());//描述
				view.setSiteId(virtualIdentityBean.getSiteId());
				
				if (virtualIdentityBean.getStatus() == 0) {
					view.setStatus("停止维护");
					view.setStatusId(0);
				} else {
					view.setStatus("维护中");
					view.setStatusId(1);
				}
				if (virtualIdentityBean.getCreaterId() == 0) {
					view.setCreaterId("系统授权");
					view.setOperationas("");
					
				} else {
					view.setCreaterId("自定义");
					view.setOperationas("修改");
					
				}
				dataview.add(view);
			}
			pageInfo = new PageInfoBean<virtualView>(dataview,virtualPeople.getTotalRecords(),virtualPeople.getPageSize(),virtualPeople.getPageNo());
		} catch (ServiceExcepiton e) {
			System.out.println("查询任务虚拟身份失败！");
		}
		return view("people/index-paging-virtualpeople").addObject("queryRest", pageInfo);
	}

	/**
	 * 新增虚拟人物跳转的方法
	 * @return
	 */
	@RequestMapping("addVirtualIdentityPeopleUrl")
	public ModelAndView addVirtualIdentityPeopleUrl(HttpServletRequest request, HttpServletResponse response) {
		//返回数据库得到的所有站点id
		PageInfoBean<virtualView> pageInfo = new PageInfoBean<virtualView>(null, null);
		CustSiteQueryBean csqb = new CustSiteQueryBean();
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		csqb.setCustId(CustId);

		String currentPage = request.getParameter("currentPage");
		if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPage)) {
			csqb.setPageNo(Integer.valueOf(currentPage));
		}
		PageInfoBean<CustSiteBean> virtualPeople = custSiteService.querySite(csqb);//站点
		List<CustSiteBean> data = virtualPeople.getData();
		List<virtualView> dataview = ListBuilder.newArrayList();
		for (CustSiteBean custSiteBean : data) {
			virtualView view = BeanUtil.copy(custSiteBean, virtualView.class);
			view.setSiteId(custSiteBean.getId());//返回站点id
			view.setSiteIdName(custSiteBean.getName());
			dataview.add(view);//添加数据
		}
		pageInfo = new PageInfoBean<virtualView>(dataview, virtualPeople.getTotalRecords(), virtualPeople.getPageSize(),virtualPeople.getPageNo());
		return view("people/addPeople").addObject("queryRest", pageInfo);
	}
	
	/**
	 * 提交新增虚拟人物的方法
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("addVirtualIdentityPeople")
	public String addVirtualIdentityPeople(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		
		String virtualIdentity = null;
		virtualIdentity = new String(request.getParameter("virtualIdentity").getBytes("ISO-8859-1"), "UTF-8");
		String virtualIdentity_site = null;
		virtualIdentity_site = new String(request.getParameter("virtualIdentity_site").getBytes("ISO-8859-1"), "UTF-8");
		String remarks = null;
		remarks = new String(request.getParameter("remarks").getBytes("ISO-8859-1"), "UTF-8");
		VirtualIdentityBean virtual = new VirtualIdentityBean();
		try {
			virtual.setNickname(virtualIdentity);//昵称，不能为空。
			virtual.setSiteId(Integer.valueOf(virtualIdentity_site));//所属站点称，不能为空。
			virtual.setContent(remarks);//描述称，不能为空。

			virtual.setOwner((short) 1);
			virtual.setStatus((short) 1);
			virtual.setPeopleId(0);
			virtual.setCustId(FMContext.getCurrent().getCustomerId());
			virtual.setCreaterId(1);

			Date date = new Date();
			virtual.setCreateTime(date);
			virtual.setUpdateTime(date);

			peopleService.addVirtualIdentity(virtual);
		} catch (ServiceExcepiton e) {
			System.out.println("新增任务虚拟身份失败");
		}


		VirtualIdentityQueryBean virtualPeopleQuery = new VirtualIdentityQueryBean();
		PageInfoBean<virtualView> pageInfo = new PageInfoBean<virtualView>(null, null);
		List<CustSiteBean> virtualSiteData= new ArrayList<CustSiteBean>();
		
		PageInfoBean<CustSiteBean> querySite = new PageInfoBean<CustSiteBean>(null,null);
		
		try {
			virtualPeopleQuery.setOrderByClause("create_time desc");
			virtualPeopleQuery.setNeedPaging(false);
			PageInfoBean<VirtualIdentityBean> virtualPeople = peopleService.queryVirtualIdentityPage(virtualPeopleQuery);
			List<VirtualIdentityBean> data = virtualPeople.getData();

			List<virtualView> dataview = ListBuilder.newArrayList();

			for (VirtualIdentityBean virtualIdentityBean : data) {
				virtualView view = BeanUtil.copy(virtualIdentityBean, virtualView.class);

				CustSiteQueryBean csqb = new CustSiteQueryBean();
				csqb.setId(virtualIdentityBean.getSiteId());//站点ID
				Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
				csqb.setCustId(CustId);
				String currentPage = request.getParameter("currentPage");
				if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPage)) {
					csqb.setPageNo(Integer.valueOf(currentPage));
				}
				PageInfoBean<CustSiteBean> virtualSite = custSiteService.querySite(csqb);//查询站点
				 virtualSiteData = virtualSite.getData();
				for (CustSiteBean custSiteBean : virtualSiteData) {
					view.setSiteIdName(custSiteBean.getName());//通过id将站点记录查询出来
					view.setMediaType(custSiteBean.getType());//媒体类型
				}
				view.setNickname(virtualIdentityBean.getNickname());//昵称
				view.setContent(virtualIdentityBean.getContent());//描述
				view.setSiteId(virtualIdentityBean.getSiteId());
				if (virtualIdentityBean.getStatus() == 0) {
					view.setStatus("停止维护");
					view.setStatusId(0);
				} else {
					view.setStatus("维护中");
					view.setStatusId(1);
				}
				if (virtualIdentityBean.getCreaterId() == 0) {
					view.setCreaterId("系统授权");
					view.setOperationas("");
				} else {
					view.setCreaterId("自定义");
					view.setOperationas("修改");
				}
				dataview.add(view);
			}
			pageInfo = new PageInfoBean<virtualView>(dataview, virtualPeople.getTotalRecords(), virtualPeople.getPageSize(),virtualPeople.getPageNo());
			
			CustSiteQueryBean csqb = new CustSiteQueryBean();
			Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
			csqb.setCustId(CustId);
			String currentPage = request.getParameter("currentPage");
			if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPage)) {
				csqb.setPageNo(Integer.valueOf(currentPage));
			}
		 querySite = custSiteService.querySite(csqb);//查询站点
			
		} catch (ServiceExcepiton e) {
			System.out.println("查询任务虚拟身份失败！");
		}
		request.setAttribute("queryRestList", querySite.getData());
		request.setAttribute("queryRest", pageInfo);
		return "redirect:/people/VirtualIdentity.htm";
	}
	
	/**
	 * 修改虚拟人物跳转的方法
	 * @return
	 * @throws ServiceExcepiton 
	 * @throws NumberFormatException 
	 */
	@RequestMapping("updateVirtualIdentityPeopleUrl")
	public String updateVirtualIdentityPeopleUrl(HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, ServiceExcepiton {
		String id = request.getParameter("id");//所修改的id
		String currentPage = request.getParameter("currentPage");
		//根据id去查虚拟人物表的数据
		VirtualIdentityBean viewVirtual = peopleService.queryVirtualIdentityById(Integer.valueOf(id));
		List<virtualView> dataview = ListBuilder.newArrayList();//作用:格式化数据后返回到前台
		CustSiteQueryBean csqb = new CustSiteQueryBean();
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		
			virtualView view = BeanUtil.copy(viewVirtual, virtualView.class);
			//返回到修改页面的返回数据。
			view.setId(Integer.valueOf(id));//返回需要修改的数据id
			view.setNickname(viewVirtual.getNickname());//虚拟名字
			view.setSiteId(viewVirtual.getSiteId());//对应的站点id
			view.setContent(viewVirtual.getContent());//描述
			if(currentPage!=null && !currentPage.equals("")){
				view.setCurrentPage(Integer.valueOf(currentPage));	
			}
			dataview.add(view);//添加数据
			csqb.setCustId(CustId);
			csqb.setName("");
			PageInfoBean<CustSiteBean> virtualPeople = custSiteService.querySite(csqb);
			List<CustSiteBean> data = virtualPeople.getData();
			List<virtualView> dataview2 = ListBuilder.newArrayList();
			for (CustSiteBean custSiteBean : data) {
					virtualView view2 = new virtualView();
					view2.setSiteId(custSiteBean.getId());//返回站点id
					view2.setSiteIdName(custSiteBean.getName());
					dataview2.add(view2);
			}
		request.setAttribute("queryRest", dataview);
		request.setAttribute("queryRest2", dataview2);	
		return "/people/updatePeople";
	}
	
	/**
	 * 修改虚拟人物的方法
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("updateVirtualIdentityPeople")
	public String updateVirtualIdentityPeople(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		String id =request.getParameter("id");
		String virtualIdentity = null;
		virtualIdentity = new String(request.getParameter("virtualIdentity").getBytes("ISO-8859-1"), "UTF-8");
		String virtualIdentity_site = null;
		virtualIdentity_site = new String(request.getParameter("virtualIdentity_site").getBytes("ISO-8859-1"), "UTF-8");
		String remarks = null;
		remarks = new String(request.getParameter("remarks").getBytes("ISO-8859-1"), "UTF-8");
		VirtualIdentityBean virtual = new VirtualIdentityBean();
		try {
			
			virtual.setId(Integer.valueOf(id));//需要修改的id
			virtual.setNickname(virtualIdentity);//修改昵称
			virtual.setSiteId(Integer.valueOf(virtualIdentity_site));//修改所属站点称。
			virtual.setContent(remarks);//修改描述称

			Date date = new Date();
			virtual.setCreateTime(date);
			virtual.setUpdateTime(date);

			peopleService.updateVirtualIdentity(virtual);
		} catch (ServiceExcepiton e) {
			System.out.println("修改人物虚拟身份失败");
		}
		

		VirtualIdentityQueryBean virtualPeopleQuery = new VirtualIdentityQueryBean();
		PageInfoBean<virtualView> pageInfo = new PageInfoBean<virtualView>(null, null);
		PageInfoBean<CustSiteBean> virtualSelectSite = new PageInfoBean<CustSiteBean>(null, null);
		try {
			//排序
			virtualPeopleQuery.setOrderByClause("create_time desc");
			virtualPeopleQuery.setCustId(FMContext.getCurrent().getCustomerId());//加cid过滤
			virtualPeopleQuery.setUserCreateFlag("all");
			virtualPeopleQuery.setPageSize(20);
			virtualPeopleQuery.setNeedPaging(true);
			PageInfoBean<VirtualIdentityBean> virtualPeople = peopleService.queryVirtualIdentityPage(virtualPeopleQuery);
			List<VirtualIdentityBean> data = virtualPeople.getData();
			
			List<virtualView> dataview = ListBuilder.newArrayList();

			for (VirtualIdentityBean virtualIdentityBean : data) {
				virtualView view = BeanUtil.copy(virtualIdentityBean, virtualView.class);
				CustSiteQueryBean csqb = new CustSiteQueryBean();
				csqb.setId(virtualIdentityBean.getSiteId());//站点ID
				Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
				csqb.setCustId(CustId);
				PageInfoBean<CustSiteBean> virtualSite = custSiteService.querySite(csqb);//分类查询站点
				List<CustSiteBean> virtualSiteData = virtualSite.getData();
				for (CustSiteBean custSiteBean : virtualSiteData) {
					view.setSiteIdName(custSiteBean.getName());//通过id将站点记录查询出来
					view.setMediaType(custSiteBean.getType());//媒体类型
				}
				view.setNickname(virtualIdentityBean.getNickname());//昵称
				view.setContent(virtualIdentityBean.getContent());//描述
				view.setSiteId(virtualIdentityBean.getSiteId());
				if (virtualIdentityBean.getStatus() == 0) {
					view.setStatus("停止维护");
					view.setStatusId(0);
				} else {
					view.setStatus("维护中");
					view.setStatusId(1);
				}
				if (virtualIdentityBean.getCreaterId() == 0) {
					view.setCreaterId("系统授权");
					view.setOperationas("");
				} else {
					view.setCreaterId("自定义");
					view.setOperationas("修改");
				}
				dataview.add(view);
			}
			pageInfo = new PageInfoBean<virtualView>(dataview, virtualPeople.getTotalRecords(), virtualPeople.getPageSize(),virtualPeople.getPageNo());
			
			CustSiteQueryBean csqb = new CustSiteQueryBean();
			Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
			csqb.setCustId(CustId);
			
			virtualSelectSite = custSiteService.querySite(csqb);//查询所有站点名字，在页面的选择站点中使用。
			request.setAttribute("queryRestList", virtualSelectSite.getData());
			request.setAttribute("queryRest", pageInfo);
			
		} catch (ServiceExcepiton e) {
			System.out.println("查询任务虚拟身份失败！");
		}
		return "/people/index";
	}
	
	
	
	/**
	 * ajax查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("updateStatusAjax")
	public ModelAndView updateStatusAjax(HttpServletRequest request, HttpServletResponse response) {
		String currentPageStatus = request.getParameter("currentPageStatus");//当前页
		String virtualIdentityQuery = request.getParameter("virtualIdentityQuery");//虚拟人物查询参数
		String virtualSelectSiteQuery = request.getParameter("virtualSelectSiteQuery");//选择站点查询参数
		String create_id = request.getParameter("create_id");//来源
		String statusUpDataQuery = request.getParameter("statusQuery_id");//查询维护状态
		String id = request.getParameter("id");//需要修改的行
		String status_id=request.getParameter("status_id");//最终的状态id

		PageInfoBean<virtualView> pageInfo = new PageInfoBean<virtualView>(null, null);
		VirtualIdentityBean virtual = new VirtualIdentityBean();
		VirtualIdentityQueryBean virtualPeopleQuery = new VirtualIdentityQueryBean();
		
		virtual.setId(Integer.valueOf(id));
		if(status_id!=null && !status_id.equals("")){
			virtual.setStatus(Short.valueOf(status_id));
		}
		
		try {
			//修改状态  
			peopleService.updateVirtualIdentity(virtual);
			virtualPeopleQuery.setOrderByClause("create_time desc");
			virtualPeopleQuery.setUserCreateFlag("all");
			virtualPeopleQuery.setCustId(FMContext.getCurrent().getCustomerId());//加cid过滤
			if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPageStatus)) {
				virtualPeopleQuery.setPageNo(Integer.valueOf(currentPageStatus));
			}
			virtualPeopleQuery.setPageSize(20);
			virtualPeopleQuery.setNeedPaging(true);
			
			virtualPeopleQuery.setNickname(virtualIdentityQuery);//加入查询条件-->昵称
			if(virtualSelectSiteQuery!=null && !virtualSelectSiteQuery.equals("")){
				virtualPeopleQuery.setSiteId(Integer.valueOf(virtualSelectSiteQuery));
			}
			//加入查询条件-->来源
			if(statusUpDataQuery!=null && !statusUpDataQuery.equals("")){
				virtualPeopleQuery.setStatus(Short.valueOf(statusUpDataQuery));
			}
			//加入查询条件-->来源
			if(create_id!=null && !create_id.equals("")){
				virtualPeopleQuery.setCreaterId(Integer.valueOf(create_id));
			}
			
			PageInfoBean<VirtualIdentityBean> virtualPeople = peopleService.queryVirtualIdentityPage(virtualPeopleQuery);
			List<VirtualIdentityBean> data = virtualPeople.getData();
			List<virtualView> dataview = ListBuilder.newArrayList();
			virtualPeople.setPageNo(Integer.valueOf(currentPageStatus));
			
			for (VirtualIdentityBean virtualIdentityBean : data) {
				virtualView view = BeanUtil.copy(virtualIdentityBean, virtualView.class);

				CustSiteQueryBean csqb = new CustSiteQueryBean();
				csqb.setId(virtualIdentityBean.getSiteId());//站点ID
				Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
				csqb.setCustId(CustId);
				PageInfoBean<CustSiteBean> virtualSite = custSiteService.querySite(csqb);//分类查询站点
				List<CustSiteBean> virtualSiteData = virtualSite.getData();
				for (CustSiteBean custSiteBean : virtualSiteData) {
					view.setSiteIdName(custSiteBean.getName());//通过id将站点记录查询出来
					view.setMediaType(custSiteBean.getType());//媒体类型
				}
				view.setNickname(virtualIdentityBean.getNickname());//昵称
				view.setContent(virtualIdentityBean.getContent());//描述
				view.setSiteId(virtualIdentityBean.getSiteId());
				
				if (virtualIdentityBean.getStatus() == 0) {
					view.setStatus("停止维护");
					view.setStatusId(0);
				} else {
					view.setStatus("维护中");
					view.setStatusId(1);
				}
				if (virtualIdentityBean.getCreaterId() == 0) {
					view.setCreaterId("系统授权");
					view.setOperationas("");
				} else {
					view.setCreaterId("自定义");
					view.setOperationas("修改");
				}
				dataview.add(view);
			}
			pageInfo = new PageInfoBean<virtualView>(dataview, virtualPeople.getTotalRecords(), virtualPeople.getPageSize(),virtualPeople.getPageNo());
		} catch (ServiceExcepiton e) {
			System.out.println("修改维护状态失败！");
		}
		return view("people/index-paging-virtualpeople").addObject("queryRest", pageInfo);
	}
	/**
	 * 根据昵称查询
	 * @param virtualIdentityBean
	 * @return
	 */
	@RequestMapping("nicknameCheck")
	@ResponseBody
	public MessageBean boundTruePeople(VirtualIdentityBean virtualIdentityBean) {
		String Nickname = null;
		try {
			Nickname = new String(virtualIdentityBean.getNickname().getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		VirtualIdentityQueryBean virtualPeopleQuery = new VirtualIdentityQueryBean(); 
		virtualPeopleQuery.setNeedPaging(false);
		virtualPeopleQuery.setUserCreateFlag("all");
		virtualPeopleQuery.setCustId(FMContext.getCurrent().getCustomerId());//加cid过滤
		virtualPeopleQuery.setNickname(Nickname);
		try {																
			PageInfoBean<VirtualIdentityBean> virtualPeople = peopleService.queryVirtualIdentityPage(virtualPeopleQuery);
			Integer countNickname=virtualPeople.getData().size();
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", countNickname);
		} catch (Exception e) {
			return buildMessage(MESSAGE_TYPE_ERROR, "失败");
		}
	}
	
}