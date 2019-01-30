package com.cyyun.fm.recommend.controller;

import static com.cyyun.fm.constant.PostTimeType.LAST_MONTH;
import static com.cyyun.fm.constant.PostTimeType.LAST_WEEK;
import static com.cyyun.fm.constant.PostTimeType.THIS_MONTH;
import static com.cyyun.fm.constant.PostTimeType.THIS_WEEK;
import static com.cyyun.fm.constant.PostTimeType.THIS_YEAR;
import static com.cyyun.fm.constant.PostTimeType.TODAY;
import static com.cyyun.fm.constant.PostTimeType.YESTERDAY;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.cyyun.base.filter.FMContext;
import com.cyyun.common.core.base.BaseException;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.fm.base.controller.BaseSupport;
import com.cyyun.fm.recommend.bean.RecommendParam;
import com.cyyun.fm.service.CustDataCategoryService;
import com.cyyun.fm.service.bean.CustDataCategoryBean;
import com.cyyun.fm.service.bean.CustDataCategoryQueryBean;
import com.cyyun.fm.service.exception.CustDataCategoryServiceException;
import com.cyyun.process.service.RecommendColumnService;
import com.cyyun.process.service.RecommendContentService;
import com.cyyun.process.service.bean.RecommendColumnBean;
import com.cyyun.process.service.bean.RecommendColumnQueryBean;
import com.cyyun.process.service.bean.RecommendContentBean;
import com.cyyun.process.service.bean.RecommendContentQueryBean;
import com.cyyun.process.service.exception.RecommendColumnServiceException;



@Component("RecommendSupport")
public class RecommendSupport extends BaseSupport {
	Logger log = LoggerFactory.getLogger(RecommendSupport.class);

	private static final Map<String, Integer> TIME = MapBuilder.put(TODAY, 1).and(YESTERDAY, 2).and(THIS_WEEK, 3).and(LAST_WEEK, 4).and(THIS_MONTH, 5).and(LAST_MONTH, 6).and(THIS_YEAR, 7).build();
	@Autowired
	private  RecommendColumnService recommendColumnService;
	
	@Autowired
	private RecommendContentService recommendContentService;
	
	@Autowired
	private CustDataCategoryService custDataCategoryService;

	public PageInfoBean<RecommendColumnBean> queryRecommendColumnListByPage(RecommendColumnQueryBean query)
			throws RecommendColumnServiceException{
		return recommendColumnService.queryRecommendColumnListByPage(query);
		 
	}
	
	/**
	 * <h3>通过条件获取文章</h3>
	 * 
	 * @param recommend 条件
	 * @param currentpage 当前页
	 * @param pagesize 每页大小
	 * @return 文章(分页)
	 * @throws Exception 
	 */
	private PageInfoBean<RecommendContentBean> queryContentByPage(RecommendContentQueryBean recommend, Integer currentpage, Integer pagesize) throws Exception {
		recommend.setPageNo(currentpage);
		recommend.setPageSize(pagesize);
		/*log.info("===recommend==="+recommend.toString());*/
		
		PageInfoBean<RecommendContentBean> pageInfoBean = recommendContentService.queryContentListPage(recommend);
		
		List<RecommendContentBean> data = pageInfoBean.getData();
		if (CollectionUtils.isEmpty(data)) {
			return pageInfoBean;
		}
		for (RecommendContentBean recommendContentBean : data) {
			recommendContentBean.setContentWeb(removeHtmlTag(recommendContentBean.getContentWeb()));
		}
		
		return pageInfoBean;
	}
	
