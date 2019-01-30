package com.cyyun.base.velocity;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;

import com.cyyun.base.util.ConfigUtil;
import com.cyyun.common.core.util.SpringContextUtil;

/**
 * <h3>Velocity 用户配置工具类</h3>
 * 
 * @author xijq
 * @version 1.0.0
 * 
 */
@DefaultKey("conf")
@ValidScope(Scope.APPLICATION)
public class ConfigTool implements ConfigUtil {

	private ConfigUtil configUtil;

	public ConfigTool() {
	}

	public ConfigUtil getConfigUtil() {
		if (configUtil == null) {
			this.configUtil = SpringContextUtil.getBean(ConfigUtil.class);
		}
		return configUtil;
	}

	@Override
	public String getValue(Integer userId, String key) {
		return getConfigUtil().getValue(userId, key);
	}

}