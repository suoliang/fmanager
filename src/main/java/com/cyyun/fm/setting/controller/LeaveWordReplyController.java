package com.cyyun.fm.setting.controller;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.DateFormatFactory;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.service.bean.MessageBoardBean;
import com.cyyun.fm.service.bean.MessageBoardQueryBean;
import com.cyyun.fm.service.bean.MessageBoardReplyBean;
import com.cyyun.fm.setting.bean.MessageBoardParam;

@Controller()
@RequestMapping("setting")
public class LeaveWordReplyController extends BaseController {
	private static final int PAGE_NO = 1;
	private static final int PAGE_SIZE = 10;

	@Autowired
	private LeaveWordReplyControllerSupport support;
	
	@RequestMapping(value = { "leaveWordReply/index" })
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
			return view("/localsetting/leaveWordReplyInfo").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载留言回复列表失败");
		}
	}	
	/**
	 * 查询单个留言信息
	 * @param messageBoardBean
	 * @return
	 */
	@RequestMapping(value = { "leaveWordReply/findMessageBoard" })
	@ResponseBody
	public MessageBean findMessageBoard(MessageBoardBean messageBoardBean) {
		MessageBoardBean messageBoard=null;
		try {
			messageBoard=support.findMessageBoard(messageBoardBean);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "操作成功！",messageBoard);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "操作失败！");
		}
	}	
	/**
	 * 添加留言回复信息
	 * @param messageBoardReplyBean
	 * @return
	 */
	@RequestMapping(value = { "leaveWordReply/addMessageBoardReply" })
	@ResponseBody
	public MessageBean addMessageBoardReply(MessageBoardReplyBean messageBoardReplyBean) {
		messageBoardReplyBean.setReplier(FMContext.getLoginUser().getUsername());//设置留言回复人真实姓名
		messageBoardReplyBean.setCreaterId(FMContext.getLoginUser().getId());//设置留言回复人ID
		messageBoardReplyBean.setParentId(1);//回复父ID  不清楚其作用  先默认设为1
		MessageBoardBean messageBoardBean=new MessageBoardBean();
		messageBoardBean.setId(messageBoardReplyBean.getMsgId());
		messageBoardBean.setStatus(1);//对应留言状态设置为   已处理--1
		try {
			support.addMessageBoardReply(messageBoardReplyBean);
			support.updateMessageBoard(messageBoardBean);//修改对应留言状态
			return buildMessage(MESSAGE_TYPE_SUCCESS, "操作成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buildMessage(MESSAGE_TYPE_ERROR, "添加留言回复信息失败！");
		}
	}	
	/**
	 * 查询留言列表
	 * @param messageBoardParam
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = { "leaveWordReply/queryList" })
	public ModelAndView queryList(MessageBoardParam messageBoardParam, Integer pageNo, Integer pageSize) {
		try {
			Date endTime = null;
			Date startTime = null;
			if (StringUtils.isNotBlank(messageBoardParam.getStartTime()))
				startTime = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern).parse(messageBoardParam.getStartTime() + " 00:00:00");
			if (StringUtils.isNotBlank(messageBoardParam.getEndTime()))
				endTime = DateFormatFactory.getDateFormat(DateFormatFactory.DatePattern.TimePattern).parse(messageBoardParam.getEndTime() + " 23:59:59");
			if (pageNo == null)
				pageNo = PAGE_NO;
			if (pageSize == null)
				pageSize = PAGE_SIZE;
			MessageBoardQueryBean query = new MessageBoardQueryBean();
			query.setStartTime(startTime);
			query.setEndTime(endTime);
			query.setStatus(messageBoardParam.getStatus());
			query.setPageNo(pageNo);
			query.setPageSize(pageSize);
			PageInfoBean<MessageBoardBean> pageInfo = support.queryBriefByPage(query);
			return view("/localsetting/leaveWordReply-paging-article").addObject("pageInfo", pageInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return message(MESSAGE_TYPE_ERROR, "加载留言回复列表失败");
		}
	}

	/**
	 * 查询留言详情
	 * @param messageBoardBean
	 * @return
	 */
	@RequestMapping(value = { "leaveWordReply/leaveWordInfoDetail" })
	public ModelAndView leaveWordInfoDetail(MessageBoardBean messageBoardBean,HttpServletRequest request) {
		MessageBoardBean messageBoard=null;
		MessageBoardReplyBean messageBoardReplyBean=null;
		try {
			messageBoard=support.findMessageBoard(messageBoardBean);
			messageBoardReplyBean=support.findMessageBoardReply(messageBoardBean);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		request.setAttribute("currentPageNo", request.getParameter("pageNo"));
		return view("/localsetting/leaveWordInfoReplyDetail").addObject("messageBoard", messageBoard).addObject("messageBoardReplyBean", messageBoardReplyBean);
	}
}
