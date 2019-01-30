package com.cyyun.fm.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cyyun.base.filter.FMContext;
import com.cyyun.webmon.utils.StatsClient;
import com.cyyun.webmon.utils.StatsData;


@Service
public class QueryLogServiceImpl implements QueryLogService{
	
	@Autowired(required = false)
	private StatsClient statsClient;
	
	@Value("${recordLogService}")
	private Boolean recordLogService;
	
	/**
	 * @author LIUJUNWU
	 */
	@SuppressWarnings("static-access")
	@Override
	public void saveInterfaceAccessLog(Long accessTime, String interfaceType, Long recordCriticalTime) {
		if (accessTime > recordCriticalTime && recordLogService) {
			String userName = FMContext.getCurrent().getLoginUser().getUsername();
			Map<String, String> tagMap = new HashMap<String, String>();
			tagMap.put("interfaceType", interfaceType);
			tagMap.put("user", userName);
			//添加语法资料	http://chonseng.eicp.net:8000/w/techniques/statsclient/
			statsClient.saveStats(new StatsData("fm30.querylog.interfaces", accessTime , tagMap));
			System.err.println(userName +"-------"+ interfaceType+"-----保存日志成功了");
		}
	}
	
	

}
