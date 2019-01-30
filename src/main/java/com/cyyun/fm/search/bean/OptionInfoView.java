package com.cyyun.fm.search.bean;

public class OptionInfoView {
	private Integer id;
	private String name;
	private OptionInfoView parent;

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

	public OptionInfoView getParent() {
		return parent;
	}

	public void setParent(OptionInfoView parent) {
		this.parent = parent;
	}
}