package com.cyyun.fm.service;

import javax.servlet.http.HttpServletRequest;

import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.custtopic.bean.ConditionParam;
import com.cyyun.fm.vo.ArticleVoInter;

public interface FmSupportService {

	public PageInfoBean<ArticleVoInter> queryArticleByParam(ConditionParam conditionParam, HttpServletRequest request) throws Exception ;
}
