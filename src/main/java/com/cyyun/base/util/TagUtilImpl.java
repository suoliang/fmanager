package com.cyyun.base.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cyyun.authim.service.UserService;
import com.cyyun.authim.service.bean.UserBean;
import com.cyyun.base.constant.ArticleConstants;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.AreaService;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.IndustryService;
import com.cyyun.base.service.WebsiteService;
import com.cyyun.base.service.bean.AreaBean;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.ArticleHomeWebsite;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.bean.IndustryBean;
import com.cyyun.base.service.bean.WebsiteBean;
import com.cyyun.base.service.bean.WebsiteHomeBean;
import com.cyyun.base.service.bean.query.ArticleQueryBean;
import com.cyyun.base.service.exception.AreaServiceException;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.base.service.exception.IndustryServiceException;
import com.cyyun.base.service.exception.WebsiteServiceException;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.CustomerService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.customer.service.bean.CustomerBean;
import com.cyyun.fm.service.CustDataCategoryService;
import com.cyyun.fm.service.FmTaskService;
import com.cyyun.fm.service.bean.CustDataCategoryBean;
import com.cyyun.fm.service.bean.TaskBean;
import com.cyyun.fm.service.exception.CustDataCategoryServiceException;
import com.cyyun.fm.service.exception.TaskServiceException;

/**
 * <h3>标签显示工具类实现</h3>
 * 
 * @author xijq
 * @version 1.0.0
 * 
 */
@Component("tagUtil")
public class TagUtilImpl implements TagUtil {

	private static final Logger log = LoggerFactory.getLogger(TagUtilImpl.class);

	@Autowired
	private UserService userService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AreaService areaService;

	@Autowired
	private IndustryService industryService;

	@Autowired
	private WebsiteService websiteService;

	@Autowired
	private CustTopicService custTopicService;

	@Autowired
	ArticleService articleService;

	@Autowired
	private CustDataCategoryService custDataCategoryService;
	
	@Autowired
	private CustomerConfigService customerConfigService;
	
	@Autowired
	private FmTaskService fmTaskService;
	
	@Value("${importance.cid}")
	private String importanceCid;
	
	private Map<Integer, UserBean> userMap;

	private Map<Integer, CustomerBean> custMap;

	private Map<Integer, AreaBean> areaMap;

	private Map<Integer, IndustryBean> industryMap;

	private Map<Integer, WebsiteBean> websiteMap;

	private Map<Integer, CustDataCategoryBean> custCateMap;

	private Map<Integer, Long> areaCodeMap;

	public TagUtilImpl() {
		this.userMap = new HashMap<Integer, UserBean>();
		this.custMap = new HashMap<Integer, CustomerBean>();
		this.areaMap = new HashMap<Integer, AreaBean>();
		this.websiteMap = new HashMap<Integer, WebsiteBean>();
		this.industryMap = new HashMap<Integer, IndustryBean>();
		this.custCateMap = new HashMap<Integer, CustDataCategoryBean>();
		this.areaCodeMap = new HashMap<Integer, Long>();
	}

	private UserBean getUser(Integer userId) {
		UserBean user = this.userMap.get(userId);
		if (user == null) {
			user = this.userService.findUser(userId);
			if (user != null) {
				this.userMap.put(user.getId(), user);
			}
		}
		return user;
	}

	private CustomerBean getCust(Integer custId) {
		CustomerBean cust = this.custMap.get(custId);
		if (cust == null) {
			cust = this.customerService.findCustomer(custId);
			if (cust != null) {
				this.custMap.put(cust.getId(), cust);
			}
		}
		return cust;
	}

	private CustTopicBean getCustTopic(Integer topicId) {
		if (topicId == 0) {
			return null;
		}
		/*CustTopicBean topic = this.custTopicMap.get(topicId);
		if (topic == null) {
			topic = this.custTopicService.queryCustTopic(topicId);
			if (topic != null) {
				this.custTopicMap.put(topic.getId(), topic);
			}
		}*/
		CustTopicBean topic = this.custTopicService.queryCustTopic(topicId);
		if (topic.getId() == null) {
			topic = null;
		}
		return topic;
	}

	private AreaBean getArea(Integer areaId) {
		AreaBean area = this.areaMap.get(areaId);
		if (area == null) {
			try {
				area = this.areaService.findAreaById(areaId);
			} catch (AreaServiceException e) {
				log.error(e.getErrorMsg(), e);
			}
			if (area != null) {
				this.areaMap.put(area.getId(), area);
			}
		}
		return area;
	}

	private long getAreaCode(Integer areaId) {
		Long code = this.areaCodeMap.get(areaId);
		if (code == null) {
			AreaBean area = this.getArea(areaId);
			if (area == null) {
				return 0;
			}
			long l = area.getCode();
			int i = 0;
			while (l % 10 == 0) {
				i++;
				l /= 10;
			}
			code = l + 1;
			for (int j = 0; j < i; j++) {
				code *= 10;
			}
			this.areaCodeMap.put(areaId, code);
		}
		return code;
	}

