package com.cyyun.base.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.query.ArticleQueryBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;

/**
 * 批量导出文章
 * @author LIUJUNWU
 *
 */
public class ArticleAllUtil {

	Logger log = LoggerFactory.getLogger(ArticleAllUtil.class);

	public List<ArticleBean> listAll(ArticleQueryBean queryBean, ArticleService articleService) {
		List<ArticleQueryBean> conditions = null;
		if(StringUtils.isNotBlank(queryBean.getSpiderStartTime()) && StringUtils.isNotBlank(queryBean.getSpiderEndTime())){
			String startStr = queryBean.getSpiderStartTime();
			if(startStr.length()==10){
				queryBean.setSpiderStartTime(startStr + " 00:00:00"); 
			}else if(startStr.length()==16){
				queryBean.setSpiderStartTime(startStr + ":00"); 
			}
			String endStr = queryBean.getSpiderEndTime();
			if(endStr.length()==10){
				queryBean.setSpiderEndTime(endStr + " 23:59:59"); 
			}else if(endStr.length()==16){
				queryBean.setSpiderEndTime(endStr + ":59"); 
			}
			conditions = halfFetchSpider(queryBean, new ArrayList<ArticleQueryBean>(), articleService);
		}else if(StringUtils.isNotBlank(queryBean.getPostStartTime()) && StringUtils.isNotBlank(queryBean.getPostEndTime())){
			String startStr = queryBean.getPostStartTime();
			if(startStr.length()==10){
				queryBean.setPostStartTime(startStr + " 00:00:00"); 
			}else if(startStr.length()==16){
				queryBean.setPostStartTime(startStr + ":00"); 
			}
			String endStr = queryBean.getPostEndTime();
			if(endStr.length()==10){
				queryBean.setPostEndTime(endStr + " 23:59:59"); 
			}else if(endStr.length()==16){
				queryBean.setPostEndTime(endStr + ":59"); 
			}
			conditions = halfFetchPost(queryBean, new ArrayList<ArticleQueryBean>(), articleService);
		}
		
		if(CollectionUtils.isEmpty(conditions)){
			return new ArrayList<>();
		}
		long start = System.currentTimeMillis();
		log.info("导出文章开始查询文章...");
		List<ArticleBean> toReturn = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(5);
		List<Future<List<ArticleBean>>> resultFuture = new ArrayList<>();
		for (ArticleQueryBean qb : conditions) {
			ListArticleByConditionOne condition = new ListArticleByConditionOne(qb, articleService);
			Future<List<ArticleBean>> f = executor.submit(condition);
			resultFuture.add(f);
		}
		List<List<ArticleBean>> tempList =new ArrayList<List<ArticleBean>>();
		for (Future<List<ArticleBean>> f : resultFuture) {
			try {
				tempList.add(f.get());
			} catch (NullPointerException e) {
				log.info("查询文章为空");
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			} catch (ExecutionException e) {
				log.error(e.getMessage(), e);
			} finally {
				//启动一次顺序关闭，执行以前提交的任务，但不接受新任务。如果已经关闭，则调用没有其他作用。
				executor.shutdown();
			}
		}
		//由于是分开几个任务查询的数据，降序排列时整个结果集的顺序就乱了，做反转处理
		if ("0".equals(conditions.get(0).getArticleOrderBy())) {
			Collections.reverse(tempList);
		}
		for (List<ArticleBean> list : tempList) {
			toReturn.addAll(list);
		}
		log.info("导出文章查询文章结束... 需要时间：{} 毫秒", System.currentTimeMillis()-start );
		//对文章list集合排序 只对按回复数、阅读数排序生效
		toReturn = listSort(toReturn, conditions.get(0).getOrder(), conditions.get(0).getArticleOrderBy());
		return toReturn;
	}
	
	/**
	 * 一次性查询所有文章
	 */
	class ListArticleByConditionOne implements Callable<List<ArticleBean>> {
		ArticleQueryBean queryBean;
		ArticleService articleServcie;

