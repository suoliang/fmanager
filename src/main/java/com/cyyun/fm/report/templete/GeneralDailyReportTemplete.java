package com.cyyun.fm.report.templete;

import java.io.Writer;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.constant.ArticleConstants.ActionType;
import com.cyyun.base.constant.ArticleConstants.ArticleSentiment;
import com.cyyun.base.constant.ArticleConstants.ArticleType;
import com.cyyun.base.constant.ArticleConstants;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.constant.OrderConstants;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.ArticleStatisticService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.query.ArticleQueryBean;
import com.cyyun.base.service.bean.query.ArticleStatisticQueryBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.base.service.exception.ArticleStatisticServiceException;
import com.cyyun.base.util.TagUtil;
import com.cyyun.base.util.UnicodeFilter;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.CustomerService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.customer.service.bean.CustomerBean;
import com.cyyun.fm.service.FmReportService;
import com.cyyun.fm.service.StatCustTopicService;
import com.cyyun.fm.service.bean.ReportTemplateBean;
import com.cyyun.fm.service.bean.StatCustTopicBean;
import com.cyyun.process.service.FocusInfoService;
import com.cyyun.process.service.bean.FocusArticleInfoBean;
import com.cyyun.process.service.bean.FocusInfoBean;
import com.cyyun.process.service.bean.FocusInfoQueryBean;
import com.cyyun.process.service.exception.FocusInfoServiceException;

