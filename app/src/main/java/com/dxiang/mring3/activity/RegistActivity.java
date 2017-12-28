package com.dxiang.mring3.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

import com.dxiang.mring3.R;
import com.dxiang.mring3.adapter.CustomDialog;
import com.dxiang.mring3.adapter.CustomDialogUpdate;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.SubscribeReq;
import com.dxiang.mring3.request.VerCodeReq;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.KeyboardUtil;
import com.dxiang.mring3.utils.TimeCount;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

/**
 * 注册页面的activity
 * 
 * @author Administrator
 * 
 */
public class RegistActivity extends BaseActivity {

	// 返回键
	private ImageView mIvBack;
	// 标题
	private TextView mTvTitle;
	// // 被叫cb
	// private CheckBox mCbCalled;
	// // 主叫cb
	// private CheckBox mCbCaller;
	// 号码输入框
	private EditText mEtPhoneNo;
	// 验证码输入框
	private EditText mEtVerCode;
	// 注册按钮
	private Button mBtnRegist;
	// 验证码按钮
	private Button mBtnVerCode;

	private LinearLayout mRegistContent;

	private SharedPreferenceService preferenceService;
	private ImageView liear_title;

	/**
	 * 键盘相关类
	 */
	private KeyboardView boardView;
	private KeyboardUtil boardUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		initViews();
		setListener();
		hintTextSize();
	}

	/**
	 * 设置hint字体大小
	 */
	private void hintTextSize() {
		preferenceService = SharedPreferenceService.getInstance(this);
		String language = preferenceService.get("language", "");
		if (Utils.CheckTextNull(language)) {
			Utils.hintSize(mEtVerCode, 14);
			// mCbCalled.setTextSize(13);
			// mCbCaller.setTextSize(13);
		}
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		// TODO Auto-generated method stub
		mRegistContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.hideInput(RegistActivity.this);
				if (boardUtil != null && boardUtil.keyBoardShow)
					boardUtil.hideKeyboard();
			}
		});
		// mCbCalled.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// if (mCbCalled.isChecked()) {
		// return true;
		// }
		// return false;
		// }
		// });
		// mCbCalled.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// // TODO Auto-generated method stub
		// if (isChecked) {
		// if (mCbCaller.isChecked()) {
		// mCbCaller.setChecked(false);
		// if (mTimeCount != null) {
		// mTimeCount.cancel();
		// mTimeCount.onFinish();
		// }
		// }
		// }
		// }
		// });
		// mCbCaller.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// if (mCbCaller.isChecked()) {
		// return true;
		// }
		// return false;
		// }
		// });
		// mCbCaller.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// // TODO Auto-generated method stub
		// if (isChecked) {
		// if (mCbCalled.isChecked()) {
		// mCbCalled.setChecked(false);
		// if (mTimeCount != null) {
		// mTimeCount.cancel();
		// mTimeCount.onFinish();
		// }
		// }
		// }
		// }
		// });

		mIvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mBtnVerCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (boardUtil != null && boardUtil.keyBoardShow)
					boardUtil.hideKeyboard();
				String callNumber = mEtPhoneNo.getText().toString();
				if (!TextUtils.isEmpty(callNumber)) {
					startPbarU();
					mBtnVerCode.setClickable(false);
					new VerCodeReq(handler).sendVerCodeReq(callNumber, "1");
					// new TimeCount(120000, 1000).start();
				} else {
					Utils.showTextToast(RegistActivity.this, R.string.phonenumber_check);
				}

				// 获取验证码
			}
		});
		mBtnRegist.setOnClickListener(new OnClickListener() {

			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// if (!mCbCalled.isChecked() && !mCbCaller.isChecked()) {
				// Utils.showTextToast(RegistActivity.this,
				// R.string.register_atleast);
				// return;
				// }
				if (boardUtil != null && boardUtil.keyBoardShow)
					boardUtil.hideKeyboard();
				if (TextUtils.isEmpty(mEtPhoneNo.getText().toString())) {
					Utils.showTextToast(RegistActivity.this, R.string.phonenumber_check);
					return;
				}
				if (TextUtils.isEmpty(mEtVerCode.getText().toString())) {
					Utils.showTextToast(RegistActivity.this, R.string.vercode_check);
					return;
				}
				startPbarU();
				regist();
			}
		});

		mEtPhoneNo.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (boardUtil != null && boardUtil.keyBoardShow)
					boardUtil.hideKeyboard();
				return false;
			}
		});
