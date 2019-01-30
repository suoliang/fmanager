package com.cyyun.fm.analyze.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.constant.ArticleConstants;
import com.cyyun.base.constant.ConstantType;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.constant.FMConstant.ARTICLEOREPLY_FLAG;
import com.cyyun.base.constant.TimeType;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.ArticleStatisticService;
import com.cyyun.base.service.ConstantService;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.bean.ArticleStatisticBean;
import com.cyyun.base.service.bean.ConstantBean;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.query.ArticleStatisticQueryBean;
import com.cyyun.base.service.bean.query.CustTopicQueryBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.base.service.exception.ArticleStatisticServiceException;
import com.cyyun.base.service.exception.ConstantServiceException;
import com.cyyun.base.service.exception.CustTopicServiceException;
import com.cyyun.base.util.ConstUtil;
import com.cyyun.base.util.DateFormatFactory;
import com.cyyun.base.util.MapKeyByTimeComparator;
import com.cyyun.base.util.MapKeyComparator;
import com.cyyun.base.util.TagUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.analyze.bean.LineChartDataView;
import com.cyyun.fm.custtopic.bean.CustTopicView;
import com.cyyun.fm.search.bean.OptionInfoView;
import com.cyyun.fm.service.AnalyzeSituationService;
import com.cyyun.fm.service.FmCustomerConfigService;
import com.cyyun.fm.service.StatCustTopicService;
import com.cyyun.fm.service.bean.StatCustTopicBean;
import com.cyyun.fm.service.bean.StatWebsiteBean;

@Component
public class AnalyzeSupport {

	@Autowired
	private ArticleService articleService;

	@Autowired
	private ArticleStatisticService articleStatisticService;

	@Autowired
	private CustomerConfigService customerConfigService;

	@Autowired
	private ConstUtil constUtil;

	@Autowired
	private TagUtil tagUtil;

	@Autowired
	private ConstantService constantService;

	@Autowired
	private AnalyzeSituationService analyzeSituationService;

	@Autowired
	private CustTopicService custTopicService;

	@Autowired
	private StatCustTopicService statCustTopicService;

	@Autowired
	private FmCustomerConfigService fmCustomerConfigService;
	
	public Map<String, Long> getSituation(String startDate, String endDate, Integer[] topic) throws Exception {
		CustConfigBean custConfig = fmCustomerConfigService.getCustConfig();
		return analyzeSituationService.analyzeSituation(startDate, endDate, topic, custConfig);
	}

	public Map<String, List<ArticleStatisticBean>> getSituationByDay(String startDate, String endDate, Integer[] topic) throws Exception {
		Integer customerId = FMContext.getCurrent().getCustomerId();
		ArticleStatisticQueryBean queryBean = new ArticleStatisticQueryBean();
		String format = "yyyy-MM-dd HH:mm:ss";
		queryBean.setTimePostFrom(DateUtils.parseDate(startDate + " 00:00:00", format).getTime());
		queryBean.setTimePostTo(DateUtils.parseDate(endDate + " 23:59:59", format).getTime());
		queryBean.setLimit(20);
		queryBean.setCustStage(Integer.parseInt(fmCustomerConfigService.getArticleStage()));
		queryBean.setCustIds(customerId.toString());
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(customerId);
		// FilterSimilar为false时查询到的文章带有相似文，为true则没有
		if (custConfig.getFilterSimilar()) {
			queryBean.setSimilar("0");//值为"0"过滤相似文
		}
		// FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
		if (!custConfig.getFilterTrash()) {
			queryBean.setCategory("0");
		}
		
		Integer[] topicId = ArrayUtils.isNotEmpty(topic) ? topic : null;
		if (topicId != null) {
			queryBean.setCustTopicIds(StringUtils.join(topicId,","));
		}
		
		queryBean.setActionType(TimeType.DAY);//按天统计数据
		return articleStatisticService.countArticleInMedia2(queryBean);
	}

