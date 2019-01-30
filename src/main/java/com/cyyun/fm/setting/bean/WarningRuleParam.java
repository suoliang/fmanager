package com.cyyun.fm.setting.bean;

/**
 * <h3>规则</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class WarningRuleParam {
	private Integer id;
	private String name;
	private String keyword;
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

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}