	private IndustryBean getIndustry(Integer industryId) {
		IndustryBean industry = this.industryMap.get(industryId);
		if (industry == null) {
			try {
				industry = this.industryService.findIndustryById(industryId);
			} catch (IndustryServiceException e) {
				log.error(e.getErrorMsg(), e);
			}
			if (industry != null) {
				this.industryMap.put(industry.getId(), industry);
			}
		}
		return industry;
	}

	private WebsiteBean getWebsite(Integer siteId) {
		WebsiteBean site = this.websiteMap.get(siteId);
		if (site == null) {
			try {
				site = this.websiteService.findWebsiteById(siteId);
			} catch (WebsiteServiceException e) {
				log.error(e.getErrorMsg(), e);
			}
			if (site != null) {
				this.websiteMap.put(site.getFid(), site);
			}
		}
		return site;
	}

	private CustDataCategoryBean getCustDataCategory(Integer cateId) {
		CustDataCategoryBean cate = this.custCateMap.get(cateId);
		if (cate == null) {
			try {
				cate = this.custDataCategoryService.getCustDataCategoryBeanById(cateId);
			} catch (CustDataCategoryServiceException e) {
				log.error(e.getErrorMsg(), e);
			}
			if (cate != null) {
				this.custCateMap.put(cate.getId(), cate);
			}
		}
		return cate;
	}

	@Override
	public String getUsername(Integer userId) {
		if (userId == null) {
			return null;
		}
		UserBean user = this.getUser(userId);
		return user == null ? userId.toString() : user.getUsername();
	}
	
	@Override
	public String getSimilarnum(String guid, String guidGroup) {
		if (guid == null) {
			return null;
		}
		ArticleQueryBean query=new ArticleQueryBean();
		Integer customerId = FMContext.getCurrent().getCustomerId();
		query.setGuidGroup(guidGroup);
		query.setCustIds(customerId.toString());
		query.setPageSize(10);
		query.setCustStage(getArticleStage(customerId));
		query.setArticleOreply("2");
		query.setCategory("0");//带有垃圾文
		PageInfoBean<ArticleBean> pageInfo = null;
		try {
			pageInfo = articleService.queryArticleByPage(query);
			/**和相识文列表处理方式一致*/
			/**此处逻辑,因为有分页,所以article的guid和articleBean的guidGroup在某一页不一定存在相同数据,每次总数都减去1条记录肯定是对的*/
			pageInfo.setTotalRecords(pageInfo.getTotalRecords()-1);
//			List<ArticleBean> articleList = pageInfo.getData();
//			for (ArticleBean articleBean : articleList) {
//				if (articleBean.getGuid().equals(guid)) {
//					pageInfo.setTotalRecords(pageInfo.getTotalRecords()-1);
//				}
//			}
		} catch (ArticleServiceException e) {
			e.printStackTrace();
			return StringUtils.EMPTY;
		}
		return pageInfo.getTotalRecords().toString();
	}
	
//	@Override
//	public String getSimilarnum(String guid, String guidGroup) {
//		CustConfigBean custConfig=customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
//		if (guid == null || custConfig.getFilterSimilar()) {//FilterSimilar为false时查询到的文章带有相似文，为true则没有
//			return "0";
//		}
//		ArticleQueryBean query = new ArticleQueryBean();
//		query.setPageNo(1);
//		query.setPageSize(1000);
//		Map<String, Integer> map=new HashMap<String, Integer>();
//		List<String> guidJoinguidGroups = new ArrayList<String>();
//		guidJoinguidGroups.add(guid+"_"+guidGroup);
//		query.setGuid_guidGroups(guidJoinguidGroups);
//		try {
//			map = articleService.countArticleSimilar(query);
//		} catch (ArticleServiceException e1) {
//			e1.printStackTrace();
//		}
////		System.err.println("相似文数======="+map.get(guid)+"====guidGroup======="+guidGroup);
//		return map.get(guid).toString();
//
//	}
	
	@Override
	public String getUsername(Integer[] userIds, String separator) {
		if (userIds == null || userIds.length == 0) {
			return null;
		}
		if (separator == null) {
			separator = ",";
		}
		StringBuffer sb = new StringBuffer();
		UserBean user = null;
		for (Integer id : userIds) {
			user = this.getUser(id);
			if (user == null) {
				sb.append(id).append(separator);
			} else {
				sb.append(user.getUsername()).append(separator);
			}
		}
		return sb.substring(0, sb.lastIndexOf(separator));
	}

