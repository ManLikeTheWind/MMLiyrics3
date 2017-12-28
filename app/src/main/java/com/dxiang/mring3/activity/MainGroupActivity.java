package com.dxiang.mring3.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.R.anim;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.dxiang.mring3.R;
import com.dxiang.mring3.app.FxService;
//import com.dxiang.mring3.app.FxService;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.file.UpdateCode;
import com.dxiang.mring3.fragment.HomeFragment;
import com.dxiang.mring3.fragment.MeFragment;
import com.dxiang.mring3.fragment.MoreFragment;
import com.dxiang.mring3.fragment.NewListFragment;
import com.dxiang.mring3.request.UserSubSerReq;
import com.dxiang.mring3.response.UserSubSerRsp;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.floatper.AppOpsManagerConstants;
import com.dxiang.mring3.utils.floatper.CheckPermissionUtils;

public class MainGroupActivity extends BaseActivity {
	
	public static final String TAG=MainGroupActivity.class.getSimpleName();
	
	private RadioButton mHomeButton;

	private RadioButton mListButton;

	private RadioButton mMeButton;

	private RadioButton mMoreButton;

	private MoreFragment mMoreFragment;

	private long lastClickPoint;

	private HomeFragment mHomeFragment;

	// 3.9测试
	private NewListFragment newlistFragment;

	private MeFragment mMeFragment;

	private SharedPreferenceService preferenceService;

	// 记录点击的位置
	private int mTabPosition = 1;
	private final int HOME = 1;
	private final int LIST = 2;
	private final int ME = 3;
	private final int MORE = 4;
	// 加密的密码
	private String userPwd;

	private FrameLayout frametlayout;
	public static final int GOTOLOGION = 1001;
	private ImageView liear_title;
	public static List<String> fragments = new ArrayList<String>();

	// 用于存储是哪一个Fragment，然后从App图标启动时初始化对应Fragment
	private String mSelectedFragment;

//	private FxService fxService;
	private boolean initFramgnt = true;
	private boolean homeKey = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.main_group_layout);

		if (arg0 != null) {
			initFramgnt = arg0.getBoolean("initFragment", false); // 被重新创建后恢复缓存的数据
			mTabPosition = arg0.getInt("mTabPosition", 1);
		}
		initViews();

		initListener();

		initDefault();

		forceUpdate();

		startLogin();
		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		
		if (!CheckPermissionUtils.checkPermission(this, AppOpsManagerConstants.OP_SYSTEM_ALERT_WINDOW)) {
			CheckPermissionUtils.applyPermission(this,  AppOpsManagerConstants.OP_SYSTEM_ALERT_WINDOW);
		}
//		int permissionReturn=checkCallingOrSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
//		int permissionReturn2=checkPermission(Manifest.permission.SYSTEM_ALERT_WINDOW, Process.myPid(), Binder.getCallingUid());
//		System.out.println("MainGroupActivity.onCreate() permissionReturn = "+permissionReturn+";permissionReturn2 = "+permissionReturn2
//						+(permissionReturn==PackageManager.PERMISSION_GRANTED?
//							";PackageManager.PERMISSION_GRANTED":
//							";PackageManager.PERMISSION_DENIED"));
		System.out.println("MainGroupActivity.onCreate() "
				+"Process.myUid() = "+Process.myUid()+";Binder.getCallingUid() = "+Binder.getCallingUid()
				+";Process.myPid() = "+Process.myPid());
	}

