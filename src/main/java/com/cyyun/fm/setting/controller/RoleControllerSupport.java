package com.cyyun.fm.setting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cyyun.authim.service.RoleService;
import com.cyyun.authim.service.bean.RoleBean;
import com.cyyun.authim.service.bean.RoleQueryBean;
import com.cyyun.base.filter.FMContext;
import com.cyyun.common.core.bean.PageInfoBean;
import com.cyyun.customer.constant.DataCode;
import com.cyyun.customer.service.CustomerConfigService;

@Component
public class RoleControllerSupport {
	
	@Value("${system.code}")
	private String systemCode;
	@Autowired
	private RoleService roleService;
	@Autowired
	private CustomerConfigService customerConfigService;
	/**
	 * 查询当前系统当前客户所有角色
	 * @return
	 */
	public PageInfoBean<RoleBean> queryAllRole() {
		Integer[] roleIds = customerConfigService.findDataIdsByCid(FMContext.getCurrent().getCustomerId(), DataCode.ROLE);
		RoleQueryBean queryBean = new RoleQueryBean();
		queryBean.setNeedPaging(false);
		queryBean.setSystemCode(systemCode);
		queryBean.setRoleIds(roleIds);
		queryBean.setCreaterId(FMContext.getCurrent().getCustomerId());
		PageInfoBean<RoleBean> page = roleService.queryRoleByPage(queryBean);
		return page;
	}
}
