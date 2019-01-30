package com.cyyun.base.task;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <h3>任务项</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class SyncStatTaskItem implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	private String intervalName;
	private String taskName;

	public void execute() {
		SyncStatTimeInterval timeInterval = applicationContext.getBean(intervalName, SyncStatTimeInterval.class);
		SyncStatTask syncStatTask = applicationContext.getBean(taskName, SyncStatTask.class);
		syncStatTask.execute(timeInterval.calculate());
	}

	public String getIntervalName() {
		return intervalName;
	}

	public void setIntervalName(String intervalName) {
		this.intervalName = intervalName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
