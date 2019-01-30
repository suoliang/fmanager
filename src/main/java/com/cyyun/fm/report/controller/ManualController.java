package com.cyyun.fm.report.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;

import com.cyyun.base.util.DateFormatFactory;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.report.bean.ManualReportParam;
import com.cyyun.process.service.bean.CustReportBean;
import com.cyyun.process.service.bean.CustReportQueryBean;

/** 
 * @author  SuoLiang  
 * @version 2016年4月6日
 */
@Controller
@RequestMapping("/report")
public class ManualController extends BaseController {
	
	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;
	
	@Autowired
	private ManualSupport manualSupport;
	
	@RequestMapping(value = { "manual/index" })
	public ModelAndView manualReportList(CustReportQueryBean queryBean, Integer pageNo, Integer pageSize){
		try {
			if (pageNo == null) {
				pageNo = PAGE_NO;
			}
			if (pageSize == null) {
				pageSize = PAGE_SIZE;
			}
			PageInfoBean<CustReportBean> pageInfo = manualSupport.queryCustByPage(queryBean,pageNo,pageSize);
			Map<String, String> categoryMap = CustReportCategoryEnum.getCustReportCategoryMap();
			return view("/report/manual-index").addObject("pageInfo", pageInfo).addObject("categoryMap", categoryMap);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载人工报告失败");
		}
		
	}
	
	
	/**
	 * 查询人工报告列表
	 * @param manualReportParam
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = { "manual/queryList" })
	public ModelAndView queryList(ManualReportParam manualReportParam, Integer pageNo, Integer pageSize) {
		try {
			Date endTime = null;
			Date startTime = null;
			if (StringUtils.isNotBlank(manualReportParam.getStartTime()))
				startTime = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern).parse(manualReportParam.getStartTime() + " 00:00:00");
			if (StringUtils.isNotBlank(manualReportParam.getEndTime()))
				endTime = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern).parse(manualReportParam.getEndTime() + " 23:59:59");
			if (pageNo == null)
				pageNo = PAGE_NO;
			if (pageSize == null)
				pageSize = PAGE_SIZE;
			CustReportQueryBean queryBean = new CustReportQueryBean();
			queryBean.setTitle(manualReportParam.getTitle());
			queryBean.setReportTimeFrom(startTime);
			queryBean.setReportTimeTo(endTime);
			queryBean.setCategory(StringUtils.isBlank(manualReportParam.getCategory()) ? null : Integer.valueOf(manualReportParam.getCategory()));
			//queryBean.setStatus(manualReportParam.getStatus());
			queryBean.setPageNo(pageNo);
			queryBean.setPageSize(pageSize);
			PageInfoBean<CustReportBean> pageInfo = manualSupport.queryCustByPage(queryBean, pageNo, pageSize);
			Map<String, String> categoryMap = CustReportCategoryEnum.getCustReportCategoryMap();
			return view("/report/manual-index-paging-report").addObject("pageInfo", pageInfo).addObject("categoryMap", categoryMap);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载人工报告列表失败");
		}
	}
	
	/***
	 * @description 下载文件,向报告状态表里添加一条记录
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "manual/downLoad" })
	public void downloadReport(Integer id,HttpServletRequest request,HttpServletResponse response) {
		try {
			manualSupport.addReportStatus(id,request,response);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}
	
	/***
	 * @description 只支持word的在线阅读
	 * @param id
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 */
	@ResponseBody
	@RequestMapping("manual/onlineRead")
	public MessageBean onlineRead(Integer id,HttpServletRequest request, HttpServletResponse response, ModelMap out) {
		try {
			String onlineReadUrl = manualSupport.onlineReadUrl(id);
			
			File onlineFile= new File(onlineReadUrl);
			
			String fileName = onlineFile.getName();

			String reverseFileName = new StringBuffer(fileName).reverse().toString();
			
			String[] split = reverseFileName.split("\\.");
			String suffix = new StringBuffer(split[0]).reverse().toString();
			if (!"doc".equalsIgnoreCase(suffix)) {
				return buildMessage(MESSAGE_TYPE_ERROR, "非doc格式文件，无法预览");
			}
			/**文件名*/
			String name = new StringBuffer(split[1]).reverse().toString();
			String filePath = request.getSession().getServletContext().getRealPath("/") + "download";
			File dir = new File(filePath);
			if(!dir.exists()){
				dir.mkdirs();
			}
			/**去掉"/"的项目名称*/
			String contextPath = request.getContextPath().replace("/", "");
			
			/**遍历文件夹下是否存在需要的预览文件*/
			File[] listFiles = dir.listFiles();
			for (File localFile : listFiles) {
				if ((name+".html").equalsIgnoreCase(localFile.getName())) {
					String htmlUrl = "/" + contextPath + "/download/" + localFile.getName();
					return buildMessage(MESSAGE_TYPE_SUCCESS, htmlUrl, "操作成功");
				}
			}
	
			//word转HTML,用于展示用。
			URL url = new URL(onlineReadUrl);
	        URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
			HWPFDocument wordDocument = new HWPFDocument(is);
			WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
			
			wordToHtmlConverter.setPicturesManager(new PicturesManager() {
				public String savePicture(byte[] content, PictureType pictureType,
						String suggestedName, float widthInches, float heightInches) {
					return suggestedName;
				}
			});
			//对HWPFDocument进行转换  
			wordToHtmlConverter.processDocument(wordDocument);
			List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
			if (pics != null) {
				for (int i = 0; i < pics.size(); i++) {
					Picture pic = (Picture) pics.get(i);
					try {
						pic.writeImageContent(new FileOutputStream(filePath + File.separator + pic.suggestFullFileName()));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			Document htmlDocument = wordToHtmlConverter.getDocument();
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			DOMSource domSource = new DOMSource(htmlDocument);
			StreamResult streamResult = new StreamResult(outStream);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(OutputKeys.METHOD, "html");
			serializer.transform(domSource, streamResult);
			outStream.close();
			String content = new String(outStream.toByteArray());
			FileUtils.write(new File(filePath, name+".html"), content, "utf-8");
			
			String htmlUrl = "/"+ contextPath + "/download/" + name + ".html";
			
			return buildMessage(MESSAGE_TYPE_SUCCESS, htmlUrl, "操作成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			String exception = e.toString();
			if (exception.contains("NotOLE2FileException")) {
				return buildMessage(MESSAGE_TYPE_ERROR, "源文件为docx,暂不支持阅读");
			}
			return buildMessage(MESSAGE_TYPE_ERROR, "在线阅读失败");
		}
	}
	
	@RequestMapping("manual/toOnlineRead")
	public ModelAndView toOnlineRead(String onlineReadUrl){
		try {
			return view("/report/manual-online-read").addObject("onlineReadUrl", onlineReadUrl);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "在线阅读加载失败");
		}
	}
	
	
}