	public Map<String, Long> getReadInMedia(String startDate, String endDate, Integer[] topic) throws Exception {
		Map<Integer, Long> sortCountArticle = new HashMap<Integer, Long>();
		Map<String, Long> resultCountArticle = new HashMap<String, Long>();
		Integer customerId = FMContext.getCurrent().getCustomerId();
		ArticleStatisticQueryBean queryBean = new ArticleStatisticQueryBean();
		String format = "yyyy-MM-dd HH:mm:ss";
		queryBean.setTimePostFrom(DateUtils.parseDate(startDate + " 00:00:00", format).getTime());
		queryBean.setTimePostTo(DateUtils.parseDate(endDate + " 23:59:59", format).getTime());
		queryBean.setLimit(20);
		queryBean.setCustStage(Integer.parseInt(fmCustomerConfigService.getArticleStage()));
		queryBean.setCustIds(customerId.toString());
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(customerId);
		// FilterSimilar为false时查询到的文章带有相似文，为true则没有
		if (custConfig.getFilterSimilar()) {
			queryBean.setSimilar("0");//值为"0"过滤相似文
		}
		// FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
		if (!custConfig.getFilterTrash()) {
			queryBean.setCategory("0");
		}
		
		Integer[] topicId = ArrayUtils.isNotEmpty(topic) ? topic : null;
		if (topicId != null) {
			queryBean.setCustTopicIds(StringUtils.join(topicId,","));
		}
		
		sortCountArticle = articleStatisticService.countMapArticleReadInMedia(queryBean);
		//只保留九种媒体的数据
		List<ConstantBean> constants = constantService.listConstantByType(ConstantType.MEDIA_TYPE);
		for (ConstantBean bean : constants) {
			Long countLong = sortCountArticle.get(Integer.parseInt(bean.getValue()));
			resultCountArticle.put(bean.getValue(), countLong == null ? 0 : countLong);
		}
		return resultCountArticle;
	}

	public Map<String, Long> getReplyInMedia(String startDate, String endDate, Integer[] topic) throws Exception {
		Map<Integer, Long> sortCountArticle = new HashMap<Integer, Long>();
		Map<String, Long> resultCountArticle = new HashMap<String, Long>();
		Integer customerId = FMContext.getCurrent().getCustomerId();
		ArticleStatisticQueryBean queryBean = new ArticleStatisticQueryBean();
		String format = "yyyy-MM-dd HH:mm:ss";
		queryBean.setTimePostFrom(DateUtils.parseDate(startDate + " 00:00:00", format).getTime());
		queryBean.setTimePostTo(DateUtils.parseDate(endDate + " 23:59:59", format).getTime());
		queryBean.setLimit(20);
		queryBean.setCustStage(Integer.parseInt(fmCustomerConfigService.getArticleStage()));
		queryBean.setCustIds(customerId.toString());
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(customerId);
		// FilterSimilar为false时查询到的文章带有相似文，为true则没有
		if (custConfig.getFilterSimilar()) {
			queryBean.setSimilar("0");//值为"0"过滤相似文
		}
		// FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
		if (!custConfig.getFilterTrash()) {
			queryBean.setCategory("0");
		}
		
		Integer[] topicId = ArrayUtils.isNotEmpty(topic) ? topic : null;
		if (topicId != null) {
			queryBean.setCustTopicIds(StringUtils.join(topicId,","));
		}
		
		sortCountArticle = articleStatisticService.countMapArticleReplyInMedia(queryBean);
		//只保留九种媒体的数据
		List<ConstantBean> constants = constantService.listConstantByType(ConstantType.MEDIA_TYPE);
		for (ConstantBean bean : constants) {
			Long countLong = sortCountArticle.get(Integer.parseInt(bean.getValue()));
			resultCountArticle.put(bean.getValue(), countLong == null ? 0 : countLong);
		}
		return resultCountArticle;
	}

	/*	@SuppressWarnings("unused")
		private Map<String, Integer> countArticle(String postStartTime, String postEndTime) throws ArticleServiceException {
			ArticleQueryBean queryBean = new ArticleQueryBean();
			queryBean.setActionType(ActionType.DAY);
			queryBean.setCustStage(getStage(FMContext.getCurrent().getCustomerId()));
			queryBean.setArticleOreply("2");
			queryBean.setPostStartTime(postStartTime);
			queryBean.setPostEndTime(postEndTime);
			queryBean.setCustIds("" + FMContext.getCurrent().getCustomerId());
			Map<String, Integer> countArticle = articleService.countArticleStyle(queryBean);

			Map<String, Integer> sortCountArticle = MapBuilder.newLinkedHashMap();

			for (Entry<String, Integer> num : countArticle.entrySet()) {
				String maxKey = "";
				int maxValue = 0;
				for (Entry<String, Integer> countItem : countArticle.entrySet()) {
					if (sortCountArticle.keySet().contains(countItem.getKey())) {
						continue;
					}
					if (countItem.getValue() >= maxValue) {
						maxKey = countItem.getKey();
						maxValue = countItem.getValue();
					}
				}
				if (StringUtils.isNoneBlank(maxKey)) {
					sortCountArticle.put(maxKey, maxValue);
				}
			}
			return sortCountArticle;
		}*/

