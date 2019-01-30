package com.cyyun.fm.analyze.controller;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ConstantService;
import com.cyyun.base.service.bean.ArticleStatisticBean;
import com.cyyun.base.service.bean.ConstantBean;
import com.cyyun.base.service.bean.CustTopicBean;
import com.cyyun.base.service.exception.CustTopicServiceException;
import com.cyyun.base.task.SyncStatTimeInterval;
import com.cyyun.base.util.CyyunDateUtils;
import com.cyyun.base.util.GetCurrentDate;
import com.cyyun.base.util.MapKeyComparator;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.util.JsonUtil;
import com.cyyun.fm.analyze.bean.AnalyzeCountBean;
import com.cyyun.fm.analyze.bean.LineChartDataView;
import com.cyyun.fm.analyze.task.SyncStatMediaTask;
import com.cyyun.fm.analyze.task.SyncStatWebsiteTask;
import com.cyyun.fm.custtopic.bean.CustTopicView;
import com.cyyun.fm.search.bean.OptionInfoView;
import com.cyyun.fm.service.QueryLogService;
import com.cyyun.fm.service.bean.StatCustTopicBean;
import com.cyyun.fm.service.bean.StatWebsiteBean;

/**
 * <h3>首页控制器</h3>
 * 
 * @author 
 * @version 1.0.0
 */
@Controller
@RequestMapping(value = { "analyze" })
public class AnalyzeController extends BaseController {

	@Autowired
	private AnalyzeSupport support;

	@Autowired
	private SyncStatMediaTask statMediaTask;

	@Autowired
	private SyncStatWebsiteTask statWebsiteTask;
	
	@Autowired
	private ConstantService constantService;
	
	@Autowired
	private QueryLogService queryLogService;

	@Autowired
	@Qualifier("intervalHourInOneMonth")
	private SyncStatTimeInterval timeIntervalForMedia;

	@Autowired
	@Qualifier("intervalDayInMonth")
	private SyncStatTimeInterval timeIntervalForWebsite;

	@RequestMapping(value = { "generalize/syncStatMedia" })
	public ModelAndView syncStatMedia() {
		statMediaTask.execute(timeIntervalForMedia.calculate());
		return message(MESSAGE_TYPE_ERROR, "同步媒体测试");
	}

	@RequestMapping(value = { "generalize/syncStatWebsite" })
	public ModelAndView syncStatWebsite() {
		statWebsiteTask.execute(timeIntervalForWebsite.calculate());
		return message(MESSAGE_TYPE_ERROR, "同步站点测试");
	}
	
