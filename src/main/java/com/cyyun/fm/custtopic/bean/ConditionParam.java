package com.cyyun.fm.custtopic.bean;

/**
 * <h3>搜索条件</h3>
 * 
 * @author LIUJUNWU
 * @version 1.0.0
 */
public class ConditionParam {
	private String keyword;
	private String startTime;
	private String endTime;
	private Integer[] mediaType;//媒体类型
	private Integer timeType;
	
	private Integer orderField;//滚动
	private Integer orderType;
	
	private Integer currentpage;
	private Integer pagesize;
	 
	private Integer[] topicId;//专题ID
	private Integer CustId;//客户ID
	private Boolean filterSimilar;//过滤相似文 true：过滤  false：不过滤

	
	

	public Boolean getFilterSimilar() {
		return filterSimilar;
	}

	public void setFilterSimilar(Boolean filterSimilar) {
		this.filterSimilar = filterSimilar;
	}
	
	
	
	
	
	public Integer getCustId() {
		return CustId;
	}
	public void setCustId(Integer custId) {
		CustId = custId;
	}
	public Integer[] getTopicId() {
		return topicId;
	}
	public void setTopicId(Integer[] topicId) {
		this.topicId = topicId;
	}
	public Integer getTimeType() {
		return timeType;
	}
	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
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
	public Integer[] getMediaType() {
		return mediaType;
	}
	public void setMediaType(Integer[] mediaType) {
		this.mediaType = mediaType;
	}
	

	
}