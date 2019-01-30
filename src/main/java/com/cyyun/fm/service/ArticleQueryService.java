package com.cyyun.fm.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.base.controller.BaseSupport;
import com.cyyun.fm.vo.ArticleVoInter;

public interface ArticleQueryService {

	public PageInfoBean<ArticleVoInter> queryArticleByParam(Map<String,String[]> params, HttpServletRequest request) throws Exception ;
	
	public ArticleVoInter findArticle(String guid, String keyword,BaseSupport baseSupport, HttpServletRequest request) throws Exception;
	
	public Map<String,Object> queryArticle(String guid, String keyword,BaseSupport baseSupport, HttpServletRequest request) throws Exception;
}