	/**
	 * 首页柱状图展示
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = { "situation/homeBar" })
	public ModelAndView getHomePageSituation() {
		try {
			String startDate = "";
			String endDate = "";
			String pattern = "yyyy-MM-dd";
			
			Calendar cal = Calendar.getInstance(Locale.CHINA);
			endDate = DateFormatUtils.format(cal.getTime(), pattern);
			
			cal.add(Calendar.DATE, -6);// 一周内
			startDate = DateFormatUtils.format(cal.getTime(), pattern);
			
			Long startTime = System.currentTimeMillis();
			Map<String, Long> situation =MapKeyComparator.sortMapByKey(support.getSituation(startDate, endDate, null));
			Long situationEndTime = System.currentTimeMillis();
			Long total = 0L;
			for (Entry<String, Long> entry : situation.entrySet()) {
				total += entry.getValue();
			}
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------首页来源分布查询耗时#"+(situationEndTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((situationEndTime-startTime), "SituationArticleNo", 3000L);
			return view("/analyze/situation-board-view")
					.addObject("situation", situation)
					.addObject("total", total)
					.addObject("data", JsonUtil.toJson(situation.entrySet()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "首页总体概况加载失败");
		}
	}
	
	/**
	 * 首页里媒体趋势条形图展示
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = { "tendency/homeLine" })
	public ModelAndView getTendency() {
		try {
			String startDate = "";
			String endDate = "";
			String pattern = "yyyy-MM-dd";
			
			Calendar cal = Calendar.getInstance(Locale.CHINA);
			endDate = DateFormatUtils.format(cal.getTime(), pattern);
			
			cal.add(Calendar.DATE, -6);// 一周内
			startDate = DateFormatUtils.format(cal.getTime(), pattern);
			
			Long startTime = System.currentTimeMillis();
			LineChartDataView tendency = support.getTendency(startDate, endDate, null);
			List<String> axisName = tendency.getAxisName();
			List<String> ddList = new ArrayList<String>();
			/**控制日期MM-dd*/
			for (String string : axisName) {
				String[] split = string.split("\\-");
				if (split.length != 0) {
					String dd= split[split.length-2]+"-"+split[split.length-1];
					ddList.add(dd);
				}
			}
			tendency.setAxisName(ddList);
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------首页媒体趋势查询耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "MediaTendency", 3000L);
			return view("/analyze/tendency-board-view").addObject("data", JsonUtil.toJson(tendency));

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "首页媒体趋势加载失败");
		}
	}
	
	/**
	 * <h3>检索专题</h3>
	 */
	@RequestMapping("topic/list")
	@ResponseBody
	public MessageBean listTopic(Integer parentId) {
		try {
			List<CustTopicBean> topics = support.listTopic(null);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", topics);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索专题失败");
		}
	}

	/**
	 * <h3>检索专题(父级联)</h3>
	 */
	@RequestMapping("topic/listTopicOptionInfo")
	@ResponseBody
	public MessageBean listTopicOptionInfo(Integer[] ids) {
		try {
			List<OptionInfoView> options = support.listTopicOptionInfo(ids);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", options);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索专题(父级联)失败");
		}
	}
	
	@ResponseBody
	@RequestMapping("topic/getAllSearchTopic") 
	public MessageBean getAllSearchTopic(String keywords) {
		try {
			List<CustTopicView> searchParentTopic = support.getAllSearchTopic(keywords);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", searchParentTopic);
		} catch (CustTopicServiceException e) {
			e.printStackTrace();
			log.error("检索专题出错", e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索专题失败");
		}
	}

	@RequestMapping(value = { "generalize/index" })
	public ModelAndView index() {
		try {
			String forwardOneMonth = CyyunDateUtils.forwardOneMonthToString(new Date(), FMConstant.DATE_FOMAT_YYYYMMDD);
			Date endDate = Calendar.getInstance().getTime();
			/**默认查询条件为今日*/
			//Date startDate = DateUtils.addDays(endDate, -6);
			
			return view("/analyze/generalize-index").
									addObject("startDate",DateFormatUtils.format(endDate, "yyy-MM-dd")).
									addObject("endDate", DateFormatUtils.format(endDate, "yyy-MM-dd")).
									addObject("forwardOneMonth", forwardOneMonth)
									;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "概况分析加载失败");
		}
	}
	
	
	
	@RequestMapping(value = { "generalize/situation" })
	public ModelAndView getSituation(String startDate, String endDate, Integer[] topic) {
		try {
			Long startTime = System.currentTimeMillis();
			Map<String, Long> situation =MapKeyComparator.sortMapByKey(support.getSituation(startDate, endDate, topic));
			Long situationEndTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------总体概况文章数查询耗时#"+(situationEndTime-startTime)+"#毫秒");
			//Map<String, Long> readInMedia = support.getReadInMedia(startDate, endDate, topic);
			//Long readInMediaEndTime = System.currentTimeMillis();
			//System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
			//		"("+FMContext.getLoginUser().getId()+")"+"#----------总体概况阅读数查询耗时#"+(readInMediaEndTime-situationEndTime)+"#毫秒");
			//Map<String, Long> replyInMedia = support.getReplyInMedia(startDate, endDate, topic);
			Long replyInMediaTime = System.currentTimeMillis();
			//System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
			//		"("+FMContext.getLoginUser().getId()+")"+"#----------总体概况回复数查询耗时#"+(replyInMediaTime-readInMediaEndTime)+"#毫秒");
			
			Long total = 0L;
			for (Entry<String, Long> entry : situation.entrySet()) {
				total += entry.getValue();
			}
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------总体概况查询耗时#"+(replyInMediaTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((replyInMediaTime-startTime), "SituationArticleNo", 3000L);
			return view("/analyze/generalize-situation-chart")//
					.addObject("situation", situation)//
					//.addObject("readInMedia", readInMedia)//
					//.addObject("replyInMedia", replyInMedia)//
					.addObject("total", total)//
					.addObject("data", JsonUtil.toJson(situation.entrySet()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "总体概况加载失败");
		}
	}
	
	@ResponseBody
	@RequestMapping("generalize/situationCount")
	public MessageBean getSituationCount(String startDate, String endDate, Integer[] topic) {
		try {
			Long startTime = System.currentTimeMillis();
			Map<String, Long> readInMedia = support.getReadInMedia(startDate, endDate, topic);
			Long readInMediaEndTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
							"("+FMContext.getLoginUser().getId()+")"+"#----------总体概况count阅读数查询耗时#"+(readInMediaEndTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((readInMediaEndTime-startTime), "SituationReadingNo", 3000L);
			Map<String, Long> replyInMedia = support.getReplyInMedia(startDate, endDate, topic);
			Long replyInMediaTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------总体概况count回复数查询耗时#"+(replyInMediaTime-readInMediaEndTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((replyInMediaTime-readInMediaEndTime), "SituationReplyNo", 3000L);
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------总体概况count查询耗时#"+(replyInMediaTime-startTime)+"#毫秒");
			return buildMessage(MESSAGE_TYPE_SUCCESS, MESSAGE_TYPE_SUCCESS, new AnalyzeCountBean(readInMedia, replyInMedia));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		    return buildMessage(MESSAGE_TYPE_ERROR, "总体概况加载失败");
		}
	}

	@RequestMapping(value = { "generalize/tendency" })
	public ModelAndView getTendency(String startDate, String endDate, Integer[] topic) {
		try {
			Long startTime = System.currentTimeMillis();
			LineChartDataView tendency = support.getTendency(startDate, endDate, topic);
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------媒体趋势查询耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "MediaTendency", 3000L);
			return view("/analyze/generalize-tendency-chart").addObject("data", JsonUtil.toJson(tendency));

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "媒体趋势加载失败");
		}
	}

	@RequestMapping(value = { "generalize/statCustTopic" })
	public ModelAndView getStatCustTopic(String startDate, String endDate) {
		try {
			Long startTime = System.currentTimeMillis();
			List<StatCustTopicBean> statCustTopics = support.getStatCustTopic(startDate, endDate);
			Long statCustTopicsEndTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------专题分布统计查询耗时#"+(statCustTopicsEndTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((statCustTopicsEndTime-startTime), "TopicDistribution", 3000L);
			Map<String, Map<String, Long>> statCustTopicsByMedia = support.getStatCustTopicByMedia(statCustTopics, startDate, endDate);
			Long statCustTopicsByMediaEndTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------专题分布map处理查询耗时#"+(statCustTopicsByMediaEndTime-statCustTopicsEndTime)+"#毫秒");
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------专题分布处理查询耗时#"+(statCustTopicsByMediaEndTime-startTime)+"#毫秒");
			
			return view("/analyze/generalize-statCustTopic-list")
					.addObject("statCustTopics", statCustTopics)
					.addObject("statCustTopicsByMedia", statCustTopicsByMedia)
					.addObject("medias", support.getMedias());

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "专题统计加载失败");
		}
	}

	@RequestMapping(value = { "generalize/statWebsite" })
	public ModelAndView getStatWebsite(String startDate, String endDate) {
		try {
			Long startTime = System.currentTimeMillis();
			Map<Integer, List<StatWebsiteBean>> statWebsiteMap = support.getStatWebsite(startDate, endDate);
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------站点统计查询耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "SiteStatistical", 3000L);
			return view("/analyze/generalize-statWebsite-list").addObject("statWebsiteMap", statWebsiteMap);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "站点统计加载失败");
		}
	}
	
	/**
	 * 导出舆情分布表
	 * @param startDate
	 * @param endDate
	 * @param response
	 */
	@RequestMapping("exportSituation")
	public void exportSituation(String startDate, String endDate, Integer[] topic, HttpServletResponse response) {
		try {
			String[] hhmms = startDate.split("-");
			String[] hhmme = endDate.split("-");
			String sDate = hhmms[1] + "." + hhmms[2];
			String eDate = hhmme[1] + "." + hhmme[2];
			String fileName = "舆情分布(" + sDate + "—" + eDate + ")";//设置下载时客户端Excel的名称

			response.setCharacterEncoding("UTF-8");
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFCellStyle cellStyle = wb.createCellStyle();
			HSSFCellStyle cellStyleBody = wb.createCellStyle();
			cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);// 设置背景色
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居右
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直   
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平 
			//			cellStyleBody.setAlignment(HSSFCellStyle.ALIGN_CENTER);//居左

			//			cellStyleBody.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cellStyleBody.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			cellStyleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			cellStyleBody.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			cellStyleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			cellStyleBody.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居右

			HSSFFont font = wb.createFont();
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 12);//设置字体大小
			font.setColor(HSSFColor.BLACK.index);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//字体增粗
			cellStyle.setFont(font);//选择需要用到的字体格式
			HSSFFont fontBody = wb.createFont();
			fontBody.setFontName("宋体");
			fontBody.setFontHeightInPoints((short) 10);//设置字体大小
			cellStyleBody.setFont(fontBody);//选择需要用到的字体格式

			sheet.setColumnWidth(0, 5000);
			sheet.setColumnWidth(1, 5000);
			sheet.setColumnWidth(2, 5000);
			sheet.setColumnWidth(3, 5000);

			sheet.addMergedRegion(new Region(0, (short) 0, 1, (short) 3));// 单元格合并   

			HSSFRow rowCreat = sheet.createRow(0);
			rowCreat.setHeightInPoints((short) 22);
			HSSFCell cell = rowCreat.createCell(0);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(fileName);

			rowCreat = sheet.createRow(2);
			rowCreat.setHeightInPoints((short) 22);
			cell = rowCreat.createCell(0);
			rowCreat.createCell(1);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("来源");
			cell = rowCreat.createCell(1);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("文章数");
			cell = rowCreat.createCell(2);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("阅读数");
			cell = rowCreat.createCell(3);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("回复数");

			List<ConstantBean> mediaTypes = constantService.listConstantByType("MediaType");

			Map<String, Long> situation = support.getSituation(startDate, endDate, topic);
			Map<String, Long> readInMedia = support.getReadInMedia(startDate, endDate, topic);
			Map<String, Long> replyInMedia = support.getReplyInMedia(startDate, endDate, topic);
			Long articleTotalRecords = 0L;
			Long articleTotalReadNos = 0L;
			Long articleTotalReplyNos = 0L;
			int r = 2;
			for (ConstantBean media : mediaTypes) {
				r++;
				rowCreat = sheet.createRow(r);
				rowCreat.setHeightInPoints((short) 18);

				cell = rowCreat.createCell(0);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(media.getName());

				cell = rowCreat.createCell(1);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(situation.get(media.getValue()));

				cell = rowCreat.createCell(2);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(readInMedia.get(media.getValue()));

				cell = rowCreat.createCell(3);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(replyInMedia.get(media.getValue()));

				articleTotalRecords = articleTotalRecords + situation.get(media.getValue());
				articleTotalReadNos = articleTotalReadNos + readInMedia.get(media.getValue());
				articleTotalReplyNos = articleTotalReplyNos + replyInMedia.get(media.getValue());
			}
			rowCreat = sheet.createRow(r + 1);
			rowCreat.setHeightInPoints((short) 18);
			cell = rowCreat.createCell(0);
			cell.setCellStyle(cellStyleBody);
			cell.setCellValue("总计");

			cell = rowCreat.createCell(1);
			cell.setCellStyle(cellStyleBody);
			cell.setCellValue(articleTotalRecords);

			cell = rowCreat.createCell(2);
			cell.setCellStyle(cellStyleBody);
			cell.setCellValue(articleTotalReadNos);

			cell = rowCreat.createCell(3);
			cell.setCellStyle(cellStyleBody);
			cell.setCellValue(articleTotalReplyNos);

			fileName = new String(fileName.getBytes("gb2312"), "iso8859-1");
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			response.setHeader("Content-disposition", new String("attachment;filename=" + fileName + ".xls"));
			OutputStream ouputStream = response.getOutputStream();
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导出媒体趋势表
	 * @param startDate
	 * @param endDate
	 * @param response
	 */
	@RequestMapping("exportTendency")
	public void exportTendency(String startDate, String endDate, Integer[] topic, HttpServletResponse response) {
		try {
			String[] hhmms = startDate.split("-");
			String[] hhmme = endDate.split("-");
			String sDate = hhmms[1] + "." + hhmms[2];
			String eDate = hhmme[1] + "." + hhmme[2];
			String fileName = "媒体趋势(" + sDate + "—" + eDate + ")";//设置下载时客户端Excel的名称

			response.setCharacterEncoding("UTF-8");
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFCellStyle cellStyle = wb.createCellStyle();
			HSSFCellStyle cellStyleBody = wb.createCellStyle();
			cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);// 设置背景色
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居右
			cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直   
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平 
			//			cellStyleBody.setAlignment(HSSFCellStyle.ALIGN_CENTER);//居左

			//			cellStyleBody.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cellStyleBody.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			cellStyleBody.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			cellStyleBody.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			cellStyleBody.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			cellStyleBody.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居右

			HSSFFont font = wb.createFont();
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 12);//设置字体大小
			font.setColor(HSSFColor.BLACK.index);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//字体增粗
			cellStyle.setFont(font);//选择需要用到的字体格式
			HSSFFont fontBody = wb.createFont();
			fontBody.setFontName("宋体");
			fontBody.setFontHeightInPoints((short) 10);//设置字体大小
			cellStyleBody.setFont(fontBody);//选择需要用到的字体格式

			sheet.setColumnWidth(0, 5000);
			sheet.setColumnWidth(1, 5000);
			sheet.setColumnWidth(2, 5000);
			sheet.setColumnWidth(3, 5000);
			sheet.setColumnWidth(4, 5000);
			sheet.setColumnWidth(5, 5000);
			sheet.setColumnWidth(6, 5000);
			sheet.setColumnWidth(7, 5000);
			sheet.setColumnWidth(8, 5000);
			sheet.setColumnWidth(9, 5000);

			sheet.addMergedRegion(new Region(0, (short) 0, 1, (short) 9));// 单元格合并   

			HSSFRow rowCreat = sheet.createRow(0);
			rowCreat.setHeightInPoints((short) 22);
			HSSFCell cell = rowCreat.createCell(0);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(fileName);

			List<ConstantBean> mediaTypes = constantService.listConstantByType("MediaType");
			//-----表头--------
			rowCreat = sheet.createRow(2);
			rowCreat.setHeightInPoints((short) 22);
			cell = rowCreat.createCell(0);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("日期");
			int headr = 1;
			for (ConstantBean media : mediaTypes) {
				cell = rowCreat.createCell(headr);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(media.getName());
				headr++;
			}
			//-----body column--------
			Map<String, List<ArticleStatisticBean>> situationByDayMap = support.getSituationByDay(startDate, endDate, topic);

			List<String> getDateByDay = support.getDateByDay(startDate, endDate);
			int bodyr = 2;
			List<ArticleStatisticBean> situationInBbs = situationByDayMap.get("1");
			List<ArticleStatisticBean> situationInBlog = situationByDayMap.get("2");
			List<ArticleStatisticBean> situationInNews = situationByDayMap.get("3");
			List<ArticleStatisticBean> situationInWeibo = situationByDayMap.get("4");
			List<ArticleStatisticBean> situationInPrint = situationByDayMap.get("5");
			List<ArticleStatisticBean> situationInWechat = situationByDayMap.get("6");
			List<ArticleStatisticBean> situationInApp = situationByDayMap.get("7");
			List<ArticleStatisticBean> situationInWenda = situationByDayMap.get("8");
			List<ArticleStatisticBean> situationInVideo = situationByDayMap.get("11");
			int situationDay = 0;
			for (String day : getDateByDay) {
				bodyr++;
				rowCreat = sheet.createRow(bodyr);
				rowCreat.setHeightInPoints((short) 18);

				cell = rowCreat.createCell(0);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(day);

				cell = rowCreat.createCell(1);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(situationInBbs.get(situationDay).getQuantity());

				cell = rowCreat.createCell(2);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(situationInBlog.get(situationDay).getQuantity());

				cell = rowCreat.createCell(3);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(situationInNews.get(situationDay).getQuantity());

				cell = rowCreat.createCell(4);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(situationInWeibo.get(situationDay).getQuantity());

				cell = rowCreat.createCell(5);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(situationInPrint.get(situationDay).getQuantity());

				cell = rowCreat.createCell(6);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(situationInWechat.get(situationDay).getQuantity());

				cell = rowCreat.createCell(7);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(situationInApp.get(situationDay).getQuantity());

				cell = rowCreat.createCell(8);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(situationInWenda.get(situationDay).getQuantity());

				cell = rowCreat.createCell(9);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue(situationInVideo.get(situationDay).getQuantity());

				situationDay++;

			}

			fileName = new String(fileName.getBytes("gb2312"), "iso8859-1");
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			response.setHeader("Content-disposition", new String("attachment;filename=" + fileName + ".xls"));
			OutputStream ouputStream = response.getOutputStream();
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}