package com.cyyun.fm.search.controller;

import static com.cyyun.fm.constant.PostTimeType.LAST_MONTH;
import static com.cyyun.fm.constant.PostTimeType.LAST_WEEK;
import static com.cyyun.fm.constant.PostTimeType.THIS_MONTH;
import static com.cyyun.fm.constant.PostTimeType.THIS_WEEK;
import static com.cyyun.fm.constant.PostTimeType.THIS_YEAR;
import static com.cyyun.fm.constant.PostTimeType.TODAY;
import static com.cyyun.fm.constant.PostTimeType.YESTERDAY;
import static org.apache.commons.lang3.time.DateFormatUtils.format;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.constant.ArticleConstants;
import com.cyyun.base.constant.ConstantType;
import com.cyyun.base.constant.FMConstant.ARTICLEOREPLY_FLAG;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.AreaService;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.ConstantService;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.IndustryService;
import com.cyyun.base.service.WebsiteService;
import com.cyyun.base.service.bean.AreaBean;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.ConstantBean;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.IndustryBean;
import com.cyyun.base.service.bean.WebsiteBean;
import com.cyyun.base.service.bean.query.ArticleQueryBean;
import com.cyyun.base.service.bean.query.CustTopicQueryBean;
import com.cyyun.base.service.exception.AreaServiceException;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.base.service.exception.ConstantServiceException;
import com.cyyun.base.service.exception.CustTopicServiceException;
import com.cyyun.base.service.exception.IndustryServiceException;
import com.cyyun.base.service.exception.WebsiteServiceException;
import com.cyyun.base.util.ArticleAllUtil;
import com.cyyun.base.util.CyyunBeanUtils;
import com.cyyun.base.util.CyyunCheckParams;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.base.util.DateFormatFactory;
import com.cyyun.base.util.GetCurrentDate;
import com.cyyun.base.util.PropertiesUtil;
import com.cyyun.common.core.base.BaseException;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.common.core.util.StringUtil;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.base.controller.BaseSupport;
import com.cyyun.fm.custtopic.controller.CusttopicSupport;
import com.cyyun.fm.search.bean.ConditionParam;
import com.cyyun.fm.search.bean.ConditionView;
import com.cyyun.fm.search.bean.OptionInfoView;
import com.cyyun.fm.service.ArticleQueryService;
import com.cyyun.fm.service.CustDataCategoryService;
import com.cyyun.fm.service.CustSiteService;
import com.cyyun.fm.service.FmSearchService;
import com.cyyun.fm.service.bean.CustDataCategoryBean;
import com.cyyun.fm.service.bean.CustDataCategoryQueryBean;
import com.cyyun.fm.service.bean.CustDataQueryBean;
import com.cyyun.fm.service.bean.CustSiteBean;
import com.cyyun.fm.service.bean.CustSiteQueryBean;
import com.cyyun.fm.service.bean.QueryConditionBean;
import com.cyyun.fm.service.exception.CustDataCategoryServiceException;
import com.cyyun.fm.service.exception.FmSearchException;
import com.cyyun.fm.vo.ArticleVo;
import com.cyyun.fm.vo.ArticleVoInter;

