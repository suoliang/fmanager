package com.cyyun.fm.custtopic.controller;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.velocity.Template;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import com.cyyun.authim.service.UserService;
import com.cyyun.base.constant.ConstantType;
import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.service.ArticleService;
import com.cyyun.base.service.ConstantService;
import com.cyyun.base.service.CustTopicKeywordService;
import com.cyyun.base.service.CustTopicService;
import com.cyyun.base.service.KeywordService;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.bean.ArticleCustomerAttrBean;
import com.cyyun.base.service.exception.ArticleServiceException;
import com.cyyun.base.service.exception.CustTopicServiceException;
import com.cyyun.base.util.ConstUtil;
import com.cyyun.base.util.DateFormatFactory;
import com.cyyun.base.util.ExportExcelUtil;
import com.cyyun.base.util.GetCurrentDate;
import com.cyyun.base.util.PropertiesUtil;
import com.cyyun.base.util.TagUtil;
import com.cyyun.base.util.UnicodeFilter;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.JsonUtil;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.common.core.util.SpringContextUtil;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.bean.CustConfigBean;
import com.cyyun.fm.custtopic.bean.ConditionParam;
import com.cyyun.fm.custtopic.bean.CustTopicView;
import com.cyyun.fm.report.templete.TemplateUtil;
import com.cyyun.fm.service.CountArticleNumService;
import com.cyyun.fm.service.FmCustTopicService;
import com.cyyun.fm.service.QueryLogService;
import com.cyyun.fm.vo.ArticleNumVo;
import com.cyyun.fm.vo.ArticleVoInter;

/**
 * <h3>专题</h3>
 * 
 * @author LIUJUNWU
 * @version 1.0.0
 */
@Controller
@RequestMapping(value = { "topic" })
public class CusttopicController extends BaseController {
	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;
	@Autowired
	private UserService userService;
	@Autowired
	private CustTopicService custTopicService;
	@Autowired
	private KeywordService keywordService;
	@Autowired
	private CustTopicKeywordService custTopicKeywordService;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private CusttopicSupport support;
	@Autowired
	private FmCustTopicService fmCustTopicService;
	@Autowired
	private VelocityConfigurer velocityConfig;
	@Autowired
	private CustomerConfigService customerConfigService;
	@Autowired
	private ConstUtil constUtil;
	@Autowired
	private TagUtil tagUtil;
	@Autowired
	private CountArticleNumService countArticleNumService; 
	@Autowired
	private QueryLogService queryLogService;
	@Autowired
	private ExportExcelUtil exportExcelUtil;
	@Autowired
	private ConstantService constantService;
	
	public static final String EXPORTDOC_PATH = PropertiesUtil.getValue("exportDocpath");
	public static Boolean EXPORT_SUC = false; 

