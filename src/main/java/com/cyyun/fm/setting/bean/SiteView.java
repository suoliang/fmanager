package com.cyyun.fm.setting.bean;

import java.util.ArrayList;
import java.util.List;

import com.cyyun.base.util.PropertiesUtil;
import com.cyyun.common.core.base.BaseQueryBean;

/*
 * 站点返回结果ConditionParam
 */
public class SiteView extends BaseQueryBean {
	
	private Integer dataId;//站点分类,数据ID
	private String siteType;//站点分类
	private String type;
	private String creater;//来源
	private String name;//站点名字
	private String url;//站点地址
	private Integer id;//站点id
	private Integer mediaType;//媒体类型
	private Integer procStatus;//处理状态
	
	private String classification;//显示分类--》系统授权不允许修改
	
	private String procStatuStr;//处理状态
	
	private String customerName;//客户名称
	
	private List<Integer> custIds = new ArrayList<Integer>();//客户id集合
	
	public List<Integer> getCustIds() {
		return custIds;
	}
	public void setCustIds(List<Integer> custIds) {
		this.custIds = custIds;
	}
	public Integer getProcStatus() {
		return procStatus;
	}
	public void setProcStatus(Integer procStatus) {
		this.procStatus = procStatus;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getProcStatuStr() {
		return procStatuStr;
	}
	/**
	 * 设置页面处理状态文字显示
	 * @param procStatus
	 */
	public void setProcStatusStrAndClassIfication(Integer procStatus){
		if(procStatus != null){
			switch (procStatus) {
			case 0:
				this.setProcStatuStr(PropertiesUtil.getValue("unprocessed"));
				this.setClassification(PropertiesUtil.getValue("classification"));
				break;
			case 1:
				this.setProcStatuStr(PropertiesUtil.getValue("processed"));
				this.setClassification("");
				break;
			case 2:
				this.setProcStatuStr(PropertiesUtil.getValue("processing"));
				this.setClassification("");
				break;
			default:
				this.setProcStatuStr(PropertiesUtil.getValue("not_processing"));
				this.setClassification("");
			}
		}
	}
	
	public void setProcStatuStr(String procStatuStr) {
		this.procStatuStr = procStatuStr;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public Integer getMediaType() {
		return mediaType;
	}
	public void setMediaType(Integer mediaType) {
		this.mediaType = mediaType;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getDataId() {
		return dataId;
	}
	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public String getSiteType() {
		return siteType;
	}
	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
