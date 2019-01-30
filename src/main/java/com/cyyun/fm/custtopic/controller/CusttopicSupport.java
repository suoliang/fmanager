package com.cyyun.fm.custtopic.controller;

import static com.cyyun.fm.constant.PostTimeType.LAST_MONTH;
import static com.cyyun.fm.constant.PostTimeType.LAST_WEEK;
import static com.cyyun.fm.constant.PostTimeType.THIS_MONTH;
import static com.cyyun.fm.constant.PostTimeType.THIS_WEEK;
import static com.cyyun.fm.constant.PostTimeType.THIS_YEAR;
import static com.cyyun.fm.constant.PostTimeType.TODAY;
import static com.cyyun.fm.constant.PostTimeType.YESTERDAY;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.cyyun.base.constant.ConstantType;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.ArticleStatisticService;
import com.cyyun.base.service.ConstantService;
import com.cyyun.base.service.CustTopicKeywordService;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.KeywordService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.ConstantBean;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.query.ArticleQueryBean;
import com.cyyun.base.service.bean.query.ArticleStatisticQueryBean;
import com.cyyun.base.service.bean.query.CustTopicQueryBean;
import com.cyyun.base.service.exception.AreaServiceException;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.base.service.exception.ArticleStatisticServiceException;
import com.cyyun.base.service.exception.ConstantServiceException;
import com.cyyun.base.service.exception.CustTopicServiceException;
import com.cyyun.base.service.exception.IndustryServiceException;
import com.cyyun.base.util.ArticleAllUtil;
import com.cyyun.base.util.CyyunBeanUtils;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.base.util.GetCurrentDate;
import com.cyyun.common.core.base.BaseException;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.common.core.util.StringUtil;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.base.controller.BaseSupport;
import com.cyyun.fm.custtopic.bean.ConditionParam;
import com.cyyun.fm.custtopic.bean.ConditionView;
import com.cyyun.fm.custtopic.bean.CustTopicView;
import com.cyyun.fm.service.ArticleQueryService;
import com.cyyun.fm.service.CustDataCategoryService;
import com.cyyun.fm.service.CustSiteService;
import com.cyyun.fm.service.FmSupportService;
import com.cyyun.fm.service.bean.CustDataCategoryBean;
import com.cyyun.fm.service.bean.CustDataCategoryQueryBean;
import com.cyyun.fm.service.bean.CustSiteBean;
import com.cyyun.fm.service.bean.QueryConditionBean;
import com.cyyun.fm.service.exception.CustDataCategoryServiceException;
import com.cyyun.fm.vo.ArticleVo;
import com.cyyun.fm.vo.ArticleVoInter;

@Component("CusttopicSupport")
public class CusttopicSupport extends BaseSupport implements FmSupportService {
	private static final Map<String, Integer> TIME = MapBuilder.put(TODAY, 1).and(YESTERDAY, 2).and(THIS_WEEK, 3).and(LAST_WEEK, 4).and(THIS_MONTH, 5).and(LAST_MONTH, 6).and(THIS_YEAR, 7).build();

	@Autowired
	private CustTopicService custTopicService;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private CustSiteService siteService;
	@Autowired
	private CustDataCategoryService custDataCategoryService;
	@Autowired
	private ArticleStatisticService articleStatisticService;
	@Autowired
	private KeywordService keywordService;

	@Autowired
	private CustTopicKeywordService custTopicKeywordService;

	@Resource(name = "ArticleQueryServiceImpl")
	private ArticleQueryService articleQueryService;

	@Resource(name = "FmSupport")
	private BaseSupport fmSupport;

