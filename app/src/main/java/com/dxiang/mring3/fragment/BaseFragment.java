package com.dxiang.mring3.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.BaseActivity;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.CustomProgressDialog;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.WindowArg;

public class BaseFragment extends Fragment {
	protected Context mContext;
	protected View mContentView;
	protected LayoutInflater mInflater;
	protected FragmentManager fm;
	protected FragmentTransaction tx;
	protected Thread mThread;
	private CustomProgressDialog progressDialog;

	private boolean needFresh = true;

	protected long responseTime = -1l;
	public int w, h;

	protected Handler p_h = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				responseTime = System.currentTimeMillis();
				int what = msg.what;
				int arg1 = msg.arg1;
				switch (what) {
				case FusionCode.NETWORK_SUCESSED:
					reqXmlSucessed(msg);
					break;
				case FusionCode.NETWORK_SUCESSED2:
					reqXmlFail(msg);
					break;
				case FusionCode.NETWORK_ERROR:
				case FusionCode.NETWORK_TIMEOUT:
				case FusionCode.NETWORK_BUSY:
				case FusionCode.PARSER_ERROR:
					reqError(msg);
					break;
				default:
					reqOther(msg);
					break;
				}
			} catch (Exception e) {
				UtilLog.e("UiParent", "Exception: " + e.getMessage());
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		needFresh = true;
		mContext = getActivity();
		w = WindowArg.getInstance(getActivity()).getWindowWidth();
		h = WindowArg.getInstance(getActivity()).getWindowHeigt();
	}

	/**
	 * 启动一个Fragment，移除当前Fragment,并将移除的Fragment放入堆栈
	 * 
	 * @param fragment
	 */
	public void startFragment(Fragment fragment) {
		if (mContext != null && mContext instanceof BaseActivity) {
			((BaseActivity) mContext).startFragment(this, fragment);
		}
	}

	/**
	 * 启动一个Fragment，移除当前Fragment,不将移除的Fragment放入堆栈
	 * 
	 * @param fragment
	 */
	public void startFragment2(Fragment fragment) {
		if (mContext != null && mContext instanceof BaseActivity) {
			((BaseActivity) mContext).startFragment2(this, fragment);
		}
	}

	/**
	 * 启动一个Fragment,不移除当前Fragment
	 * 
	 * @param fragment
	 */
	public void openFragment(Fragment fragment) {
		if (mContext != null && mContext instanceof BaseActivity) {
			((BaseActivity) mContext).startFragment(fragment);
		}
	}

	/**
	 * 退出当前的Fragment
	 */
	public void finish() {
		if (mContext != null && mContext instanceof BaseActivity) {
			((BaseActivity) mContext).popFragment();
		}
	}

	public void popFragment(Fragment fragment) {
		if (fragment == null) {
			return;
		}
		getFragmentManager().popBackStack(fragment.getClass().getSimpleName(),
				FragmentManager.POP_BACK_STACK_INCLUSIVE);

	}

	public void skiptoFragment(int container, Fragment fragment, String tag) {
		if (fragment == null) {
			return;
		}
		FragmentTransaction trans = getFragmentManager().beginTransaction();
		trans.replace(container, fragment, tag);
		trans.addToBackStack(tag);
		trans.commitAllowingStateLoss();

	}

	public void popfromFragment(String tag, boolean b) {
		try {
			getFragmentManager().popBackStack(tag,
					b ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
		} catch (Exception e) {

		}
	}

	protected void reqError(Message msg) {
		if (needFresh) {
			error(msg);
		}
	}

	public void error(Message msg) {
		String result = Utils.getResouceStr(getActivity(), R.string.nowifi);
		showTextToast(getActivity(), result);
	}

	public void showTextToast(Context context, String msg) {
		Utils.showTextToast(context, msg);
	}

	protected void reqXmlSucessed(Message msg) {

	}

	protected void reqXmlFail(Message msg) {

	}

	protected void reqOther(Message msg) {

	}

	/**
	 * 查找本地数据库数据
	 */
	protected void LoadDBData() {

	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}

	/**
	 * 从数据中获取数据完成
	 */
	protected void LoadDBDataComplete() {

	}

	protected void startPbarU() {
		if (progressDialog == null) {
			progressDialog = new CustomProgressDialog(getActivity(),
					R.style.CustomProgressDialog, true);
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(true);
			// progressDialog.setMessage("正在加载中...");
			System.out.println("-------start-------");
		}
		// progressDialog.setAnimation();
		p_h.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (Math.abs(responseTime - Commons.lastTime) < Commons.HIDETIME) {
					return;
				}

				if (!needFresh) {
					return;
				}

				if (progressDialog != null && !progressDialog.isShowing()) {
					progressDialog.show();
				}
			}
		}, Commons.HIDETIME);
		// CustomProgressDialog.getInstance().show();
	}
	protected boolean isShowing() {
		if (progressDialog != null && progressDialog.isShowing()) {
			return true;
		}
		return false;
	}
	protected void stopPbarU() {
		if (progressDialog != null && progressDialog.isShowing()) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
				}
			}, Commons.HIDETIME);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		needFresh = true;
		FlurryAgent.onStartSession(mContext, Utils.API_KEY);
	}

	@Override
	public void onStop() {
		super.onStop();
		needFresh = false;
		FlurryAgent.onEndSession(mContext);
	}
}
