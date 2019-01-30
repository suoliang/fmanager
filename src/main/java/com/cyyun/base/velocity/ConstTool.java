package com.cyyun.base.velocity;

import java.util.List;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;

import com.cyyun.base.service.bean.ConstantBean;
import com.cyyun.base.util.ConstUtil;
import com.cyyun.common.core.util.SpringContextUtil;

/**
 * <h3>Velocity 常量工具类</h3>
 * 
 * @author xijq
 * @version 1.0.0
 * 
 */
@DefaultKey("const")
@ValidScope(Scope.APPLICATION)
public class ConstTool implements ConstUtil {

	private ConstUtil constUtil;

	public ConstTool() {
	}

	public ConstUtil getConstUtil() {
		if (constUtil == null) {
			this.constUtil = SpringContextUtil.getBean(ConstUtil.class);
		}
		return constUtil;
	}

	@Override
	public List<ConstantBean> list(String type) {
		return getConstUtil().list(type);
	}

	@Override
	public String getName(String type, String value) {
		return getConstUtil().getName(type, value);
	}

	@Override
	public String getName(String type, String[] values, String separator) {
		return getConstUtil().getName(type, values, separator);
	}

	@Override
	public String getName(String type, Integer value) {
		return getConstUtil().getName(type, value);
	}

	@Override
	public String getName(String type, Integer[] values, String separator) {
		return getConstUtil().getName(type, values, separator);
	}

}