/**
 * <h3>搜索控制器支持</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Component
public class FmSearchSupport extends BaseSupport {
	private static final Map<String, Integer> TIME = MapBuilder.put("全部", 8).and(TODAY, 1).and(YESTERDAY, 2).and(THIS_WEEK, 3).and(LAST_WEEK, 4).and(THIS_MONTH, 5).and(LAST_MONTH, 6).and(THIS_YEAR, 7).build();

	@Autowired
	private ConstantService constantService;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private AreaService areaService;

	@Autowired
	private IndustryService industryService;

	@Autowired
	private CustTopicService custTopicService;

	@Autowired
	private CustDataCategoryService custDataCategoryService;

	@Autowired
	private CustSiteService siteService;

	@Autowired
	private FmSearchService searchService;
	@Autowired
	private CusttopicSupport custtopicSupport;
	
	@Autowired
	private WebsiteService websiteService;
	
	@Resource(name = "ArticleQueryServiceImpl")
	private ArticleQueryService articleQueryService;
	
	@Resource(name = "FmSupport")
	private BaseSupport fmSupport;

	//###########################################   article   ###########################################//

	public ArticleVoInter findArticle(String guid, String keyword, HttpServletRequest request) throws Exception {
		return articleQueryService.findArticle(guid, keyword, StringUtils.isNotBlank(keyword)?this:fmSupport, request);
//		ArticleBean article = articleService.findArticleDetail(guid);
//		if(StringUtils.isNotBlank(keyword)){
//			Set<String> keywordSet = toStringSet(keyword);
//			showLight(article, keywordSet);
//		}
//		ArticleVo articleVo = new ArticleVo();
//		CyyunBeanUtils.copyProperties(articleVo, article);
//		return articleVo;
	}

	public PageInfoBean<ArticleVoInter> queryArticleByCondition(Integer conditionId, Integer currentpage, Integer pagesize, HttpServletRequest request) throws Exception {
		QueryConditionBean condition = findCondition(conditionId);
		PageInfoBean<ArticleVoInter> pageInfo = queryArticleByPage(condition, currentpage, pagesize, request);
		return pageInfo;
	}
	
	public PageInfoBean<ArticleVoInter> queryArticleByCondition(QueryConditionBean condition, Integer currentpage, Integer pagesize, HttpServletRequest request) throws Exception {
		PageInfoBean<ArticleVoInter> pageInfo = queryArticleByPage(condition, currentpage, pagesize, request);
		CyyunCheckParams.setObjectFieldsEmpty(pageInfo.getData(), "guid","title","websiteName","postTime","guidGroup");
		return pageInfo;
	}

	public PageInfoBean<ArticleVoInter> queryArticleByParam(ConditionParam conditionParam, HttpServletRequest request) throws Exception {
		QueryConditionBean condition = convertParamToCondition(conditionParam);
		return queryArticleByPage(condition, conditionParam.getCurrentpage(), conditionParam.getPagesize(), request);
	}
	
	public List<ArticleBean> exportAllParam(ConditionParam conditionParam, HttpServletRequest request) throws Exception {
		QueryConditionBean condition = convertParamToCondition(conditionParam);
		return queryArticles(condition, request);
	}

	private List<ArticleBean> queryArticles(QueryConditionBean condition, HttpServletRequest request) throws Exception {
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
		ArticleQueryBean query = new ArticleQueryBean();
		if (condition != null) {
			query = convertConditionToArticleQuery(condition);
		}
		Integer customerId = FMContext.getCurrent().getCustomerId();
		query.setCustIds(customerId.toString());
		query.setCustStage(getArticleStage(customerId));
		query.setArticleOreply("2");
//		query.setWithDetail(true);//返回文章内容
		if (condition.getFilterSimilar() != null) {
			if (!condition.getFilterSimilar()) {
				query.setSimilar("0");//值为"0"过滤相似文
			}
		} else {
			//FilterSimilar为false时查询到的文章带有相似文，为true则没有
			if (custConfig.getFilterSimilar()) {
				query.setSimilar("0");//值为"0"过滤相似文
			}
		}
		
		if (!custConfig.getFilterTrash()) {//FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
			query.setCategory("0");
		}
		//临时为国信办客户查询热点数据start	
		//1112为国信办客户cid  
		String hot = request.getParameter("hot");
		if ("hotBoardView".equals(hot)) {
			if (Integer.parseInt("1112") == customerId) {
				query.setHearderWebsiteId(PropertiesUtil.getValue("hearderWebsiteId"));
				query.setHearderArticleFrom(query.getPostStartTime());
				query.setHearderArticleTo(query.getPostEndTime());
			}
		}
		//临时为国信办客户查询热点数据end
		ArticleAllUtil  articleAllUtil = new ArticleAllUtil();
		List<ArticleBean> articles = articleAllUtil.listAll(query, articleService);
		//记录时间范围（用于导出全部Excel功能）
		request.setAttribute("articleStartTime", query.getPostStartTime());
		request.setAttribute("articleEndTime", query.getPostEndTime());
		return articles;
	}
	
	
	
	/**
	 * <h3>通过条件获取文章</h3>
	 * 
	 * @param condition 条件
	 * @param currentpage 当前页
	 * @param pagesize 每页大小
	 * @return 文章(分页)
	 * @throws ArticleServiceException
	 * @throws IndustryServiceException 
	 * @throws AreaServiceException 
	 */
	private PageInfoBean<ArticleVoInter> queryArticleByPage(QueryConditionBean condition, Integer currentpage, Integer pagesize, HttpServletRequest request) throws Exception {
		ArticleQueryBean query = new ArticleQueryBean();
		query.setNeedHighLight(true);
		Integer customerId = FMContext.getCurrent().getCustomerId();
		if (condition != null) {
			query = convertConditionToArticleQuery(condition);
			query.setCustIds(customerId.toString());
			query.setPageNo(currentpage);
			query.setPageSize(pagesize);
			query.setCustStage(getArticleStage(customerId));
			query.setArticleOreply(ARTICLEOREPLY_FLAG.TWO.getValue());
			query.setNumhContent("1");
			query.setSizehContent("300");
		}

		PageInfoBean<ArticleBean> pageInfo = new PageInfoBean<ArticleBean>();
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(customerId);
		if (condition.getFilterSimilar() != null) {
			if (!condition.getFilterSimilar()) {
				query.setSimilar("0");//值为"0"过滤相似文
			}
		} else {
			//FilterSimilar为false时查询到的文章带有相似文，为true则没有
			if (custConfig.getFilterSimilar()) {
				query.setSimilar("0");//值为"0"过滤相似文
			}
		}
		//FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
		if (!custConfig.getFilterTrash()) {
			query.setCategory("0");
		}
		//临时为国信办客户查询热点数据start	
		//1112为国信办客户cid  
		String hot = request.getParameter("hot");
		if ("hotBoardView".equals(hot)) {
			if (Integer.parseInt("1112") == customerId) {
				query.setHearderWebsiteId(PropertiesUtil.getValue("hearderWebsiteId"));
				query.setHearderArticleFrom(query.getPostStartTime());
				query.setHearderArticleTo(query.getPostEndTime());
			}
		}
		//临时为国信办客户查询热点数据end
		
		
		
		pageInfo = articleService.queryArticleByPage(query);
		HttpSession session = request.getSession();
		List<String> custTopicIds = (List<String>) session.getAttribute("CUSTTOPICIDS_WITHSHARE");
		boolean flag = CollectionUtils.isNotEmpty(custTopicIds);
		List<ArticleVoInter> articleVoList = new ArrayList<ArticleVoInter>();
		if(CollectionUtils.isNotEmpty(pageInfo.getData())){
			for(ArticleBean articleBean: pageInfo.getData()){
				ArticleVoInter articleVo = new ArticleVo(this,condition.getKeywords());
				Set<Integer> custAttr = new HashSet<Integer>();
				if (articleBean.getCustAttrs().get(customerId.toString()) != null) {
					Set<Integer> custAttrCustTopicIds = articleBean.getCustAttrs().get(customerId.toString()).getCustTopicIds();
					for (Integer custTopicId : custAttrCustTopicIds) {
						if (flag) {
							/**统计包括分享的专题*/
							if (custTopicIds.contains(custTopicId.toString())) {
								custAttr.add(custTopicId);
							}
						}
					}
					articleBean.getCustAttrs().get(customerId.toString()).setCustTopicIds(custAttr);
				}
				CyyunBeanUtils.copyProperties(articleVo, articleBean);
				articleVoList.add(articleVo);
			}
//			pageInfo.setData(articleVoList);
		}
		return new PageInfoBean<ArticleVoInter>(articleVoList, pageInfo.getTotalRecords(), pageInfo.getPageSize(), pageInfo.getPageNo());
	}
	
