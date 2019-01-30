package com.cyyun.fm.people.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.fm.constant.BoardType;
import com.cyyun.fm.people.constant.ModuleName;
import com.cyyun.fm.service.WebpageBoardService;
import com.cyyun.fm.service.bean.BoardBean;
import com.cyyun.fm.service.bean.ModuleBean;
import com.cyyun.fm.service.exception.WebpageBoardException;
import com.cyyun.process.service.PeopleService;
import com.cyyun.process.service.bean.PeopleBean;
import com.cyyun.process.service.bean.PeopleQueryBean;
import com.cyyun.process.service.exception.ServiceExcepiton;

/**
 * <h3>首页控制器支持</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Component
public class PeoplefocusSupport {

	@Autowired
	private PeopleService peopleService;

	@Autowired
	private WebpageBoardService boardService;

	//###########################################   manager   ###########################################//
	public PageInfoBean<PeopleBean> listVirtualIdentity() throws ServiceExcepiton {
		PeopleQueryBean query = new PeopleQueryBean();
		return peopleService.queryPeoplePage(query);
	}

	//###########################################   index   ###########################################//

	public List<BoardBean> listUserBoard(Integer userId) throws WebpageBoardException {
		return boardService.listUserBoard(userId, BoardType.PEOPLE);
	}

	public List<BoardBean> listHideDefBoard(Integer userId) throws WebpageBoardException {
		List<BoardBean> result = ListBuilder.newArrayList();
		List<Integer> defBoardIds = ListBuilder.newArrayList();
		List<Integer> existent = ListBuilder.newArrayList();

		List<BoardBean> defBoards = boardService.listDefBoard(BoardType.PEOPLE);
		for (BoardBean board : defBoards) {
			defBoardIds.add(board.getId());
		}
		for (BoardBean board : boardService.listUserBoard(userId, BoardType.PEOPLE)) {
			if (board.getIsDefault() && defBoardIds.contains(board.getId())) {//查找用户板块中所有已显示的系统板块
				existent.add(board.getId());
			}
		}
		for (BoardBean board : defBoards) {
			if (!existent.contains(board.getId())) {
				result.add(board);
			}
		}
		return result;
	}

	public List<ModuleBean> listModule() throws WebpageBoardException {
		return boardService.listModule();
	}

	public BoardBean findBoard(Integer boardId) throws WebpageBoardException {
		return boardService.findBoard(boardId);
	}

	public BoardBean saveBoard(BoardBean board, Integer userId) throws WebpageBoardException {
		if (board.getId() == null) {//添加板块
			if (boardService.findModuleByName(ModuleName.DEFAULT).getId() == board.getModuleId()) {//添加系统默认板块时，display=true
				for (String item : board.getParams().split("&")) {
					if (item.contains("def")) {
						Integer boardId = Integer.valueOf(item.split("=")[1]);
						boardService.updateUserBoardDisplay(true, boardId, userId, BoardType.PEOPLE);
						boardService.moveUserBoardToLast(userId, boardId, BoardType.PEOPLE);
						board = boardService.findBoard(boardId);
					}
				}
			} else {//添加新板块
				board.setCreaterId(userId);
				board.setNeedShow(true);
				board = boardService.addBoard(board, userId, BoardType.PEOPLE);
			}
		} else {//更新板块
			board = boardService.updateBoard(board);
		}
		return board;
	}

	public void deleteBoard(Integer boardId, Integer userId, Integer type) throws WebpageBoardException {
		BoardBean board = boardService.findBoard(boardId);
		if (board == null) //已经被删除
			return;

		if (board.getIsDefault()) {//删除系统默认板块时，display=false
			boardService.updateUserBoardDisplay(false, boardId, userId, BoardType.PEOPLE);

		} else {//删除板块
			boardService.deleteBoard(boardId);
		}
	}

	public void updateBoardIndex(Integer index, Integer boardId, int userId) throws WebpageBoardException {
		boardService.updateUserBoardIndex(index, boardId, userId, BoardType.PEOPLE);
	}

	public ModuleBean findModule(Integer id) throws WebpageBoardException {
		return boardService.findModule(id);
	}
}