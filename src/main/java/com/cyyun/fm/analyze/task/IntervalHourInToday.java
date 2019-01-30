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
 * <h3>今天的 小时时间段</h3>
 * 
 * @author GUOQIANG
 * @version 1.0.0
 */
public class IntervalHourInToday implements SyncStatTimeInterval {

	@Override
	public List<SyncStatTimePoint> calculate() {
		try {
			List<SyncStatTimePoint> result = ListBuilder.newArrayList();

			Date today = Calendar.getInstance().getTime();
			int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			today = DateUtils.addHours(today, -hours);//0点

			for (int i = 1; i <= hours; i++) {
				Date beginTime = DateUtils.parseDate(DateFormatUtils.format(today, "yyyy-MM-dd HH:00:00"), "yyyy-MM-dd HH:mm:ss");
				Date endTime = DateUtils.addHours(beginTime, 1);
				endTime = DateUtils.addMilliseconds(endTime, -1);

				SyncStatTimePoint timePoint = new SyncStatTimePoint(beginTime, endTime);
				result.add(timePoint);

				today = DateUtils.addHours(today, 1);//1小时递增
			}

			return result;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		IntervalHourInToday intervalTodayPastHour = new IntervalHourInToday();
		List<SyncStatTimePoint> list = intervalTodayPastHour.calculate();
		for (SyncStatTimePoint timePoint : list) {
			System.out.println("[beginTime=" + DateFormatUtils.format(timePoint.getBeginTime(), "yyyy-MM-dd HH:mm:ss SSS") + ", endTime=" + DateFormatUtils.format(timePoint.getEndTime(), "yyyy-MM-dd HH:mm:ss SSS") + "]");
		}
	}
}
