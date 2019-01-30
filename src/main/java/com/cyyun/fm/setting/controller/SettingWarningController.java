package com.cyyun.fm.setting.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.setting.bean.BatchSaveRuleParam;
import com.cyyun.process.service.bean.WarningRuleBean;

/**
 * <h3>设置预警控制器支持</h3>
 * 
 * @author chenqq
 * @version 1.0.0
 */
@Controller
@RequestMapping("/setting")
public class SettingWarningController extends BaseController {

	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;

	@Autowired
	private SettingWarningSupport support;

	@RequestMapping(value = { "warning/index" })
	public ModelAndView index(WarningRuleBean warningRule, Integer pageNo, Integer pageSize) {
		try {
			if (pageNo == null) {
				pageNo = PAGE_NO;
			}
			if (pageSize == null) {
				pageSize = PAGE_SIZE;
			}
			PageInfoBean<WarningRuleBean> pageInfo = support.queryWarningRuleByPage(warningRule, pageNo, pageSize);
			return view("/localsetting/warning-index").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询预警规则失败");
		}
	}

	@RequestMapping(value = { "warning/rule/list" })
	public ModelAndView listRule(WarningRuleBean warningRule, Integer pageNo, Integer pageSize) {
		try {
			PageInfoBean<WarningRuleBean> pageInfo = support.queryWarningRuleByPage(warningRule, pageNo, pageSize);
			return view("/localsetting/warning-index-paging-rule").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载预警规则失败");
		}
	}

	@RequestMapping(value = { "warning/rule/find" })
	@ResponseBody
	public MessageBean findRule(Integer ruleId) {
		try {
			WarningRuleBean warningRule = null;
			if(ruleId != null){
				warningRule = support.findRule(ruleId);
			}
			return buildMessage(MESSAGE_TYPE_SUCCESS, "查找规则成功", warningRule);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "查找规则失败");
		}
	}

	@RequestMapping(value = { "warning/rule/input" })
	public ModelAndView inputRule(Integer ruleId) {
		try {
			WarningRuleBean rule = support.findRule(ruleId);
			return view("/localsetting/warning-rule-input").addObject("rule", rule);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载规则编辑页失败");
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "warning/rule/save" })
	@ResponseBody
	public MessageBean saveRule(HttpServletRequest request) {
		try {
			support.saveWarningRule(request.getParameterMap());
			return buildMessage(MESSAGE_TYPE_SUCCESS, "保存规则成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "保存规则失败");
		}
	}

	@RequestMapping(value = { "warning/rule/changeRuleStatus" })
	@ResponseBody
	public MessageBean changeRuleStatus(Integer ruleId, Integer stauts) {
		try {
			support.changeRuleStatus(ruleId, stauts);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "保存规则成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "保存规则失败");
		}
	}

	@RequestMapping(value = { "warning/rule/batchinput" })
	public ModelAndView batchInputRule() {
		try {
			return view("/localsetting/warning-rule-batchinput");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载规则新增页失败");
		}
	}

	@RequestMapping(value = { "warning/rule/batchsave" })
	@ResponseBody
	public MessageBean batchSaveRule(BatchSaveRuleParam param) {
		try {
			support.saveRule(param.getRules());
			return buildMessage(MESSAGE_TYPE_SUCCESS, "保存预警关键词成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "保存预警关键词失败");
		}
	}

	@RequestMapping(value = { "warning/rule/sendMessageToMQ" })
	@ResponseBody
	public MessageBean sendMessageToMQ(String guid, Integer cid) {
		try {
			support.sendMessage(guid, cid);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "发送预警消息成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "发送预警消息失败");
		}
	}
}