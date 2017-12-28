package com.dxiang.mring3.activity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.gem.imgcash.ImageDownLoader;

import com.dxiang.mring3.R;
import com.dxiang.mring3.adapter.CustomDialog;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.SendPwdReq;
import com.dxiang.mring3.request.UserSubSerReq;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.response.UserSubSerRsp;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.DESUtil;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.KeyboardUtil;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;
import com.dxiang.mring3.view.RoundedImageView;

/**
 * 登录页面的activity
 * 
 * @author Administrator
 * 
 */
public class LoginActivity extends BaseActivity implements OnTouchListener {
	// 返回键
	private Button mBtnback;
	// 用户名
	private EditText mEtUserName;
	// 用户账号
	private EditText mEtUserPwd;
	// rember me
	private CheckBox mCbRemember;
	// auto Login
	private CheckBox mCbAutoLogin;
	// 忘记密码
	private ImageView mIvForget;
	// 登录按钮
	private Button mBtnLogin;
	// 注册按钮
	private Button mBtnRegist;
	//
	private SharedPreferenceService preferenceService;
	private ImageView mIvBack;
	private TextView mTitle;
	private String from;
	// 加密的密码
	private String strPwd;
	private LinearLayout mLoginContent;
	private TextView mTVRemember;
	private TextView mTVAutoLogin;
	private boolean mNeedSkip = false;
	private ImageView liear_title;
	private RoundedImageView imagehead;
	private InputMethodManager manager;
	private int inputType;
	private KeyboardView keyboard;
	private KeyboardUtil boardUtil;
	private RelativeLayout rl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_login);
		initData();
		initView();
		setListener();
		toggleHideyBar();
		if (getIntent().getStringExtra("from") != null) {
			from = getIntent().getStringExtra("from");

		}
		TextSize();
	}

	/**
	 * 设置字体大小和控件位置
	 */
	private void TextSize() {
		String language = preferenceService.get("language", "");
		if (Utils.CheckTextNull(language)) {
			mTVRemember.setTextSize(14f);
			mTVAutoLogin.setTextSize(14f);
			LayoutParams lp = new LayoutParams(mIvForget.getLayoutParams());
			LayoutParams params = (LayoutParams) mCbAutoLogin.getLayoutParams();
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lp.setMargins(12, 0, 0, 0);
			params.setMargins(14, 0, 0, 0);
			mCbAutoLogin.setLayoutParams(params);
			mIvForget.setLayoutParams(lp);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (boardUtil != null && boardUtil.keyBoardShow) {
				boardUtil.hideKeyboard();
				boardUtil.keyBoardShow = false;
				return true;
			}
			if (null != from && from.equals("OrderFragment")) {
				finish();
				return true;
			}
			if (null != from && from.equals("DetailsRingtoneActivity")) {
				finish();
				return true;
			}
			Intent intent = new Intent(LoginActivity.this, MainGroupActivity.class);
			intent.putExtra("retunmain", true);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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

	public void toggleHideyBar() {

		// The UI options currently enabled are represented by a bitfield.
		// getSystemUiVisibility() gives us that bitfield.
		int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;
		boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
		if (isImmersiveModeEnabled) {
			Log.i("toggleHideyBar", "Turning immersive mode mode off. ");
		} else {
			Log.i("toggleHideyBar", "Turning immersive mode mode on.");
		}

		// Navigation bar hiding: Backwards compatible to ICS.
		if (Build.VERSION.SDK_INT >= 14) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}

		// Status bar hiding: Backwards compatible to Jellybean
		if (Build.VERSION.SDK_INT >= 16) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
		}

		// Immersive mode: Backward compatible to KitKat.
		// Note that this flag doesn't do anything by itself, it only augments
		// the behavior
		// of HIDE_NAVIGATION and FLAG_FULLSCREEN. For the purposes of this
		// sample
		// all three flags are being toggled together.
		// Note that there are two immersive mode UI flags, one of which is
		// referred to as "sticky".
		// Sticky immersive mode differs in that it makes the navigation and
		// status bars
		// semi-transparent, and the UI flag does not get cleared when the user
		// interacts with
		// the screen.
		if (Build.VERSION.SDK_INT >= 18) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}

		getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		// inputType = mEtUserPwd.getInputType();
		// mEtUserPwd.setInputType(InputType.TYPE_NULL);

//		hideSoftKeyboard(mEtUserName);
//		hideSoftKeyboard(mEtUserPwd);
		rl.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Utils.hideInput(LoginActivity.this);
				if (boardUtil != null && boardUtil.keyBoardShow)
					boardUtil.hideKeyboard();
				
				return false;
			}
		});
