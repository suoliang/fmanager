package com.cyyun.base.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cyyun.base.service.bean.ArticleBean;

/**
 * 导出Excel工具类
 * @author LIUJUNWU
 *
 */
public interface ExportExcelUtil {

	public void exportExcel(List<ArticleBean> articles, HttpServletRequest request) throws Exception;
	
}