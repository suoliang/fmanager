package com.cyyun.fm.focus.controller;

import static com.cyyun.fm.constant.PostTimeType.LAST_MONTH;
import static com.cyyun.fm.constant.PostTimeType.LAST_WEEK;
import static com.cyyun.fm.constant.PostTimeType.THIS_MONTH;
import static com.cyyun.fm.constant.PostTimeType.THIS_WEEK;
import static com.cyyun.fm.constant.PostTimeType.THIS_YEAR;
import static com.cyyun.fm.constant.PostTimeType.TODAY;
import static com.cyyun.fm.constant.PostTimeType.YESTERDAY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.query.ArticleQueryBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.base.util.CyyunBeanUtils;
import com.cyyun.base.util.CyyunCheckParams;
import com.cyyun.base.util.CyyunDateUtils;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.common.core.base.BaseException;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.base.controller.BaseSupport;
import com.cyyun.fm.focus.bean.ConditionParam;
import com.cyyun.fm.focus.bean.FocusInfoVo;
import com.cyyun.fm.newspath.NewsPathWay;
import com.cyyun.fm.newspath.NewsPathWay.ArticleInfo;
import com.cyyun.fm.service.ArticleQueryService;
import com.cyyun.fm.service.CustDataCategoryService;
import com.cyyun.fm.service.CustSiteService;
import com.cyyun.fm.service.bean.CustDataCategoryBean;
import com.cyyun.fm.service.bean.CustDataCategoryQueryBean;
import com.cyyun.fm.service.exception.CustDataCategoryServiceException;
import com.cyyun.fm.vo.ArticleVoInter;
import com.cyyun.process.service.FocusInfoService;
import com.cyyun.process.service.bean.FocusInfoBean;
import com.cyyun.process.service.bean.FocusInfoQueryBean;
import com.cyyun.process.service.exception.FocusInfoServiceException;

/**
 * <h3>搜索控制器支持/h3>
 * 
 * @author zhangfei
 * @version 1.0.0
 */
@Component
public class FmVoicefocusSupport extends BaseSupport{
	private static final Map<String, Integer> TIME = MapBuilder.put("7日内", 8).and(TODAY, 1).and(YESTERDAY, 2).and(THIS_WEEK, 3).and(LAST_WEEK, 4).and(THIS_MONTH, 5).and(LAST_MONTH, 6).and(THIS_YEAR, 7).build();

	@Autowired
	private FocusInfoService focusInfoService;
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private CustSiteService siteService;
	
	@Autowired
	private CustDataCategoryService custDataCategoryService;
	
	@Resource(name = "FmSupport")
	private BaseSupport fmSupport;
	
	@Resource(name = "FmVoicefocusArticleQueryServiceImpl")
	private ArticleQueryService articleQueryService;
	
	private static Logger logger = LoggerFactory.getLogger(CyyunCheckParams.class);
	
