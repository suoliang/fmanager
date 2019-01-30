package com.cyyun.base.util;

import java.util.List;

import com.cyyun.base.service.bean.ConstantBean;

/**
 * <h3>常量工具类接口</h3>
 * 
 * @author xijq
 * @version 1.0.0
 * 
 */
public interface ConstUtil {

	public List<ConstantBean> list(String type);
	
	public String getName(String type, String value);
	
	public String getName(String type, Integer value);
	
	public String getName(String type, String[] values, String separator);
	
	public String getName(String type, Integer[] values, String separator);
	
	
}