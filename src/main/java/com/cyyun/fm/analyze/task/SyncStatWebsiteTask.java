package com.cyyun.fm.analyze.task;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cyyun.base.constant.ArticleConstants;
import com.cyyun.base.constant.ConstantType;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleStatisticService;
import com.cyyun.base.service.ConstantService;
import com.cyyun.base.service.WebsiteService;
import com.cyyun.base.service.bean.ArticleStatisticBean;
import com.cyyun.base.service.bean.ConstantBean;
import com.cyyun.base.service.bean.WebsiteBean;
import com.cyyun.base.service.bean.query.ArticleStatisticQueryBean;
import com.cyyun.base.service.exception.ArticleStatisticServiceException;
import com.cyyun.base.service.exception.WebsiteServiceException;
import com.cyyun.base.task.SyncStatTask;
import com.cyyun.base.task.SyncStatTimePoint;
import com.cyyun.base.util.TagUtil;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.service.FmAnalyzeService;
import com.cyyun.fm.service.bean.StatWebsiteBean;

/**
 * <h3>同步媒体发文数</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Service("syncStatWebsiteTask")
public class SyncStatWebsiteTask implements SyncStatTask {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${task.syncStatWebsite}")
	private Boolean syncStatWebsite;

	@Autowired
	private ArticleStatisticService articleStatisticService;

	@Autowired
	private ConstantService constantService;

	@Autowired
	private FmAnalyzeService fmAnalyzeService;

	@Autowired
	private CustomerConfigService customerConfigService;

	@Autowired
	private TagUtil tagUtil;

	@Override
	public void execute(List<SyncStatTimePoint> points) {
		if (!syncStatWebsite) {
			return;
		}
		try {
			Integer custId = 700;
			List<ConstantBean> constants = constantService.listConstantByType(ConstantType.MEDIA_TYPE);

			for (SyncStatTimePoint timePoint : points) {
				if (log.isDebugEnabled()) {
					log.debug("Sync StatWebsite[beginTime=" + DateFormatUtils.format(timePoint.getBeginTime(), "yyyy-MM-dd HH:mm:ss SSS") + ", endTime=" + DateFormatUtils.format(timePoint.getEndTime(), "yyyy-MM-dd HH:mm:ss SSS") + "]");
				}
				for (ConstantBean constant : constants) {
					Integer mediaType = Integer.parseInt(constant.getValue());
					syncStatWebsite(custId, timePoint.getBeginTime(), timePoint.getEndTime(), mediaType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void syncStatWebsite(Integer custId, Date beginTime, Date endTime, Integer mediaType) throws Exception {
		ArticleStatisticQueryBean query = new ArticleStatisticQueryBean();
		query.setCustIds("" + custId);//遍历客户ID
		query.setCustStage(Integer.valueOf(getStage(FMContext.getCurrent().getCustomerId())));
		query.setTimePostFrom(beginTime.getTime());
		query.setTimePostTo(endTime.getTime());
		query.setMediaType(mediaType);
		query.setLimit(500);
		List<ArticleStatisticBean> articleStatistics = articleStatisticService.countArticleInMediaWebsite(query);

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
				statWebsiteBean.setCustId(custId);
				statWebsiteBean.setMediaType(mediaType);
				statWebsiteBean.setWebsiteId(websiteId);

				try {
					StatWebsiteBean oldStatWebsite = fmAnalyzeService.findStatWebsite(statWebsiteBean);
					if (oldStatWebsite != null) {
						statWebsiteBean = oldStatWebsite;
					}

					statWebsiteBean.setCount(total);//查询之后 传入总数

					statWebsiteBean = fmAnalyzeService.saveStatWebsite(statWebsiteBean);

					if (log.isDebugEnabled()) {
						log.debug("Sync StatWebsite Success! [id=" + statWebsiteBean.getId() + ", mediaType=" + statWebsiteBean.getMediaType() + ", websiteId=" + statWebsiteBean.getWebsiteId() + ", custId=" + statWebsiteBean.getCustId() + "]");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getStage(Integer cid) {
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(cid);
		Boolean viewMonitorData = custConfig.getViewMonitorData();
		String stage = viewMonitorData != null && viewMonitorData ? ArticleConstants.ArticleStage.PUTONG : ArticleConstants.ArticleStage.GUIDANG;
		return stage;
	}
}