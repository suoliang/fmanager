package com.cyyun.fm.base.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cyyun.authim.service.bean.MenuBean;
import com.cyyun.common.core.constant.SessionConstant;

@Controller(value = "indexController")
@RequestMapping(value = { "/" })
@SessionAttributes(SessionConstant.MENUS)
public class IndexController {

	@RequestMapping(value = { "index" })
	public String index(HttpServletRequest request, @ModelAttribute(SessionConstant.MENUS) List<MenuBean> menus) {
		String url = "/home/index.htm";
//		if (CollectionUtils.isNotEmpty(menus)) {
//			MenuBean menu = menus.get(0);
//			url = menu.getUrl() != null ? menu.getUrl() : url;
//		}
		return "redirect:" + url;
	}

}