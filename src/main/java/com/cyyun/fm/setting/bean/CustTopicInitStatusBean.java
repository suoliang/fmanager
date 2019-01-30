package com.cyyun.fm.setting.bean;

import com.cyyun.base.service.bean.CustTopicBean;

/** 
 * @author  SuoLiang  
 * @version 2016年5月27日
 */
public class CustTopicInitStatusBean extends CustTopicBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1756456227959946300L;
	/**
	 * FM初始化专题任务状态
	 */
	private Integer fmTaskStatus;

	public Integer getFmTaskStatus() {
		return fmTaskStatus;
	}
	
	public void setFmTaskStatus(Integer fmTaskStatus) {
		this.fmTaskStatus = fmTaskStatus;
	}
	
}