/**
 * <h3>日报模版</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Component("RT_daily_general")
public class GeneralDailyReportTemplete implements DailyReportTemplete {

	private static final String HTML = "html";
	private static final String DOC = "doc";

	@Autowired
	private DocumentGenerator generator;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private ArticleStatisticService articleStatisticService;

	@Autowired
	private FocusInfoService focusInfoService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerConfigService customerConfigService;

	@Autowired
	private FmReportService fmReportService;

	@Autowired
	private TagUtil tagUtil;
	
	@Autowired
	private StatCustTopicService statCustTopicService;

	@Override
	public void generateHtml(Integer userId, Date date, Writer writer, HttpServletRequest request) {
		try {
			generate(userId, date, writer, HTML, MapBuilder.putObj("contextPath", request.getContextPath()).build(), request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void generateDoc(Integer userId, Date date, Writer writer, HttpServletRequest request) {
		try {
			generate(userId, date, writer, DOC, null, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void generate(Integer userId, Date date, Writer writer, String type, Map<String, Object> outerParams, HttpServletRequest request) throws Exception {
		CustomerBean customer = customerService.findCustomerByUid(userId);

		ReportTemplateBean template = fmReportService.findReportTempleteByCustId(customer.getId());

		if (DateUtils.isSameDay(date, Calendar.getInstance().getTime())) {
			date = Calendar.getInstance().getTime();
		} else {
			date = DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd 23:59:59 999"), "yyyy-MM-dd HH:mm:ss SSS");
		}

		Map<String, Object> params = getParams(userId, customer, date, request);
		if (MapUtils.isNotEmpty(outerParams)) {
			params.putAll(outerParams);
		}

		String docPath = null;
		if (HTML.endsWith(type)) {
			docPath = template.getHtmlTemplate();
		} else {
			docPath = template.getDocTemplate();
		}
		generator.generate(docPath, params, writer);
	}

	private Map<String, Object> getParams(Integer userId, CustomerBean customer, Date date, HttpServletRequest request) throws Exception {

		GeneralDailyReportView report = new GeneralDailyReportView();

		List<FocusArticleInfoBean> focusArticleInfos = queryFocusArticleInfo(customer.getId(), date, request);
		focusArticleInfoBeanUnicodeFilter(focusArticleInfos);
		List<ArticleBean> focusArticles = BeanUtil.buildListByFieldName(focusArticleInfos, "articleBean");
		articleBeanUnicodeFilter(focusArticles);
		List<ArticleBean> topArticles = queryTopArticle(customer.getId(), date);
		articleBeanUnicodeFilter(topArticles);
		List<StatCustTopicBean> statCustTopics = queryStatCustTopic(customer.getId(), date);
		

		Integer negativeTotal = countArticleBySentiment(customer.getId(), date, Integer.parseInt(ArticleSentiment.NEGATIVE));
		Integer positiveTotal = countArticleBySentiment(customer.getId(), date, Integer.parseInt(ArticleSentiment.POSITIVE));
		Integer articleTotal = 0;
		Map<String, Integer> countArticle = countArticleByMediaType(customer.getId(), date);
		for (Entry<String, Integer> entry : countArticle.entrySet()) {
			articleTotal += entry.getValue();
		}
		report.setCustName(customer.getName());
		report.setDailyDate(date);
		report.setArticleTotal(articleTotal);
		report.setNewsTotal(countArticle.get(ArticleType.XINWEN));
		report.setAppTotal(countArticle.get(ArticleType.APPXINWEN));
		report.setWeixinTotal(countArticle.get(ArticleType.WEIXIN));
		report.setWeiboTotal(countArticle.get(ArticleType.WEIBO));
		report.setForumTotal(countArticle.get(ArticleType.LUNTAN));
		report.setBlogTotal(countArticle.get(ArticleType.BOKE));
		report.setPaperTotal(countArticle.get(ArticleType.ZHIMEITI));
		report.setWendaTotal(countArticle.get(ArticleType.WENDA));
		report.setShipinTotal(countArticle.get(ArticleType.SHIPIN));
		report.setNegativeTotal(negativeTotal);//FIXME 日报 - 负面总数
		report.setPositiveTotal(positiveTotal);//FIXME 日报 - 正面总数
		report.setWarningTotal(Integer.parseInt(request.getAttribute("warningTotal").toString()));//今日预警总数
		report.setFocusArticleInfos(focusArticleInfos);
		report.setFocusArticles(focusArticles);
		report.setTopArticles(topArticles);
		report.setStatCustTopics(statCustTopics);

		return MapBuilder.putObj("report", report).build();
	}
	/**
	 * 过滤ArticleBean特殊字符
	 * @param list
	 * @throws Exception 
	 */
	private void articleBeanUnicodeFilter(List<ArticleBean> list) throws Exception{
		for (ArticleBean articleBean : list) {
			articleBean.setTitle(UnicodeFilter.filter(articleBean.getTitle()));
			articleBean.setContent(UnicodeFilter.filter(articleBean.getContent()));
		}
	}
	/**
	 * 过滤focusArticleInfoBean特殊字符
	 * @param list
	 * @throws Exception 
	 */
	private void focusArticleInfoBeanUnicodeFilter(List<FocusArticleInfoBean> list) throws Exception{
		for (FocusArticleInfoBean focusArticleInfoBean : list) {
			focusArticleInfoBean.getFocusInfoBean().setTitle(UnicodeFilter.filter(focusArticleInfoBean.getFocusInfoBean().getTitle()));
			focusArticleInfoBean.getFocusInfoBean().setContent(UnicodeFilter.filter(focusArticleInfoBean.getFocusInfoBean().getContent()));
			
			focusArticleInfoBean.getArticleBean().setTitle(UnicodeFilter.filter(focusArticleInfoBean.getArticleBean().getTitle()));
			focusArticleInfoBean.getArticleBean().setContent(UnicodeFilter.filter(focusArticleInfoBean.getArticleBean().getContent()));
		}
	}
	
	private Integer countArticleBySentiment(Integer custId, Date date, Integer sentiment) throws ArticleStatisticServiceException, ParseException {
		Date beginTime = DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd 00:00:00 000"), "yyyy-MM-dd HH:mm:ss SSS");
		Date endTime = DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd 23:59:59 999"), "yyyy-MM-dd HH:mm:ss SSS");
		ArticleStatisticQueryBean query = new ArticleStatisticQueryBean();
		query.setCustIds("" + custId);
		query.setCustStage(Integer.valueOf(getStage(FMContext.getCurrent().getCustomerId())));
		query.setSentiment(sentiment);
		query.setTimePostFrom(beginTime.getTime());
		query.setTimePostTo(endTime.getTime());
		return articleStatisticService.countArticleInConditions(query);
	}

	private Map<String, Integer> countArticleByMediaType(Integer custId, Date date) throws ArticleServiceException {
		ArticleQueryBean queryBean = new ArticleQueryBean();
		String today = DateFormatUtils.format(date, "yyyy-MM-dd");
		queryBean.setActionType(ActionType.DAY);
		queryBean.setCustStage(getStage(FMContext.getCurrent().getCustomerId()));
//		queryBean.setArticleOreply("2");//查询所有类型
		queryBean.setPostStartTime(today);
		queryBean.setPostEndTime(today);
		queryBean.setCustIds("" + custId);
		CustConfigBean custConfig = customerConfigService
				.findCustConfigByCid(custId);
		// FilterSimilar为false时查询到的文章带有相似文，为true则没有
		if (custConfig.getFilterSimilar()) {
			queryBean.setSimilar("0");//值为"0"过滤相似文
		}
		// FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
		if (!custConfig.getFilterTrash()) {
			queryBean.setCategory("0");
		}
		Map<String, Integer> countArticle = articleService.countArticleStyle(queryBean);
		return countArticle;
	}

	private String getStage(Integer cid) {
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(cid);
		Boolean viewMonitorData = custConfig.getViewMonitorData();
		String stage = viewMonitorData != null && viewMonitorData ? ArticleConstants.ArticleStage.PUTONG : ArticleConstants.ArticleStage.GUIDANG;
		return stage;
	}

	private List<FocusArticleInfoBean> queryFocusArticleInfo(Integer custId, Date date, HttpServletRequest request) throws FocusInfoServiceException, ParseException, ArticleServiceException {
		List<FocusArticleInfoBean> result = ListBuilder.newArrayList();

		//获取聚焦
		Date beginTime = DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd 00:00:00 000"), "yyyy-MM-dd HH:mm:ss SSS");
		Date endTime = DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd 23:59:59 999"), "yyyy-MM-dd HH:mm:ss SSS");
		FocusInfoQueryBean query = new FocusInfoQueryBean();
		query.setStartCreateTime(beginTime);
		query.setEndCreateTime(endTime);
		query.setCustId(custId);
		query.setPageSize(10);
		PageInfoBean<FocusInfoBean> pageInfo = focusInfoService.queryWarningByPage(query);
		request.setAttribute("warningTotal", pageInfo.getTotalRecords());
		List<FocusInfoBean> focusInfos = pageInfo.getData();

		//获取文章
		Map<String, ArticleBean> articleMap = MapBuilder.newHashMap();
		String[] guids = (focusInfos != null && focusInfos.size() > 0) ? BeanUtil.<String, FocusInfoBean> buildArrayByFieldName(focusInfos, "articleId") : new String[] {};
		List<ArticleBean> articles = null;
		if (ArrayUtils.isNotEmpty(guids)) {
			articles = articleService.listArticle(guids);
		}
		if (articles != null) {
			for (ArticleBean article : articles) {
				articleMap.put(article.getGuid(), article);
			}
		}

		//匹配映射
		for (FocusInfoBean focusInfo : focusInfos) {
			FocusArticleInfoBean focusArticleInfo = new FocusArticleInfoBean();
			focusArticleInfo.setFocusInfoBean(focusInfo);
			//focusArticleInfo.setArticleBean(articleService.findArticle(focusInfo.getArticleId()));
			ArticleBean article = articleMap.get(focusInfo.getArticleId());
			if (article != null) {
				String content = TemplateUtil.getValueAfterRepalceTNR(TemplateUtil.getValueAfterRepalceSpecialWord(focusInfo.getContent()));
				article.setContent(content);
			} else {
				article = new ArticleBean();
				article.setGuid(focusInfo.getArticleId());
				article.setTitle(focusInfo.getTitle());
				String content = TemplateUtil.getValueAfterRepalceTNR(TemplateUtil.getValueAfterRepalceSpecialWord(focusInfo.getContent()));
				article.setContent(content);
				article.setUrl(focusInfo.getUrl());
			}
			focusArticleInfo.setArticleBean(article);
			result.add(focusArticleInfo);
		}
		return result;
	}

	private List<ArticleBean> queryTopArticle(Integer custId, Date date) throws ArticleServiceException {
		List<ArticleBean> result = ListBuilder.newArrayList();

		//获取前十名文章
		ArticleQueryBean query = new ArticleQueryBean();
		query.setCustIds("" + custId);
		query.setCustStage(getStage(custId));
		query.setPostStartTime(DateFormatUtils.format(date, "yyyy-MM-dd"));
		query.setPostEndTime(DateFormatUtils.format(date, "yyyy-MM-dd"));
		query.setPageSize(10);
		query.setOrder(OrderConstants.ArticleOrder.READ);
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(custId);
		//FilterSimilar为false时查询到的文章带有相似文，为true则没有
		if(custConfig.getFilterSimilar()){
			query.setSimilar("0");//值为"0"过滤相似文
		}
		//FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
		if (!custConfig.getFilterTrash()) {
			query.setCategory("0");
		}
		PageInfoBean<ArticleBean> pageInfo = articleService.queryArticleByPage(query);
		List<ArticleBean> articles = pageInfo.getData();

		for (ArticleBean article : articles) {
			String content = TemplateUtil.getValueAfterRepalceTNR(TemplateUtil.getValueAfterRepalceSpecialWord(article.getContent()));
			article.setContent(content);
			result.add(article);
		}
		return result;
	}
	
	private List<StatCustTopicBean> queryStatCustTopic(Integer custId, Date date) throws Exception {
		String daily = DateFormatUtils.format(date, FMConstant.DATE_FOMAT_YYYYMMDD);
//		List<StatCustTopicBean> result = ListBuilder.newArrayList();
//		Date beginTime = DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd 00:00:00 000"), "yyyy-MM-dd HH:mm:ss SSS");
//		Date endTime = DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd 23:59:59 999"), "yyyy-MM-dd HH:mm:ss SSS");
//		ArticleStatisticQueryBean query = new ArticleStatisticQueryBean();
//		query.setCustIds("" + custId);//遍历客户ID
//		query.setCustStage(Integer.valueOf(getStage(FMContext.getCurrent().getCustomerId())));
//		query.setTimePostFrom(beginTime.getTime());
//		query.setTimePostTo(endTime.getTime());
//		query.setLimit(10);
//		List<ArticleStatisticBean> articleStatistics = statCustTopicService.countArticleInCustopic(daily, daily);
//		if (articleStatistics != null) {
//			for (ArticleStatisticBean articleStatistic : articleStatistics) {
//				Integer custTopicId = Integer.parseInt(articleStatistic.getCustTopicId());
//				Integer total = articleStatistic.getQuantity().intValue();
//				if (custTopicId == 0) {
//					continue;
//				}
//				StatCustTopicBean statCustTopic = new StatCustTopicBean();
//				statCustTopic.setName(tagUtil.getCustTopicName(custTopicId));
////				statCustTopic.setBeginTime(beginTime);
////				statCustTopic.setEndTime(endTime);
//				statCustTopic.setCustId(FMContext.getCurrent().getCustomerId());
//				statCustTopic.setTopicId(custTopicId);
//				statCustTopic.setCount(total);
//				result.add(statCustTopic);
//			}
//		}
//		return result;
		return statCustTopicService.searchStatCustTopic(daily, daily, FMConstant.CUST_TOPIC_DAILY_TOP);
	}
}