package com.cyyun.fm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyyun.base.constant.ConstantType;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.ArticleCustomerAttrBean;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.query.CustTopicQueryBean;
import com.cyyun.base.service.exception.CustTopicServiceException;
import com.cyyun.base.util.ArticleAllUtil;
import com.cyyun.base.util.ConstUtil;
import com.cyyun.base.util.UnicodeFilter;
import com.cyyun.fm.report.templete.TemplateUtil;
import com.cyyun.fm.vo.ArticleNumVo;

/** 
 * @author  SuoLiang  
 * @version 2016年9月19日
 */
@Service("CountArticleNumServiceImpl")
public class CountArticleNumServiceImpl implements CountArticleNumService {

	Logger log = LoggerFactory.getLogger(ArticleAllUtil.class);

	@Autowired
	private CustTopicService custTopicService;
	
	@Autowired
	private ConstUtil constUtil;

	@Override
	public ArticleNumVo getArticleCountMap(List<ArticleBean> list) throws CustTopicServiceException {

		ArticleNumVo articleNumVo = new ArticleNumVo();
		Map<Integer, Long> countSentimentArticleMap = new HashMap<Integer, Long>();
		Map<Integer, Long> countTopicArticleMap = new HashMap<Integer, Long>();
		Map<Integer, Long> countStyleArticleMap = new HashMap<Integer, Long>();
		LinkedHashMap<String, ArticleBean> articleBeanMap = new LinkedHashMap<String, ArticleBean>();
		List<String> weiboGuidList = new ArrayList<String>();

		String cId = FMContext.getCurrent().getCustomerId().toString();
		Integer sentimentKey = 0;
		Long countSentiment = 0L;
		Integer mediaType = 0;
		Long countMedia = 0L;
		Set<Integer> custTopicIds = new HashSet<Integer>();
		//获取当前客户下的可用专题
		String allCustTopicIds = getAllCustTopicId();
		List<String> custTopicIdList = new ArrayList<String>();
		if (StringUtils.isNotBlank(allCustTopicIds)) {
			allCustTopicIds = allCustTopicIds.substring(0, allCustTopicIds.length() - 1);
		}
		custTopicIdList = Arrays.asList(StringUtils.trimToEmpty(allCustTopicIds).split(","));
		long start = System.currentTimeMillis();
		log.info("导出文章开始统计文章...");
		for (ArticleBean articleBean : list) {
			//字段特殊处理
			articleBean.setAuthor(articleBean.getAuthor() == null ? "" : articleBean.getAuthor());
			articleBean.setAbContent(TemplateUtil.checkSize(UnicodeFilter.filter(articleBean.getAbContent())));
			articleBean.setTitle(TemplateUtil.checkSize(UnicodeFilter.filter(articleBean.getTitle())));
			articleBean.setContent(articleBean.getAbContent());//文章内容替换成摘要

			//按正负面统计
			try {
				sentimentKey = articleBean.getCustAttrs().get(cId).getSentiment();
			} catch (Exception e) {
				log.error("获取Sentiment属性异常!!GUID=" + articleBean.getGuid());
			}
			countSentiment = countSentimentArticleMap.get(sentimentKey);
			countSentimentArticleMap.put(sentimentKey, (countSentiment == null) ? 1 : countSentiment + 1);

			//按专题统计
			try {
				custTopicIds = articleBean.getCustAttrs().get(cId).getCustTopicIds();
			} catch (Exception e) {
				log.error("获取CustTopicIds属性异常!!GUID=" + articleBean.getGuid());
			}
			/**专题展示包含在专题列表里的*/
			Set<Integer> custAttrCustTopic = new HashSet<Integer>();
			if (custTopicIds != null) {
				for (Integer custTopicId : custTopicIds) {
					/**统计包括分享的专题*/
					if (custTopicIdList.contains(custTopicId.toString())) {
						Long countTopic = countTopicArticleMap.get(custTopicId);
						countTopicArticleMap.put(custTopicId, (countTopic == null) ? 1 : countTopic + 1);
						custAttrCustTopic.add(custTopicId);
					}
				}
				//为避免导出模版中多层对象取值，导致jxls包导出Excel时内部报出警告
				articleBean.setWebsiteTagIds(custAttrCustTopic.toArray(new Integer[] {}));
			} else {
				articleBean.setWebsiteTagIds(new Integer[] {});
			}
			
			//预警等级字段赋值
			Map<String, ArticleCustomerAttrBean> custAttrs = articleBean.getCustAttrs();
			String warnName = "";
			if (custAttrs != null) {
				ArticleCustomerAttrBean articleCustomerAttrBean = custAttrs.get(cId);
				if (articleCustomerAttrBean != null) {
					//预警已通知过的文章(客户stage=22)才显示"预警"级别
					if(articleCustomerAttrBean.getStage() == 22){
						warnName = constUtil.getName(ConstantType.WARN_LEVEL, articleCustomerAttrBean.getWarnLevel());
					}
				}
				if (StringUtils.isBlank(warnName)) {
					warnName = "未预警";
				}
			}
			/**此处导出将预警级别放到boardName字段中方便获取*/
			articleBean.setBoardName(warnName);

			//放入articleBeanMap
			articleBeanMap.put(articleBean.getGuid(), articleBean);

			//收集微博数据Guid
			if (articleBean.getStyle() == 4) {
				weiboGuidList.add(articleBean.getGuid());
			}

			//按媒体类型统计
			mediaType = articleBean.getStyle();
			countMedia = countStyleArticleMap.get(mediaType);
			countStyleArticleMap.put(mediaType, (countMedia == null) ? 1 : countMedia + 1);

		}
		//统计为空处理
		if (countSentimentArticleMap.size() < 4) {
			for (Integer i = 0; i < 4; i++) {
				if (!countSentimentArticleMap.keySet().contains(i)) {
					countSentimentArticleMap.put(i, 0L);
				}
			}
		}
		if (countStyleArticleMap.size() < 12) {
			for (Integer i = 0; i < 12; i++) {
				if (!countStyleArticleMap.keySet().contains(i)) {
					countStyleArticleMap.put(i, 0L);
				}
			}
		}
		log.info("导出文章统计文章结束... 需要时间：{} 毫秒", System.currentTimeMillis() - start);
		articleNumVo.setCountSentimentArticleMap(countSentimentArticleMap);
		articleNumVo.setCountStyleArticleMap(countStyleArticleMap);
		articleNumVo.setCountTopicArticleMap(countTopicArticleMap);
		articleNumVo.setArticleBeanMap(articleBeanMap);
		articleNumVo.setWeiboGuidList(weiboGuidList);
		return articleNumVo;
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

}
