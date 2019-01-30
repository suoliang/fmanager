package com.cyyun.fm.setting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.base.filter.FMContext;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.service.bean.BoardBean;
import com.cyyun.fm.setting.bean.BoardView;

/**
 * <h3>设置首页控制器支持</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Controller
@RequestMapping("/setting")
public class SettingHomepageController extends BaseController {

	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;

	@Autowired
	private SettingHomepageSupport support;

	@RequestMapping(value = { "homepage/list" })
	public ModelAndView list(String name) {
		try {
			PageInfoBean<BoardBean> pageInfo = support.queryBoard(FMContext.getCurrent().getUserId(), name, PAGE_NO, PAGE_SIZE);
			return view("/localsetting/homepageList").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询网页板块失败");
		}
	}

	@RequestMapping(value = { "homepage/query" })
	public ModelAndView query(String name, Integer currentpage, Integer pagesize) {
		try {
			PageInfoBean<BoardBean> pageInfo = support.queryBoard(FMContext.getCurrent().getUserId(), name, currentpage, pagesize);
			return view("/localsetting/homepageList-paging-board").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询网页板块失败");
		}
	}

	@RequestMapping(value = { "homepage/share" })
	@ResponseBody
	public MessageBean share(Integer boardId, Integer[] userIds) {
		try {
			BoardView board = support.shareBoard(boardId, userIds);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功共享用户", board);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "共享用户失败");
		}
	}
}