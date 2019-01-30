package com.cyyun.base.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cyyun.base.service.ConstantService;
import com.cyyun.base.service.bean.ConstantBean;
import com.cyyun.base.service.exception.ConstantServiceException;

/**
 * <h3>常量工具类实现</h3>
 * 
 * @author xijq
 * @version 1.0.0
 * 
 */
@Component("constUtil")
public class ConstUtilImpl implements ConstUtil{

	private static final Logger log = LoggerFactory.getLogger(ConstUtilImpl.class);
	
	@Autowired
	private ConstantService constantService;
	
	private Map<String,List<ConstantBean>> constMap;
	
	public ConstUtilImpl(){
		this.constMap=new HashMap<String,List<ConstantBean>>();
	}

	@Override
	public List<ConstantBean> list(String type){
		if(type==null){
			return null;
		}
		List<ConstantBean> result=constMap.get(type);
		if(result==null){
			try {
				result=this.constantService.listConstantByType(type);
				constMap.put(type, result);
			} catch (ConstantServiceException e) {
				log.error(e.getMessage(),e);
			}
		}
		return result;
	}
	
	@Override
	public String getName(String type, String value){
		if(type==null || value==null){
			return null;
		}
		List<ConstantBean> lt=this.list(type);
		for(ConstantBean bean:lt){
			if(bean.getValue().equals(value)){
				return bean.getName();
			}
		}
		if("0".equals(value)){
			return null;
		}
		return value;
	}
	
	@Override
	public String getName(String type, String[] values, String separator){
		if(type==null || values==null || values.length==0){
			return null;
		}
		if(separator==null){
			separator=",";
		}
		StringBuffer sb=new StringBuffer();
		List<ConstantBean> lt=this.list(type);
		boolean flag=false;
		for(String v:values){
			flag=false;
			for(ConstantBean bean:lt){
				if(bean.getValue().equals(v)){
					flag=true;
					sb.append(bean.getName()).append(separator);
					break;
				}
			}
			if(!flag){
				sb.append(v).append(separator);
			}
		}
		return sb.substring(0, sb.lastIndexOf(separator));
	}

	@Override
	public String getName(String type, Integer value) {
		if(value==null){
			return null;
		}
		return this.getName(type, value.toString());
	}

	@Override
	public String getName(String type, Integer[] values, String separator) {
		if(values==null || values.length==0){
			return null;
		}
		String[] strs=new String[values.length];
		for(int i=0; i<values.length; i++){
			strs[i]=values[i].toString();
		}
		return this.getName(type, strs, separator);
	}
}