	private Map<String, Long> countArticle(ArticleStatisticQueryBean queryBean, String postStartTime, String postEndTime, Integer[] topic) throws ArticleServiceException {
		Map<String, Long> sortCountArticle = new HashMap<String, Long>();
		Map<String, Long> resultCountArticle = new HashMap<String, Long>();
		try {
			Integer customerId = FMContext.getCurrent().getCustomerId();
			String format = "yyyy-MM-dd HH:mm:ss";
			queryBean.setTimePostFrom(DateUtils.parseDate(postStartTime + " 00:00:00", format).getTime());
			queryBean.setTimePostTo(DateUtils.parseDate(postEndTime + " 23:59:59", format).getTime());
			queryBean.setLimit(20);
			queryBean.setCustStage(Integer.parseInt(getStage(customerId)));
			queryBean.setCustIds(customerId.toString());
			CustConfigBean custConfig = customerConfigService.findCustConfigByCid(customerId);
			// FilterSimilar为false时查询到的文章带有相似文，为true则没有
			if (custConfig.getFilterSimilar()) {
				queryBean.setSimilar("0");//值为"0"过滤相似文
			}

			// FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
			if (!custConfig.getFilterTrash()) {
				queryBean.setCategory("0");
			}
			
			Integer[] topicId = ArrayUtils.isNotEmpty(topic) ? topic : null;
			if (topicId != null) {
				queryBean.setCustTopicIds(StringUtils.join(topicId,","));
			}
			
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultCountArticle;
	}

	private String getStage(Integer cid) {
		CustConfigBean custConfig = getConfig();
		Boolean viewMonitorData = custConfig.getViewMonitorData();
		String stage = viewMonitorData != null && viewMonitorData ? ArticleConstants.ArticleStage.PUTONG : ArticleConstants.ArticleStage.GUIDANG;
		return stage;
	}

	public LineChartDataView getTendency(String startDate, String endDate, Integer[] topic) throws ArticleServiceException, ParseException, ArticleStatisticServiceException, ConstantServiceException {
		LineChartDataView lineChartDataView = new LineChartDataView();

		Map<String, List<Long>> data = MapBuilder.newLinkedHashMap();
		/**key的集合*/
		List<String> axisName = ListBuilder.newArrayList();
		List<String> legendName = ListBuilder.newArrayList();
		List<List<Long>> seriesDatas = ListBuilder.newArrayList();

		Date datePoint = DateUtils.parseDate(startDate, "yyyy-MM-dd");
		Date lastPoint = DateUtils.parseDate(endDate, "yyyy-MM-dd");
		
		if (startDate.equals(endDate)) {
			String hoursLiteral = StringUtils.EMPTY;
			for (int i = 0; i <= 24; i++) {
				hoursLiteral = endDate + " " + i;
				if (i < 10) {
					hoursLiteral = endDate + " " + "0" + i;
				}
				if (i == 24) {
					hoursLiteral = endDate + " 24";
				}
				axisName.add(hoursLiteral);
			}
			getTimesMedias(startDate, endDate, topic, data, axisName, TimeType.HOUR);
		} else {
			while (datePoint.before(lastPoint)) {
				String dayLiteral = DateFormatUtils.format(datePoint, "yyyy-MM-dd");
				axisName.add(dayLiteral);
				
				datePoint = DateUtils.addDays(datePoint, 1);
			}
			String dayLiteral = DateFormatUtils.format(lastPoint, "yyyy-MM-dd");
			axisName.add(dayLiteral);
			getTimesMedias(startDate, endDate, topic, data, axisName, TimeType.DAY);
		}
		
		for (Entry<String, List<Long>> entry : data.entrySet()) {
			legendName.add(constUtil.getName(ConstantType.MEDIA_TYPE, entry.getKey()));
			seriesDatas.add(entry.getValue());
		}

		lineChartDataView.setAxisName(axisName);
		lineChartDataView.setLegendName(legendName);
		lineChartDataView.setSeriesDatas(seriesDatas);

		return lineChartDataView;
	}

	
	private void getTimesMedias(String startDate, String endDate,
			Integer[] topic, Map<String, List<Long>> data,List<String> keyList,TimeType timeType) throws ParseException, ArticleStatisticServiceException, ConstantServiceException {
		ArticleStatisticQueryBean queryBean = new ArticleStatisticQueryBean();
		Integer customerId = FMContext.getCurrent().getCustomerId();
		String format = "yyyy-MM-dd HH:mm:ss";
		queryBean.setTimePostFrom(DateUtils.parseDate(startDate + " 00:00:00", format).getTime());
		queryBean.setTimePostTo(DateUtils.parseDate(endDate + " 23:59:59", format).getTime());
		queryBean.setLimit(20);
		queryBean.setCustStage(Integer.parseInt(getStage(customerId)));
		queryBean.setCustIds(customerId.toString());
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(customerId);
		// FilterSimilar为false时查询到的文章带有相似文，为true则没有
		if (custConfig.getFilterSimilar()) {
			queryBean.setSimilar("0");//值为"0"过滤相似文
		}

		// FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
		if (!custConfig.getFilterTrash()) {
			queryBean.setCategory("0");
		}
		
		Integer[] topicId = ArrayUtils.isNotEmpty(topic) ? topic : null;
		if (topicId != null) {
			queryBean.setCustTopicIds(StringUtils.join(topicId,","));
		}
		queryBean.setActionType(timeType);
		
		Map<String, List<ArticleStatisticBean>> countArticleInTimesMediasNoSort = articleStatisticService.countArticleInTimesMedias(queryBean);
		
		if (countArticleInTimesMediasNoSort == null) {
			countArticleInTimesMediasNoSort = new HashMap<String, List<ArticleStatisticBean>>();
		}
		Set<String> keySet = countArticleInTimesMediasNoSort.keySet();
		
		List<ArticleStatisticBean> list = new ArrayList<ArticleStatisticBean>();
		/**数据封装，key不存在的封装上去*/
		for (String string : keyList) {
			if(!keySet.contains(string)){
				countArticleInTimesMediasNoSort.put(string, list);
			}
		}
		
		Map<String, List<ArticleStatisticBean>> countArticleInTimesMedias = MapKeyByTimeComparator.sortMapByKey(countArticleInTimesMediasNoSort);
		
		List<ConstantBean> constants = constantService.listConstantByType(ConstantType.MEDIA_TYPE);

		Map<String, Long> sortCountArticle = new HashMap<String, Long>();
		Map<String, Long> resultCountArticle = new HashMap<String, Long>();
		/**数据合并，key相同的将value值有序放到集合中*/
		for (Entry<String,List<ArticleStatisticBean>> articlesMap : countArticleInTimesMedias.entrySet()) {
			
			sortCountArticle.clear();
			resultCountArticle.clear();
			
			List<ArticleStatisticBean> articleBean = articlesMap.getValue();
			for (ArticleStatisticBean articleStatisticBean : articleBean) {
				sortCountArticle.put(articleStatisticBean.getMedia(), articleStatisticBean.getQuantity());
			}
			for (ConstantBean bean : constants) {
				Long countLong = sortCountArticle.get(bean.getValue());
				resultCountArticle.put(bean.getValue(), countLong == null ? 0 : countLong);
			}
			
			resultCountArticle = MapKeyComparator.sortMapByKey(resultCountArticle);
			
			for (String string : resultCountArticle.keySet()) {
				List<Long> count = new ArrayList<Long>();
				if (data.containsKey(string)) {
					count.addAll(data.get(string));
					count.add(resultCountArticle.get(string));
					data.put(string, count);
				} else {
					count.add(resultCountArticle.get(string));
					data.put(string, count);
				}
			}
			
		}
		
	}

	public List<StatCustTopicBean> getStatCustTopic(String startDate, String endDate) throws Exception {
		//		List<StatCustTopicBean> result = ListBuilder.newArrayList();
		//
		//		Date begin = DateUtils.parseDate(startDate, "yyyy-MM-dd");
		//		Date end = DateUtils.parseDate(endDate, "yyyy-MM-dd");
		//		Date beginTime = DateUtils.parseDate(DateFormatUtils.format(begin, "yyyy-MM-dd 00:00:00 000"), "yyyy-MM-dd HH:mm:ss SSS");
		//		Date endTime = DateUtils.parseDate(DateFormatUtils.format(end, "yyyy-MM-dd 23:59:59 999"), "yyyy-MM-dd HH:mm:ss SSS");
		//
		//		ArticleStatisticQueryBean query = new ArticleStatisticQueryBean();
		//		query.setCustIds("" + FMContext.getCurrent().getCustomerId());//遍历客户ID
		//		query.setCustStage(Integer.valueOf(getStage(FMContext.getCurrent().getCustomerId())));
		//		query.setTimePostFrom(beginTime.getTime());
		//		query.setTimePostTo(endTime.getTime());
		////		query.setLimit(16);
		//		CustConfigBean custConfig=getConfig();
		//		//FilterSimilar为false时查询到的文章带有相似文，为true则没有
		//		if(custConfig.getFilterSimilar()){
		//			query.setSimilar("0");//值为"0"过滤相似文
		//		}
		//		List<ArticleStatisticBean> articleStatistics = new ArrayList<ArticleStatisticBean>();
		//		if (custConfig.getFilterTrash()) {
		//			//FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
		//			articleStatistics = articleStatisticService.countArticleInCustopic(query);
		//		}else{
		//			query.setCategory("0");
		//			articleStatistics = articleStatisticService.countArticleInCustopic(query);
		//		}
		//		
		//		if (articleStatistics != null) {
		//			for (ArticleStatisticBean articleStatistic : articleStatistics) {
		//				Integer custTopicId = Integer.parseInt(articleStatistic.getCustTopicId());
		//				if(custTopicId !=0){
		//					CustTopicBean custTopicBean = custTopicService.queryCustTopic(custTopicId);
		//					if(CUST_TOPIC_STATUS.yes.getStatus().equals(custTopicBean.getStatus())){
		//						Integer total = articleStatistic.getQuantity().intValue();
		//						StatCustTopicBean statCustTopic = new StatCustTopicBean();
		//						statCustTopic.setBeginTime(beginTime);
		//						statCustTopic.setEndTime(endTime);
		//						statCustTopic.setCustId(FMContext.getCurrent().getCustomerId());
		//						statCustTopic.setTopicId(custTopicId);
		//						statCustTopic.setCount(total);
		//						result.add(statCustTopic);
		//					}
		//				}
		//				
		//			}
		//		}
		//		return result.size() <= 16?result:result.subList(0, 16);
		return statCustTopicService.searchStatCustTopic(startDate, endDate, FMConstant.CUST_TOPIC_FENBU_LIMIT);
	}
	
	public Map<String, Map<String, Long>> getStatCustTopicByMedia(List<StatCustTopicBean> statCustTopics, String startDate, String endDate) throws Exception {

		Map<String, Map<String, Long>> statisticalResults = new LinkedHashMap<String, Map<String, Long>>();
		List<ArticleStatisticQueryBean> queryBeanList = new ArrayList<ArticleStatisticQueryBean>();
		Integer[] custTopicIds = new Integer[statCustTopics.size()];
		for (int i = 0; i < statCustTopics.size(); i++) {
			ArticleStatisticQueryBean queryBean = new ArticleStatisticQueryBean();
			queryBean.setCustTopicIds(statCustTopics.get(i).getTopicId().toString());
			queryBean.setLimit(statCustTopics.get(i).getCount());//用limit属性保存count值
			queryBeanList.add(queryBean);
			custTopicIds[i] = statCustTopics.get(i).getTopicId();
		}

		List<ArticleStatisticQueryBean> availableStatCustTopicList = eliminateDisableTopic(queryBeanList, custTopicIds);//剔除不可用专题

		Collections.sort(availableStatCustTopicList, new Comparator<ArticleStatisticQueryBean>() {
			public int compare(ArticleStatisticQueryBean bean1, ArticleStatisticQueryBean bean2) {
				//按照limit(count)进行降序排列  
				if (bean1.getLimit() < bean1.getLimit()) {
					return 1;
				}
				if (bean2.getLimit() == bean2.getLimit()) {
					return 0;
				}
				return -1;
			}
		});
		
		for (ArticleStatisticQueryBean articleStatisticQueryBean : availableStatCustTopicList) {
			//key为“专题id-媒体类型id”格式
			statisticalResults.put(articleStatisticQueryBean.getCustTopicIds() + "-" + articleStatisticQueryBean.getLimit(), MapKeyComparator.sortMapByKey(countArticle(articleStatisticQueryBean, startDate, endDate, null)));
		}
		return statisticalResults;
	}

	/**
	 * 剔除不可用专题
	 * @param statCustTopics
	 * @param custTopicIds
	 * @return
	 * @throws CustTopicServiceException
	 */
	private List<ArticleStatisticQueryBean> eliminateDisableTopic(List<ArticleStatisticQueryBean> queryBeanList, Integer[] custTopicIds) throws CustTopicServiceException {
		List<ArticleStatisticQueryBean> results = new ArrayList<ArticleStatisticQueryBean>();
		CustTopicQueryBean custTopicQueryBean = new CustTopicQueryBean();
		custTopicQueryBean.setStatus(1);//启用
		custTopicQueryBean.setDataIds(custTopicIds);
		custTopicQueryBean.setNeedPaging(false);
		List<CustTopicBean> custTopicBeanList = custTopicService.queryCustTopicsByCondition(custTopicQueryBean);
		
		if (custTopicBeanList != null) {
			for (ArticleStatisticQueryBean articleStatisticQueryBean : queryBeanList) {
				if (results.size() == 10){//最多展示10个可用专题
					return results;
				}
				for (CustTopicBean custTopicBean : custTopicBeanList) {
					if (articleStatisticQueryBean.getCustTopicIds().equals(custTopicBean.getId().toString())) {
							results.add(articleStatisticQueryBean);
					}
				}
			}
		}
		return results;
	}

	public Map<Integer, List<StatWebsiteBean>> getStatWebsite(String startDate, String endDate) throws Exception {
		List<StatWebsiteBean> site1 = getStatWebsite(startDate, endDate, 1);
		List<StatWebsiteBean> site2 = getStatWebsite(startDate, endDate, 2);
		List<StatWebsiteBean> site3 = getStatWebsite(startDate, endDate, 3);
		List<StatWebsiteBean> site4 = getStatWebsite(startDate, endDate, 4);
		List<StatWebsiteBean> site5 = getStatWebsite(startDate, endDate, 5);
		List<StatWebsiteBean> site6 = getStatWebsite(startDate, endDate, 6);
		List<StatWebsiteBean> site7 = getStatWebsite(startDate, endDate, 7);
		List<StatWebsiteBean> site8 = getStatWebsite(startDate, endDate, 8);
		List<StatWebsiteBean> site11 = getStatWebsite(startDate, endDate, 11);
		return MapBuilder.put(1, site1).and(2, site2).and(3, site3).and(4, site4).and(5, site5).and(6, site6).and(7, site7).and(8, site8).and(11, site11).build();
	}

	private List<StatWebsiteBean> getStatWebsite(String startDate, String endDate, Integer mediaType) throws Exception {
		List<StatWebsiteBean> result = ListBuilder.newArrayList();

		Date begin = DateUtils.parseDate(startDate, "yyyy-MM-dd");
		Date end = DateUtils.parseDate(endDate, "yyyy-MM-dd");
		Date beginTime = DateUtils.parseDate(DateFormatUtils.format(begin, "yyyy-MM-dd 00:00:00 000"), "yyyy-MM-dd HH:mm:ss SSS");
		Date endTime = DateUtils.parseDate(DateFormatUtils.format(end, "yyyy-MM-dd 23:59:59 999"), "yyyy-MM-dd HH:mm:ss SSS");

		ArticleStatisticQueryBean query = new ArticleStatisticQueryBean();
		query.setCustIds("" + FMContext.getCurrent().getCustomerId());//遍历客户ID
		query.setCustStage(Integer.valueOf(getStage(FMContext.getCurrent().getCustomerId())));
		query.setTimePostFrom(beginTime.getTime());
		query.setTimePostTo(endTime.getTime());
		query.setMediaType(mediaType);
		query.setLimit(16);
		query.setArticleOreply(ARTICLEOREPLY_FLAG.TWO.getValue());
		CustConfigBean custConfig = getConfig();
		//FilterSimilar为true时查询到的文章带有相似文，为false则没有
		if (custConfig.getFilterSimilar()) {
			query.setSimilar("0");//值为"0"过滤相似文
		}
		List<ArticleStatisticBean> articleStatistics = new ArrayList<ArticleStatisticBean>();

		if (custConfig.getFilterTrash()) {
			//FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
			//			articleStatistics = articleStatisticService.countArticleInMediaWebsite(query);
			articleStatistics = articleStatisticService.countArticleInMediaParentWebsite(query);
		} else {
			query.setCategory("0");
			//			articleStatistics = articleStatisticService.countArticleInMediaWebsite(query);
			articleStatistics = articleStatisticService.countArticleInMediaParentWebsite(query);
		}
		if (articleStatistics != null) {
			for (ArticleStatisticBean articleStatistic : articleStatistics) {

				Integer websiteId = Integer.parseInt(articleStatistic.getWebsiteId());
				Integer total = articleStatistic.getQuantity().intValue();

				String websiteName = tagUtil.getWebsiteName(websiteId);
				if (StringUtils.isBlank(websiteName)) {
					continue;//空名称的直接跳过
				}

				StatWebsiteBean statWebsiteBean = new StatWebsiteBean();
				statWebsiteBean.setBeginTime(beginTime);
				statWebsiteBean.setEndTime(endTime);
				statWebsiteBean.setCustId(FMContext.getCurrent().getCustomerId());
				statWebsiteBean.setMediaType(7);
				statWebsiteBean.setWebsiteId(websiteId);
				statWebsiteBean.setCount(total);

				result.add(statWebsiteBean);
			}
		}
		return result;
	}

	private CustConfigBean getConfig() {
		return customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
	}

	/**
	 * 得到时间范围内的每一天的日期
	 * @param startDate  传参格式 （2016-04-05）
	 * @param endDate	 传参格式 （2016-04-05）
	 * @return
	 */
	public List<String> getDateByDay(String startDate, String endDate) {
		List<String> dateByDayList = new ArrayList<String>();
		if (startDate.equals(endDate)) {
			dateByDayList.add(endDate);
			return dateByDayList;
		}
		SimpleDateFormat dateFormat = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.DatePattern);

		Date dateOne = null;
		Date dateTwo = null;
		try {
			dateOne = dateFormat.parse(startDate);
			dateTwo = dateFormat.parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(dateOne);

		while (calendar.getTime().before(dateTwo)) {
			dateByDayList.add(dateFormat.format(calendar.getTime()));
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		dateByDayList.add(endDate);
		return dateByDayList;

	}

	public List<ConstantBean> getMedias() throws ConstantServiceException{
		List<ConstantBean> constants = constantService.listConstantByType(ConstantType.MEDIA_TYPE);
		return constants;
	}

	/****      专题topic搜索        ****/
	public List<CustTopicBean> listTopic(Integer parentId) throws CustTopicServiceException {
		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setParentId(parentId);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setStatus(1);
		query.setNeedPaging(false);
		query.setCreaterId(FMContext.getCurrent().getUserId());
		List<CustTopicBean> topics = custTopicService.listCustTopicWithShare(query);
		
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

	/**
	 * 查询出所有父子专题
	 * @param keywords
	 * @return
	 * @throws CustTopicServiceException
	 */
	public List<CustTopicView> getAllSearchTopic(String keywords) throws CustTopicServiceException {
		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setParentId(null);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setStatus(1);
		query.setCreaterId(FMContext.getCurrent().getUserId());
		query.setName(keywords);
		query.setNeedPaging(false);/**不分页*/

		List<CustTopicView> topics = new ArrayList<CustTopicView>();
		
		List<CustTopicBean> topicWithShare = custTopicService.listCustTopicWithShare(query);
		
		for (CustTopicBean custTopicBean : topicWithShare) {
			if (custTopicBean.getName().contains(keywords)) {/**过滤掉不包含关键词的父专题*/
				CustTopicView topicView = new CustTopicView();
				topicView.setId(custTopicBean.getId().toString());
				topicView.setName(custTopicBean.getName());
				topicView.setSubCount(custTopicBean.getSubCount());
				topics.add(topicView);
			}
		}
		
		return topics;
	}
	
}