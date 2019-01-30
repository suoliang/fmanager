package com.cyyun.fm.setting.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cyyun.authim.shiro.AuthUtil;
import com.cyyun.base.constant.PermissionCode;
import com.cyyun.base.exception.ErrorMessage;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.CustTopicKeywordService;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.KeywordService;
import com.cyyun.base.service.SpiderService;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.CustTopicKeywordBean;
import com.cyyun.base.service.bean.KeywordBean;
import com.cyyun.base.service.bean.query.CustTopicKeywordQueryBean;
import com.cyyun.base.service.bean.query.CustTopicQueryBean;
import com.cyyun.base.service.bean.query.SpiderQueryBean;
import com.cyyun.base.service.exception.CustTopicServiceException;
import com.cyyun.base.util.DateFormatFactory;
import com.cyyun.common.core.protocol.ResponseBean;
import com.cyyun.common.core.protocol.exception.ProtocolException;
import com.cyyun.common.core.protocol.http.HttpParamBean;
import com.cyyun.common.core.protocol.http.HttpProtocolUtil;
import com.cyyun.common.core.protocol.http.HttpRequestBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.service.FmTaskService;
import com.cyyun.fm.service.bean.TaskBean;
import com.cyyun.fm.service.exception.TaskServiceException;
import com.cyyun.fm.setting.bean.CustTopicInitTaskBean;
import com.cyyun.fm.setting.bean.KeywordView;

