package com.cyyun.fm.setting.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.authim.service.UserService;
import com.cyyun.authim.service.bean.UserBean;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.common.core.util.BeanUtil;
import com.cyyun.fm.constant.BoardType;
import com.cyyun.fm.service.WebpageBoardService;
import com.cyyun.fm.service.bean.BoardBean;
import com.cyyun.fm.service.bean.BoardQueryBean;
import com.cyyun.fm.service.exception.WebpageBoardException;
import com.cyyun.fm.setting.bean.BoardView;

/**
 * <h3>设置首页控制器支持</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Component
public class SettingHomepageSupport {

	@Autowired
	private UserService userService;

	@Autowired
	private WebpageBoardService boardService;

	public PageInfoBean<BoardBean> queryBoard(Integer createrId, String name, Integer currentpage, Integer pagesize) throws WebpageBoardException {
		BoardQueryBean query = new BoardQueryBean();

		query.setType(BoardType.HOMEPAGE);
		query.setUserId(createrId);
		if (name != null) {
			query.setName(CyyunSqlUtil.dealSql(name.trim()));
		}
		query.setPageNo(currentpage);
		query.setPageSize(pagesize);

		boardService.checkUserBoard(createrId, BoardType.HOMEPAGE);//确保用户配置存在
		PageInfoBean<BoardBean> pageInfo = boardService.queryUserBoard(query);
		return pageInfo;
	}

	public BoardView shareBoard(Integer boardId, Integer[] userIds) throws WebpageBoardException {
		BoardBean board = boardService.shareBoard(boardId, userIds);

		BoardView boardView = BeanUtil.copy(board, BoardView.class);

		if (board.getUserIds() != null) {
			List<UserBean> users = userService.listUserById(Arrays.asList(board.getUserIds()));
			boardView.setUsers(users);
		}

		return boardView;
	}
}