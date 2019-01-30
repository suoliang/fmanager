package com.cyyun.fm.people.bean;

import java.util.Date;
import java.util.List;

import com.cyyun.process.service.bean.VirtualIdentityBean;

/*
 * 虚拟人物返回结果格式化bean
 */
public class TruePeopleView {
	
	private Integer id;//id
	private String realName;//真实姓名
	private String keywords;//关键字
	private String content;//简介
	private Integer status;//维护状态
	private String statusName;//维护状态名字
	//可选择项
	private String sex;//性别
	private Integer sexId;//性别id
	private String mobile;//电话
	private String email;//email
	private String qq;//qq
	private String wx;//wx
	private Date birthday;//生日
	private String job;//职业
	private String education;//学历名称
	private Integer educationId;//学历id
	private Integer createrId;//创建者
	private String createrIdName;//创建者名字
	private Integer categoryId;//分类id
	private String categoryName;//分类名字
	private Date createTime;//创建时间
	private Integer pageNo;//当前页
	private Integer virtualListSize;//对应虚拟身份集合
	
	
	public Integer getVirtualListSize() {
		return virtualListSize;
	}
	public void setVirtualListSize(Integer virtualListSize) {
		this.virtualListSize = virtualListSize;
	}
	public Integer getSexId() {
		return sexId;
	}
	public void setSexId(Integer sexId) {
		this.sexId = sexId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getEducationId() {
		return educationId;
	}
	public void setEducationId(Integer educationId) {
		this.educationId = educationId;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getWx() {
		return wx;
	}
	public void setWx(String wx) {
		this.wx = wx;
	}
	
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public Integer getCreaterId() {
		return createrId;
	}
	public void setCreaterId(Integer createrId) {
		this.createrId = createrId;
	}
	public String getCreaterIdName() {
		return createrIdName;
	}
	public void setCreaterIdName(String createrIdName) {
		this.createrIdName = createrIdName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
