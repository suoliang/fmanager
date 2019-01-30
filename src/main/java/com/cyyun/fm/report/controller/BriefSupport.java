package com.cyyun.fm.report.controller;

import java.io.File;
import java.io.FileReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.rtf.ITemplateEngine;
import net.sourceforge.rtf.context.DigesterRTFContextFields;
import net.sourceforge.rtf.context.RTFContextFieldsReader;
import net.sourceforge.rtf.context.fields.RTFContextBookmark;
import net.sourceforge.rtf.context.fields.RTFContextField;
import net.sourceforge.rtf.context.fields.RTFContextFields;
import net.sourceforge.rtf.helper.RTFTemplateBuilder;
import net.sourceforge.rtf.template.IContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.query.ArticleQueryBean;
import com.cyyun.base.util.CyyunCheckParams;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.base.controller.BaseSupport;
import com.cyyun.fm.constant.ReportType;
import com.cyyun.fm.report.templete.TemplateUtil;
import com.cyyun.fm.service.ArticleQueryService;
import com.cyyun.fm.service.FmReportService;
import com.cyyun.fm.service.bean.ReportBean;
import com.cyyun.fm.service.bean.ReportQueryBean;
import com.cyyun.fm.service.exception.FmReportException;
import com.cyyun.fm.vo.ArticleVoInter;

@Component
public class BriefSupport extends BaseSupport{

	@Autowired
	private FmReportService fmReportService;

	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private CustomerConfigService customerConfigService;
	
	@Resource(name = "ArticleQueryServiceImpl")
	private ArticleQueryService articleQueryService;

	@Resource(name = "FmSupport")
	private BaseSupport fmSupport;
	
	public PageInfoBean<ReportBean> queryBriefByPage(ReportBean reportBean, Integer pageNo, Integer pageSize) throws FmReportException {
		ReportQueryBean query = new ReportQueryBean();
		query.setType(ReportType.BRIEF);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setPageNo(pageNo);
		query.setPageSize(pageSize);
		query.setOrderBy(null);
		if (reportBean != null) {
			query.setTitle(CyyunSqlUtil.dealSql(reportBean.getTitle()));
		}
		PageInfoBean<ReportBean> pageInfo = fmReportService.queryReportByPage(query);
		return pageInfo;
	}

	public List<ReportBean> listBrief(ReportBean report) throws FmReportException {
		if (report == null) {
			report = new ReportBean();
		}
		report.setType(ReportType.BRIEF);
		report.setCustId(FMContext.getCurrent().getCustomerId());
		return fmReportService.listReport(report);
	}

	public void saveBrief(Integer reportId, String title) throws FmReportException {
		ReportBean reportBean = new ReportBean();
		reportBean.setType(ReportType.BRIEF);
		reportBean.setCustId(FMContext.getCurrent().getCustomerId());
		reportBean.setCreaterId(FMContext.getCurrent().getUserId());

		reportBean.setId(reportId);
		reportBean.setTitle(title);

		fmReportService.saveReport(reportBean);
	}

	public void deleteBrief(Integer reportId) throws FmReportException {
		fmReportService.deleteReport(reportId);
	}

	public ReportBean findBrief(Integer reportId) throws FmReportException {
		return fmReportService.findReport(reportId);
	}

	public List<ArticleBean> exportBrief(ReportBean report) throws Exception {
		List<ArticleBean> result = ListBuilder.newArrayList();
		if(report.getArticleIds()!=null){
			String[] articleIds = report.getArticleIds();
			List<ArticleBean> listArticle = articleService.listArticle(articleIds);
			
			for (ArticleBean articleBean : listArticle) {
				String content = TemplateUtil.getValueAfterRepalceTNR(TemplateUtil.getValueAfterRepalceSpecialWord(articleBean.getContent()));
				articleBean.setContent(content);
				result.add(articleBean);
			}
			
			/**按照发文时间降序排序*/
			Collections.sort(result, new ListArticleSort());
		}
		
		return result;
	}
	
	/**
	 * ArticleIds属性按发文时间降序排列
	 * @param report
	 * @return
	 * @throws Exception
	 */
	public ReportBean guidOrder(ReportBean report) throws Exception {
		ArticleQueryBean articleQueryBean = new ArticleQueryBean();
		articleQueryBean.setGuid(StringUtils.join(report.getArticleIds(), ","));
		articleQueryBean.setOrder("1");
		articleQueryBean.setArticleOrderBy("0");
		Integer customerId = FMContext.getCurrent().getCustomerId();
		articleQueryBean.setCustStage(getArticleStage(customerId));
		articleQueryBean.setPageSize(report.getArticleIds() != null ? report.getArticleIds().length : 0);
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(customerId);
		// FilterSimilar为false时查询到的文章带有相似文，为true则没有
		if (custConfig.getFilterSimilar()) {
			articleQueryBean.setSimilar("0");//值为"0"过滤相似文
		}

		// FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
		if (!custConfig.getFilterTrash()) {
			articleQueryBean.setCategory("0");
		}
		CyyunCheckParams.checkParams(articleQueryBean);
		List<ArticleBean> articleBeanList = new ArrayList<ArticleBean>();
		if(articleQueryBean.getGuid()!=null){
			articleBeanList = articleService.queryArticleByPage(articleQueryBean).getData();
		}else{
			return null;
		}
		String[] guids = new String[articleBeanList.size()];
		if (articleBeanList.size() > 0) {
			for (int i = 0; i < articleBeanList.size(); i++) {
				guids[i] = articleBeanList.get(i).getGuid();
			}
		}
		report.setArticleIds(guids);
		return report;
	}

