package com.cyyun.fm.export.controller;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.service.TaskService;
import com.cyyun.base.service.bean.TaskBean;
import com.cyyun.base.service.bean.query.TaskQueryBean;
import com.cyyun.base.service.exception.TaskServiceException;
import com.cyyun.base.util.DateFormatFactory;
import com.cyyun.base.util.PropertiesUtil;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;

@Component
public class ExportSupport {
	
	@Autowired
	private TaskService taskService;
	
	
	public PageInfoBean<TaskBean> queryTaskByPage(TaskQueryBean query) throws TaskServiceException  {
		PageInfoBean<TaskBean> pageInfo = taskService.queryTasksByPage(query);
		List<TaskBean> taskBeanList = pageInfo.getData();
		List<TaskBean> resultList = new ArrayList<TaskBean>();
		if (CollectionUtils.isNotEmpty(taskBeanList)) {
			for (TaskBean taskBean : taskBeanList) {//剔除删除状态的任务   100 - 已删除(fm3.0数据特有状态)
				if (taskBean.getStatus()!=100) {
					resultList.add(taskBean);
				}
			}
		}
		pageInfo.setData(resultList);
		return pageInfo;
	}
	
	public Integer addTask(TaskQueryBean query, HttpServletRequest request) throws TaskServiceException{
		TaskBean taskBean = BeanUtil.copy(query, TaskBean.class);
		SimpleDateFormat dft = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern2);
		//task type值约定
		//11 - FM搜索数据导出Excel 12 - FM预警数据导出Excel 13 - FM专题数据导出Excel
		//14 - FM搜索数据导出Word 15 - FM预警数据导出Word 16 - FM专题数据导出Word
		String fileType=query.getType()==11 || query.getType()==12 || query.getType()==13 ? ".xls" : query.getType()==14 || query.getType()==15 || query.getType()==16 ? ".doc" : "";
		taskBean.setName(dft.format(new Date()) + fileType);
		taskBean.setStatus((short) 1);//1 - 处理中  2 - 成功  3 - 失败  100 - 已删除(fm3.0数据特有状态)
		taskBean.setRefId(0);
		taskBean.setContent("FM3.0");
		taskBean.setCreateTime(new Date());
		taskBean.setUpdateTime(new Date());
		request.getSession().setAttribute("fileName", taskBean.getName());
		return taskService.addTask(taskBean);
	}
	
	public void updateTaskStatus(TaskQueryBean query) throws TaskServiceException{
		TaskBean taskBean = BeanUtil.copy(query, TaskBean.class);
		taskBean.setUpdateTime(new Date());
		taskService.updateTask(taskBean);
	}
	
	public void deleteTaskFile(TaskQueryBean query) {
		String exportDocpath = PropertiesUtil.getValue("exportDocpath");
		String taskFileUrl = exportDocpath+"/"+query.getName();
		File taskFile = new File(taskFileUrl); 
		if(taskFile.exists()){
			taskFile.delete(); 
		}
	}
	
}
