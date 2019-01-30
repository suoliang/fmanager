package com.cyyun.base.task;

import java.util.List;

/**
 * <h3>时间间隔</h3>
 * <p></p>
 * @author GUOQIANG
 * @version 1.0.0
 */
public interface SyncStatTimeInterval {
	public List<SyncStatTimePoint> calculate();
}