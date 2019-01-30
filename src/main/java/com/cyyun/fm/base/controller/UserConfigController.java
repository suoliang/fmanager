package com.cyyun.fm.base.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyyun.base.filter.FMContext;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.base.BaseException;
import com.cyyun.common.core.bean.MessageBean;

/**
 * <h3>用户配置控制器</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Controller
@RequestMapping("/userconfig")
public class UserConfigController extends BaseController {

	@Autowired
	private UserConfigSupport support;

	@RequestMapping(value = { "ue/set" })
	@ResponseBody
	public MessageBean setUEConfig(String key, String value, HttpSession session) {
		try {
			if ("theme".equals(key)) {
				session.setAttribute("theme", value);
				support.set(FMContext.getCurrent().getUserId(), "主题", "theme", value);
			}
			return buildMessage(MESSAGE_TYPE_SUCCESS, "配置成功");
		} catch (BaseException e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "配置失败");
		}
	}
}