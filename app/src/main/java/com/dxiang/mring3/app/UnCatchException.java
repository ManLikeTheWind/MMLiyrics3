package com.dxiang.mring3.app;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.MainGroupActivity;
import com.dxiang.mring3.file.FileHelper;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.Utils;

public class UnCatchException implements UncaughtExceptionHandler {
	private UncaughtExceptionHandler mDefaultHandler;
	public static final String TAG = "CatchExcep";
//	private BaseApp application;
	private Context mContext;
//	ArrayList<Activity> list;

	public UnCatchException(BaseApp application) {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		mContext = application.getApplicationContext();
//		this.application = application;
//		list = new ArrayList<Activity>();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		String versioninfo = getVersionInfo();
		String mobileInfo = getMobileInfo();
		String errorinfo = getErrorInfo(ex);
		Log.e("CrashHandler", "errorinfo-->" + errorinfo);
		// 是发布版，并且还得运行输入错误日志到文件
		if (Commons.CRASH_FILE) {
			StringBuilder sb = new StringBuilder();
			sb.append(mContext.getString(R.string.version_info, versioninfo))
					.append("\n")
					.append(mContext
							.getString(R.string.mobile_info, mobileInfo))
					.append("\n")
					.append(mContext.getString(R.string.error_info, errorinfo));
			saveCrashInfo2File(sb.toString());
		}
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			if (Commons.IS_RELOAD_APP) {

				Intent intent = new Intent(mContext.getApplicationContext(),
						MainGroupActivity.class);
				PendingIntent restartIntent = PendingIntent.getActivity(
						mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
				AlarmManager mgr = (AlarmManager) mContext
						.getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
						restartIntent);
			}
//			finishActivity();
		}
	}
//	public void removeActivity(Activity a) {
//	list.remove(a);
//}
//
//public void addActivity(Activity a) {
//	list.add(a);
//}
//// 结束Activity
//public void finishActivity() {
//	for (Activity activity : list) {
//		if (null != activity) {
//			activity.finish();
//		}
//	}
//	android.os.Process.killProcess(android.os.Process.myPid());
//}
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				if (Commons.IS_RELOAD_APP) 
				{
					Utils.showTextToast(mContext, mContext.getResources().getString(R.string.app_exception_notification));
				}
				else 
				{
					Utils.showTextToast(mContext, mContext.getResources().getString(R.string.app_exception_notification));
				}
				Looper.loop();
			}
		}.start();
		return true;
	}

	/**
	 * 获取错误的信
	 * 
	 * @param arg1
	 * @return
	 */
	private String getErrorInfo(Throwable arg1) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}

	/**
	 * 获取手机的硬件信
	 * 
	 * @return
	 */
	private String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		try {
			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 获取手机的版本信
	 * 
	 * @return
	 */
	private String getVersionInfo() {
		try {
			PackageManager pm = mContext.getApplicationContext()
					.getPackageManager();
			PackageInfo info = pm.getPackageInfo(mContext
					.getApplicationContext().getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return mContext.getApplicationContext().getString(
					R.string.unknow_version);
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务
	 */
	private String saveCrashInfo2File(String logMsg) {
		DateFormat formatter = new SimpleDateFormat("MM-dd-HH-mm-ss");
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());

			String fileName = "log-" + time + ".log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				if (!FileHelper.fileExist(Commons.LOG_PATH, fileName)) {
					FileHelper.createFile(Commons.LOG_PATH, fileName);
				}

				FileOutputStream fos = new FileOutputStream(Commons.LOG_PATH
						+ fileName);
				fos.write(logMsg.getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}
}
