package com.cyyun.fm.focus.bean;

import com.cyyun.process.service.bean.FocusInfoBean;

public class FocusInfoView extends FocusInfoBean {
	private static final long serialVersionUID = 1L;

	private String levelName;
	private String levelColor;
	private Integer Appraise;
	private String sentiments;//褒贬
	private Integer relatedArticles;//相关文章
	private String warningRuleNames;
	
	public Integer getRelatedArticles() {
		return relatedArticles;
	}

	public void setRelatedArticles(Integer relatedArticles) {
		this.relatedArticles = relatedArticles;
	}

	public String getSentiments() {
		return sentiments;
	}

	public void setSentiments(String sentiments) {
		this.sentiments = sentiments;
	}

	public Integer getAppraise() {
		return Appraise;
	}

	public void setAppraise(Integer appraise) {
		Appraise = appraise;
	}

	public String getLevelColor() {
		return levelColor;
	}

	public void setLevelColor(String levelColor) {
		this.levelColor = levelColor;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getWarningRuleNames() {
		return warningRuleNames;
	}

	public void setWarningRuleNames(String warningRuleNames) {
		this.warningRuleNames = warningRuleNames;
	}
}