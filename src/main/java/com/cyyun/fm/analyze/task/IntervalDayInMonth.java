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
 * <h3>近一月的 天时间段</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class IntervalDayInMonth implements SyncStatTimeInterval {

	private Integer delay = 1;

	@Override
	public List<SyncStatTimePoint> calculate() {
		try {
			List<SyncStatTimePoint> result = ListBuilder.newArrayList();

			Integer days = 30;

			Date today = DateUtils.addHours(Calendar.getInstance().getTime(), -delay);
			today = DateUtils.addDays(Calendar.getInstance().getTime(), -days);

			for (int i = 1; i <= 30; i++) {
				Date beginTime = DateUtils.parseDate(DateFormatUtils.format(today, "yyyy-MM-dd 00:00:00 000"), "yyyy-MM-dd HH:mm:ss SSS");
				Date endTime = DateUtils.parseDate(DateFormatUtils.format(today, "yyyy-MM-dd 23:59:59 999"), "yyyy-MM-dd HH:mm:ss SSS");

				SyncStatTimePoint timePoint = new SyncStatTimePoint(beginTime, endTime);
				result.add(timePoint);

				today = DateUtils.addDays(today, 1);//1天递增
			}

			return result;
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
		IntervalDayInMonth todayDelayOneHour = new IntervalDayInMonth();
		List<SyncStatTimePoint> list = todayDelayOneHour.calculate();
		for (SyncStatTimePoint timePoint : list) {
			System.out.println("[beginTime=" + DateFormatUtils.format(timePoint.getBeginTime(), "yyyy-MM-dd HH:mm:ss SSS") + ", endTime=" + DateFormatUtils.format(timePoint.getEndTime(), "yyyy-MM-dd HH:mm:ss SSS") + "]");
		}
	}

}
