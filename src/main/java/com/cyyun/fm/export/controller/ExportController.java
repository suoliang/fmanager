/**
 *
 */
package com.cyyun.fm.export.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyyun.base.service.bean.TaskBean;
import com.cyyun.base.service.bean.query.TaskQueryBean;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;



/**
 * <h3>导出文件</h3>
 * 
 * @author LIUJUNWU
 * @version 1.0.0
 */
@Controller
@RequestMapping("/export")
public class ExportController extends BaseController
{
	
	@Autowired
	private ExportSupport support;

	
	/**
	 * <h3>查找任务</h3>
	 */
	@RequestMapping("searchTask")
	@ResponseBody
	public MessageBean searchTask(TaskQueryBean query) {
		try {
			List<TaskBean> taskList=support.queryTaskByPage(query).getData();
			return buildMessage(MESSAGE_TYPE_SUCCESS, "检索任务列表成功", taskList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索任务列表失败");
		}
	}
	
	/**
	 * <h3>新增任务</h3>
	 */
	@RequestMapping("addTask")
	@ResponseBody
	public MessageBean addTask(TaskQueryBean query, HttpServletRequest request) {
		try {
			Integer id = support.addTask(query, request);
			query.setId(id);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "新增任务成功", query);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "新增任务失败");
		}
	}
	
	/**
	 * <h3>修改任务状态</h3>
	 */
	@RequestMapping("updateTaskStatus")
	@ResponseBody
	public MessageBean updateTaskStatus(TaskQueryBean query) {
		try {
			support.updateTaskStatus(query);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "修改任务状态成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "修改任务状态失败");
		}
	}
	
	/**
	 * <h3>删除任务对应文件</h3>
	 */
	@RequestMapping("deleteTaskFile")
	@ResponseBody
	public MessageBean deleteTaskFile(TaskQueryBean query) {
		try {
			support.deleteTaskFile(query);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "删除任文件成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "删除任文件失败");
		}
	}
	
}
