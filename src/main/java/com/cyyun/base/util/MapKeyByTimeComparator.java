package com.cyyun.base.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.cyyun.base.service.bean.ArticleStatisticBean;
import com.cyyun.base.util.DateFormatFactory.DatePattern;

/**
 * 
 * key是日期类型,按照key升序排序
 */
public class MapKeyByTimeComparator implements Comparator<String> {
	/**
	 * 数字排序(升序),
	 * 支持yyyy-MM-dd
	 * 支持yyyy-MM-dd HH
	 */
	@Override
	public int compare(String o1, String o2) {
		SimpleDateFormat dateFormat;
		if (o1.length() == 13) {
			dateFormat = DateFormatFactory.getDateFormat(DatePattern.TimePatternHour);
		} else {
			dateFormat = DateFormatFactory.getDateFormat(DatePattern.DatePattern);
		}
		try {
			Long time1 = dateFormat.parse(o1).getTime();
			Long time2 = dateFormat.parse(o2).getTime();
			
			//如果有空值，直接返回0
			if (o1 == null || o2 == null)
				return 0;
			
			return time1.compareTo(time2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 使用 Map按key进行排序
	 * ①支持yyyy-MM-dd
	 * ②支持yyyy-MM-dd HH
	 * @param map
	 * @return
	 */
	public static Map<String, List<ArticleStatisticBean>> sortMapByKey(Map<String, List<ArticleStatisticBean>> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<String, List<ArticleStatisticBean>> sortMap = new TreeMap<String, List<ArticleStatisticBean>>(new MapKeyByTimeComparator());

		sortMap.putAll(map);

		return sortMap;
	}
}
