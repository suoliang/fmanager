package com.cyyun.base.task;

import java.util.Date;

/**
 * <h3>时间点</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class SyncStatTimePoint {
	private Date beginTime;
	private Date endTime;

	public SyncStatTimePoint() {
	}

	public SyncStatTimePoint(Date beginTime, Date endTime) {
		this.beginTime = beginTime;
		this.endTime = endTime;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}