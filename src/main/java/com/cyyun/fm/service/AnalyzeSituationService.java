package com.cyyun.fm.service;

import java.util.Map;

import com.cyyun.customer.service.bean.CustConfigBean;

public interface AnalyzeSituationService {
	/**
	 * 首页舆情概况统计，概况分析模块统计
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public Map<String, Long> analyzeSituation(String startDate, String endDate, Integer[] topic, CustConfigBean custConfig) throws Exception;
}
