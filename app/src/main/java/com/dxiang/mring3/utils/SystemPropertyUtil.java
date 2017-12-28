package com.dxiang.mring3.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.os.Environment;

import com.dxiang.mring3.BuildConfig;
import com.dxiang.mring3.file.SharedPreferenceService;

/**
 * @author DC
 *
 */
public class SystemPropertyUtil {

	/**
	 * 初始化系统的配置文件
	 */
	public static void initSystemProperties(Context c,
			SharedPreferenceService sf) {
		InputStream is = null;
		try {
			is = c.getAssets().open("config.properties");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Properties properties = new Properties();
		try {
			if (is == null) {
				return;
			}
			properties.load(is);
			Commons.IS_DEBUG = BuildConfig.DEBUG;
			Commons.CRASH_FILE = Boolean.parseBoolean(properties.getProperty(
					"crashfile", "false"));
			Commons.IS_RELOAD_APP = Boolean.parseBoolean(properties
					.getProperty("reloadapp", "false"));
			Commons.DATABASE_VERSION = Integer.parseInt(properties.getProperty(
					"dbversion", "1"));
			Commons.DATABASE_NAME = properties.getProperty("dbname");
			Commons.DEVICE_TYPE_CODE = properties.getProperty("devicetypecode");
			Commons.PASSWORD = properties.getProperty("password");
			Commons.IMAGEURL = properties.getProperty("imageurl");
			Commons.KEY = properties.getProperty("key");
			Commons.ADURL = properties.getProperty("adurl");
			Commons.WELCOMEURL = properties.getProperty("welcomeurl");
			Commons.UPDATEURL = properties.getProperty("updateurl");

			Commons.RATIO = properties.getProperty("ratio");
			Commons.MONEY = properties.getProperty("money");
			
			int time = 10000;
			try 
			{
				time = Integer.parseInt(properties.getProperty("outTime")) * 1000;
			} 
			catch (Exception e) 
			{
			}
			Commons.OUTTIME = time;

			int hide = 500;
			try 
			{
				hide = Integer.parseInt(properties.getProperty("hidetime"));
			} 
			catch (Exception e) 
			{
			}
			Commons.HIDETIME = hide;
			
			int show = 500;
			try 
			{
				show = Integer.parseInt(properties.getProperty("showtime"));
			} 
			catch (Exception e) 
			{
			}
			Commons.SHOWTIME = show;
			try {
				Commons.FILE_PATH = Environment.getExternalStorageDirectory()
						+ properties.getProperty("filepath") + "/";
				Commons.LOG_PATH = Environment.getExternalStorageDirectory()
						+ properties.getProperty("logpath") + "/";
				
			} catch (Exception e) {
				Commons.FILE_PATH = c.getCacheDir() + "/";
				Commons.LOG_PATH = c.getCacheDir() + "/";
				if (e != null) {
					UtilLog.e(
							"init",
							"init Variable.FILE_PAH,LOG_PAHT error ： "
									+ e.getMessage());
				}
			}

			Commons.APP_VERSION_CODE = String.valueOf(Utils.getVersionCode(c));
			Commons.APP_VERSION_NAME = Utils.getVersionName(c);
			Commons.SOAP_NAMESPACE = properties.getProperty("namespace");
			Commons.SOAP_URL = properties.getProperty("url");
			Commons.OPERATOR = properties.getProperty("operator");
			Commons.ABOUTUSURL = properties.getProperty("aboutusUrl");
		} catch (IOException e) {
			UtilLog.e(e.getMessage());
		} finally {
			Utils.closeStream(is);
		}
	}
	
	public static void initHomeAds(Context c)
	{
		InputStream is = null;
		try {
			is = c.getAssets().open("adpath/ad.properties");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Properties properties = new Properties();
		try {
			if (is == null) {
				return;
			}
			properties.load(is);
			int count = properties.size();
			for(int i = 0; i < count; i++)
			{
				int temp = i + 1;
				String name = "HomeAd" + temp;
				String result = Commons.ADURL + properties.getProperty(name);
				Commons.mAdBanners.add(result);
			}
	     } 
		 catch(Exception e)
	     {
		  
	     }
	}
}