	@RequestMapping("index")
	public ModelAndView index(ConditionParam conditionParam, HttpServletRequest request) {
		PageInfoBean<ArticleVoInter> pageInfo = new PageInfoBean<ArticleVoInter>();
		ModelAndView view = view("/custtopic/index");
		Long startTime1 = System.currentTimeMillis();
		Boolean custTopicIsno = false;//用户是否有专题标记
		try {
			//参数中文乱码处理
			chineseStatementDeal(conditionParam);
			Integer pageNo = conditionParam.getCurrentpage() == null ? PAGE_NO : conditionParam.getCurrentpage();
			Integer pageSize = conditionParam.getPagesize() == null ? PAGE_SIZE : conditionParam.getPagesize();

			view.addAllObjects(support.getOptions());
			conditionParam.setCurrentpage(pageNo);
			conditionParam.setPagesize(pageSize);
			conditionParam.setCustId(FMContext.getCurrent().getCustomerId());// cid(客户id)
			CustConfigBean custConfig = customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
			conditionParam.setFilterSimilar(!custConfig.getFilterSimilar());

			String startTimeInput = request.getParameter("startTimeInput");
			String endTimeInput = request.getParameter("endTimeInput");
			Integer[] topicId = ArrayUtils.isNotEmpty(conditionParam.getTopicId()) ? conditionParam.getTopicId() : null;

			if (ArrayUtils.isEmpty(topicId)) {
				topicId = setOneCustTopicId(conditionParam);
				if (ArrayUtils.isEmpty(topicId)) {//当用户没有专题时情况
					custTopicIsno = true;
				}
			}
			
			Integer firstTopicId = topicId == null ? null : topicId[0];
			if (FMConstant.FROM_BOARD.equals(request.getParameter("fromBoard"))) {
				// 最近7天
				Calendar cal = Calendar.getInstance(Locale.CHINA);
				String pattern = "yyyy-MM-dd";
				String end = DateFormatUtils.format(cal.getTime(), pattern) + " 23:59:59";
				cal.add(Calendar.DATE, -6);
				String start = DateFormatUtils.format(cal.getTime(), pattern) + " 00:00:00";
				conditionParam.setStartTime(start);
				conditionParam.setEndTime(end);
				view.addObject("fromBoard", FMConstant.FROM_BOARD);
			} else {
				if (StringUtils.isBlank(conditionParam.getStartTime()) && StringUtils.isBlank(conditionParam.getStartTime())) {
					conditionParam.setTimeType(conditionParam.getTimeType() != null ? conditionParam.getTimeType() : 1);
				}
			}
			if (ObjectUtils.notEqual(topicId, null) && topicId.length > 1) {
				/**相识文返回时专题选中*/
				String[] result = fmCustTopicService.queryCustTopicTree(firstTopicId);
				view.addObject("tree", result[1]).addObject("topCustTopicId", result[0]);
			}
			pageInfo = support.queryArticleByParam(conditionParam, request);

			String topicIdString = firstTopicId == null ? StringUtils.EMPTY: firstTopicId.toString();//support.integerArrayToString(topicId);

			Map<Integer, Long> countArticleList = support.countMapArticleInMedia(conditionParam);

			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------专题模块加载耗时#"+(endTime-startTime1)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime1), "TopicLoading", 3000L);
			if (custTopicIsno) {//当用户没有专题时情况处理
				List<ArticleVoInter> data = new ArrayList<ArticleVoInter>();
				pageInfo.setTotalRecords(0);
				pageInfo.setData(data);
				countArticleList = new LinkedHashMap<Integer, Long>();
			}
			view.addObject("pageInfo", pageInfo).addObject("startTimeInput", startTimeInput).addObject("endTimeInput", endTimeInput).addObject("topicId", topicIdString)
			.addObject("condition", JsonUtil.toJson(support.convertToConditionView(conditionParam))).addObject("countArticleList", countArticleList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询专题文章失败");
		}
		return view;
	}

	/**
	 * 初始化专题列表
	 * @return
	 */
	@RequestMapping("showCusttopic")
	@ResponseBody
	public MessageBean showCusttopic(Integer parentId) {
		try {
			List<CustTopicView> topics = support.listTopic(parentId);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", topics);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索专题失败");
		}
	}

