package com.cyyun.fm.search.controller;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import com.alibaba.fastjson.TypeReference;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.bean.AreaBean;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.IndustryBean;
import com.cyyun.base.service.bean.WebsiteBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.base.util.ConstUtil;
import com.cyyun.base.util.GetCurrentDate;
import com.cyyun.base.util.PropertiesUtil;
import com.cyyun.base.util.TagUtil;
import com.cyyun.base.util.UnicodeFilter;
import com.cyyun.base.util.ExportExcelUtil;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.JsonUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.common.core.util.SpringContextUtil;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.report.templete.TemplateUtil;
import com.cyyun.fm.search.bean.ConditionParam;
import com.cyyun.fm.search.bean.ConditionView;
import com.cyyun.fm.search.bean.OptionInfoView;
import com.cyyun.fm.service.QueryLogService;
import com.cyyun.fm.service.bean.CustSiteBean;
import com.cyyun.fm.service.bean.QueryConditionBean;
import com.cyyun.fm.vo.ArticleVoInter;

/**
 * <h3>搜索控制器</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Controller
@RequestMapping("/search")
public class FmSearchController extends BaseController {

	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;
	public static final String EXPORTDOC_PATH = PropertiesUtil.getValue("exportDocpath");
	public static Boolean EXPORT_SUC = false;

	@Autowired
	private FmSearchSupport support;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private VelocityConfigurer velocityConfig;
	@Autowired
	private CustomerConfigService customerConfigService;
	@Autowired
	private QueryLogService queryLogService;
	@Autowired
	private ExportExcelUtil exportExcelUtil;

	/**
	 * <h3>搜索页面</h3>
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("index")
	public ModelAndView index(ConditionParam conditionParam, HttpServletRequest request) {
		try {
			Long startTime = System.currentTimeMillis();
			//参数中文乱码处理
			support.chineseStatementDeal(conditionParam);

			Integer pageNo = conditionParam.getCurrentpage() == null ? PAGE_NO : conditionParam.getCurrentpage();
			Integer pageSize = conditionParam.getPagesize() == null ? PAGE_SIZE : conditionParam.getPagesize();

			ModelAndView view = view("/search/index");
			view.addAllObjects(support.getOptions());
			view.addObject("filters", support.listUserCondition(FMContext.getCurrent().getUserId()));// filters
			PageInfoBean<ArticleVoInter> pageInfo = null;// articles
			if (conditionParam.getConditionId() != null) {// by condition 
				ConditionView conditionView = support.findConditionView(conditionParam.getConditionId());
				view.addObject("condition", JsonUtil.toJson(conditionView));
				pageInfo = support.queryArticleByCondition(conditionParam.getConditionId(), PAGE_NO, PAGE_SIZE, request);
			} else if (conditionParam.getType() != null) {//首页推荐板块跳转
				conditionParam.setWebsiteTagId(conditionParam.getType().toString());//type对应板块的数据来源WebsiteTagId
				conditionParam.setOrderField(7);//按重要性排序
				conditionParam.setTimeType(1);//今日
				CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
				conditionParam.setFilterSimilar(!custConfig.getFilterSimilar());
				view.addObject("websiteTagId", conditionParam.getType());
				pageInfo = support.queryArticleByParam(conditionParam, request);
				pageInfo.setPageSize(10);//设置为10 控制滚动分页的条数
				ConditionView conditionView = new ConditionView();
				conditionView.setOrderField(7);//页面选择重要性排序
				String today = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
				conditionView.setPostStartTime(today + " 00:00:00");
				conditionView.setPostEndTime(today + " 23:59:59");
				conditionView.setFilterSimilar(conditionParam.getFilterSimilar());
				view.addObject("condition", JsonUtil.toJson(conditionView));
			} else {// by default
				conditionParam.setCurrentpage(pageNo);
				conditionParam.setPagesize(pageSize);
				if (StringUtils.isBlank(conditionParam.getStartTime()) && StringUtils.isBlank(conditionParam.getStartTime())) {
					conditionParam.setTimeType(conditionParam.getTimeType() != null ? conditionParam.getTimeType() : 3);
				}
				if (conditionParam.getFilterSimilar() ==null) {
					CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
					conditionParam.setFilterSimilar(!custConfig.getFilterSimilar());
				}
				//临时为国信办客户查询热点数据start
				String hot = request.getParameter("hot");
				if ("hotBoardView".equals(hot)) {
					view.addObject("hot", hot);
				}
				//临时为国信办客户查询热点数据end
				view.addObject("condition", JsonUtil.toJson(support.convertToConditionView(conditionParam)));
				pageInfo = support.queryArticleByParam(conditionParam, request);
			}
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------搜索模块加载耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "SearchLoading", 3000L);
			view.addObject("pageInfo", pageInfo);
			return view;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载搜索页面失败");
		}
	}

	/**
	 * <h3>搜索页面</h3>
	 */
	@RequestMapping("searchArticle")
	public ModelAndView searchArticle(ConditionParam conditionParam, HttpServletRequest request) {
		try {
			Long startTime = System.currentTimeMillis();
			/**自定义时间为空时默认全部*/
			if (StringUtils.isBlank(conditionParam.getStartTime()) && StringUtils.isBlank(conditionParam.getStartTime())) {
				conditionParam.setTimeType(conditionParam.getTimeType() != null ? conditionParam.getTimeType() : 3);
			}
			PageInfoBean<ArticleVoInter> pageInfo = new PageInfoBean<ArticleVoInter>(new ArrayList<ArticleVoInter>(), 0, 10, 1);
			if (StringUtils.isBlank(conditionParam.getSiteName())) {
				pageInfo = support.queryArticleByParam(conditionParam, request);
			} else {
				/**字符串中的一个多个空格进行分割*/
				String[] siteNames = conditionParam.getSiteName().split("\\s+");
				List<WebsiteBean> websiteBean = new ArrayList<WebsiteBean>();
				for (String siteName : siteNames) {
					List<WebsiteBean> findWebsiteBean = support.findWebsiteBean(siteName);
					websiteBean.addAll(findWebsiteBean);
				}
				
				if (websiteBean.size() >= 200) {
					return message(MESSAGE_TYPE_QUESTION, "关键词匹配的站点过多，请调整站点关键词后查询！");
				}
				if (websiteBean.size() != 0) {
					pageInfo = support.queryArticleByParam(conditionParam, request);
				}
			}
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------搜索模块加载耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "SearchLoading", 3000L);
			//			PageInfoBean<ArticleVoInter> pageInfo = support.queryArticleByParam1(conditionParam);
			return view("/search/index-paging-article").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询文章失败");
		}
	}

	/**
	 * <h3>文章详情</h3>
	 */
	@RequestMapping("findArticle")
	public ModelAndView findArticle(String guid, String keyword,HttpServletRequest request) {
		try {
			return view("/common/articleDetail/detail").addObject("article", support.findArticle(guid, keyword, request)).addObject("TOPICDISPLAY", FMConstant.TOPIC_DISPLAY.none.getValue());
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
			return view("/common/articleDetail/article-detail").addObject("guid", guid).addObject("keyword", keyword);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询文章失败");
		}
	}

	//###########################################   condition   ###########################################//

	/**
	 * <h3>保存过滤器</h3>
	 */
	@RequestMapping("saveCondition")
	@ResponseBody
	public MessageBean saveCondition(ConditionParam conditionParam) {
		try {
			QueryConditionBean existCondition = support.findConditionByName(FMContext.getCurrent().getUserId(), conditionParam.getConditionName());
			if (existCondition != null && !conditionParam.getForce()) {
				return buildMessage(MESSAGE_TYPE_QUESTION, "是否强制更新", existCondition);
			}
			QueryConditionBean conditionBean = support.saveCondition(FMContext.getCurrent().getUserId(), conditionParam, existCondition);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", conditionBean);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "保存过滤器失败");
		}
	}

	/**
	 * <h3>删除过滤器</h3>
	 */
	@RequestMapping("deleteCondition")
	@ResponseBody
	public MessageBean deleteCondition(Integer id) {
		try {
			support.deleteCondition(id);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "删除过滤器失败");
		}
	}

	/**
	 * <h3>检索过滤器</h3>
	 */
	@RequestMapping("listCondition")
	@ResponseBody
	public MessageBean listCondition() {
		try {
			List<QueryConditionBean> filters = support.listUserCondition(FMContext.getCurrent().getUserId());
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", filters);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索过滤器失败");
		}
	}

	/**
	 * <h3>查找过滤器</h3>
	 */
	@RequestMapping("findCondition")
	@ResponseBody
	public MessageBean findCondition(Integer id) {
		try {
			ConditionView conditionView = support.findConditionView(id);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", conditionView);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "查找过滤器失败");
		}
	}

	//###########################################   board   ###########################################//

	/**
	 * <h3>文章板块视图</h3>
	 */
	@RequestMapping("articleBoardView")
	public ModelAndView articleBoardView(Integer conditionId, HttpServletRequest request) {
		ModelAndView view = view("/search/article-board-view");
		try {
			Long startTime = System.currentTimeMillis();
			QueryConditionBean condition = support.findCondition(conditionId);
			if (condition != null) {
				view.addObject("keyword", condition.getKeywords());
				condition = support.changeCondition(condition);//自定义时间小于7天则按自定义时间查询，大于7天则按近7天查询
				view.addObject("articles", support.queryArticleByCondition(condition, request));
			} else {
				view.addObject("isDelete", true);
				view.addObject("articles", ListBuilder.newArrayList());
			}
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------文章板块查询耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "ArticleBoard", 3000L);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "文章板块加载失败");
		}
		return view;
	}

	/**
	 * <h3>文章板块配置</h3>
	 */
	@RequestMapping("articleBoardInput")
	public ModelAndView articleBoardInput(final Integer conditionId, String userConfigInfo) {
		ModelAndView view = view("/search/article-board-input");
		try {
			final List<Map<String, String>> list = JsonUtil.getObject(userConfigInfo, new TypeReference<List<Map<String, String>>>() {
			});

			if (conditionId != null) {
				view.addObject("conditionId", conditionId);
			}

			List<QueryConditionBean> conditions = support.listUserCondition(FMContext.getCurrent().getUserId());

			conditions = ListBuilder.newRecombinedList(conditions, new ListBuilder.Combiner<QueryConditionBean>() {

				public boolean isUseable(QueryConditionBean condition) {
					for (Map<String, String> param : list) {
						if (!("" + conditionId).equals(param.get("conditionId")) && ("" + condition.getId()).equals(param.get("conditionId"))) {
							return false;
						}
					}
					return true;
				}
			});

			view.addObject("conditions", conditions);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "文章板块配置加载失败");
		}
		return view;
	}

	/**
	 * <h3>推荐板块视图</h3>
	 * @param  用于区分 国内、国际、社会...等九个板块
	 * 		   type对应板块的数据来源WebsiteTagId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = { "/recommendedView" })
	public ModelAndView recommendedView(Integer type, HttpServletRequest request) {
		//特殊推荐板块处理
		if (type.equals(1)) {//词云
			return new ModelAndView("redirect://home/hotKeyWords.htm");
		} else if (type.equals(2)) {//系统通知
			return new ModelAndView("redirect://home/inform.htm");
		} else if (type.equals(3)) {//热点
			request.setAttribute("hot", "hotBoardView");
			return new ModelAndView("redirect://search/hotBoardView.htm?hot=hotBoardView");
		}
		ModelAndView view = view("/search/article-board-view");
		try {
			QueryConditionBean condition = new QueryConditionBean();
			condition.setCustIds(FMContext.getCurrent().getCustomerId().toString());
			condition.setWebsiteTagId(type != null ? type.toString() : null);
			condition.setOrderField(1);//按发文时间排序
			String today = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
			condition.setPostStartTime(today+" 00:00:00");
			condition.setPostEndTime(today+" 23:59:59");
			view.addObject("articles", support.queryArticleByCondition(condition, request));
			return view;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "推荐板块加载失败");
		}
	}
	
	/**
	 * <h3>热点板块视图</h3>
	 * @param  
	 * @param request
	 * @return
	 */
	@RequestMapping(value = { "/hotBoardView" })
	public ModelAndView hotBoardView(HttpServletRequest request) {
		ModelAndView view = view("/search/article-board-view");
		try {
			QueryConditionBean condition = new QueryConditionBean();
			condition.setCustIds(FMContext.getCurrent().getCustomerId().toString());
			condition.setOrderField(1);//按发文时间排序
			//------------
			// 最近3天
			Calendar cal = Calendar.getInstance(Locale.CHINA);
			String pattern = "yyyy-MM-dd";
			String end = DateFormatUtils.format(cal.getTime(), pattern) + " 23:59:59";
			cal.add(Calendar.DATE, -2);
			String start = DateFormatUtils.format(cal.getTime(), pattern) + " 00:00:00";
			//------------
			condition.setPostStartTime(start);
			condition.setPostEndTime(end);
			Long startTime = System.currentTimeMillis();
			view.addObject("articles", support.queryArticleByCondition(condition, request));
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------热点【系统】查询耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "HotSystem", 3000L);
			return view;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "热点板块加载失败");
		}
	}

	//###########################################   topic   ###########################################//

	/**
	 * <h3>检索专题</h3>
	 */
	@RequestMapping("topic/list")
	@ResponseBody
	public MessageBean listTopic(Integer parentId) {
		try {
			List<CustTopicBean> topics = support.listTopic(parentId);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", topics);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索专题失败");
		}
	}

	/**
	 * <h3>检索专题(父级联)</h3>
	 */
	@RequestMapping("topic/listTopicOptionInfo")
	@ResponseBody
	public MessageBean listTopicOptionInfo(Integer[] ids) {
		try {
			List<OptionInfoView> options = support.listTopicOptionInfo(ids);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", options);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索专题(父级联)失败");
		}
	}

	//###########################################   industry   ###########################################//

	/**
	 * <h3>检索行业</h3>
	 */
	@RequestMapping("industry/list")
	@ResponseBody
	public MessageBean listIndustry(Integer parentId) {
		try {
			List<IndustryBean> industrys = support.listIndustry(parentId);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", industrys);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索行业失败");
		}
	}

	/**
	 * <h3>检索行业(父级联)</h3>
	 */
	@RequestMapping("industry/listIndustryOptionInfo")
	@ResponseBody
	public MessageBean listIndustryOptionInfo(Integer[] ids) {
		try {
			List<OptionInfoView> options = support.listIndustryOptionInfo(ids);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", options);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索行业(父级联)失败");
		}
	}

	//###########################################   area   ###########################################//

	/**
	 * <h3>获取地域</h3>
	 */
	@RequestMapping("area/list")
	@ResponseBody
	public MessageBean listArea(Integer parentId) {
		try {
			List<AreaBean> areas = support.listArea(parentId);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", areas);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "获取地域失败");
		}
	}

	/**
	 * <h3>获取地域(父级联)</h3>
	 * 
	 * @return JSON
	 */
	@RequestMapping("area/listAreaOptionInfo")
	@ResponseBody
	public MessageBean listAreaOptionInfo(Integer[] ids) {
		try {
			List<OptionInfoView> options = support.listAreaOptionInfo(ids);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", options);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "获取地域(父级联)失败");
		}
	}

	//###########################################   site   ###########################################//

	/**
	 * <h3>获取站点数据</h3>
	 */
	@RequestMapping("site/list")
	@ResponseBody
	public MessageBean listSite() {
		try {
			List<CustSiteBean> sites = support.listSite();
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", sites);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "获取站点数据类型失败");
		}
	}

	/**
	 * <h3>获取站点数据</h3>
	 */
	@RequestMapping("site/listSiteByCategory")
	@ResponseBody
	public MessageBean listSiteByCategory(Integer categoryId) {
		try {
			List<CustSiteBean> sites = support.listSiteByCategory(categoryId);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", sites);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "获取站点数据类型失败");
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
//			CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
//			custConfig.setFilterSimilar(true);//查询到的文章带有相似文
//			List<String> guids = support.exportAllParam(conditionParam, custConfig, request);
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
			List<ArticleBean> articles = support.exportAllParam(conditionParam, request);
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
					content = TemplateUtil.getValueAfterRepalceTNR(UnicodeFilter.filter(TemplateUtil.getValueAfterRepalceSpecialWord(article.getContent())));
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
					article.setContent(content);
					article.setTitle(title);
					article.setAuthor(article.getAuthor() == null ? "" : article.getAuthor());
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
}