package com.cyyun.base.util;

import org.apache.commons.beanutils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CyyunDateConvert implements Converter {

	private static final Logger logger = LoggerFactory
			.getLogger(CyyunDateConvert.class);
	
	public CyyunDateConvert(){
		
	}
	
	@Override
	public Object convert(@SuppressWarnings("rawtypes") Class type, Object value) {
		try {
			if (value != null) 
					return value;
		} catch (Exception e) {
			logger.error("CyyunDateConvert.convert()::日期转换器出错！", e);
		}
		return null;
	}
}
