package com.cyyun.fm.report.controller;

import java.io.OutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.velocity.Template;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import com.cyyun.base.constant.FMConstant;
import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.util.DateFormatFactory;
import com.cyyun.base.util.PropertiesUtil;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.service.bean.ReportBean;

/**
 * <h3>简报</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Controller
@RequestMapping(value = { "report" })
public class BriefController extends BaseController {

	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;

	@Autowired
	private BriefSupport support;

	@Autowired
	private VelocityConfigurer velocityConfig;
	

	@RequestMapping(value = { "brief/index" })
	public ModelAndView briefIndex(ReportBean report, Integer pageNo, Integer pageSize) {
		try {
			if (pageNo == null) {
				pageNo = PAGE_NO;
			}
			if (pageSize == null) {
				pageSize = PAGE_SIZE;
			}
			PageInfoBean<ReportBean> pageInfo = support.queryBriefByPage(report, pageNo, pageSize);
			return view("/report/brief-index").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载简报失败");
		}
	}

	@RequestMapping(value = { "brief/list" })
	public ModelAndView listBrief(ReportBean report, Integer pageNo, Integer pageSize) {
		try {
			PageInfoBean<ReportBean> pageInfo = support.queryBriefByPage(report, pageNo, pageSize);
			return view("/report/brief-index-paging-report").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载简报失败");
		}
	}

	@RequestMapping(value = { "brief/listBrief" })
	@ResponseBody
	public MessageBean listBriefBean(ReportBean report) {
		try {
			List<ReportBean> briefs = support.listBrief(report);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "检索简报列表成功", briefs);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "检索简报列表失败");
		}
	}

	@RequestMapping(value = { "brief/save" })
	@ResponseBody
	public MessageBean saveBrief(Integer reportId, String title) {
		try {
			support.saveBrief(reportId, title);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "保存简报成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "保存简报失败");
		}
	}

	@RequestMapping(value = { "brief/delete" })
	@ResponseBody
	public MessageBean deleteBrief(Integer reportId) {
		try {
			support.deleteBrief(reportId);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "删除简报成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "删除简报失败");
		}
	}

	@RequestMapping(value = { "brief/export" })
	public void exportBrief(Integer reportId, HttpServletRequest request, HttpServletResponse response) {
		try {
			ReportBean report = support.findBrief(reportId);
			List<ArticleBean> articles = support.exportBrief(report);

			response.addHeader("Content-Disposition", "attachment;filename=" + new String((report.getTitle().replaceAll(" | ", "")+".doc").getBytes("gb2312"),"ISO8859-1"));

			ToolManager manager = new ToolManager();
			manager.configure("/toolbox.xml");
			ToolContext context = manager.createContext();

			context.put("documentTitle", report.getTitle());
			context.put("articles", articles);

			response.setCharacterEncoding("UTF-8");
			velocityConfig.getVelocityEngine().mergeTemplate("/template/report/brief-article.vm", "UTF-8", context, response.getWriter());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private static final String ZIPFILENAME = "简报.zip";
	private static final String CHARACTERENCODE = "UTF-8";

	@RequestMapping(value = { "brief/batchExport" })
	public void batchExportBrief(Integer reportId, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			SimpleDateFormat sdf = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern3);
			String str ="";
			String[] reportIds = request.getParameterValues("reportId");
			if (ArrayUtils.isNotEmpty(reportIds)) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.addHeader(
						"Content-Disposition",
						"attachment;filename="
								+ new String(ZIPFILENAME.getBytes("gb2312"),
										"ISO8859-1"));
				response.setCharacterEncoding(CHARACTERENCODE);
				ToolManager manager = new ToolManager();
				manager.configure("/toolbox.xml");
				OutputStream outputStream = response.getOutputStream();
				ZipOutputStream zipOutputStream = new ZipOutputStream(
						outputStream);
				for (String reportid : reportIds) {
					
					ReportBean report = support.findBrief(Integer
							.valueOf(reportid));
					List<ArticleBean> articles = support.exportBrief(report);
					ToolContext context = manager.createContext();
					context.put("documentTitle", report.getTitle());
					context.put("articles", articles);
					StringWriter writer = new StringWriter();
					Template template = velocityConfig.getVelocityEngine()
							.getTemplate(
									PropertiesUtil.getValue("templatepath"),
									CHARACTERENCODE);
					template.merge(context, writer);
					Date date = report.getCreateTime();
					str=sdf.format(date);
					zipOutputStream.putNextEntry(new ZipEntry(report.getTitle()+str+ ".doc"));
					zipOutputStream.write(writer.toString().getBytes());
					zipOutputStream.flush();
					zipOutputStream.closeEntry();
				}
				zipOutputStream.flush();
				zipOutputStream.close();
				outputStream.flush();
				outputStream.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	

	@RequestMapping(value = { "brief/article/index" })
	public ModelAndView briefArticleIndex(Integer reportId,HttpServletRequest request) {
		try {
			if (reportId == null) {
				return view("redirect:/report/brief/index.htm");
			}
			ReportBean report = support.findBrief(reportId);
			PageInfoBean<ArticleBean> pageInfo = support.queryBriefArticleByPage(report, PAGE_NO, PAGE_SIZE,request);
			return view("/report/brief-article").addObject("pageInfo", pageInfo).addObject("report", report);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载简报文章失败");
		}
	}

	@RequestMapping(value = { "brief/article/list" })
	public ModelAndView listBriefArticle(Integer reportId, Integer pageNo, Integer pageSize,HttpServletRequest request) {
		try {
			ReportBean report = support.findBrief(reportId);
			PageInfoBean<ArticleBean> pageInfo = support.queryBriefArticleByPage(report, pageNo, pageSize, request);
			return view("/report/brief-article-paging-article").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载简报文章失败");
		}
	}

	@RequestMapping(value = { "brief/article/delete" })
	@ResponseBody
	public MessageBean deleteBriefArticle(Integer reportId, String guid) {
		try {
			support.deleteBriefArticle(reportId, guid);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "删除简报文章成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "删除简报文章失败");
		}
	}

	@RequestMapping(value = { "brief/article/add" })
	@ResponseBody
	public MessageBean addBriefArticle(Integer[] reportIds, String guid) {
		try {
			support.addBriefArticle(reportIds, guid);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "添加简报文章成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "添加简报文章失败");
		}
	}
	
	/**
	 * <h3>文章详情</h3>
	 */
	@RequestMapping("findArticle")
	public ModelAndView findArticle(String guid, String keyword, HttpServletRequest request) {
		try {
			return view("/report/detail").addObject("article", support.findArticle(guid, keyword, request)).addObject("TOPICDISPLAY", FMConstant.TOPIC_DISPLAY.none.getValue());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询文章失败");
		}
	}
	/**
	 * <h3>跳转文章详情页</h3>
	 */
	@RequestMapping("openArticleDetail")
	public ModelAndView openArticleDetail(String guid, String keyword) {
		try {
			return view("/report/article-detail").addObject("guid", guid).addObject("keyword", keyword);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询文章失败");
		}
	}
}