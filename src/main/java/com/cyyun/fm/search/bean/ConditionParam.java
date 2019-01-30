package com.cyyun.fm.search.bean;

/**
 * <h3>搜索条件</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class ConditionParam {
	private Integer conditionId;
	private String conditionName;

	private String keyword;

	private String startTime;
	private String endTime;
	private Integer timeType;
	private Integer[] topicType;
	private Integer[] industryType;
	private Integer[] areaType;
	private Integer[] siteType;
	private Integer[] mediaType;
	private Integer[] sentiment;
	private String siteName;
	private String author;

	private Integer orderField;
	private Integer orderType;

	private Integer currentpage;
	private Integer pagesize;
	private Boolean force = false;
	
	private Integer custStage;// 客户对文章的stage
	private String custIds;// 客户ID
	
	private Integer spiderSiteId;
	
	private String siteScope;//1:仅外媒，2：仅国内媒体，0：全部（默认）
	
	private Integer type;//推荐板块类型
	private String websiteTagId;//站点标签ID
	
	private String hot;//热点
	
	private Boolean filterSimilar;//过滤相似文 true：过滤  false：不过滤

	
	

	public Boolean getFilterSimilar() {
		return filterSimilar;
	}

	public void setFilterSimilar(Boolean filterSimilar) {
		this.filterSimilar = filterSimilar;
	}

	public String getHot() {
		return hot;
	}

	public void setHot(String hot) {
		this.hot = hot;
	}

	public String getWebsiteTagId() {
		return websiteTagId;
	}

	public void setWebsiteTagId(String websiteTagId) {
		this.websiteTagId = websiteTagId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getSiteScope() {
		return siteScope;
	}

	public void setSiteScope(String siteScope) {
		this.siteScope = siteScope;
	}

	public Integer getSpiderSiteId() {
		return spiderSiteId;
	}

	public void setSpiderSiteId(Integer spiderSiteId) {
		this.spiderSiteId = spiderSiteId;
	}

	public Integer getCustStage() {
		return custStage;
	}

	public void setCustStage(Integer custStage) {
		this.custStage = custStage;
	}

	public String getCustIds() {
		return custIds;
	}

	public void setCustIds(String custIds) {
		this.custIds = custIds;
	}

	public Integer getConditionId() {
		return conditionId;
	}

	public void setConditionId(Integer conditionId) {
		this.conditionId = conditionId;
	}

	public String getConditionName() {
		return conditionName;
	}

	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getTimeType() {
		return timeType;
	}

	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
	}

	public Integer[] getTopicType() {
		return topicType;
	}

	public void setTopicType(Integer[] topicType) {
		this.topicType = topicType;
	}

	public Integer[] getIndustryType() {
		return industryType;
	}

	public void setIndustryType(Integer[] industryType) {
		this.industryType = industryType;
	}

	public Integer[] getAreaType() {
		return areaType;
	}

	public void setAreaType(Integer[] areaType) {
		this.areaType = areaType;
	}

	public Integer[] getSiteType() {
		return siteType;
	}

	public void setSiteType(Integer[] siteType) {
		this.siteType = siteType;
	}

	public Integer[] getMediaType() {
		return mediaType;
	}

	public void setMediaType(Integer[] mediaType) {
		this.mediaType = mediaType;
	}

	public Integer[] getSentiment() {
		return sentiment;
	}

	public void setSentiment(Integer[] sentiment) {
		this.sentiment = sentiment;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
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

	public Integer getCurrentpage() {
		return currentpage;
	}

	public void setCurrentpage(Integer currentpage) {
		this.currentpage = currentpage;
	}

	public Integer getPagesize() {
		return pagesize;
	}

	public void setPagesize(Integer pagesize) {
		this.pagesize = pagesize;
	}

	public Boolean getForce() {
		return force;
	}

	public void setForce(Boolean force) {
		this.force = force;
	}
}