//		hideSoftKeyboard(mEtVerCode);
//		mEtVerCode.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				Utils.hideInput(RegistActivity.this);
//				if (boardUtil != null && !boardUtil.keyBoardShow) {
//					boardUtil.setEd(mEtVerCode);
//					boardUtil.showKeyboard();
//				} else if (boardUtil != null && boardUtil.keyBoardShow) {
//					boardUtil.setEd(mEtVerCode);
//				} else if (boardUtil == null) {
//					boardUtil = new KeyboardUtil(mContext, mEtVerCode, boardView);
//
//				}
//				return false;
//			}
//		});
		liear_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(RegistActivity.this, MainGroupActivity.class));
				finish();
			}
		});
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
	 * 初始化组件
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		mRegistContent = (LinearLayout) findViewById(R.id.regist_content);
		mIvBack = (ImageView) findViewById(R.id.title_iv);
		mTvTitle = (TextView) findViewById(R.id.title_tv);
		// mCbCalled = (CheckBox) findViewById(R.id.regist_called_iv);
		// mCbCaller = (CheckBox) findViewById(R.id.regist_caller_iv);
		mEtPhoneNo = (EditText) findViewById(R.id.regist_phone_num_et);
		mEtVerCode = (EditText) findViewById(R.id.regist_vercode_et);
		mBtnRegist = (Button) findViewById(R.id.regist_btn);
		mBtnVerCode = (Button) findViewById(R.id.regist_vercode_btn);
		liear_title = (ImageView) findViewById(R.id.liear_title);
		boardView = (KeyboardView) findViewById(R.id.keyboard_view);
		// mCbCalled.setChecked(true);
		mIvBack.setVisibility(View.VISIBLE);
		mTvTitle.setText(R.string.regist);
	}

	/**
	 * 检测输入合法性，发起注册请求
	 */
	private void regist() {
		String phoneNo = mEtPhoneNo.getText().toString();
		String verCode = mEtVerCode.getText().toString();
		if (!Utils.CheckTextNull(phoneNo)) {
			// 提示号码和验证码不能为空
			Utils.showTextToast(RegistActivity.this, R.string.phonenumber_check);
		} else if (!Utils.CheckTextNull(verCode)) {
			Utils.showTextToast(RegistActivity.this, R.string.vercode_check);
		} else {
			// 注册请求
			// 跳转登录页面
			// startFragment(new LoginFragment());
			String serviceType = "1";
			// if (mCbCalled.isChecked()) {
			// serviceType = "1";
			// } else if (mCbCaller.isChecked()) {
			// serviceType = "2";
			// }
			startPbarU();
			new SubscribeReq(handler).sendSubscribeReq(mEtPhoneNo.getText().toString(), "0", serviceType, "1", "1",
					mEtVerCode.getText().toString());
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	CountDownTimer mTimeCount;

	@Override
	protected void reqXmlSucessed(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_VERCODEEVT:
			mEtVerCode.setEnabled(true);
			mTimeCount = new TimeCount(120000, 1000, mBtnVerCode, mEtVerCode).start();
			stopPbarU();
			break;
		case FusionCode.REQUEST_SUBSCRIBEAPPEVT:
			stopPbarU();
			CustomDialogUpdate.Builder builder = new CustomDialogUpdate.Builder(mContext);
			builder.setMessage(R.string.regist_success_txt);
			builder.setTitle(R.string.user_cancellation);
			builder.setPositiveButton(R.string.btn_comfirm, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});
			builder.create().show();
			break;
		default:
			break;
		}
	}

	@Override
	protected void reqError(Message msg) {
		// TODO Auto-generated method stub
		super.reqError(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_VERCODEEVT:
			mBtnVerCode.setClickable(true);
			break;
		}
		stopPbarU();
	}

	@Override
	protected void reqXmlFail(Message msg) {
		// TODO Auto-generated method stub
		stopPbarU();
		super.reqXmlFail(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_VERCODEEVT:
			mBtnVerCode.setClickable(true);
			break;
		}
		EntryP entryP = (EntryP) msg.obj;
		if ("400028".equals(entryP.getResult())) {
			CustomDialogUpdate.Builder builder = new CustomDialogUpdate.Builder(mContext);
			builder.setMessage(UtlisReturnCode.ReturnCode(entryP.getResult(), mContext));
			builder.setTitle(R.string.user_cancellation);
			builder.setPositiveButton(R.string.determine, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		} else
			Utils.showTextToast(RegistActivity.this, UtlisReturnCode.ReturnCode(entryP.getResult(), mContext));
	}
}
