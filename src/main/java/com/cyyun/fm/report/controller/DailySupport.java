package com.cyyun.fm.report.controller;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.cyyun.base.filter.FMContext;
import com.cyyun.fm.report.templete.DailyReportTemplete;
import com.cyyun.fm.service.FmReportService;
import com.cyyun.fm.service.bean.ReportTemplateBean;

@Component
public class DailySupport implements ApplicationContextAware {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private FmReportService fmReportService;

	public String generateHtml(String dateStr, HttpServletRequest request) throws Exception {
		ReportTemplateBean reportTemplate = fmReportService.findReportTempleteByCustId(FMContext.getCurrent().getCustomerId());
		DailyReportTemplete dailyReportTemplete = applicationContext.getBean(reportTemplate.getCode(), DailyReportTemplete.class);

		Date date = Calendar.getInstance().getTime();
		if (StringUtils.isNoneBlank(dateStr)) {
			date = DateUtils.parseDate(dateStr, "yyyy-MM-dd");
		}

		StringWriter writer = new StringWriter();
		dailyReportTemplete.generateHtml(FMContext.getCurrent().getUserId(), date, writer, request);
		return writer.toString();
	}

	public void generateDoc(String dateStr, Writer writer, HttpServletRequest request) throws Exception {
		ReportTemplateBean reportTemplate = fmReportService.findReportTempleteByCustId(FMContext.getCurrent().getCustomerId());
		DailyReportTemplete dailyReportTemplete = applicationContext.getBean(reportTemplate.getCode(), DailyReportTemplete.class);

		Date date = Calendar.getInstance().getTime();
		if (StringUtils.isNoneBlank(dateStr)) {
			date = DateUtils.parseDate(dateStr, "yyyy-MM-dd");
		}

		dailyReportTemplete.generateDoc(FMContext.getCurrent().getUserId(), date, writer, request);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}