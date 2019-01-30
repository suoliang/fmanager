package com.cyyun.fm.service;


public interface QueryLogService {
	/**
	 * 保存查询日志
	 * @param accessTime
	 * @param interfaceType
	 * @param recordCriticalTime
	 */
	public void saveInterfaceAccessLog(Long accessTime, String interfaceType, Long recordCriticalTime);
}