/**
 * <h3>客户专题控制器支持</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Component
public class CustTopicSupport {

	private static final Integer keywordnumber = 1000;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${topic.keyword.default.gid}")
	private Integer defGid;

	@Value("${topic.keyword.sina.maxsize}")
	private Integer sinaMaxSize;

	@Autowired
	private SpiderService spiderService;

	@Autowired
	private CustTopicService custTopicService;

	@Autowired
	private CustTopicKeywordService custTopicKeywordService;

	@Autowired
	private KeywordService keywordService;

	@Autowired
	private CustomerConfigService customerConfigService;
	
	@Autowired
	private FmTaskService fmTaskService;
	
	@Autowired
	HttpProtocolUtil httpProtocolUtil;

	public CustTopicBean saveCustTopic(CustTopicBean custTopic, String[] keywords) throws Exception {
		//特殊处理
		custTopic.setId(custTopic.getId() != null ? custTopic.getId() : 0);
		custTopic.setParentId(custTopic.getParentId() != null ? custTopic.getParentId() : 0);

		//新浪查询验证 FIXME 是否需要重新验证？
		/*for (String keyword : keywords) {
			if (!validateSinaKeyword(keyword)) {
				throw new ErrorMessage("采集范围过大，请联系客服");
			}
		}*/

		//查询客户专题, 同级专题不能重名
		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setParentId(custTopic.getParentId());
		query.setName2(custTopic.getName());
		SimpleDateFormat format = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern);
		System.out.println("custTopicService.queryCustTopicsByCondition(start)--------"+format.format(new Date()));
		List<CustTopicBean> topics = custTopicService.queryCustTopicsByCondition(query);
		System.out.println("custTopicService.queryCustTopicsByCondition(end)--------"+format.format(new Date()));
		if (topics.size() > 0) {
			for (CustTopicBean c : topics) {
				if (!c.getId().equals(custTopic.getId())) {
					throw new ErrorMessage("同级专题不能重名");
				}
			}
		}

		//保存专题
		custTopic = saveCustTopic(custTopic);

		//保存专题关键字
		saveCustTopicKeyword(custTopic.getId(), keywords);

		return custTopic;
	}

	private CustTopicBean saveCustTopic(CustTopicBean custTopic) throws Exception {
		SimpleDateFormat format = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern);
		if (custTopic.getId() > 0) {//更新
			custTopic.setFilterParent(false);
			custTopicService.updateCustTopic(custTopic);

		} else {//新增
			//新增专题默认共享给客户下所有用户     2016/7/7 注释 
/*			System.out.println("customerConfigService.listUidByCid(start)--------"+format.format(new Date()));
//			List<Integer> userIds = customerConfigService.listUidByCid(FMContext.getCurrent().getCustomerId());
//			System.out.println("customerConfigService.listUidByCid(end)--------"+format.format(new Date()));
//			if (userIds.indexOf(FMContext.getCurrent().getUserId())>=0) {
//				userIds.remove(userIds.indexOf(FMContext.getCurrent().getUserId()));
//			}
//			Integer[] uids = userIds.toArray(new Integer[] {});

			custTopic.setUserIds(uids.length > 0 ? uids : null);//共享所有用户*/
			custTopic.setStatus(1);//启用
			custTopic.setNeedSpider(true);//需要采集
			custTopic.setNeedWarn(true);//需要预警 FIXME 字段没用?

			custTopic.setCustId(FMContext.getCurrent().getCustomerId());
			custTopic.setCreaterId(FMContext.getCurrent().getUserId());

			custTopic = this.custTopicService.addCustTopic(custTopic);
		}
		return custTopic;
	}

	private void saveCustTopicKeyword(Integer custTopicId, String[] keywords) throws Exception {
		//客户配置
		SimpleDateFormat format = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern);
		System.out.println("customerConfigService.findCustConfigByCid(start)--------"+format.format(new Date()));
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
		System.out.println("customerConfigService.findCustConfigByCid(end)--------"+format.format(new Date()));
		
		List<Integer> gidList = ListBuilder.newArrayList();
		if (custConfig.getAutoSpider()) {
			//删除专题关键字和采集关键字
			System.out.println("custTopicKeywordService.findCustTopicKeywordByCustTopicId(start)--------"+format.format(new Date()));
			List<CustTopicKeywordBean> custTopicKeywords = custTopicKeywordService.findCustTopicKeywordByCustTopicId(custTopicId);
			System.out.println("custTopicKeywordService.findCustTopicKeywordByCustTopicId(end)--------"+format.format(new Date()));
			List<Integer> kids = BeanUtil.buildListByFieldName(custTopicKeywords, "spiderKeyId");

			System.out.println("for each keywordService.findKeywordById(start)--------"+format.format(new Date()));
			for (Integer kid : kids) {
				KeywordBean keyword = keywordService.findKeywordById(kid);
				keywordService.deleteKeywordById(kid);//删除采集关键字
			}
			System.out.println("for each keywordService.findKeywordById(end)--------"+format.format(new Date()));
		}
		custTopicKeywordService.deleteCustTopicKeywordByCustTopicId(custTopicId);//删除专题关键字

		//添加专题关键字和采集关键字
		Iterator<Integer> gidQueue = gidList.iterator();
		List<CustTopicKeywordBean> topicKeywords = new ArrayList<CustTopicKeywordBean>();
		System.out.println("for each saveKeyword(start)--------"+format.format(new Date()));
		for (String keyword : keywords) {
			Integer kid = 0;
			if (custConfig.getAutoSpider()) {//判断是否是自动采集关键字的用户
				if (countCustTopicKeyword() + keywords.length > keywordnumber) {
					throw new ErrorMessage("关键词不能超过" + keywordnumber + "个");
				}
				kid = saveKeyword(null, keyword, 1, gidQueue.hasNext() ? gidQueue.next() : defGid);//保存采集关键字
			}
			if (StringUtils.isNotEmpty(keyword)) {
				CustTopicKeywordBean custTopicKeyword = new CustTopicKeywordBean();
				custTopicKeyword.setTopicId(custTopicId);
				custTopicKeyword.setWord(keyword);
				custTopicKeyword.setSpiderKeyId(kid);
				custTopicKeyword.setCustId(FMContext.getCurrent().getCustomerId());//设置cid属性
				custTopicKeyword.setFilterTitle(false);//不过滤标题
				topicKeywords.add(custTopicKeyword);
			}
		}
		System.out.println("for each saveKeyword(end)--------"+format.format(new Date()));
		if (topicKeywords.size() > 0) {
			System.out.println("custTopicKeywordService.addCustTopicKeyword(start)--------"+format.format(new Date()));
			custTopicKeywordService.addCustTopicKeyword(topicKeywords);//保存专题关键字
			System.out.println("custTopicKeywordService.addCustTopicKeyword(end)--------"+format.format(new Date()));
		}
	}

	public CustTopicBean queryCustTopic(Integer id) {
		CustTopicBean custTopic = custTopicService.queryCustTopic(id);
		//关键词规则显示特殊处理
		if (custTopic.getKeyword() != null) {
			String Keyword = custTopic.getKeyword().trim();
			Keyword = Keyword.replace(" ", "+");
			String ExcludedKeyword = custTopic.getExcludedKeyword().trim();
			if (!("").equals(custTopic.getExcludedKeyword()) && custTopic.getExcludedKeyword() != null) {
				ExcludedKeyword = ExcludedKeyword.replace(" ", "|");
				String[] Keywords = Keyword.split(";");
				String trmpStr = "";
				for (String str : Keywords) {
					trmpStr = trmpStr + str + "-(" + ExcludedKeyword + ");";
				}
				Keyword = trmpStr;
			}
			custTopic.setKeyword(Keyword);
		}
		return custTopic;
	}

	private Integer countCustTopicKeyword() {
		CustTopicKeywordQueryBean query = new CustTopicKeywordQueryBean();
		query.setCustId(FMContext.getCurrent().getCustomerId());
		Integer size = custTopicKeywordService.countCustTopicKeyword(query);//FIXME
		return size;
	}

	private Integer saveKeyword(Integer kid, String keyword, Integer active, Integer gid) throws Exception {
		KeywordBean keywordBean = null;
		if (kid != null) {
			keywordBean = keywordService.findKeywordById(kid);
		}
		if (keywordBean == null || keywordBean.getKid() == null) {
			Date time = Calendar.getInstance().getTime();
			keywordBean = new KeywordBean();
			keywordBean.setCid(FMContext.getCurrent().getCustomerId());
			keywordBean.setIsFspiderActive(1);
			keywordBean.setIsSitespiderActive(1);
			keywordBean.setIsSespiderActive(1);
//			if (validateSinaKeyword(keyword) <= 100000) {
//				keywordBean.setIsWeiboActive(1);//true
//			}else{
//				keywordBean.setIsWeiboActive(0);//false
//			}
			keywordBean.setIsWeiboActive(0);//false
			keywordBean.setIsWeixinActive(0);
			keywordBean.setTmCreate(time);
		}
		if (StringUtils.isNotEmpty(keyword)) {
			keywordBean.setDefinedKeyword(keyword);
		}
		if (keywordBean.getKid() != null) {
			keywordService.updateKeywordById(keywordBean);
			return keywordBean.getKid();
		} else {
			return 0;//现在AutoSpider开关一般是关闭的  return 0是让cust_topic_keyword表中的spider_key_id字段变为0  不去关联public schemer下的keyword表
			//这里执行时word字段是空的  会抛异常
//			return keywordService.addKeyword(keywordBean);
		}
	}

	public List<KeywordView> parseInput(String input) throws Exception {
		List<KeywordView> result = ListBuilder.newArrayList();

//		input = input.replaceAll(" ", "");
		input = input.replaceAll("\\+", " ");
		input = input.replaceAll("\\-", " \\-");
		input = input.replaceAll("\\|", " | ");
		input = input.replaceAll("[\\n\\r\\t]", "");

		String regex = "^[a-zA-Z0-9_\\-\\+\\|\\;\\(\\)\\n\\. \u4e00-\u9fa5]{0,}$";
		if (!Pattern.matches(regex, input)) {
			throw new ErrorMessage("关键词包含其他特殊字符");
		}

		List<String> keywords = ListBuilder.newArrayList();
		String[] subinputs = input.split("\\;");
		for (String subinput : subinputs) {
			if (StringUtils.isNotBlank(subinput)) {
				//keywords.addAll(BooleanConverter.convert(subinput));/**这个方法会大写传入的参数*/
				keywords.add(subinput);
			}
		}

		for (String keyword : keywords) {
			result.add(getKeywordView(keyword));
		}

		return result;
	}

	public List<KeywordView> listCustTopicKeywordView(Integer id) throws Exception {
		List<KeywordView> result = ListBuilder.newArrayList();
		List<CustTopicKeywordBean> keywords = custTopicKeywordService.findCustTopicKeywordByCustTopicId(id);
		for (CustTopicKeywordBean keyword : keywords) {
			KeywordView keywordView = new KeywordView();
			keywordView.setCountSize(0);
			keywordView.setEnable(true);
			keywordView.setKeyword(keyword.getWord());
			result.add(keywordView);
		}
		return result;
	}

	private KeywordView getKeywordView(String keyword) throws Exception {
		KeywordView keywordView = new KeywordView();
		keywordView.setKeyword(keyword);
//		Integer sinaArticleCount = validateSinaKeyword(keyword);
//		keywordView.setCountSize(sinaArticleCount);
//		if (sinaArticleCount >= sinaMaxSize) {
//			keywordView.setEnable(false);
//		} else {
//			keywordView.setEnable(true);
//		}
		keywordView.setEnable(false);
		return keywordView;
	}

	private Integer countSinaArticle(String keyword, Date yesterday) {
		SpiderQueryBean query = new SpiderQueryBean();
		query.setStartTime(yesterday);
		query.setEndTime(yesterday);
		query.setKeyword(keyword);
		Integer sinaArticleCount = spiderService.searchSinaArticleCount(query);
		return sinaArticleCount;
	}

	private Integer validateSinaKeyword(String keyword) throws Exception {
		SimpleDateFormat format = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern);
		System.out.println("date1:"+format.format(new Date()));
		Date today = Calendar.getInstance().getTime();
		//昨天
		Date yesterday = DateUtils.addDays(today, -1);
		Integer temp = countSinaArticle(keyword, yesterday);
		Integer sinaArticleCount = temp;
		//三天前
		Date threeDay = DateUtils.addDays(today, -3);
		temp = countSinaArticle(keyword, threeDay);
		sinaArticleCount = sinaArticleCount < temp ? temp : sinaArticleCount;
		//七天前
		Date sevenDay = DateUtils.addDays(today, -7);
		temp = countSinaArticle(keyword, sevenDay);
		sinaArticleCount = sinaArticleCount < temp ? temp : sinaArticleCount;
		System.out.println("date2:"+format.format(new Date()));
		return sinaArticleCount;
	}

	public static void main(String[] args) throws Exception {
		//		String[] parseKeyword = new CustTopicSupport().parseKeyword("(上海|北京)+(雾霾|污染|化工)+(今年|去年)");
		//		System.out.println(Arrays.toString(parseKeyword));
		String include_kw = "  a  b    c ; d    s ;;s;;";
		include_kw = include_kw.trim();
		include_kw = include_kw.replaceAll(" +", " ");
		include_kw = include_kw.replaceAll(" \\;", "\\;");
		include_kw = include_kw.replaceAll("\\; ", "\\;");

		System.out.println(include_kw);
	}

