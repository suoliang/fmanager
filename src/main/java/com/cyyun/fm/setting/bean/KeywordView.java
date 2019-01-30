package com.cyyun.fm.setting.bean;

/**
 * <h3>关键字</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class KeywordView {
	private String keyword;
	private boolean enable;
	private Integer countSize;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public Integer getCountSize() {
		return countSize;
	}

	public void setCountSize(Integer countSize) {
		this.countSize = countSize;
	}
}