	public Map<String,Object> findArticle(String guid, String keyword, HttpServletRequest request) throws Exception {
		return articleQueryService.queryArticle(guid, keyword, StringUtils.isNotBlank(keyword)?this:fmSupport, request);
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
	
	public PageInfoBean<ArticleBean> similarQueryInfo(HttpServletRequest request, Integer pageNo, Integer pageSize) throws ArticleServiceException{
		String guidGroup = request.getParameter("guidGroup");
		String guid = request.getParameter("guid");
		Integer customerId = FMContext.getCurrent().getCustomerId();
//		CustConfigBean custConfig=customerConfigService.findCustConfigByCid(customerId);
		ArticleQueryBean query = new ArticleQueryBean();
		query.setPageNo(pageNo);
		query.setPageSize(pageSize);
		query.setGuidGroup(guidGroup);
		query.setCustIds(customerId.toString());
		query.setCustStage(getArticleStage(customerId));
		query.setArticleOreply("2");
		request.setAttribute("similarGuidGroup", guidGroup);
		request.setAttribute("similarGuid", guid);
//		if (!custConfig.getFilterTrash()) {//FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
			query.setCategory("0");
//		}
		PageInfoBean<ArticleBean> pageInfo = articleService.queryArticleByPage(query);
		List<ArticleBean> articleList = pageInfo.getData();
		List<ArticleBean> articleResultsList = new ArrayList<ArticleBean>();
		Integer total = pageInfo.getTotalRecords()-1;
		for (ArticleBean article : articleList) {
			if (!article.getGuid().equals(guid)) {
				articleResultsList.add(article);
			} else {
				/**如果总数是整数,有一条数据显示不出来处理*/
				if (total%pageSize == 0) {
					query.setPageNo(total/pageSize + 1);
					PageInfoBean<ArticleBean> lastOne = articleService.queryArticleByPage(query);
					article = lastOne.getData().get(0) == null ? null : lastOne.getData().get(0);
					if (ObjectUtils.notEqual(article, null)) {
						if (!article.getGuid().equals(guid)) {
							articleResultsList.add(article);
						}
					}
				}
			}
		}
		/**此处逻辑,因为有分页,所以article的guid和articleBean的guidGroup在某一页不一定存在相同数据,每次总数都减去1条记录肯定是对的*/
		pageInfo.setTotalRecords(total);
		
		pageInfo.setData(articleResultsList);
		return pageInfo;
	}
	
	public ArrayList<ArticleInfo> arrayListArticleInfo(String guidGroup) throws ArticleServiceException{
		Integer customerId = FMContext.getCurrent().getCustomerId();
		CustConfigBean custConfig=customerConfigService.findCustConfigByCid(customerId);
		ArticleQueryBean query = new ArticleQueryBean();
		query.setPageNo(1);
		query.setPageSize(10);
		query.setGuidGroup(guidGroup);
		query.setCustIds(customerId.toString());
		query.setCustStage(getArticleStage(customerId));
		query.setArticleOreply("2");
		if (!custConfig.getFilterTrash()) {//FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
			query.setCategory("0");
		}
		PageInfoBean<ArticleBean> pageInfo = articleService.queryArticleByPage(query);
		
		Integer totalRecords = pageInfo.getTotalRecords();
		
		return getGuidsArticleInfo(query, totalRecords);
	}
	
	private ArrayList<ArticleInfo> getGuidsArticleInfo(ArticleQueryBean query, Integer totalRecords) throws ArticleServiceException {
		
		ArrayList<ArticleInfo> articleInfos=new ArrayList<ArticleInfo>();
		
		int i;
		for (i = 20; i < totalRecords && totalRecords - (i - 20) > 20; i += 20) {//按PageSize=20分页后，需要查询的数量>20则进循环
			query.setPageNo(i / 20);
			query.setPageSize(20);
			
			setArticleInfo(query, articleInfos);
		}
		if (totalRecords > 20) {//经过上面的for循环处理后，再处理totalRecords-(i-20)的情况
			query.setPageNo(i / 20);
			query.setPageSize(20);
			
			setArticleInfo(query, articleInfos);
		} else {//totalRecords<20
			query.setPageNo(1);
			query.setPageSize(totalRecords);
			
			setArticleInfo(query, articleInfos);
		}
		return articleInfos;
	}

	private void setArticleInfo(ArticleQueryBean query,ArrayList<ArticleInfo> articleInfos) throws ArticleServiceException {
		PageInfoBean<ArticleBean> pageInfo = articleService.queryArticleByPage(query);
		List<ArticleBean> data = pageInfo.getData();
		if (CollectionUtils.isNotEmpty(data)) {
			for (ArticleBean articleVo : data) {
				NewsPathWay.ArticleInfo  articleInfo=new NewsPathWay.ArticleInfo();
				articleInfo.setSite(articleVo.getWebsiteName());
				articleInfo.setUrl(articleVo.getUrl());
				articleInfo.setTitle(articleVo.getTitle());
				articleInfo.setTime(DateFormatUtils.format(articleVo.getPostTime(), "yyyy-MM-dd HH:mm"));
				articleInfo.setSourceSite(articleVo.getSourceSiteName());
			    articleInfos.add(articleInfo);
			}
		}
	}
	
	public List<CustDataCategoryBean> listSiteCustDataCategory() throws CustDataCategoryServiceException {
		CustDataCategoryQueryBean query = new CustDataCategoryQueryBean();
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setType(3);
		return custDataCategoryService.queryCustDataCategoryBeanListByCondition(query);
	}
	
	/**
	 * 预警查询
	 * @throws ArticleServiceException 
	 */
	public PageInfoBean<ArticleVoInter> queryFocusInfo(ConditionParam conditionParam, HttpServletRequest request) throws Exception {
		FocusInfoQueryBean query = convertParamToCondition(conditionParam);
		PageInfoBean<FocusInfoBean> queryRest = getFocusData(conditionParam, query);
		List<FocusInfoBean> data = queryRest.getData();
		List<ArticleVoInter> focusInfoBeanList = convertToVo(data,conditionParam.getKeyword());
		return new PageInfoBean<ArticleVoInter>(focusInfoBeanList,
				queryRest.getTotalRecords(), queryRest.getPageSize(),
				queryRest.getPageNo());
	}

	private PageInfoBean<FocusInfoBean> getFocusData(ConditionParam conditionParam, FocusInfoQueryBean query) throws FocusInfoServiceException {
		PageInfoBean<FocusInfoBean> queryRest = null;
		if (conditionParam.getScopeFlag() == 0) {// 我的预警
			query.setUserId(FMContext.getCurrent().getUserId());// userId
			System.err.println("用户-"+FMContext.getLoginUser().getId()+"----------预警数据查询参数--focusInfoService.queryFocusInfoByPage：" + query.toString());
			queryRest = focusInfoService.queryFocusInfoByPage(query);
		} else {// 所有预警
			query.setCustId(FMContext.getCurrent().getCustomerId());// cid
			System.err.println("用户-"+FMContext.getLoginUser().getId()+"----------预警数据查询参数--focusInfoService.queryWarningByPage：" + query.toString());
			queryRest = focusInfoService.queryWarningByPage(query);
		}
		return queryRest;
	}
	
	private List<ArticleVoInter> convertToVo(List<FocusInfoBean> datas, String keyword){
		try {
			List<ArticleVoInter> focusInfoBeanList = new ArrayList<ArticleVoInter>();
			FmVoicefocusSupport fmVoicefocusSupport=new FmVoicefocusSupport();
			if(CollectionUtils.isNotEmpty(datas)){
				for(FocusInfoBean focusInfoBean: datas){
					ArticleVoInter focusInfoVo = new FocusInfoVo(fmVoicefocusSupport, keyword);
					//-------------
//					ArticleQueryBean queryBean=new ArticleQueryBean();
//					List<String> guid_guidGroups=new ArrayList<String>();
//					guid_guidGroups.add(focusInfoBean.getArticleId());
//					queryBean.setGuid_guidGroups(guid_guidGroups);
//					Map<String, Integer> articleMap = articleService.countArticleSimilar(queryBean);
//					int b=articleMap.get(guid_guidGroups);
//					focusInfoBean.setSource(b);
					//-------------
					CyyunBeanUtils.copyProperties(focusInfoVo,focusInfoBean);
					
					focusInfoBeanList.add(focusInfoVo);
				}
				return focusInfoBeanList;
			}
		} catch (Exception e) {
			logger.error("convertToVo::", e);
		}
		return null;
	}
	
	public List<ArticleBean> queryArticleList(ConditionParam conditionParam, HttpServletRequest request) throws Exception {
		FocusInfoQueryBean queryCondition = convertParamToCondition(conditionParam);
		//记录时间范围（用于导出全部Excel功能）
		request.setAttribute("articleStartTime", CyyunDateUtils.formatDate(queryCondition.getStartCreateTime(), CyyunDateUtils.DATEFORMAT));
		request.setAttribute("articleEndTime", CyyunDateUtils.formatDate(queryCondition.getEndCreateTime(), CyyunDateUtils.DATEFORMAT));
		List<String[]> conditions = queryGuidArrList(queryCondition, conditionParam);
		if(CollectionUtils.isEmpty(conditions)){
			return new ArrayList<>();
		}
		long start = System.currentTimeMillis();
		logger.info("导出文章开始查询文章...");
		List<ArticleBean> toReturn = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(5);
		List<Future<List<ArticleBean>>> resultFuture = new ArrayList<>();
		for (String[] guids : conditions) {
			ListArticleByConditionOne condition = new ListArticleByConditionOne(guids, articleService);
			Future<List<ArticleBean>> f = executor.submit(condition);
			resultFuture.add(f);
		}
		for (Future<List<ArticleBean>> f : resultFuture) {
			try {
				toReturn.addAll(f.get());
			} catch (NullPointerException e) {
				logger.info("查询文章为空");
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			} catch (ExecutionException e) {
				logger.error(e.getMessage(), e);
			} finally {
				//启动一次顺序关闭，执行以前提交的任务，但不接受新任务。如果已经关闭，则调用没有其他作用。
				executor.shutdown();
			}
		}
		logger.info("导出文章查询文章结束... 需要时间：{} 毫秒", System.currentTimeMillis()-start );
		return toReturn;
	}

	private List<String[]> queryGuidArrList(FocusInfoQueryBean query,ConditionParam conditionParam) throws Exception {
		query.setOrderBy("post_time");//按发文时间降序
		PageInfoBean<FocusInfoBean> queryRest = getFocusData(conditionParam, query);
		
		List<String[]> guids = new ArrayList<String[]>();
		Integer totalRecords=queryRest.getTotalRecords();
		List<FocusInfoQueryBean> conditions = breakQueryCondition(query, totalRecords, conditionParam);
		long start = System.currentTimeMillis();
		logger.info("导出文章开始查询预警文章Guid...");
		ExecutorService executor = Executors.newFixedThreadPool(5);
		List<Future<String[]>> resultFuture = new ArrayList<>();
		for (FocusInfoQueryBean bean : conditions) {
			ListFocusGuidByConditionOne condition = new ListFocusGuidByConditionOne(bean, conditionParam, focusInfoService);
			Future<String[]> f = executor.submit(condition);
			resultFuture.add(f);
		}
		for (Future<String[]> f : resultFuture) {
			try {
				guids.add(f.get());
			} catch (NullPointerException e) {
				logger.info("查询文章Guid为空");
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			} catch (ExecutionException e) {
				logger.error(e.getMessage(), e);
			} finally {
				//启动一次顺序关闭，执行以前提交的任务，但不接受新任务。如果已经关闭，则调用没有其他作用。
				executor.shutdown();
			}
		}
		logger.info("导出文章查询预警文章Guid结束... 需要时间：{} 毫秒", System.currentTimeMillis()-start );
		return guids;
	}
	
	private List<FocusInfoQueryBean> breakQueryCondition(FocusInfoQueryBean query, Integer totalRecords, ConditionParam conditionParam) throws FocusInfoServiceException {
		List<FocusInfoQueryBean> conditions = new ArrayList<FocusInfoQueryBean>();
		int i;
		for (i = 100; i < totalRecords && totalRecords - (i - 100) > 100; i += 100) {//按PageSize=100分页后，需要查询的数量>100则进循环
			FocusInfoQueryBean qb = new FocusInfoQueryBean();
			try {
				qb = (FocusInfoQueryBean) BeanUtils.cloneBean(query);
			} catch (Exception e) {
				e.printStackTrace();
			}
			qb.setPageNo(i / 100);
			qb.setPageSize(100);
			conditions.add(qb);
		}
		if (totalRecords > 100) {//经过上面的for循环处理后，再处理totalRecords-(i-100)的情况
			query.setPageNo(i / 100);
			query.setPageSize(100);
			conditions.add(query);
		} else {//totalRecords<100
			query.setPageNo(1);
			query.setPageSize(totalRecords);
			conditions.add(query);
		}
		return conditions;
	}

	/**
	 * 一次性查询所有文章
	 */
	class ListArticleByConditionOne implements Callable<List<ArticleBean>> {
		String[] guids;
		ArticleService articleServcie;

		public ListArticleByConditionOne(String[] guids, ArticleService articleService) {
			this.guids = guids;
			this.articleServcie = articleService;
		}

		@Override
		public List<ArticleBean> call() throws Exception {
			List<ArticleBean> articleList = null;
			logger.info("查询到GUID数量为：" + guids.length);
			try {
				articleList = articleServcie.listArticleWithoutContent(guids);
			} catch (Exception e) {
				// 如果第一次查询异常，则catch到，然后继续用下面的for循环去查询文章
			}
			
			int count = 0;
			while(articleList == null){
				if(count>2){
					break;
				}
				Thread.sleep(500);
				try {
					articleList = articleServcie.listArticleWithoutContent(guids);
				} catch (Exception e) {
					// 如果有异常，则catch，直到3次循环结束
				}
				count++;
			}
			
			if(articleList == null){
				logger.error("查询文章异常！！！查询参数为：{}", guids.toString());
			}
			logger.info("查询到文章数量为：" + articleList.size());
			return articleList ;
		}
	}
	
	/**
	 * 一次性查询所有预警文章的guid
	 */
	class ListFocusGuidByConditionOne implements Callable<String[]> {
		private FocusInfoQueryBean focusInfoQueryBean;
		private ConditionParam conditionParam;
		private FocusInfoService focusInfoService;
		
		public ListFocusGuidByConditionOne(FocusInfoQueryBean focusInfoQueryBean, ConditionParam conditionParam, FocusInfoService focusInfoService) {
			this.focusInfoQueryBean = focusInfoQueryBean;
			this.focusInfoService = focusInfoService;
			this.conditionParam = conditionParam;
		}
		
		@Override
		public String[] call() throws Exception {
			String[] guids = null;
			PageInfoBean<FocusInfoBean> queryRest = null;
			if (conditionParam.getScopeFlag() == 0) {// 我的预警
				focusInfoQueryBean.setUserId(FMContext.getLoginUser().getId());// userId
				System.err.println("用户-"+FMContext.getLoginUser().getId()+"----------预警数据查询参数--focusInfoService.queryFocusInfoByPage：" + focusInfoQueryBean.toString());
				queryRest = focusInfoService.queryFocusInfoGuidsByPage(focusInfoQueryBean);
				if (queryRest.getData() != null) 
				guids = BeanUtil.<String, FocusInfoBean> buildArrayByFieldName(queryRest.getData(), "articleId");
			} else {// 所有预警
				focusInfoQueryBean.setCustId(FMContext.getCurrent().getCustomerId());// cid
				System.err.println("用户-"+FMContext.getLoginUser().getId()+"----------预警数据查询参数--focusInfoService.queryWarningByPage：" + focusInfoQueryBean.toString());
				queryRest = focusInfoService.queryWarningByPage(focusInfoQueryBean);
				guids = BeanUtil.<String, FocusInfoBean> buildArrayByFieldName(queryRest.getData(), "articleId");
			}
			
			if(guids == null){
				logger.error("查询预警文章的guid异常！！！查询参数为：{}", focusInfoQueryBean);
			}
			return guids;
		}
	}
	
	/**
	 * <h3>转换参数为条件</h3>
	 * 
	 * @param 参数        
	 * @return 条件
	 */
	private FocusInfoQueryBean convertParamToCondition(ConditionParam conditionParam) throws Exception {
		FocusInfoQueryBean query = new FocusInfoQueryBean();
		Integer timeType = conditionParam.getTimeType();
		if (StringUtils.isNoneBlank(conditionParam.getStartTime())
				|| StringUtils.isNoneBlank(conditionParam.getEndTime())) {
			if (StringUtils.isNoneBlank(conditionParam.getStartTime())) {
				query.setStartCreateTime(DateUtils.parseDate(conditionParam.getStartTime(),"yyyy-MM-dd HH:mm:ss"));
			}
			if (StringUtils.isNoneBlank(conditionParam.getEndTime())) {
				Date date = DateUtils.parseDate(conditionParam.getEndTime(), "yyyy-MM-dd HH:mm:ss");
				/**由于数据库存放数据精确到毫秒,此处结束时间加1S*/
				Calendar calendar = Calendar.getInstance();    
			    calendar.setTime(date);    
			    calendar.add(Calendar.SECOND, 1);    
				query.setEndCreateTime(calendar.getTime());
			}
		} else if (timeType != null) {
			String start = "";
			String end = "";
			String pattern = "yyyy-MM-dd";
			String pattern1 = "yyyy-MM-dd HH:mm:ss";
			String begine = " 00:00:00";
			String ending = " 23:59:59";
			Calendar cal = Calendar.getInstance(Locale.CHINA);
			if (TIME.get(TODAY).equals(timeType)) {//当天
				start = DateFormatUtils.format(cal.getTime(), pattern)+begine;
				end = DateFormatUtils.format(cal.getTime(), pattern1);
			} else if (TIME.get(YESTERDAY).equals(timeType)) {
				cal.add(Calendar.DATE, -1);// 昨天
				start = DateFormatUtils.format(cal.getTime(), pattern)+begine;
				end = DateFormatUtils.format(cal.getTime(), pattern)+ending;
			} else if (TIME.get(THIS_WEEK).equals(timeType)) {// 本周
				end = DateFormatUtils.format(cal.getTime(), pattern1);
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 本周一
				start = DateFormatUtils.format(cal.getTime(), pattern)+begine;
			} else if (TIME.get(LAST_WEEK).equals(timeType)) {//上周
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//上周日
				end = DateFormatUtils.format(cal.getTime(), pattern)+ending;
				cal.add(Calendar.WEEK_OF_MONTH, -1);//上周
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//上周一
				start = DateFormatUtils.format(cal.getTime(), pattern)+begine;
			} else if (TIME.get(THIS_MONTH).equals(timeType)) {//当月
				end = DateFormatUtils.format(cal.getTime(), pattern1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern)+begine;
			} else if (TIME.get(LAST_MONTH).equals(timeType)) {//上月
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				end = DateFormatUtils.format(cal.getTime(), pattern)+ending;
				cal.set(Calendar.DAY_OF_MONTH, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern)+begine;
			} else if (TIME.get(THIS_YEAR).equals(timeType)) {//本年
				end = DateFormatUtils.format(cal.getTime(), pattern1);
				cal.set(Calendar.DAY_OF_YEAR, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern)+begine;
			} else if (TIME.get("7日内").equals(timeType)) {
				cal.add(Calendar.DATE, -6);// 一周内
				start = DateFormatUtils.format(cal.getTime(), pattern)+begine;
				
				cal.add(Calendar.DATE, +6);// 一周内
				end = DateFormatUtils.format(cal.getTime(), pattern1);
			}
			query.setStartCreateTime(DateUtils.parseDate(start, pattern1));
			query.setEndCreateTime(DateUtils.parseDate(end, pattern1));
		}
		query.setSource(conditionParam.getSource());// 来源
		query.setKeyWord(CyyunSqlUtil.dealSql(conditionParam.getKeyword()));// 关键词
		query.setPageNo(conditionParam.getCurrentpage());
		query.setPageSize(conditionParam.getPagesize());
		query.setStyleMulti(conditionParam.getMediaType()!=null && conditionParam.getMediaType().length==0 ? null : conditionParam.getMediaType());
		query.setLevel(conditionParam.getWarningLevel());//预警等级
		return query;
	}
}
