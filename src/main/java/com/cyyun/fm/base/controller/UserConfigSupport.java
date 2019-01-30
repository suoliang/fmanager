package com.cyyun.fm.base.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.fm.service.UserConfigService;
import com.cyyun.fm.service.bean.UserConfigBean;
import com.cyyun.fm.service.exception.UserConfigException;

/**
 * <h3>用户配置控制器</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
@Component
public class UserConfigSupport {

	@Autowired
	private UserConfigService userConfigService;

	public UserConfigBean set(Integer userId, String name, String key, String value) throws UserConfigException {
		return userConfigService.saveUserConfig(new UserConfigBean(userId, name, key, value));
	}
}