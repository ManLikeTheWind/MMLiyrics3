package com.dxiang.mring3.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class TimeUtil {
	public final static SimpleDateFormat Operation = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm:ss");
//	public final static SimpleDateFormat Operationnew = new SimpleDateFormat(
//			"dd:MM:yyyy HH:mm:ss");
	public final static SimpleDateFormat Operationnew530 = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");
	public final static SimpleDateFormat timerFormatter = new SimpleDateFormat(
			"yyyy.MM.dd");

	public static String bracelettime(String str) throws ParseException {
		long millionSeconds = Operation.parse(str).getTime();// 毫秒
		Date curDate = new Date(millionSeconds);
		String strtime = Operationnew530.format(curDate);
		return strtime;
	}

	public static int daysBetween(String timeStr) {
		long between_days = 0;
		if ((!timeStr.equals("string{}")) && (!timeStr.equals(""))) {
			Date date2 = null;
			try {
				date2 = timerFormatter.parse(timeStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Date date3 = new Date(System.currentTimeMillis());
			String strtime = timerFormatter.format(date3);
			Date date1 = null;
			try {
				date1 = timerFormatter.parse(strtime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(date1);
			long time1 = cal.getTimeInMillis();
			cal.setTime(date2);
			long time2 = cal.getTimeInMillis();
			between_days = (time2 - time1) / (1000 * 3600 * 24);
		}
		int dayresult = Integer.parseInt(String.valueOf(between_days));
		return (dayresult < 0 ? 0 : dayresult) + 1;
	}

}
