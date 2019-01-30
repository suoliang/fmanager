package com.cyyun.base.filter;

import com.cyyun.authim.service.bean.UserBean;
import com.cyyun.authim.shiro.AuthUtil;
import com.cyyun.customer.service.bean.CustomerBean;

/**
 * <h3>FMANGER 上下文</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class FMContext {
	private static final ThreadLocal<FMContext> fmContext = new ThreadLocal<FMContext>();

	private String systemCode;
	private Integer userId;
	private Integer customerId;
	private CustomerBean customer;

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public CustomerBean getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBean customer) {
		this.customer = customer;
	}

	//########################## Business Method ##########################//

	//########################## Context Static Method ##########################//

	public static UserBean getLoginUser() {
		return AuthUtil.getLoginUser();
	}

	//########################## Context Method (Ignore) ##########################//

	public static FMContext getCurrent() {
		FMContext context = fmContext.get();

		if (context == null) {
			context = new FMContext();
			setCurrent(context);
		}

		return context;
	}

	public static void setCurrent(FMContext context) {
		fmContext.set(context);
	}

	public static void resetCurrent() {
		fmContext.remove();
	}
}