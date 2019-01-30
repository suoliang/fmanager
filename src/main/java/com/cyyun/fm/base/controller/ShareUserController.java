package com.cyyun.fm.base.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyyun.authim.service.bean.OrganizationBean;
import com.cyyun.authim.service.bean.UserBean;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.base.BaseException;
import com.cyyun.common.core.bean.MessageBean;

/**
 * <h3>共享用户控制器</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Controller
@RequestMapping("/shareuser")
public class ShareUserController extends BaseController {

	@Autowired
	private ShareUserSupport support;

	@RequestMapping(value = { "searchUser" })
	@ResponseBody
	public MessageBean searchUser(String keyword) {
		try {
			List<UserBean> users = support.searchUser(keyword);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "查询共享用户成功", users);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "查询共享用户失败");
		}
	}

	@RequestMapping(value = { "listUser" })
	@ResponseBody
	public MessageBean listUser(Integer[] orgId) {
		try {
			List<UserBean> users = support.listUser(orgId);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "查询共享用户成功", users);
		} catch (BaseException e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "查询共享用户失败");
		}
	}

	@RequestMapping(value = { "listOrganization" })
	@ResponseBody
	public MessageBean listOrganization(Integer orgId) {
		try {
			List<OrganizationBean> organizations = support.listOrganization(orgId);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "查询共享用户成功", organizations);
		} catch (BaseException e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "查询共享用户失败");
		}
	}
}