	public PageInfoBean<ArticleBean> queryBriefArticleByPage(ReportBean report, Integer pageNo, Integer pageSize, HttpServletRequest request) throws Exception {

		if(report.getArticleIds()!=null){

			String[] articleIds = report.getArticleIds();
			
			List<ArticleBean> listArticle = articleService.listArticle(articleIds);
			
			/**按照发文时间降序排序*/
			Collections.sort(listArticle, new ListArticleSort());
			Integer start = ((pageNo - 1) * pageSize); 
			Integer end = pageNo * pageSize;
			if (end >= articleIds.length) {
				end = articleIds.length;
			}
			List<ArticleBean> subList = listArticle.subList(start, end);
			
			HttpSession session = request.getSession();
			List<String> custTopicIds = (List<String>) session.getAttribute("CUSTTOPICIDS_WITHSHARE");
			boolean flag = CollectionUtils.isNotEmpty(custTopicIds);
			Integer customerId = FMContext.getCurrent().getCustomerId();
			for (ArticleBean articleBean : subList) {
				Set<Integer> custAttr = new HashSet<Integer>();
				if (articleBean.getCustAttrs().get(customerId.toString()) != null) {
					Set<Integer> custAttrCustTopicIds = articleBean.getCustAttrs().get(customerId.toString()).getCustTopicIds();
					for (Integer custTopicId : custAttrCustTopicIds) {
						if (flag) {
							/**统计包括分享的专题*/
							if (custTopicIds.contains(custTopicId.toString())) {
								custAttr.add(custTopicId);
							}
						}
					}
					articleBean.getCustAttrs().get(customerId.toString()).setCustTopicIds(custAttr);
				}
			}
			
			PageInfoBean<ArticleBean> pageInfoBean = new PageInfoBean<ArticleBean>(subList, articleIds.length, pageSize, pageNo); 
			
			return pageInfoBean;
		}else{
			return new PageInfoBean<ArticleBean>();
		}
	}

	class ListArticleSort implements Comparator {

		public int compare(Object arg0, Object arg1) {
			ArticleBean bean0 = (ArticleBean) arg0;
			ArticleBean bean1 = (ArticleBean) arg1;

			return bean1.getPostTime().compareTo(bean0.getPostTime());
		}

	}
	
	public void deleteBriefArticle(Integer reportId, String guid) throws FmReportException {
		fmReportService.deleteReportArticle(reportId, guid);
	}

	public void addBriefArticle(Integer[] reportIds, String guid) throws FmReportException {
		String[] guids = new String[] { guid };
		fmReportService.addReportArticle(reportIds, guids);
	}

	public ArticleVoInter findArticle(String guid, String keyword, HttpServletRequest request) throws Exception {
		return articleQueryService.findArticle(guid, keyword, StringUtils.isNotBlank(keyword)?this:fmSupport, request);
	}
	
	
	//废弃方法
	public void exportBrief2(ReportBean report, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(report.getTitle(), "utf-8") + ".rtf");
		response.setCharacterEncoding("utf-8");

		RTFTemplateBuilder builder = RTFTemplateBuilder.newRTFTemplateBuilder();
		ITemplateEngine templateEngine = builder.newTemplateEngine("vm");
		templateEngine.setTemplate(new FileReader(ResourceUtils.getFile("classpath:template/report/brief-article.rtf")));
		IContext iContext = templateEngine.initializeContext();

		RTFContextFieldsReader reader = new RTFContextFieldsReader();
		reader.readContext(templateEngine.getContext());

		Map oldContextBookmarksMap;
		Iterator iter;
		RTFContextFields newFields = reader.getContextFields();
		File inFile = ResourceUtils.getFile("classpath:template/report/brief-article.fields.xml");
		if (inFile.exists()) {
			RTFContextFields oldFields = DigesterRTFContextFields.getRTFContextFields(inFile);

			newFields.setDescription(oldFields.getDescription());

			Map oldContextFieldsMap = oldFields.getMergeFieldsMap();

			for (Iterator iter2 = newFields.getMergeFields().iterator(); iter2.hasNext();) {
				RTFContextField newField = (RTFContextField) iter2.next();

				RTFContextField oldField = (RTFContextField) oldContextFieldsMap.get(newField.getName());
				if (oldField != null) {
					newField.setDescription(oldField.getDescription());
				}
			}

			oldContextBookmarksMap = oldFields.getBookmarksMap();

			for (iter = newFields.getBookmarks().iterator(); iter.hasNext();) {
				RTFContextBookmark newBookmark = (RTFContextBookmark) iter.next();

				RTFContextBookmark oldBookmark = (RTFContextBookmark) oldContextBookmarksMap.get(newBookmark.getType());
				if (oldBookmark != null) {
					newBookmark.setDescription(oldBookmark.getDescription());
				}
			}
		}
		newFields.toXml(inFile);

		List<ArticleBean> articles = articleService.listArticle(report.getArticleIds());
		iContext.put("articles", articles);
		templateEngine.merge(response.getWriter());
	}
}