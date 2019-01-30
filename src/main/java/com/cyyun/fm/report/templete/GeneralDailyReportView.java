package com.cyyun.fm.report.templete;

import java.util.Date;
import java.util.List;

import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.fm.service.bean.StatCustTopicBean;
import com.cyyun.process.service.bean.FocusArticleInfoBean;

/**
 * <h3>日报视图</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class GeneralDailyReportView {

	private String custName;//客户名称
	private Date dailyDate;//日报日期
	private Integer articleTotal = 0;//监测文章总数
	private Integer newsTotal = 0;//新闻总数
	private Integer appTotal = 0;//app新闻总数
	private Integer weixinTotal = 0;//微信总数
	private Integer weiboTotal = 0;//微博总数
	private Integer forumTotal = 0;//论坛总数
	private Integer blogTotal = 0;//博客总数
	private Integer paperTotal = 0;//纸媒总数
	private Integer wendaTotal = 0;//问答总数
	private Integer shipinTotal = 0;//视频总数
	private Integer negativeTotal = 0;//负面总数
	private Integer positiveTotal = 0;//正面总数
	private Integer warningTotal = 0;//预警总数
	private List<FocusArticleInfoBean> focusArticleInfos;//预警文章
	private List<ArticleBean> focusArticles;//预警文章
	private List<ArticleBean> topArticles;//热点文章
	private List<StatCustTopicBean> statCustTopics;//热点专题

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public Date getDailyDate() {
		return dailyDate;
	}

	public void setDailyDate(Date dailyDate) {
		this.dailyDate = dailyDate;
	}

	public Integer getArticleTotal() {
		return articleTotal;
	}

	public void setArticleTotal(Integer articleTotal) {
		this.articleTotal = articleTotal;
	}

	public Integer getNewsTotal() {
		return newsTotal;
	}

	public void setNewsTotal(Integer newsTotal) {
		this.newsTotal = newsTotal;
	}

	public Integer getAppTotal() {
		return appTotal;
	}

	public void setAppTotal(Integer appTotal) {
		this.appTotal = appTotal;
	}

	public Integer getWeixinTotal() {
		return weixinTotal;
	}

	public void setWeixinTotal(Integer weixinTotal) {
		this.weixinTotal = weixinTotal;
	}

	public Integer getWeiboTotal() {
		return weiboTotal;
	}

	public void setWeiboTotal(Integer weiboTotal) {
		this.weiboTotal = weiboTotal;
	}

	public Integer getForumTotal() {
		return forumTotal;
	}

	public void setForumTotal(Integer forumTotal) {
		this.forumTotal = forumTotal;
	}

	public Integer getBlogTotal() {
		return blogTotal;
	}

	public void setBlogTotal(Integer blogTotal) {
		this.blogTotal = blogTotal;
	}

	public Integer getPaperTotal() {
		return paperTotal;
	}

	public void setPaperTotal(Integer paperTotal) {
		this.paperTotal = paperTotal;
	}

	public Integer getNegativeTotal() {
		return negativeTotal;
	}

	public void setNegativeTotal(Integer negativeTotal) {
		this.negativeTotal = negativeTotal;
	}

	public Integer getPositiveTotal() {
		return positiveTotal;
	}

	public void setPositiveTotal(Integer positiveTotal) {
		this.positiveTotal = positiveTotal;
	}

	public Integer getWarningTotal() {
		return warningTotal;
	}

	public void setWarningTotal(Integer warningTotal) {
		this.warningTotal = warningTotal;
	}

	public List<FocusArticleInfoBean> getFocusArticleInfos() {
		return focusArticleInfos;
	}

	public void setFocusArticleInfos(List<FocusArticleInfoBean> focusArticleInfos) {
		this.focusArticleInfos = focusArticleInfos;
	}

	public List<ArticleBean> getFocusArticles() {
		return focusArticles;
	}

	public void setFocusArticles(List<ArticleBean> focusArticles) {
		this.focusArticles = focusArticles;
	}

	public List<ArticleBean> getTopArticles() {
		return topArticles;
	}

	public void setTopArticles(List<ArticleBean> topArticles) {
		this.topArticles = topArticles;
	}

	public List<StatCustTopicBean> getStatCustTopics() {
		return statCustTopics;
	}

	public void setStatCustTopics(List<StatCustTopicBean> statCustTopics) {
		this.statCustTopics = statCustTopics;
	}
	public Integer getWendaTotal() {
		return wendaTotal;
	}

	public void setWendaTotal(Integer wendaTotal) {
		this.wendaTotal = wendaTotal;
	}

	public Integer getShipinTotal() {
		return shipinTotal;
	}

	public void setShipinTotal(Integer shipinTotal) {
		this.shipinTotal = shipinTotal;
	}
}
