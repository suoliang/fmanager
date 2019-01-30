package com.cyyun.fm.analyze.task;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cyyun.base.constant.ArticleConstants;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleStatisticService;
import com.cyyun.base.service.bean.ArticleStatisticBean;
import com.cyyun.base.service.bean.query.ArticleStatisticQueryBean;
import com.cyyun.base.service.exception.ArticleStatisticServiceException;
import com.cyyun.base.task.SyncStatTask;
import com.cyyun.base.task.SyncStatTimePoint;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.service.FmAnalyzeService;
import com.cyyun.fm.service.bean.StatMediaBean;
import com.cyyun.fm.service.exception.FmAnalyzeException;

/**
 * <h3>同步媒体发文数</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Service("syncStatMediaTask")
public class SyncStatMediaTask implements SyncStatTask {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${task.syncStatMedia}")
	private Boolean syncStatMedia;

	@Autowired
	private ArticleStatisticService articleStatisticService;

	@Autowired
	private CustomerConfigService customerConfigService;

	@Autowired
	private FmAnalyzeService fmAnalyzeService;

	@Override
	public void execute(List<SyncStatTimePoint> points) {
		if (!syncStatMedia) {
			return;
		}
		List<Integer> custIds = ListBuilder.add(700).and(81).build();
		for (SyncStatTimePoint timePoint : points) {
			for (Integer custId : custIds) {
				Date beginTime = timePoint.getBeginTime();
				Date endTime = timePoint.getEndTime();
				try {
					syncStatMedia(custId, beginTime, endTime);
				} catch (Exception e) {
					log.debug("同步失败[开始时间=" + DateFormatUtils.format(beginTime, "yyyy-MM-dd HH:mm:ss") + ", 结束时间=" + DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss") + ", CID=" + custId + "]", e);
				}
			}
		}
	}

	private void syncStatMedia(Integer custId, Date beginTime, Date endTime) throws ArticleStatisticServiceException, FmAnalyzeException {
		StatMediaBean statMediaBean = new StatMediaBean();
		statMediaBean.setBeginTime(beginTime);
		statMediaBean.setEndTime(DateUtils.addMilliseconds(endTime, 1));
		statMediaBean.setCustId(custId);

		fillStatisticalData(custId, beginTime, endTime, statMediaBean);

		statMediaBean = fmAnalyzeService.saveStatMedia(statMediaBean);

		if (log.isDebugEnabled()) {
			log.debug("同步成功[开始时间=" + DateFormatUtils.format(beginTime, "yyyy-MM-dd HH:mm:ss") + ", 结束时间=" + DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss") + ", CID=" + custId + "]");
		}
	}

	private void fillStatisticalData(Integer custId, Date beginTime, Date endTime, StatMediaBean statMediaBean) throws ArticleStatisticServiceException {
		ArticleStatisticQueryBean query = new ArticleStatisticQueryBean();
		query.setCustIds("" + custId);
		query.setCustStage(Integer.valueOf(getStage(custId)));
		query.setTimePostFrom(beginTime.getTime());
		query.setTimePostTo(endTime.getTime());
		query.setLimit(7);
		List<ArticleStatisticBean> articleStatistics = articleStatisticService.countArticleInMedia(query);

		if (articleStatistics != null) {
			for (ArticleStatisticBean articleStatistic : articleStatistics) {

				String mediaKey = articleStatistic.getMedia();
				Integer total = articleStatistic.getQuantity().intValue();

				if ("1".equals(mediaKey)) {
					statMediaBean.setForum(total);
				} else if ("2".equals(mediaKey)) {
					statMediaBean.setBlog(total);
				} else if ("3".equals(mediaKey)) {
					statMediaBean.setNews(total);
				} else if ("4".equals(mediaKey)) {
					statMediaBean.setWeibo(total);
				} else if ("5".equals(mediaKey)) {
					statMediaBean.setPaper(total);
				} else if ("6".equals(mediaKey)) {
					statMediaBean.setWeixin(total);
				} else if ("7".equals(mediaKey)) {
					statMediaBean.setApp(total);
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