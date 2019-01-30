package com.cyyun.fm.setting.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.fm.service.FmMessageBoardReplyService;
import com.cyyun.fm.service.FmMessageBoardService;
import com.cyyun.fm.service.bean.MessageBoardBean;
import com.cyyun.fm.service.bean.MessageBoardQueryBean;
import com.cyyun.fm.service.bean.MessageBoardReplyBean;
import com.cyyun.fm.service.exception.FmMessageBoardException;
import com.cyyun.fm.service.exception.FmMessageBoardReplyException;

@Component
public class LeaveWordControllerSupport {
	@Autowired
	private FmMessageBoardService fmMessageBoardService;
	@Autowired
	private FmMessageBoardReplyService fmMessageBoardReplyService;
	
	
	public PageInfoBean<MessageBoardBean> queryBriefByPage(MessageBoardQueryBean query) throws FmMessageBoardException {
		PageInfoBean<MessageBoardBean> pageInfo = fmMessageBoardService.queryMessageBoard(query);
		return pageInfo;
	}
	
	public MessageBoardBean findMessageBoard(MessageBoardBean query) throws FmMessageBoardException {
		MessageBoardBean messageBoardBean = fmMessageBoardService.findMessageBoard(query.getId());
		return messageBoardBean;
	}
	public MessageBoardReplyBean findMessageBoardReply(MessageBoardBean query) throws FmMessageBoardReplyException {
		MessageBoardReplyBean messageBoardReplyBean = fmMessageBoardReplyService.findMessageBoardReply(query.getId());
		return messageBoardReplyBean;
	}
	public void addMessageBoard(MessageBoardBean query) throws FmMessageBoardException {
		fmMessageBoardService.addMessageBoard(query);
	}
	
}