		public ListArticleByConditionOne(ArticleQueryBean queryBean, ArticleService articleService) {
			this.queryBean = queryBean;
			this.articleServcie = articleService;
		}

		@Override
		public List<ArticleBean> call() throws Exception {
			Integer total = queryBean.getPageSize();
			queryBean.setPageSize(total);
			queryBean.setPageNo(1);
			PageInfoBean<ArticleBean> page = null;
			try {
				page = articleServcie.queryArticleByPage(queryBean);
			} catch (Exception e) {
				// 如果第一次查询异常，则catch到，然后继续用下面的for循环去查询文章
			}
			
			int count = 0;
			while(page == null || CollectionUtils.isEmpty(page.getData()) || page.getTotalRecords()<total){
				if(count>2){
					break;
				}
				Thread.sleep(500);
				try {
					page = articleServcie.queryArticleByPage(queryBean);
				} catch (Exception e) {
					// 如果有异常，则catch，直到3次循环结束
				}
				count++;
			}
			
			if(page == null){
				log.error("查询文章异常！！！查询参数为：{}", queryBean);
			}
			log.info("查询到文章数量为：{}，实际文章数量：{}，查询参数为：{}", page.getTotalRecords(),total, queryBean);
			return page.getData();
		}
	}

	/**
	 * 分次导出
	 */
	class ListArticleByCondition implements Callable<List<ArticleBean>> {

		ArticleQueryBean queryBean;
		ArticleService articleServcie;

		public ListArticleByCondition(ArticleQueryBean queryBean, ArticleService articleService) {
			this.queryBean = queryBean;
			this.articleServcie = articleService;
		}

		@Override
		public List<ArticleBean> call() throws Exception {
			Integer total = queryBean.getPageSize();
			List<ArticleBean> result = new ArrayList<>();
			queryBean.setPageSize(100);
			while (true) {
				PageInfoBean<ArticleBean> page = null;
				try {
					page = articleServcie.queryArticleByPage(queryBean);
				} catch (Exception e) {
					// 如果第一次查询异常，则catch到，然后继续用下面的for循环去查询文章
				}

				if (page == null || CollectionUtils.isEmpty(page.getData())) {
					for (int i = 0; i < 3; i++) {
						try {
							page = articleServcie.queryArticleByPage(queryBean);
						} catch (Exception e) {
							// 如果有异常，则catch，直到3次循环结束
						}
					}
				}

				if (page != null) {
					result.addAll(page.getData());
				}

				if (queryBean.getPageNo() * 100 >= total) {
					break;
				}
				queryBean.setPageNo(queryBean.getPageNo() + 1);
			}
			return result;
		}
	}

	private List<ArticleQueryBean> halfFetchPost(ArticleQueryBean queryBean, List<ArticleQueryBean> result, ArticleService articleServcie) {
		Long count = 0L;
		try {
			count = articleServcie.queryArticleCount(queryBean);
			if (count == 0) {
				count = articleServcie.queryArticleCount(queryBean);
			}
		} catch (ArticleServiceException e) {
			return result;
		}

		if (count > 1000 && (CyyunDateUtils.parseDate(queryBean.getPostEndTime(), CyyunDateUtils.DATEFORMATLONG).getTime()-CyyunDateUtils.parseDate(queryBean.getPostStartTime(), CyyunDateUtils.DATEFORMATLONG).getTime())>1000) {
			Date middleDate = makeMiddleDate(CyyunDateUtils.parseDate(queryBean.getPostStartTime(), CyyunDateUtils.DATEFORMATLONG), CyyunDateUtils.parseDate(queryBean.getPostEndTime(), CyyunDateUtils.DATEFORMATLONG));

			ArticleQueryBean queryBeanPart1 = BeanUtil.copy(queryBean, ArticleQueryBean.class);
			queryBeanPart1.setPostEndTime(CyyunDateUtils.formatDate(middleDate, CyyunDateUtils.DATEFORMATLONG));

			halfFetchPost(queryBeanPart1, result, articleServcie);

			ArticleQueryBean queryBeanPart2 = BeanUtil.copy(queryBean, ArticleQueryBean.class);
			Calendar cal = Calendar.getInstance();
			cal.setTime(middleDate);
			cal.add(Calendar.SECOND, 1);
			queryBeanPart2.setPostStartTime(CyyunDateUtils.formatDate(cal.getTime(), CyyunDateUtils.DATEFORMATLONG));
			halfFetchPost(queryBeanPart2, result, articleServcie);
		} else {
			queryBean.setPageNo(1);
			queryBean.setPageSize(count.intValue());
			result.add(queryBean);
		}

		return result;
	}

