package com.dxiang.mring3.utils;

import android.util.Log;

public class UtilLog {

	public static boolean isLog = true;

	/**
	 * 打印日志
	 */
	public final static void e(String tag, String msg) {
		if (isLog) {
			if (msg == null || "".equals(msg)) {
				msg = "UtilLog";
			}
			Log.e(tag, msg);
		}
	}

	public final static void e(String msg) {
		if (isLog) {
			if (msg == null || "".equals(msg)) {
				msg = "UtilLog";
			}
			Log.e(Commons.DEFAULT_LOG_TAG, msg);
		}
	}

	/**
	 * 打印日志
	 */
	public final static void d(String tag, String msg) {
		if (isLog) {
			if (msg == null || "".equals(msg)) {
				msg = "UtilLog";
			}
			Log.d(tag, msg);
		}
	}

	/**
	 * 打印日志
	 */
	public final static void d(String msg) {
		if (isLog) {
			if (msg == null || "".equals(msg)) {
				msg = "UtilLog";
			}
			Log.d(Commons.DEFAULT_LOG_TAG, msg);
		}
	}

	/**
	 * 打印日志
	 */
	public final static void w(String tag, String msg) {
		if (isLog) {
			if (msg == null || "".equals(msg)) {
				msg = "UtilLog";
			}
			Log.d(tag, msg);
		}
	}

	/**
	 * 打印日志
	 */
	public final static void w(String msg) {
		if (isLog) {
			if (msg == null || "".equals(msg)) {
				msg = "UtilLog";
			}
			Log.w(Commons.DEFAULT_LOG_TAG, msg);
		}
	}

	/**
	 * 打印日志(打包后也需要打印的日志)
	 */
	public final static void eSys(String tag, String msg) {
		if (isLog) {
			if (msg == null || "".equals(msg)) {
				msg = "UtilLogSys";
			}
			Log.e(tag, msg);
		}
	}
}