//		mEtUserName.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (boardUtil != null && !boardUtil.keyBoardShow) {
//					boardUtil.setEd(mEtUserName);
//					boardUtil.showKeyboard();
//				} else if (boardUtil != null && boardUtil.keyBoardShow) {
//					boardUtil.setEd(mEtUserName);
//				} else if (boardUtil == null) {
//					boardUtil = new KeyboardUtil(mContext, mEtUserName, keyboard);
//
//				}
//				return false;
//			}
//		});
//		mEtUserPwd.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//
//				// manager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
//				// 0);
//
//				if (boardUtil != null && !boardUtil.keyBoardShow) {
//					boardUtil.setEd(mEtUserPwd);
//					boardUtil.showKeyboard();
//				} else if (boardUtil != null && boardUtil.keyBoardShow) {
//					boardUtil.setEd(mEtUserPwd);
//				} else if (boardUtil == null) {
//
//					boardUtil = new KeyboardUtil(mContext, mEtUserPwd, keyboard);
//
//				}
//				// mEtUserPwd.setInputType(inputType);
//				return false;
//			}
//		});
		// TODO Auto-generated method stub
		mLoginContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.hideInput(LoginActivity.this);
				if (boardUtil != null) {
					boardUtil.hideKeyboard();
					Utils.hideInput(LoginActivity.this);
				}
			}
		});
		mBtnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startLogin();
				// preferenceService.put("logined", true);
				// startActivity(new Intent(LoginActivity.this,
				// MainGroupActivity.class));
				// finish();
			}
		});
		mBtnRegist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
				startActivity(intent);
			}
		});
		mCbAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					mCbRemember.setChecked(true);
				}
			}
		});
		mTVAutoLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mCbAutoLogin.isChecked()) {
					mCbAutoLogin.setChecked(true);
					if (!mCbRemember.isChecked()) {
						mCbRemember.setChecked(true);
					}
				} else {
					mCbAutoLogin.setChecked(false);
				}
			}
		});
		mCbRemember.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					if (mCbAutoLogin.isChecked()) {
						mCbAutoLogin.setChecked(false);
					}
				}
			}
		});
		mTVRemember.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mCbRemember.isChecked()) {
					mCbRemember.setChecked(true);
				} else {
					mCbRemember.setChecked(false);
					if (mCbAutoLogin.isChecked()) {
						mCbAutoLogin.setChecked(false);
					}
				}
			}
		});
		mIvForget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(mEtUserName.getText().toString())) {
					Utils.showTextToast(LoginActivity.this, R.string.phonenumber_check);
					return;
				}
				String callNumber = mEtUserName.getText().toString();
				// 发起找回密码的请求
				startPbarU();
				new SendPwdReq(handler).sendGetPwdRequest(callNumber);
			}
		});

		mIvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != from && from.equals("OrderFragment")) {
					finish();
				} else if (null != from && from.equals("DetailsRingtoneActivity")) {
					finish();
				} else {
					Intent intent = new Intent(LoginActivity.this, MainGroupActivity.class);
					intent.putExtra("retunmain", true);
					startActivity(intent);
					finish();
				}
			}
		});
		liear_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(LoginActivity.this, MainGroupActivity.class));
				finish();
			}
		});
	}

	/**
	 * 初始化组件和组件数据
	 */
	private void initView() {
		imagehead = (RoundedImageView) findViewById(R.id.imagehead);
		// 初始化组件
		rl = (RelativeLayout) findViewById(R.id.login_rl);
		mLoginContent = (LinearLayout) findViewById(R.id.login_content);
		mEtUserName = (EditText) findViewById(R.id.login_name_et);
		mEtUserPwd = (EditText) findViewById(R.id.login_pwd_et);
		keyboard = (KeyboardView) findViewById(R.id.keyboard_view);
		mCbRemember = (CheckBox) findViewById(R.id.login_remember_cb);
		mTVRemember = (TextView) findViewById(R.id.remember);
		mCbAutoLogin = (CheckBox) findViewById(R.id.login_auto_login_cb);
		mTVAutoLogin = (TextView) findViewById(R.id.quto);
		mIvForget = (ImageView) findViewById(R.id.login_forget_iv);
		mBtnLogin = (Button) findViewById(R.id.login_btn);
		mBtnRegist = (Button) findViewById(R.id.regist_btn);
		mIvBack = (ImageView) findViewById(R.id.title_iv);
		mIvBack.setVisibility(View.VISIBLE);
		mTitle = (TextView) findViewById(R.id.title_tv);
		liear_title = (ImageView) findViewById(R.id.liear_title);

		// 初始化组件数据
		mTitle.setText(R.string.login_title_txt);
		String textname = preferenceService.get("userName", "");
		mEtUserName.setText(textname);
		if (preferenceService.get("isAutoLogin", false)) {
			mCbAutoLogin.setChecked(true);
		}
		String headurl = Commons.getImageSavedpath1() + textname + Commons.headname;
		if (new File(headurl).exists()) {
			ImageDownLoader.getShareImageDownLoader().clearCache();
			imagehead.setImageBitmap(ImageDownLoader.getShareImageDownLoader().showCacheBitmap(headurl));
		} else {
			imagehead.setImageResource(R.drawable.user_imforimgtop);
		}
		if (preferenceService.get("isRemember", false)) {
			String encPwd = preferenceService.get("userPwd", "");
			String pwd = "";
			if (!TextUtils.isEmpty(encPwd)) {
				DESUtil encryptTool = DESUtil.getInstance();
				try {
					pwd = encryptTool.getDecrypt(encPwd);
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mEtUserPwd.setText(pwd);
			mCbRemember.setChecked(true);
		}

	}

	/**
	 * 初始化SharedPreference
	 */
	private void initData() {
		// TODO Auto-generated method stub
		preferenceService = SharedPreferenceService.getInstance(this);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

	}

	/**
	 * 开始登陆，检测输入的合法性，发起登录请求
	 */
	private void startLogin() {
		String phoneNo = mEtUserName.getText().toString();
		String pwd = mEtUserPwd.getText().toString();
		if ("".equals(phoneNo)) {
			Utils.showTextToast(LoginActivity.this, R.string.phonenumber_check);
			return;
		}
		if ("".equals(pwd)) {
			Utils.showTextToast(LoginActivity.this, R.string.password_can_not_be_empty);
			return;
		}
		startPbarU();
		// new UserSubSerReq(handler).sendUserSubSerRequest("13306306003",
		// "222222");
		DESUtil encryptTool = DESUtil.getInstance();
		try {
			strPwd = encryptTool.encryptString(pwd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new UserSubSerReq(handler).sendUserSubSerRequest(phoneNo, strPwd);

	}

	/**
	 * 登录跳转前,保存用户账号信息
	 */
	private void beforLogined() {
		UserVariable.CALLNUMBER = mEtUserName.getText().toString();
		UserVariable.USERPWD = strPwd;
		preferenceService.put("userName", mEtUserName.getText().toString());
		if (mCbRemember.isChecked()) {
			preferenceService.put("isRemember", true);
			preferenceService.put("userPwd", strPwd);
		} else {
			preferenceService.put("isRemember", false);
			preferenceService.put("userPwd", "");
		}

		if (mCbAutoLogin.isChecked()) {

			preferenceService.put("isAutoLogin", true);
		}
		UserVariable.LOGINED = true;
	}

	@Override
	protected void reqXmlSucessed(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_SENDPWDEVT:
			stopPbarU();
			Utils.showTextToast(LoginActivity.this, R.string.message_notice);
			break;
		case FusionCode.REQUEST_USERSUBSEREVT:
			// 发起登录请求，根据返回结果处理
			mNeedSkip = true;
			UserSubSerRsp userSubSerRsp = (UserSubSerRsp) msg.obj;
			UserVariable.STATUSCALLED = userSubSerRsp.getStatusCalled();
			UserVariable.STATUSCALLING = userSubSerRsp.getStatusCalling();
			stopPbarU();
			if (isShowing()) {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						skipToMe();
					}
				}, Commons.HIDETIME);
			} else {
				skipToMe();
			}
			break;
		}

	}

	/**
	 * 处理不同登录请求的结果： 1、如果用户主叫被叫只要有一个为1，跳转到me页面 2、如果主叫或者被叫有一个为3，且另一个不为1，提示用户已经停机
	 * 3、如果主叫或被叫都不为1或3，提示激活。
	 */
	private void skipToMe() {
		mNeedSkip = false;
		if (null != from && from.equals("OrderFragment")) {
			if ((UserVariable.STATUSCALLED.equals("0") && UserVariable.STATUSCALLING.equals("0"))) {
				CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
				builder.setMessage(R.string.login_not_active, Gravity.LEFT);
				builder.setTitle(R.string.user_cancellation);
				builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				builder.setNegativeButton(R.string.determine, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
						startActivity(intent);
					}
				});

				builder.create().show();
				// Utils.showTextToast(LoginActivity.this, "Not exist");
				return;
			} else if ((UserVariable.STATUSCALLED.equals("2") && !UserVariable.STATUSCALLING.equals("1"))
					|| (!UserVariable.STATUSCALLED.equals("1") && UserVariable.STATUSCALLING.equals("2"))) {
				CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
				builder.setMessage(R.string.login_not_active, Gravity.LEFT);
				builder.setTitle(R.string.user_cancellation);
				builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				builder.setNegativeButton(R.string.determine, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
						startActivity(intent);
					}
				});

				builder.create().show();
				// Utils.showTextToast(LoginActivity.this,
				// "Please log in after to activate.");
				return;
			} else if ((UserVariable.STATUSCALLED.equals("3") && !UserVariable.STATUSCALLING.equals("1"))
					|| (!UserVariable.STATUSCALLED.equals("1") && UserVariable.STATUSCALLING.equals("3"))) {
				CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
				builder.setMessage(R.string.login_suspended, Gravity.LEFT);
				builder.setTitle(R.string.user_cancellation);
				builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				builder.setNegativeButton(R.string.determine, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
						startActivity(intent);
					}
				});

				builder.create().show();
				// Utils.showTextToast(LoginActivity.this,
				// "you have been suspended. Please top up first.");
				return;
			} else {
				beforLogined();
				finish();
				return;
			}
		} else if ((UserVariable.STATUSCALLED.equals("0") && UserVariable.STATUSCALLING.equals("0"))) {
			CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
			builder.setMessage(R.string.login_not_active, Gravity.LEFT);
			builder.setTitle(R.string.user_cancellation);
			builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.setNegativeButton(R.string.determine, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
					startActivity(intent);
				}
			});

			builder.create().show();
			// Utils.showTextToast(LoginActivity.this, "Not exist");
			return;
		} else if ((UserVariable.STATUSCALLED.equals("2") && !UserVariable.STATUSCALLING.equals("1"))
				|| (!UserVariable.STATUSCALLED.equals("1") && UserVariable.STATUSCALLING.equals("2"))) {
			CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
			builder.setMessage(R.string.login_not_active, Gravity.LEFT);
			builder.setTitle(R.string.user_cancellation);
			builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.setNegativeButton(R.string.determine, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
					startActivity(intent);
				}
			});

			builder.create().show();
			// Utils.showTextToast(LoginActivity.this,
			// "Please log in after to activate.");
			return;
		} else if ((UserVariable.STATUSCALLED.equals("3") && !UserVariable.STATUSCALLING.equals("1"))
				|| (!UserVariable.STATUSCALLED.equals("1") && UserVariable.STATUSCALLING.equals("3"))) {
			CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
			builder.setMessage(R.string.login_suspended, Gravity.LEFT);
			builder.setTitle(R.string.user_cancellation);
			builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.setNegativeButton(R.string.determine, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
					startActivity(intent);
				}
			});

			builder.create().show();
			// Utils.showTextToast(LoginActivity.this,
			// "you have been suspended. Please top up first.");
			return;
		} else {
			beforLogined();
			if (null != from && from.equals("OrderFragment")) {
				finish();
			} else if (null != from && from.equals("DetailsRingtoneActivity")) {
				finish();
			} else {
				Intent intent = new Intent(LoginActivity.this, MainGroupActivity.class);
				intent.putExtra("LoginActivityRetunMain", true);
				startActivity(intent);
				finish();
			}
		}
	}

	@Override
	protected void reqError(Message msg) {
		// TODO Auto-generated method stub
		super.reqError(msg);
		stopPbarU();
	}

	@Override
	protected void reqXmlFail(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlFail(msg);
		stopPbarU();
		EntryP entryP = (EntryP) msg.obj;
		if ("301001".equals(entryP.result)) {
			CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);
			builder.setMessage(R.string.login_not_active, Gravity.LEFT);
			builder.setTitle(R.string.user_cancellation);
			builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.setNegativeButton(R.string.determine, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
					startActivity(intent);
				}
			});

			builder.create().show();
		} else {

			Utils.showTextToast(LoginActivity.this, UtlisReturnCode.ReturnCode(entryP.getResult(), mContext));
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (boardUtil!=null&& boardUtil.keyBoardShow) {// dx-2017-12-28
			boardUtil.hideKeyboard();
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return false;
	}

}
