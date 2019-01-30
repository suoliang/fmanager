package com.cyyun.fm.service;

import java.util.List;

import com.cyyun.base.service.bean.ArticleBean;
import com.cyyun.base.service.exception.CustTopicServiceException;
import com.cyyun.fm.vo.ArticleNumVo;

/** 
 * @author  SuoLiang  
 * @version 2016年9月19日
 */
public interface CountArticleNumService {
	
	public ArticleNumVo getArticleCountMap(List<ArticleBean> list) throws CustTopicServiceException ;
	
}
