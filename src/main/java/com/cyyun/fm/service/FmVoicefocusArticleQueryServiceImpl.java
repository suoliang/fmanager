package com.cyyun.fm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.KeywordService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.KeywordBean;
import com.cyyun.base.service.bean.query.ArticleQueryBean;
import com.cyyun.base.service.bean.query.KeywordQueryBean;
import com.cyyun.base.util.CyyunBeanUtils;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.base.controller.BaseSupport;
import com.cyyun.fm.vo.ArticleVo;
import com.cyyun.fm.vo.ArticleVoInter;
import com.cyyun.process.service.FocusInfoService;
import com.cyyun.process.service.WarningService;
import com.cyyun.process.service.bean.FocusInfoBean;
import com.cyyun.process.service.bean.FocusInfoQueryBean;
import com.cyyun.process.service.bean.WarningKeywordBean;
import com.cyyun.process.service.bean.WarningRuleBean;

@Service("FmVoicefocusArticleQueryServiceImpl")
public class FmVoicefocusArticleQueryServiceImpl implements ArticleQueryService{

	@Autowired
	private FocusInfoService focusInfoService;
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private WarningService warningService;
	
	@Autowired
	private KeywordService keywordService;
	
	@Autowired
	private CustomerConfigService customerConfigService;
	
	@Override
	public PageInfoBean<ArticleVoInter> queryArticleByParam(
			Map<String, String[]> params, HttpServletRequest request) throws Exception {
		return null;
	}

