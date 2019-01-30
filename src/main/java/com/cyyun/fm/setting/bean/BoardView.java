package com.cyyun.fm.setting.bean;

import java.util.List;

import com.cyyun.authim.service.bean.UserBean;
import com.cyyun.common.core.util.ListBuilder;
import com.cyyun.fm.service.bean.BoardBean;

/**
 * <h3>板块</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class BoardView extends BoardBean {
	private static final long serialVersionUID = 1L;

	private List<UserBean> users = ListBuilder.newArrayList();

	public List<UserBean> getUsers() {
		return users;
	}

	public void setUsers(List<UserBean> users) {
		this.users = users;
	}
}