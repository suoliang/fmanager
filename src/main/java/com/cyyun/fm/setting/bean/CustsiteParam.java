package com.cyyun.fm.setting.bean;

public class CustsiteParam {
	private String remark;//备注
	private Integer procStatus;//状态
	private Integer id;//站点id
	private Integer spiderSiteId;//采集站点ID
	private Integer MediaType;//媒体类型
	
	
	
	
	public Integer getMediaType() {
		return MediaType;
	}
	public void setMediaType(Integer mediaType) {
		MediaType = mediaType;
	}
	public Integer getSpiderSiteId() {
		return spiderSiteId;
	}
	public void setSpiderSiteId(Integer spiderSiteId) {
		this.spiderSiteId = spiderSiteId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getProcStatus() {
		return procStatus;
	}
	public void setProcStatus(Integer procStatus) {
		this.procStatus = procStatus;
	}
	
	
	
}