	@Override
	public ArticleVoInter findArticle(String guid, String keyword,
			BaseSupport baseSupport, HttpServletRequest request) throws Exception {
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> queryArticle(String guid, String keyword,BaseSupport baseSupport, HttpServletRequest request)
			throws Exception {
		ArticleBean article = articleService.findArticleWithImagePositin(guid);
		
		Integer customerId = FMContext.getCurrent().getCustomerId();
		
		HttpSession session = request.getSession();
		List<String> custTopicIds = (List<String>) session.getAttribute("CUSTTOPICIDS_WITHSHARE");
		boolean flag = CollectionUtils.isNotEmpty(custTopicIds);
		
		Set<Integer> custAttr = new HashSet<Integer>();
		Set<Integer> custAttrCustTopicIds = null;
		if (article.getCustAttrs().get(customerId.toString()) != null) {
			custAttrCustTopicIds = article.getCustAttrs().get(customerId.toString()).getCustTopicIds();
			custAttrCustTopicIds = (custAttrCustTopicIds == null ? new HashSet<Integer>() : custAttrCustTopicIds);
			for (Integer custTopicId : custAttrCustTopicIds) {
				if (flag) {
					/**统计包括分享的专题*/
					if (custTopicIds.contains(custTopicId.toString())) {
						custAttr.add(custTopicId);
					}
				}
			}
			article.getCustAttrs().get(customerId.toString()).setCustTopicIds(custAttr);
		}
		
		ArticleQueryBean queryBean=new ArticleQueryBean();
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(customerId);
		Map<String,Object> resultMap = new HashMap<String, Object>();
		if(custConfig.getViewReplyData()){
			queryBean.setOrigGuid(guid);
			queryBean.setArticleOreply("1");
			queryBean.setWithDetail(true);//设置返回详情
			PageInfoBean<ArticleBean> articleBean=articleService.queryArticleByPage(queryBean);
			//---------------------
//			List<ArticleBean> aliList = new ArrayList<ArticleBean>();
//			ArticleBean a=new ArticleBean();
//			a.setAuthor("刘军武");
//			a.setContent("盗贼团哒哒哒");
//			aliList.add(a);
//			articleBean.setData(aliList);
			//---------------------
			resultMap.put("listReplys", articleBean.getData());
			
		}
		if(StringUtils.isBlank(keyword)){
			/**返回字段有rules_names的处理*/
			FocusInfoQueryBean query = new FocusInfoQueryBean();
			query.setUserId(FMContext.getCurrent().getUserId());// userId
			query.setArticleId(guid);//文章id查询出单条数据集合
			PageInfoBean<FocusInfoBean> queryRest = focusInfoService.queryFocusInfoByPage(query);
			if (CollectionUtils.isNotEmpty(queryRest.getData())) {
				resultMap.put("warningRuleName", StringUtils.join(queryRest.getData().get(0).getRuleNames(), ","));
				
				List<WarningRuleBean> warningRules = queryRest.getData().get(0).getWarningRules();
				
				StringBuffer keywordsSingle = new StringBuffer();
				List<String> warningRuleNameListSingle = new ArrayList<String>();
				if(CollectionUtils.isNotEmpty(warningRules)){
					for(WarningRuleBean warningRuleBean: warningRules){
						warningRuleNameListSingle.add(warningRuleBean.getName());
						System.out.println("预警规则名称="+warningRuleBean.getName()+"\n");
						if(CollectionUtils.isNotEmpty(warningRuleBean.getKeywords())){
							for(WarningKeywordBean warningKeywordBean: warningRuleBean.getKeywords()){
								System.out.println("预警关键词="+warningKeywordBean.getWord()+";");
								keywordsSingle.append(warningKeywordBean.getWord()+",");
							}
						}
					}
				}
				KeywordQueryBean keywordQueryBeanSingle = new KeywordQueryBean ();
				keywordQueryBeanSingle.setCustIds(new Integer[]{FMContext.getCurrent().getCustomerId()});
				PageInfoBean<KeywordBean> pageInfoBeanSingle = keywordService.queryKeywordByPage(keywordQueryBeanSingle);
				if(pageInfoBeanSingle != null && CollectionUtils.isNotEmpty(pageInfoBeanSingle.getData())){
					for(KeywordBean keywordBean: pageInfoBeanSingle.getData()){
						keywordsSingle.append(keywordBean.getWord()+",");
					}
				}
				if(keywordsSingle.length() > 0){
					keywordsSingle.deleteCharAt(keywordsSingle.length() -1);
					keyword = keywordsSingle.toString();
				}
				
			} else {/**原始处理方式*/
				Map<String,Object> paramMap = new HashMap<String,Object>();
				paramMap.put("articleId", guid);
				paramMap.put("custIds", new Integer[]{FMContext.getCurrent().getCustomerId()});
				Map<String,Object> articleRuleListMap = warningService.getArticleRulesNameByParam(paramMap);
				List<WarningRuleBean> warningRuleBeanList = (List<WarningRuleBean>)articleRuleListMap.get("warningRuleBeanList");
				StringBuffer keywordsBuffer = new StringBuffer();
				List<String> warningRuleNameList = new ArrayList<String>();
				if(CollectionUtils.isNotEmpty(warningRuleBeanList)){
					for(WarningRuleBean warningRuleBean: warningRuleBeanList){
						warningRuleNameList.add(warningRuleBean.getName());
						System.out.println("预警规则名称="+warningRuleBean.getName()+"\n");
						if(CollectionUtils.isNotEmpty(warningRuleBean.getKeywords())){
							for(WarningKeywordBean warningKeywordBean: warningRuleBean.getKeywords()){
								System.out.println("预警关键词="+warningKeywordBean.getWord()+";");
								keywordsBuffer.append(warningKeywordBean.getWord()+",");
							}
						}
					}
				}
				KeywordQueryBean keywordQueryBean = new KeywordQueryBean ();
				keywordQueryBean.setCustIds(new Integer[]{FMContext.getCurrent().getCustomerId()});
				PageInfoBean<KeywordBean> pageInfoBean = keywordService.queryKeywordByPage(keywordQueryBean);
				if(pageInfoBean != null && CollectionUtils.isNotEmpty(pageInfoBean.getData())){
					for(KeywordBean keywordBean: pageInfoBean.getData()){
						keywordsBuffer.append(keywordBean.getWord()+",");
					}
				}
				if(keywordsBuffer.length() > 0){
					keywordsBuffer.deleteCharAt(keywordsBuffer.length() -1);
					keyword = keywordsBuffer.toString();
				}
				if(warningRuleNameList.size() > 0){
					resultMap.put("warningRuleName", StringUtils.join(warningRuleNameList.toArray(), ","));
				}
			}
			
		}
		System.out.println("keyword="+keyword);
		ArticleVoInter articleVo = new ArticleVo(baseSupport, keyword);
		CyyunBeanUtils.copyProperties(articleVo, article);
		resultMap.put("article", articleVo);
		return resultMap;
	}

}
