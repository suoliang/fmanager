package com.cyyun.fm.people.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.fm.people.bean.TruePeopleView;
import com.cyyun.fm.service.CustDataCategoryService;
import com.cyyun.fm.service.CustSiteService;
import com.cyyun.fm.service.bean.CustDataBean;
import com.cyyun.fm.service.bean.CustDataCategoryBean;
import com.cyyun.fm.service.bean.CustDataCategoryQueryBean;
import com.cyyun.fm.service.bean.CustDataQueryBean;
import com.cyyun.fm.service.exception.CustDataCategoryServiceException;
import com.cyyun.process.service.PeopleService;
import com.cyyun.process.service.bean.PeopleBean;
import com.cyyun.process.service.bean.PeopleQueryBean;
import com.cyyun.process.service.bean.VirtualIdentityBean;
import com.cyyun.process.service.exception.ServiceExcepiton;

/**
 * <h3>人物真实身份</h3>
 * 
 * @author ZHANGFEI
 * @version 1.0.0
 */
@Controller
@RequestMapping("/people")
public class PeopleTrueIdentityController extends BaseController {
	@Autowired
	private PeopleService peopleService;
	@Autowired
	private CustSiteService custSiteService;
	@Autowired
	private CustDataCategoryService custDataCategoryService;
	/**
	 * 页面初始化方法
	 * @param request
	 * @param response
	 * @return
	 * @throws CustDataCategoryServiceException 
	 */
	@RequestMapping("TrueIdentity")
	public String TrueIdentity(HttpServletRequest request, HttpServletResponse response) throws CustDataCategoryServiceException {
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		List<TruePeopleView> dataview = ListBuilder.newArrayList();
		
		PageInfoBean<TruePeopleView> pageInfo = new PageInfoBean<TruePeopleView>(null, null);
		PageInfoBean<CustDataCategoryBean> pageInfo2 = new PageInfoBean<CustDataCategoryBean>(null, null);
		try {
			PeopleQueryBean queryBean= new PeopleQueryBean();
			queryBean.setOrderByClause("a.create_time desc");
			queryBean.setCustId(FMContext.getCurrent().getCustomerId());//加cid过滤
			String currentPage = request.getParameter("currentPage");
			if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPage)) {
				queryBean.setPageNo(Integer.valueOf(currentPage));
			}
			queryBean.setUserCreateFlag("all");//全部
//			queryBean.setUserCreateFlag("true");//查询自己创建的
//			queryBean.setUserCreateFlag("false");//查询系统创建的
			queryBean.setPageSize(20);
			queryBean.setNeedPaging(true);
			
			PageInfoBean<PeopleBean> data = peopleService.queryPeoplePage(queryBean);
			
			List<PeopleBean> listDate = data.getData();
			
