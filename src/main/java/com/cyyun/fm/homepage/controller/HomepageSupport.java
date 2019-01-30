package com.cyyun.fm.homepage.controller;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.ArticleStatisticService;
import com.cyyun.base.service.ConstantService;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.query.ArticleQueryBean;
import com.cyyun.base.service.bean.query.ArticleStatisticQueryBean;
import com.cyyun.base.service.bean.query.CustTopicQueryBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.base.service.exception.CustTopicServiceException;
import com.cyyun.base.util.CyyunCheckParams;
import com.cyyun.base.util.PropertiesUtil;
import com.cyyun.common.algorithm.Util;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.JsonUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.common.core.util.MapBuilder;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.base.controller.BaseSupport;
import com.cyyun.fm.constant.BoardType;
import com.cyyun.fm.homepage.constant.ModuleName;
import com.cyyun.fm.service.AnalyzeSituationService;
import com.cyyun.fm.service.ArticleQueryService;
import com.cyyun.fm.service.FmCustTopicService;
import com.cyyun.fm.service.FmCustomerConfigService;
import com.cyyun.fm.service.FmSearchService;
import com.cyyun.fm.service.WebpageBoardService;
import com.cyyun.fm.service.bean.BoardBean;
import com.cyyun.fm.service.bean.ModuleBean;
import com.cyyun.fm.service.bean.QueryConditionBean;
import com.cyyun.fm.service.exception.FmSearchException;
import com.cyyun.fm.service.exception.WebpageBoardException;
import com.cyyun.fm.vo.ArticleVoInter;
import com.cyyun.keyword.KeywordExtractor;
import com.cyyun.process.service.FocusInfoService;

