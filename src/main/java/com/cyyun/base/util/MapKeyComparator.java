package com.cyyun.base.util;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * <h3>工具类</h3>
 * 
 * @author LIUJUNWU
 * @version 1.0.0
 * 
 */
public class MapKeyComparator implements Comparator<String> {
	/**
	 * 数字排序(升序)
	 */
	@Override
	public int compare(String o1, String o2) {
		Integer int1 = Integer.parseInt(o1);
		Integer int2 = Integer.parseInt(o2);

		//如果有空值，直接返回0
		if (o1 == null || o2 == null)
			return 0;
		return int1.compareTo(int2);

	}

	/**
	 * 使用 Map按key进行排序
	 * @param map
	 * @return
	 */
	public static Map<String, Long> sortMapByKey(Map<String, Long> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<String, Long> sortMap = new TreeMap<String, Long>(new MapKeyComparator());

		sortMap.putAll(map);

		return sortMap;
	}
}
