package com.cyyun.fm.focus.controller;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.velocity.Template;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cyyun.base.constant.ArticleConstants;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.base.util.ConstUtil;
import com.cyyun.base.util.CyyunCheckParams;
import com.cyyun.base.util.ExportExcelUtil;
import com.cyyun.base.util.GetCurrentDate;
import com.cyyun.base.util.PropertiesUtil;
import com.cyyun.base.util.TagUtil;
import com.cyyun.base.util.UnicodeFilter;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.JsonUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.common.core.util.SpringContextUtil;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.focus.bean.ConditionParam;
import com.cyyun.fm.newspath.NewsPathWay;
import com.cyyun.fm.newspath.NewsPathWay.ArticleInfo;
import com.cyyun.fm.report.templete.TemplateUtil;
import com.cyyun.fm.service.FmTaskService;
import com.cyyun.fm.service.QueryLogService;
import com.cyyun.fm.service.WeiboSourceService;
import com.cyyun.fm.service.bean.TaskBean;
import com.cyyun.fm.service.bean.TaskSourceBean;
import com.cyyun.fm.service.bean.WeiboSourceBean;
import com.cyyun.fm.service.exception.TaskServiceException;
import com.cyyun.fm.vo.ArticleVo;
import com.cyyun.fm.vo.ArticleVoInter;
import com.cyyun.process.service.FocusInfoService;
import com.cyyun.process.service.bean.FocusInfoBean;
import com.cyyun.process.service.bean.FocusInfoQueryBean;

/**
 * @author zhangfei
 * 2014-12-10
 * @param <UserCheckService>
 * @param <UserCheckServer>
 */
@Controller
@RequestMapping(value = { "focusing" })
public class FmVoicefocusController extends BaseController {

	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;
	public static final String EXPORTDOC_PATH = PropertiesUtil.getValue("exportDocpath");
	public static Boolean EXPORT_SUC = false;

	@Autowired
	private FocusInfoService focusInfoService;

	@Autowired
	private FmVoicefocusSupport support;
	
	@Autowired
	private ArticleService articleService;

	@Autowired
	private CustomerConfigService customerConfigService;

	@Autowired
	private VelocityConfigurer velocityConfig;
	
	@Autowired
	private FmTaskService taskService;
	
	@Autowired
	private WeiboSourceService weiboSourceService;
	
	@Autowired
	private QueryLogService queryLogService;
	
	@Autowired
	private ExportExcelUtil exportExcelUtil;
	
	@Value("${url.interface.weiboSource}")
	String weiboSourceInterfaceUrl;
	
	@Value("${url.interface.weiboSource.callback}")
	String weiboSourceCallbackInterfaceUrl;