	public String removeHtmlTag(String htmlStr) {
		if (StringUtils.isBlank(htmlStr)) {
			return StringUtils.EMPTY;
		}
		String regExScript = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // script
		String regExStyle = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // style
		String regExHtml = "<[^>]+>"; // HTML tag
		String regExSpace = "\\s+|\t|\r|\n";// other characters

		Pattern p_script = Pattern.compile(regExScript,Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll("");
		Pattern p_style = Pattern.compile(regExStyle, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll("");
		Pattern p_html = Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll("");
		Pattern p_space = Pattern.compile(regExSpace, Pattern.CASE_INSENSITIVE);
		Matcher m_space = p_space.matcher(htmlStr);
		htmlStr = m_space.replaceAll(" ");
		return htmlStr;
	}
		
	public PageInfoBean<RecommendContentBean> queryContentListPage(RecommendParam recommendParam) throws Exception{
		RecommendContentQueryBean recommend = convertParamToCondition(recommendParam);
		recommend.setColumnIds(recommendParam.getColumnIds());
		recommend.setType(recommendParam.getType());
		setDateForQueryRecommend(recommend, recommendParam);
		return	queryContentByPage(recommend,recommendParam.getCurrentpage(),recommendParam.getPagesize());
	}
	
	public RecommendContentBean queryRecommendContentById(Integer id){
		return recommendContentService.queryRecommendContentById(id);
		
	}
	
	/**
	 * <h3>转换参数为条件</h3>
	 * 
	 * @param 参数
	 * @return 条件
	 */
	public RecommendContentQueryBean convertParamToCondition(RecommendParam recommendParam) {
		RecommendContentQueryBean recommendbean = new RecommendContentQueryBean();
		recommendbean.setUserIds(recommendParam.getUserIds());
		recommendbean.setCustIds(recommendParam.getCustIds());
		recommendbean.setKeyWord(recommendParam.getKeyWord());
		recommendbean.setCreateTimeStart(recommendParam.getCreateTimeEnd());
		recommendbean.setCreateTimeEnd(recommendParam.getCreateTimeEnd());
		return recommendbean;
	}
	
	//时间
	private void setDateForQueryRecommend(RecommendContentQueryBean query,RecommendParam recommend) {
		String postStartTime = recommend.getCreateTimeStart();
		String postEndTime = recommend.getCreateTimeEnd();
		Integer timeType = recommend.getTimeType();
		String start = "";
		String end = "";
		String pattern = "yyyy-MM-dd";
		String pattern1 = "yyyy-MM-dd HH:mm:ss";
		String begine = " 00:00:00";
		String ending = " 23:59:59";
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		if (StringUtils.isNotBlank(postStartTime)
				|| StringUtils.isNotBlank(postEndTime)) {
			start = postStartTime+begine;
			end = postEndTime+ending;
		} else if (timeType != null) {
			if (TIME.get(TODAY).equals(timeType)) {// 当天
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
				end = DateFormatUtils.format(cal.getTime(), pattern1);
			} else if (TIME.get(YESTERDAY).equals(timeType)) {
				cal.add(Calendar.DATE, -1);// 昨天
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
			} else if (TIME.get(THIS_WEEK).equals(timeType)) {// 本周
				end = DateFormatUtils.format(cal.getTime(), pattern1);
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 本周一
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(LAST_WEEK).equals(timeType)) {// 上周
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 上周日
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.add(Calendar.WEEK_OF_MONTH, -1);// 上周
				cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 上周一
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(THIS_MONTH).equals(timeType)) {// 当月
				end = DateFormatUtils.format(cal.getTime(), pattern1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(LAST_MONTH).equals(timeType)) {// 上月
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				end = DateFormatUtils.format(cal.getTime(), pattern) + ending;
				cal.set(Calendar.DAY_OF_MONTH, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			} else if (TIME.get(THIS_YEAR).equals(timeType)) {// 本年
				end = DateFormatUtils.format(cal.getTime(), pattern1);
				cal.set(Calendar.DAY_OF_YEAR, 1);
				start = DateFormatUtils.format(cal.getTime(), pattern) + begine;
			}
		}
		query.setCreateTimeStart(start);
		query.setCreateTimeEnd(end);
	}
	
	public Map<String, Map<String, Integer>> getOptions() throws BaseException {
		return MapBuilder.put("timeType", TIME).and("siteCategorys", this.getsiteCategorys()).build();
	}

	private Map<String, Integer> getsiteCategorys() throws CustDataCategoryServiceException {
		List<CustDataCategoryBean> result = listSiteCustDataCategory();
		Map<String, Integer> type = MapBuilder.newHashMap();
		for (CustDataCategoryBean item : result) {
			type.put(item.getName(), item.getId());
		}
		return type;
	}
	
	public List<CustDataCategoryBean> listSiteCustDataCategory() throws CustDataCategoryServiceException {
		CustDataCategoryQueryBean query = new CustDataCategoryQueryBean();
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setType(3);
		return custDataCategoryService.queryCustDataCategoryBeanListByCondition(query);
	}
}