package com.cyyun.fm.recommend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.base.filter.FMContext;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.recommend.bean.RecommendParam;
import com.cyyun.process.service.bean.RecommendColumnBean;
import com.cyyun.process.service.bean.RecommendColumnQueryBean;
import com.cyyun.process.service.bean.RecommendContentBean;

/**
 * <h3>推荐</h3>
 * 
 * @author 
 * @version 1.0.0
 */
@Controller
@RequestMapping(value = { "recommend" })
public class RecommendController extends BaseController {
	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;
	
	@Autowired
	private RecommendSupport support;

	@RequestMapping("recommend")
	public ModelAndView recommend(RecommendParam recommendParam) {
		ModelAndView view = view("/recommend/recommend");
		try {
			view.addAllObjects(support.getOptions());
			recommendParam.setCurrentpage(PAGE_NO);
			recommendParam.setPagesize(PAGE_SIZE);
			Integer [] CustIds={FMContext.getCurrent().getCustomerId()};
			recommendParam.setCustIds(CustIds);//客户ID
			Integer [] userIds={FMContext.getLoginUser().getId()};
			recommendParam.setUserIds(userIds);//当前用户ID
			recommendParam.setType(null);
			recommendParam.setTimeType(recommendParam.getTimeType() != null ? recommendParam.getTimeType() : 1);
			PageInfoBean<RecommendContentBean> pageInfo=support.queryContentListPage(recommendParam);
			recommendParam.setCustIds(CustIds);//客户ID
 			recommendParam.setUserIds(userIds);//当前用户ID
			view.addObject("pageInfo", pageInfo);
			return view;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询推荐文章失败");
		}
	}
	
	/**
	 * <h3>文章详情</h3>
	 */
	@RequestMapping("/detail")
	private  ModelAndView findVehandedById(Integer rid){
		try {
			ModelAndView view = this.view("/recommend/detail");
			RecommendContentBean recommendContentBean=support.queryRecommendContentById(rid);
			RecommendColumnQueryBean query = new RecommendColumnQueryBean();
			query.setId(recommendContentBean.getColumnId());
			Integer [] CustIds={FMContext.getCurrent().getCustomerId()};
			Integer [] userIds={FMContext.getLoginUser().getId()};
			query.setCustIds(CustIds);
			query.setUserIds(userIds);
			PageInfoBean<RecommendColumnBean> ColumnBean=support.queryRecommendColumnListByPage(query);
			view.addObject("recommendContentBean",recommendContentBean);
			view.addObject("ColumnBean",ColumnBean.getData().get(0));
			return view;
		} catch (Exception e) {
			return message(MESSAGE_TYPE_ERROR, "查询失败");
		}
	}
	
	/**
	 * <h3>跳转文章详情页</h3>
	 */
	@RequestMapping("openArticleDetail")
	public ModelAndView openArticleDetail(String rid) {
		try {
			return view("/recommend/article-detail").addObject("rid", rid);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询文章失败");
		}
	}
	
	/**
	 * <h3>查找文章</h3>
	 */
	@RequestMapping("recommendArticle")
	public ModelAndView recommendArticle(RecommendParam recommendParam,RecommendColumnQueryBean query) {
		try {
			Integer [] CustIds={FMContext.getCurrent().getCustomerId()};
			recommendParam.setCustIds(CustIds);//客户ID
			Integer [] userIds={FMContext.getLoginUser().getId()};
			recommendParam.setUserIds(userIds);//当前用户ID
			recommendParam.setTimeType(recommendParam.getTimeType() != null ? recommendParam.getTimeType() : 1);
			if( recommendParam.getType()==0 || recommendParam.getColumnIds()==null || 
					recommendParam.getColumnIds().length==0  || recommendParam.getColumnIds()[0]==null){
				recommendParam.setColumnIds(null);
			}
			if(recommendParam.getType()==0){
				recommendParam.setType(null);
			}
			PageInfoBean<RecommendContentBean> pageInfo=support.queryContentListPage(recommendParam);
			return view("/recommend/paging-article").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "查询专题文章失败");
		}
	}
	
	/**
	 *
	 * 查找栏目
	 * @return
	 */
	@RequestMapping("recommendColumn")
	@ResponseBody
	public MessageBean showCusttopic(RecommendColumnQueryBean query) {
		try {
			Integer [] CustIds={FMContext.getCurrent().getCustomerId()};
			query.setCustIds(CustIds);//客户ID
			Integer [] userIds={FMContext.getLoginUser().getId()};
			query.setUserIds(userIds);//当前用户ID
			PageInfoBean<RecommendColumnBean> ColumnBean=support.queryRecommendColumnListByPage(query);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功", ColumnBean.getData());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "失败");
		}
	}
	
}