			for (PeopleBean peopleBean : listDate) {
				TruePeopleView view = BeanUtil.copy(peopleBean, TruePeopleView.class);//将源对象的数据复制到目标对象。peopleBean:源,view：目标
				//按真实人物ID查询对应的虚拟人物集合
				List<VirtualIdentityBean> virtualPeoples=peopleService.queryVirtualIdentityByPeopleId(view.getId());
				view.setVirtualListSize(virtualPeoples.size());
				view.setRealName(peopleBean.getRealName());
				view.setKeywords(peopleBean.getKeywords());
				view.setContent(peopleBean.getContent());
				//维护状态状态
				if(peopleBean.getStatus()==1){
					view.setStatus(1);
					view.setStatusName("维护中");
				}else{
					view.setStatus(0);
					view.setStatusName("停止维护");
				}
				//来源
				if(peopleBean.getCreaterId()==1){
					view.setCreaterId(1);
					view.setCreaterIdName("自定义");
				}else{
					view.setCreaterId(0);
					view.setCreaterIdName("系统授权");
				}
				CustDataCategoryQueryBean cdc = new CustDataCategoryQueryBean();
				cdc.setType(2);//2表示人物
				cdc.setDataId(peopleBean.getId());
				cdc.setCustId(CustId);
				
				CustDataCategoryQueryBean cdc2 = new CustDataCategoryQueryBean();
				cdc2.setType(2);//2表示人物
				//TODO
				List<CustDataCategoryBean> categoryList = custDataCategoryService.queryCustDataCategoryBeanListByDataIdAndType(cdc);//站点分类、类型、来源
				List<CustDataCategoryBean> categoryList2 = custDataCategoryService.queryCustDataCategoryBeanListByType(cdc2);//返回所有的人物具体分类
				
				pageInfo2 = new PageInfoBean<CustDataCategoryBean>(categoryList2, data.getTotalRecords(), data.getPageSize(),data.getPageNo());
				
				String types = "";
				for (CustDataCategoryBean custDataCategoryBean : categoryList) {
					types += "，" + custDataCategoryBean.getName();
				}
				if(types!=null && !types.equals("")){
					types = types.substring(1);	
				}
				view.setCategoryName(types);//人物分类名字
				dataview.add(view);
			}
			pageInfo = new PageInfoBean<TruePeopleView>(dataview, data.getTotalRecords(), data.getPageSize(),data.getPageNo());
			request.setAttribute("pageInfo2", pageInfo2);
			request.setAttribute("pageInfo", pageInfo);
		} catch (ServiceExcepiton e) {
			System.out.println("查询真实人物列表失败");
			e.printStackTrace();
		}
		
		return "/people/trueIdentity";
	}
	/**
	 * 新增真实人物跳转的方法
	 * @return
	 */
	@RequestMapping("addTrueIdentityPeopleUrl")
	public String addTrueIdentityPeopleUrl(HttpServletRequest request, HttpServletResponse response) {
		return  "/people/addTruePeople";
	}
	/**
	 * 提交新增真实人物的方法
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws ParseException 
	 */
	@RequestMapping("addTrueIdentityPeople")
	public String addTrueIdentityPeople(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, ParseException {
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		//新增时，传入的参数 ---->开始
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		
		String birthday =request.getParameter("birthday");
		
		String Education=request.getParameter("Education");
		
		String truePeople =request.getParameter("truePeople");
		
		String trueKeyword =request.getParameter("trueKeyword");
		
		String trueDescription = request.getParameter("trueDescription");
		
		String sex = request.getParameter("sex");
		
		String mobile = null;
		if(request.getParameter("mobile")!=null && !request.getParameter("mobile").equals("")){
			mobile=new String(request.getParameter("mobile").getBytes("ISO-8859-1"), "UTF-8");
		}
		String qq = null;
		if(request.getParameter("qq")!=null && !request.getParameter("qq").equals("")){
			qq=new String(request.getParameter("qq").getBytes("ISO-8859-1"), "UTF-8");
		}
		String wx = request.getParameter("wx");//微信
		
		String email = request.getParameter("email");//邮箱
		
		String Occupation=request.getParameter("Occupation");//职业
		//新增时，传入的参数 ---->结束
		
		PeopleBean people = new PeopleBean();
		people.setOwner((short) 1);
		people.setStatus((short) 1);
		people.setCustId(CustId);
		people.setCreaterId(1);
		Date date = new Date();
		people.setCreateTime(date);
		people.setUpdateTime(date);
		
		people.setRealName(truePeople);
		people.setKeywords(trueKeyword);
		people.setContent(trueDescription);
		
		people.setMobile(mobile);//电话
		people.setQq(qq);//qq
		people.setWx(wx);//微信
		people.setEmail(email);//email
		if(Education!=null && !Education.equals("")){
			people.setEducation(Short.valueOf(Education));//学历
		}
		if(birthday!=null && !birthday.equals("")){
			people.setBirthday(sdf.parse(birthday));//生日
		}
		people.setJob(Occupation);//职业
		
		if(sex!=null && sex.equals("1")){
			people.setSex("N");
		}else if(sex!=null && sex.equals("2")){
			people.setSex("M");
		}
		try {
			peopleService.addPeople(people);
		} catch (ServiceExcepiton e) {
			
		}
		
		return "redirect:/people/TrueIdentity.htm";
	}
	
	
	
	
	
	/**
	 * 修改状态
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @throws CustDataCategoryServiceException 
	 */
	@RequestMapping(value = { "updateStatus" })
	public String updateStatus(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws CustDataCategoryServiceException {
		String statuStr =request.getParameter("status");//状态
		String id =request.getParameter("id");//状态
		String pageNoStr =request.getParameter("pageNo");
		PeopleBean people = new PeopleBean();
		people.setId(Integer.valueOf(id));
		people.setStatus(Short.valueOf(statuStr));
		try {
			peopleService.updatePeople(people);
		} catch (ServiceExcepiton e1) {
			System.out.println("改变状态失败");
		};
		String trueIdentity =request.getParameter("trueIdentity");//真实身份
		String status_id =request.getParameter("status_id");//状态
		String create_id =request.getParameter("create_id");//创建者
		String type_id =request.getParameter("type_id");//分类
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		List<TruePeopleView> dataview = ListBuilder.newArrayList();
		PageInfoBean<TruePeopleView> pageInfo = new PageInfoBean<TruePeopleView>(null, null);
		try {
			PeopleQueryBean queryBean= new PeopleQueryBean();
			queryBean.setRealName(trueIdentity);
			queryBean.setOrderByClause("create_time desc");
			
			if (org.apache.commons.lang3.StringUtils.isNotBlank(pageNoStr)) {
				queryBean.setPageNo(Integer.valueOf(pageNoStr));
			}
			//TODO
			queryBean.setPageSize(20);
			queryBean.setNeedPaging(true);
			queryBean.setCustId(FMContext.getCurrent().getCustomerId());//加cid过滤
			
			if(status_id!=null && !status_id.equals("")){
				queryBean.setStatus(Short.valueOf(status_id));
			}
			if(create_id!=null && !create_id.equals("")){
				if(Integer.valueOf(create_id)==0){
					queryBean.setUserCreateFlag("false");
				}if(Integer.valueOf(create_id)==1){
					//自定义
					queryBean.setUserCreateFlag("true");
				}else{
					queryBean.setUserCreateFlag("all");
				}
				queryBean.setCreaterId(Integer.valueOf(create_id));
			}
			
			PageInfoBean<PeopleBean> data = peopleService.queryPeoplePage(queryBean);
			List<PeopleBean> listDate = data.getData();
			CustDataCategoryQueryBean cdc = new CustDataCategoryQueryBean();
			for (PeopleBean peopleBean : listDate) {
				TruePeopleView view = BeanUtil.copy(peopleBean, TruePeopleView.class);
				//按真实人物ID查询对应的虚拟人物集合
				List<VirtualIdentityBean> virtualPeoples=peopleService.queryVirtualIdentityByPeopleId(view.getId());
				view.setVirtualListSize(virtualPeoples.size());
				cdc.setType(2);//2表示人物
				cdc.setDataId(peopleBean.getId());
				cdc.setCustId(CustId);
				if(type_id!=null && !type_id.equals("")){
					cdc.setCateId(Integer.valueOf(type_id));
				}
				List<CustDataCategoryBean> categoryList = custDataCategoryService.queryCustDataCategoryBeanListByDataIdAndType(cdc);//站点分类、类型、来源
				if(categoryList.size()>0){
					view.setId(peopleBean.getId());
					view.setRealName(peopleBean.getRealName());
					view.setKeywords(peopleBean.getKeywords());
					view.setContent(peopleBean.getContent());
					//维护状态状态
					if(peopleBean.getStatus()==1){
						view.setStatus(1);
						view.setStatusName("维护中");
					}else{
						view.setStatus(0);
						view.setStatusName("停止维护");
					}
					//来源
					if(peopleBean.getCreaterId()==1){
						view.setCreaterId(1);
						view.setCreaterIdName("自定义");
					}else{
						view.setCreaterId(0);
						view.setCreaterIdName("系统授权");
					}
					String types = "";
					for (CustDataCategoryBean custDataCategoryBean : categoryList) {
						types += "，" + custDataCategoryBean.getName();
					}
					if (types != null && !types.equals("")) {
						types = types.substring(1);
					}
					view.setCategoryName(types);//人物分类名字
					dataview.add(view);
				}else{
					view.setId(peopleBean.getId());
					view.setRealName(peopleBean.getRealName());
					view.setKeywords(peopleBean.getKeywords());
					view.setContent(peopleBean.getContent());
					//维护状态状态
					if(peopleBean.getStatus()==1){
						view.setStatus(1);
						view.setStatusName("维护中");
					}else{
						view.setStatus(0);
						view.setStatusName("停止维护");
					}
					//来源
					if(peopleBean.getCreaterId()==1){
						view.setCreaterId(1);
						view.setCreaterIdName("自定义");
					}else{
						view.setCreaterId(0);
						view.setCreaterIdName("系统授权");
					}
					dataview.add(view);
				}
			}
			pageInfo = new PageInfoBean<TruePeopleView>(dataview, data.getTotalRecords(), data.getPageSize(),data.getPageNo());
			request.setAttribute("pageInfo", pageInfo);
		} catch (ServiceExcepiton e) {
			System.out.println("查询真实人物列表失败");
			e.printStackTrace();
		}
		return "/people/trueIdentity-paging-truepeople";
	}
	
	
	
	/**
	 * ajax查询
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @throws CustDataCategoryServiceException 
	 */
	@RequestMapping(value = { "queryAjax" })
	public String queryAjax(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws CustDataCategoryServiceException {
		
		String trueIdentity =request.getParameter("trueIdentity");//真实身份
		String status_id =request.getParameter("status_id");//状态
		String create_id =request.getParameter("create_id");//来源
		String type_id =request.getParameter("type_id");//分类
		//TODO
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		List<TruePeopleView> dataview = ListBuilder.newArrayList();//作用:格式化数据后返回到前台
		PageInfoBean<TruePeopleView> pageInfo = new PageInfoBean<TruePeopleView>(null, null);
		try {
			PeopleQueryBean queryBean= new PeopleQueryBean();
			queryBean.setRealName(CyyunSqlUtil.dealSql(trueIdentity));
			queryBean.setOrderByClause("create_time desc");
			String currentPage = request.getParameter("currentPage");
			queryBean.setCustId(FMContext.getCurrent().getCustomerId());//加cid过滤
			if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPage)) {
				queryBean.setPageNo(Integer.valueOf(currentPage));
			}
			queryBean.setPageSize(20);
			queryBean.setNeedPaging(true);
			if(type_id!=null && !type_id.equals("")){
				queryBean.setCategoryId(Integer.valueOf(type_id));
			}
			//状态
			if(status_id!=null && !status_id.equals("")){
				queryBean.setStatus(Short.valueOf(status_id));
			}
			if(create_id!=null && !create_id.equals("")){
				if(Integer.valueOf(create_id)==0){
					queryBean.setUserCreateFlag("false");
//					list.add(32);
//					queryBean.setPeopleIds(list);
				}
				else if(Integer.valueOf(create_id)==1){
					queryBean.setUserCreateFlag("true");
				}else{
					queryBean.setUserCreateFlag("all");
				}
				queryBean.setCreaterId(Integer.valueOf(create_id));
			}
			//查询出对应的分类的人物ID
			CustDataQueryBean custDataBean=new CustDataQueryBean();
			custDataBean.setCustId(CustId);
			if(type_id!=null && !type_id.equals("")){
				custDataBean.setCateId(Integer.valueOf(type_id));
			}
			custDataBean.setType(2);
			custDataBean.setNeedPaging(false);
			List<CustDataBean> custDataBeanList = custDataCategoryService.queryCustDataBeanListByCondition(custDataBean);
			List<Integer> CategoryPeopleIds=new ArrayList<Integer>();
			if (custDataBeanList.size()>0) {
				for (CustDataBean cBean : custDataBeanList) {
					CategoryPeopleIds.add(cBean.getDataId());
				}
			}else{//没有人物属于这个分类时
				CategoryPeopleIds.add(-1);//使返回的pageInfo数据为空
			}
			
			
			
			if(type_id!=null && !type_id.equals("")){
				queryBean.setCategoryPeopleIds(CategoryPeopleIds);//分类条件赋值
			}
			PageInfoBean<PeopleBean> data = peopleService.queryPeoplePage(queryBean);
			
			List<PeopleBean> listDate = data.getData();
			CustDataCategoryQueryBean cdc = new CustDataCategoryQueryBean();
			for (PeopleBean peopleBean : listDate) {
				TruePeopleView view = BeanUtil.copy(peopleBean, TruePeopleView.class);//将源对象的数据复制到目标对象。peopleBean:源,view：目标
				
				view.setId(peopleBean.getId());
				view.setRealName(peopleBean.getRealName());
				view.setKeywords(peopleBean.getKeywords());
				view.setContent(peopleBean.getContent());
				//维护状态状态
				if(peopleBean.getStatus()==1){
					view.setStatus(1);
					view.setStatusName("维护中");
				}else{
					view.setStatus(0);
					view.setStatusName("停止维护");
				}
				//来源
				if(peopleBean.getCreaterId()==1){
					view.setCreaterId(1);
					view.setCreaterIdName("自定义");
				}else{
					view.setCreaterId(0);
					view.setCreaterIdName("系统授权");
				}
				cdc.setType(2);//2表示人物
				cdc.setDataId(peopleBean.getId());
				cdc.setCustId(CustId);
				
				if(type_id!=null && !type_id.equals("")){
					cdc.setCateId(Integer.valueOf(type_id));
				}
				
				List<CustDataCategoryBean> categoryList = custDataCategoryService.queryCustDataCategoryBeanListByDataIdAndType(cdc);//站点分类、类型、来源
					String types = "";
					for (CustDataCategoryBean custDataCategoryBean : categoryList) {
						types += "，" + custDataCategoryBean.getName();
					}
					if (types != null && !types.equals("")) {
						types = types.substring(1);
					}
					//按真实人物ID查询对应的虚拟人物集合
					List<VirtualIdentityBean> virtualPeoples=peopleService.queryVirtualIdentityByPeopleId(view.getId());
					view.setVirtualListSize(virtualPeoples.size());
					view.setCategoryName(types);//人物分类名字
					dataview.add(view);
			}
			pageInfo = new PageInfoBean<TruePeopleView>(dataview, data.getTotalRecords(), data.getPageSize(),data.getPageNo());
			request.setAttribute("pageInfo", pageInfo);
		} catch (ServiceExcepiton e) {
			System.out.println("查询真实人物列表失败");
			e.printStackTrace();
		}
		return "/people/trueIdentity-paging-truepeople";
	}
	
	
	/**
	 * 人物分类的方法
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 */
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
	 * function:提交人物分类设置
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
	 * 详细
	 * @return
	 * @throws CustDataCategoryServiceException 
	 */
	@RequestMapping("details")
	public String details(HttpServletRequest request, HttpServletResponse response) throws CustDataCategoryServiceException {
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		String id = request.getParameter("id");
		try {
			PeopleBean data = peopleService.queryPeopleById(Integer.valueOf(id));
			TruePeopleView view = new TruePeopleView();
			view.setId(data.getId());
			view.setRealName(data.getRealName());
			view.setKeywords(data.getKeywords());
			view.setContent(data.getContent());
			view.setBirthday(data.getBirthday());//生日
			view.setJob(data.getJob());//职业
			view.setCreateTime(data.getCreateTime());
			if(data.getSex()!=null && data.getSex().equals("N")){
				view.setSexId(1);
				view.setSex("男");//性别
			}else if(data.getSex()!=null && data.getSex().equals("M")){
				view.setSexId(2);
				view.setSex("女");//性别
			}
			
			
			//学历初始化
			if(data.getEducation()!=null && data.getEducation()==1){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("");
			}else if(data.getEducation()!=null && data.getEducation()==2){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("小学及以下");
			}else if(data.getEducation()!=null && data.getEducation()==3){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("初中");
			}else if(data.getEducation()!=null && data.getEducation()==4){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("高中");
			}else if(data.getEducation()!=null && data.getEducation()==5){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("中专");
			}else if(data.getEducation()!=null && data.getEducation()==6){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("大专");
			}else if(data.getEducation()!=null && data.getEducation()==7){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("本科");
			}else if(data.getEducation()!=null && data.getEducation()==8){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("研究生");
			}else if(data.getEducation()!=null && data.getEducation()==9){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("博士及以上");
			}
			
			//可选择项目初始化
			if(data.getEmail()==null ||data.getEmail().equals("")){
				view.setEmail(" ");
			}else{
				view.setEmail(data.getEmail());
			}
			if(data.getMobile()==null || data.getMobile().equals("")){
				view.setMobile(" ");
			}else{
				view.setMobile(data.getMobile());
			}
			if(data.getQq()==null ||data.getQq().equals("")){
				view.setQq(" ");
			}else{
				view.setQq(data.getQq());
			}
			if(data.getWx()==null ||data.getWx().equals("")){
				data.setWx(" ");
			}else{
				view.setWx(data.getWx());
			}
			
			//维护状态状态
			if(data.getStatus()!=null && data.getStatus()==1){
				view.setStatus(1);
				view.setStatusName("维护中");
			}else{
				view.setStatus(0);
				view.setStatusName("停止维护");
			}
			//来源
			if(data.getCreaterId()==1){
				view.setCreaterId(1);
				view.setCreaterIdName("自定义");
			}else{
				view.setCreaterId(0);
				view.setCreaterIdName("系统授权");
			}
			
			CustDataCategoryQueryBean cdc = new CustDataCategoryQueryBean();
			cdc.setType(2);//2表示人物
			cdc.setDataId(data.getId());
			cdc.setCustId(CustId);
			
			List<CustDataCategoryBean> categoryList = custDataCategoryService.queryCustDataCategoryBeanListByDataIdAndType(cdc);//站点分类、类型、来源
			
			String types = "";
			for (CustDataCategoryBean custDataCategoryBean : categoryList) {
				types += "，" + custDataCategoryBean.getName();
			}
			if(types!=null && !types.equals("")){
				types = types.substring(1);	
			}
			view.setCategoryName(types);//人物分类名字
			
			request.setAttribute("pageInfo", view);
			request.setAttribute("currentPageNo", request.getParameter("pageNo"));
		} catch (ServiceExcepiton e) {
			System.out.println("根据id查询数据失败");
		}
		
		return  "/people/details";
	}
	
	/**
	 * 编辑真实人物
	 * @throws CustDataCategoryServiceException 
	 */
	@RequestMapping("updatePeople")
	public String updatePeople(HttpServletRequest request, HttpServletResponse response) throws CustDataCategoryServiceException {
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		String id = request.getParameter("id");
		try {
			PeopleBean data = peopleService.queryPeopleById(Integer.valueOf(id));
			TruePeopleView view = new TruePeopleView();
			view.setId(data.getId());
			view.setRealName(data.getRealName());
			view.setKeywords(data.getKeywords());
			view.setContent(data.getContent());
			view.setBirthday(data.getBirthday());//生日
			view.setJob(data.getJob());//职业
			view.setCreateTime(data.getCreateTime());
			if(data.getSex()!=null && data.getSex().equals("N")){
				view.setSexId(1);
				view.setSex("男");//性别
			}else if(data.getSex()!=null && data.getSex().equals("M")){
				view.setSexId(2);
				view.setSex("女");//性别
			}
			//学历初始化
			if(data.getEducation()!=null && data.getEducation()==1){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("");
			}else if(data.getEducation()!=null && data.getEducation()==2){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("小学及以下");
			}else if(data.getEducation()!=null && data.getEducation()==3){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("初中");
			}else if(data.getEducation()!=null && data.getEducation()==4){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("高中");
			}else if(data.getEducation()!=null && data.getEducation()==5){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("中专");
			}else if(data.getEducation()!=null && data.getEducation()==6){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("大专");
			}else if(data.getEducation()!=null && data.getEducation()==7){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("本科");
			}else if(data.getEducation()!=null && data.getEducation()==8){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("研究生");
			}else if(data.getEducation()!=null && data.getEducation()==9){
				view.setEducationId(data.getEducation()+0);//学历id
				view.setEducation("博士及以上");
			}
			//可选择项目初始化
			if(data.getEmail()==null ||data.getEmail().equals("")){
				view.setEmail(" ");
			}else{
				view.setEmail(data.getEmail());
			}
			if(data.getMobile()==null || data.getMobile().equals("")){
				view.setMobile(" ");
			}else{
				view.setMobile(data.getMobile());
			}
			if(data.getQq()==null ||data.getQq().equals("")){
				view.setQq(" ");
			}else{
				view.setQq(data.getQq());
			}
			if(data.getWx()==null ||data.getWx().equals("")){
				data.setWx(" ");
			}else{
				view.setWx(data.getWx());
			}
			//维护状态状态
			if(data.getStatus()==1){
				view.setStatus(1);
				view.setStatusName("维护中");
			}else{
				view.setStatus(0);
				view.setStatusName("停止维护");
			}
			//来源
			if(data.getCreaterId()==1){
				view.setCreaterId(1);
				view.setCreaterIdName("自定义");
			}else{
				view.setCreaterId(0);
				view.setCreaterIdName("系统授权");
			}
			CustDataCategoryQueryBean cdc = new CustDataCategoryQueryBean();
			cdc.setType(2);//2表示人物
			cdc.setDataId(data.getId());
			cdc.setCustId(CustId);
			List<CustDataCategoryBean> categoryList = custDataCategoryService.queryCustDataCategoryBeanListByDataIdAndType(cdc);//站点分类、类型、来源
			String types = "";
			for (CustDataCategoryBean custDataCategoryBean : categoryList) {
				types += "，" + custDataCategoryBean.getName();
			}
			if(types!=null && !types.equals("")){
				types = types.substring(1);	
			}
			view.setCategoryName(types);//人物分类名字
			request.setAttribute("pageInfo", view);
		} catch (ServiceExcepiton e) {
			System.out.println("根据id查询数据失败");
		}
		return  "/people/updateTruePeople";
	}
	 
	@RequestMapping("Edit")
	public String Edit(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, ParseException {
		Integer CustId = FMContext.getCurrent().getCustomerId();//得到CustId
		String id=request.getParameter("id");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String birthday =request.getParameter("birthday");//生日
		String Education=request.getParameter("Education");//学历
		String truePeople =request.getParameter("truePeople");//真实人物
		String trueKeyword =request.getParameter("trueKeyword");//关键字
		String trueDescription = request.getParameter("trueDescription");//描述
		String sex = request.getParameter("sex");//性别
		String mobile = null;//电话
		if(request.getParameter("mobile")!=null && !request.getParameter("mobile").equals("")){
			mobile=new String(request.getParameter("mobile").getBytes("ISO-8859-1"), "UTF-8");
		}
		String qq = null;//QQ
		if(request.getParameter("qq")!=null && !request.getParameter("qq").equals("")){
			qq=new String(request.getParameter("qq").getBytes("ISO-8859-1"), "UTF-8");
		}
		String wx = request.getParameter("wx");//微信
		String email = request.getParameter("email");//邮箱
		String Occupation=request.getParameter("Occupation");//职业
		PeopleBean people = new PeopleBean();
		people.setId(Integer.valueOf(id));//id,必填
		people.setOwner((short) 1);
		people.setStatus((short) 1);
		people.setCustId(CustId);
		people.setCreaterId(1);
		Date date = new Date();
		people.setCreateTime(date);
		people.setUpdateTime(date);
		//必填项
		people.setRealName(truePeople);//真实姓名
		people.setKeywords(trueKeyword);//关键字
		people.setContent(trueDescription);//简介
		//可选择项
		people.setMobile(mobile);//电话
		people.setQq(qq);//qq
		people.setWx(wx);//微信
		people.setEmail(email);//email
		if(Education!=null && !Education.equals("")){
			people.setEducation(Short.valueOf(Education));//学历
		}
		if(birthday!=null && !birthday.equals("")){
			people.setBirthday(sdf.parse(birthday));//生日
		}
		people.setJob(Occupation);//职业
		if(sex.equals("2")){
			people.setSex("M");//性别,女
		}else if(sex.equals("1")){
			people.setSex("N");//性别,男
		}
		//设置新增时需要的参数---->结束
		try {
			peopleService.updatePeople(people);
		} catch (ServiceExcepiton e) {
			System.out.println("详情编辑失败");
		}
		return "redirect:/people/TrueIdentity.htm";
	}
	
}