package com.cyyun.fm.search.bean;

import java.util.List;

import com.cyyun.common.core.util.ListBuilder;

/**
 * <h3>列表</h3>
 * 
 * @author 	LIUJUNWU
 * @version 1.0.0
 */
public class CloudSearchParam {
	private List<EngineParam> engineIds = ListBuilder.newArrayList();
	private String keyword;
	private Integer pageNo = 1;
	
	
	
	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public List<EngineParam> getEngineIds() {
		return engineIds;
	}

	public void setEngineIds(List<EngineParam> engineIds) {
		this.engineIds = engineIds;
	}

	
}