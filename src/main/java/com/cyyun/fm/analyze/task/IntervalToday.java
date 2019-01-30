package com.cyyun.fm.analyze.task;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.cyyun.base.task.SyncStatTimeInterval;
import com.cyyun.base.task.SyncStatTimePoint;
import com.cyyun.common.core.util.ListBuilder;

/**
 * <h3>今天 的 天时间段</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class IntervalToday implements SyncStatTimeInterval {

	private Integer delay = 1;

	@Override
	public List<SyncStatTimePoint> calculate() {
		try {
			Date today = DateUtils.addHours(Calendar.getInstance().getTime(), -delay);
			Date beginTime = DateUtils.parseDate(DateFormatUtils.format(today, "yyyy-MM-dd 00:00:00 000"), "yyyy-MM-dd HH:mm:ss SSS");
			Date endTime = DateUtils.parseDate(DateFormatUtils.format(today, "yyyy-MM-dd 23:59:59 999"), "yyyy-MM-dd HH:mm:ss SSS");

			return ListBuilder.add(new SyncStatTimePoint(beginTime, endTime)).build();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}

	public static void main(String[] args) {
		IntervalToday todayDelayOneHour = new IntervalToday();
		List<SyncStatTimePoint> list = todayDelayOneHour.calculate();
		for (SyncStatTimePoint timePoint : list) {
			System.out.println("[beginTime=" + DateFormatUtils.format(timePoint.getBeginTime(), "yyyy-MM-dd HH:mm:ss SSS") + ", endTime=" + DateFormatUtils.format(timePoint.getEndTime(), "yyyy-MM-dd HH:mm:ss SSS") + "]");
		}
	}

}
