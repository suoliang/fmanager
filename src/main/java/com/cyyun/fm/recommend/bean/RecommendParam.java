package com.cyyun.fm.recommend.bean;

public class RecommendParam {
	private Integer currentpage;
	
	private Integer pagesize;
	
	private Integer[] custIds;
	
	private Integer[] userIds;
	
	private Integer[] columnIds;
	
	private Integer columnId;
	
	private String keyWord;
		
	private Integer timeType;//时间类型
	
	
	private Short type;//栏目分类(1：地域，2：行业，3：中青出品，4：客户定制)
	
	/**
	 * 开始时间（格式：yyyy-MM-dd HH:mm:ss）
	 */
	private String createTimeStart;
	
	/**
	 * 结束时间（格式：yyyy-MM-dd HH:mm:ss）
	 */
	private String createTimeEnd;
	
	public Integer[] getColumnIds() {
		return columnIds;
	}

	public void setColumnIds(Integer[] columnIds) {
		this.columnIds = columnIds;
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

	public Integer[] getCustIds() {
		return custIds;
	}

	public void setCustIds(Integer[] custIds) {
		this.custIds = custIds;
	}

	public Integer[] getUserIds() {
		return userIds;
	}

	public void setUserIds(Integer[] userIds) {
		this.userIds = userIds;
	}

	public Integer getColumnId() {
		return columnId;
	}

	public void setColumnId(Integer columnId) {
		this.columnId = columnId;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public String getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public Integer getTimeType() {
		return timeType;
	}

	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
	}
}