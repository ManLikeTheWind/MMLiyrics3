package com.dxiang.mring3.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.SetingRandomDefaultActivity;
import com.dxiang.mring3.adapter.CustomDialog;
import com.dxiang.mring3.adapter.UserCenterDialog;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.ActDeactUser;
import com.dxiang.mring3.request.SubscribeReq;
import com.dxiang.mring3.request.UnSubscribe;
import com.dxiang.mring3.request.UserSubSerReq;
import com.dxiang.mring3.response.ActdeactUserRsp;
import com.dxiang.mring3.response.UnSubscribeRsp;
import com.dxiang.mring3.response.UserSubSerRsp;
import com.dxiang.mring3.slipbutton.Slipbutton;
import com.dxiang.mring3.slipbutton.Slipbutton.OnChangedListener;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

/**
 * 用户中心界面
 * 
 * @author humingzhuo
 */

@SuppressLint("ValidFragment")
public class UserCenterActivity extends BaseActivity {

	// 控件

	private RelativeLayout ll_usercenter, rl_clled, rl_cller;
	private LinearLayout ll_unsubscribe_caller, ll_unsubscribe_called;
	private TextView tv_passwordchange, tv_cller, tv_clled, tv_unsubscribe_caller, tv_unsubscribe_called, tv_logoff,
			tv_logout, mTitle_tv;
	private ImageView mTitle_iv;
	private CheckBox check_rbt;
	private Slipbutton celler;// 激活未激活主叫
	private Slipbutton celled;// 激活未激活被叫
	// 变量

	// 激活去激活
	private ActDeactUser actdeatUser;

	// 用户登录
	private UserSubSerReq mUserSubSerReq;

	private int id, userId;

	// 主叫是否存在赋值
	private String userCeller;

	// 被叫是否存在赋值
	private String userCelled;

	// 注销
	private UnSubscribe unsubscribe;

	// 判断状态
	public static boolean ISCHECKUSER = false;

	public static boolean ISUSERPASSWORDUNSUBSCRIBE = false;

	private boolean ISLOGOFF = false;

	private SharedPreferenceService preferenceService;
	private ImageView liear_title;
	// 记录点击的位置
	private int mUserCenter = 1;
	private final int ACTIVATECALLER = 1;
	private final int ACTIVATECALLED = 2;
	private TextView tv_setusernikename;

	private TextView tv_setrandomdefault;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usercenter);
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
		if (ISCHECKUSER) {
			ISCHECKUSER = false;
			startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
			setResult(10);
			UserCenterActivity.this.finish();
			return;
		}
//		if (ISUSERPASSWORDUNSUBSCRIBE) {
//			ISUSERPASSWORDUNSUBSCRIBE = false;
			mUserSubSer();
			startPbarU();
