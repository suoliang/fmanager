package com.cyyun.base.task;

import java.util.List;

/**
 * <h3>异步任务</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public interface SyncStatTask {
	public void execute(List<SyncStatTimePoint> points);
}