	public List<CustTopicView> listTopic(Integer parentId) throws CustTopicServiceException {
		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setParentId(parentId);
		//query.setCreaterId(FMContext.getCurrent().getUserId());
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setStatus(1);
		//if (!AuthUtil.hasPermission(PermissionCode.DATA_TOPIC_ALL)) {
		query.setCreaterId(FMContext.getCurrent().getUserId());
		//}
		query.setNeedPaging(false);/**查询所有，不分页查询*/
		List<CustTopicView> topics = new ArrayList<CustTopicView>();
		if (parentId == 0) {
			CustTopicView topicViewAll = new CustTopicView();
			topicViewAll.setId(getAllCustTopicId());
			topicViewAll.setName("全部专题");
			topics.add(topicViewAll);
			List<CustTopicBean> topicWithShare = custTopicService.listCustTopicWithShare(query);
			for (CustTopicBean custTopicBean : topicWithShare) {
				CustTopicView topicView = new CustTopicView();
				topicView.setId(custTopicBean.getId().toString());
				topicView.setName(custTopicBean.getName());
				topicView.setSubCount(custTopicBean.getSubCount());
				topics.add(topicView);
			}
			//2016-4-14 注释 “其它”专题  （用“其它”专题 查询到的是没标专题的文章）
			//			CustTopicBean qita = new CustTopicBean();
			//			qita.setId(0);
			//			qita.setName("其它");
			//			topics.add(qita);
		} else {
			query.setCreaterId(null);
			List<CustTopicBean> queryCustTopicsByCondition = custTopicService.queryCustTopicsByCondition(query);
			for (CustTopicBean custTopicBean : queryCustTopicsByCondition) {
				CustTopicView topicView = new CustTopicView();
				topicView.setId(custTopicBean.getId().toString());
				topicView.setName(custTopicBean.getName());
				topicView.setSubCount(custTopicBean.getSubCount());
				topics.add(topicView);
			}
		}

		return topics;
	}

	public String getAllCustTopicId() throws CustTopicServiceException {

		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setParentId(0);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setStatus(1);
		query.setCreaterId(FMContext.getCurrent().getUserId());
		query.setNeedPaging(false);/**查询所有，不分页查询*/
		List<CustTopicBean> topics = custTopicService.listCustTopicWithShare(query);
		StringBuffer custTopics = new StringBuffer();

		getCustTopicIds(topics, custTopics);

		return custTopics.toString();
	}

	public Integer[] stringToIntegerArray(String custTopics) {
		String[] strings = custTopics.toString().split("\\,");
		if (strings.length < 1) {
			return ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
		}
		if (strings.length == 1) {
			if (strings[0].isEmpty()) {
				return ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
			}
		}
		Integer array[] = new Integer[strings.length];
		for (int i = 0; i < strings.length; i++) {
			array[i] = Integer.parseInt(strings[i]);
		}
		return array;
	}

	public String integerArrayToString(Integer[] ids) {
		if (ArrayUtils.isEmpty(ids)) {
			return StringUtils.EMPTY;
		}
		StringBuffer id = new StringBuffer();
		for (Integer integer : ids) {
			id.append(integer.toString() + ",");
		}
		return id.toString();
	}

	public void getCustTopicIds(List<CustTopicBean> topics, StringBuffer custTopics) throws CustTopicServiceException {
		for (CustTopicBean custTopicBean : topics) {
			CustTopicQueryBean query = new CustTopicQueryBean();
			query.setParentId(custTopicBean.getId());
			query.setCustId(FMContext.getCurrent().getCustomerId());
			query.setStatus(1);
			query.setCreaterId(FMContext.getCurrent().getUserId());
			custTopics = custTopics.append(custTopicBean.getId() + ",");
			List<CustTopicBean> sonCustTopic = custTopicService.listCustTopicWithShare(query);

			getCustTopicIds(sonCustTopic, custTopics);
		}
	}

	public PageInfoBean<ArticleVoInter> queryArticleByParam(ConditionParam conditionParam, HttpServletRequest request) throws Exception {
		QueryConditionBean condition = convertParamToCondition(conditionParam);
		return queryArticleByPage(condition, conditionParam.getCurrentpage(), conditionParam.getPagesize(), request);
	}

	public List<ArticleBean> exportAllParam(ConditionParam conditionParam, CustConfigBean custConfig, HttpServletRequest request) throws Exception {
		QueryConditionBean condition = convertParamToCondition(conditionParam);
		List<ArticleBean> resultList = queryArticles(condition, custConfig, request);
		return resultList;
	}

	public ConditionView convertToConditionView(ConditionParam conditionParam) throws BaseException {
		QueryConditionBean condition = convertParamToCondition(conditionParam);
		ConditionView conditionView = convertConditionToView(condition);
		return conditionView;
	}