//	private List<String> parseKeyword(String input) throws ErrorMessage {
//		String[] minusKeys = input.split("\\-");
//		if (minusKeys.length < 1) {
//			throw new ErrorMessage("没有匹配关键词");
//		}
//
//		input = minusKeys[0];
//
//		List<List<String>> andKeys = splitKeyword(input);
//		String[] combineResult = generateKeyword(andKeys.iterator());
//
//		String minus = "";
//		for (int i = 1; i < minusKeys.length; i++) {
//			minus += " -" + minusKeys[i];
//		}
//
//		ListBuilder<String> result = ListBuilder.addAll(null);
//		for (String key : combineResult) {
//			result.and(key + minus);
//		}
//
//		return result.build();
//	}

//	private static List<List<String>> splitKeyword(String input) {
//		List<List<String>> andKeys = ListBuilder.newArrayList();
//		String[] keywords = input.split("\\;");
//		for (String keyword : keywords) {
//			String[] andkeys = keyword.split("\\+");
//			for (String andkey : andkeys) {
//				if (StringUtils.isBlank(andkey)) {
//					continue;
//				}
//				List<String> orKeys = ListBuilder.newArrayList();
//				String[] orkeys = andkey.split("\\|");
//				for (String orkey : orkeys) {
//					if (StringUtils.isNotBlank(orkey)) {
//						orKeys.add(orkey);
//					}
//				}
//				andKeys.add(orKeys);
//			}
//		}
//		return andKeys;
//	}

