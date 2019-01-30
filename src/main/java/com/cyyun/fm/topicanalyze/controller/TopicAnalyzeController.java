package com.cyyun.fm.topicanalyze.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.common.core.base.BaseController;

/**
 * <h3>报告</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Controller
@RequestMapping(value = { "topicanalyze" })
public class TopicAnalyzeController extends BaseController {

	@Autowired
	private TopicAnalyzeSupport support;

	@RequestMapping(value = { "index" })
	public ModelAndView index() {
		return view("/topicanalyze/index");
	}
}