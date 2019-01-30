package com.cyyun.fm.setting.bean;

import java.util.Date;

/**
 * <h3>CustTopicInitTaskBean</h3>
 * 
 * @author zhangliao
 * @version 1.0.0
 * 
 */
public class CustTopicInitTaskBean {

	/**
	 * id
	 */
	private Integer id;
	
	/**
	 * type:	1 - 微博溯源   2 - 主题文章导出	3 - 客户专题初始化
	 */
	private Short type;

	/**
	 * 0 - 停用 1 - 启用
	 */
	private Short status;
	
	/**
	 * name
	 */
	private String name;

	/**
	 * 主题描述
	 */
	private String content;
	
	/**
	 * 结果
	 */
	private String result;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 修改时间
	 */
	private Date updateTime;

	/**
	 * 客户ID
	 */
	private String cids;
	
	/**
	 * 客户名称
	 */
	private String custNames;
	
	/**
	 * 客户专题ID
	 */
	private String labelIds;
	
	/**
	 * 客户专题名称
	 */
	private String custTopicNames;
	
	/**
	 * 开始时间 <br>
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
	private String startTime;

	/**
	 * 结束时间 <br>
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
	private String endTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCids() {
		return cids;
	}

	public void setCids(String cids) {
		this.cids = cids;
	}

	public String getCustNames() {
		return custNames;
	}

	public void setCustNames(String custNames) {
		this.custNames = custNames;
	}

	public String getLabelIds() {
		return labelIds;
	}

	public void setLabelIds(String labelIds) {
		this.labelIds = labelIds;
	}


	public String getCustTopicNames() {
		return custTopicNames;
	}

	public void setCustTopicNames(String custTopicNames) {
		this.custTopicNames = custTopicNames;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	
}