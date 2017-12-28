package com.dxiang.mring3.utils;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;

import com.gem.ysutil.FileUtil;

public class Commons {
	public static ArrayList<String> mAdBanners = new ArrayList<String>();

	// 请求序列号
	public static int SEQCODE = 0;

	public static String ADURL = "";

	public static String WELCOMEURL = "";

	public static String UPDATEURL = "";

	public static int OUTTIME = 10000;

	/**
	 * 该应用程序的版本
	 */
	public static String APP_VERSION_NAME = "0.0.0";

	public static String DEVICE_TYPE_CODE = "008";

	public static String PASSWORD = "";

	public static String KEY = "";

	public static String IMAGEURL = "";

	public static long lastTime = -1l;

	/**
	 * 该应用程序的version_code
	 */
	public static String APP_VERSION_CODE = "0";

	public static final String VERSION = "1.0";

	/**
	 * 兑换
	 */
	public static String RATIO = "";
	public static String MONEY = "";

	/**
	 * 数据库名称（默认）
	 */
	public static String DATABASE_NAME = "myring.db";

	/**
	 * 数据库版本号 （默认）
	 */
	public static int DATABASE_VERSION = 1;

	public static boolean IS_RELOAD_APP = true;

	/**
	 * 是否保存crash日志到文件中
	 */
	public static boolean CRASH_FILE = true;

	/**
	 * log缓存到手机中路径
	 */
	public static String LOG_PATH = "";

	/**
	 * 文件缓存到手机中路径
	 */
	public static String FILE_PATH = "";

	/**
	 * 日志的默认TAG
	 */
	public static String DEFAULT_LOG_TAG = "MyRing";

	/**
	 * 是否打印日志 false : 不打印日志信息，true : 打印日志信息
	 */
	public static boolean IS_DEBUG = true;

	/**
	 * 设备屏幕参数
	 */
	public static float DESITY = 1f;
	public static int WIDTH = 0;
	public static int HEIGHT = 0;

	public static int HIDETIME = 500;

	public static int SHOWTIME = 500;

	/**
	 * 命名空间
	 */
	public static String SOAP_NAMESPACE = "http://impl.pcrbt.zte.com/";
	/**
	 * WSDL文档URL
	 */
//	public static String SOAP_URL = "http://10.47.170.121:8080/colorring/services";
	//现网
    public static String SOAP_URL ="";
    
//    public static String SOAP_URL ="http://210.51.195.25:8080/colorring/services";
	
	public static final String TODAYTIME = "TIMESTAMP";

	/**
	 * 手机运营商
	 */
	public static String OPERATOR = "Entel Mobile";

	public static String ABOUTUSURL = "";

	/*
	 * 图片头像裁剪大小
	 */
	public static int CameraMin = 400;
	public static String headname = "head.png";

	public static String getImageSavedpath() {
		if (!new File(FILE_PATH).exists()) {
			try {
				FileUtil.createFolders(FILE_PATH);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return FILE_PATH;
	}
	public static String getImageSavedpath1() {
		if (!new File(LOG_PATH).exists()) {
			try {
				FileUtil.createFolders(LOG_PATH);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return LOG_PATH;
	}

	public static void delSavepath() {
		deleteSavePath(new File(FILE_PATH));
	}

	public static void deleteSavePath(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteSavePath(childFiles[i]);
			}
			file.delete();
		}
	}
}
