package com.dxiang.mring3.utils;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.dxiang.mring3.R;
import com.dxiang.mring3.bean.ImageInfo;
import com.dxiang.mring3.bean.ToneInfo;

public class Utils {

	private static long lastClickTime;
	public static String URL = "";
	private static Toast toast = null;
	public final static String API_KEY = "G8QM99V88J6WSD2GVXQP";
	public static double lowest = 0.0;
	public static double latest = 0.0;
	public static String storeurl = "";
	public static String httpurl = "";
	public static String updateinfoub = "";
	public static String updateinfofp = "";
	public static String updateinfofp1 = "";

	/**
	 * json数据解析
	 * 
	 * @param obj
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static String reSoapObjectStr(SoapObject obj, String name) {
		try {
			return obj.getProperty(name).toString();
		} catch (Exception ex) {
			return "";
		}

	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 数据显示的时间
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date stringToDate(String dateString) {
		/** 从第一个字符开始解析 */
		ParsePosition position = new ParsePosition(0);
		/**
		 * 而这个Date的格式为"yyyy.MM.dd - yyyy.MM.dd" 因为设置他的格式就是为“yyyy.MM.dd -
		 * yyyy.MM.dd” 如果需要更改date就需要改他的格式了
		 */
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy.MM.dd - yyyy.MM.dd");
		/** 对参数dtToDate（String类型）从第一个字符开始解析（由pos），转换成java.util.Date类型 */
		Date dateValue = simpleDateFormat.parse(dateString, position);
		return dateValue;
	}

	/**
	 * 判断网络状态
	 * 
	 * @return
	 */
	public static boolean isNetworkAvailable(Activity activity) {
		// 获取手机的网络信息
		ConnectivityManager connectivityManager = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			// 当前网络不可用，在此可引导用户去设置网络
			return false;
		} else {
			String type = networkinfo.getTypeName();
			if (networkinfo.getTypeName() != null
					&& networkinfo.getExtraInfo() != null) {
				type = networkinfo.getExtraInfo();
				if (networkinfo.getTypeName().toString().toUpperCase()
						.equals("MOBILE")
						&& networkinfo.getExtraInfo().equals("cmwap")) {
					type = "mobile";
				}
			}
			return true;
		}
	}

	public static final void onStartSession(Context context) {

		try {
			// FlurryAgent.setUseHttps(false);
			FlurryAgent.setCaptureUncaughtExceptions(true);
			// 这个是KEY
			FlurryAgent.onStartSession(context, API_KEY);
			FlurryAgent.setLogEvents(true);
		} catch (Throwable t) {
		}
	}

	public static final void onEndSession(Context context) {
		try {
			FlurryAgent.onEndSession(context);
		} catch (Throwable t) {
		}
	}

	/**
	 * 根据ID获取字符串
	 * 
	 * @param id
	 * @return
	 */
	public final static String getResouceStr(Context context, int id) {
		if (context != null) {

			return context.getResources().getString(id);
		} else {
			return "";
		}

	}

	/**
	 * 关闭流
	 * 
	 * @param stream
	 */
	public static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
				stream = null;
			} catch (IOException e) {
				UtilLog.e(e.getMessage());
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						stream = null;
						UtilLog.e(e.getMessage());
					}
					stream = null;
				}
			}
		}
	}

	// 取得版本号
	public static String getVersionName(Context context) {
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return manager.versionName;
		} catch (NameNotFoundException e) {
			return "Unknown";
		}
	}

	// 取得版本号
	public static int getVersionCode(Context context) {
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return manager.versionCode;
		} catch (NameNotFoundException e) {
			return -1;
		}
	}

	public static void initScreenProperties(Context c) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);
		Commons.DESITY = dm.density;
		Commons.WIDTH = dm.widthPixels;
		Commons.HEIGHT = dm.heightPixels;
	}

	public final static void hideInput(Context context) {
		try {
			/** hideSoftInputFromWindow的无返回值版：从窗口上下文中确定当前接收输入的窗口，隐藏其输入法窗口 */
			((InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(((Activity) context)
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {

		}
	}

	public final static void showInput(Context context, EditText edit) {
		try {
			/**
			 * 显示输入法的软键盘区域，这样用户可以到看到输入法窗口并能与其交互。 只能由当前激活输入法调用，因需令牌(token)验证。
			 */
			((InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.showSoftInput(edit, 0);
		} catch (Exception e) {

		}
	}

	public static final boolean isEmptyString(String data) {
		if (data == null || "".equals(data)) {
			return true;
		}
		return false;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean checkMobile(String mobiles) {
		boolean flag = false;
		try {
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[0-9])|(14[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证密码长度
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean IsPasswLength(String str_Length) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^\\w{6,20}$");
			Matcher m = p.matcher(str_Length);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 判断email格式是否正确
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	/**
	 * 判断是否全是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 判断空值null或""
	 * 
	 * @param value
	 * @return
	 */
	public static boolean CheckTextNull(String value) {
		if (null != value && !("".equals(value))) {
			return true;
		} else {

			return false;

		}
	}

	/**
	 * 限制按钮的点击时间
	 * 
	 * @return
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long delt = Math.abs(time - lastClickTime);
		if (delt < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 限制按钮的点击时间
	 * 
	 * @return
	 */
	public static boolean isFastDoubleClick2() {
		long time = System.currentTimeMillis();
		long delt = Math.abs(time - lastClickTime);
		if (delt < 1000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 限制按钮的点击时间2秒
	 * 
	 * @return
	 */
	public static boolean isFastDoubleClickToo() {
		long time = System.currentTimeMillis();
		long delt = Math.abs(time - lastClickTime);
		if (delt < 2000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	// Toast 提示框
	public static void showTextToast(Context context, String msg) {
		showToast(context, msg, 1);
	}

	private static void showToast(Context context, String msg, int time) {
		if (null != context) {
			/** 自定义界面 */
			final LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.toast_layout, null);
			TextView iv = (TextView) v.findViewById(R.id.tv_toast);
			if (!("".equals(msg))) {
				if (toast == null) {
					toast = new Toast(context);
					iv.setText(msg);
					toast.setDuration(time);
				} else {
					iv.setText(msg);
				}
				toast.setView(v);
				toast.show();
			}
		}
	}

	public static void showTextToast(Context context, int msg) {
		if (msg == 0) {
			return;
		}
		String data = context.getResources().getString(msg);
		showTextToast(context, data);
	}

	public static void showToastShort(Context context, int msg) {
		if (msg == 0) {
			return;
		}
		String data = context.getResources().getString(msg);
		showToast(context, data, 0);
	}

	public static final boolean isNeedToLoadDatas(long time) {
		if (time == 0l) {
			return true;
		}
		long day = System.currentTimeMillis();
		Date d1 = new Date(time);
		Date d2 = new Date(day);

		if (d2.getDay() != d1.getDay()) {
			return true;
		}
		return false;
	}

	public static final ToneInfo getToneInfos(SoapObject obj) {
		ToneInfo info = new ToneInfo();
		String singerName = Utils.reSoapObjectStr(obj, "singerName");
		String toneName = Utils.reSoapObjectStr(obj, "toneName");
		if (!singerName.equalsIgnoreCase("string{}")
				&& !toneName.equalsIgnoreCase("string{}")) {
			info.setCpID(Utils.reSoapObjectStr(obj, "cpID"));
			info.setGiftTimes(Utils.reSoapObjectStr(obj, "giftTimes"));
			info.setInfo(Utils.reSoapObjectStr(obj, "info"));
			info.setOffset(Utils.reSoapObjectStr(obj, "offset"));
			info.setPara1(Utils.reSoapObjectStr(obj, "para1"));
			info.setPara2(Utils.reSoapObjectStr(obj, "para2"));
			info.setPrice(Utils.reSoapObjectStr(obj, "price"));
			info.setSingerName(Utils.reSoapObjectStr(obj, "singerName"));
			info.setStatus(Utils.reSoapObjectStr(obj, "status"));
			info.setToneClassID(Utils.reSoapObjectStr(obj, "toneClassID"));
			info.setToneID(Utils.reSoapObjectStr(obj, "toneID"));
			info.setToneIndex(Utils.reSoapObjectStr(obj, "toneIndex"));
			info.setToneLong(Utils.reSoapObjectStr(obj, "toneLong"));
			info.setToneName(Utils.reSoapObjectStr(obj, "toneName"));
			info.setTonePreListenAddress(Utils.reSoapObjectStr(obj,
					"tonePreListenAddress"));
			info.setToneNameLetter(Utils.reSoapObjectStr(obj, "toneNameLetter"));

			Log.e("打印歌曲的信息", Utils.reSoapObjectStr(obj, "price"));
			Log.e("打印歌曲的信息", Utils.reSoapObjectStr(obj, "singerName"));
			Log.e("打印歌曲的信息", Utils.reSoapObjectStr(obj, "toneName"));
			Log.e("打印歌曲的信息", Utils.reSoapObjectStr(obj, "toneNameLetter"));

			info.setToneSize(Utils.reSoapObjectStr(obj, "toneSize"));
			info.setToneType(Utils.reSoapObjectStr(obj, "toneType"));
			info.setToneValidDay(Utils.reSoapObjectStr(obj, "toneValidDay"));
			info.setUpdateTime(Utils.reSoapObjectStr(obj, "updateTime"));
			info.setUseTimes(Utils.reSoapObjectStr(obj, "useTimes"));
		}
		return info;
	}

	public static final ImageInfo getImageInfo(SoapObject obj) {
		ImageInfo info = new ImageInfo();
		info.setNo(Utils.reSoapObjectStr(obj, "no"));
		info.setImageURL(Utils.reSoapObjectStr(obj, "imageURL"));
		info.setLinkURL(Utils.reSoapObjectStr(obj, "linkURL"));

		return info;

	}

	/**
	 * 计算兑换比例
	 * 
	 * @param str
	 * @return
	 */
	public static String doCount(String str) {
		float f;
		if (!Utils.CheckTextNull(str)) {
			float x = Float.valueOf(str.trim()).floatValue();
			java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
			String str1 = String.valueOf(df.format(x)).replaceAll("\\.", ",");
			return Commons.MONEY + " " + str1;
		}

		try {
			f = Float.parseFloat(str)
					/ Integer.parseInt(Commons.RATIO.trim().split(":")[1]);
		} catch (Exception e) {
			double x = Float.valueOf(str.trim()).floatValue();
			java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
			String str1 = String.valueOf(df.format(x)).replaceAll("\\.", ",");
			return Commons.MONEY + " " + str1;
		}
		double x = f;
		java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
		String str1 = String.valueOf(df.format(x)).replaceAll("\\.", ",");
		return Commons.MONEY + " " + str1;
	}

	/**
	 * 设置Hint文字大小
	 * 
	 * @param edit
	 */
	public static void hintSize(EditText edit, int size) {
		// 获取控件EditText中的hint文本
		String hintTextSize = edit.getHint().toString();
		// 新建一个可以添加属性的文本对象
		SpannableString sm = new SpannableString(hintTextSize);
		// 新建一个属性对象,设置文字的大小。12表示字体大小
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(size, true);
		// 附加属性到文本
		sm.setSpan(ass, 0, sm.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 最后一步转换，把内容放进控件里
		edit.setHint(new SpannableString(sm));
	}
}
