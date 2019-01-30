package com.cyyun.fm.analyze.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.constant.ArticleConstants;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleStatisticService;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.analyze.bean.LineChartDataView;
import com.cyyun.fm.service.FmAnalyzeService;
import com.cyyun.fm.service.StatCustTopicService;
import com.cyyun.fm.service.bean.StatCustTopicBean;
import com.cyyun.fm.service.bean.StatMediaBean;
import com.cyyun.fm.service.bean.StatWebsiteBean;

@Component
public class OfflineAnalyzeSupport {

	@Autowired
	private FmAnalyzeService fmAnalyzeService;

	@Autowired
	private ArticleStatisticService articleStatisticService;

	@Autowired
	private CustomerConfigService customerConfigService;
	
	@Autowired
	private StatCustTopicService statCustTopicService;

	public Map<String, Integer> getSituation(String startDate, String endDate) throws Exception {
		return countArticle(startDate, endDate);
	}
	
	@SuppressWarnings("unused")
	private Map<String, Integer> countArticle(String postStartTime, String postEndTime) throws Exception {
		postStartTime = postStartTime + " 00:00:00";
		postEndTime = postEndTime + " 23:59:59";
		Date startTime = DateUtils.parseDate(postStartTime, "yyyy-MM-dd HH:mm:ss");
		Date endTime = DateUtils.parseDate(postEndTime, "yyyy-MM-dd HH:mm:ss");
		Integer customerId = FMContext.getCurrent().getCustomerId();
		StatMediaBean statMedia = fmAnalyzeService.listStatMedia(customerId, startTime, endTime);

		if (statMedia == null) {
			return null;
		}

		Map<String, Integer> countArticle = MapBuilder.put("1", statMedia.getForum()).and("2", statMedia.getBlog()).and("3", statMedia.getNews())//
				.and("4", statMedia.getWeibo()).and("5", statMedia.getPaper()).and("6", statMedia.getWeixin()).and("7", statMedia.getApp()).build();

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
	}

	public LineChartDataView getTendency(String startDate, String endDate) throws Exception {
		LineChartDataView lineChartDataView = new LineChartDataView();

		List<String> axisName = ListBuilder.newArrayList();
		List<String> legendName = ListBuilder.newArrayList();
		List<List<Long>> seriesDatas = ListBuilder.newArrayList();

		List<Long> forumList = ListBuilder.newArrayList();//论坛
		List<Long> blogList = ListBuilder.newArrayList();//博客
		List<Long> newsList = ListBuilder.newArrayList();//新闻
		List<Long> weiboList = ListBuilder.newArrayList();//微博
		List<Long> paperList = ListBuilder.newArrayList();//纸媒
		List<Long> weixinList = ListBuilder.newArrayList();//微信
		List<Long> appList = ListBuilder.newArrayList();//APP新闻

		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";
		Date startTime = DateUtils.parseDate(startDate, "yyyy-MM-dd HH:mm:ss");
		Date endTime = DateUtils.parseDate(endDate, "yyyy-MM-dd HH:mm:ss");
		Integer customerId = FMContext.getCurrent().getCustomerId();
		List<StatMediaBean> statMedias = fmAnalyzeService.listStatMediaOnDaily(customerId, startTime, endTime);

		if (CollectionUtils.isEmpty(statMedias)) {
			return null;
		}
		
		String postStartTime;
		String postEndTime;
		String startDateStr=startDate.substring(0,10);
		String endDateStr=endDate.substring(0,10);;
		String axisNameStr;;
		
		if (startDateStr.equals(endDateStr)) {
			for(int i=0;i<24;i++){
				postStartTime =endDateStr+" "+i+":00:00";
				postEndTime =endDateStr+" "+(i+1)+":00:00";
				if (i<10) {
					postStartTime=endDateStr+" "+"0"+i+":00:00";
				}
				if (i<9) {
					postEndTime =endDateStr+" "+"0"+(i+1)+":00:00";
				}
				if (i==23) {
					postEndTime =endDateStr+" "+i+":59:59";
				}
				Date begin = DateUtils.parseDate(postStartTime, "yyyy-MM-dd HH:mm:ss");
				Date end = DateUtils.parseDate(postEndTime, "yyyy-MM-dd HH:mm:ss");
				List<StatMediaBean> statMediasByHours = fmAnalyzeService.listStatMediaOnDaily(customerId, begin, end);
				
				axisNameStr =endDateStr+" "+i;
				if (i<10) {
					axisNameStr=endDateStr+" "+"0"+i;
				}
				if (i==24) {
					axisNameStr=endDateStr+" 24";
				}
				axisName.add(axisNameStr);
				
				for (StatMediaBean statMediaBean : statMediasByHours) {
					forumList.add(statMediaBean.getForum().longValue());
					blogList.add(statMediaBean.getBlog().longValue());
					newsList.add(statMediaBean.getNews().longValue());
					weiboList.add(statMediaBean.getWeibo().longValue());
					paperList.add(statMediaBean.getPaper().longValue());
					weixinList.add(statMediaBean.getWeixin().longValue());
					appList.add(statMediaBean.getApp().longValue());
				}
			}
		}else{
			for (StatMediaBean statMediaBean : statMedias) {
				
				axisName.add(DateFormatUtils.format(statMediaBean.getBeginTime(), "yyyy-MM-dd"));
				
				forumList.add(statMediaBean.getForum().longValue());
				blogList.add(statMediaBean.getBlog().longValue());
				newsList.add(statMediaBean.getNews().longValue());
				weiboList.add(statMediaBean.getWeibo().longValue());
				paperList.add(statMediaBean.getPaper().longValue());
				weixinList.add(statMediaBean.getWeixin().longValue());
				appList.add(statMediaBean.getApp().longValue());
			}
		}
				
		seriesDatas.add(forumList);
		seriesDatas.add(blogList);
		seriesDatas.add(newsList);
		seriesDatas.add(weiboList);
		seriesDatas.add(paperList);
		seriesDatas.add(weixinList);
		seriesDatas.add(appList);

		legendName.add("论坛");
		legendName.add("博客");
		legendName.add("新闻");
		legendName.add("微博");
		legendName.add("纸媒");
		legendName.add("微信");
		legendName.add("APP新闻");

		lineChartDataView.setAxisName(axisName);
		lineChartDataView.setLegendName(legendName);
		lineChartDataView.setSeriesDatas(seriesDatas);

		return lineChartDataView;
	}

