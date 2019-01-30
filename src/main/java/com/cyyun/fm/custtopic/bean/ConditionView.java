package com.cyyun.fm.custtopic.bean;

import java.util.List;

import com.cyyun.base.service.bean.AreaBean;
import com.cyyun.base.service.bean.ConstantBean;
import com.cyyun.base.service.bean.IndustryBean;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.fm.service.bean.CustSiteBean;

/**
 * <h3>专题数据展示</h3>
 * 
 * @author LIUJUNWU
 * @version 1.0.0
 */
public class ConditionView {
	private Integer id;
	private String name;
	private String keywords;
	private String author;
	private String postStartTime;
	private String postEndTime;

	private Integer postTimeType;
	private List<CustTopicBean> topics;
	private List<IndustryBean> industrys;
	private List<AreaBean> areas;
	private List<CustSiteBean> sites;
	private List<ConstantBean> medias;
	private Integer[] sentiment;

	private Integer orderField;
	private Integer orderType;
	private Boolean filterSimilar;//过滤相似文 true：过滤  false：不过滤

	
	

	public Boolean getFilterSimilar() {
		return filterSimilar;
	}

	public void setFilterSimilar(Boolean filterSimilar) {
		this.filterSimilar = filterSimilar;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Integer getPostTimeType() {
		return postTimeType;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPostStartTime() {
		return postStartTime;
	}

	public void setPostStartTime(String postStartTime) {
		this.postStartTime = postStartTime;
	}

	public String getPostEndTime() {
		return postEndTime;
	}

	public void setPostEndTime(String postEndTime) {
		this.postEndTime = postEndTime;
	}

	public void setPostTimeType(Integer postTimeType) {
		this.postTimeType = postTimeType;
	}

	public List<CustTopicBean> getTopics() {
		return topics;
	}

	public void setTopics(List<CustTopicBean> topics) {
		this.topics = topics;
	}

	public List<IndustryBean> getIndustrys() {
		return industrys;
	}

	public void setIndustrys(List<IndustryBean> industrys) {
		this.industrys = industrys;
	}

	public List<AreaBean> getAreas() {
		return areas;
	}

	public void setAreas(List<AreaBean> areas) {
		this.areas = areas;
	}

	public List<CustSiteBean> getSites() {
		return sites;
	}

	public void setSites(List<CustSiteBean> sites) {
		this.sites = sites;
	}

	public List<ConstantBean> getMedias() {
		return medias;
	}

	public void setMedias(List<ConstantBean> medias) {
		this.medias = medias;
	}

	public Integer[] getSentiment() {
		return sentiment;
	}

	public void setSentiment(Integer[] sentiment) {
		this.sentiment = sentiment;
	}

	public Integer getOrderField() {
		return orderField;
	}

	public void setOrderField(Integer orderField) {
		this.orderField = orderField;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
}