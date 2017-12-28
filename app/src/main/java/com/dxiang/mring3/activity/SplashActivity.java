package com.dxiang.mring3.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.flurry.android.FlurryAgent;
import com.gem.imgcash.ImageDownLoader;
import com.gem.imgcash.ImageDownLoader.onImageLoaderListener;
import com.dxiang.mring3.R;
import com.dxiang.mring3.db.DBOperator;
import com.dxiang.mring3.db.DbHelper;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.Constants;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.WindowArg;

/**
 * humingzhuo 启动画面
 * 
 */
@SuppressLint("HandlerLeak")
@SuppressWarnings("unused")
public class SplashActivity extends Activity {
	public static boolean isFirstIn = false;
	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;

	private SharedPreferenceService preferenceService;

	private ImageView iv_begin;
	public String isSplashImage = "2";

	private DbHelper DbHelper;

	// 延迟3秒?
	private static final long SPLASH_DELAY_MILLIS = 0;

	/**
	 * Handler:跳转到不同界面?
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		try {
			initViews();
			init();
			isSplashImage = preferenceService.get("isSplashImage", "2");
			UtilLog.e(isSplashImage + "--------------------");
			mHandler.postDelayed(r, 3000);
		} catch (Exception e) {
			isSplashImage = "2";
			if (!"1".equals(isSplashImage)) {
				UtilLog.e("---------------------------splash");
				mHandler.postDelayed(r, 3000);
			}
			e.printStackTrace();
		}

	};

	/**
	 * 初始化控件
	 */
	private void initViews() {
		iv_begin = (ImageView) this.findViewById(R.id.iv_begin);
		iv_begin.setBackgroundResource(R.mipmap.splash);
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Utils.API_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	Runnable r = new Runnable() {
		@Override
		public void run() {
			goHome();
		}
	};

	/**
	 * 下载显示图片
	 */
	private void showImage() {
		 String saveDir = Commons.getImageSavedpath()
					+ Commons.WELCOMEURL.replaceAll("[^\\w]", "");
      Bitmap bm=ImageDownLoader.getShareImageDownLoader().showCacheBitmap(saveDir);
      if(bm==null){
    	  iv_begin.setBackgroundResource(R.mipmap.splash);
      }else{
    	  iv_begin.setImageBitmap(bm);
      }
      int w =WindowArg.getInstance(this).getWindowWidth();
      int h =WindowArg.getInstance(this).getWindowHeigt();
      ImageDownLoader.getShareImageDownLoader().downloadImage(saveDir, Commons.WELCOMEURL,w/3,h/4, new onImageLoaderListener() {
				
				@Override
				public void onImageLoader(Bitmap arg0, String arg1) {
					// TODO Auto-generated method stub
					if (isSplashImage.equals("1")) {
						iv_begin.setImageBitmap(arg0);
					} else {
						iv_begin.setBackgroundResource(R.mipmap.splash);
					}
					preferenceService.put("isSplashImage", "1");
				}
			});
	};
//		ImageLoader.getInstance().displayImage(Commons.WELCOMEURL, iv_begin,
//				mImageOptions, new SimpleImageLoadingListener() {
//
//					@Override
//					public void onLoadingStarted(String imageUri, View view) {
//
//						if (!isSplashImage.equals("1")) {
//							iv_begin.setImageBitmap(null);
//							iv_begin.setBackgroundResource(R.drawable.splash);
//						}
//					}
//
//					@Override
//					public void onLoadingFailed(String imageUri, View view,
//							FailReason failReason) {
//						if ("1".equals(isSplashImage)) {
//
//							iv_begin.setImageBitmap(null);
//							iv_begin.setBackgroundResource(R.drawable.splash);
//						}
//					}
//
//					@Override
//					public void onLoadingComplete(String imageUri, View view,
//							Bitmap loadedImage) {
//						if (loadedImage == null) {
//							iv_begin.setImageBitmap(null);
//							iv_begin.setBackgroundResource(R.drawable.splash);
//						} else {
//							UtilLog.e("--------------isSplashImage:"
//									+ isSplashImage);
//							if (isSplashImage.equals("1")) {
//
//								iv_begin.setBackgroundResource(0);
//								iv_begin.setImageBitmap(loadedImage);
//							} else {
//								iv_begin.setBackgroundResource(R.drawable.splash);
//								iv_begin.setImageBitmap(null);
//							}
//							preferenceService.put("isSplashImage", "1");
//						}
//					}
//
//				});
//	}

	/**
	 * 获取IMEI信息
	 * 
	 * @return
	 */
	private String getImei() {
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();// 机器的唯一
		return imei;
	}

	@SuppressWarnings("static-access")
	@Override
	protected void onResume() {
		super.onResume();
		String imei = preferenceService.get("imei", "");
		UtilLog.e("=====imei======", imei + "");
		// 如果非当前手机删除数据库信息
		if (!imei.equals(getImei())) {
			DbHelper = DbHelper.getInstance(this);
			// 删除用户信息
			DBOperator.deleteAll(DbHelper.getWritableDatabase());
			preferenceService.clear();
			// 保存IMEI数据
			saveImei(getImei());
		}
	}

	/**
	 * 保存手机IMEI信息
	 */
	private void saveImei(String imei) {
		// SharedPreferences.Editor mEditor = sp.edit();
		preferenceService.put("imei", imei);
	}

	private void init() throws Exception {
		preferenceService = SharedPreferenceService.getInstance(this);
		if (isFirstIn == false) {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
			isFirstIn = true;
		} else {
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		}

	}

	private void goHome() {
		Intent intent = new Intent(SplashActivity.this, MainGroupActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}

	private void goGuide() {
		showImage();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
