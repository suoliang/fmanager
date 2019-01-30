package com.cyyun.fm.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.CustTopicKeywordService;
import com.cyyun.base.service.KeywordService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.CustTopicKeywordBean;
import com.cyyun.base.service.bean.KeywordBean;
import com.cyyun.base.service.bean.query.KeywordQueryBean;
import com.cyyun.base.util.CyyunBeanUtils;
import com.cyyun.base.util.CyyunStringUtils;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.fm.base.controller.BaseSupport;
import com.cyyun.fm.custtopic.bean.ConditionParam;
import com.cyyun.fm.vo.ArticleVo;
import com.cyyun.fm.vo.ArticleVoInter;

@Service("ArticleQueryServiceImpl")
public class ArticleQueryServiceImpl implements ArticleQueryService{

	@Autowired
	private ArticleService articleService;
	
	@Autowired
	protected CustomerConfigService customerConfigService;
	
	@Autowired
	private CustTopicKeywordService custTopicKeywordService;
	
	@Resource(name = "CusttopicSupport")
	private FmSupportService fmSupportService;
	
	@Autowired
	private KeywordService keywordService;
	
	@Override
	public PageInfoBean<ArticleVoInter> queryArticleByParam(Map<String, String[]> params, HttpServletRequest request) throws Exception {
		Map<String, String> queryMap = CyyunStringUtils.request2Map(params);
		ConditionParam conditionParam = new ConditionParam();
		// 最近7天
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		String pattern = "yyyy-MM-dd";
		String end = DateFormatUtils.format(cal.getTime(), pattern) + " 23:59:59";
		cal.add(Calendar.DATE, -6);
		String start = DateFormatUtils.format(cal.getTime(), pattern) + " 00:00:00";
		conditionParam.setStartTime(start);
		conditionParam.setEndTime(end);
		//conditionParam.setStartTime(queryMap.get("startTimeInput"));
		//conditionParam.setEndTime(queryMap.get("endTimeInput"));
		conditionParam.setTopicId(new Integer[] { Integer.valueOf(queryMap.get("topicId")) });
		conditionParam.setCurrentpage(1);
		conditionParam.setPagesize(6);
		return fmSupportService.queryArticleByParam(conditionParam, request);
	}

	@Override
	public ArticleVoInter findArticle(String guid, String keyword,BaseSupport baseSupport, HttpServletRequest request)
			throws Exception {
			ArticleBean article = articleService.findArticleWithImagePositin(guid);

			List<Integer> custTopicIdList = new ArrayList<Integer>();
			
			HttpSession session = request.getSession();
			List<String> custTopicIds = (List<String>) session.getAttribute("CUSTTOPICIDS_WITHSHARE");
			boolean flag = CollectionUtils.isNotEmpty(custTopicIds);
			Set<Integer> custAttr = new HashSet<Integer>();
			String cid = FMContext.getCurrent().getCustomerId().toString();
			Set<Integer> custAttrCustTopicIds = null;
			if (article.getCustAttrs().get(cid) != null) {
				custAttrCustTopicIds = article.getCustAttrs().get(cid).getCustTopicIds();
				custAttrCustTopicIds = (custAttrCustTopicIds == null ? new HashSet<Integer>() : custAttrCustTopicIds);
				
				for (Integer custTopicId : custAttrCustTopicIds) {
					if (flag) {
						/**统计包括分享的专题*/
						if (custTopicIds.contains(custTopicId.toString())) {
							custTopicIdList.add(custTopicId);
							custAttr.add(custTopicId);
						}
					}
				}
				article.getCustAttrs().get(cid).setCustTopicIds(custAttr);
			}
			
			if(StringUtils.isBlank(keyword)){//专题关键词高亮显示

				StringBuffer buffer = new StringBuffer();
				if(CollectionUtils.isNotEmpty(custTopicIdList)){
					List<CustTopicKeywordBean> custTopicKeywordBeans = custTopicKeywordService.listCustTopicKeywordByCustTopicId(custTopicIdList);
					if(CollectionUtils.isNotEmpty(custTopicKeywordBeans)){
						for(CustTopicKeywordBean custTopicKeywordBean: custTopicKeywordBeans){
							System.out.println("专题关键词="+custTopicKeywordBean.getWord()+"\n");
							buffer.append(custTopicKeywordBean.getWord()+",");
						}
					}
				}
				KeywordQueryBean keywordQueryBean = new KeywordQueryBean ();
				keywordQueryBean.setCustIds(new Integer[]{FMContext.getCurrent().getCustomerId()});
				PageInfoBean<KeywordBean> pageInfoBean = keywordService.queryKeywordByPage(keywordQueryBean);
				if(pageInfoBean != null && CollectionUtils.isNotEmpty(pageInfoBean.getData())){
					for(KeywordBean keywordBean: pageInfoBean.getData()){
						buffer.append(keywordBean.getWord()+",");
					}
				}
				if(buffer.length() > 0){
					buffer.deleteCharAt(buffer.length() -1);
					keyword = buffer.toString();
				}
			}
			System.out.println("keyword="+keyword);
			ArticleVoInter articleVo = new ArticleVo(baseSupport, keyword);
			CyyunBeanUtils.copyProperties(articleVo, article);
			return articleVo;
		}

	@Override
	public Map<String, Object> queryArticle(String guid, String keyword,BaseSupport baseSupport, HttpServletRequest request)
			throws Exception {
		
		return null;
	}
}
