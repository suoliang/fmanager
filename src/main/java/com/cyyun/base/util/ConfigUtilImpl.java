package com.cyyun.base.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.fm.service.UserConfigService;
import com.cyyun.fm.service.bean.UserConfigBean;
import com.cyyun.fm.service.exception.UserConfigException;

/**
 * <h3>用户配置工具类实现</h3>
 * 
 * @author xijq
 * @version 1.0.0
 * 
 */
@Component("configUtil")
public class ConfigUtilImpl implements ConfigUtil{

	private static final Logger log = LoggerFactory.getLogger(ConfigUtilImpl.class);

	@Autowired
	private UserConfigService userConfigService;
	
	private Map<String,UserConfigBean> confMap;
	
	public ConfigUtilImpl(){
		this.confMap=new HashMap<String,UserConfigBean>();
	}
	
	private UserConfigBean getUserConfig(Integer userId,String key){
		String mapKey=userId+"_"+key;
		UserConfigBean conf=confMap.get(mapKey);
		if(conf==null){
			try {
				conf=this.userConfigService.findUserConfig(userId, key);
			} catch (UserConfigException e) {
				log.error(e.getMessage(), e);
			}
			confMap.put(mapKey, conf);
		}
		return conf;
	}
	
	@Override
	public String getValue(Integer userId,String key){
		if(userId==null || key==null){
			return null;
		}
		UserConfigBean conf=this.getUserConfig(userId, key);
		return conf==null ? null : conf.getValue();
	}
	
}