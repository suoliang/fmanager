package com.cyyun.base.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author yaodw
 *
 */
public class CyyunStringUtils {

	private static final Logger log = LoggerFactory.getLogger(CyyunStringUtils.class);
	/**
	 * 转换为map<String,String>
	 * @param map
	 * @return
	 */
	public static Map<String,String> request2Map(Map<String,String[]> map){
		if(map != null){
			Map<String,String> result = new HashMap<String,String>();
			Set<Entry<String,String[]>> set = map.entrySet();
			for(Iterator<Entry<String,String[]>> iterator = set.iterator();iterator.hasNext();){
				Entry<String,String[]> entry = iterator.next();
				String key = entry.getKey();
				String[] values = entry.getValue();
				if(values != null && values.length != 0){
					if(StringUtils.isNotBlank(values[0])){
						result.put(key, values[0]);
						if(values.length > 1){
							log.info("键："+key+"取值大于1");
						}
					}
				}
			}
			return result;
		}
		return null;
	}
	/**
	 *  转换为map<String,String> 指定key
	 * @param map
	 * @param args
	 * @return
	 */
	public static Map<String,String> request2Map(Map<String,String[]> map, String...args){
		if(map != null){
			Map<String,String> result = new HashMap<String,String>();
			Set<Entry<String,String[]>> set = map.entrySet();
			for(Iterator<Entry<String,String[]>> iterator = set.iterator();iterator.hasNext();){
				Entry<String,String[]> entry = iterator.next();
				String key = entry.getKey();
				if(ArrayUtils.isNotEmpty(args)){
					for(String arg: args){
						if(key.equals(arg)){
							String[] values = entry.getValue();
							if(values != null && values.length != 0){
								if(StringUtils.isNotBlank(values[0])){
									result.put(key, values[0]);
									if(values.length > 1){
										log.info("键："+key+"取值大于1");
									}
								}
							}
						}
					}
				}
			}
			return result;
		}
		return null;
	}
	/**
	 * 目前仅支持页面传递过来的字符串或字符串数组类型
	 * @param map
	 * @param bean
	 * @param args
	 */
	public static <T> void request2Bean(Map<String,String[]> map, T bean,String... args){
		try{
			if(map != null){
				Set<Entry<String,String[]>> set = map.entrySet();
				for(Iterator<Entry<String,String[]>> iterator = set.iterator();iterator.hasNext();){
					Entry<String,String[]> entry = iterator.next();
					String key = entry.getKey();
					String[] values = entry.getValue();
					if(values != null && values.length != 0){
						Matcher matcher = Pattern.compile("\\[\\]").matcher(key);
						boolean isMatcher = matcher.find();
						if(isMatcher)
							key = matcher.replaceAll("");
						if(ArrayUtils.isNotEmpty(args)){
							for(String arg: args){
								if(key.equals(arg)){
									setProperty(bean, values, isMatcher, arg);
									break;
								}
							}
						}else{
							setProperty(bean, values, isMatcher, key);
						}
						if(!isMatcher){
							if(values.length > 1){
								log.info("键："+key+"取值大于1");
							}
						}
					}
				}
			}
		}catch(Exception e){
			log.error("转换bean出错！", e);
		}
	}
	/**
	 * 应用于页面多行批处理数据
	 * @param map
	 * @param clas 目标bean class
	 * @param args
	 * @return
	 */
	public static <T> List<T> request2List(Map<String,String[]> map, Class<T> clas, String... args){
		try {
			if(map != null){
				Set<Entry<String,String[]>> set = map.entrySet();
				Object[] objArr = null;
				for(Iterator<Entry<String,String[]>> iterator = set.iterator();iterator.hasNext();){
					Entry<String,String[]> entry = iterator.next();
					String key = entry.getKey();
					String[] values = entry.getValue();
					if(ArrayUtils.isNotEmpty(values)){
						if(objArr == null) objArr = new Object[values.length];
						for(int i = 0; i < values.length; i ++){
							if(objArr[i] == null) objArr[i] = clas.newInstance();
							if(ArrayUtils.isNotEmpty(args)){
								for(String arg: args){
									if(key.equals(arg) && StringUtils.isNotBlank(values[i])){
										BeanUtils.setProperty(objArr[i], arg, values[i]);
										break;
									}
								}
							}
						}
					}
				}
				if(ArrayUtils.isNotEmpty(objArr)){
					List<T> beanList = new ArrayList<T>();
					for(int i = 0; i < objArr.length; i ++){
						beanList.add((T)objArr[i]);
					}
					return beanList;
				}
			}
		} catch (Exception e) {
			log.error("转换list出错！", e);
		}
		return null;
	}

	private static <T> void setProperty(T bean, String[] values,boolean isMatcher, String arg) 
											throws IllegalAccessException,InvocationTargetException {
		if(isMatcher){
			BeanUtils.setProperty(bean, arg, values);
		}else{
			if(StringUtils.isNotBlank(values[0]))
				BeanUtils.setProperty(bean, arg, values[0]);
		}
	}
	
	public static Integer integerValueOf(String str){
		if(StringUtils.isNotBlank(str)){
			return Integer.valueOf(str);
		}
		return null;
	}
	
	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
//		Type type = WarningRuleBean.class.getDeclaredField("mediaType").getGenericType();
//		if("class [Ljava.lang.Integer;".equals(type.toString()))
//			System.out.println("ok");
//		System.out.println(WarningRuleBean.class.getDeclaredField("keywords").getGenericType());
		Matcher matcher = Pattern.compile("\\[\\]").matcher("mediaType[]");
		System.out.println(matcher.replaceAll(""));
	}
}