	@RequestMapping(value = { "QueryInfo" })
	public ModelAndView QueryInfo(ConditionParam conditionParam, HttpServletRequest request) {
		try {
			Long startTime = System.currentTimeMillis();
			if (conditionParam.getCurrentpage() == null) {
				conditionParam.setCurrentpage(PAGE_NO);
			}
			if (conditionParam.getPagesize() == null) {
				conditionParam.setPagesize(PAGE_SIZE);
			}
			CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
			conditionParam.setTimeType(conditionParam.getTimeType() != null ? conditionParam.getTimeType() : 8);
			//预警数据
			PageInfoBean<ArticleVoInter> pageInfo = support.queryFocusInfo(conditionParam, request);
			//全部预警 | 我的预警
			Boolean scopeflag = false;
			Boolean grade_type_flag = true;//预警等级标记
			//Version 0:自动化版用户 1：高级版用户
//			if (custConfig.getVersion() == 1) {
//				grade_type_flag = true;//不是高级版用户才有预警等级和预警类型下拉框
//				if (AuthUtil.hasPermission(PermissionCode.DATA_WARNING_ALL)) {
//					scopeflag = true; //有数据管理权限（管理员权限）且是fm高级版用户才有预警范围下拉框
//				}
//			}
			//没有预警规则时 自动化版显示"自动"，普通版显示"人工"
			String focusdisplay = custConfig.getVersion()==0 ? FMConstant.FOCUS_DISPLAY.noneauto.getValue() : FMConstant.FOCUS_DISPLAY.none.getValue();
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------预警列表查询耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "WarningLoading", 3000L);
			return view("voicefocus/voicefocus").addObject("pageInfo", pageInfo).addObject("scopeflag", scopeflag).addObject("grade_type_flag", grade_type_flag)
					.addObject("FOCUSDISPLAY", focusdisplay).addAllObjects(support.getOptions()).addObject("defaultTimeType", conditionParam.getTimeType());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载预警失败");
		}
	}

	/**
	 * <h3>聚焦页面</h3>
	 * @param endTimeInput 
	 * @param startTimeInput 
	 * @param YJDJ_hidden 
	 * 
	 * @return JSON
	 */
	@RequestMapping("voicefocusByPage")
	public ModelAndView voicefocusByPage(ConditionParam conditionParam, HttpServletRequest request) {
		try {
			Long startTime = System.currentTimeMillis();
			CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
			Boolean grade_type_flag = true;//预警等级标记
//			if (custConfig.getVersion() == 1) {
//				grade_type_flag = true;//不是高级版用户才有预警等级和预警类型下拉框
//			}
			conditionParam.setKeyword(conditionParam.getKeyword().trim());
			PageInfoBean<ArticleVoInter> pageInfo = support.queryFocusInfo(conditionParam, request);
			//没有预警规则时 自动化版显示"自动"，普通版显示"人工"
			String focusdisplay = custConfig.getVersion()==0 ? FMConstant.FOCUS_DISPLAY.noneauto.getValue() : FMConstant.FOCUS_DISPLAY.none.getValue();
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------预警列表查询耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "WarningLoading", 3000L);
			return view("voicefocus/index-paging-article").addObject("pageInfo", pageInfo).addObject("grade_type_flag", grade_type_flag).addObject("FOCUSDISPLAY", focusdisplay);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载预警失败");
		}
	}

	//	private List<ArticleVoInter> convertToVo(List<FocusInfoBean> datas, String keyword){
	//		try {
	//			List<ArticleVoInter> focusInfoBeanList = new ArrayList<ArticleVoInter>();
	//			if(CollectionUtils.isNotEmpty(datas)){
	//				for(FocusInfoBean focusInfoBean: datas){
	//					ArticleVoInter focusInfoVo = new FocusInfoVo(support, keyword);
	//					//-------------
	////					ArticleQueryBean queryBean=new ArticleQueryBean();
	////					List<String> guid_guidGroups=new ArrayList<String>();
	////					guid_guidGroups.add(focusInfoBean.getArticleId());
	////					queryBean.setGuid_guidGroups(guid_guidGroups);
	////					Map<String, Integer> articleMap = articleService.countArticleSimilar(queryBean);
	////					int b=articleMap.get(guid_guidGroups);
	////					focusInfoBean.setSource(b);
	//					//-------------
	//					CyyunBeanUtils.copyProperties(focusInfoVo,focusInfoBean);
	//					
	//					focusInfoBeanList.add(focusInfoVo);
	//				}
	//				return focusInfoBeanList;
	//			}
	//		} catch (Exception e) {
	//			log.error("convertToVo::", e);
	//		}
	//		return null;
	//	}

	//	private List<FocusInfoView> convertToView(List<FocusInfoBean> data, String keyword) throws ArticleServiceException {
	//		List<FocusInfoView> focusInfoViewList = ListBuilder.newArrayList();
	//		Set<String> keywordSet = null;
	//		for (FocusInfoBean focusInfoBean : data) {
	//			FocusInfoView view = BeanUtil.copy(focusInfoBean,
	//					FocusInfoView.class);
	//			// 相似�
	//			if (focusInfoBean.getArticleIds() != null) {
	//				view.setRelatedArticles(focusInfoBean.getArticleIds().length);
	//			} else {
	//				view.setRelatedArticles(0);
	//			}
	//			// 预警等级
	//			if (focusInfoBean.getLevel() == 1) {
	//				view.setLevelName("紧�);
	//				view.setLevelColor("c_emergency c_fl c_mt10");
	//			} else if (focusInfoBean.getLevel() == 2) {
	//				view.setLevelName("重要");
	//				view.setLevelColor("c_important c_fl c_mt10");
	//			} else if (focusInfoBean.getLevel() == 3) {
	//				view.setLevelName("一�);
	//				view.setLevelColor("c_general c_fl c_mt10");
	//			} else {
	//				// view.setLevelName("自动");
	//				// view.setLevelColor("c_automatic c_fl c_mt10");
	//			}
	//
	//			// 正负�
	//			if (focusInfoBean.getSentiment() == null) {
	//				view.setSentiments("未知");
	//			} else if (focusInfoBean.getSentiment() == 0) {
	//				view.setSentiments("未知");
	//			} else if (focusInfoBean.getSentiment() == 1) {
	//				view.setSentiments("负面");
	//			} else if (focusInfoBean.getSentiment() == 2) {
	//				view.setSentiments("正面");
	//			} else if (focusInfoBean.getSentiment() == 3) {
	//				view.setSentiments("中�);
	//			} else {
	//				view.setSentiments("未知");
	//			}
	//			
	//			if (StringUtils.isNotBlank(keyword)) {
	//				if (keywordSet == null)
	//					keywordSet = support.toStringSet(keyword);
	//				if (StringUtils.isNotBlank(view.getTitle())) {
	//					String title = view.getTitle().replaceAll("[\\t\\n\\r]",
	//							"");
	//					view.setTitle(support.markKeyword(title, keywordSet));
	//				}
	//				if (StringUtils.isNotBlank(view.getContent())) {
	//					String content = view.getContent().replaceAll(
	//							"[\\t\\n\\r]", "");
	//					view.setContent(support.markKeyword(content, keywordSet));
	//				}
	//			}
	//			if(CollectionUtils.isNotEmpty(focusInfoBean.getWarningRules())){
	//				List<String> warningRuleNameList = new ArrayList<String>();
	//				for(WarningRuleBean warningRuleBean: focusInfoBean.getWarningRules()){
	//					warningRuleNameList.add(warningRuleBean.getName());
	//				}
	//				view.setWarningRuleNames(StringUtils.join(warningRuleNameList, ","));
	//			}
	//			focusInfoViewList.add(view);
	//		}
	//		return focusInfoViewList;
	//	}

	private static final Integer HOMEPAGE_FOCUSINFO_PAGESIZE = 6;

	@RequestMapping(value = { "alertInfo" })
	public ModelAndView alertInfo(HttpServletRequest request) {
		FocusInfoQueryBean focusInfoQueryBean = new FocusInfoQueryBean();
		Integer userId = FMContext.getCurrent().getUserId();// 按登陆用户显示预警数量
		// 最近7天
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		String pattern = "yyyy-MM-dd";
		String pattern1 = "yyyy-MM-dd HH:mm:ss";
		String end = DateFormatUtils.format(cal.getTime(), pattern) + " 23:59:59";
		cal.add(Calendar.DATE, -6);
		String start = DateFormatUtils.format(cal.getTime(), pattern) + " 00:00:00";
		try {
			focusInfoQueryBean.setStartCreateTime(DateUtils.parseDate(start, pattern1));
			focusInfoQueryBean.setEndCreateTime(DateUtils.parseDate(end, pattern1));
		} catch (ParseException e) {
			//时间转换这里不作处理
		}
		focusInfoQueryBean.setUserId(userId);
		focusInfoQueryBean.setPageSize(HOMEPAGE_FOCUSINFO_PAGESIZE);
		Long startTime = System.currentTimeMillis();
		PageInfoBean<FocusInfoBean> queryRest = focusInfoService.queryFocusInfoByPage(focusInfoQueryBean);
		// Boolean typeflag = false;
		// if (custConfig.getVersion() == 1) {// Version 0:自动化版用户 1：高级版用户
		// typeflag = true;
		// }
		List<FocusInfoBean> queryList = queryRest.getData();
		PageInfoBean<FocusInfoBean> queryRestSix = new PageInfoBean<FocusInfoBean>();
		if (CollectionUtils.isNotEmpty(queryList)) {
			CyyunCheckParams.setObjectFieldsEmpty(queryList, "articleId", "level", "title", "websiteName", "createTime", "guidGroup");
			queryRestSix.setData(queryList);
		}
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
		Long endTime = System.currentTimeMillis();
		System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
				"("+FMContext.getLoginUser().getId()+")"+"#----------最新预警【系统】查询耗时#"+(endTime-startTime)+"#毫秒");
		queryLogService.saveInterfaceAccessLog((endTime-startTime), "WarningSystem", 3000L);
		return view("voicefocus/alert-board-view").addObject("queryRestSix", queryRestSix).addObject("typeflag", custConfig.getVersion() == 1 ? true : false);
	}

	@RequestMapping(value = { "article" })
	public String initLeftGroups(HttpServletRequest request, ModelMap modelMap) throws UnsupportedEncodingException {

		String stepTemp = request.getParameter("step");
		int setp = Integer.valueOf(stepTemp);
		if (setp == 3) {

			FocusInfoQueryBean fib = new FocusInfoQueryBean();
			//得到前面传过来的用户id,再次执行查询
			fib.setUserId(223);
			PageInfoBean<FocusInfoBean> queryRest = focusInfoService.queryFocusInfoByPage(fib);
			//把得到的bean转为数据
			List<FocusInfoBean> fb = queryRest.getData();
			//遍历这个bean
			for (FocusInfoBean focusInfoBean : fb) {
				request.setAttribute("queryList", focusInfoBean.getContent());
			}

		} else {
			request.setAttribute("queryList", URLDecoder.decode(request.getParameter("hab"), "utf-8"));
		}
		//返回到文章详情页
		return "voicefocus/voicefocus";

	}

	@RequestMapping(value = { "focusingArticle" })
	public ModelAndView focusingArticle(HttpServletRequest request, ModelMap modelMap) throws UnsupportedEncodingException {
		String guid = request.getParameter("id");
		try {
			ArticleBean a = articleService.findArticle(guid);
			request.setAttribute("content", a);//返回文章bean
			return view("/search/detail").addObject("article", a);
		} catch (ArticleServiceException e) {
			e.printStackTrace();
		}
		//		 	 FocusInfoQueryBean fib = new FocusInfoQueryBean();
		//		 				//得到前面传过来的用户id,再次执行查询�
		//		 				fib.setUserId(222);
		//		 				PageInfoBean<FocusInfoBean> queryRest = focusInfoService.queryFocusInfoByPage(fib);
		//		 				//把得到的bean转为数据articleDetail
		//		 			    List<FocusInfoBean> fb = queryRest.getData();
		//		 				//遍历这个bean
		//		 				 for (FocusInfoBean focusInfoBean : fb) {
		//		 					request.setAttribute("title",focusInfoBean.getTitle());//标题
		//		 					request.setAttribute("content",focusInfoBean.getContent());//内容
		//		 					request.setAttribute("time",focusInfoBean.getCreateTime());//发布时间
		//						}
		//返回到文章详情页�
		//	            return "voicefocus/voicefocus";
		return null;
	}

	@RequestMapping(value = { "theme" })
	public String back(HttpServletRequest request, ModelMap modelMap) {
		//返回到文章详情页
		return "details/theme";
	}

	/**
	 * 根据guid判断文章是否真的存在
	 * @param guid
	 * @return
	 * @throws ArticleServiceException
	 */
	@RequestMapping(value = { "findArticle" })
	@ResponseBody
	public ArticleBean findArticle(String guid, HttpServletRequest request) throws ArticleServiceException {
		return articleService.findArticle(guid);
	}

	/**
	 * 根据guid没有找到文章时，显示预警文章详情
	 * @param guid
	 * @return
	 * @throws ArticleServiceException
	 */
	@RequestMapping(value = { "findWarningArticle" })
	public ModelAndView findWarningArticle(String guid) throws ArticleServiceException {
		FocusInfoQueryBean fib = new FocusInfoQueryBean();
		int id = FMContext.getCurrent().getUserId();//按登陆用户显示预警数据
		fib.setUserId(id);
		fib.setArticleId(guid);
		PageInfoBean<FocusInfoBean> pageInfo = focusInfoService.queryFocusInfoByPage(fib);
		FocusInfoBean focusInfo = pageInfo.getData().get(0);
		return view("/voicefocus/warning-detail").addObject("article", focusInfo);
	}
	
	/**
	 * 文章详情
	 * @param guid
	 * @param keyword
	 * @param source
	 * @return
	 */
	@RequestMapping("articleDeatail")
	public ModelAndView articleDeatail(HttpServletRequest request) {
		try {		
			String source = request.getParameter("source");
			String guid = request.getParameter("guid");
			Map<String, Object> resultMap = support.findArticle(guid, request.getParameter("keyword"), request);
			Object article= resultMap.get("article");
			CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
			//没有预警规则时 自动化版显示"自动"，普通版显示"人工"
			String focusdisplay = custConfig.getVersion()==0 ? FMConstant.FOCUS_DISPLAY.noneauto.getValue() : FMConstant.FOCUS_DISPLAY.none.getValue();
			return view("/voicefocus/detail").addObject("article", article).addObject("listReplys",  resultMap.get("listReplys")).addObject("source", source).addObject("warningRuleName", resultMap.get("warningRuleName")).addObject("FOCUSDISPLAY", focusdisplay);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询文章失败");
		}
	}
	
	/**
	 * <h3>跳转文章详情页</h3>
	 */
	@RequestMapping("openArticleDetail")
	public ModelAndView openArticleDetail(String guid, String keyword) {
		try {
			return view("/voicefocus/article-detail").addObject("guid", guid).addObject("keyword", keyword);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询文章失败");
		}
	}
	
	@ResponseBody
	@RequestMapping("checkIsSuccess")
	public MessageBean queryStatus(String url){
		try {
			TaskBean taskBean = taskService.queryByName("【微博溯源】"+url);
			if (ObjectUtils.notEqual(taskBean, null)) {
				/**处理成功*/
				if (taskBean.getStatus() == 2) {
					return super.buildMessage(MESSAGE_TYPE_SUCCESS, "");
				} 
				if (taskBean.getStatus() == 3) {
					return super.buildMessage(MESSAGE_TYPE_ERROR, "");
				}
				return super.buildMessage(MESSAGE_TYPE_SUCCESS, "正在处理中", "");
			}
		} catch (TaskServiceException e) {
			e.printStackTrace();
			return super.buildMessage(MESSAGE_TYPE_ERROR, "");
		}
		return super.buildMessage(MESSAGE_TYPE_ERROR, "");
	}
	
	/***
	 * 保存记录到weibo_source表和任务task表
	 * @param request
	 * @return
	 */
	@RequestMapping("save")
	@ResponseBody
	public MessageBean saveTask(@RequestParam("url")String url,@RequestParam("level")String level){
		try {
			String name = "【微博溯源】"+url;
			TaskBean taskBean2 = taskService.queryByName(name);
			/**如果任务对象存在并且处理成功直接展示,省去溯源分析时间*/
			if (ObjectUtils.notEqual(taskBean2, null)) {
				if (taskBean2.getStatus() == 2) {/**处理成功*/
					return super.buildMessage(MESSAGE_TYPE_SUCCESS, "");
				}
			}
			
			log.info("===url===" +url + "===");
			log.info("===level===" +level + "===");
			//log.info("===qty===" +qty + "===");
			
			if(StringUtils.isBlank(url) ){
				return super.buildMessage(MESSAGE_TYPE_ERROR, "url不能为空");
			}else{
				url = url.trim();
			}
			
			if(StringUtils.isBlank(level) ){
				return super.buildMessage(MESSAGE_TYPE_ERROR, "层级不能为空");
			}else{
				level = level.trim();
			}
			
			/*
			if(StringUtils.isBlank(qty) ){
				return super.buildMessage(MESSAGE_TYPE_ERROR, "数量不能为空");
			}else{
				qty = qty.trim();
			}
			*/
			
			
			Integer userId = FMContext.getCurrent().getUserId();
			TaskBean taskBean = new TaskBean();
			taskBean.setCreaterId(userId);
			
			taskBean.setType((short) 1);	//微博溯源
			taskBean.setStatus((short) 0);	//未处理
			taskBean.setName(name);
			taskBean.setRemark(null);
			taskBean.setResult(null);
			
			TaskSourceBean bean = new TaskSourceBean();
			bean.setUrl(url);
			bean.setLevel(level);
			//bean.setQty(qty);
			
			String contentJson = JsonUtil.toJson(bean);
			log.info("===contentJson===" +contentJson + "===");
			
			taskBean.setTaskSourceBean(bean);
			taskBean.setContent(contentJson);
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
		
			taskService.addTaskAndCallInterface(resultMap, taskBean, weiboSourceInterfaceUrl, weiboSourceCallbackInterfaceUrl);
			
			log.info("===result_code==="+resultMap.get("result_code"));
			log.info("===result_desc==="+resultMap.get("result_desc"));
			
		} catch (TaskServiceException e) {
			log.error("===exception==="+e.getErrorCode(), e);
			
			return super.buildMessage(MESSAGE_TYPE_ERROR, "系统异常："+e.getErrorCode());
		}
		return super.buildMessage(MESSAGE_TYPE_SUCCESS, "");
		
	}
	
	@RequestMapping("weiboSourceCallback")
	public String weiboSourceCallback(HttpServletRequest request, HttpServletResponse response){
		String taskId = request.getParameter("taskId");
		String status = request.getParameter("status");
		String data = request.getParameter("data");
		log.info("===taskId===" +taskId + "===");
		log.info("===status===" +status + "===");
		log.info("===data===" +data + "===");
		
		JSONObject json = new JSONObject();
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		TaskBean taskBean = new TaskBean();
		try {
			if(StringUtils.isBlank(taskId) ){
				resultMap.put("result_code", "00001");
				resultMap.put("result_desc", "taskId参数不能为空");
				
				json.put("data",resultMap);
				outJsonString(response, json);
				return null;
			}else{
				taskId = taskId.trim();
			}
			
			if(StringUtils.isBlank(status) ){
				resultMap.put("result_code", "00002");
				resultMap.put("result_desc", "status参数不能为空");
				
				json.put("data",resultMap);
				outJsonString(response, json);
				return null;
			}else{
				status = status.trim();
			}
			
			Integer id = Integer.parseInt(taskId);
			taskBean.setId(id);
			taskBean.setUpdateTime(new Date());
			if(status!=null && "1".equals(status)){	//成功
				taskBean.setStatus((short)2);	//成功
			}else if(status!=null && "0".equals(status)){	//失败
				taskBean.setStatus((short)3);	//失败
			}
			
			if(StringUtils.isBlank(data) ){
				resultMap.put("result_code", "00003");
				resultMap.put("result_desc", "data参数不能为空");
				
				taskBean.setStatus((short)3);	//失败
				taskBean.setResult("result_code：00003，result_desc：data参数不能为空");
				try {
					taskService.updateTask(taskBean);
				} catch (TaskServiceException e) {
					log.error("===updateTask===exception==="+e.getMessage(),e);
				}
				
				json.put("data",resultMap);
				outJsonString(response, json);
				return null;
			}else{
				data = data.trim();
			}

			WeiboSourceBean weiboSourceBean = JsonUtil.getObject(data, WeiboSourceBean.class);
			if(weiboSourceBean!= null){
				weiboSourceService.addWeiboSourceUpdTask(resultMap, weiboSourceBean, taskBean);
				
				resultMap.put("result_code", "0000");
				resultMap.put("result_desc", "");
				json.put("data",resultMap);
				outJsonString(response, json);
				return null;
			}else{
				taskBean.setStatus((short)3);	//失败
				taskBean.setResult("result_code：00005，result_desc：data参数解析错误");
				try {
					taskService.updateTask(taskBean);
				} catch (TaskServiceException e) {
					log.error("===updateTask===exception==="+e.getMessage(),e);
				}
				
				resultMap.put("result_code", "00005");
				resultMap.put("result_desc", "data参数解析错误");
				
				json.put("data",resultMap);
				outJsonString(response, json);
				return null;
			}
		} catch (Exception e1) {
			log.error("===addWeiboSource===exception==="+e1.getMessage(),e1);
			
			taskBean.setStatus((short)3);	//失败
			taskBean.setResult("result_code：00004，result_desc：系统异常："+e1.getMessage());
			try {
				taskService.updateTask(taskBean);
			} catch (TaskServiceException e) {
				log.error("===updateTask===exception==="+e.getMessage(),e);
			}
			
			resultMap.put("result_code", "00004");
			resultMap.put("result_desc", "系统异常："+e1.getMessage());
			
			json.put("data",resultMap);
			outJsonString(response, json);
			return null;
		}
	}
	
	public void outJsonString(HttpServletResponse response, JSONObject json)
    {
        response.setContentType("text/javascript;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, max-age=0, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        try
        {
            PrintWriter out = response.getWriter();
            out.write(json.toString());
            out.close();
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
        }
    }
	
	@RequestMapping("weiboTraceability")
	public ModelAndView weiboTraceability(@RequestParam("url")String url){
		try {
			String result = "";
			Short status = 0; 
			String name = "【微博溯源】"+url;
			TaskBean taskBean2 = taskService.queryByName(name);
			if (ObjectUtils.notEqual(taskBean2, null)) {
				if (taskBean2.getStatus() == 2) {/**处理成功*/
					result = taskBean2.getResult();
					status = taskBean2.getStatus();
				}
			}
			
			String id = result;//request.getParameter("id");
			if(id!=null){
				id = id.trim();
			}
			log.info("===id==="+id+"===");
			
			List<WeiboSourceBean> list = Collections.emptyList();
			if (StringUtils.isNotBlank(id)) {
				list = weiboSourceService.queryWeiboSourceListByRecursive(Integer.parseInt(id));
			}
			List<WeiboSourceBean> rootTrees = this.getNewList(list);
			
			log.info("===data==="+JSONArray.toJSON(rootTrees).toString()+"===");
			
			return view("/voicefocus/weiboTraceability").addObject("data",JSONArray.toJSON(rootTrees).toString()).addObject("status", status);
		} catch (Exception e) {
			log.error("===weiboSourceDetail==="+e.getMessage(),e);
			return message(MESSAGE_TYPE_ERROR, "查询微博溯源失败");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<WeiboSourceBean> getNewList(List<WeiboSourceBean> trees){
		List<WeiboSourceBean> rootTrees = new ArrayList<WeiboSourceBean>();
        for (WeiboSourceBean tree : trees) {
        	tree.setName(tree.getAuthor()+"\n转发数量："+tree.getForwardTimes()+"\n转发时间："+tree.getPostTimeStr());
            if(tree.getParentId() == 0){
                rootTrees.add(tree);
            }
            for (WeiboSourceBean t : trees) {
            	//t.setName(t.getName()+"\n转发数量："+t.getForwardTimes()+"\n转发时间："+t.getPostTimeStr());
                if(t.getParentId().intValue() == tree.getId().intValue()){
                    if(tree.getChildren() == null){
                        List<WeiboSourceBean> myChildrens = new ArrayList<WeiboSourceBean>();
                        myChildrens.add(t);
                        tree.setChildren(myChildrens);
                    }else{
                        tree.getChildren().add(t);
                    }
                }
            }
            if (CollectionUtils.isEmpty(tree.getChildren())) {
				tree.setChildren(Collections.EMPTY_LIST);
			}
            
        }
        
        return rootTrees;
		
	}
	
	/**
	 * 文章溯源
	 * @param guid
	 * @param keyword
	 * @return
	 */
	
	@RequestMapping(value = { "/forwardTraceability" })
	public ModelAndView forwardTraceability(HttpServletRequest request) {
		try {
			Map<String, Object> resultMap = support.findArticle(request.getParameter("guid"), null, request);
			ArticleVo articleVo= (ArticleVo) resultMap.get("article");
			//---------------
			String guidGroup = request.getParameter("guidGroup");
			//---------------
			NewsPathWay npwff=new NewsPathWay();
			List<ArticleInfo> articleInfos = support.arrayListArticleInfo(guidGroup);
			Map<String, List<ArticleInfo>> similarArticles = new HashMap<String, List<ArticleInfo>>();
			similarArticles.put(articleVo.getTitle(), articleInfos);
			String newsPath = npwff.createNPTFromSimilarArticles(similarArticles);
			
	        return view("/voicefocus/traceability").addObject("newsPath",newsPath);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询文章溯源失败");
		}
	}

	/**
	 * 相似文
	 * @param guid
	 * @param articleBean
	 * @param request
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = { "similarQueryInfo" })
	public ModelAndView similarQueryInfo(HttpServletRequest request, Integer pageNo, Integer pageSize) {
		try {
			if (pageNo == null) {
				pageNo = PAGE_NO;
			}
			if (pageSize == null) {
				pageSize = PAGE_SIZE;
			}
			PageInfoBean<ArticleBean> pageInfo = support.similarQueryInfo(request, pageNo, pageSize);
			return view("similar/index").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载相似文失败");
		}
	}

	/**
	 * 相似文列表
	 * @param query
	 * @param guid
	 * @param request
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = { "queryList" })
	public ModelAndView queryList(HttpServletRequest request, Integer pageNo, Integer pageSize) {
		try {
			if (pageNo == null) {
				pageNo = PAGE_NO;
			}
			if (pageSize == null) {
				pageSize = PAGE_SIZE;
			}
			PageInfoBean<ArticleBean> pageInfo = support.similarQueryInfo(request, pageNo, pageSize);
			return view("similar/index-paging-article").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载相似文列表失败");
		}
	}

	//###########################################   export   ###########################################//

	/**
	 * <h3>开启导出线程 Word</h3>
	 */
//	@RequestMapping("exportWordThread")
//	@ResponseBody
//	public MessageBean exportWordThread(ConditionParam conditionParam, HttpServletResponse response, HttpServletRequest request) {
//		try {
//			CustConfigBean custConfig=customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
//			custConfig.setFilterSimilar(true);//查询到的文章带有相似文
//			List<String> guids = support.exportAllParam(conditionParam, custConfig);
//			Thread thread = new Thread(new ExportWord(response, guids, request.getSession()));
//			thread.start();
//			for (;;) {
//				if (EXPORT_SUC) {
//					EXPORT_SUC = false;
//					break;
//				} else {
//					Thread.sleep(1000);
//				}
//			}
//			return buildMessage(MESSAGE_TYPE_SUCCESS, "导出Word成功");
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return buildMessage(MESSAGE_TYPE_ERROR, "导出Word失败");
//		}
//	}

	/**
	 * <h3>开启导出线程 Excel</h3>
	 */
	@RequestMapping("exportExcelThread")
	@ResponseBody
	public MessageBean exportExcelThread(ConditionParam conditionParam, HttpServletRequest request) {
		try {
			List<ArticleBean> articles = support.queryArticleList(conditionParam, request);
			exportExcelUtil.exportExcel(articles, request);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "导出Excel成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "导出Excel失败");
		}
	}

	private class ExportWord implements Runnable {
		private HttpServletResponse response;
		private List<String> guids;
		private HttpSession session;

		public ExportWord(HttpServletResponse response, List<String> guids, HttpSession session) {
			this.response = response;
			this.guids = guids;
			this.session = session;
		}

		@Override
		public void run() {
			try {
				response.addHeader("Content-Disposition", "attachment;filename=" + session.getAttribute("fileName").toString());
				ToolManager manager = new ToolManager();
				manager.configure("/toolbox.xml");
				ToolContext context = manager.createContext();
				List<ArticleBean> result = ListBuilder.newArrayList();
				String content = null;
				String title = null;
				for (String guid : guids) {
					ArticleBean article = articleService.findArticle(guid);
					content = TemplateUtil.getValueAfterRepalceTNR(TemplateUtil.getValueAfterRepalceSpecialWord(UnicodeFilter.filter(article.getContent())));
					title = TemplateUtil.getValueAfterRepalceSpecialWord(UnicodeFilter.filter(article.getTitle()));
					article.setAuthor(article.getAuthor() == null ? "" : article.getAuthor());
					article.setContent(content);
					article.setTitle(title);
					result.add(article);
				}
				context.put("articles", result);
				response.setCharacterEncoding("UTF-8");

				String targetPath = EXPORTDOC_PATH + "/";
				String fileName = session.getAttribute("fileName").toString();
				String reportFile = targetPath + fileName;

				Template template = velocityConfig.getVelocityEngine().getTemplate("/template/report/brief-article.vm");
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reportFile), "UTF-8"));
				template.merge(context, out);
				out.close();
				//				velocityConfig.getVelocityEngine().mergeTemplate("/template/report/brief-article.vm", "UTF-8", context, out);
				//velocityConfig.getVelocityEngine().mergeTemplate("/template/report/brief-article.vm", "UTF-8", context, response.getWriter());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				EXPORT_SUC = true;
			}
		}
	}

	public class ExportExcel implements Runnable {
		private HttpSession session;
		private List<String> guids;

		public ExportExcel(List<String> guids, HttpSession session) {
			this.guids = guids;
			this.session = session;
		}

		@Override
		public void run() {
			Map<String, Object> beans = new HashMap<String, Object>();
			List<ArticleBean> result = ListBuilder.newArrayList();
			String content = null;
			String title = null;
			for (String guid : guids) {
				ArticleBean article = new ArticleBean();
				try {
					article = articleService.findArticle(guid);
					content = TemplateUtil.checkSize(TemplateUtil.getValueAfterRepalceSpecialWord(UnicodeFilter.filter(article.getContent())));
					title = TemplateUtil.getValueAfterRepalceSpecialWord(UnicodeFilter.filter(article.getTitle()));
					article.setAuthor(article.getAuthor() == null ? "" : article.getAuthor());
					article.setContent(content);
					article.setTitle(title);
					result.add(article);
				} catch (ArticleServiceException e) {
					e.printStackTrace();
				}
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			beans.put("articles", result);
			beans.put("dateFormat", dateFormat);
			beans.put("tag", SpringContextUtil.getBean(TagUtil.class));
			beans.put("const", SpringContextUtil.getBean(ConstUtil.class));

			String path = session.getServletContext().getRealPath("/");
			String targetPath = EXPORTDOC_PATH + "/";
			String fileName = session.getAttribute("fileName").toString();
			String reportFile = targetPath + fileName;
			String templateFile = path + PropertiesUtil.getValue("exceltemplatepath");
			//			String templateFile ="E:\\workspace-fm\\template-article-excel.xls";
			XLSTransformer transformer = new XLSTransformer();
			transformer.markAsFixedSizeCollection("articles");//对应模板所循环的集合名字
			try {
				transformer.transformXLS(templateFile, beans, reportFile);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				EXPORT_SUC = true;
			}
		}
	}
	public String getArticleStage(Integer cid) {
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(cid);
		Boolean viewMonitorData = custConfig.getViewMonitorData();
		String stage = viewMonitorData != null && viewMonitorData ? ArticleConstants.ArticleStage.PUTONG : ArticleConstants.ArticleStage.GUIDANG;
		return stage;
	}

}