	@Override
	public String getRealName(Integer userId) {
		if (userId == null) {
			return null;
		}
		UserBean user = this.getUser(userId);
		return user == null ? userId.toString() : user.getRealName();
	}

	@Override
	public String getRealName(Integer[] userIds, String separator) {
		if (userIds == null || userIds.length == 0) {
			return null;
		}
		if (separator == null) {
			separator = ",";
		}
		StringBuffer sb = new StringBuffer();
		UserBean user = null;
		for (Integer id : userIds) {
			user = this.getUser(id);
			if (user == null) {
				sb.append(id).append(separator);
			} else {
				sb.append(user.getRealName()).append(separator);
			}
		}
		return sb.substring(0, sb.lastIndexOf(separator));
	}

	@Override
	public String getCustName(Integer custId) {
		if (custId == null) {
			return null;
		}
		CustomerBean cust = this.getCust(custId);
		return cust == null ? custId.toString() : cust.getName();
	}

	@Override
	public String getCustName(Integer[] custIds, String separator) {
		if (custIds == null || custIds.length == 0) {
			return null;
		}
		if (separator == null) {
			separator = ",";
		}
		StringBuffer sb = new StringBuffer();
		CustomerBean cust = null;
		for (Integer id : custIds) {
			cust = this.getCust(id);
			if (cust == null) {
				sb.append(id).append(separator);
			} else {
				sb.append(cust.getName()).append(separator);
			}
		}
		return sb.substring(0, sb.lastIndexOf(separator));
	}

	@Override
	public String getAreaName(Integer areaId) {
		if (areaId == null) {
			return null;
		}
		AreaBean area = this.getArea(areaId);
		return area == null ? areaId.toString() : area.getName();
	}

	@Override
	public String getAreaName(Integer[] areaIds, String separator) {
		if (areaIds == null || areaIds.length == 0) {
			return null;
		}
		if (separator == null) {
			separator = ",";
		}
		StringBuffer sb = new StringBuffer();
		AreaBean area = null;
		for (Integer id : areaIds) {
			area = this.getArea(id);
			if (area == null) {
				sb.append(id).append(separator);
			} else {
				sb.append(area.getName()).append(separator);
			}
		}
		return sb.substring(0, sb.lastIndexOf(separator));
	}

	@Override
	public String getAreaName(Integer[] areaIds, String separator, Integer[] limitAreaIds) {
		if (limitAreaIds == null || limitAreaIds.length == 0) {
			return this.getAreaName(areaIds, separator);
		}
		if (areaIds == null || areaIds.length == 0) {
			return null;
		}

		List<Integer> areaIdList = new ArrayList<Integer>();
		for (Integer areaId : areaIds) {
			AreaBean area = this.getArea(areaId);
			if (area == null) {
				continue;
			}

			AreaBean limitArea = null;
			long maxCode = 0;
			for (Integer id : limitAreaIds) {
				limitArea = this.getArea(id);
				if (limitArea == null) {
					continue;
				}
				maxCode = this.getAreaCode(id);
				if (area.getCode() >= limitArea.getCode() && area.getCode() < maxCode) {
					areaIdList.add(area.getId());
					break;
				} else {
					continue;
				}
			}
		}
		return this.getAreaName(areaIdList.toArray(new Integer[] {}), separator);
	}

	@Override
	public String getIndustryName(Integer industryId) {
		if (industryId == null) {
			return null;
		}
		IndustryBean industry = this.getIndustry(industryId);
		return industry == null ? industryId.toString() : industry.getName();
	}

	@Override
	public String getIndustryName(Integer[] industryIds, String separator) {
		if (industryIds == null || industryIds.length == 0) {
			return null;
		}
		if (separator == null) {
			separator = ",";
		}
		StringBuffer sb = new StringBuffer();
		IndustryBean industry = null;
		for (Integer id : industryIds) {
			industry = this.getIndustry(id);
			if (industry == null) {
				sb.append(id).append(separator);
			} else {
				sb.append(industry.getName()).append(separator);
			}
		}
		return sb.substring(0, sb.lastIndexOf(separator));
	}

	@Override
	public String getWebsiteName(Integer siteId) {
		if (siteId == null) {
			return null;
		}
		WebsiteBean site = this.getWebsite(siteId);
		return site == null ? siteId.toString() : site.getName();
	}

	@Override
	public String getWebsiteName(Integer[] siteIds, String separator) {
		if (siteIds == null || siteIds.length == 0) {
			return null;
		}
		if (separator == null) {
			separator = ",";
		}
		StringBuffer sb = new StringBuffer();
		WebsiteBean site = null;
		for (Integer id : siteIds) {
			site = this.getWebsite(id);
			if (site == null) {
				sb.append(id).append(separator);
			} else {
				sb.append(site.getName()).append(separator);
			}
		}
		return sb.substring(0, sb.lastIndexOf(separator));
	}

