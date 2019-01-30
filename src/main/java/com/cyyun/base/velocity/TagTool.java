package com.cyyun.base.velocity;

import java.util.List;
import java.util.Set;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;

import com.cyyun.base.service.bean.ArticleHomeWebsite;
import com.cyyun.base.util.TagUtil;
import com.cyyun.common.core.util.SpringContextUtil;

/**
 * <h3>Velocity 标签显示工具类</h3>
 * 
 * @author xijq
 * @version 1.0.0
 * 
 */
@DefaultKey("tag")
@ValidScope(Scope.APPLICATION)
public class TagTool implements TagUtil {

	private TagUtil tagUtil;

	public TagTool() {
	}

	public TagUtil getTagUtil() {
		if (tagUtil == null) {
			this.tagUtil = SpringContextUtil.getBean(TagUtil.class);
		}
		return tagUtil;
	}

	@Override
	public String getUsername(Integer userId) {
		return getTagUtil().getUsername(userId);
	}

	@Override
	public String getUsername(Integer[] userIds, String separator) {
		return getTagUtil().getUsername(userIds, separator);
	}

	@Override
	public String getRealName(Integer userId) {
		return getTagUtil().getRealName(userId);
	}

	@Override
	public String getRealName(Integer[] userIds, String separator) {
		return getTagUtil().getRealName(userIds, separator);
	}

	@Override
	public String getCustName(Integer custId) {
		return getTagUtil().getCustName(custId);
	}

	@Override
	public String getCustName(Integer[] custIds, String separator) {
		return getTagUtil().getCustName(custIds, separator);
	}

	@Override
	public String getAreaName(Integer areaId) {
		return getTagUtil().getAreaName(areaId);
	}

	@Override
	public String getAreaName(Integer[] areaIds, String separator) {
		return getTagUtil().getAreaName(areaIds, separator);
	}
	
	public String getAreaName(Integer[] areaIds, String separator, Integer[] limitAreaIds) {
		return getTagUtil().getAreaName(areaIds, separator, limitAreaIds);
	}

	@Override
	public String getAreaName(List<Integer> areaIds, String separator) {
		return getTagUtil().getAreaName(areaIds, separator);
	}

	@Override
	public String getIndustryName(Integer industryId) {
		return getTagUtil().getIndustryName(industryId);
	}

	@Override
	public String getIndustryName(Integer[] industryIds, String separator) {
		return getTagUtil().getIndustryName(industryIds, separator);
	}

	@Override
	public String getIndustryName(List<Integer> industryIds, String separator) {
		return getTagUtil().getIndustryName(industryIds, separator);
	}

	@Override
	public String getWebsiteName(Integer siteId) {
		return getTagUtil().getWebsiteName(siteId);
	}

	@Override
	public String getWebsiteName(Integer[] siteIds, String separator) {
		return getTagUtil().getWebsiteName(siteIds, separator);
	}

	@Override
	public String getCustTopicName(Integer topicId) {
		return getTagUtil().getCustTopicName(topicId);
	}

	@Override
	public String getCustTopicName(Integer[] topicIds, String separator) {
		return getTagUtil().getCustTopicName(topicIds, separator);
	}

	@Override
	public String getCustTopicName(Set<Integer> topicIds, String separator) {
		return getTagUtil().getCustTopicName(topicIds, separator);
	}

	@Override
	public String getCustDataCateName(Integer cateId) {
		return getTagUtil().getCustDataCateName(cateId);
	}

	@Override
	public String getCustDataCateName(Integer[] cateIds, String separator) {
		return getTagUtil().getCustDataCateName(cateIds, separator);
	}

	@Override
	public <S, T> String join(List<T> source, String fieldName, String separator) {
		return getTagUtil().join(source, fieldName, separator);
	}

	@Override
	public String getSimilarnum(String guid, String guidGroup) {
		return getTagUtil().getSimilarnum(guid, guidGroup);
	}
	
	@Override
	public String getImportance(String importanceMap, String importance) {
		return getTagUtil().getImportance(importanceMap,importance);
	}

	@Override
	public String getWebsiteHome(List<ArticleHomeWebsite> homeWebsites, String separator) {
		return getTagUtil().getWebsiteHome(homeWebsites, separator);
	}

	@Override
	public String getWeiboTaskStatus(String weiboUrl) {
		return getTagUtil().getWeiboTaskStatus(weiboUrl);
	}
	
}