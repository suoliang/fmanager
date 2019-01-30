package com.cyyun.fm.base.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.authim.service.OrganizationService;
import com.cyyun.authim.service.UserService;
import com.cyyun.authim.service.bean.OrganizationBean;
import com.cyyun.authim.service.bean.UserBean;
import com.cyyun.base.filter.FMContext;
import com.cyyun.base.util.CyyunSqlUtil;
import com.cyyun.customer.service.CustomerConfigService;
import com.cyyun.customer.service.CustomerService;
import com.cyyun.customer.service.bean.CustomerBean;
import com.cyyun.fm.service.exception.UserConfigException;

/**
 * <h3>用户配置控制器</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Component
public class ShareUserSupport {

	@Autowired
	private CustomerConfigService customerConfigService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationService organizationService;

	public List<UserBean> searchUser(String keyword) {
		CyyunSqlUtil.dealSql(keyword);
		List<UserBean> resultsList=new ArrayList<UserBean>();
		List<Integer> userIds = customerConfigService.listUidByCid(FMContext.getCurrent().getCustomerId());
		List<UserBean> users = userService.listUserById(userIds);
		for (UserBean userBean : users) {
			if (userBean.getRealName().contains(keyword)) {
				resultsList.add(userBean);
			}
		}
		return resultsList;
	}

	public List<UserBean> listUser(Integer[] orgIds) throws UserConfigException {
		List<UserBean> users = null;
		if (ArrayUtils.isEmpty(orgIds)) {
			List<Integer> userIds = customerConfigService.listUidByCid(FMContext.getCurrent().getCustomerId());
			users = userService.listUserById(userIds);
		} else {
			users = userService.listUserByOrgId(orgIds);
		}
		return users;
	}

	public List<OrganizationBean> listOrganization(Integer orgId) throws UserConfigException {

		List<OrganizationBean> organizations = null;

		if (orgId == null) {
			Integer customerId = FMContext.getCurrent().getCustomerId();
			CustomerBean customer = customerService.findCustomer(customerId);
			Integer[] orgIds = customer.getOrgIds();
			if (ArrayUtils.isNotEmpty(orgIds)) {
				orgId = orgIds[0];
			}
		}

		if (orgId != null) {
			organizations = organizationService.listOrganizationByParentId(orgId);
		}

		return organizations;
	}
}