	/**
	 * <h3>搜索页面</h3>
	 */
	@RequestMapping("topicArticle")
	public ModelAndView topicArticle(ConditionParam conditionParam, HttpServletRequest request) {
		try {
			Long startTime = System.currentTimeMillis();

			conditionParam.setCustId(FMContext.getCurrent().getCustomerId());//cid(客户id)
			PageInfoBean<ArticleVoInter> pageInfo = support.queryArticleByParam(conditionParam, request);
			Map<Integer, Long> countArticleList = support.countMapArticleInMedia(conditionParam);
			if (conditionParam.getTopicId().length < 1) {//当用户没有专题时情况处理
				List<ArticleVoInter> data = new ArrayList<ArticleVoInter>();
				pageInfo.setTotalRecords(0);
				pageInfo.setData(data);
				countArticleList = new LinkedHashMap<Integer, Long>();
			}
			Long endTime = System.currentTimeMillis();
			System.err.println(GetCurrentDate.yyyyMMddHHmmss()+" 用户-#"+FMContext.getLoginUser().getUsername()+
					"("+FMContext.getLoginUser().getId()+")"+"#----------专题模块加载耗时#"+(endTime-startTime)+"#毫秒");
			queryLogService.saveInterfaceAccessLog((endTime-startTime), "TopicLoading", 3000L);
			return view("/custtopic/index-paging-article").addObject("pageInfo", pageInfo).addObject("countArticleList", countArticleList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询专题文章失败");
		}
	}

	private void chineseStatementDeal(ConditionParam conditionParam) throws UnsupportedEncodingException {
		if (StringUtils.isNoneBlank(conditionParam.getKeyword())) {
			String keyword = URLDecoder.decode(conditionParam.getKeyword(), "UTF-8");
			conditionParam.setKeyword(keyword);
		}
		if (StringUtils.isNotBlank(conditionParam.getStartTime())) {
			String startTime = URLDecoder.decode(conditionParam.getStartTime(), "UTF-8");
			conditionParam.setStartTime(startTime);
		}
		if (StringUtils.isNotBlank(conditionParam.getEndTime())) {
			String endTime = URLDecoder.decode(conditionParam.getEndTime(), "UTF-8");
			conditionParam.setEndTime(endTime);
		}
	}
	
	private Integer[] setOneCustTopicId(ConditionParam conditionParam)
			throws CustTopicServiceException {
		Integer[] topicId;
		String custTopicIds = support.getAllCustTopicId();
		if (StringUtils.isNotBlank(custTopicIds)) {
			custTopicIds = custTopicIds.substring(0, custTopicIds.length() - 1);
		}
		topicId = support.stringToIntegerArray(custTopicIds);
		topicId = ArrayUtils.isNotEmpty(topicId) ? topicId : null;
		/**此处默认查询第一个专题数据--start，如果删除查询全部专题*/
		if (ArrayUtils.isNotEmpty(topicId)) {//
			topicId = ArrayUtils.toArray(topicId[0]);
		}
		/**此处默认查询第一个专题数据--end*/
		conditionParam.setTopicId(topicId);
		return topicId;
	}
	
	
	/**
	 * <h3>开启导出线程 Word</h3>
	 */
//	@RequestMapping("exportWordThread")
//	@ResponseBody
//	public MessageBean exportWordThread(ConditionParam conditionParam, HttpServletResponse response, HttpServletRequest request) {
//		try {
//			CustConfigBean custConfig=customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
//			custConfig.setFilterSimilar(true);//查询到的文章带有相似文
//			List<String> guids = support.exportAllParam(conditionParam, custConfig);
//			Thread thread = new Thread(new ExportWord(response, guids, request.getSession()));
//			thread.start();
//			for (;;) {
//				if (EXPORT_SUC) {
//					EXPORT_SUC = false;
//					break;
//				} else {
//					Thread.sleep(1000);
//				}
//			}
//			return buildMessage(MESSAGE_TYPE_SUCCESS, "导出Word成功");
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return buildMessage(MESSAGE_TYPE_ERROR, "导出Word失败");
//		}
//	}
	
	/**
	 * <h3>开启导出线程 Excel</h3>
	 */
	@RequestMapping("exportExcelThread")
	@ResponseBody
	public MessageBean exportExcelThread(ConditionParam conditionParam, HttpServletRequest request) {
		try {
			CustConfigBean custConfig=customerConfigService.findCustConfigByCid(FMContext.getCurrent().getCustomerId());
			custConfig.setFilterSimilar(false);//查询到的文章带有相似文
			List<ArticleBean> articles = support.exportAllParam(conditionParam, custConfig, request);
			exportExcelUtil.exportExcel(articles, request);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "导出Excel成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "导出Excel失败");
		}
	}
	
	/**
	 * 专题、搜索、预警导出word方法
	 */
	@RequestMapping("findArticles")
	public void exportBrief(String[] guids, HttpServletRequest request, HttpServletResponse response) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
			String sDate = df.format(new Date());//当前时间
			String topName = "文章详情";
			response.addHeader("Content-Disposition", "attachment;filename=" + new String((sDate + topName).getBytes("gb2312"), "ISO8859-1") + ".doc");
			ToolManager manager = new ToolManager();
			manager.configure("/toolbox.xml");
			ToolContext context = manager.createContext();
			//			context.put("documentTitle", "文章");
			List<ArticleBean> result = ListBuilder.newArrayList();
			guids = request.getParameterValues("guids");
			String content = null;
			String title = null;
			String url = null;
			for (String guid : guids) {
				ArticleBean article = articleService.findArticleDetail(guid);
				content = TemplateUtil.getValueAfterRepalceTNR(TemplateUtil.getValueAfterRepalceSpecialWord(UnicodeFilter.filter(article.getContent())));
				title = TemplateUtil.getValueAfterRepalceSpecialWord(UnicodeFilter.filter(article.getTitle()));
				url = TemplateUtil.getValueAfterRepalceSpecialWord(article.getUrl());
				article.setContent(content);
				article.setTitle(title);
				article.setUrlHash(url);
				result.add(article);
			}
			context.put("articles", result);
			context.put("CUSTOMER", FMContext.getCurrent().getCustomer());
			response.setCharacterEncoding("UTF-8");
			velocityConfig.getVelocityEngine().mergeTemplate("/template/report/brief-article.vm", "UTF-8", context, response.getWriter());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 专题、搜索、预警导出全部word方法
	 */
	@RequestMapping("exportBriefAllWord")
	public void exportArticleAllWord(List<String> guids, HttpServletResponse response, HttpServletRequest request) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
			String sDate = df.format(new Date());//当前时间
			String topName = "文章详情";
			response.addHeader("Content-Disposition", "attachment;filename=" + new String((sDate + topName).getBytes("gb2312"), "ISO8859-1") + ".doc");
			ToolManager manager = new ToolManager();
			manager.configure("/toolbox.xml");
			ToolContext context = manager.createContext();
			//			context.put("documentTitle", "文章");
			List<ArticleBean> result = ListBuilder.newArrayList();
			String content = null;
			String title = null;
			String url = null;
			for (String guid : guids) {
				ArticleBean article = articleService.findArticle(guid);
				content = TemplateUtil.getValueAfterRepalceTNR(TemplateUtil.getValueAfterRepalceSpecialWord(UnicodeFilter.filter(article.getContent())));
				title = TemplateUtil.getValueAfterRepalceSpecialWord(UnicodeFilter.filter(article.getTitle()));
				url = TemplateUtil.getValueAfterRepalceSpecialWord(article.getUrl());
				article.setContent(content);
				article.setTitle(title);
				article.setUrl(url);
				result.add(article);
			}
			context.put("articles", result);
			response.setCharacterEncoding("UTF-8");
			velocityConfig.getVelocityEngine().mergeTemplate("/template/report/brief-article.vm", "UTF-8", context, response.getWriter());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		//-----------------------新导出方式---------------------------
//		try {
//			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
//			String sDate = df.format(new Date());//当前时间
//			String topName = "文章详情";
//			response.addHeader("Content-Disposition", "attachment;filename=" + new String((sDate + topName).getBytes("gb2312"), "ISO8859-1") + ".doc");
//			ToolManager manager = new ToolManager();
//			manager.configure("/toolbox.xml");
////			ToolContext context = manager.createContext();
//			//			context.put("documentTitle", "文章");
//			List<ArticleBean> result = ListBuilder.newArrayList();
//			for (String guid : guids) {
//				ArticleBean article = articleService.findArticle(guid);
//				String content = TemplateUtil.getValueAfterRepalceTNR(TemplateUtil.getValueAfterRepalceSpecialWord(article.getContent()));
//				String title = TemplateUtil.getValueAfterRepalceSpecialWord(article.getTitle());
//				
////				content.replaceAll("[//x00-//x08//x0b-//x0c//x0e-//x1f]", "");
//				article.setContent(content);
//				article.setTitle(title);
//				result.add(article);
//			}
////			context.put("articles", result);
//			response.setCharacterEncoding("UTF-8");
//			
//			String contextPath = request.getSession().getServletContext().getRealPath("/"); 
////		    Template template = Velocity.getTemplate(contextPath+"/template/report/brief-article.vm");
//			String aString="E:/workspace-fm/fmanager/src/main/resources/template/report/brief-article.vm";
//		    Template template = Velocity.getTemplate(aString);
//		    VelocityContext context = new VelocityContext();
//		    context.put("articles", result);
//		    String fileName = new String((sDate + topName).getBytes("gb2312"), "ISO8859-1") + ".doc";
//		    String path=EXPORTDOC_PATH + fileName;
//		    FileOutputStream fos = new FileOutputStream(path); 
//		    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));//设置写入的文件编码,解决中文问题 
//		    template.merge(context, writer); 
//		    writer.close();
//		    
////			velocityConfig.getVelocityEngine().mergeTemplate("/template/report/brief-article.vm", "UTF-8", context, response.getWriter());
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
		//-----------------------新查询方法---------------------------
//		try {
//			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
//			String sDate = df.format(new Date());//当前时间
//			String topName = "文章详情";
//			response.addHeader("Content-Disposition", "attachment;filename=" + new String((sDate + topName).getBytes("gb2312"), "ISO8859-1") + ".doc");
//			ToolManager manager = new ToolManager();
//			manager.configure("/toolbox.xml");
//			ToolContext context = manager.createContext();
//			//			context.put("documentTitle", "文章");
//			String[] guidsArray =new String[guids.size()];
//			for(int i=0;i<guids.size();i++){
//				guidsArray[i]=guids.get(i);
//			}
//			List<ArticleBean> result = ListBuilder.newArrayList();
//			List<ArticleBean> articleBeanList=articleService.listArticle(guidsArray);
//			for (ArticleBean articleBean : articleBeanList) {
//				String content = TemplateUtil.getValueAfterRepalceTNR(TemplateUtil.getValueAfterRepalceSpecialWord(articleBean.getContent()));
////				content.replaceAll("[//x00-///x08//x0b-//x0c//x0e-//x1f]", "");
//				articleBean.setContent(content);
//				result.add(articleBean);
//			}
//			context.put("articles", result);
//			response.setCharacterEncoding("UTF-8");
//			velocityConfig.getVelocityEngine().mergeTemplate("/template/report/brief-article.vm", "UTF-8", context, response.getWriter());
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
	}

