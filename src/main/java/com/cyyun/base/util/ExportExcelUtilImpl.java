package com.cyyun.base.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.common.core.util.SpringContextUtil;
import com.cyyun.fm.service.CountArticleNumService;
import com.cyyun.fm.vo.ArticleNumVo;

/**
 * 导出Excel工具类实现
 * @author LIUJUNWU
 *
 */
@Component("ExportExcelUtil")
public class ExportExcelUtilImpl implements ExportExcelUtil {

	public static final String EXPORTDOC_PATH = PropertiesUtil.getValue("exportDocpath");
	
	Logger log = LoggerFactory.getLogger(ExportExcelUtil.class);
	
	@Autowired
	private CountArticleNumService countArticleNumService;
	
	@Autowired
	private ArticleService articleService;

	@Override
	public void exportExcel(List<ArticleBean> articles, HttpServletRequest request) throws Exception {
		Map<String, Object> beans = new HashMap<String, Object>();
		SimpleDateFormat dateFormat = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern);
		ArticleNumVo articleNumVo = countArticleNumService.getArticleCountMap(articles);
		//媒体类型为微博的文章，内容赋值为真正的内容（其它媒体类型内容字段为摘要）
		articles = weiboSpecialHandling(articleNumVo);

		//文档基础信息
		beans.put("articleStartTime", CyyunDateUtils.formatDate(request.getAttribute("articleStartTime").toString(), "yyyy年MM月dd日"));
		beans.put("articleEndTime", CyyunDateUtils.formatDate(request.getAttribute("articleEndTime").toString(), "yyyy年MM月dd日"));
		beans.put("cName", FMContext.getCurrent().getCustomer().getName());
		//文档数据信息
		beans.put("countStyleArticleMap", articleNumVo.getCountStyleArticleMap());
		beans.put("countSentimentArticleMap", articleNumVo.getCountSentimentArticleMap());
		beans.put("countTopicArticleMap", articleNumVo.getCountTopicArticleMap());
		beans.put("articles", articles);
		beans.put("articlesNum", articles.size());
		beans.put("dateFormat", dateFormat);
		beans.put("tag", SpringContextUtil.getBean(TagUtil.class));
		beans.put("const", SpringContextUtil.getBean(ConstUtil.class));

		String path = request.getSession().getServletContext().getRealPath("/");
		String targetPath = EXPORTDOC_PATH + "/";
//		String targetPath = "E:/workspace-fm25/";
		String fileName = request.getSession().getAttribute("fileName").toString();
		String reportFile = targetPath + fileName;
		String templateFile = path + PropertiesUtil.getValue("exceltemplatepath");
		XLSTransformer transformer = new XLSTransformer();
		transformer.markAsFixedSizeCollection("articles");//对应模板所循环的集合名字
		try {
			transformer.transformXLS(templateFile, beans, reportFile);
		} catch (ParsePropertyException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 媒体类型为微博的文章，内容赋值为真正的内容（其它媒体类型内容字段为摘要）
	 * @param articleNumVo
	 * @return
	 */
	private List<ArticleBean> weiboSpecialHandling(ArticleNumVo articleNumVo) {
		List<ArticleBean> articles = new ArrayList<ArticleBean>();
		List<ArticleBean> articleDetails = new ArrayList<ArticleBean>();
		LinkedHashMap<String, ArticleBean> articleBeanMap = articleNumVo.getArticleBeanMap();
		List<String> weiboGuidList = articleNumVo.getWeiboGuidList();
		if (weiboGuidList.size() == 0) {
			articles.addAll(articleBeanMap.values());
			return articles;
		}
		//分成容量为50的n个数组
		List<String[]> weiboGuidArrList = listToArray(weiboGuidList, 50);
		for (String[] weiboGuidArr : weiboGuidArrList) {
			try {
				articleDetails = articleService.listArticle(weiboGuidArr);
			} catch (ArticleServiceException e) {
				log.error("listArticle调用异常!!GuidArr=" + weiboGuidArr);
				e.printStackTrace();
			}
			for (ArticleBean articleBean : articleDetails) {
				//给Content赋值 
				articleBeanMap.get(articleBean.getGuid()).setContent(articleBean.getContent());
			}
		}
		articles.addAll(articleBeanMap.values());
		return articles;
	}
	
	/**
	 * List集合分成一定容量的n个数组
	 * @param list
	 * @param bccSize  初始化数组大小
	 * @return
	 */
	public List<String[]> listToArray(List<String> list, int bccSize) {
		List<String[]> resultList = new ArrayList<String[]>();
		String arr[] = null;
		
		for (int j = 1, len = list.size() + 1; j < len; j++) {
			if (j == 1) {
				if (len > bccSize) {
					arr = new String[bccSize];
				} else {
					arr = new String[len - 1];
				}
			}
			arr[j - (bccSize * resultList.size()) - 1] = list.get(j - 1); //给数组赋值  
			if (j % bccSize == 0) {
				resultList.add(arr); //数组填值满后放到集合中  
				if (len - j - 1 > bccSize) {
					arr = new String[bccSize];
					//不允许数组有空值创建最后一个数组的大小(如果都要一定大小可以去掉)  
				} else {
					arr = new String[len - (bccSize * resultList.size()) - 1];
				}
			} else if (j == len - 1) {
				resultList.add(arr);//最后一个数组不设置大小  
			}
		}
		return resultList;
	}
}