	private List<ArticleQueryBean> halfFetchSpider(ArticleQueryBean queryBean, List<ArticleQueryBean> result, ArticleService articleServcie) {
		Long count = 0L;
		try {
			count = articleServcie.queryArticleCount(queryBean);
			if (count == 0) {
				count = articleServcie.queryArticleCount(queryBean);
			}
		} catch (ArticleServiceException e) {
			return result;
		}

		if (count > 1000 && (CyyunDateUtils.parseDate(queryBean.getSpiderEndTime(), CyyunDateUtils.DATEFORMATLONG).getTime()-CyyunDateUtils.parseDate(queryBean.getSpiderStartTime(), CyyunDateUtils.DATEFORMATLONG).getTime()) > 1000) {
			Date middleDate = makeMiddleDate(CyyunDateUtils.parseDate(queryBean.getSpiderStartTime(), CyyunDateUtils.DATEFORMATLONG), CyyunDateUtils.parseDate(queryBean.getSpiderEndTime(), CyyunDateUtils.DATEFORMATLONG));

			ArticleQueryBean queryBeanPart1 = BeanUtil.copy(queryBean, ArticleQueryBean.class);
			queryBeanPart1.setSpiderEndTime(CyyunDateUtils.formatDate(middleDate, CyyunDateUtils.DATEFORMATLONG));

			halfFetchSpider(queryBeanPart1, result, articleServcie);

			ArticleQueryBean queryBeanPart2 = BeanUtil.copy(queryBean, ArticleQueryBean.class);
			Calendar cal = Calendar.getInstance();
			cal.setTime(middleDate);
			cal.add(Calendar.SECOND, 1);
			queryBeanPart2.setSpiderStartTime(CyyunDateUtils.formatDate(cal.getTime(), CyyunDateUtils.DATEFORMATLONG));
			halfFetchSpider(queryBeanPart2, result, articleServcie);
		} else {
			queryBean.setPageNo(1);
			queryBean.setPageSize(count.intValue());
			result.add(queryBean);
		}

		return result;
	}

	private static Date makeMiddleDate(Date start, Date end) {

		Long internalTime = end.getTime() - start.getTime();
		Long halfInternalTime = internalTime / 2;
		return new Date(start.getTime() + halfInternalTime);

	}
	
	/**
	 * 对文章list集合排序
	 * 只对按回复数、阅读数排序生效
	 * @param list
	 * @param order
	 * @param orderBy
	 * @return
	 */
	private List<ArticleBean> listSort(List<ArticleBean> list, final String order, final String orderBy) {
		if ("3".equals(order) || "4".equals(order)) {//3-回复数，4-阅读数  
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
			Collections.sort(list, new Comparator<ArticleBean>() {
				public int compare(ArticleBean A1, ArticleBean A2) {
					Integer property1 = "3".equals(order) ? A1.getReplyCount() : A1.getReadCount();
					Integer property2 = "3".equals(order) ? A2.getReplyCount() : A2.getReadCount();
					if ("0".equals(orderBy)) {//降序排列
						return property2.compareTo(property1);
					} else {//升序排列
						return property1.compareTo(property2);
					}
				}
			});
		}
		return list;
	}
}
