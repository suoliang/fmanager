package com.cyyun.fm.report.templete;

import java.io.Writer;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;


/**
 * <h3>日报模版</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public interface DailyReportTemplete {

	public void generateHtml(Integer userId, Date date, Writer writer, HttpServletRequest request);

	public void generateDoc(Integer userId, Date date, Writer writer, HttpServletRequest request);
}