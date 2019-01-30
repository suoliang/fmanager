package com.cyyun.fm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyyun.base.constant.ConstantType;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleStatisticService;
import com.cyyun.base.service.ConstantService;
import com.cyyun.base.service.bean.ArticleStatisticBean;
import com.cyyun.base.service.bean.ConstantBean;
import com.cyyun.base.service.bean.query.ArticleStatisticQueryBean;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;

@Service
public class AnalyzeSituationServiceImpl implements AnalyzeSituationService{

	@Autowired
	private CustomerConfigService customerConfigService;
	
	@Autowired
	private ArticleStatisticService articleStatisticService;
	
	@Autowired
	private ConstantService constantService;
	
	@Autowired
	private FmCustomerConfigService fmCustomerConfigService;
	

	@Override
	public Map<String, Long> analyzeSituation(String startDate, String endDate, Integer[] topic, CustConfigBean custConfig) throws Exception {
		Map<String, Long> sortCountArticle = new HashMap<String, Long>();
		Map<String, Long> resultCountArticle = new HashMap<String, Long>();
		Integer customerId = FMContext.getCurrent().getCustomerId();
		ArticleStatisticQueryBean queryBean = new ArticleStatisticQueryBean();
		String format = "yyyy-MM-dd HH:mm:ss";
		queryBean.setTimePostFrom(DateUtils.parseDate(startDate + " 00:00:00", format).getTime());
		queryBean.setTimePostTo(DateUtils.parseDate(endDate + " 23:59:59", format).getTime());
		queryBean.setLimit(20);
		queryBean.setCustStage(Integer.parseInt(fmCustomerConfigService.getArticleStage()));
		queryBean.setCustIds(customerId.toString());
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
		//只保留九种媒体的数据
		List<ConstantBean> constants = constantService.listConstantByType(ConstantType.MEDIA_TYPE);
		for (ConstantBean bean : constants) {
			Long countLong = sortCountArticle.get(bean.getValue());
			resultCountArticle.put(bean.getValue(), countLong == null ? 0 : countLong);
		}
		return resultCountArticle;
	}
}
