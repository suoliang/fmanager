package com.cyyun.fm.homepage.task;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <h3>任务项</h3>
 * 
 * @author LIUJUNWU
 * @version 1.0.0
 */
public class QueryPlatformTalNumTaskItem implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	private String taskName;

	public void execute() {
		QueryStatTask syncStatTask = applicationContext.getBean(taskName, QueryStatTask.class);
		syncStatTask.execute();
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
