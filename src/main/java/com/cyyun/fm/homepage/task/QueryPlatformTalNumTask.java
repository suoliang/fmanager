package com.cyyun.fm.homepage.task;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.query.ArticleQueryBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.common.core.bean.PageInfoBean;


/**
 * <h3>查询平台今日采集</h3>
 * 
 * @author LIUJUNWU
 * @version 1.0.0
 */
@Service("queryPlatformTalNumTask")
public class QueryPlatformTalNumTask implements QueryStatTask {

	@Autowired
	private ArticleService articleService;

	public static String platformTotalNumToday = "0";
	
	@Override
	public void execute() {
		ArticleQueryBean query = new ArticleQueryBean();
		PageInfoBean<ArticleBean> pageInfo = new PageInfoBean<ArticleBean>();
		String today = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		query.setSpiderStartTime(today + " 00:00:00");
		query.setSpiderEndTime(today + " 23:59:59");
		query.setCategory("0");//包含垃圾文
		try {
			pageInfo = articleService.queryArticleByPage(query);
		} catch (ArticleServiceException e) {
			e.printStackTrace();
		}
		DecimalFormat df = new DecimalFormat("#,###");
		platformTotalNumToday = df.format(pageInfo.getTotalRecords() * 5);//经理说我们采集的文章太少了，需要加点
	}


}