	/**
	 * 专题、搜索、预警导出excel方法
	 */
	@RequestMapping("listExportExcel")
	public void listExportExcel(String[] guids, String districtName, HttpServletResponse response, HttpServletRequest request) {
		try {
			String postTime = "";
			response.setCharacterEncoding("UTF-8");
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFCellStyle cellStyle = wb.createCellStyle();
			HSSFCellStyle cellStyleBody = wb.createCellStyle();
			HSSFCellStyle cellStyleFirst = wb.createCellStyle();//第一行内容样式
			HSSFCellStyle cellStyleTop = wb.createCellStyle();//前几行内容样式
			cellStyle.setFillForegroundColor(HSSFColor.SEA_GREEN.index);// 设置背景色
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居右
			cellStyleBody.setAlignment(HSSFCellStyle.ALIGN_CENTER);//居左
			HSSFFont font = wb.createFont();
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 10);//设置字体大小
			font.setColor(HSSFColor.WHITE.index);
			cellStyle.setFont(font);//选择需要用到的字体格式
			HSSFFont fontBody = wb.createFont();
			fontBody.setFontName("宋体");
			fontBody.setFontHeightInPoints((short) 10);//设置字体大小
			cellStyleBody.setFont(fontBody);//选择需要用到的字体格式
			
			cellStyleTop.setAlignment(HSSFCellStyle.ALIGN_LEFT);//水平
			cellStyleTop.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
			
			cellStyleFirst.setAlignment(HSSFCellStyle.ALIGN_LEFT);//水平
			cellStyleFirst.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
			cellStyleFirst.setFillForegroundColor(HSSFColor.SEA_GREEN.index);// 设置背景色
			cellStyleFirst.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			
			//专题名
			sheet.setColumnWidth(0, 5000);
			//标题
			sheet.setColumnWidth(1, 10000);
			//站点
			sheet.setColumnWidth(2, 5000);
			//媒体类型
			sheet.setColumnWidth(3, 5000);
			//发布人
			sheet.setColumnWidth(4, 5000);
			//阅读数
			sheet.setColumnWidth(5, 5000);
			//回复数
			sheet.setColumnWidth(6, 5000);
			//转发数
			sheet.setColumnWidth(7, 5000);
			//点赞数
			sheet.setColumnWidth(8, 5000);
			//预警
			sheet.setColumnWidth(9, 6000);
			//发布时间
			sheet.setColumnWidth(10, 6000);
			//内容
			sheet.setColumnWidth(11, 20000);
			//链接地址
			sheet.setColumnWidth(12, 15000);

			HSSFRow rowCreat = sheet.createRow(4);
			rowCreat.setHeightInPoints((short) 22);
			HSSFCell cell = rowCreat.createCell(0);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("专题");
			cell = rowCreat.createCell(1);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("标题");
			cell = rowCreat.createCell(2);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("站点");
			cell = rowCreat.createCell(3);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("媒体类型");
			cell = rowCreat.createCell(4);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("发布人");
			cell = rowCreat.createCell(5);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("阅读数");
			cell = rowCreat.createCell(6);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("回复数");
			cell = rowCreat.createCell(7);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("转发数");
			cell = rowCreat.createCell(8);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("点赞数");
			cell = rowCreat.createCell(9);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("预警");
			cell = rowCreat.createCell(10);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("发布时间");
			cell = rowCreat.createCell(11);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("内容");
			cell = rowCreat.createCell(12);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("链接地址");

			guids = request.getParameterValues("guids");
			String cId = FMContext.getCurrent().getCustomerId().toString();
			String cName = FMContext.getCurrent().getCustomer().getName();
			
			HSSFRow rowOneCreat = sheet.createRow(0);
			rowOneCreat.setHeightInPoints((short) 22);
			HSSFCell cellOne = rowOneCreat.createCell(0);
			
			for (int i = 0; i < 13; i++) {
				cellOne = rowOneCreat.createCell(i);
				cellOne.setCellStyle(cellStyleFirst);
			}
			cellOne = rowOneCreat.createCell(0);
			cellOne.setCellStyle(cellStyleFirst);
			cellOne.setCellValue(cName+"网络舆情");
			
			List<ArticleBean> listArticle = articleService.listArticle(guids);
			ArticleNumVo articleCountMap = countArticleNumService.getArticleCountMap(listArticle);
			Map<Integer, Long> countStyleArticleMap = articleCountMap.getCountStyleArticleMap();
			/**媒体类型数据量*/
			HSSFRow rowTwoCreat = sheet.createRow(1);
			rowTwoCreat.setHeightInPoints((short) 22);
			HSSFCell cellTwo = rowTwoCreat.createCell(0);
			cellTwo.setCellStyle(cellStyleTop);
			String styleArticleName = "共监测到信息"+listArticle.size()+"条，其中论坛"+countStyleArticleMap.get(1)+"条，博客"
					+countStyleArticleMap.get(2)+"条，新闻"+countStyleArticleMap.get(3)+"条，微博"+countStyleArticleMap.get(4)+"条，纸媒"
					+countStyleArticleMap.get(5)+"条，微信"+countStyleArticleMap.get(6)+"条，APP新闻"+countStyleArticleMap.get(7)+"条，问答"
					+countStyleArticleMap.get(8)+"条，视频"+countStyleArticleMap.get(11)+"条。";
			cellTwo.setCellValue(styleArticleName);
			
			Map<Integer, Long> countSentimentArticleMap = articleCountMap.getCountSentimentArticleMap();
			/**信息属性数据量*/
			HSSFRow rowThreeCreat = sheet.createRow(2);
			rowThreeCreat.setHeightInPoints((short) 22);
			HSSFCell cellThree = rowThreeCreat.createCell(0);
			cellThree.setCellStyle(cellStyleTop);
			String sentimentName = "信息属性中，共监测到负面信息"+countSentimentArticleMap.get(1)+"条；正面信息"+countSentimentArticleMap.get(2)+"条；中立信息"
					+countSentimentArticleMap.get(3)+"条；未知信息"+countSentimentArticleMap.get(0)+"条。";
			System.out.println(sentimentName);
			cellThree.setCellValue(sentimentName);
			
			Map<Integer, Long> countTopicArticleMap = articleCountMap.getCountTopicArticleMap();
			/**专题统计数据量*/
			HSSFRow rowFourCreat = sheet.createRow(3);
			rowFourCreat.setHeightInPoints((short) 22);
			HSSFCell cellFour = rowFourCreat.createCell(0);
			cellFour.setCellStyle(cellStyleTop);
			cellFour.setCellValue("专题统计：");
			
			int situation = 1;
			for (Integer integerKey : countTopicArticleMap.keySet()) {
				String custTopicName = tagUtil.getCustTopicName(integerKey);
				Long custTopicNum = countTopicArticleMap.get(integerKey);
				
				cellFour = rowFourCreat.createCell(situation);
				cellFour.setCellStyle(cellStyleTop);
				cellFour.setCellValue(custTopicName+"("+custTopicNum+")");
				situation++;
			}
			
			HttpSession session = request.getSession();
			List<String> custTopicIds = (List<String>) session.getAttribute("CUSTTOPICIDS_WITHSHARE");
			boolean flag = CollectionUtils.isNotEmpty(custTopicIds);
			
			int r = 4;//从第5行开始输出内容
			for (String guid : guids) {
				r++;
				ArticleBean article = articleService.findArticleDetail(guid);
				rowCreat = sheet.createRow(r);
				rowCreat.setHeightInPoints((short) 18);

				/**专题*/
				cell = rowCreat.createCell(0);
				cell.setCellStyle(cellStyleBody);
				Set<Integer> custAttr = new HashSet<Integer>();
				Set<Integer> custAttrCustTopicIds = null;
				if (article.getCustAttrs().get(cId) != null) {
					custAttrCustTopicIds = article.getCustAttrs().get(cId).getCustTopicIds();
					custAttrCustTopicIds = (custAttrCustTopicIds == null ? new HashSet<Integer>() : custAttrCustTopicIds);
					for (Integer custTopicId : custAttrCustTopicIds) {
						if (flag) {
							/**统计包括分享的专题*/
							if (custTopicIds.contains(custTopicId.toString())) {
								custAttr.add(custTopicId);
							}
						}
					}
					article.getCustAttrs().get(cId).setCustTopicIds(custAttr);
				}
				
				String custTopicName = tagUtil.getCustTopicName(custAttr, ",");
				
				if (StringUtils.isBlank(custTopicName)) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(custTopicName);
				}
				/**标题*/
				cell = rowCreat.createCell(1);
				cell.setCellStyle(cellStyleBody);
				if (article.getTitle() == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(UnicodeFilter.filter(article.getTitle()));
				}
				/**站点*/
				cell = rowCreat.createCell(2);
				cell.setCellStyle(cellStyleBody);
				if (article.getWebsiteName() == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(article.getWebsiteName());
				}
				/**媒体类型*/
				cell = rowCreat.createCell(3);
				cell.setCellStyle(cellStyleBody);
				if (article.getWebsiteName() == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(constUtil.getName("MediaType", article.getStyle()));
				}
				/**发布人*/
				cell = rowCreat.createCell(4);
				cell.setCellStyle(cellStyleBody);
				if (article.getAuthor() == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(article.getAuthor());
				}
				/**阅读数*/
				cell = rowCreat.createCell(5);
				cell.setCellStyle(cellStyleBody);
				if (article.getReadCount() == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(article.getReadCount());
				}
				/**回复数*/
				cell = rowCreat.createCell(6);
				cell.setCellStyle(cellStyleBody);
				if (article.getReplyCount() == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(article.getReplyCount());
				}
				/**转发数*/
				cell = rowCreat.createCell(7);
				cell.setCellStyle(cellStyleBody);
				if (article.getCopyCount() == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(article.getCopyCount());
				}
				/**点赞数*/
				cell = rowCreat.createCell(8);
				cell.setCellStyle(cellStyleBody);
				if (article.getLikeCount() == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(article.getLikeCount());
				}
				/**预警等级*/
				cell = rowCreat.createCell(9);
				cell.setCellStyle(cellStyleBody);
				if (article.getCustAttrs() == null) {
					cell.setCellValue("");
				} else {
					ArticleCustomerAttrBean bean = article.getCustAttrs().get(FMContext.getCurrent().getCustomerId().toString());
					Integer warnLevel = null;
					if (bean != null) {
						//预警已通知过的文章(客户stage=22)才显示"预警"级别
						if (bean.getStage() == 22) {
							warnLevel = bean.getWarnLevel();
						}
					}
					String warnName = constUtil.getName(ConstantType.WARN_LEVEL, warnLevel);
					if (StringUtils.isBlank(warnName)) {
						warnName = "未预警";
					}
					cell.setCellValue(warnName);
				}
				/**发布时间*/
				if (article.getPostTime() == null) {
					postTime = "";
				} else {
					SimpleDateFormat df = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern);//设置日期格式
					postTime = df.format(article.getPostTime());//发布时间
				}
				cell = rowCreat.createCell(10);
				cell.setCellStyle(cellStyleBody);
				if (postTime == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(postTime);
				}
				/**内容*/
				cell = rowCreat.createCell(11);
				cell.setCellStyle(cellStyleBody);
				if (article.getContent() == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(TemplateUtil.checkSize(UnicodeFilter.filter(article.getContent())));
				}
				/**链接地址*/
				cell = rowCreat.createCell(12);
				cell.setCellStyle(cellStyleBody);
				if (article.getUrl() == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(article.getUrl());
				}
				cell = rowCreat.createCell(13);
				cell.setCellStyle(cellStyleBody);
				cell.setCellValue("");
			}
			SimpleDateFormat df = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.DatePattern);//设置日期格式
			String sDate = df.format(new Date());//当前时间
			String fileName = sDate + "文章列表" + ".xls";//设置下载时客户端Excel的名称
			fileName = new String(fileName.getBytes("gb2312"), "iso8859-1");
			System.out.println(fileName);
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			response.setHeader("Content-disposition", new String("attachment;filename=" + fileName));
			OutputStream ouputStream = response.getOutputStream();
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private class ExportWord implements Runnable {
		private HttpServletResponse response;
		private List<String> guids;
		private HttpSession session;

		public ExportWord(HttpServletResponse response, List<String> guids, HttpSession session) {
			this.response = response;
			this.guids = guids;
			this.session = session;
		}

		@Override
		public void run() {
			try {
				response.addHeader("Content-Disposition", "attachment;filename=" + session.getAttribute("fileName").toString());
				ToolManager manager = new ToolManager();
				manager.configure("/toolbox.xml");
				ToolContext context = manager.createContext();
				List<ArticleBean> result = ListBuilder.newArrayList();
				String content = null;
				String title = null;
				for (String guid : guids) {
					ArticleBean article = articleService.findArticle(guid);
					content = TemplateUtil.getValueAfterRepalceTNR(UnicodeFilter.filter(TemplateUtil.getValueAfterRepalceSpecialWord(article.getContent())));
					title = TemplateUtil.getValueAfterRepalceSpecialWord(UnicodeFilter.filter(article.getTitle()));
					article.setAuthor(article.getAuthor() == null ? "" : article.getAuthor());
					article.setContent(content);
					article.setTitle(title);
					result.add(article);
				}
				context.put("articles", result);
				response.setCharacterEncoding("UTF-8");

				String targetPath = EXPORTDOC_PATH + "/";
				String fileName = session.getAttribute("fileName").toString();
				String reportFile = targetPath + fileName;

				Template template = velocityConfig.getVelocityEngine().getTemplate("/template/report/brief-article.vm");
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reportFile), "UTF-8"));
				template.merge(context, out);
				out.close();
				//				velocityConfig.getVelocityEngine().mergeTemplate("/template/report/brief-article.vm", "UTF-8", context, out);
				//velocityConfig.getVelocityEngine().mergeTemplate("/template/report/brief-article.vm", "UTF-8", context, response.getWriter());
			}  catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				EXPORT_SUC = true;
			}
		}
	}

	public class ExportExcel implements Runnable {
		private HttpSession session;
		private List<String> guids;

		public ExportExcel(List<String> guids, HttpSession session) {
			this.guids = guids;
			this.session = session;
		}

		@Override
		public void run() {
			Map<String, Object> beans = new HashMap<String, Object>();
			List<ArticleBean> result = ListBuilder.newArrayList();
			String content = null;
			String title = null;
			for (String guid : guids) {
				ArticleBean article = new ArticleBean();
				try {
					article = articleService.findArticle(guid);
					content = TemplateUtil.checkSize(TemplateUtil.getValueAfterRepalceSpecialWord(UnicodeFilter.filter(article.getContent())));
					title = TemplateUtil.getValueAfterRepalceSpecialWord(UnicodeFilter.filter(article.getTitle()));
					article.setAuthor(article.getAuthor() == null ? "" : article.getAuthor());
					article.setContent(content);
					article.setTitle(title);
					result.add(article);
				} catch (ArticleServiceException e) {
					e.printStackTrace();
				}
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			beans.put("articles", result);
			beans.put("dateFormat", dateFormat);
			beans.put("tag", SpringContextUtil.getBean(TagUtil.class));
			beans.put("const", SpringContextUtil.getBean(ConstUtil.class));

			String path = session.getServletContext().getRealPath("/");
			String targetPath = EXPORTDOC_PATH + "/";
			String fileName = session.getAttribute("fileName").toString();
			String reportFile = targetPath + fileName;
			String templateFile = path + PropertiesUtil.getValue("exceltemplatepath");
			//			String templateFile ="E:\\workspace-fm\\template-article-excel.xls";
			XLSTransformer transformer = new XLSTransformer();
			transformer.markAsFixedSizeCollection("articles");//对应模板所循环的集合名字
			try {
				transformer.transformXLS(templateFile, beans, reportFile);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				EXPORT_SUC = true;
			}
		}
		
	}
}