	/**
	 * <h3>转换参数为条件</h3>
	 * 
	 * @param 参数
	 * @return 条件
	 */
	public QueryConditionBean convertParamToCondition(ConditionParam conditionParam) {
		QueryConditionBean conditionBean = new QueryConditionBean();
		conditionBean.setKeywords(CyyunSqlUtil.dealSql(StringUtils.isNotBlank(conditionParam.getKeyword()) ? conditionParam.getKeyword() : null));

		conditionBean.setPostStartTime(conditionParam.getStartTime());
		conditionBean.setPostEndTime(conditionParam.getEndTime());

		conditionBean.setPostTimeType(conditionParam.getTimeType());
		conditionBean.setSiteTypes(conditionParam.getMediaType() != null ? conditionParam.getMediaType() : null);

		conditionBean.setCreateTime(new Timestamp(System.currentTimeMillis()));
		conditionBean.setUpdateTime(new Timestamp(System.currentTimeMillis()));

		conditionBean.setOrderField(conditionParam.getOrderField());
		conditionBean.setOrderType(conditionParam.getOrderType());

		conditionBean.setCustTopicIds(conditionParam.getTopicId());//给专题ID数组赋值
		conditionBean.setFilterSimilar(conditionParam.getFilterSimilar());
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

		conditionView.setPostTimeType(condition.getPostTimeType());
		conditionView.setPostStartTime(condition.getPostStartTime());
		conditionView.setPostEndTime(condition.getPostEndTime());
		conditionView.setSentiment(condition.getSentiment());

		conditionView.setOrderField(condition.getOrderField());
		conditionView.setOrderType(condition.getOrderType());
		conditionView.setFilterSimilar(condition.getFilterSimilar());

		if (condition.getCustTopicIds() != null && condition.getCustTopicIds().length > 0) {
			CustTopicQueryBean query = new CustTopicQueryBean();
			query.setDataIds(condition.getCustTopicIds());
			conditionView.setTopics(custTopicService.queryCustTopicsByCondition(query));
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

	public List<CustDataCategoryBean> listSiteCustDataCategory() throws CustDataCategoryServiceException {
		CustDataCategoryQueryBean query = new CustDataCategoryQueryBean();
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setType(3);
		return custDataCategoryService.queryCustDataCategoryBeanListByCondition(query);
	}

	/**
	 * postEndTime的时分秒是23:59:59
	 * @param query
	 * @param condition
	 */
	private void setDateForQueryArticle(ArticleQueryBean query, QueryConditionBean condition) {
		String postStartTime = condition.getPostStartTime();
		String postEndTime = condition.getPostEndTime();
		Integer timeType = condition.getPostTimeType();
		String start = "";
		String end = "";
		String pattern = "yyyy-MM-dd";
		//		String pattern1 = "yyyy-MM-dd HH:mm:ss";
		String begine = " 00:00:00";
		String ending = " 23:59:59";
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		if (StringUtils.isNotBlank(postStartTime) || StringUtils.isNotBlank(postEndTime)) {
			start = postStartTime;
			end = postEndTime;
		} else if (timeType != null) {
			if (TIME.get(TODAY).equals(timeType)) {// 当天
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
			} else if (TIME.get(YESTERDAY).equals(timeType)) {
				cal.add(Calendar.DATE, -1);// 昨天
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
			} else if (TIME.get(THIS_WEEK).equals(timeType)) {// 本周
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 本周一
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(LAST_WEEK).equals(timeType)) {// 上周
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 上周日
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.add(Calendar.WEEK_OF_MONTH, -1);// 上周
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 上周一
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(THIS_MONTH).equals(timeType)) {// 当月
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.set(Calendar.DAY_OF_MONTH, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(LAST_MONTH).equals(timeType)) {// 上月
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.set(Calendar.DAY_OF_MONTH, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(THIS_YEAR).equals(timeType)) {// 本年
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.set(Calendar.DAY_OF_YEAR, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			}
		}
		query.setPostStartTime(start);
		query.setPostEndTime(end);
	}

	/**
	 * 废弃方法（postEndTime的时分秒是系统当前时间）
	 */
	//	private void setDateForQueryArticle(ArticleQueryBean query,
	//			QueryConditionBean condition) {
	//		String postStartTime = condition.getPostStartTime();
	//		String postEndTime = condition.getPostEndTime();
	//		Integer timeType = condition.getPostTimeType();
	//		String start = "";
	//		String end = "";
	//		String pattern = "yyyy-MM-dd";
	//		String pattern1 = "yyyy-MM-dd HH:mm:ss";
	//		String begine = " 00:00:00";
	//		String ending = " 23:59:59";
	//		Calendar cal = Calendar.getInstance(Locale.CHINA);
	//		if (StringUtils.isNotBlank(postStartTime)
	//				|| StringUtils.isNotBlank(postEndTime)) {
	//			start = postStartTime;
	//			end = postEndTime;
	//		} else if (timeType != null) {
	//			if (TIME.get(TODAY).equals(timeType)) {// 当天
	//				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
	//				end = DateFormatUtils.format(cal.getTime(), pattern1);
	//			} else if (TIME.get(YESTERDAY).equals(timeType)) {
	//				cal.add(Calendar.DATE, -1);// 昨天
	//				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
	//				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
	//			} else if (TIME.get(THIS_WEEK).equals(timeType)) {// 本周
	//				end = DateFormatUtils.format(cal.getTime(), pattern1);
	//				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 本周一
	//				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
	//			} else if (TIME.get(LAST_WEEK).equals(timeType)) {// 上周
	//				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 上周日
	//				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
	//				cal.add(Calendar.WEEK_OF_MONTH, -1);// 上周
	//				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 上周一
	//				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
	//			} else if (TIME.get(THIS_MONTH).equals(timeType)) {// 当月
	//				end = DateFormatUtils.format(cal.getTime(), pattern1);
	//				cal.set(Calendar.DAY_OF_MONTH, 1);
	//				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
	//			} else if (TIME.get(LAST_MONTH).equals(timeType)) {// 上月
	//				cal.set(Calendar.DAY_OF_MONTH, 1);
	//				cal.add(Calendar.DAY_OF_MONTH, -1);
	//				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
	//				cal.set(Calendar.DAY_OF_MONTH, 1);
	//				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
	//			} else if (TIME.get(THIS_YEAR).equals(timeType)) {// 本年
	//				end = DateFormatUtils.format(cal.getTime(), pattern1);
	//				cal.set(Calendar.DAY_OF_YEAR, 1);
	//				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
	//			}
	//		}
	//		query.setPostStartTime(start);
	//		query.setPostEndTime(end);
	//	}

	/**
	 * <h3>通过条件获取文章</h3>
	 * 
	 * @param condition 条件
	 * @param currentpage 当前页
	 * @param pagesize 每页大小
	 * @return 文章(分页)
	 * @throws Exception 
	 */
	private PageInfoBean<ArticleVoInter> queryArticleByPage(QueryConditionBean condition, Integer currentpage, Integer pagesize, HttpServletRequest request) throws Exception {
		ArticleQueryBean query = new ArticleQueryBean();
		query.setNeedHighLight(true);
		if (condition != null) {
			query = convertConditionToArticleQuery(condition);
		}
		Integer customerId = FMContext.getCurrent().getCustomerId();
		query.setCustIds(customerId.toString());
		query.setPageNo(currentpage);
		query.setPageSize(pagesize);
		query.setCustStage(getArticleStage(customerId));
		query.setArticleOreply("2");
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
		if (!custConfig.getFilterTrash()) {//FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
			query.setCategory("0");
		}
		PageInfoBean<ArticleBean> pageInfo = new PageInfoBean<ArticleBean>();
		pageInfo = articleService.queryArticleByPage(query);
		
		HttpSession session = request.getSession();
		List<String> custTopicIds = (List<String>) session.getAttribute("CUSTTOPICIDS_WITHSHARE");
		boolean flag = CollectionUtils.isNotEmpty(custTopicIds);
		
		List<ArticleVoInter> articleVoList = new ArrayList<ArticleVoInter>();
		for (ArticleBean articleBean : pageInfo.getData()) {
			ArticleVoInter articleVo = new ArticleVo(this, condition.getKeywords());
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
		//		pageInfo.setData(articleVoList);
		return new PageInfoBean<ArticleVoInter>(articleVoList, pageInfo.getTotalRecords(), pageInfo.getPageSize(), pageInfo.getPageNo());
	}

	private List<ArticleBean> queryArticles(QueryConditionBean condition, CustConfigBean custConfig, HttpServletRequest request) throws Exception {
		ArticleQueryBean query = new ArticleQueryBean();
		if (condition != null) {
			query = convertConditionToArticleQuery(condition);
		}
		Integer customerId = FMContext.getCurrent().getCustomerId();
		query.setCustIds(customerId.toString());
		query.setCustStage(getArticleStage(customerId));
		query.setArticleOreply("2");
//		query.setWithDetail(true);//返回文章内容
		if (!condition.getFilterSimilar()) {
			query.setSimilar("0");//值为"0"过滤相似文
		}
		
		if (!custConfig.getFilterTrash()) {//FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
			query.setCategory("0");
		}
		//记录时间范围（用于导出全部Excel功能）
		request.setAttribute("articleStartTime", query.getPostStartTime());
		request.setAttribute("articleEndTime", query.getPostEndTime());
		ArticleAllUtil  articleAllUtil = new ArticleAllUtil();
		List<ArticleBean> articles = articleAllUtil.listAll(query, articleService);
		return articles;
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
				"("+FMContext.getLoginUser().getId()+")"+"#----------专题模块导出全部Excel返回guids耗时#"+(endTime-startTime)+"#毫秒");
		return guids;
	}

	private ArticleQueryBean convertConditionToArticleQuery(QueryConditionBean condition) {
		ArticleQueryBean query = new ArticleQueryBean();

		List<Integer> spiderSiteIds = null;
		if (condition.getSiteIds() != null) {
			List<CustSiteBean> custSite = siteService.listSite(condition.getSiteIds());
			spiderSiteIds = BeanUtil.buildListByFieldName(custSite, "spiderSiteId");
		}

		query.setKeywords(condition.getKeywords());//关键字
		query.setCustTopicIds(StringUtil.join(condition.getCustTopicIds(), ","));//主题
		query.setIndustryIds(StringUtil.join(condition.getIndustryIds(), ","));//行业
		query.setAreaIds(StringUtil.join(condition.getAreaIds(), ","));//地域
		query.setWebsiteId(StringUtil.join(spiderSiteIds, ","));//站点
		query.setStyle(StringUtil.join(condition.getSiteTypes(), ","));//媒体类型
		query.setSentiment(StringUtil.join(condition.getSentiment(), ","));//褒贬
		query.setAuthor(condition.getAuthor());//作者

		setDateForQueryArticle(query, condition);//发布时间

		query.setOrder(condition.getOrderField() != null ? condition.getOrderField().toString() : "1");//排序字段
		query.setArticleOrderBy(condition.getOrderType() != null ? condition.getOrderType().toString() : "0");//排序类型

		return query;
	}

	public ArticleVoInter findArticle(String guid, String keyword,HttpServletRequest request) throws Exception {
		return articleQueryService.findArticle(guid, keyword, StringUtils.isNotBlank(keyword) ? this : fmSupport, request);
	}

	/**
	 * postEndTime的时分秒是23:59:59
	 * @param query
	 * @param condition
	 * @throws ParseException 
	 */
	private void setDateForQueryArticleStatistic(ArticleStatisticQueryBean query, QueryConditionBean condition) throws ParseException {
		String postStartTime = condition.getPostStartTime();
		String postEndTime = condition.getPostEndTime();
		Integer timeType = condition.getPostTimeType();
		String start = "";
		String end = "";
		String pattern = "yyyy-MM-dd";
		//		String pattern1 = "yyyy-MM-dd HH:mm:ss";
		String begine = " 00:00:00";
		String ending = " 23:59:59";
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		if (StringUtils.isNotBlank(postStartTime) || StringUtils.isNotBlank(postEndTime)) {
			start = postStartTime;
			end = postEndTime;
		} else if (timeType != null) {
			if (TIME.get(TODAY).equals(timeType)) {// 当天
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
			} else if (TIME.get(YESTERDAY).equals(timeType)) {
				cal.add(Calendar.DATE, -1);// 昨天
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
			} else if (TIME.get(THIS_WEEK).equals(timeType)) {// 本周
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 本周一
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(LAST_WEEK).equals(timeType)) {// 上周
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 上周日
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.add(Calendar.WEEK_OF_MONTH, -1);// 上周
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 上周一
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(THIS_MONTH).equals(timeType)) {// 当月
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.set(Calendar.DAY_OF_MONTH, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(LAST_MONTH).equals(timeType)) {// 上月
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.set(Calendar.DAY_OF_MONTH, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(THIS_YEAR).equals(timeType)) {// 本年
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.set(Calendar.DAY_OF_YEAR, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			}
		}
		String format = "yyyy-MM-dd HH:mm:ss";
		if (StringUtils.isNotBlank(start)) {
			query.setTimePostFrom(DateUtils.parseDate(start, format).getTime());
		}
		if (StringUtils.isNotBlank(end)) {
			query.setTimePostTo(DateUtils.parseDate(end, format).getTime());
		}
	}

	public Map<Integer, Long> countMapArticleInMedia(ConditionParam conditionParam) throws ParseException, ArticleStatisticServiceException, ConstantServiceException {
		ArticleStatisticQueryBean queryBean = new ArticleStatisticQueryBean();

		QueryConditionBean condition = convertParamToCondition(conditionParam);

		List<Integer> spiderSiteIds = null;
		if (condition.getSiteIds() != null) {
			List<CustSiteBean> custSite = siteService.listSite(condition.getSiteIds());
			spiderSiteIds = BeanUtil.buildListByFieldName(custSite, "spiderSiteId");
		}

		queryBean.setKeyword(condition.getKeywords());//关键字
		queryBean.setCustTopicIds(StringUtil.join(condition.getCustTopicIds(), ","));//主题
		queryBean.setAreaIds(StringUtil.join(condition.getAreaIds(), ","));//地域
		queryBean.setWebsiteId(StringUtil.join(spiderSiteIds, ","));//站点
		//queryBean.setFilterMediaType(condition.getSiteTypes());//媒体类型
		queryBean.setAuthor(condition.getAuthor());//作者

		setDateForQueryArticleStatistic(queryBean, condition);//发布时间

		Integer customerId = FMContext.getCurrent().getCustomerId();
		queryBean.setCustIds(customerId.toString());
		queryBean.setCustStage(Integer.valueOf(getArticleStage(customerId)));
		queryBean.setArticleOreply("2");
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(customerId);
		if (condition.getFilterSimilar() != null) {
			if (!condition.getFilterSimilar()) {
				queryBean.setSimilar("0");//值为"0"过滤相似文
			}
		} else {
			//FilterSimilar为false时查询到的文章带有相似文，为true则没有
			if (custConfig.getFilterSimilar()) {
				queryBean.setSimilar("0");//值为"0"过滤相似文
			}
		}
		if (!custConfig.getFilterTrash()) {//FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
			queryBean.setCategory("0");
		}
		queryBean.setLimit(20);

		Map<Integer, Long> countMapArticleInMedia = articleStatisticService.countMapArticleInMedia(queryBean);

		/*Map<String, Long> sortCountArticle = new HashMap<String, Long>();
		Map<String, Long> resultCountArticle = new HashMap<String, Long>();
		
		List<ArticleStatisticBean> countArticleList = articleStatisticService.countArticleInMedia(queryBean);
		sortCountArticle = MapBuilder.newLinkedHashMap();
		if (countArticleList != null) {
			for (ArticleStatisticBean articleStatisticBean : countArticleList) {
				sortCountArticle.put(articleStatisticBean.getMedia(), articleStatisticBean.getQuantity());
			}
		}
		List<ConstantBean> constants = constantService.listConstantByType(ConstantType.MEDIA_TYPE);
		for (ConstantBean bean : constants) {
			Long countLong = sortCountArticle.get(bean.getValue());
			resultCountArticle.put(bean.getValue(), countLong == null ? 0 : countLong);
		}*/
		return countMapArticleInMedia;
	}

}