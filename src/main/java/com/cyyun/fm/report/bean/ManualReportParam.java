package com.cyyun.fm.report.bean;
/** 
 * @description 人工报告的查询参数
 * @author  SuoLiang  
 * @version 2016年4月7日
 */
public class ManualReportParam {
	
	private String title;
	
	private String category;
	
	private String status;
	
	private String startTime;
	
	private String endTime;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
