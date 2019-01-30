package com.cyyun.fm.analyze.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.util.JsonUtil;
import com.cyyun.fm.analyze.bean.LineChartDataView;
import com.cyyun.fm.service.bean.StatCustTopicBean;
import com.cyyun.fm.service.bean.StatWebsiteBean;

/**
 * <h3>首页控制器</h3>
 * 
 * @author 
 * @version 1.0.0
 */
@Controller
@RequestMapping(value = { "analyze/offline" })
public class OfflineAnalyzeController extends BaseController {

	@Autowired
	private OfflineAnalyzeSupport support;

	@RequestMapping(value = { "generalize/index" })
	public ModelAndView index() {
		try {
			Date endDate = Calendar.getInstance().getTime();
			Date startDate = DateUtils.addDays(endDate, -6);
			return view("/analyze/offline-generalize-index").addObject("startDate", DateFormatUtils.format(startDate, "yyy-MM-dd")).addObject("endDate", DateFormatUtils.format(endDate, "yyy-MM-dd"));

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "概况分析加载失败");
		}
	}

	@RequestMapping(value = { "generalize/situation" })
	public ModelAndView getSituation(String startDate, String endDate) {
		try {
			Map<String, Integer> situation = support.getSituation(startDate, endDate);
			ModelAndView view = view("/analyze/offline-generalize-situation-chart");
			view.addObject("situation", situation);
			if (situation != null) {
				Integer total = 0;
				for (Entry<String, Integer> entry : situation.entrySet()) {
					total += entry.getValue();
				}
				view.addObject("total", total);
				view.addObject("data", JsonUtil.toJson(situation.entrySet()));
			}
			return view;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "总体概况加载失败");
		}
	}

	@RequestMapping(value = { "generalize/tendency" })
	public ModelAndView getTendency(String startDate, String endDate) {
		try {
			LineChartDataView tendency = support.getTendency(startDate, endDate);
			ModelAndView view = view("/analyze/offline-generalize-tendency-chart");
			if (tendency != null) {
				view.addObject("data", JsonUtil.toJson(tendency));
			}
			return view;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "媒体趋势加载失败");
		}
	}

	@RequestMapping(value = { "generalize/statCustTopic" })
	public ModelAndView getStatCustTopic(String startDate, String endDate) {
		try {
			List<StatCustTopicBean> statCustTopics = support.getStatCustTopic(startDate, endDate);
			return view("/analyze/generalize-statCustTopic-list").addObject("statCustTopics", statCustTopics);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "站点统计加载失败");
		}
	}

	@RequestMapping(value = { "generalize/statWebsite" })
	public ModelAndView getStatWebsite(String startDate, String endDate) {
		try {
			Map<Integer, List<StatWebsiteBean>> statWebsiteMap = support.getStatWebsite(startDate, endDate);
			return view("/analyze/offline-generalize-statWebsite-list").addObject("statWebsiteMap", statWebsiteMap);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "站点统计加载失败");
		}
	}
}