//	private void startFxService() {
//		boolean adFlag2 = isServiceWork(getApplicationContext(), "com.dxiang.mring3.app.FxService");
//		Log.e("FxService", adFlag2 + "77777777");
//
//		// SharedPreferenceService.getInstance(getApplicationContext())
//		// .get("ad", false);// 获得窗体存在状态，则关闭，在重新打开进行刷新
//		// 此处是创建，false：关闭状态
//
//		if (!adFlag2) {// 窗体开启，不存在就创建，跳转到服务；
//			Intent intent = new Intent(getApplicationContext(), FxService.class);
//			startService(intent);
//
//			SharedPreferenceService.getInstance(getApplicationContext()).put("adOpen", true);
//
//		}
//	}

	/**
	 * 判断某个服务是否正在运行的方法
	 * 
	 * @param mContext
	 * @param serviceName
	 *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
	 * @return true代表正在运行，false代表服务没有正在运行
	 */
	public boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> myList = myAM.getRunningServices(1000);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			Log.e("service name", mName + "==" + myList.get(i).service.getClassName());

			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}

	private void initDefault() {
		preferenceService = SharedPreferenceService.getInstance(this);
		switchLanguage(preferenceService.get("language", ""));
		if (Utils.CheckTextNull(preferenceService.get("language", ""))) {
			mHomeButton.setBackgroundResource(R.drawable.home_spain_selector);
			mListButton.setBackgroundResource(R.drawable.list_spain_selector);
			mMeButton.setBackgroundResource(R.drawable.me_spain_selector);
			mMoreButton.setBackgroundResource(R.drawable.more_spain_selector);
		}
		if (initFramgnt) {
			skipToHome();
		} else {

			switch (mTabPosition) {
			case HOME:
				mHomeButton.setChecked(true);
				break;
			case LIST:
				mListButton.setChecked(true);
				break;
			case ME:
				mMeButton.setChecked(true);
				break;
			case MORE:
				mMoreButton.setChecked(true);
				break;
			default:
				break;
			}

		}
	}

	// @Override
	// protected void onNewIntent(Intent intent) {
	// // TODO Auto-generated method stub
	// super.onNewIntent(intent);
	//
	// skipToLogin();
	// mMeButton.setChecked(true);
	//
	// }

	private void initListener() {
		mHomeButton.setOnClickListener(mViewListener);
		mListButton.setOnClickListener(mViewListener);
		mMeButton.setOnClickListener(mViewListener);
		mMoreButton.setOnClickListener(mViewListener);
		liear_title.setOnClickListener(mViewListener);

	}

	// private void initFragment() {
	// mSelectedFragment = preferenceService.get("mSelectedFragment", "HOME");
	//
	// if (mSelectedFragment.equals("HOME")) {
	// skipToHome();
	// Log.e("MainGroupActivity==onResume()", "***HOME***");
	// } else if (mSelectedFragment.equals("LIST")) {
	// skipToList(false);
	// Log.e("MainGroupActivity==onResume()", "***LIST***");
	// }
	// // else if (mSelectedFragment.equals("ME")) {
	// // skipToMe();
	// // Log.e("MainGroupActivity==onResume()", "***ME***");
	// // } else if (mSelectedFragment.equals("MORE")) {
	// // skipToMore();
	// // Log.e("MainGroupActivity==onResume()", "***MORE***");
	// // }
	//
	// }

	public void skipToHome() {

		preferenceService.put("mSelectedFragment", "HOME");

		FragmentManager manager = getSupportFragmentManager();
		mHomeFragment = (HomeFragment) manager.findFragmentByTag(HomeFragment.TAG);
		// if (mHomeFragment == null) {
		mHomeFragment = new HomeFragment();
		// }
		// if (mListFragment != null) {
		// mListFragment.setNeedBack(false);
		// }

		FragmentManager manager1 = getSupportFragmentManager();
		FragmentTransaction tracstion = manager1.beginTransaction();
		tracstion.replace(R.id.container, mHomeFragment, HomeFragment.TAG);
		tracstion.commitAllowingStateLoss();

	}

	private void skipToList(boolean needBack) {
		// if (mListFragment == null) {
		// mListFragment = new ListFragment();
		// }

		// frametlayout.removeAllViews();
		preferenceService.put("mSelectedFragment", "LIST");
		newlistFragment = new NewListFragment();

		// mListFragment.setNeedBack(needBack);
		// FragmentManager manager = getSupportFragmentManager();
		// FragmentTransaction tracstion = manager.beginTransaction();
		// tracstion.replace(R.id.container, mListFragment, "ListFragment");
		//
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction tracstion = manager.beginTransaction();
		tracstion.replace(R.id.container, newlistFragment, "NEWListFragment");
		tracstion.commitAllowingStateLoss();

	}

	private void skipToMore() {
		preferenceService.put("mSelectedFragment", "MORE");

		mMoreFragment = new MoreFragment();

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction tracstion = manager.beginTransaction();
		tracstion.replace(R.id.container, mMoreFragment, "MoreFragment");
		tracstion.commitAllowingStateLoss();
	}

	private void skipToMe() {
		preferenceService.put("mSelectedFragment", "ME");

		mMeFragment = new MeFragment();

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction tracstion = manager.beginTransaction();
		tracstion.replace(R.id.container, mMeFragment, "MeFragment");
		tracstion.commitAllowingStateLoss();
	}

	private void skipToLogin() {
		Intent intent = new Intent(MainGroupActivity.this, LoginActivity.class);
		startActivity(intent);
		// if (mLoginFragment == null) {
		// mLoginFragment = new LoginFragment();
		// }
		//
		// FragmentManager manager = getSupportFragmentManager();
		// FragmentTransaction tracstion = manager.beginTransaction();
		// tracstion.replace(R.id.container, mLoginFragment, "LoginFragment");
		// tracstion.commit();
	}

	private OnClickListener mViewListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			FragmentManager manager = getSupportFragmentManager();
			if (manager.getBackStackEntryCount() > 0) {
				manager.popBackStackImmediate();
			}

			// if (pagerCarrosel.getVisibility() == View.VISIBLE) {
			// GetimageinforReq req = new GetimageinforReq(handler);
			// req.sendGetimageinforReqRequest("2");
			//
			// }
			switch (arg0.getId()) {
			case R.id.main_tab_home:
				mTabPosition = HOME;
				skipToHome();
				break;
			case R.id.main_tab_list:
				mTabPosition = LIST;
				skipToList(false);
				break;
			case R.id.main_tab_me:
				if (!UserVariable.LOGINED) {
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivityForResult(intent, GOTOLOGION);
				} else {
					mTabPosition = ME;
					skipToMe();
				}
				break;
			case R.id.main_tab_more:
				mTabPosition = MORE;
				skipToMore();
				break;
			// case R.id.iv_close_window:
			// mHandlerPosterAutoScroll.removeCallbacks(autoFling);
			// header.setVisibility(View.GONE);
			// break;
			case R.id.liear_title:
				if (mTabPosition != HOME) {
					mHomeButton.setChecked(true);
					mTabPosition = HOME;
					skipToHome();
				}
				break;
			default:
				break;
			}
		}
	};

	public void skipToListFragment(boolean needBack) {
		skipToList(true);
		mListButton.setChecked(true);
	}

	private void initViews() {
		mHomeButton = (RadioButton) findViewById(R.id.main_tab_home);
		mListButton = (RadioButton) findViewById(R.id.main_tab_list);
		mMeButton = (RadioButton) findViewById(R.id.main_tab_me);
		mMoreButton = (RadioButton) findViewById(R.id.main_tab_more);
		frametlayout = (FrameLayout) findViewById(R.id.container);
		liear_title = (ImageView) findViewById(R.id.liear_title);

	}

	// private void setUrlToView(final ImageView view, String url,
	// DisplayImageOptions options, boolean needAdd, final int id) {
	// if (needAdd) {
	// url = Commons.IMAGEURL + "/" + url;
	// }
	// ImageLoader.getInstance().displayImage(url, view, options,
	// new ImageLoadingListener() {
	//
	// @Override
	// public void onLoadingStarted(String arg0, View arg1) {
	// }
	//
	// @Override
	// public void onLoadingFailed(String arg0, View arg1,
	// FailReason arg2) {
	// view.setBackgroundResource(id);
	// view.setImageBitmap(null);
	// }
	//
	// @Override
	// public void onLoadingComplete(String arg0, View arg1,
	// Bitmap arg2) {
	// if (arg2 == null) {
	// arg1.setBackgroundResource(id);
	// } else {
	// arg1.setBackgroundResource(0);
	// view.setImageBitmap(arg2);
	// }
	// }
	//
	// @Override
	// public void onLoadingCancelled(String arg0, View arg1) {
	// }
	// });
	// }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (getBackStackEntryCount() > 0) {
				return super.onKeyDown(keyCode, event);
			}

			// if (mListFragment != null && mListFragment.isNeedBack()) {
			// skipToHome();
			// return true;
			// }
			if (fragments != null && fragments.contains("1")) {
				mTabPosition = HOME;
				skipToHome();
				return false;
			}
			if (fragments != null && fragments.contains("2")) {
				mTabPosition = LIST;
				skipToList(false);
				return false;
			}
			long currTimePoint = System.currentTimeMillis();

			if (currTimePoint - lastClickPoint < 2500) {
				UserVariable.LOGINED = false;
				SplashActivity.isFirstIn = false;
				finish();
				return true;
			} else {
				lastClickPoint = currTimePoint;
				Utils.showToastShort(this, R.string.back_app);
				return true;
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	/**
	 * 发起登录
	 */
	private void startLogin() {
		if (UserVariable.LOGINED) {
			return;
		} else {
			if (preferenceService.get("isAutoLogin", false)) {
				// 发起登录请求，成功后
				String userName = preferenceService.get("userName", "");
				userPwd = preferenceService.get("userPwd", "");
				new UserSubSerReq(handler).sendUserSubSerRequest(userName, userPwd);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GOTOLOGION) {
			if (UserVariable.LOGINED) {
				mTabPosition = ME;
			}
		} else {
			if (0 == resultCode) {
				switch (mTabPosition) {
				case HOME:
					mHomeButton.setChecked(true);
					break;
				case LIST:
					mListButton.setChecked(true);
					break;
				case ME:
					mMeButton.setChecked(true);
					break;
				case MORE:
					mMoreButton.setChecked(true);
					break;
				default:
					break;
				}
			} else if (1 == resultCode) {
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);

		// 从启Activity
		if (intent.getExtras() != null) {
			if (intent.getExtras().getBoolean("setLanguage", false)) {
				MainGroupActivity.this.finish();
				startActivity(new Intent(this, MainGroupActivity.class));
			}
			if (intent.getExtras().getBoolean("LoginActivityRetunMain", true)) {
				if (!UserVariable.LOGINED) {
					if (!UserVariable.LOGINED && mMeButton.isChecked()) {
						mHomeButton.setChecked(true);
						getSupportFragmentManager().beginTransaction().remove(mMeFragment);
						mMeFragment = null;
						skipToHome();
					}
				} else {
					mMeButton.setChecked(true);
					if (null == mMeFragment) {
						skipToMe();
					} else {
						mMeFragment.refreshUserData();
					}
				}
			}
		} else {
			mHomeButton.setChecked(true);
			mTabPosition = HOME;
			skipToHome();
		}
	}

	/**
	 * 设置应用语言变换
	 * 
	 * @param language
	 */
	public void switchLanguage(String language) {
		// 设置应用语言类型
		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		if (language.equals("es")) {
			config.locale = new Locale("es", "ES");
		} else {
			config.locale = Locale.getDefault();
		}
		resources.updateConfiguration(config, dm);

		// 保存设置语言的类型
		preferenceService.put("language", language);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("MainGroupActivity==()", "重新调用绑定Fragment,也就相当于刷新Fragment");
		// getSupportFragmentManager().beginTransaction().replace(R.id.container,
		// mHomeFragment, HomeFragment.TAG);
		// startFxService();
		// initFragment();

		Log.e("Main==============", "service**************");

//		bindService();
//		if (fxService != null)
//			fxService.showWindow();
		homeKey = false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// boolean adFlag2 = isServiceWork(getApplicationContext(),
		// "com.dxiang.mring3.app.FxService");
		// if (adFlag2)
		// fxService.removeView();
		// Log.e("FxService2", adFlag2+"1010101");
//		if (fxService != null && homeKey) {
//			fxService.hideWindow();
//		}
//		unBind();

	}

	// 监听home键
	private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
		String SYSTEM_REASON = "reason";
		String SYSTEM_HOME_KEY = "homekey";
		String SYSTEM_HOME_KEY_LONG = "recentapps";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
					// 表示按了home键,程序到了后台
					homeKey = true;
					// Toast.makeText(getApplicationContext(), "home",
					// 1).show();
				} else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
					// 表示长按home键,显示最近使用的程序列表
				}
			}
		}
	};

	@Override
	public void onStop() {// 在这里它们就把Service关掉了
		super.onStop();
		// boolean adFlag2 =
		// isServiceWork(getApplicationContext(),"com.dxiang.mring3.app.FxService");
		// Log.e("FxService2", adFlag2+"999999");
		// unBind();

	}

	// 强制升级
	private void forceUpdate() {
		// if(SplashActivity.isFirstIns==false){

		// ifForcedToUpgrade();
		UpdateCode c = new UpdateCode();
		UpdateCode.ISABOUTUS = true;
		c.onCreateDialogPt(this);
		// }
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("initFragment", false);// 被摧毁前缓存一些数据
		outState.putInt("mTabPosition", mTabPosition);
		super.onSaveInstanceState(outState);
	}

	// @Override
	// protected void onRestoreInstanceState(Bundle savedInstanceState) {
	// super.onRestoreInstanceState(savedInstanceState);
	//
	//
	// }

	@Override
	public void onStart() {
		super.onStart();
		//
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mHomeKeyEventReceiver!=null) {
			unregisterReceiver(mHomeKeyEventReceiver);
		}
//		unBind();

	}

	@Override
	protected void reqXmlSucessed(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_USERSUBSEREVT:
			UserSubSerRsp userSubSerRsp = (UserSubSerRsp) msg.obj;
			UserVariable.USERPWD = userPwd;
			UserVariable.CALLNUMBER = preferenceService.get("userName", "");
			UserVariable.STATUSCALLED = userSubSerRsp.getStatusCalled();
			UserVariable.STATUSCALLING = userSubSerRsp.getStatusCalling();
			UserVariable.LOGINED = true;
			break;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void reqError(Message msg) {
		super.reqError(msg);
	}

		
		
		

}
