package com.cyyun.fm.people.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.cyyun.base.filter.FMContext;
import com.cyyun.common.core.base.BaseController;
import com.cyyun.common.core.bean.MessageBean;
import com.cyyun.fm.constant.BoardType;
import com.cyyun.fm.service.bean.BoardBean;
import com.cyyun.fm.service.bean.ModuleBean;
import com.cyyun.fm.service.exception.WebpageBoardException;

/**
 * <h3>首页控制器</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Controller
@RequestMapping("/people")
public class PeoplefocusController extends BaseController {

	@Autowired
	private PeoplefocusSupport support;

	//###########################################   manager   ###########################################//

	@RequestMapping("peopleMgr")
	public ModelAndView realIdentityMgr() {
		return view("/peoplefocus/index");
	}

	//###########################################   index   ###########################################//

	/**
	 * <h3>配置页面</h3>
	 * 
	 * @return 页面
	 */
	@RequestMapping("index")
	public ModelAndView config() {
		return view("/peoplefocus/index");
	}

	/**
	 * <h3>检索用户所有可见板块</h3>
	 * 
	 * @return JSON
	 */
	@RequestMapping("listViewBoard")
	@ResponseBody
	public MessageBean listViewBoard() {
		try {
			List<BoardBean> boards = support.listUserBoard(FMContext.getCurrent().getUserId());
			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功检索网页板块配置", boards);
		} catch (WebpageBoardException e) {
			return buildMessage(MESSAGE_TYPE_ERROR, "检索网页板块配置失败");
		}
	}

	/**
	 * <h3>检索用户所有可配置板块</h3>
	 * 
	 * @return JSON
	 */
	@RequestMapping("listAllBoard")
	@ResponseBody
	public MessageBean listAllBoard() {
		try {
			List<BoardBean> boards = support.listUserBoard(FMContext.getCurrent().getUserId());

			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功检索网页板块配置", boards);
		} catch (WebpageBoardException e) {
			return buildMessage(MESSAGE_TYPE_ERROR, "检索网页板块配置失败");
		}
	}

	/**
	 * <h3>检索模板配置</h3>
	 * 
	 * @return JSON
	 */
	@RequestMapping("listModule")
	@ResponseBody
	public MessageBean listModule() {
		try {
			List<ModuleBean> modules = support.listModule();

			return buildMessage(MESSAGE_TYPE_SUCCESS, "成功检索网页板块配置", modules);
		} catch (WebpageBoardException e) {
			return buildMessage(MESSAGE_TYPE_ERROR, "检索网页板块配置失败");
		}
	}

	/**
	 * <h3>首页页面</h3>
	 * 
	 * @return 页面
	 */
	@RequestMapping("input")
	public ModelAndView boardConfig(Integer boardId) {
		ModelAndView view = view("/homepage/input");
		try {
			List<ModuleBean> modules = support.listModule();

			if (boardId != null) {
				view.addObject("board", support.findBoard(boardId));
			}

			return view.addObject("modules", modules);

		} catch (WebpageBoardException e) {
			return message(MESSAGE_TYPE_ERROR, e.getErrorMsg());
		}
	}

	//###########################################   board   ###########################################//

	/**
	 * <h3>创建页面板块</h3>
	 * 
	 * @param board 网页板块
	 */
	@RequestMapping(value = { "saveBoard" })
	@ResponseBody
	public MessageBean saveBoard(BoardBean board) {
		try {
			board = support.saveBoard(board, FMContext.getCurrent().getUserId());

			return buildMessage(MESSAGE_TYPE_SUCCESS, "保存网页板块成功", board);
		} catch (WebpageBoardException e) {
			return buildMessage(MESSAGE_TYPE_ERROR, e.getErrorMsg());
		}
	}

	/**
	 * <h3>删除网页板块</h3>
	 * @param index 
	 * @param boardId 
	 */
	@RequestMapping(value = { "deleteBoard" })
	@ResponseBody
	public MessageBean deleteBoard(Integer boardId) {
		try {
			support.deleteBoard(boardId, FMContext.getCurrent().getUserId(), BoardType.HOMEPAGE);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "删除网页板块成功");

		} catch (WebpageBoardException e) {
			return buildMessage(MESSAGE_TYPE_ERROR, e.getErrorMsg());
		}
	}

	/**
	 * <h3>删除网页板块</h3>
	 * @param index 
	 * @param boardId 
	 */
	@RequestMapping(value = { "findBoard" })
	@ResponseBody
	public MessageBean findBoard(Integer id) {
		try {
			BoardBean board = support.findBoard(id);
			return buildMessage(MESSAGE_TYPE_SUCCESS, "删除网页板块成功", board);

		} catch (WebpageBoardException e) {
			return buildMessage(MESSAGE_TYPE_ERROR, e.getErrorMsg());
		}
	}

	/**
	 * <h3>删除网页板块</h3>
	 * @param index 
	 * @param boardId 
	 */
	@RequestMapping(value = { "updateBoardIndex" })
	@ResponseBody
	public MessageBean updateBoardIndex(Integer index, Integer boardId) {
		try {
			support.updateBoardIndex(index, boardId, FMContext.getCurrent().getUserId());
			return buildMessage(MESSAGE_TYPE_SUCCESS, "删除网页板块成功");

		} catch (WebpageBoardException e) {
			return buildMessage(MESSAGE_TYPE_ERROR, e.getErrorMsg());
		}
	}
}