	@Override
	public String getCustTopicName(Integer topicId) {
		if (topicId == null || topicId == 0) {
			return null;
		}
		CustTopicBean topic = this.getCustTopic(topicId);
		return topic == null ? topicId.toString() : topic.getName();
	}

	@Override
	public String getCustTopicName(Integer[] topicIds, String separator) {
		if (topicIds == null || topicIds.length == 0) {
			return "";
		}
		if (separator == null) {
			separator = ",";
		}
		StringBuffer sb = new StringBuffer();
		CustTopicBean topic = null;
		for (Integer id : topicIds) {
			topic = this.getCustTopic(id);
			if (topic != null) {
				sb.append(topic.getName()).append(separator);
			}
		}
		return sb.length() > 0 ? sb.substring(0, sb.lastIndexOf(separator)) : "";
	}

	@Override
	public String getCustTopicName(Set<Integer> topicIds, String separator) {
		if (topicIds == null || topicIds.size() == 0) {
			return null;
		}
		Integer[] ids = new Integer[] {};
		ids = topicIds.toArray(ids);
		return getCustTopicName(ids, separator);
	}

	@Override
	public String getCustDataCateName(Integer cateId) {
		if (cateId == null || cateId == 0) {
			return null;
		}
		CustDataCategoryBean cate = this.getCustDataCategory(cateId);
		return cate == null ? cateId.toString() : cate.getName();
	}

	@Override
	public String getCustDataCateName(Integer[] cateIds, String separator) {
		if (cateIds == null || cateIds.length == 0) {
			return null;
		}
		if (separator == null) {
			separator = ",";
		}
		StringBuffer sb = new StringBuffer();
		CustDataCategoryBean cate = null;
		for (Integer id : cateIds) {
			cate = this.getCustDataCategory(id);
			if (cate == null) {
				sb.append(id).append(separator);
			} else {
				sb.append(cate.getName()).append(separator);
			}
		}
		return sb.substring(0, sb.lastIndexOf(separator));
	}

	@Override
	public String getAreaName(List<Integer> areaIds, String separator) {
		return this.getAreaName(areaIds.toArray(new Integer[areaIds.size()]), separator);
	}

	@Override
	public String getIndustryName(List<Integer> industryIds, String separator) {
		return this.getIndustryName(industryIds.toArray(new Integer[industryIds.size()]), separator);
	}

	@Override
	public <S, T> String join(List<T> source, String fieldName, String separator) {
		return BeanUtil.joinByFieldName(source, fieldName, separator);
	}
	
	/**
	 * <h3>获取客户文章Stage</h3>
	 * 
	 * @param cid
	 * @return
	 */
	public String getArticleStage(Integer cid) {
		CustConfigBean custConfig = customerConfigService.findCustConfigByCid(cid);
		Boolean viewMonitorData = custConfig.getViewMonitorData();
		String stage = viewMonitorData != null && viewMonitorData ? ArticleConstants.ArticleStage.PUTONG : ArticleConstants.ArticleStage.GUIDANG;
		return stage;
	}

	@Override
	public String getImportance(String importanceMap, String importance) {
		
		String[] cids = importanceCid.split("\\,");
		Integer cid = FMContext.getCurrent().getCustomerId();
		
		/**如果cid在配置的客户cid集合中，显示map集合里的值*/
		for (String string : cids) {
			if (StringUtils.equalsIgnoreCase(string, cid.toString())) {
				importance = importanceMap;
				break;
			}
		}
	
		String result = String.format("%.4f", Double.valueOf(importance));
		return result;
	}

	@Override
	public String getWebsiteHome(List<ArticleHomeWebsite> homeWebsites, String separator) {
		if (homeWebsites == null || homeWebsites.size() == 0) {
			return null;
		} 
		if (separator == null) {
			separator = ",";
		}
		StringBuffer sb = new StringBuffer();
		WebsiteHomeBean websiteHomeBean = new WebsiteHomeBean();
		for (ArticleHomeWebsite bean : homeWebsites) {
			websiteHomeBean = websiteService.findWebsiteHomeById(Long.parseLong(bean.getWebsiteId().toString()));
			if (websiteHomeBean != null) {
				sb.append(websiteHomeBean.getWebsiteName()).append(separator);
			}
		}
		return sb.length() > 0 ? sb.substring(0, sb.lastIndexOf(separator)) : "";
	}

	@Override
	public String getWeiboTaskStatus(String weiboUrl) {
		String status = "3";/**此处默认失败，页面正常显示*/
		try {
			TaskBean taskBean = fmTaskService.queryByName("【微博溯源】"+weiboUrl);
			if (ObjectUtils.notEqual(taskBean, null)) {
				status = taskBean.getStatus().toString();
			}
		} catch (TaskServiceException e) {
			e.printStackTrace();
		}
		return status;
	}
}