//		}
		isUserCenterActDeact(UserVariable.STATUSCALLING, UserVariable.STATUSCALLED);
	}

	/**
	 * 初始化系统控件
	 */
	private void initWidget() {
		tv_setusernikename = (TextView) findViewById(R.id.tv_setusernikename);
		ll_usercenter = (RelativeLayout) UserCenterActivity.this.findViewById(R.id.rl_title_layout);// 返回字
		tv_passwordchange = (TextView) UserCenterActivity.this.findViewById(R.id.tv_passwordchange);// 修改密码
		
		tv_setrandomdefault=(TextView) UserCenterActivity.this.findViewById(R.id.tv_setrandomdefault);// 设置随机默认模式
		tv_cller = (TextView) UserCenterActivity.this.findViewById(R.id.tv_cller);// 来电激活去激活
		tv_clled = (TextView) UserCenterActivity.this.findViewById(R.id.tv_clled);// 去电激活去激活
		tv_unsubscribe_caller = (TextView) UserCenterActivity.this.findViewById(R.id.tv_unsubscribe_caller);// 注销来电
		tv_unsubscribe_called = (TextView) UserCenterActivity.this.findViewById(R.id.tv_unsubscribe_called);// 注销去电
		rl_cller = (RelativeLayout) UserCenterActivity.this.findViewById(R.id.rl_cller);// 注销来电
		rl_clled = (RelativeLayout) UserCenterActivity.this.findViewById(R.id.rl_clled);// 注销去电
		celler = (Slipbutton) UserCenterActivity.this.findViewById(R.id.order_tv1);// 激活来电
		celled = (Slipbutton) UserCenterActivity.this.findViewById(R.id.order_tv2);// 激活去电
		celler.setChecked(false);
		celled.setChecked(false);
		ll_unsubscribe_caller = (LinearLayout) UserCenterActivity.this.findViewById(R.id.ll_unsubscribe_caller);// 激活来电
		ll_unsubscribe_called = (LinearLayout) UserCenterActivity.this.findViewById(R.id.ll_unsubscribe_called);// 激活来电
		tv_logout = (TextView) UserCenterActivity.this.findViewById(R.id.tv_logout);// 注销账户
		tv_logoff = (TextView) UserCenterActivity.this.findViewById(R.id.tv_logoff);// 退出
		mTitle_iv = (ImageView) UserCenterActivity.this.findViewById(R.id.title_iv);
		mTitle_tv = (TextView) UserCenterActivity.this.findViewById(R.id.title_tv);
		liear_title = (ImageView) UserCenterActivity.this.findViewById(R.id.liear_title);
		check_rbt = (CheckBox) UserCenterActivity.this.findViewById(R.id.check_rbt);

	}

	/**
	 * 初始化显示页面
	 */
	private void init() {
		mTitle_iv.setVisibility(View.VISIBLE);
		tv_logoff.setVisibility(View.GONE);
		mTitle_tv.setText(getResources().getString(R.string.user_center));
		preferenceService = SharedPreferenceService.getInstance(this);
		actdeatUser = new ActDeactUser(handler);
		unsubscribe = new UnSubscribe(handler);
		mUserSubSerReq = new UserSubSerReq(handler);
		isUserCenterActDeact(UserVariable.STATUSCALLING, UserVariable.STATUSCALLED);

		mUserSubSer();
		startPbarU();

	}

	/**
	 * 控件绑定事件
	 */
	private void setClickListener() {

		Click click = new Click();
		Changed changed = new Changed();// 滑动
		celler.setOnChangedListener(changed);
		celled.setOnChangedListener(changed);
		mTitle_iv.setOnClickListener(click);
		tv_passwordchange.setOnClickListener(click);
		tv_setrandomdefault.setOnClickListener(click);
		tv_setusernikename.setOnClickListener(click);
		tv_unsubscribe_caller.setOnClickListener(click);
		tv_unsubscribe_called.setOnClickListener(click);
		tv_logout.setOnClickListener(click);
		tv_logoff.setOnClickListener(click);
		liear_title.setOnClickListener(click);
		check_rbt.setOnClickListener(click);
	}

	/**
	 * 判断是否用户有被叫或主叫（有则显示）
	 * 
	 * @param cller
	 * @param clled
	 */
	public void isUserCenterActDeact(String cller, String clled) {
		UtilLog.e(cller + "----   :   ---" + clled);
		if ("1".equals(UserVariable.STATUSCALLED)) {
			UtilLog.e(UserVariable.STATUSCALLED + "------------");
			UserCenterActivity.this.celled.setChecked(true);
			celled.invalidate();
		} else {
			UserCenterActivity.this.celled.setChecked(false);
			celled.invalidate();
		}
		if ("1".equals(UserVariable.STATUSCALLING)) {
			UserCenterActivity.this.celler.setChecked(true);
			celler.invalidate();
		} else {
			UserCenterActivity.this.celler.setChecked(false);
			celler.invalidate();
		}

		// if (!"0".equals(cller)) {
		// rl_cller.setVisibility(View.VISIBLE);
		// ll_unsubscribe_caller.setVisibility(View.VISIBLE);
		// userCeller = "1";
		// } else {
		// rl_cller.setVisibility(View.GONE);
		// ll_unsubscribe_caller.setVisibility(View.GONE);
		// userCeller = "2";
		// }
		// if (!"0".equals(clled)) {
		// rl_clled.setVisibility(View.VISIBLE);
		// ll_unsubscribe_called.setVisibility(View.VISIBLE);
		// userCelled = "1";
		// } else {
		// rl_clled.setVisibility(View.GONE);
		// ll_unsubscribe_called.setVisibility(View.GONE);
		// userCelled = "2";
		// }
	}

	/**
	 * 处理点击事件
	 */
	private class Click implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.tv_setusernikename:// nikename设置
				Intent intent = new Intent(UserCenterActivity.this, UserNikenameSetActivity.class);
				startActivity(intent);
				break;
			case R.id.title_iv: // 字返回
				if (!Utils.isFastDoubleClick()) {
					setResult(20);
					UserCenterActivity.this.finish();
				}
				break;
			case R.id.tv_passwordchange: // 密码
				if (!Utils.isFastDoubleClick()) {
					startActivity(new Intent(UserCenterActivity.this, PasswordChangeActivity.class));
				}
				break;

			case R.id.tv_setrandomdefault: // 设置默认随机
				if (!Utils.isFastDoubleClick()) {
					startActivity(new Intent(UserCenterActivity.this, SetingRandomDefaultActivity.class));
				}
				break;
			case R.id.tv_unsubscribe_caller: // 来电注销
				if (!Utils.isFastDoubleClick()) {
					// 判断是否是一个 显示都显示
					if (Utils.CheckTextNull(UserVariable.STATUSCALLED)
							|| Utils.CheckTextNull(UserVariable.STATUSCALLING)) {
						if ("0".equals(UserVariable.STATUSCALLED) && !"0".equals(UserVariable.STATUSCALLING)) {
							CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
							builder.setMessage(R.string.do_you_confirm_to_cancle_caller_RBT_service, Gravity.CENTER);
							builder.setTitle(R.string.user_cancellation);
							builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									// 设置你的操作事项
								}
							});

							builder.setNegativeButton(R.string.determine,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
											startActivity(
													new Intent(UserCenterActivity.this, UnsubscribeActivity.class));
											UnsubscribeActivity.id = "2";
										}
									});

							builder.create().show();
						} else {
							startActivity(new Intent(UserCenterActivity.this, UnsubscribeActivity.class));
							UnsubscribeActivity.id = "2";
						}
					} else {
						// showTextToast(mContext, "STATUSCALLING 空");
					}
				}
				break;
			case R.id.check_rbt:
				if (UserVariable.STATUSCALLING.equals("2") || UserVariable.STATUSCALLING.equals("0")) {
					final String serviceType = "2";
					UserCenterDialog.Builder builder = new UserCenterDialog.Builder(mContext);
					builder.setMessage(getResources().getString(R.string.register_rbt));
					builder.setTitle(getResources().getString(R.string.dialog_title));
					builder.setPositiveButton(getResources().getString(R.string.determine),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									new SubscribeReq(handler).sendSubscribeReq(UserVariable.CALLNUMBER, "0",
											serviceType, "1", "1", null);
									dialog.dismiss();
								}
							});
					builder.create().show();
				} else {
					final String serviceType = "2";
					UserCenterDialog.Builder builder = new UserCenterDialog.Builder(mContext);
					builder.setMessage(getResources().getString(R.string.deregister_rbt));
					builder.setTitle(getResources().getString(R.string.dialog_title));
					builder.setPositiveButton(getResources().getString(R.string.determine),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									unsubscribe.qryUnSubscribe(UserVariable.CALLNUMBER, UserVariable.USERPWD, "2",
											true);
									dialog.dismiss();
								}
							});
					builder.create().show();
				}
				break;
			case R.id.tv_unsubscribe_called: // 被叫注销
				if (!Utils.isFastDoubleClick()) {
					// 判断是否是一个 显示都显示
					UtilLog.e(UserVariable.STATUSCALLED + "     :    " + UserVariable.STATUSCALLING);
					if (Utils.CheckTextNull(UserVariable.STATUSCALLED)
							|| Utils.CheckTextNull(UserVariable.STATUSCALLING)) {

						if (!"0".equals(UserVariable.STATUSCALLED) && "0".equals(UserVariable.STATUSCALLING)) {
							CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
							builder.setMessage(R.string.do_you_confirm_to_cancle_called_RBT_service, Gravity.CENTER);
							builder.setTitle(R.string.user_cancellation);
							builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									// 设置你的操作事项
								}
							});

							builder.setNegativeButton(R.string.determine,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
											startActivity(
													new Intent(UserCenterActivity.this, UnsubscribeActivity.class));
											UnsubscribeActivity.id = "1";
										}
									});

							builder.create().show();
						} else {
							startActivity(new Intent(UserCenterActivity.this, UnsubscribeActivity.class));
							UnsubscribeActivity.id = "1";
						}
					}
				}
				break;

			case R.id.tv_logout: // 退出登录
				if (!Utils.isFastDoubleClick()) {
					CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
					builder.setMessage(R.string.exit_the_current_user, Gravity.CENTER);
					builder.setTitle(R.string.user_cancellation);
					builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 设置你的操作事项

						}
					});

					builder.setNegativeButton(R.string.determine,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									preferenceService.put("isAutoLogin", false);
									// 自动登录变为false
									// preferenceService.put("logined", false);
									UserVariable.LOGINED = false;
									startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
									setResult(10);
									UserCenterActivity.this.finish();

								}
							});

					builder.create().show();
				}
				break;
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(UserCenterActivity.this, MainGroupActivity.class));
				finish();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 处理滑动按钮滑动事件
	 */
	private class Changed implements OnChangedListener {

		@Override
		public void OnChanged(Slipbutton slipbtn, boolean checkstate) {
			switch (slipbtn.getId()) {
			case R.id.order_tv1:
				if (checkstate) {
					// 判断是否是一个 显示都显示
					actdeatUser.qryActDeactUser(UserVariable.CALLNUMBER, UserVariable.USERPWD, "2", "2");
					id = 1;
					startPbarU();
				} else {
					userId = 1;
					if (Utils.CheckTextNull(UserVariable.STATUSCALLED)
							|| Utils.CheckTextNull(UserVariable.STATUSCALLING)) {
						if (!"1".equals(UserVariable.STATUSCALLED) && "1".equals(UserVariable.STATUSCALLING)) {

							CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
							builder.setMessage(R.string.do_you_confirm_to_deactive_caller_RBT_service, Gravity.CENTER);
							builder.setTitle(R.string.user_cancellation);
							builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									celler.setChecked(true);
									celler.invalidate();
								}
							});

							builder.setNegativeButton(R.string.determine,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
											// 设置你的操作事项
											actdeatUser.qryActDeactUser(UserVariable.CALLNUMBER, UserVariable.USERPWD,
													"3", "2");
											id = 5;
											startPbarU();
										}
									});

							builder.create().show();
						} else {
							actdeatUser.qryActDeactUser(UserVariable.CALLNUMBER, UserVariable.USERPWD, "3", "2");
							id = 2;
							mUserCenter = ACTIVATECALLER;
							startPbarU();
						}
					}
				}
				break;
			case R.id.order_tv2:
				if (checkstate) {
					actdeatUser.qryActDeactUser(UserVariable.CALLNUMBER, UserVariable.USERPWD, "2", "1");
					id = 3;
					startPbarU();
				} else {
					userId = 2;
					if (Utils.CheckTextNull(UserVariable.STATUSCALLED)
							|| Utils.CheckTextNull(UserVariable.STATUSCALLING)) {
						if ("1".equals(UserVariable.STATUSCALLED) && !"1".equals(UserVariable.STATUSCALLING)) {

							// 判断是否是一个 显示都显示
							CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
							builder.setMessage(R.string.do_you_confirm_to_deactive_called_RBT_service, Gravity.CENTER);
							builder.setTitle(R.string.user_cancellation);
							builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									celled.setChecked(true);
									celled.invalidate();
								}
							});

							builder.setNegativeButton(R.string.determine,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
											// 设置你的操作事项
											actdeatUser.qryActDeactUser(UserVariable.CALLNUMBER, UserVariable.USERPWD,
													"3", "1");
											id = 6;
											startPbarU();
										}
									});

							builder.create().show();
						} else {

							actdeatUser.qryActDeactUser(UserVariable.CALLNUMBER, UserVariable.USERPWD, "3", "1");
							id = 4;
							mUserCenter = ACTIVATECALLED;
							startPbarU();
						}
					}

				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 请求xml成功后的处理
	 */
	@SuppressLint("NewApi")
	protected void reqXmlSucessed(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_ACTDEACTUSER:
			ActdeactUserRsp en = (ActdeactUserRsp) msg.obj;
			isCheckActivate();

			if ("2".equals(UserVariable.STATUSCALLED) && "2".equals(UserVariable.STATUSCALLING)
					&& UserVariable.LOGINED) {

			} else {
				mUserSubSer();
			}
			break;

		// 注销主叫
		case FusionCode.REQUEST_UNSUBSCRIBE:
			check_rbt.setBackground(getResources().getDrawable(R.drawable.choose_icon1));
			// showTextToast(mContext, "注销主叫");
			UserVariable.STATUSCALLING = "2";
			// if (ISLOGOFF) {
			// ISLOGOFF = false;
			// preferenceService.put("isAutoLogin", false);
			// // 自动登录变为false
			// UserVariable.LOGINED = false;
			// stopPbarU();
			// if (isShowing()) {
			// handler.postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// startActivity(new Intent(UserCenterActivity.this,
			// LoginActivity.class));
			// setResult(10);
			// UserCenterActivity.this.finish();
			// }
			// }, Commons.HIDETIME);
			// } else {
			// startActivity(new Intent(UserCenterActivity.this,
			// LoginActivity.class));
			// setResult(10);
			// UserCenterActivity.this.finish();
			// }
			//
			// }
			break;
		// 注册主叫
		case FusionCode.REQUEST_SUBSCRIBEAPPEVT:

			check_rbt.setBackground(getResources().getDrawable(R.drawable.choose_icon2));
			// showTextToast(mContext, "注册主叫");
			UserVariable.STATUSCALLING = "1";
			break;

		// 查询用户业务
		case FusionCode.REQUEST_USERSUBSEREVT:
			// 发起登录请求，根据返回结果处理
			UserSubSerRsp userSubSerRsp = (UserSubSerRsp) msg.obj;

			// showTextToast(mContext, "查询用户业务");
			UserVariable.STATUSCALLED = userSubSerRsp.getStatusCalled();
			UserVariable.STATUSCALLING = userSubSerRsp.getStatusCalling();
			if (UserVariable.STATUSCALLING.equals("1")) {
				check_rbt.setBackground(getResources().getDrawable(R.drawable.choose_icon2));
			} else {
				check_rbt.setBackground(getResources().getDrawable(R.drawable.choose_icon1));
			}

			UtilLog.e(userSubSerRsp.getStatusCalled() + " called:cller " + userSubSerRsp.getStatusCalling());
			handlers.sendEmptyMessage(100);
			if (!"1".equals(userSubSerRsp.getStatusCalling()) && !"1".equals(userSubSerRsp.getStatusCalled())
					&& UserVariable.LOGINED) {
				preferenceService.put("isAutoLogin", false);
				// 自动登录变为false
				UserVariable.LOGINED = false;
				stopPbarU();

				if (isShowing()) {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
							setResult(10);
							UserCenterActivity.this.finish();
						}
					}, Commons.HIDETIME);
				} else {
					startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
					setResult(10);
					UserCenterActivity.this.finish();
				}
				return;
			}
			stopPbarU();
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
		case FusionCode.REQUEST_ACTDEACTUSER:
			ActdeactUserRsp en = (ActdeactUserRsp) msg.obj;
			UtilLog.e(en.description + "passwordChange");
			// 判断状态显示
			if (id == 1) {
				celler.setChecked(false);
				celler.invalidate();
				if ("600001".equals(en.result)) {
					celler.setChecked(true);
					celler.invalidate();
				}
			} else if (id == 2) {
				celler.setChecked(true);
				celler.invalidate();
			} else if (id == 3) {
				celled.setChecked(false);
				celled.invalidate();
				if ("600001".equals(en.result)) {
					celled.setChecked(true);
					celled.invalidate();
				}
			} else if (id == 4) {
				celled.setChecked(true);
				celled.invalidate();
			} else if (id == 5) {
				celler.setChecked(true);
				celler.invalidate();
			} else if (id == 6) {
				celled.setChecked(true);
				celled.invalidate();
			} else {

			}
			// stopPbarU();
			showTextToast(mContext, UtlisReturnCode.ReturnCode(en.getResult(), mContext));
			mUserSubSer();
			// startPbarU();

			break;

		case FusionCode.REQUEST_UNSUBSCRIBE:
			UnSubscribeRsp unsubscribe = (UnSubscribeRsp) msg.obj;
			UtilLog.e(unsubscribe.description + "unsubscribe");
			stopPbarU();
			showTextToast(mContext, UtlisReturnCode.ReturnCode(unsubscribe.getResult(), mContext));
			break;

		case FusionCode.REQUEST_USERSUBSEREVT:
			// 发起登录请求，根据返回结果处理
			final UserSubSerRsp userSubSerRsp = (UserSubSerRsp) msg.obj;

			UserVariable.STATUSCALLED = userSubSerRsp.getStatusCalled();
			UserVariable.STATUSCALLING = userSubSerRsp.getStatusCalling();
			// showTextToast(mContext, userSubSerRsp.getDescription());
			stopPbarU();
			if (isShowing()) {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if ("301005".equals(userSubSerRsp.result) && !ISCHECKUSER) {
							preferenceService.put("isAutoLogin", false);
							// 自动登录变为false
							// UserVariable.STATUSCALLING = "2";
							UserVariable.LOGINED = false;
							UserVariable.USERPWD = "";
							preferenceService.put("userPwd", "");
							startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
							setResult(10);
							UserCenterActivity.this.finish();
						}
					}
				}, Commons.HIDETIME);
			} else {
				if ("301005".equals(userSubSerRsp.result) && !ISCHECKUSER) {
					preferenceService.put("isAutoLogin", false);
					// 自动登录变为false
					// UserVariable.STATUSCALLING = "2";
					UserVariable.LOGINED = false;
					UserVariable.USERPWD = "";
					preferenceService.put("userPwd", "");
					startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
					setResult(10);
					UserCenterActivity.this.finish();
				}
			}

		default:
			break;
		}
	}

	@Override
	protected void reqError(Message msg) {
		// stopPbarU();
		super.reqError(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_ACTDEACTUSER:
			// 判断滑动状态显示
			if (id == 1) {
				celler.setChecked(false);
				celler.invalidate();
			} else if (id == 2) {
				celler.setChecked(true);
				celler.invalidate();
			} else if (id == 3) {
				celled.setChecked(false);
				celled.invalidate();
			} else if (id == 4) {
				celled.setChecked(true);
				celled.invalidate();
			} else if (id == 5) {
				celler.setChecked(true);
				celler.invalidate();
			} else if (id == 6) {
				celled.setChecked(true);
				celled.invalidate();
			} else {

			}
			stopPbarU();
			break;
		default:
			stopPbarU();
			break;
		}
	}

	private void mUserSubSer() {
		mUserSubSerReq.sendUserSubSerRequest(UserVariable.CALLNUMBER, UserVariable.USERPWD);
	}

	/**
	 * 判断滑动按钮是否是激活或去激活 id=1 : celler 是激活状态 id=2 : celler 是去激活状态 id=3 : celled
	 * 是激活状态 id=4 : celled 是去激活状态 id=5,6 : celled和celler 都是去激活状态
	 */
	private void isCheckActivate() {
		if (id == 1) {
			celler.setChecked(true);
			celler.invalidate();
		} else if (id == 2) {
			celler.setChecked(false);
			celler.invalidate();
		} else if (id == 3) {
			celled.setChecked(true);
			celled.invalidate();
		} else if (id == 4) {
			celled.setChecked(false);
			celled.invalidate();
		} else if (id == 5) {
			preferenceService.put("isAutoLogin", false);
			// 自动登录变为false
			// UserVariable.STATUSCALLING = "2";
			UserVariable.LOGINED = false;
			startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
			setResult(10);
			UserCenterActivity.this.finish();
		} else if (id == 6) {
			preferenceService.put("isAutoLogin", false);
			// 自动登录变为false
			UserVariable.LOGINED = false;
			// UserVariable.STATUSCALLED = "2";
			startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
			setResult(10);
			UserCenterActivity.this.finish();
		} else {
			if (userId == 1) {
				celler.setChecked(false);
				celler.invalidate();
			} else if (userId == 2) {
				celled.setChecked(false);
				celled.invalidate();
			}
		}
	}

	/**
	 * 刷新界面
	 */
	private Handler handlers = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				isUserCenterActDeact(UserVariable.STATUSCALLING, UserVariable.STATUSCALLED);
				// invalidate();//刷新界面
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

}
