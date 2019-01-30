package com.cyyun.base.util;

import java.util.List;
import java.util.Set;

import com.cyyun.base.service.bean.ArticleHomeWebsite;


/**
 * <h3>标签显示工具类接口</h3>
 * 
 * @author xijq
 * @version 1.0.0
 * 
 */
public interface TagUtil {

	public String getUsername(Integer userId);
	
	public String getSimilarnum(String guid, String guidGroup);
	
	public String getUsername(Integer[] userIds, String separator);

	public String getRealName(Integer userId);

	public String getRealName(Integer[] userIds, String separator);

	public String getCustName(Integer custId);

	public String getCustName(Integer[] custIds, String separator);

	public String getAreaName(Integer areaId);

	public String getAreaName(Integer[] areaIds, String separator);

	public String getAreaName(List<Integer> areaIds, String separator);
	
	public String getAreaName(Integer[] areaIds, String separator, Integer[] limitAreaIds);

	public String getIndustryName(Integer industryId);

	public String getIndustryName(Integer[] industryIds, String separator);

	public String getIndustryName(List<Integer> industryIds, String separator);

	public String getWebsiteName(Integer siteId);

	public String getWebsiteName(Integer[] siteIds, String separator);

	public String getCustTopicName(Integer topicId);

	public String getCustTopicName(Integer[] topicIds, String separator);

	public String getCustTopicName(Set<Integer> topicIds, String separator);

	public String getCustDataCateName(Integer cateId);

	public String getCustDataCateName(Integer[] cateIds, String separator);

	public <S, T> String join(List<T> source, String fieldName, String separator);

	public String getImportance(String importanceMap, String importance);
	
	public String getWebsiteHome(List<ArticleHomeWebsite> homeWebsites, String separator);
	
	public String getWeiboTaskStatus(String weiboUrl);
	
}