//	private static String[] generateKeyword(Iterator<List<String>> iterator) {
//		if (!iterator.hasNext()) {
//			return null;
//		}
//		List<String> next = iterator.next();
//		String[] last = generateKeyword(iterator);
//
//		ListBuilder<String> result = ListBuilder.addAll(null);
//		for (String keyword : next) {
//			if (ArrayUtils.isNotEmpty(last)) {
//				for (String keyword2 : last) {
//					result.and(keyword + " " + keyword2);
//				}
//			} else {
//				result.and(keyword);
//			}
//		}
//
//		return result.buildArray();
//	}
	
	public List<CustTopicBean> listTopic(Integer parentId) throws CustTopicServiceException {
		CustTopicQueryBean query = new CustTopicQueryBean();
		query.setParentId(parentId);
		query.setCustId(FMContext.getCurrent().getCustomerId());
		query.setStatus(1);
		query.setNeedPaging(false);
		if (!AuthUtil.hasPermission(PermissionCode.DATA_TOPIC_ALL)) {
			query.setCreaterId(FMContext.getCurrent().getUserId());
		}
		List<CustTopicBean> topics = null;
		if (parentId == 0) {
			topics = custTopicService.listCustTopicWithShare(query);
		} else {
			query.setCreaterId(null);//子专题为同一用户所创建，避免过滤共享用户的专题
			topics = custTopicService.queryCustTopicsByCondition(query);
		}
		return topics;
	}
	
	public Integer addCustTopicInitTask(TaskBean taskBean) throws TaskServiceException{
		if(taskBean==null){
			return null;
		}
		Integer result = fmTaskService.addTask(taskBean);
		return result;
	}
	
	public void custTopicInitCallInterface(CustTopicInitTaskBean bean, Integer taskId, String interfaceUrl) throws Exception{
		
		TaskBean updateTaskBean = new TaskBean();
		try {
			log.info("===taskId===" +taskId + "===");
			if(taskId!=null ){
				updateTaskBean.setId(taskId);
				updateTaskBean.setUpdateTime(new Date());
				
				List<HttpParamBean> params=new ArrayList<HttpParamBean>();
				HttpParamBean param=new HttpParamBean();
				param.setName("taskId");
				param.setValue(""+taskId);
				params.add(param);
				
				param=new HttpParamBean();
				param.setName("cids");
				param.setValue( bean.getCids());
				params.add(param);
				
				param=new HttpParamBean();
				param.setName("labelIds");
				param.setValue( bean.getLabelIds());
				params.add(param);
				
				/**用于区分IM和FM的task任务，表不同需要判断处理成功失败状态*/
				param=new HttpParamBean();
				param.setName("source");
				param.setValue("fm");
				params.add(param);
				
				param=new HttpParamBean();
				param.setName("startTime");
				param.setValue( bean.getStartTime());
				params.add(param);
				
				param=new HttpParamBean();
				param.setName("endTime");
				param.setValue( bean.getEndTime());
				params.add(param);
				
				HttpRequestBean req=new HttpRequestBean();
				req.setAddress(interfaceUrl);
				req.setTimeout(10000);
				req.setData(params);

				try {
					ResponseBean response = httpProtocolUtil.send(req);
					if(response.isSuccess() && response.getData()!=null ){
						
						JSONObject jobj = JSONObject.parseObject(response.getData().toString().trim());
						if(jobj!=null && jobj.getString("retCode")!=null){
							String resultCode = jobj.getString("retCode").toString();
							log.info("===resultCode===" +resultCode + "===");
							if("0".equals(resultCode)){
								updateTaskBean.setStatus((short)1);
								try {
									this.updateCustTopicInitTask(updateTaskBean);
								} catch (TaskServiceException e) {
									log.error("===updateCustTopicInitTask===exception==="+e.getMessage(),e);
								}
							}else{
								String resultDesc = jobj.getString("detail").toString();
								log.error("===调用接口失败==="+resultDesc);
								
								updateTaskBean.setStatus((short)3);
								updateTaskBean.setResult(resultDesc);
								try {
									this.updateCustTopicInitTask(updateTaskBean);
								} catch (TaskServiceException e) {
									log.error("===updateCustTopicInitTask===exception==="+e.getMessage(),e);
								}
								throw new Exception(new TaskServiceException("调用接口失败："+resultDesc));
							}
						} else{
							log.error("===调用接口失败：返回参数错误===");
							updateTaskBean.setStatus((short)3);
							updateTaskBean.setResult("调用接口失败：返回参数错误");
							try {
								this.updateCustTopicInitTask(updateTaskBean);
							} catch (TaskServiceException e) {
								log.error("===updateCustTopicInitTask===exception==="+e.getMessage(),e);
							}
							throw new Exception(new TaskServiceException("调用接口失败：返回参数错误"));
						}
						
					} else{
						log.error("===调用接口失败：连接错误===");
						updateTaskBean.setStatus((short)3);
						updateTaskBean.setResult("调用接口失败：返回参数错误");
						try {
							this.updateCustTopicInitTask(updateTaskBean);
						} catch (TaskServiceException e) {
							log.error("===updateCustTopicInitTask===exception==="+e.getMessage(),e);
						}
						throw new Exception(new TaskServiceException("调用接口失败：返回参数错误"));
					}
				} catch (ProtocolException e) {
					log.error("===ProtocolException==="+e.getMessage(),e);
					updateTaskBean.setStatus((short)3);
					updateTaskBean.setResult("调用接口失败："+e.getMessage());
					try {
						this.updateCustTopicInitTask(updateTaskBean);
					} catch (TaskServiceException te) {
						log.error("===updateCustTopicInitTask===exception==="+te.getMessage(),te);
					}
					
					throw new Exception(new TaskServiceException("调用接口失败："+e.getMessage()));
				}
			}
		} catch (Exception e) {
			log.error("===exception==="+e.getMessage(), e);
			
			updateTaskBean.setStatus((short)3);
			updateTaskBean.setResult("调用接口失败："+e.getMessage());
			try {
				this.updateCustTopicInitTask(updateTaskBean);
			} catch (TaskServiceException te) {
				log.error("===updateCustTopicInitTask===exception==="+te.getMessage(),te);
			}
			throw new Exception(new TaskServiceException("调用接口失败："+e.getMessage()));
		}
		
	}
	
	public Integer updateCustTopicInitTask(TaskBean taskBean) throws TaskServiceException{
		if(taskBean==null || taskBean.getId()==null){
			return null;
		}
		Integer result = fmTaskService.updateTask(taskBean);
		return result;
	}
	
	/**
	 * 返回给定专题的最顶级专题id
	 * @param custTopicBean
	 * @param list
	 */
	public String queryTopIdCustTopic(CustTopicBean custTopicBean,List<CustTopicBean> list) {
		if (custTopicBean.getParentId() != 0) {
			CustTopicBean parentCustTopicBean = custTopicService.queryCustTopic(custTopicBean.getParentId());
			list.add(parentCustTopicBean);
			queryTopIdCustTopic(parentCustTopicBean, list);
		}
		CustTopicBean topCustTopic = list.get(list.size() - 1);// 最顶层专题
		return String.valueOf(topCustTopic.getId());
	}
	
	public String[] arrayUnique(String[] a) {
		Set<String> set = new HashSet<String>();
		set.addAll(Arrays.asList(a));
		return set.toArray(new String[0]);
	}
	
}