package com.cyyun.fm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleStatisticService;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.bean.ArticleStatisticBean;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.query.ArticleStatisticQueryBean;
import com.cyyun.base.util.TagUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.service.bean.StatCustTopicBean;

@Service
public class StatCustTopicServiceImpl implements StatCustTopicService{

	@Autowired
	private ArticleStatisticService articleStatisticService;
	
	@Autowired
	private CustTopicService custTopicService;
	
	@Autowired
	private FmCustomerConfigService fmCustomerConfigService;
	
	@Autowired
	private TagUtil tagUtil;
	
	@Autowired
	private FmCustTopicService fmCustTopicService;
	
	@Override
	public List<StatCustTopicBean> searchStatCustTopic(String startDate,
			String endDate,Integer limit) throws Exception {
		List<StatCustTopicBean> result = ListBuilder.newArrayList();
//		Date beginTime = DateUtils.parseDate(startDate + " 00:00:00",
//				FMConstant.DATE_FOMAT_YYYYMMDDHHMMSS);
//		Date endTime = DateUtils.parseDate(endDate + " 23:59:59",
//				FMConstant.DATE_FOMAT_YYYYMMDDHHMMSS);
//		ArticleStatisticQueryBean query = new ArticleStatisticQueryBean();
//		query.setCustIds(FMContext.getCurrent().getCustomerId().toString());// 遍历客户ID
//		query.setCustStage(Integer.valueOf(fmCustomerConfigService
//				.getArticleStage()));
//		query.setTimePostFrom(beginTime.getTime());
//		query.setTimePostTo(endTime.getTime());
//		CustConfigBean custConfig = fmCustomerConfigService.getCustConfig();
//
//		if (custConfig.getFilterSimilar()) {// FilterSimilar为false时查询到的文章带有相似文，为true则没有
//			query.setSimilar("0");//值为"0"过滤相似文
//		}
//		if (!custConfig.getFilterTrash()) {// FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
//			query.setCategory("0");
//		}
		List<ArticleStatisticBean> articleStatistics = countArticleInCustopic(startDate,endDate);
		if (CollectionUtils.isNotEmpty(articleStatistics)) {
			for (ArticleStatisticBean articleStatistic : articleStatistics) {
				Integer custTopicId = Integer.parseInt(articleStatistic
						.getCustTopicId());
				if (custTopicId != 0) {
//					CustTopicBean custTopicBean = custTopicService
//							.queryCustTopic(custTopicId);
//					if (CUST_TOPIC_STATUS.yes.getStatus().equals(
//							custTopicBean.getStatus())) {
						Integer total = articleStatistic.getQuantity()
								.intValue();
						StatCustTopicBean statCustTopic = new StatCustTopicBean();
//						statCustTopic.setBeginTime(beginTime);
//						statCustTopic.setEndTime(endTime);
						statCustTopic.setName(tagUtil.getCustTopicName(custTopicId));
						statCustTopic.setCustId(FMContext.getCurrent()
								.getCustomerId());
						statCustTopic.setTopicId(custTopicId);
						statCustTopic.setCount(total);
						result.add(statCustTopic);
//					}
				}

			}
		}
		return result.size() <= limit ? result : result.subList(0, limit);
	}

	@Override
	public List<ArticleStatisticBean> countArticleInCustopic(String startDate, String endDate) throws Exception {
		Date beginTime = DateUtils.parseDate(startDate + " 00:00:00", FMConstant.DATE_FOMAT_YYYYMMDDHHMMSS);
		Date endTime = DateUtils.parseDate(endDate + " 23:59:59", FMConstant.DATE_FOMAT_YYYYMMDDHHMMSS);
		ArticleStatisticQueryBean query = new ArticleStatisticQueryBean();
		query.setCustIds(FMContext.getCurrent().getCustomerId().toString());// 遍历客户ID
		query.setCustStage(Integer.valueOf(fmCustomerConfigService.getArticleStage()));
		query.setTimePostFrom(beginTime.getTime());
		query.setTimePostTo(endTime.getTime());
		CustConfigBean custConfig = fmCustomerConfigService.getCustConfig();

		if (custConfig.getFilterSimilar()) {// FilterSimilar为false时查询到的文章带有相似文，为true则没有
			query.setSimilar("0");//值为"0"过滤相似文
		}
		if (!custConfig.getFilterTrash()) {// FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
			query.setCategory("0");
		}
		List<CustTopicBean> list = fmCustTopicService.queryCurrentCustAllTopics();
		List<ArticleStatisticBean> articleStatistics = new ArrayList<ArticleStatisticBean>();
		List<ArticleStatisticBean> delArticleStatistics = new ArrayList<ArticleStatisticBean>();
		if(CollectionUtils.isNotEmpty(list)){
			query.setLimit(list.size());
			articleStatistics = articleStatisticService.countArticleInCustopic(query);
			//剔除专题不存在的统计结果（注：articleStatisticService.countArticleInCustopic接口所统计的专题是大数据那根据文章所属专题来统计的）
			if(CollectionUtils.isNotEmpty(articleStatistics)){
				for (ArticleStatisticBean articleStatisticBean : articleStatistics) {
					CustTopicBean custTopicBean = custTopicService.queryCustTopic(Integer.parseInt(articleStatisticBean.getCustTopicId()));
					/**专题不可用的不显示*/
					if (custTopicBean.getId() == null || custTopicBean.getStatus() == 0) {
						delArticleStatistics.add(articleStatisticBean);
					}
				}
				articleStatistics.removeAll(delArticleStatistics);
			}
		}
		return articleStatistics;
	}
}