//	private PageInfoBean<ArticleVoInter> queryArticleByPage1(QueryConditionBean condition, Integer currentpage, Integer pagesize) throws Exception {
//		ArticleQueryBean query = new ArticleQueryBean();
//		Integer customerId = FMContext.getCurrent().getCustomerId();
//		if (condition != null) {
//			query = convertConditionToArticleQuery(condition);
//			query.setCustIds(customerId.toString());
//			query.setPageNo(currentpage);
//			query.setPageSize(pagesize);
//			query.setCustStage(getArticleStage(customerId));
//			query.setArticleOreply(ARTICLEOREPLY_FLAG.TWO.getValue());
//			query.setNumhContent("1");
//			query.setSizehContent("300");
//		}
//		PageInfoBean<ArticleBean> pageInfo = new PageInfoBean<ArticleBean>();
//		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(customerId);
//		//FilterSimilar为false时查询到的文章带有相似文，为true则没有
//		if(custConfig.getFilterSimilar()){
//			query.setSimilar("0");//值为"0"过滤相似文
//		}
//		
//		if (!custConfig.getFilterTrash()) {
//			query.setCategory("0");
//		}
//		pageInfo = articleService.queryArticleByPage(query);
//		List<ArticleVoInter> articleVoList = new ArrayList<ArticleVoInter>();
//		if(CollectionUtils.isNotEmpty(pageInfo.getData())){
//			for(ArticleBean articleBean: pageInfo.getData()){
//				ArticleVoInter articleVo = new ArticleVo(this,condition.getKeywords());
//				CyyunBeanUtils.copyProperties(articleVo, articleBean);
//				articleVoList.add(articleVo);
//			}
////			pageInfo.setData(articleVoList);
//		}
//		return new PageInfoBean<ArticleVoInter>(articleVoList, pageInfo.getTotalRecords(), pageInfo.getPageSize(), pageInfo.getPageNo());
//	}

	private ArticleQueryBean convertConditionToArticleQuery(QueryConditionBean condition) throws WebsiteServiceException {
		ArticleQueryBean query = new ArticleQueryBean();

		List<Integer> spiderSiteIds = null;
		if (condition.getSiteIds() != null) {
			List<CustSiteBean> custSite = siteService.listSite(condition.getSiteIds());
			spiderSiteIds = BeanUtil.buildListByFieldName(custSite, "spiderSiteId");
		}
		query.setCustIds("" + condition.getCustIds());//遍历客户ID
		query.setCustStage("" + condition.getCustStage());
		query.setKeywords(condition.getKeywords());//关键字
		query.setCustTopicIds(StringUtil.join(condition.getCustTopicIds(), ","));//主题
		query.setIndustryIds(StringUtil.join(condition.getIndustryIds(), ","));//行业
		query.setAreaIds(StringUtil.join(condition.getAreaIds(), ","));//地域
		query.setStyle(StringUtil.join(condition.getSiteTypes(), ","));//媒体类型
		query.setSentiment(StringUtil.join(condition.getSentiment(), ","));//褒贬
		query.setAuthor(condition.getAuthor());//作者
//		query.setWebsiteName(condition.getSiteName());//站点名称
//		query.setParentWebsiteName(condition.getSiteName());
		if (condition.getSiteScope() != null) {
			query.setIsForeignWebsite(condition.getSiteScope().trim());
		}
		setDateForQueryArticle(query, condition);//发布时间

		query.setOrder(condition.getOrderField() != null ? condition.getOrderField().toString() : "1");//排序字段
		query.setArticleOrderBy(condition.getOrderType() != null ? condition.getOrderType().toString() : "0");//排序类型
		if (condition.getSpiderSiteId() != null) {
//			query.setWebsiteId("" + condition.getSpiderSiteId());//站点
			query.setParentWebsiteId("" + condition.getSpiderSiteId());
		} else {
//			query.setWebsiteId(StringUtil.join(spiderSiteIds, ","));//站点
			query.setParentWebsiteId(StringUtil.join(spiderSiteIds, ","));
		}
		query.setWebsiteTagId(condition.getWebsiteTagId());//站点标签ID
		
		if (StringUtils.isNotBlank(condition.getSiteName())) {
			/**字符串中的一个多个空格进行分割*/
			String[] siteNames = condition.getSiteName().split("\\s+");
			List<WebsiteBean> websiteBean = new ArrayList<WebsiteBean>();
			for (String siteName : siteNames) {
				List<WebsiteBean> findWebsiteBean = findWebsiteBean(siteName);
				websiteBean.addAll(findWebsiteBean);
			}
			
			List<Integer> websiteIds = new ArrayList<Integer>();
			for (WebsiteBean websiteBean2 : websiteBean) {
				websiteIds.add(websiteBean2.getFid());
			}
			query.setWebsiteId(StringUtils.join(websiteIds,","));
		}
		
		return query;
	}
	
	private void setDateForQueryArticle(ArticleQueryBean query, QueryConditionBean condition) {
		String postStartTime = condition.getPostStartTime();
		String postEndTime = condition.getPostEndTime();
		Integer timeType = condition.getPostTimeType();
		Date custCreateTime = FMContext.getCurrent().getCustomer().getCreateTime();
		if ((StringUtils.isNotBlank(postStartTime) || StringUtils.isNotBlank(postEndTime)) || timeType != null) {
			String pattern = "yyyy-MM-dd";

			String start = "";
			String end = "";

			if (StringUtils.isNotBlank(postStartTime) || StringUtils.isNotBlank(postEndTime)) {
				start = postStartTime;
				end = postEndTime;

			} else {
				Calendar cal = Calendar.getInstance(Locale.CHINA);
				if (TIME.get(TODAY).equals(timeType)) {
					start = format(cal.getTime(), pattern);
					end = format(cal.getTime(), pattern);

				} else if (TIME.get(YESTERDAY).equals(timeType)) {
					cal.add(Calendar.DATE, -1);//昨天
					start = format(cal.getTime(), pattern);
					end = format(cal.getTime(), pattern);

				} else if (TIME.get(THIS_WEEK).equals(timeType)) {
					end = format(cal.getTime(), pattern);
					if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {//周日为开始，周日需位移至上周
						cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
					}
					cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//本周一
					start = format(cal.getTime(), pattern);

				} else if (TIME.get(LAST_WEEK).equals(timeType)) {
					if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {//周日为开始，周日需位移至上周
						cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
					}
					cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//本周一
					end = format(DateUtils.addDays(cal.getTime(), -1), pattern);
					cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, -1);//上周一
					start = format(cal.getTime(), pattern);

				} else if (TIME.get(THIS_MONTH).equals(timeType)) {
					end = format(cal.getTime(), pattern);
					cal.set(Calendar.DAY_OF_MONTH, 1);//当月一号
					start = format(cal.getTime(), pattern);

				} else if (TIME.get(LAST_MONTH).equals(timeType)) {
					cal.set(Calendar.DAY_OF_MONTH, 1);//当月一号
					end = format(DateUtils.addDays(cal.getTime(), -1), pattern);
					cal.add(Calendar.MONTH, -1);//上月一号
					start = format(cal.getTime(), pattern);

				} else if (TIME.get(THIS_YEAR).equals(timeType)) {
					end = format(cal.getTime(), pattern);
					cal.set(Calendar.DAY_OF_YEAR, 1);//元月一号
					start = format(cal.getTime(), pattern);
				} else if (TIME.get("全部").equals(timeType)) {
					end = format(cal.getTime(), pattern);
					start = format(custCreateTime, pattern);//客户创建时间
				}
			}
			query.setPostStartTime(start);
			query.setPostEndTime(end);
		}
	}
	
	private QueryConditionBean setDateForConditionArticle(QueryConditionBean condition) {
		String postStartTime = condition.getPostStartTime();
		String postEndTime = condition.getPostEndTime();
		Integer timeType = condition.getPostTimeType();
		Date custCreateTime = FMContext.getCurrent().getCustomer().getCreateTime();
		if ((StringUtils.isNotBlank(postStartTime) || StringUtils.isNotBlank(postEndTime)) || timeType != null) {
			String pattern = "yyyy-MM-dd";

			String start = "";
			String end = "";

			if (StringUtils.isNotBlank(postStartTime) || StringUtils.isNotBlank(postEndTime)) {
				start = postStartTime;
				end = postEndTime;

			} else {
				Calendar cal = Calendar.getInstance(Locale.CHINA);
				if (TIME.get(TODAY).equals(timeType)) {
					start = format(cal.getTime(), pattern);
					end = format(cal.getTime(), pattern);

				} else if (TIME.get(YESTERDAY).equals(timeType)) {
					cal.add(Calendar.DATE, -1);//昨天
					start = format(cal.getTime(), pattern);
					end = format(cal.getTime(), pattern);

				} else if (TIME.get(THIS_WEEK).equals(timeType)) {
					end = format(cal.getTime(), pattern);
					if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {//周日为开始，周日需位移至上周
						cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
					}
					cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//本周一
					start = format(cal.getTime(), pattern);

				} else if (TIME.get(LAST_WEEK).equals(timeType)) {
					if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {//周日为开始，周日需位移至上周
						cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
					}
					cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//本周一
					end = format(DateUtils.addDays(cal.getTime(), -1), pattern);
					cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, -1);//上周一
					start = format(cal.getTime(), pattern);

				} else if (TIME.get(THIS_MONTH).equals(timeType)) {
					end = format(cal.getTime(), pattern);
					cal.set(Calendar.DAY_OF_MONTH, 1);//当月一号
					start = format(cal.getTime(), pattern);

				} else if (TIME.get(LAST_MONTH).equals(timeType)) {
					cal.set(Calendar.DAY_OF_MONTH, 1);//当月一号
					end = format(DateUtils.addDays(cal.getTime(), -1), pattern);
					cal.add(Calendar.MONTH, -1);//上月一号
					start = format(cal.getTime(), pattern);

				} else if (TIME.get(THIS_YEAR).equals(timeType)) {
					end = format(cal.getTime(), pattern);
					cal.set(Calendar.DAY_OF_YEAR, 1);//元月一号
					start = format(cal.getTime(), pattern);
				} else if (TIME.get("全部").equals(timeType)) {
					end = format(cal.getTime(), pattern);
					start = format(custCreateTime, pattern);//客户创建时间
				}
			}
			condition.setPostStartTime(start);
			condition.setPostEndTime(end);
		}
		return condition;
	}

	/**
	 * <h3>转换参数为条件</h3>
	 * 
	 * @param 参数        
	 * @return 条件
	 */
	private QueryConditionBean convertParamToCondition(ConditionParam conditionParam) {
		QueryConditionBean conditionBean = new QueryConditionBean();

		conditionBean.setId(conditionParam.getConditionId() != null ? conditionParam.getConditionId() : null);
		conditionBean.setName(conditionParam.getConditionName() != null ? conditionParam.getConditionName() : null);

		conditionBean.setKeywords(CyyunSqlUtil.dealSql(StringUtils.isNotBlank(conditionParam.getKeyword()) ? conditionParam.getKeyword() : null));

		conditionBean.setPostStartTime(conditionParam.getStartTime());
		conditionBean.setPostEndTime(conditionParam.getEndTime());

		conditionBean.setPostTimeType(conditionParam.getTimeType());
		conditionBean.setCustTopicIds(conditionParam.getTopicType() != null ? conditionParam.getTopicType() : null);
		conditionBean.setIndustryIds(conditionParam.getIndustryType() != null ? conditionParam.getIndustryType() : null);
		conditionBean.setAreaIds(conditionParam.getAreaType() != null ? conditionParam.getAreaType() : null);
		conditionBean.setSiteIds(conditionParam.getSiteType() != null ? conditionParam.getSiteType() : null);
		conditionBean.setSiteTypes(conditionParam.getMediaType() != null ? conditionParam.getMediaType() : null);
		conditionBean.setSentiment(conditionParam.getSentiment());
		conditionBean.setAuthor(StringUtils.isNotBlank(conditionParam.getAuthor()) ? conditionParam.getAuthor() : null);
		conditionBean.setSiteName(conditionParam.getSiteName());

		conditionBean.setCreateTime(new Timestamp(System.currentTimeMillis()));
		conditionBean.setUpdateTime(new Timestamp(System.currentTimeMillis()));

		conditionBean.setOrderField(conditionParam.getOrderField());
		conditionBean.setOrderType(conditionParam.getOrderType());

		conditionBean.setCustStage(conditionParam.getCustStage());
		conditionBean.setCustIds(conditionParam.getCustIds());
		conditionBean.setSpiderSiteId(conditionParam.getSpiderSiteId());
		if (conditionParam.getSiteScope() != null) {
			conditionBean.setSiteScope(conditionParam.getSiteScope().trim());
		}
		/**条件未选中过滤，默认值获取*/
		Boolean filterSimilar = conditionParam.getFilterSimilar();
		if (filterSimilar==null) {
			CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
			filterSimilar = custConfig.getFilterSimilar();
		}
		conditionBean.setFilterSimilar(filterSimilar);
		conditionBean.setWebsiteTagId(conditionParam.getWebsiteTagId());
		conditionBean.setHot(conditionParam.getHot());
		return conditionBean;
	}

	/**
	 * <h3>转换条件为视图</h3>
	 * 
	 * @param 参数
	 * @return 条件
	 * @throws AreaServiceException 
	 * @throws ConstantServiceException 
	 * @throws IndustryServiceException 
	 */
	private ConditionView convertConditionToView(QueryConditionBean condition) throws BaseException {
		ConditionView conditionView = new ConditionView();

		conditionView.setId(condition.getId());
		conditionView.setName(condition.getName());
		conditionView.setKeywords(condition.getKeywords());
		conditionView.setAuthor(condition.getAuthor());
		conditionView.setSiteName(condition.getSiteName());

		conditionView.setPostTimeType(condition.getPostTimeType());
		conditionView.setPostStartTime(condition.getPostStartTime());
		conditionView.setPostEndTime(condition.getPostEndTime());
		conditionView.setSentiment(condition.getSentiment());

		conditionView.setOrderField(condition.getOrderField());
		conditionView.setOrderType(condition.getOrderType());
		
		conditionView.setSiteScope(condition.getSiteScope());
		
		conditionView.setWebsiteTagId(condition.getWebsiteTagId());//站点标签id
		conditionView.setHot(condition.getHot());//热点数据标识
		conditionView.setFilterSimilar(condition.getFilterSimilar());//过滤相似文

		if (condition.getCustTopicIds() != null && condition.getCustTopicIds().length > 0) {
			CustTopicQueryBean query = new CustTopicQueryBean();
			query.setDataIds(condition.getCustTopicIds());
			conditionView.setTopics(custTopicService.queryCustTopicsByCondition(query));
		}
		if (condition.getIndustryIds() != null && condition.getIndustryIds().length > 0) {
			conditionView.setIndustrys(industryService.listIndustry(condition.getIndustryIds()));
		}
		if (condition.getAreaIds() != null && condition.getAreaIds().length > 0) {
			conditionView.setAreas(areaService.listArea(condition.getAreaIds()));
		}
		if (condition.getSiteIds() != null && condition.getSiteIds().length > 0) {
			conditionView.setSites(siteService.listSite(condition.getSiteIds()));
		}
		if (condition.getSiteTypes() != null && condition.getSiteTypes().length > 0) {
			List<ConstantBean> medias = ListBuilder.newArrayList();
			List<ConstantBean> result = constantService.listConstantByType(ConstantType.MEDIA_TYPE);
			for (ConstantBean constantBean : result) {
				if (ArrayUtils.contains(condition.getSiteTypes(), Integer.valueOf(constantBean.getValue()))) {
					medias.add(constantBean);
				}
			}
			conditionView.setMedias(medias);
		}

		return conditionView;
	}
	
	private List<String> setGuids(ArticleQueryBean query, Integer totalRecords) throws ArticleServiceException {
		List<String> guids = new ArrayList<String>();
		Long startTime = System.currentTimeMillis();
		int i;
		for (i = 100; i < totalRecords && totalRecords - (i - 100) > 100; i += 100) {//按PageSize=100分页后，需要查询的数量>100则进循环
			query.setPageNo(i / 100);
			query.setPageSize(100);
			guids.addAll(articleService.queryArticleGuidByPage(query));
		}
		if (totalRecords > 100) {//经过上面的for循环处理后，再处理totalRecords-(i-100)的情况
			query.setPageNo(i / 100);
			query.setPageSize(100);
			guids.addAll(articleService.queryArticleGuidByPage(query).subList(0, totalRecords - (i - 100)));
		} else {//totalRecords<100
			query.setPageNo(1);
			query.setPageSize(totalRecords);
			guids.addAll(articleService.queryArticleGuidByPage(query));
		}
		Long endTime = System.currentTimeMillis();
		System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
				"("+FMContext.getLoginUser().getId()+")"+"#----------搜索模块导出全部Excel返回guids耗时#"+(endTime-startTime)+"#毫秒");
		return guids;
	}

	//###########################################   condition   ###########################################//

	public QueryConditionBean saveCondition(Integer userId, ConditionParam conditionParam, QueryConditionBean existCondition) throws FmSearchException {
		QueryConditionBean conditionBean = convertParamToCondition(conditionParam);
		if (existCondition == null) {
			conditionBean.setCreaterId(userId);
			return searchService.addCondition(conditionBean);
		} else {
			conditionBean.setId(existCondition.getId());
			conditionBean.setCreaterId(existCondition.getCreaterId());
			return searchService.updateCondition(conditionBean);
		}
	}

	public void deleteCondition(Integer id) throws FmSearchException {
		searchService.deleteCondition(id);
	}

	public List<QueryConditionBean> listUserCondition(Integer userId) throws FmSearchException {
		return searchService.listUserCondition(userId);
	}

	public QueryConditionBean findCondition(Integer id) throws FmSearchException {
		return searchService.findCondition(id);
	}

	public QueryConditionBean findConditionByName(Integer userId, String name) throws FmSearchException {
		QueryConditionBean existCondition = searchService.findConditionByName(userId, name);
		return existCondition;
	}

	public ConditionView findConditionView(Integer id) throws BaseException {
		QueryConditionBean findCondition = searchService.findCondition(id);
		ConditionView conditionView = convertConditionToView(findCondition);
		return conditionView;
	}

	public ConditionView convertToConditionView(ConditionParam conditionParam) throws BaseException {
		QueryConditionBean condition = convertParamToCondition(conditionParam);
		ConditionView conditionView = convertConditionToView(condition);
		return conditionView;
	}

	public String getStage(Integer cid) {
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(cid);
		Boolean viewMonitorData = custConfig.getViewMonitorData();
		String stage = viewMonitorData != null && viewMonitorData ? ArticleConstants.ArticleStage.PUTONG : ArticleConstants.ArticleStage.GUIDANG;
		return stage;
	}
	
	public QueryConditionBean changeCondition(QueryConditionBean condition) {
		condition = setDateForConditionArticle(condition);
		String postStartTime = condition.getPostStartTime();
		String postEndTime = condition.getPostEndTime();

		SimpleDateFormat formatter = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.DatePattern);
		try {
			long startTime = formatter.parse(postStartTime).getTime();
			long endTime = formatter.parse(postEndTime).getTime();
			long betweenDate = (endTime - startTime) / (1000 * 60 * 60 * 24); //计算间隔多少天;
			if (betweenDate > 7) {
				Calendar cal = Calendar.getInstance(Locale.CHINA);
				String pattern = "yyyy-MM-dd";
				String end = DateFormatUtils.format(cal.getTime(), pattern) + " 23:59:59";
				cal.add(Calendar.DATE, -6);
				String start = DateFormatUtils.format(cal.getTime(), pattern) + " 00:00:00";
				condition.setPostStartTime(start);
				condition.setPostEndTime(end);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return condition;
	}
	
	//###########################################   options   ###########################################//

	public Map<String, Map<String, Integer>> getOptions() throws BaseException {
		return MapBuilder.put("timeType", TIME).and("siteCategorys", this.getsiteCategorys()).build();
	}

	private Map<String, Integer> getsiteCategorys() throws CustDataCategoryServiceException {
		List<CustDataCategoryBean> result = listSiteCustDataCategory();
		Map<String, Integer> type = MapBuilder.newHashMap();
		for (CustDataCategoryBean item : result) {
			type.put(item.getName(), item.getId());
		}
		return type;
	}

	public Map<String, Integer> getIndustryType() throws BaseException {
		List<IndustryBean> result = industryService.listIndustry(new Integer[] {
				1,
				2 });
		Map<String, Integer> type = MapBuilder.newHashMap();
		for (IndustryBean item : result) {
			type.put(item.getName(), item.getId());
		}
		return type;
	}

	public Map<String, Integer> getAreaType() throws BaseException {
		List<AreaBean> result = areaService.listArea(new Integer[] {
				1,
				2,
				3 });
		Map<String, Integer> type = MapBuilder.newHashMap();
		for (AreaBean areaBean : result) {
			type.put(areaBean.getName(), areaBean.getId());
		}
		return type;
	}

	//###########################################   article board   ###########################################//

//	public List<ArticleBean> queryArticleByCondition(Integer conditionId) throws Exception {
//		PageInfoBean<ArticleBean> pageInfo = queryArticleByCondition(conditionId, 1, 6);
//		return pageInfo.getData();
//	}
	
	public List<ArticleVoInter> queryArticleByCondition(QueryConditionBean condition, HttpServletRequest request) throws Exception {
		PageInfoBean<ArticleVoInter> pageInfo = queryArticleByCondition(condition, 1, 6, request);
		return pageInfo.getData();
	}

	//###########################################   topic   ###########################################//

	public List<CustTopicBean> listTopic(Integer parentId) throws CustTopicServiceException {
		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setParentId(parentId);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setStatus(1);
		query.setNeedPaging(false);
		//if (!AuthUtil.hasPermission(PermissionCode.DATA_TOPIC_ALL)) {
		query.setCreaterId(FMContext.getCurrent().getUserId());
		//}
		List<CustTopicBean> topics = null;
		if (parentId == 0) {
			topics = custTopicService.listCustTopicWithShare(query);
		} else {
			query.setCreaterId(null);//子专题为同一用户所创建，避免过滤共享用户的专题
			topics = custTopicService.queryCustTopicsByCondition(query);
		}
		return topics;
	}

	public List<OptionInfoView> listTopicOptionInfo(Integer[] ids) {
		List<OptionInfoView> optionInfos = ListBuilder.newArrayList();
		if (ArrayUtils.isNotEmpty(ids)) {
			List<CustTopicBean> topics = custTopicService.queryCustTopicsAndParentByIds(FMContext.getCurrent().getCustomerId(), ids);
			for (CustTopicBean topic : topics) {
				optionInfos.add(findTopicParent(topic));
			}
		}
		return optionInfos;
	}

	public OptionInfoView findTopicParent(CustTopicBean topic) {
		if (topic == null || topic.getId() == null) {
			return null;
		}

		OptionInfoView infoBean = new OptionInfoView();
		infoBean.setId(topic.getId());
		infoBean.setName(topic.getName());

		OptionInfoView parent = findTopicParent(topic.getParentBean());
		if (parent != null) {
			infoBean.setParent(parent);
		}

		return infoBean;
	}

	//###########################################   industry   ###########################################//

	public List<IndustryBean> listIndustry(Integer parentId) throws IndustryServiceException {
		List<IndustryBean> industrys = null;
		if (parentId == 0) {
			Integer[] industryIds = customerConfigService.findDataIdsByCid(FMContext.getCurrent().getCustomerId(), "industry");
			if (ArrayUtils.isNotEmpty(industryIds)) {
				industrys = industryService.listIndustry(industryIds);
			}
		} else {
			industrys = industryService.listIndustryChildren(parentId);
		}
		return industrys;
	}

	public List<OptionInfoView> listIndustryOptionInfo(Integer[] ids) throws IndustryServiceException {
		List<OptionInfoView> optionInfos = ListBuilder.newArrayList();
		if (ArrayUtils.isNotEmpty(ids)) {
			for (Integer id : ids) {
				IndustryBean industry = industryService.findIndustryById(id);
				optionInfos.add(findIndustryParent(industry));
			}
		}
		return optionInfos;
	}

	public OptionInfoView findIndustryParent(IndustryBean industry) throws IndustryServiceException {
		if (industry == null || industry.getId() == null) {
			return null;
		}
		OptionInfoView infoBean = new OptionInfoView();
		infoBean.setId(industry.getId());
		infoBean.setName(industry.getName());

		IndustryBean parentIndustry = industryService.findIndustryById(industry.getParentId());
		OptionInfoView parent = findIndustryParent(parentIndustry);

		if (parent != null) {
			infoBean.setParent(parent);
		}
		return infoBean;
	}

	//###########################################   area   ###########################################//

	public List<AreaBean> listArea(Integer parentId) throws AreaServiceException {
		List<AreaBean> areas = null;
		if (parentId == 0) {
			Integer[] areaIds = customerConfigService.findDataIdsByCid(FMContext.getCurrent().getCustomerId(), "area");
			if (areaIds!=null && areaIds.length > 0) {
				areas = areaService.listArea(areaIds);
				//			areas = areaService.listAreaChildren(parentId);
			}
		} else {
			areas = areaService.listAreaChildren(parentId);
		}
		return areas;
	}

	public List<OptionInfoView> listAreaOptionInfo(Integer[] ids) throws AreaServiceException {
		List<OptionInfoView> optionInfos = ListBuilder.newArrayList();
		if (ArrayUtils.isNotEmpty(ids)) {
			List<AreaBean> areas = areaService.findAreaWithParent(ids);
			for (AreaBean area : areas) {
				optionInfos.add(findAreaParent(area));
			}
		}
		return optionInfos;
	}

	public OptionInfoView findAreaParent(AreaBean area) {
		if (area == null || area.getId() == null) {
			return null;
		}
		OptionInfoView infoBean = new OptionInfoView();
		infoBean.setId(area.getId());
		infoBean.setName(area.getName());

		OptionInfoView parent = findAreaParent(area.getParentArea());

		if (parent != null) {
			infoBean.setParent(parent);
		}
		return infoBean;
	}

	//###########################################   site   ###########################################//

	public List<CustDataCategoryBean> listSiteCustDataCategory() throws CustDataCategoryServiceException {
		CustDataCategoryQueryBean query = new CustDataCategoryQueryBean();
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setType(3);
		return custDataCategoryService.queryCustDataCategoryBeanListByCondition(query);
	}

	public List<CustSiteBean> listSite() {
		List<CustSiteBean> result = ListBuilder.newArrayList();
		CustSiteQueryBean query = new CustSiteQueryBean();
		query.setNeedPaging(false);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		List<CustSiteBean> sites = siteService.querySite(query).getData();
		if (sites != null) {
			for (CustSiteBean site : sites) {
				if (site.getSpiderSiteId() != null && site.getSpiderSiteId() != 0) {
					result.add(site);
				}
			}
		}
		return result;
	}

	public List<CustSiteBean> listSiteByCategory(Integer categoryId) {
		List<CustSiteBean> result = ListBuilder.newArrayList();
		CustDataQueryBean query = new CustDataQueryBean();
		query.setCateId(categoryId);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		String dataIds = custDataCategoryService.queryCustDataIdsByCondition(query);
		if (StringUtils.isNotBlank(dataIds)) {
			result = siteService.listSite(StringUtil.buildArray(dataIds, ",", Integer.class));
		}
		return result;
	}
	
	void chineseStatementDeal(ConditionParam conditionParam) throws UnsupportedEncodingException {
		if (StringUtils.isNoneBlank(conditionParam.getKeyword())) {
			String keyword = URLDecoder.decode(conditionParam.getKeyword(), "UTF-8");
			conditionParam.setKeyword(keyword);
		}
		if (StringUtils.isNoneBlank(conditionParam.getAuthor())) {
			String author = URLDecoder.decode(conditionParam.getAuthor(), "UTF-8");
			conditionParam.setAuthor(author);
		}
		if (StringUtils.isNoneBlank(conditionParam.getSiteName())) {
			String siteName = URLDecoder.decode(conditionParam.getSiteName(), "UTF-8");
			conditionParam.setSiteName(siteName);
		}
		if (StringUtils.isNotBlank(conditionParam.getStartTime())) {
			String startTime = URLDecoder.decode(conditionParam.getStartTime(), "UTF-8");
			conditionParam.setStartTime(startTime);
		}
		if (StringUtils.isNotBlank(conditionParam.getEndTime())) {
			String endTime = URLDecoder.decode(conditionParam.getEndTime(), "UTF-8");
			conditionParam.setEndTime(endTime);
		}
	}

	public List<WebsiteBean> findWebsiteBean(String siteName) throws WebsiteServiceException {
		List<WebsiteBean> webSiteBean = websiteService.listExactWebSiteBean(siteName);
		if (webSiteBean == null) {
			return new ArrayList<WebsiteBean>();
		}
		return webSiteBean;
	}
	
}
