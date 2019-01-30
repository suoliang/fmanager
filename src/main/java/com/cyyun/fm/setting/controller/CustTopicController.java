/**
 *
 */
package com.cyyun.fm.setting.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.cyyun.authim.service.UserService;
import com.cyyun.authim.service.bean.UserBean;
import com.cyyun.authim.shiro.AuthUtil;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.constant.FMConstant.CUST_TOPIC_IS_ENABLE;
import com.cyyun.base.constant.PermissionCode;
import com.cyyun.base.exception.ErrorMessage;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.CustTopicKeywordService;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.KeywordService;
import com.cyyun.base.service.SpiderService;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.CustTopicKeywordBean;
import com.cyyun.base.service.bean.KeywordBean;
import com.cyyun.fm.service.FmTaskService;
import com.cyyun.fm.service.bean.TaskBean;
import com.cyyun.fm.service.bean.TaskQueryBean;
import com.cyyun.fm.service.exception.TaskServiceException;
import com.cyyun.base.service.bean.query.CustTopicQueryBean;
import com.cyyun.base.service.exception.CustTopicKeywordServiceException;
import com.cyyun.base.service.exception.CustTopicServiceException;
import com.cyyun.base.service.exception.KeywordServiceException;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.base.util.CyyunStringUtils;
import com.cyyun.base.utils.KeywordUtils;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.base.BaseException;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.JsonUtil;
import com.cyyun.common.core.util.SetBuilder;
import com.cyyun.customer.constant.CustTopicStatus;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.setting.bean.CustTopicInitStatusBean;
import com.cyyun.fm.setting.bean.CustTopicInitTaskBean;
import com.cyyun.fm.setting.bean.KeywordView;

/**
 * @author cyyun
 *
 */
@Controller
@RequestMapping("/custtopic")
public class CustTopicController extends BaseController {

	@Autowired
	private FmTaskService fmTaskService;
	
	@Autowired
	private CustTopicService custTopicService;
	@Autowired
	private KeywordService keywordService;
	@Autowired
	private CustTopicKeywordService custTopicKeywordService;

	@Autowired
	private UserService userService;
	@Autowired
	private CustomerConfigService customerConfigService;

	@Autowired
	private SpiderService spiderService;

	@Autowired
	private CustTopicSupport support;
	
	@Value("${url.interface.custTopicInit}")
	private String custTopicInitInterfaceUrl;

	@Value("${topic.keyword.default.gid}")
	private Integer defGid;

