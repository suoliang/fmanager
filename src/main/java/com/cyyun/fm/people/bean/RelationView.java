package com.cyyun.fm.people.bean;
/*
 * 身份关联页面返回结果格式化bean
 */
public class RelationView {
	private Integer tid;//真实人物id
	private String trueName;//真实身份
	private Integer vid;//虚拟人物id
	private String virtualName;//虚拟身份
	private String siteIdName;//站点名字
	private Integer siteId;//站点id
	private Integer currentPage;//当前页
	public String getTrueName() {
		return trueName;
	}
	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}
	public String getVirtualName() {
		return virtualName;
	}
	public void setVirtualName(String virtualName) {
		this.virtualName = virtualName;
	}
	public String getSiteIdName() {
		return siteIdName;
	}
	public void setSiteIdName(String siteIdName) {
		this.siteIdName = siteIdName;
	}
	public Integer getSiteId() {
		return siteId;
	}
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public Integer getVid() {
		return vid;
	}
	public void setVid(Integer vid) {
		this.vid = vid;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	
	
	
}
