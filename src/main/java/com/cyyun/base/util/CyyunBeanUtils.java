package com.cyyun.base.util;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

/**
 * 支持java.util.Date数据类型为空的对象属性复制器
 * 
 * @author yaodw
 * 
 */
public class CyyunBeanUtils extends BeanUtils {

	public CyyunBeanUtils() {

	}

	static {
		// 注册sql.date的转换器，即允许BeanUtils.copyProperties时的源目标的sql类型的值允许为空
		ConvertUtils.register(new CyyunDateConvert(), java.util.Date.class);
		// ConvertUtils.register(new
		// SqlTimestampConverter(),java.sql.Timestamp.class);
		// 注册util.date的转换器，即允许BeanUtils.copyProperties时的源目标的util类型的值允许为空
		// ConvertUtils.register(new UtilDateConverter(), java.util.Date.class);
	}

	public static void copyProperties(Object target, Object source)
			throws InvocationTargetException, IllegalAccessException {
		BeanUtils.copyProperties(target, source);
	}
	
	public static <T> T copyProperties(Class<T> clas, Object source)
			throws InvocationTargetException, IllegalAccessException, InstantiationException {
		T t = clas.newInstance();
		BeanUtils.copyProperties(t, source);
		return t;
	}
	
}
