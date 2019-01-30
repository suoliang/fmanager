package com.cyyun.fm.report.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.PropertiesUtil;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.base.controller.BaseSupport;
import com.cyyun.process.service.CustReportService;
import com.cyyun.process.service.bean.CustReportBean;
import com.cyyun.process.service.bean.CustReportQueryBean;
import com.cyyun.process.service.bean.CustReportStatusBean;
import com.cyyun.process.service.exception.CustReportException;

/** 
 * @author  SuoLiang  
 * @version 2016年4月6日
 */
@SuppressWarnings("restriction")
@Component
public class ManualSupport extends BaseSupport {
	
	@Autowired
	private CustReportService custReportService;
	
	public PageInfoBean<CustReportBean> queryCustByPage(CustReportQueryBean queryBean, Integer pageNo, Integer pageSize) throws CustReportException {
		
 		queryBean.setPageNo(pageNo);
		queryBean.setPageSize(pageSize);
		queryBean.setCustIds(ArrayUtils.toArray(FMContext.getCurrent().getCustomerId()));
		PageInfoBean<CustReportBean> pageInfoBean = custReportService.queryReportByPage(queryBean);
		
		return pageInfoBean;
	}
	
	public void addReportStatus(Integer id,HttpServletRequest request,HttpServletResponse response) throws CustReportException, ServletException, IOException {
		CustReportStatusBean reportStatusBean = new CustReportStatusBean();
		CustReportBean custReportBean = custReportService.findReportById(id);
		reportStatusBean.setReportId(custReportBean.getId());
		reportStatusBean.setContactId(custReportBean.getContactIds()[0]);
		reportStatusBean.setCustId(custReportBean.getCustId());
		reportStatusBean.setUserId(FMContext.getLoginUser().getId());
		reportStatusBean.setReadTime(new Date());
		custReportService.addReportStatus(reportStatusBean);
		
		String fileName = new StringBuffer(custReportBean.getFile()).reverse().toString();
		
		String[] split = fileName.split("\\.");
		String suffix = new StringBuffer(split[0]).reverse().toString();
		/**以报告标题命名的文件*/
		fileName = custReportBean.getTitle()+"."+suffix;
		
		String remotefilePath = PropertiesUtil.getValue("path.visit.custReport.document");
		remotefilePath = remotefilePath+custReportBean.getCustId()+"/"+custReportBean.getFile();
		
		String agent = request.getHeader("user-agent");
		fileName = encodeDownloadFilename(fileName, agent);
		
		response.setCharacterEncoding("utf-8");  
        response.setContentType("multipart/form-data");  
        response.setHeader("Content-Disposition", "attachment;fileName="+fileName);
         
        URL url = new URL(remotefilePath);
        URLConnection conn = url.openConnection();
		InputStream inputStream = conn.getInputStream();
        OutputStream os=response.getOutputStream();  
        byte[] b=new byte[4096];  
        int length;  
        while((length=inputStream.read(b))>0){  
            os.write(b,0,length);  
        }  
        os.flush();
        inputStream.close();  
	}
	
	 /**
	 * 下载文件时，针对不同浏览器，进行附件名的编码
	 * 
	 * @param filename
	 *            下载文件名
	 * @param agent
	 *            客户端浏览器
	 * @return 编码后的下载附件名
	 * @throws IOException
	 */
	public String encodeDownloadFilename(String filename, String agent)
			throws IOException {
		if (agent.contains("Firefox")) { // 火狐浏览器
			filename = "=?UTF-8?B?"+ new BASE64Encoder().encode(filename.getBytes("utf-8")) + "?=";
			filename = filename.replaceAll("\r\n", "");
		} else { // IE及其他浏览器
			filename = URLEncoder.encode(filename, "utf-8");
			filename = filename.replace("+", " ");
		}
		return filename;
	}

	public String onlineReadUrl(Integer id) throws CustReportException {
		CustReportBean custReportBean = custReportService.findReportById(id);
		String filePath = PropertiesUtil.getValue("path.visit.custReport.document");
		return filePath+custReportBean.getCustId()+"/"+custReportBean.getFile();
	}
	
}
