package com.cyyun.fm.focus.bean;
/**
 * <h3>搜索条件</h3>
 * @author GUOQIANG
 *
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
	private String author;

	private Integer orderField;
	private Integer orderType;

	private Integer currentpage;
	private Integer pagesize;
	private Boolean force = false;
	
	private Integer scopeFlag = 0;//预警范围  0-我的预警  1-所有预警
	
	private Integer source;//预警类型 1 - 人工预警  2 - 系统采集
	
	private Integer warningLevel;//预警等级  1 - 紧急  2 - 重要  3 - 一般 
	
	
	

	public Integer getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel(Integer warningLevel) {
		this.warningLevel = warningLevel;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
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

	public Integer getScopeFlag() {
		return scopeFlag;
	}

	public void setScopeFlag(Integer scopeFlag) {
		this.scopeFlag = scopeFlag;
	}
	
	
}
