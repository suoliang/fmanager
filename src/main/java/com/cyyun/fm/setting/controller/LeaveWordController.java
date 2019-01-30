package com.cyyun.fm.setting.controller;



import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.base.filter.FMContext;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.service.bean.MessageBoardBean;
import com.cyyun.fm.service.bean.MessageBoardQueryBean;
import com.cyyun.fm.service.bean.MessageBoardReplyBean;
import com.cyyun.fm.setting.bean.MessageBoardParam;

@Controller()
@RequestMapping("setting")
public class LeaveWordController extends BaseController {
	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;

	@Autowired
	private LeaveWordControllerSupport support;
	/**
	 * 加载首页
	 * @param messageBoardBean
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = { "leaveWord/index" })
	public ModelAndView addSiteUrl(MessageBoardBean messageBoardBean, Integer pageNo, Integer pageSize) {
		try {
			if (pageNo == null) {
				pageNo = PAGE_NO;
			}
			if (pageSize == null) {
				pageSize = PAGE_SIZE;
			}
			MessageBoardQueryBean query=new MessageBoardQueryBean();
			
			query.setPageNo(pageNo);
			query.setPageSize(pageSize);
//			query.setCustId(FMContext.getCurrent().getCustomerId());//加cid过滤
			PageInfoBean<MessageBoardBean> pageInfo=support.queryBriefByPage(query);
			return view("/localsetting/leaveWordInfo").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载留言列表失败");
		}
	}
	/**
	 * 新增页面跳转
	 * @return
	 */
	@RequestMapping(value = { "leaveWord/leaveWordOn" })
	public ModelAndView leaveWordOn() {
		return view("/localsetting/leaveWord-add");
	}
	/**
	 * 新增留言
	 * @param messageBoardBean
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = { "leaveWord/leaveWordAdd" })
	public String leaveWordAdd(MessageBoardBean messageBoardBean,HttpServletRequest request) throws UnsupportedEncodingException  {
		String title = null;
		title = new String(request.getParameter("title").getBytes("ISO-8859-1"), "UTF-8");
		String mobile = null;
		mobile = new String(request.getParameter("mobile").getBytes("ISO-8859-1"), "UTF-8");
		String contacter = null;
		contacter = new String(request.getParameter("contacter").getBytes("ISO-8859-1"), "UTF-8");
		String content = null;
		content = new String(request.getParameter("content").getBytes("ISO-8859-1"), "UTF-8");
		
		messageBoardBean.setTitle(title);
		messageBoardBean.setMobile(mobile);
		messageBoardBean.setContacter(contacter);
		messageBoardBean.setContent(content);
		messageBoardBean.setStatus(0);//状态  0 - 未处理    1 - 已处理
		messageBoardBean.setCreaterId(FMContext.getCurrent().getUserId());
		messageBoardBean.setCustId(FMContext.getCurrent().getCustomerId());
		try {  
			support.addMessageBoard(messageBoardBean);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			message(MESSAGE_TYPE_ERROR, "添加留言失败");
		}
		return "redirect:/setting/leaveWord/index.htm";
	}
	/**
	 * 留言详情
	 * @param messageBoardBean
	 * @return
	 */
	@RequestMapping(value = { "leaveWord/leaveWordInfoDetail" })
	public ModelAndView leaveWordInfoDetail(MessageBoardBean messageBoardBean,HttpServletRequest request) {
		MessageBoardBean messageBoard=null;
		MessageBoardReplyBean messageBoardReplyBean=null;
		try {
			messageBoard=support.findMessageBoard(messageBoardBean);
			messageBoardReplyBean=support.findMessageBoardReply(messageBoardBean);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			message(MESSAGE_TYPE_ERROR, "加载留言详情");
		}
		
		request.setAttribute("currentPageNo", request.getParameter("pageNo"));
		return view("/localsetting/leaveWordInfoDetail").addObject("messageBoard", messageBoard).addObject("messageBoardReplyBean", messageBoardReplyBean);
	}
	/**
	 * 查询留言列表
	 * @param messageBoardParam
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = { "leaveWord/queryList" })
	public ModelAndView queryList(MessageBoardParam messageBoardParam, Integer pageNo, Integer pageSize) {
		try {
			if (pageNo == null) {
				pageNo = PAGE_NO;
			}
			if (pageSize == null) {
				pageSize = PAGE_SIZE;
			}
			MessageBoardQueryBean query=new MessageBoardQueryBean();
			query.setPageNo(pageNo);
			query.setPageSize(pageSize);
			PageInfoBean<MessageBoardBean> pageInfo=support.queryBriefByPage(query);
			return view("/localsetting/leaveWord-paging-article").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载留言回复列表失败");
		}
	}
}
