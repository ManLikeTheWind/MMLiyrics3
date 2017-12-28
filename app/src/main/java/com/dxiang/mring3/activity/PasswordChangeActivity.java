package com.dxiang.mring3.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

import com.dxiang.mring3.R;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.EditPwd;
import com.dxiang.mring3.response.EditPwdRsp;
import com.dxiang.mring3.utils.CustomProgressDialog;
import com.dxiang.mring3.utils.DESUtil;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.KeyboardUtil;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

/**
 * 注销彩铃界面
 * 
 * @author humingzhuo
 */

@SuppressLint("ValidFragment")
public class PasswordChangeActivity extends BaseActivity {

	// 控件

	private RelativeLayout rl_title_layout;
	private EditText et_old_password, et_new_password, et_password_again;
	// 确定
	private Button btn_determine;
	// 返回标题
	private TextView mTitle_tv;
	// 返回图片
	private ImageView mTitle_iv;
	// 变量
	public static int id = 0;

	// 加密后的密码
	private String strPwd = "";

	private EditPwd ep;
	private ImageView liear_title;
	public static final int SHOWDIALOG = 1001;
	public static final int DISSMISSDIALOG = 1002;
	private LinearLayout ll;
	/**
	 * 键盘相关类
	 */
	private KeyboardView keyboard;
	private KeyboardUtil boardUtil;

	Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOWDIALOG:
				if (progressDialog != null && !progressDialog.isShowing())
					progressDialog.show();
				break;
			case DISSMISSDIALOG:
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				break;
			default:
				return;
			}
		}
	};
	private CustomProgressDialog progressDialog;

	protected void startPbarU() {
		if (progressDialog == null) {
			progressDialog = new CustomProgressDialog(this, R.style.CustomProgressDialog, true);
			progressDialog.setCanceledOnTouchOutside(true);
			// progressDialog.setMessage("正在加载中...");
		}
	}

	private void sendMessage(int paramInt) {
		Message localMessage = this.mhandler.obtainMessage();
		localMessage.what = paramInt;
		this.mhandler.sendMessage(localMessage);
	}

	private SharedPreferenceService preferenceService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_change);
		initWidget();
		setClickListener();
		init();
	}

	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 判断密码
	 */
	@SuppressLint("ShowToast")
	private boolean checkPassword() {
		if (!Utils.CheckTextNull(et_old_password.getText().toString())) {
			showTextToast(mContext, Utils.getResouceStr(mContext, R.string.the_old_password_can_not_be_empty));
			return false;
		}
		if (!Utils.CheckTextNull(et_new_password.getText().toString())) {
			showTextToast(mContext, Utils.getResouceStr(mContext, R.string.the_new_password_can_not_be_empty));
			return false;
		}
		if (!Utils.CheckTextNull(et_password_again.getText().toString())) {
			showTextToast(mContext, Utils.getResouceStr(mContext, R.string.the_confirmed_password_can_not_be_empty));
			return false;
		}
		if (!Utils.IsPasswLength(et_new_password.getText().toString())) {
			showTextToast(mContext,
					Utils.getResouceStr(mContext, R.string.the_new_password_should_be_no_less_than_6_numbers));
			return false;
		}
		if (!Utils.IsPasswLength(et_old_password.getText().toString())) {
			showTextToast(mContext,
					Utils.getResouceStr(mContext, R.string.the_old_password_should_be_no_less_than_6_numbers));
			return false;
		}
		if (!Utils.IsPasswLength(et_password_again.getText().toString())) {
			showTextToast(mContext,
					Utils.getResouceStr(mContext, R.string.the_confirmed_password_should_be_no_less_than_6_numbers));
			return false;
		}
		// 新密码和确认密码必须一致
		if (!et_new_password.getText().toString().equals(et_password_again.getText().toString())) {
			// Toast.makeText(mContext,
			// et_new_password.getText().toString()+" :
			// "+et_password_again.getText().toString()+"",
			// 1000).show();
			showTextToast(mContext, Utils.getResouceStr(mContext,
					R.string.the_confirmend_password_and_the_new_password_must_be_consistent));
			return false;
		}
		// 旧密码和新密码必须不一致
		if (et_old_password.getText().toString().equals(et_new_password.getText().toString())) {
			showTextToast(mContext,
					Utils.getResouceStr(mContext, R.string.the_new_password_and_old_password_must_not_be_consistent));
			return false;
		}

		return true;

	}

	/**
	 * 初始化系统控件
	 */
	private void initWidget() {

		rl_title_layout = (RelativeLayout) PasswordChangeActivity.this.findViewById(R.id.rl_title_layout);// 返回字
		et_old_password = (EditText) PasswordChangeActivity.this.findViewById(R.id.et_old_password);// 输入旧密码
		et_new_password = (EditText) PasswordChangeActivity.this.findViewById(R.id.et_new_password);// 输入新密码
		et_password_again = (EditText) PasswordChangeActivity.this.findViewById(R.id.et_password_again);// 确认密码
		btn_determine = (Button) PasswordChangeActivity.this.findViewById(R.id.ibtn_determine);// 确定
		mTitle_iv = (ImageView) PasswordChangeActivity.this.findViewById(R.id.title_iv);
		mTitle_tv = (TextView) PasswordChangeActivity.this.findViewById(R.id.title_tv);
		liear_title = (ImageView) PasswordChangeActivity.this.findViewById(R.id.liear_title);
		keyboard = (KeyboardView) findViewById(R.id.keyboard_view);
		ll = (LinearLayout) findViewById(R.id.password_ll);
	}

	/**
	 * 初始化显示页面
	 */

	private void init() {
		mTitle_iv.setVisibility(View.VISIBLE);
		mTitle_tv.setText(Utils.getResouceStr(mContext, R.string.password_change));
		preferenceService = SharedPreferenceService.getInstance(this);
	}

	/**
	 * 隐藏软键盘,而且带光标
	 */
	private void hideSoftKeyboard(EditText et) {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Method setShowSoftInputOnFocus = null;
		try {
			setShowSoftInputOnFocus = et.getClass().getMethod("setShowSoftInputOnFocus", boolean.class);
			setShowSoftInputOnFocus.setAccessible(true);
			setShowSoftInputOnFocus.invoke(et, false);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 控件绑定事件
	 */
	private void setClickListener() {

		Click click = new Click();
		btn_determine.setOnClickListener(click);
		mTitle_iv.setOnClickListener(click);
		liear_title.setOnClickListener(click);
		ll.setOnClickListener(click);

//		hideSoftKeyboard(et_new_password);
//		hideSoftKeyboard(et_password_again);
//		hideSoftKeyboard(et_old_password);
//		et_old_password.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (boardUtil != null && !boardUtil.keyBoardShow) {
//					boardUtil.setEd(et_old_password);
//					boardUtil.showKeyboard();
//				} else if (boardUtil != null && boardUtil.keyBoardShow) {
//					boardUtil.setEd(et_old_password);
//				} else if (boardUtil == null) {
//
//					boardUtil = new KeyboardUtil(mContext, et_old_password, keyboard);
//
//				}
//				return false;
//			}
//		});
//		et_new_password.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// Utils.hideInput(PasswordChangeActivity.this);
//				if (boardUtil != null && !boardUtil.keyBoardShow) {
//					boardUtil.setEd(et_new_password);
//					boardUtil.showKeyboard();
//				} else if (boardUtil != null && boardUtil.keyBoardShow) {
//					boardUtil.setEd(et_new_password);
//				} else if (boardUtil == null) {
//
//					boardUtil = new KeyboardUtil(mContext, et_new_password, keyboard);
//
//				}
//				return false;
//			}
//		});
//
//		et_password_again.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// Utils.hideInput(PasswordChangeActivity.this);
//				if (boardUtil != null && !boardUtil.keyBoardShow) {
//					boardUtil.setEd(et_password_again);
//					boardUtil.showKeyboard();
//				} else if (boardUtil != null && boardUtil.keyBoardShow) {
//					boardUtil.setEd(et_password_again);
//				} else if (boardUtil == null) {
//
//					boardUtil = new KeyboardUtil(mContext, et_password_again, keyboard);
//
//				}
//				return false;
//			}
//		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (boardUtil != null && boardUtil.keyBoardShow) {
			boardUtil.hideKeyboard();
			boardUtil.keyBoardShow = false;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 处理点击事件
	 */
	private class Click implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.title_iv: // 字返回
				PasswordChangeActivity.this.finish();
				break;
			case R.id.password_ll:
				if (boardUtil != null && boardUtil.keyBoardShow)
					boardUtil.hideKeyboard();
				break;

			case R.id.ibtn_determine: // 字返回
				if (!Utils.isFastDoubleClick()) {

					if (checkPassword()) {
						ep = new EditPwd(handler);
						// String md5String = Encrypt.getMD5String("111111");
						ep.qryEditPwd(UserVariable.CALLNUMBER, et_old_password.getText().toString().trim(),
								et_new_password.getText().toString().trim());
						sendMessage(SHOWDIALOG);
					}
				}
				break;
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(PasswordChangeActivity.this, MainGroupActivity.class));
				finish();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 请求xml成功后的处理
	 */
	protected void reqXmlSucessed(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_EDITPWD:
			EditPwdRsp en = (EditPwdRsp) msg.obj;
			boolean isRemember = preferenceService.get("isRemember", true);
			DESUtil encryptTool = DESUtil.getInstance();
			try {
				strPwd = encryptTool.encryptString(et_new_password.getText().toString().trim());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (isRemember) {
				preferenceService.put("userPwd", strPwd);
			}
			UserVariable.USERPWD = strPwd;
			UserCenterActivity.ISUSERPASSWORDUNSUBSCRIBE = true;
			Utils.showTextToast(mContext, UtlisReturnCode.ReturnCode(en.getResult(), mContext));
			sendMessage(DISSMISSDIALOG);
			PasswordChangeActivity.this.finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 请求xml成功后的失败处理
	 */
	protected void reqXmlFail(Message msg) {
		super.reqXmlFail(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_EDITPWD:
			EditPwdRsp en = (EditPwdRsp) msg.obj;
			sendMessage(DISSMISSDIALOG);
			showTextToast(mContext, UtlisReturnCode.ReturnCode(en.getResult(), mContext));
			break;
		default:
			break;
		}
	}

	@Override
	protected void reqError(Message msg) {
		sendMessage(DISSMISSDIALOG);
		super.reqError(msg);

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
}
