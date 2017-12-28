package com.dxiang.mring3.activity;

import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dxiang.mring3.R;
import com.dxiang.mring3.app.BaseApp;
import com.dxiang.mring3.app.UnCatchException;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.CustomProgressDialog;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.KeyboardUtil;
import com.dxiang.mring3.utils.SystemPropertyUtil;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.WindowArg;

public class BaseActivity extends FragmentActivity {
	protected Context mContext;
	protected LayoutInflater mInflater;
	protected FragmentManager fm;
	protected FragmentTransaction tx;
	
	
	
	private CustomProgressDialog progressDialog;

	protected long responseTime = -1l;

	private boolean needFresh = true;
	public int w, h;

	protected Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				int what = msg.what;
				Log.e("message", "what==" + what);
				responseTime = System.currentTimeMillis();
				switch (what) {
				case FusionCode.NETWORK_SUCESSED:
					reqXmlSucessed(msg);
					break;
				// 内容格式错误
				case FusionCode.NETWORK_SUCESSED2:
					reqXmlFail(msg);
					break;
				case FusionCode.NETWORK_ERROR:// 网络失败
				case FusionCode.NETWORK_TIMEOUT:
				case FusionCode.NETWORK_BUSY:
				case FusionCode.PARSER_ERROR:
					reqError(msg);
					break;
				default:// 处理其它
					reqOther(msg);
					break;
				}
			} catch (Exception e) {
				UtilLog.e("UiParent", "Exception: " + e.getMessage());
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		mContext = this;
		needFresh = true;
		mInflater = LayoutInflater.from(mContext);
		fm = getSupportFragmentManager();
		if (Commons.WIDTH == 0) {
			Utils.initScreenProperties(this);
		}
		if (TextUtils.isEmpty(Commons.FILE_PATH)) {
			SystemPropertyUtil.initSystemProperties(this, null);
		}
		w = WindowArg.getInstance(this).getWindowWidth();
		h = WindowArg.getInstance(this).getWindowHeigt();
		// UnCatchException catchException=new UnCatchException((BaseApp)
		// getApplicationContext());
		// catchException.addActivity(this);
	}

	/**
	 * 启动一个Fragment，移除当前Fragment,并将移除的Fragment放入堆栈
	 * 
	 * @param cur
	 * @param tar
	 */
	public void startFragment(Fragment cur, Fragment tar) {
		if (cur != null && tar != null) {
			FragmentTransaction trans = fm.beginTransaction();
			trans.add(R.id.container, tar, tar.getClass().getSimpleName());
			trans.remove(cur);
			trans.addToBackStack(null);
			trans.commitAllowingStateLoss();
		}
	}

	/**
	 * 启动一个Fragment，移除当前Fragment,不将移除的Fragment放入堆栈
	 * 
	 * @param cur
	 * @param tar
	 */
	public void startFragment2(Fragment cur, Fragment tar) {
		if (cur != null && tar != null) {
			FragmentTransaction trans = fm.beginTransaction();
			trans.add(R.id.container, tar, tar.getClass().getSimpleName());
			trans.remove(cur);
			// trans.addToBackStack(null);
			trans.commitAllowingStateLoss();
		}
	}

	/**
	 * 启动一个Fragment,不移除当前Fragment
	 * 
	 * @param tar
	 */
	public void startFragment(Fragment tar) {
		if (tar != null) {
			FragmentTransaction trans = fm.beginTransaction();
			trans.add(R.id.container, tar, tar.getClass().getSimpleName());
			// trans.remove(cur);
			trans.addToBackStack(null);
			trans.commitAllowingStateLoss();
		}
	}

	// public void startFragment(Fragment tar) {
	// if (tar != null) {
	// FragmentTransaction trans = fm.beginTransaction();
	// trans.add(R.id.sub_container, tar, tar.getClass().getSimpleName());
	// Fragment cur = fm.findFragmentById(R.id.sub_container);
	// if (cur != null) {
	// trans.remove(cur);
	// trans.addToBackStack(null);
	// }
	// trans.commitAllowingStateLoss();
	// }
	// }

	public void popFragment() {
		if (getBackStackEntryCount() > 0) {
			try {
				onStart();
				fm.popBackStack();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int getBackStackEntryCount() {
		if (fm != null) {
			return fm.getBackStackEntryCount();
		}
		return 0;
	}

	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

			
			// 处理Fragment弹出栈
			if (getBackStackEntryCount() > 0) {
				popFragment();
				// onBackPressed();
				return true;

			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 网络异常处理方法
	 */
	public void error(Message msg) {
		String result = Utils.getResouceStr(this, R.string.nowifi);
		showTextToast(getApplicationContext(), result);
	}

	public void showTextToast(Context context, String msg) {
		Utils.showTextToast(context, msg);
	}

	/**
	 * 请求网络失败处理
	 */
	protected void reqError(Message msg) {
		error(msg);
	}

	/**
	 * 请求xml成功后的处理
	 */
	protected void reqXmlSucessed(Message msg) {

	}

	/**
	 * 请求xml成功处理
	 * 
	 */
	protected void reqXmlFail(Message msg) {
	}

	@Override
	public void finish() {
		super.finish();
	}

	/**
	 * 处理其它请求返回
	 */
	protected void reqOther(Message msg) {

	}

	protected void startPbarU() {
//		if (progressDialog == null) {
//			progressDialog = new CustomProgressDialog(this, R.style.CustomProgressDialog, true);
//			progressDialog.setCanceledOnTouchOutside(true);
//			// progressDialog.setMessage("正在加载中...");
//		}
//		handler.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				if (Math.abs(responseTime - Commons.lastTime) < Commons.HIDETIME) {
//					return;
//				}
//				if (!needFresh) {
//					return;
//				}
//
//				if (progressDialog != null && !progressDialog.isShowing()) {
//					progressDialog.show();
//				}
//			}
//		}, Commons.HIDETIME);
		// CustomProgressDialog.getInstance().show();
	}

	protected void setOnDismissListener(OnDismissListener listener) {
		progressDialog.setOnDismissListener(listener);
	}

	protected void stopPbarU() {
		if (progressDialog != null && progressDialog.isShowing()) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
				}
			}, Commons.HIDETIME);
		}
	}

	protected boolean isShowing() {
		if (progressDialog != null && progressDialog.isShowing()) {
			return true;
		}
		return false;
	}

	@Override
	public void onStart() {
		super.onStart();
		needFresh = true;
		Utils.onStartSession(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		needFresh = false;
		Utils.onEndSession(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		needFresh = true;
	}

}
