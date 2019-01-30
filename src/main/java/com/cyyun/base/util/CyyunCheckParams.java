package com.cyyun.base.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 查看调用接口最终参数
 * @author yaodw
 *
 */
public class CyyunCheckParams {

	private static final Logger logger = LoggerFactory.getLogger(CyyunCheckParams.class);
	
	@SuppressWarnings("rawtypes")
	public static  void checkParams(Object obj){
			Class clas = obj.getClass();
			Field[] fields = clas.getDeclaredFields();
			if(ArrayUtils.isNotEmpty(fields)){
				System.out.println("打印参数开始：--------------------------");
				for(Field field: fields){
					try {
						System.out.println(field.getName()+"="+BeanUtils.getProperty(obj, field.getName())+"\n");
					} catch (Exception e) {
						System.out.println("打印属性出错："+field.getName());
					}
				}
				System.out.println("打印参数结束：--------------------------");
			}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setObjectFieldsEmpty(Object obj,
			String... excludePropertys) {
		// 对obj反射
		Class objClass = obj.getClass();
		Method[] objmethods = objClass.getDeclaredMethods();
		Map objMeMap = new HashMap();
		for (int i = 0; i < objmethods.length; i++) {
			Method method = objmethods[i];
			objMeMap.put(method.getName(), method);
		}

		OK: for (int i = 0; i < objmethods.length; i++) {
			String methodName = objmethods[i].getName();
			if (methodName != null && methodName.startsWith("get")) {
				if (ArrayUtils.isNotEmpty(excludePropertys)) {
					for (String excludeProperty : excludePropertys) {
						if (excludeProperty.toLowerCase().equals(
								methodName.split("get")[1].toLowerCase())) {
							continue OK;
						}
					}
				}
				try {
					Object returnObj = objmethods[i].invoke(obj, new Object[0]);
					Method setmethod = (Method) objMeMap.get("set"
							+ methodName.split("get")[1]);
					if (returnObj != null) {
						returnObj = null;
						if (objmethods[i].getReturnType().equals(double.class)) {/**importance重要性是double类型，控制台报错处理*/
							returnObj = 0.0;
						}
					}
					setmethod.invoke(obj, returnObj);
				} catch (IllegalArgumentException e) {
					System.out.println(methodName.split("get")[1].toLowerCase());
					logger.error("setObjectFieldsEmpty{}", e);
				} catch (IllegalAccessException e) {
					System.out.println(methodName.split("get")[1].toLowerCase());
					logger.error("setObjectFieldsEmpty{}", e);
				} catch (InvocationTargetException e) {
					System.out.println(methodName.split("get")[1].toLowerCase());
					logger.error("setObjectFieldsEmpty{}", e);
				}  catch (NullPointerException e){
//					System.out.println(methodName.split("get")[1].toLowerCase()+"----------"+objMeMap.get("set"+ methodName.split("get")[1]));
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void setObjectFieldsEmpty(List list,
			String... excludePropertys) {
		if (CollectionUtils.isNotEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				setObjectFieldsEmpty(list.get(i), excludePropertys);
			}
		}
	}
	
	/**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
    
    /**
     * 校验手机号
     * 
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }
	
	public static void main(String[] args) {
		test test = new test();
		CyyunCheckParams.setObjectFieldsEmpty(test,"propert2");
		System.out.println(test);
//		CollectionUtils.
		Map map = new ConcurrentHashMap();
	}
}

 class test{
	public test(){
		
	}
	private String name = "test";
	private int propert2;
	private double propert3 = 1.2;
	private float propert4;
	private long propert5;
	private byte propert6;
	private char propert7 = 0;
	private List propert10;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getPropert4() {
		return propert4;
	}
	public void setPropert4(float propert4) {
		this.propert4 = propert4;
	}
	public double getPropert3() {
		return propert3;
	}
	public void setPropert3(double propert3) {
		this.propert3 = propert3;
	}
	public int getPropert2() {
		return propert2;
	}
	public void setPropert2(int propert2) {
		this.propert2 = propert2;
	}
	public long getPropert5() {
		return propert5;
	}
	public void setPropert5(long propert5) {
		this.propert5 = propert5;
	}
	public byte getPropert6() {
		return propert6;
	}
	public void setPropert6(byte propert6) {
		this.propert6 = propert6;
	}
	public char getPropert7() {
		return propert7;
	}
	public void setPropert7(char propert7) {
		this.propert7 = propert7;
	}
	public Object getPropert10() {
		return propert10;
	}
	public void setPropert10(List propert10) {
		this.propert10 = propert10;
	}
	@Override
	public String toString() {
		return "test [name=" + name + ", propert2=" + propert2 + ", propert3="
				+ propert3 + ", propert4=" + propert4 + ", propert5="
				+ propert5 + ", propert6=" + propert6 + ", propert7="
				+ propert7 + ", propert10=" + propert10 + "]";
	}
	
}