	@RequestMapping(value = { "parseKeyword" })
	public ModelAndView parseKeyword(String keyword) {
		try {
			List<KeywordView> keywords = support.parseInput(keyword);
			return view("/localsetting/custTopic-keyword-list").addObject("keywords", keywords);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "解析关键字失败");
		}
	}

	@RequestMapping(value = { "saveCustTopic" })
	@ResponseBody
	public MessageBean saveCustTopic(CustTopicBean custTopic, String[] keywords) {
		try {
			String include_kw = custTopic.getKeyword();
			String exclude_kw = custTopic.getExcludedKeyword();

			String regex = "^[a-zA-Z0-9_ \\;(|)\\.\u4e00-\u9fa5]{0,}$";
			boolean include_verify = Pattern.matches(regex, include_kw);
			boolean exclude_verify = Pattern.matches(regex, exclude_kw);

			if (!include_verify || !exclude_verify) {
				return buildMessage(MESSAGE_TYPE_ERROR, "关键词不能包含特殊字符");
			}

			custTopic = support.saveCustTopic(custTopic, keywords);

			return buildMessage(MESSAGE_TYPE_SUCCESS, "保存专题成功", custTopic);
		} catch (ErrorMessage e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "保存专题失败");
		}
	}

	@RequestMapping(value = { "inputCustTopic" })
	public ModelAndView inputCustTopic(Integer id) {
		try {
			ModelAndView view = view("/localsetting/custTopic-input");
			if (id != null) {
				view.addObject("addTitle", "专题详情");
				CustTopicBean custTopic = this.custTopicService.queryCustTopic(id);
				//查询父类  
				if (custTopic.getParentId() > 0) {
					custTopic.setParentBean(this.custTopicService.queryCustTopic(custTopic.getParentId()));
				}
				if (custTopic != null) {
					List<KeywordView> keywords = support.listCustTopicKeywordView(custTopic.getId());
					view.addObject("keywords", keywords);
					view.addObject("keywordsJson", JsonUtil.toJson(keywords));
				}
				view.addObject("custTopic", custTopic);
			} else {
				view.addObject("addTitle", "新增专题");
			}
			//客户配置
			CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
			view.addObject("autoSpider", custConfig.getAutoSpider());
			return view;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查看专题详情失败");
		}
	}
	
	/**
	 * <h3>检索专题</h3>
	 */
	@RequestMapping("topic/list")
	@ResponseBody
	public MessageBean listTopic(Integer parentId) {
		try {
			List<CustTopicBean> topics = support.listTopic(parentId);
			Integer userId=FMContext.getCurrent().getUserId();
			Integer createId=null;
			for(int i=0;i<topics.size();i++){
				createId=topics.get(i).getCreaterId();
				if(createId.equals(userId)){
					return buildMessage("当前用户存在顶级专题", "成功", topics);
				}
			}
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", topics);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索专题失败");
		}
	}

	

	@RequestMapping(value = { "advanced/list" })
	public ModelAndView listAdvancedCustTopic(String keywords, Integer parentId, Integer currentpage, Integer pagesize) {
		try {
			ModelAndView view = view("/localsetting/custTopic-advanced");
			if (AuthUtil.hasPermission(PermissionCode.DATA_TOPIC_ALL)) {
				view.addObject("isAdmin", true);
			}
			return view.addObject("page", queryCustTopicByPage(keywords, parentId, currentpage, pagesize));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询客户专题失败");
		}
	}

	@RequestMapping(value = { "advanced/inputCustTopic" })
	public ModelAndView inputAdvancedCustTopic(Integer id) {
		try {
			ModelAndView view = view("/localsetting/custTopic-advanced-input");
			if (id != null) {
				view.addObject("addTitle", "编辑客户专题");
				CustTopicBean custTopic = support.queryCustTopic(id);
				//查询父类  
				if (custTopic.getParentId() > 0) {
					custTopic.setParentBean(custTopicService.queryCustTopic(custTopic.getParentId()));
				}
				if (custTopic != null) {
					List<KeywordView> keywords = support.listCustTopicKeywordView(custTopic.getId());
					view.addObject("keywords", keywords);
				}
				view.addObject("custTopic", custTopic);
			} else {
				view.addObject("addTitle", "新增客户专题");
			}
			return view;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "编辑专题失败");
		}
	}

	@RequestMapping(value = { "advanced/saveCustTopic" })
	@ResponseBody
	public MessageBean saveAdvancedCustTopic(CustTopicBean custTopic, String[] keywords) {
		try {
			String include_kw = custTopic.getKeyword();
			String exclude_kw = custTopic.getExcludedKeyword();

			String regex = "^[a-zA-Z0-9_\\;\\|\\+\\-\\(\\)\u4e00-\u9fa5]{0,}$";
			boolean include_verify = Pattern.matches(regex, include_kw);
			boolean exclude_verify = Pattern.matches(regex, exclude_kw);

			if (!include_verify || !exclude_verify) {
				return buildMessage(MESSAGE_TYPE_ERROR, "关键词不能包含特殊字符");
			}

			custTopic = support.saveCustTopic(custTopic, keywords);

			return buildMessage(MESSAGE_TYPE_SUCCESS, "保存专题成功", custTopic);
		} catch (ErrorMessage e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "保存专题失败");
		}
	}
	
	@RequestMapping(value = { "advanced/listPageByAjax" })
	public ModelAndView advancedQueryListPageByAjax(String keywords, Integer parentId, Integer currentpage, Integer pagesize) {
		try {
			ModelAndView view = view("/localsetting/custTopicAdvancedList");
			if (AuthUtil.hasPermission(PermissionCode.DATA_TOPIC_ALL)) {
				view.addObject("isAdmin", true);
			} 
			PageInfoBean<CustTopicBean> page = queryCustTopicByPage(keywords, parentId, currentpage, pagesize);
			List<CustTopicBean> custTopicList = page.getData();
			for (CustTopicBean custTopicBean : custTopicList) {
				if (!("").equals(custTopicBean.getExcludedKeyword()) && custTopicBean.getExcludedKeyword() != null) {
					custTopicBean.setKeyword(custTopicBean.getKeyword() + "(不包含：" + custTopicBean.getExcludedKeyword() + ")");
				}
			}
			page.setData(custTopicList);
			return view.addObject("page", page);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询客户专题失败");
		}
	}
	
	@RequestMapping(value = { "advanced/detail" })
	public String advancedQueryCustTopicById(HttpServletRequest request, HttpServletResponse response, ModelMap out) throws CustTopicKeywordServiceException {
		int id = Integer.parseInt(request.getParameter("id"));
		CustTopicBean vo = this.custTopicService.queryCustTopic(id);
		if(!("").equals(vo.getExcludedKeyword()) && vo.getExcludedKeyword()!=null){
			vo.setKeyword(vo.getKeyword()+"(不包含："+vo.getExcludedKeyword()+")");
		}
		//查询父类
		if (vo.getParentId() > 0) {
			vo.setParentBean(this.custTopicService.queryCustTopic(vo.getParentId()));
		}
		//查询创建人
		UserBean cuser = this.userService.findUser(vo.getCreaterId());

		if (vo != null) {
			List<CustTopicKeywordBean> keywords = custTopicKeywordService.findCustTopicKeywordByCustTopicId(vo.getId());
			out.put("keywords", keywords);
		}

		out.put("beanVO", vo);
		out.put("creator", cuser == null ? "系统" : cuser.getRealName());

		return "/localsetting/custTopic-advanced-Detail";
	}
	
	@RequestMapping(value = "findCustTopic")
	@ResponseBody
	public MessageBean findCustTopic(Integer id) {
		try {
			CustTopicBean topic = custTopicService.queryCustTopic(id);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", topic);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "失败");
		}
	}

	/**
	 * sharefun2068 
	 * 2014-12-24
	 * function:设置模块--》客户专题列表
	 * @param request
	 * @param response
	 * @param out
	 * @return 
	 */
	@RequestMapping(value = { "list" })
	public ModelAndView queryList(HttpServletRequest request) {
		try {
			ModelAndView view = view("/localsetting/custTopic");
			if (AuthUtil.hasPermission(PermissionCode.DATA_TOPIC_ALL))
											view.addObject("isAdmin", true);
			Map<String,String> paramMap = CyyunStringUtils.request2Map(request.getParameterMap());
			paramMap.put("topicStatus", CUST_TOPIC_IS_ENABLE.enable.getValue().toString());//默认查询可用
			paramMap.put("parentId", "0");//默认查询父专题
			return view.addObject("page", queryCustTopicByPage(paramMap));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询客户专题失败");
		}
	}

	/**
	* sharefun2068 
	* 2014-12-24
	* function:设置模块--》客户专题列表
	* @param request
	* @param response
	* @param out
	* @return 
	 * @throws CustTopicServiceException 
	*/
	@RequestMapping(value = { "listPageByAjax" })
	public ModelAndView queryListPageByAjax(HttpServletRequest request) {
		try {
			ModelAndView view = view("/localsetting/custTopicList");
			if (AuthUtil.hasPermission(PermissionCode.DATA_TOPIC_ALL))
											view.addObject("isAdmin", true);
			return view.addObject("page", queryCustTopicByPage(CyyunStringUtils.request2Map(request.getParameterMap())));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询客户专题失败");
		}
	}

	private PageInfoBean<CustTopicBean> queryCustTopicByPage(String keywords, Integer parentId, Integer currentpage, Integer pagesize) throws CustTopicServiceException, CustTopicKeywordServiceException {
		if (currentpage == null) {
			currentpage = 1;
		}
		if (pagesize == null) {
			pagesize = 10;
		}
		CustTopicQueryBean query = new CustTopicQueryBean();
		if (StringUtils.isNotEmpty(keywords)) {
			query.setName(keywords);
		}
		query.setPageNo(currentpage);
		query.setPageSize(pagesize);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		if (parentId != null) {
			query.setParentId(parentId);
		}
		if (!AuthUtil.hasPermission(PermissionCode.DATA_TOPIC_ALL)) {
			query.setCreaterId(FMContext.getCurrent().getUserId());
		}

		PageInfoBean<CustTopicBean> page = custTopicService.queryCustTopicsWithShareByPage(query);

		return page;
	}
	
	private PageInfoBean<CustTopicInitStatusBean> queryCustTopicByPage(Map<String,String> paramMap) 
														throws CustTopicServiceException, 
														CustTopicKeywordServiceException, TaskServiceException, InvocationTargetException, IllegalAccessException {
		CustTopicQueryBean query = new CustTopicQueryBean();
		Integer currentpage = paramMap.get("currentpage") != null?Integer.valueOf(paramMap.get("currentpage")):FMConstant.PAGE_NO;
		query.setPageNo(currentpage);
		Integer pagesize = paramMap.get("pagesize") != null?Integer.valueOf(paramMap.get("pagesize")):FMConstant.PAGE_SIZE;
		query.setPageSize(pagesize);
		Integer topicStatus = StringUtils.isNotBlank(paramMap.get("topicStatus"))?Integer.valueOf(paramMap.get("topicStatus")):null;
		query.setStatus(topicStatus);
		String keywords = paramMap.get("keywords");
		if (StringUtils.isNotEmpty(keywords)) {
			query.setName(CyyunSqlUtil.dealSql(keywords));
		}
		//if (!AuthUtil.hasPermission(PermissionCode.DATA_TOPIC_ALL))
		query.setCreaterId(FMContext.getCurrent().getUserId());
		query.setCustId(FMContext.getCurrent().getCustomerId());
		if (paramMap.get("parentId") != null) {
			query.setParentId(Integer.valueOf(paramMap.get("parentId")));
		}
		PageInfoBean<CustTopicBean> pageInfoBean = custTopicService.queryCustTopicsWithShareByPage(query);
		PageInfoBean<CustTopicInitStatusBean> infoBean = new PageInfoBean<CustTopicInitStatusBean>();
		BeanUtils.copyProperties(pageInfoBean, infoBean);

		List<CustTopicInitStatusBean> data = new ArrayList<CustTopicInitStatusBean>();
		List<CustTopicBean> list = pageInfoBean.getData()==null?new ArrayList<CustTopicBean>():pageInfoBean.getData();
		for (CustTopicBean custTopicBean : list) {
			CustTopicInitStatusBean custTopicInitStatusBean = new CustTopicInitStatusBean();
			TaskBean taskBean = fmTaskService.queryByName("【客户专题初始化】"+custTopicBean.getId());
			if (ObjectUtils.notEqual(taskBean, null)) {
				custTopicInitStatusBean.setFmTaskStatus(Integer.valueOf(taskBean.getStatus().toString()));
			}
			BeanUtils.copyProperties(custTopicBean, custTopicInitStatusBean);
			data.add(custTopicInitStatusBean);
		}
		infoBean.setData(data);
		return infoBean;
	}

	/**
	 * sharefun2068 
	 * 2014-12-30
	 * function:设置模块--》获取客户专题  子级列表
	 * @param request
	 * @param response
	 * @param out
	 * @return 
	 * @throws TaskServiceException 
	 */
	@RequestMapping(value = { "subListByParentId" })
	public String querySubListByParentId(HttpServletRequest request, HttpServletResponse response, ModelMap out) throws CustTopicKeywordServiceException, TaskServiceException {

		String parentId = request.getParameter("parentId");
		String level = request.getParameter("level");
		String subId = request.getParameter("subId");

		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setParentId(Integer.parseInt(parentId));

		List<CustTopicBean> list = this.custTopicService.queryCustTopicsByCondition(query);
		List<CustTopicInitStatusBean> data = new ArrayList<CustTopicInitStatusBean>();
		for (CustTopicBean custTopicBean : list) {
			CustTopicInitStatusBean custTopicInitStatusBean = new CustTopicInitStatusBean();
			TaskBean taskBean = fmTaskService.queryByName("【客户专题初始化】"+custTopicBean.getId());
			if (ObjectUtils.notEqual(taskBean, null)) {
				custTopicInitStatusBean.setFmTaskStatus(Integer.valueOf(taskBean.getStatus().toString()));
			}
			BeanUtils.copyProperties(custTopicBean, custTopicInitStatusBean);
			data.add(custTopicInitStatusBean);
		}
		
		out.put("sublist", data);
		out.put("level", Integer.parseInt(level) + 10);
		out.put("subId", subId + parentId);
		if (AuthUtil.hasPermission(PermissionCode.DATA_TOPIC_ALL)) {
			out.put("isAdmin", true);
		}
		return "/localsetting/custTopicSubList";
	}

	private Integer saveKeyword(Integer kid, String include_kw, String exclude_kw, Integer active, Integer gid) throws KeywordServiceException {
		KeywordBean keywordBean = null;
		if (kid != null) {
			keywordBean = keywordService.findKeywordById(kid);
		}
		if (keywordBean == null || keywordBean.getKid() == null) {
			Date time = Calendar.getInstance().getTime();
			keywordBean = new KeywordBean();
			keywordBean.setCid(FMContext.getCurrent().getCustomerId());
			keywordBean.setIsFspiderActive(1);
			keywordBean.setIsSitespiderActive(1);
			keywordBean.setIsSespiderActive(1);
			keywordBean.setIsWeiboActive(1);
			keywordBean.setIsWeixinActive(0);
			keywordBean.setTmCreate(time);
		}
		
		keywordBean.setWord(KeywordUtils.assembleKeyword(include_kw, null, exclude_kw));
		
//		if (StringUtils.isNotEmpty(include_kw)) {
//			keywordBean.setWord1(include_kw);
//		}
//		if (StringUtils.isNotEmpty(exclude_kw)) {
//			keywordBean.setWord3(exclude_kw);
//		}
		if (keywordBean.getKid() != null) {
			keywordService.updateKeywordById(keywordBean);
			return keywordBean.getKid();
		}else {
			return 0;//现在AutoSpider开关一般是关闭的  return 0是让cust_topic_keyword表中的spider_key_id字段变为0  不去关联public schemer下的keyword表
			//这里执行时word字段是空的  会抛异常
//			return keywordService.addKeyword(keywordBean);
		}
	}

	/**
	 * sharefun2068 
	 * 2014-12-24
	 * function:设置模块--》客户专题详情
	 * @param request
	 * @param response
	 * @param out
	 * @return 
	 */
	@RequestMapping(value = { "detail" })
	public String queryCustTopicById(HttpServletRequest request, HttpServletResponse response, ModelMap out) throws CustTopicKeywordServiceException {
		int id = Integer.parseInt(request.getParameter("id"));
		CustTopicBean vo = this.custTopicService.queryCustTopic(id);

		//查询父类
		if (vo.getParentId() > 0) {
			vo.setParentBean(this.custTopicService.queryCustTopic(vo.getParentId()));
		}
		//查询创建人
		UserBean cuser = this.userService.findUser(vo.getCreaterId());

		if (vo != null) {
			List<CustTopicKeywordBean> keywords = custTopicKeywordService.findCustTopicKeywordByCustTopicId(vo.getId());
			out.put("keywords", keywords);
		}

		out.put("beanVO", vo);
		out.put("creator", cuser == null ? "系统" : cuser.getRealName());

		return "/localsetting/custTopicDetail";
	}

	/**
	* sharefun2068 
	* 2014-12-25 
	* function:设置模块--》客户专题状态切换
	*
	* @param request
	* @param response
	* @param out
	* @return
	*/
	@RequestMapping(value = { "changeStatus" })
	public void changeStatusById(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		JSONObject json = new JSONObject();
		String id = request.getParameter("id");
		String status = request.getParameter("status");
		try {
			int custTopicId = Integer.parseInt(id);
			int topicStauts = Integer.parseInt(status);
			if (CustTopicStatus.ENABLED.equals(topicStauts)) {
				CustTopicBean custTopic = this.custTopicService.queryCustTopic(custTopicId);
				if (custTopic.getParentId() != null) {
					CustTopicBean parent = this.custTopicService.queryCustTopic(custTopic.getParentId());
					if (parent != null && CustTopicStatus.DISABLED.equals(parent.getStatus())) {
						throw new RuntimeException("上级组织不可用，当前组织不能设置为可用状态。");
					}
				}
			}
			this.custTopicService.updateCustTopicStatus(custTopicId, topicStauts);
			List<CustTopicBean> topics = this.custTopicService.listCustTopicAndSub(custTopicId);
			for(CustTopicBean custTopicBean: topics){
				if(custTopicId != custTopicBean.getId()){
					custTopicService.updateCustTopicStatus(custTopicBean.getId(), topicStauts);
				}
			}

			for (CustTopicBean topic : topics) {
				List<CustTopicKeywordBean> include_ctkwbs = this.custTopicKeywordService.findCustTopicKeywordByCustTopicId(topic.getId());
				List<Integer> include_kids = BeanUtil.buildListByFieldName(include_ctkwbs, "spiderKeyId");
				Set<Integer> kids = SetBuilder.newHashSet();
				kids.addAll(include_kids);

				for (Integer kid : kids) {
					if (kid!=0) {
						saveKeyword(kid, null, null, topicStauts, null);
					}
				}
			}
			json.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", e.getMessage());
		}
		outJsonString(response, json);

	}

	/** 查询下一级组织 */
	@RequestMapping(value = { "queryNextOrgnization" })
	public String queryNextOrgnization(ModelMap modelMap, HttpServletRequest request) {
		log.info("queryNextOrgnization  in...");

		String rootOrgId = request.getParameter("rootOrgId");
		String varpadding = request.getParameter("varpadding");
		String searchKey = request.getParameter("searchKey");

		List<UserBean> resultList = new ArrayList<UserBean>();
		if (StringUtils.isBlank(searchKey)) {
			resultList = null;
		} else {
			resultList = null;
		}

		modelMap.put("rootOrgId", rootOrgId);
		modelMap.put("resultList", resultList);
		if (StringUtils.isBlank(varpadding)) {
			modelMap.put("varpadding", 0);
		} else {
			modelMap.put("varpadding", Integer.parseInt(varpadding) + 5);
		}

		log.info("queryNextOrgnization  out...");
		return "/localsetting/org_choose";
	}

	/** 根据组织ID查询成员列表 */
	@RequestMapping(value = { "queryUserListByOrgId" })
	public String queryMemberByOrgId(ModelMap modelMap, HttpServletRequest request) {
		log.info("queryMemberByOrgId  in...");
		int cid = FMContext.getCurrent().getCustomerId();
		int uid = FMContext.getCurrent().getUserId();
		String orgName = request.getParameter("orgName");
		List<Integer> uidList = this.customerConfigService.listUidByCidExceptAssignUid(cid, uid);

		List<UserBean> resultList = new ArrayList<UserBean>();
		resultList = this.userService.listUserById(uidList);

		modelMap.put("resultList", resultList);
		modelMap.put("orgName", orgName);

		log.info("queryMemberByOrgId  out...");
		return "/localsetting/member_choose";
	}

	/**
	* sharefun2068 
	* 2015-01-27 
	* function:设置模块--》进入客户专题用户共享弹出窗
	*
	* @param request
	* @param response
	* @param out
	* @return
	*/
	@RequestMapping(value = { "getShareUsers" })
	public void getShareUsers(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		JSONObject json = new JSONObject();
		String id = request.getParameter("id");
		try {
			CustTopicBean vo = this.custTopicService.queryCustTopic(Integer.parseInt(id));
			String userIds = "";
			if (vo.getUserIds() != null) {
				for (Integer userID : vo.getUserIds()) {
					userIds = userIds + userID + ",";
				}
			}
			json.put("userIds", vo.getUserIds());
		} catch (Exception e) {
			e.printStackTrace();
			json.put("userIds", null);
		}
		outJsonString(response, json);

	}

	/**
	* sharefun2068 
	* 2014-12-25 
	* function:设置模块--》客户专题用户共享
	*
	* @param request
	* @param response
	* @param out
	* @return
	*/
	@RequestMapping(value = { "shareUsers" })
	public void shareUsers(HttpServletRequest request, HttpServletResponse response, ModelMap out, Integer[] userIds) {
		JSONObject json = new JSONObject();
		String id = request.getParameter("id");
		try {
			CustTopicBean vo = new CustTopicBean();
			vo.setId(Integer.parseInt(id));
			vo.setUserIds(userIds);

			this.custTopicService.shareCustTopicForUsers(vo);
			json.put("result", "success");
		} catch (BaseException e) {
			log.error("子专题共享时不能移除父专题共享的用户", e);
			json.put("result", "子专题共享时不能移除父专题共享的用户");
		} catch (Exception e) {
			json.put("result", "共享用户失败");
		}
		outJsonString(response, json);

	}

	/**
	  * sharefun2068 
	  * 2015-01-19 
	  * function:设置模块--》客户专题 设置排序
	  *
	  * @param request
	  * @param response
	  * @param out
	  * @return
	  */
	@RequestMapping(value = { "setIndex" })
	public void setIndexById(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		JSONObject json = new JSONObject();
		String id = request.getParameter("id");
		String index = (String) request.getParameter("index");
		try {
			CustTopicBean vo = new CustTopicBean();
			vo.setId(Integer.parseInt(id));
			vo.setIndex(Integer.parseInt(index));
			vo.setFilterParent(false);
			this.custTopicService.updateCustTopic(vo);
			json.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", e.toString());
		}
		outJsonString(response, json);
	}

	public void outJsonString(HttpServletResponse response, JSONObject json) {
		response.setContentType("text/javascript;charset=UTF-8");
		response.setHeader("Cache-Control", "no-store, max-age=0, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		try {
			PrintWriter out = response.getWriter();
			out.write(json.toString());
			out.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * function:设置-->专题设置(删除客户专题)
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 */
	@RequestMapping(value = { "delete" })
	public void deleteCustTopicById(HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		JSONObject json = new JSONObject();
		String id = request.getParameter("id");
		try {
			//删除专题及对应子专题 和 关键词处理
			CustTopicBean custTopicBean = custTopicService.queryCustTopic(Integer.parseInt(id));
			HashSet<Integer> impactCustTopicId = new HashSet<Integer>();
			queryCustTopicSonId(custTopicBean, impactCustTopicId);
			CustTopicBean vo = new CustTopicBean();
			for (Integer custTopicId : impactCustTopicId) {
				vo.setId(custTopicId);
				//删除关键词
				custTopicKeywordService.deleteCustTopicKeywordByCustTopicId(custTopicId);
				//删除客户专题
				custTopicService.deleteCustTopic(vo);
				
				/**删除专题时一并删除对应的任务数据(如果有) -- 3表示专题初始化*/
				fmTaskService.deleteTaskByTypeAndName(3,"【客户专题初始化】"+custTopicId);
			}
			json.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			json.put("result", e.toString());
		}
		outJsonString(response, json);
	}
	
	/**
	 * 根据给定专题递归查询子专题id(包含自身id)
	 * @param custTopicBean
	 * @param list
	 * @throws CustTopicServiceException 
	 */
	private void queryCustTopicSonId(CustTopicBean custTopicBean, HashSet<Integer> custTopicSonId) throws CustTopicServiceException {
		CustTopicQueryBean query = new CustTopicQueryBean();
		custTopicSonId.add(custTopicBean.getId());
		query.setParentId(custTopicBean.getId());
		List<CustTopicBean> custTopicBeanList = custTopicService.queryCustTopicsByCondition(query);
		for (CustTopicBean custTopic : custTopicBeanList) {
			custTopicSonId.add(custTopic.getId());
			custTopic.setParentId(custTopic.getId());
			queryCustTopicSonId(custTopic, custTopicSonId);
		}
	}
	
	/***
	 * 检查还有没有正在初始化的任务
	 * @return
	 */
	@ResponseBody
	@RequestMapping("checkIsHaveInit")
	public MessageBean checkIsHaveInit(){
		TaskQueryBean query = new TaskQueryBean();
		Integer createrId = FMContext.getCurrent().getUserId();
		query.setCreaterId(createrId);
		query.setStatus((short) 1);
		query.setType((short) 3);
		try {
			PageInfoBean<TaskBean> pageInfoBean = fmTaskService.queryTasksByPage(query);
			List<TaskBean> list = pageInfoBean.getData()==null?new ArrayList<TaskBean>():pageInfoBean.getData();
			if (list.size() > 0) {
				return buildMessage(MESSAGE_TYPE_ERROR, "请等待正在初始化的专题完成,稍后再试");
			}
		} catch (TaskServiceException e) {
			log.error("检查是否有正在初始化的任务异常", e);
			e.printStackTrace();
		}
		return buildMessage(MESSAGE_TYPE_SUCCESS, "成功");
	}
	
	/***
	 * 专题初始化
	 * @param request
	 * @return
	 */
	@RequestMapping("custTopicInit")
	@ResponseBody
	public MessageBean custTopicInit(HttpServletRequest request) {
		MessageBean messageBean = null;
		String custId = FMContext.getCurrent().getCustomerId().toString();
		String custTopicId = request.getParameter("topicId");
		String startTime = request.getParameter("topicInitDate");
		String endTime = request.getParameter("endTime");
		log.info("===custId==="+custId+"===custTopicId==="+custTopicId+"===startTime==="+startTime+"===endTime==="+endTime);
		try {
			Integer userId = FMContext.getCurrent().getUserId();
			TaskBean taskBean = new TaskBean();
			String name = "【客户专题初始化】"+custTopicId;
			TaskBean custTopicTaskBean = fmTaskService.queryByName(name);
			
			Integer result = null;
			CustTopicInitTaskBean bean = new CustTopicInitTaskBean();
			if (ObjectUtils.notEqual(custTopicTaskBean, null)) {
				/**成功，再次初始化;失败，初始化*/
				if (custTopicTaskBean.getStatus() == 2 || custTopicTaskBean.getStatus() == 3) {
					custTopicTaskBean.setCreaterId(userId);
					custTopicTaskBean.setStatus((short) 1);
					custTopicTaskBean.setRemark("再次初始化");
					custTopicTaskBean.setResult("再次初始化");
					custTopicTaskBean.setUpdateTime(new Date());/**修改时间*/
					
					bean.setCids(custId);
					bean.setLabelIds(custTopicId);
					bean.setStartTime(startTime);
					bean.setEndTime(endTime);
					
					String contentJson = JsonUtil.toJson(bean);
					log.info("===contentJson===" +contentJson + "===");
					
					custTopicTaskBean.setContent(contentJson);
					
					fmTaskService.updateTask(custTopicTaskBean);
					
					result = custTopicTaskBean.getId();
				}
			} else {
				taskBean.setCreaterId(userId);
				taskBean.setType((short) 3);	//客户专题初始化
				taskBean.setStatus((short) 1);	//处理中
				taskBean.setName(name);
				taskBean.setRemark("初始化");
				taskBean.setResult("初始化");
				
				bean.setCids(custId);
				bean.setLabelIds(custTopicId);
				bean.setStartTime(startTime);
				bean.setEndTime(endTime);
				
				String contentJson = JsonUtil.toJson(bean);
				log.info("===contentJson===" +contentJson + "===");
				
				taskBean.setContent(contentJson);
				
			    result = support.addCustTopicInitTask(taskBean);
			}
			
			if(result != null) {
				support.custTopicInitCallInterface(bean, result, custTopicInitInterfaceUrl);
				messageBean = buildMessage(MESSAGE_TYPE_SUCCESS, "操作成功");
			} else {
				messageBean = buildMessage(MESSAGE_TYPE_ERROR, "操作失败");
			}
		} catch (Exception e) {
			log.error("===custTopicInit==="+e.getMessage(), e);
			messageBean = buildMessage(MESSAGE_TYPE_ERROR, "操作失败");
		}
		return messageBean;
	}
	
}
