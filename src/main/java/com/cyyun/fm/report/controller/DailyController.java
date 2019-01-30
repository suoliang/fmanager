package com.cyyun.fm.report.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.base.filter.FMContext;
import com.cyyun.common.core.base.BaseController;

/**
 * <h3>日报</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Controller
@RequestMapping(value = { "report" })
public class DailyController extends BaseController {

	@Autowired
	private DailySupport support;

	@RequestMapping(value = { "daily/index" })
	public ModelAndView dailyIndex() {
		try {
			return view("/report/daily-index");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载日报失败");
		}
	}

	@RequestMapping(value = { "daily/html" })
	public ModelAndView dailyHtml(String date, HttpServletRequest request) {
		try {
			String page = support.generateHtml(date,request);
			return view("/report/daily-html").addObject("page", page);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载日报失败");
		}
	}

	@RequestMapping(value = { "daily/doc" })
	public void dailyDoc(String date, HttpServletResponse response, HttpServletRequest request) {
		try {
			String date2 = DateFormatUtils.format(DateUtils.parseDate(date, "yyyy-MM-dd"), "yyyyMMdd");
			response.addHeader("Content-Disposition", "attachment;filename=" +  new String((FMContext.getCurrent().getCustomer().getName() + date2 + "日报").getBytes("gb2312"),"ISO8859-1") + ".doc");
			response.setCharacterEncoding("utf-8");
			support.generateDoc(date, response.getWriter(), request);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}