	public Map<Integer, List<StatWebsiteBean>> getStatWebsite(String startDate, String endDate) throws Exception {
		startDate = startDate + " 00:00:00 000";
		endDate = endDate + " 23:59:59 999";

		Date startTime = DateUtils.parseDate(startDate, "yyyy-MM-dd HH:mm:ss SSS");
		Date endTime = DateUtils.parseDate(endDate, "yyyy-MM-dd HH:mm:ss SSS");

		Integer customerId = FMContext.getCurrent().getCustomerId();

		List<StatWebsiteBean> statWebsites = fmAnalyzeService.listStatWebsite(customerId, 7, startTime, endTime);

		return MapBuilder.put(7, statWebsites).build();
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
//		query.setLimit(16);
//
//		List<ArticleStatisticBean> articleStatistics = articleStatisticService.countArticleInCustopic(query);
//		if (articleStatistics != null) {
//			for (ArticleStatisticBean articleStatistic : articleStatistics) {
//
//				Integer custTopicId = Integer.parseInt(articleStatistic.getCustTopicId());
//				Integer total = articleStatistic.getQuantity().intValue();
//				if (custTopicId == 0) {
//					continue;
//				}
//
//				StatCustTopicBean statCustTopic = new StatCustTopicBean();
//				statCustTopic.setBeginTime(beginTime);
//				statCustTopic.setEndTime(endTime);
//				statCustTopic.setCustId(FMContext.getCurrent().getCustomerId());
//				statCustTopic.setTopicId(custTopicId);
//				statCustTopic.setCount(total);
//
//				result.add(statCustTopic);
//			}
//		}
//		return result;
		return statCustTopicService.searchStatCustTopic(startDate, endDate, FMConstant.CUST_TOPIC_FENBU_LIMIT);
	}

	private String getStage(Integer cid) {
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(cid);
		Boolean viewMonitorData = custConfig.getViewMonitorData();
		String stage = viewMonitorData != null && viewMonitorData ? ArticleConstants.ArticleStage.PUTONG : ArticleConstants.ArticleStage.GUIDANG;
		return stage;
	}
}