/**
 * <h3>首页控制器支持</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Component
public class HomepageSupport extends BaseSupport {

	@Autowired
	private FocusInfoService focusInfoService;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private FmSearchService searchService;

	@Autowired
	private WebpageBoardService boardService;
	
	@Autowired
	private ConstantService constantService;
	
	@Autowired
	private ArticleStatisticService articleStatisticService;
	
	@Autowired
	private CustTopicService custTopicService;
	
	@Resource(name = "ArticleQueryServiceImpl")
	private ArticleQueryService articleQueryService;
	
	@Autowired
	private AnalyzeSituationService analyzeSituationService;
	
	@Autowired
	private FmCustTopicService fmCustTopicService;
	
	@Autowired
	private FmCustomerConfigService fmCustomerConfigService;

	public List<BoardBean> listUserBoard(Integer userId) throws WebpageBoardException {
		boardService.checkUserBoard(userId, BoardType.HOMEPAGE);//确保用户配置存在
		return boardService.listUserBoard(userId, BoardType.HOMEPAGE);
	}

	public List<BoardBean> listHideDefBoard(Integer userId) throws WebpageBoardException {//为了配置板块
		List<BoardBean> result = ListBuilder.newArrayList();
		List<Integer> defBoardIds = ListBuilder.newArrayList();
		List<Integer> existent = ListBuilder.newArrayList();

		List<BoardBean> defBoards = boardService.listDefBoard(BoardType.HOMEPAGE);
		for (BoardBean board : defBoards) {
			defBoardIds.add(board.getId());
		}
		for (BoardBean board : boardService.listUserBoard(userId, BoardType.HOMEPAGE)) {
			if (board.getIsDefault() && defBoardIds.contains(board.getId())) {//查找用户板块中所有已显示的系统板块
				existent.add(board.getId());
			}
		}
		for (BoardBean board : defBoards) {
			if (!existent.contains(board.getId())) {
				result.add(board);
			}
		}
		return result;
	}

	public List<ModuleBean> listModule() throws WebpageBoardException {
		return boardService.listModule();
	}

	public BoardBean findBoard(Integer boardId) throws WebpageBoardException {
		return boardService.findBoard(boardId);
	}

	public void setInputParam(List<ModuleBean> modules) throws WebpageBoardException {
		List<BoardBean> boards = boardService.listUserBoard(FMContext.getCurrent().getUserId(), BoardType.HOMEPAGE);
		for (ModuleBean module : modules) {
			List<Map<String, String>> params = ListBuilder.newArrayList();
			for (BoardBean b : boards) {
				if (!b.getModuleId().equals(10)) {//如用户已在首页系统tab中添加了“词云”板块，则把词云板块信息页面，作为之后判断用户添加了“词云”的依据
					if (!module.getId().equals(b.getModuleId())) {
						continue;
					}
				}
				Map<String, String> param = MapBuilder.newHashMap();
				for (String item : b.getParams().split("&")) {
					if(item.split("=").length > 1){
						param.put(item.split("=")[0], item.split("=")[1]);
					}
				}
				params.add(param);
			}
			String userConfigInfo = "userConfigInfo=" + JsonUtil.toJsonWithClassName(params);
			module.setRemark(userConfigInfo);
		}
	}

	public BoardBean saveBoard(BoardBean board, Integer userId) throws WebpageBoardException {
		if (board.getId() == null) {//添加板块
			if (boardService.findModuleByName(ModuleName.DEFAULT).getId().equals(board.getModuleId())) {//添加系统默认板块时，display=true
				for (String item : board.getParams().split("&")) {
					if (item.contains("def")) {
						Integer boardId = Integer.valueOf(item.split("=")[1]);

						// FIXME 首页 - 目前不支持显示隐藏
						boardService.updateUserBoardDisplay(true, boardId, userId, BoardType.HOMEPAGE);
						boardService.moveUserBoardToLast(userId, boardId, BoardType.HOMEPAGE);
						
						board = boardService.findBoard(boardId);
					}
				}
				//因首页词云、来源分布、媒体趋势默认板块是系统上线一段时间后才加上的，考虑到已有帐号没有这些默认板块的配置，所以才有以下判断
				/*	板块名        	板块id
				 *  词云			4
				 *  来源分布		6
				 *  媒体趋势		7			
				 */
				if (board.getId().equals(4) || board.getId().equals(6) || board.getId().equals(7)) { 
					boardService.addSystemBoardConfig(userId, board.getId(), BoardType.HOMEPAGE, true);
				}
			} else {//添加新板块
				board.setCreaterId(userId);
				board.setNeedShow(true);
				board = boardService.addBoard(board, userId, BoardType.HOMEPAGE);
			}
		} else {//更新板块
			board = boardService.updateBoard(board);
		}
		return board;
	}

	public void deleteBoard(Integer boardId, Integer userId, Integer type) throws WebpageBoardException {
		BoardBean board = boardService.findBoard(boardId);
		if (board == null) //已经被删除
			return;

		if (board.getIsDefault()) {//删除系统默认板块时，display=false FIXME 首页 - 目前不支持显示隐藏
			boardService.updateUserBoardDisplay(false, boardId, userId, BoardType.HOMEPAGE);

		} else if (!board.getCreaterId().equals(userId)) {//删除板块配置信息
			boardService.deleteUserBoard(userId, boardId);

		} else {//删除板块
			boardService.deleteBoard(boardId);
		}
	}

	public void updateBoardIndex(Integer index, Integer boardId, int userId) throws WebpageBoardException {
		boardService.updateUserBoardIndex(index, boardId, userId, BoardType.HOMEPAGE);
	}

	public ModuleBean findModule(Integer id) throws WebpageBoardException {
		return boardService.findModule(id);
	}

	/*public Map<String, Integer> getSituation() throws ArticleServiceException {
		ArticleQueryBean queryBean = new ArticleQueryBean();
		String today = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		queryBean.setActionType(ActionType.DAY);
		//		queryBean.setStage(ArticleConstants.ArticleStage.PUTONG);
		queryBean.setCustStage(getArticleStage(FMContext.getCurrent().getCustomerId()));
		queryBean.setArticleOreply("2");
		queryBean.setPostStartTime(today);
		queryBean.setPostEndTime(today);
		queryBean.setCustIds("" + FMContext.getCurrent().getCustomerId());
		Map<String, Integer> countArticle = articleService.countArticleStyle(queryBean);
		Map<String, Integer> sortCountArticle = MapBuilder.newLinkedHashMap();

		for (Entry<String, Integer> num : countArticle.entrySet()) {
			String maxKey = "";
			int maxValue = 0;
			for (Entry<String, Integer> countItem : countArticle.entrySet()) {
				if (sortCountArticle.keySet().contains(countItem.getKey())) {
					continue;
				}
				if (countItem.getValue() >= maxValue) {
					maxKey = countItem.getKey();
					maxValue = countItem.getValue();
				}
			}
			if (StringUtils.isNoneBlank(maxKey)) {
				sortCountArticle.put(maxKey, maxValue);
			}
		}

		return sortCountArticle;
	}*/
	
	public Map<String, Long> getSituation() throws Exception {
		String today = DateFormatUtils.format(new Date(), FMConstant.DATE_FOMAT_YYYYMMDD);
		CustConfigBean custConfig = fmCustomerConfigService.getCustConfig();
		custConfig.setFilterSimilar(false);//设置不过滤相似文
		Map<String, Long> sortCountArticle = analyzeSituationService.analyzeSituation(today, today, null, custConfig);
		long total = 0L;
		for (Iterator<Entry<String, Long>> iterator = sortCountArticle.entrySet().iterator(); iterator.hasNext();) {
			total = MathUtils.addAndCheck(total, iterator.next().getValue());
		}
		sortCountArticle.put("total", total);
		return sortCountArticle;
	}

	
	public Integer getAlert() throws ParseException {
		String todayStr = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		String tomorrowStr = DateFormatUtils.format(DateUtils.addDays(new Date(), 1), "yyyy-MM-dd");
		Date today = DateUtils.parseDate(todayStr + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
		Date tomorrow = DateUtils.parseDate(tomorrowStr + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
		return focusInfoService.findWarningNumber(FMContext.getCurrent().getUserId(), today, tomorrow, null, null);
	}


	public Integer getArchive() throws ArticleServiceException {
		int count=0;
		try {
			String today = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
			ArticleStatisticQueryBean queryBean = new ArticleStatisticQueryBean();
			queryBean.setTimePostFrom(DateUtils.parseDate(today+" 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime());
			queryBean.setTimePostTo(DateUtils.parseDate(today+" 23:59:59", "yyyy-MM-dd HH:mm:ss").getTime());
			queryBean.setCustIds("" + FMContext.getCurrent().getCustomerId());
			CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
			//FilterSimilar为false时查询到的文章带有相似文，为true则没有
			if(custConfig.getFilterSimilar()){
				queryBean.setSimilar("0");//值为"0"过滤相似文
			}
			
			if (custConfig.getFilterTrash()) {
				//FilterTrash为flase时查询到的文章带有垃圾文，为true则没有
				count = articleStatisticService.countArticleInConditions(queryBean);
			}else{
				queryBean.setCategory("0");
				count = articleStatisticService.countArticleInConditions(queryBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 查询所有客户专题(自身创建的及共享的)
	 * @return
	 * @throws CustTopicServiceException 
	 */
	public List<CustTopicBean> queryCustTopicList() throws Exception {
		CustTopicQueryBean custTopicQueryBean = new CustTopicQueryBean();
		custTopicQueryBean.setCustId(FMContext.getCurrent().getCustomerId());
		custTopicQueryBean.setCreaterId(FMContext.getCurrent().getUserId());
		custTopicQueryBean.setStatus(FMConstant.CUST_TOPIC_STATUS.yes.getStatus());
		custTopicQueryBean.setNeedPaging(false);
		String custTopicIds = getAllCustTopicId();
		Integer[] topicId = stringToIntegerArray(custTopicIds);
		topicId = ArrayUtils.isNotEmpty(topicId) ? topicId : null;
		custTopicQueryBean.setDataIds(topicId);
		PageInfoBean<CustTopicBean> data = custTopicService.queryCustTopicsWithShareByPage(custTopicQueryBean);
		return data.getData();
		//return fmCustTopicService.queryCurrentCustAllTopics();
	}
	
	public Integer[] stringToIntegerArray(String custTopics) {
		String[] strings = custTopics.toString().split("\\,");
		if (strings.length <= 1) {
			return ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
		}
		Integer array[] = new Integer[strings.length];
		for (int i = 0; i < strings.length; i++) {
			array[i] = Integer.parseInt(strings[i]);
		}
		return array;
	}
	
	public String getAllCustTopicId() throws CustTopicServiceException {

		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setParentId(0);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setStatus(1);
		query.setCreaterId(FMContext.getCurrent().getUserId());
		List<CustTopicBean> topics = custTopicService.listCustTopicWithShare(query);
		StringBuffer custTopics = new StringBuffer();

		getCustTopicIds(topics, custTopics);

		if (StringUtils.isNotBlank(custTopics)) {
			return custTopics.substring(0, custTopics.length() - 1);
		}
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

	/**
	 * 查询客户专题文章
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public List<ArticleVoInter> queryCustTopicArticles(Map<String, String[]> requestParams, HttpServletRequest request) throws Exception {
		PageInfoBean<ArticleVoInter> pageInfoBean = articleQueryService.queryArticleByParam(requestParams, request);
		CyyunCheckParams.setObjectFieldsEmpty(pageInfoBean.getData(), "guid","title","websiteName","postTime","guidGroup");
		return pageInfoBean.getData();
	}
	
	public List<Entry<String, Double>> getHotKeyWordsDatas(HttpSession session) throws ArticleServiceException {
		String hotKeyWordSource = PropertiesUtil.getValue("hotKeyWords.sogou.word");//热词来源配置
		String hotKeyWordEliminate = PropertiesUtil.getValue("hotKeyWords.stopwords");//剔除热词配置
		String path = session.getServletContext().getRealPath("/");
		KeywordExtractor ke = new KeywordExtractor(path + hotKeyWordSource, path + hotKeyWordEliminate);
		List<Entry<String, Double>> res = new ArrayList<Map.Entry<String, Double>>();

		ArrayList<String> sentences = new ArrayList<String>();
		String[] guids = getGuids("20,21,22");//查预警数据
		if (guids.length != 0) {
			articleContentByGuids(sentences, guids);
		} else {
			guids = getGuids(getArticleStage(FMContext.getCurrent().getCustomerId()));//查监测或归档数据
			articleContentByGuids(sentences, guids);
		}
		res = Util.sortMapEntry(ke.getKeyword(sentences, 20));
		return res;
	}

	private void articleContentByGuids(ArrayList<String> sentences, String[] guids) throws ArticleServiceException {
		List<ArticleBean> articleBeanList;
		if (guids.length != 0) {
			articleBeanList = articleService.listArticle(guids);
			for (ArticleBean articleBean : articleBeanList) {
				sentences.add(articleBean.getContent());
			}
		}
	}

	private String[] getGuids(String stages) throws ArticleServiceException {
		ArticleQueryBean query = new ArticleQueryBean();
		query.setCustIds(FMContext.getCurrent().getCustomerId().toString());
		query.setSimilar("0");//过滤相似文
		query.setOrder("7");//按重要性排序
		query.setArticleOrderBy("0");//降序
//		query.setStages(stages);//全局stage
		query.setCustStage(stages);//客户stage
		query.setPageSize(50);
		String today = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		query.setPostStartTime(today + " 00:00:00");
		query.setPostEndTime(today + " 23:59:59");
		List<String> guids = articleService.queryArticleGuidByPage(query);
		System.err.println("今天词云文章数(" + stages + ")：" + guids.size());
		return guids.toArray(new String[guids.size()]);
	}

	/**
	 * @return  今日平台总采集量
	 * @throws ArticleServiceException
	 */
	String getPlatformTotalNumToday() throws ArticleServiceException{
		ArticleQueryBean query = new ArticleQueryBean();
		PageInfoBean<ArticleBean> pageInfo = new PageInfoBean<ArticleBean>();
		String today = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		query.setSpiderStartTime(today+" 00:00:00");
		query.setSpiderEndTime(today+" 23:59:59");
		query.setCategory("0");//包含垃圾文
		pageInfo = articleService.queryArticleByPage(query);
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(pageInfo.getTotalRecords() * 5);//经理说我们采集的文章太少了，需要加点
	}
	
	public List<QueryConditionBean> listUserCondition(Integer userId) throws FmSearchException {
		return searchService.listUserCondition(userId);
	}
	
	public CustTopicBean findCustTopic(Integer id) {
		return custTopicService.queryCustTopic(id);
	}
}