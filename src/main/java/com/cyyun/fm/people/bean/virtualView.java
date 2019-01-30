package com.cyyun.fm.people.bean;

/*
 * 虚拟人物返回结果格式化bean
 */
public class virtualView {
	
	private Integer id;//虚拟人物id
	private String createrId;//来源
	private Integer mediaType;//媒体类型
	private String nickname;//昵称
	private String siteIdName;//站点名字
	private Integer siteId;//站点id
	private String content;//备注
	private String status;//维护状态名字
	private Integer statusId;//维护状态id
	private Integer currentPage;//当前页
	private String operationas;//显示修改--》系统授权不允许修改
	
	public String getOperationas() {
		return operationas;
	}
	public void setOperationas(String operationas) {
		this.operationas = operationas;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getStatusId() {
		return statusId;
	}
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreaterId() {
		return createrId;
	}
	public void setCreaterId(String createrId) {
		this.createrId = createrId;
	}
	public Integer getMediaType() {
		return mediaType;
	}
	public void setMediaType(Integer mediaType) {
		this.mediaType = mediaType;
	}
}
