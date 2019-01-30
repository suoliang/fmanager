package com.cyyun.fm.setting.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.base.service.bean.KeywordBean;
import com.cyyun.base.service.exception.KeywordServiceException;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;

/**
 * <h3>监测设置控制器</h3>
 * @author LIUJUNWU
 */
@Controller
@RequestMapping("/setting")
public class SettingMonitorController extends BaseController {

	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;

	@Autowired
	private SettingMonitorSupport support;

	@RequestMapping(value = { "monitor/index" })
	public ModelAndView index(KeywordBean keywordBean, Integer pageNo, Integer pageSize) {
		try {
			if (pageNo == null) {
				pageNo = PAGE_NO;
			}
			if (pageSize == null) {
				pageSize = PAGE_SIZE;
			}
			PageInfoBean<KeywordBean> pageInfo = support.queryMonitorKeywordByPage(keywordBean, pageNo, pageSize);
			return view("/localsetting/monitor-index").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询监测关键词失败");
		}
	}

	@RequestMapping(value = { "monitor/keyword/list" })
	public ModelAndView listRule(KeywordBean keywordBean, Integer pageNo, Integer pageSize) {
		try {
			PageInfoBean<KeywordBean> pageInfo = support.queryMonitorKeywordByPage(keywordBean, pageNo, pageSize);
			return view("/localsetting/monitor-paging-keywords").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载监测关键词失败");
		}
	}

	@RequestMapping(value = { "monitor/keyword/find" })
	@ResponseBody
	public MessageBean findRule(Integer kid) {
		try {
			KeywordBean keywordBean = null;
			if(kid != null){
				keywordBean = support.findMonitorKeyword(kid);
			}
			return buildMessage(MESSAGE_TYPE_SUCCESS, "查找监测关键词成功", keywordBean);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "查找监测关键词失败");
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "monitor/keyword/update" })
	@ResponseBody
	public MessageBean updateRule(HttpServletRequest request) {
		try {
			support.updateMonitorKeyword(request.getParameterMap());
			return buildMessage(MESSAGE_TYPE_SUCCESS, "保存监测关键词成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "保存监测关键词失败");
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "monitor/keyword/checkMonitorKeywordIsExist" })
	@ResponseBody
	public MessageBean checkMonitorKeywordIsExist(HttpServletRequest request) {
		try {
			support.checkMonitorKeywordIsExist(request.getParameterMap());
			return buildMessage(MESSAGE_TYPE_SUCCESS, "检查监测关键词是否存在成功");
		} catch (KeywordServiceException e) {
			if ("monitorKeywordExisting.error".equals(e.getErrorCode())) {
				return buildMessage(MESSAGE_TYPE_QUESTION, e.getErrorMsg());
			} else {
				log.error(e.getMessage(), e);
				return buildMessage(MESSAGE_TYPE_ERROR, "检查监测关键词是否存在失败");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检查监测关键词是否存在失败");
		}
	}
	
	@RequestMapping(value = { "monitor/keyword/delete" })
	@ResponseBody
	public MessageBean deleteRule(Integer kid) {
		try {
			support.deleteMonitorKeyword(kid);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "删除监测关键词成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "删除监测关键词失败");
		}
	}
}