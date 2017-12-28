package com.dxiang.mring3.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gem.imgcash.ImageDownLoader;
import com.dxiang.mring3.R;
import com.dxiang.mring3.adapter.CustomDialogUpdate;
import com.dxiang.mring3.adapter.OrderDialog;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.ContentBuyTone;
import com.dxiang.mring3.request.ContentBuyToneForApp;
import com.dxiang.mring3.request.GetPwdReq;
import com.dxiang.mring3.request.GiftToneRequest;
import com.dxiang.mring3.request.QryValidaReq;
import com.dxiang.mring3.request.SetToneReq;
import com.dxiang.mring3.request.UserSubSerReq;
import com.dxiang.mring3.request.VerCodeReq;
import com.dxiang.mring3.response.ContentBuyToneForAppRsp;
import com.dxiang.mring3.response.ContentBuyToneRsp;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.response.GetPwdRsp;
import com.dxiang.mring3.response.GiftToneRsp;
import com.dxiang.mring3.response.QryValidaResp;
import com.dxiang.mring3.response.UserSubSerRsp;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.TimeCount;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

/**
 * 订购界面
 * 
 * @author Administrator
 *
 */
public class OrderActivity extends BaseActivity {
	private TextView mSong, mSinger, mPhonenum, mPrice, mBuy, mTitle;
	private TextView order_Reveive_tv, order_Reveive_number;
	private TextView mLogin, mGetVerCode, mHelp;
	// private EditText mEtPhone,mEtVerCode;
	private ImageView mBack;
	// private RelativeLayout mRl;
	// private LinearLayout mLl;
	private boolean isLogin = false;// 是否已经登录
	private SharedPreferenceService preferenceService;
	private ToneInfo info;
	private boolean caller, calledd;
	private String pwd = null;
	private TimeCount time;// 倒计时
	private boolean isPressed = false;// 验证码是否获取成功
	private LinearLayout Receive_tv_layout;
	private String from;// 从哪里跳转过来的
	private int isOwner = -1;// 判断是否拥有
	private ImageView liear_title;
	private LinearLayout linear_ringtonebg;
	String saveDir;
	String strFailValida;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_);
		setContentView(R.layout.order_activity_layout);
		initDataACT();
		initViewACT();
		hintTextSizeACT();
		setClickListenerACT();
	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//
//	}

	/**
	 * 设置hint字体大小
	 */
	private void hintTextSizeACT() {
		String language = preferenceService.get("language", "");
		if (Utils.CheckTextNull(language)) {
			// Utils.hintSize(mEtVerCode,14);
		}
	}

	/**
	 * 返回成功处理方法
	 */
	@Override
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		// case FusionCode.REQUEST_GETPWDEVT:
		// //获取密码成功后发送是否为注册用户接口
		// GetPwdRsp rsp=(GetPwdRsp) msg.obj;
		// pwd=rsp.getPwd().toString().trim();
		// UserSubSerReq userSubSerReq=new UserSubSerReq(handler);
		// userSubSerReq.sendUserSubSerRequest(mEtPhone.getText().toString().trim(),
		// pwd);
		//
		// break;
		case FusionCode.REQUEST_USERSUBSEREVT:
			UserSubSerRsp userSubSerRsp = (UserSubSerRsp) msg.obj;
			String called = userSubSerRsp.getStatusCalled();
			String calling = userSubSerRsp.getStatusCalling();
			if ("1".equals(called) || "1".equals(calling)) {
				// 已经注册
				ContentBuyTone buyTone = new ContentBuyTone(handler);
				buyTone.getContentBuyTone(UserVariable.CALLNUMBER, info.getToneType(), info.getToneID());
			} else {
				// //未注册
				// ContentBuyToneForApp buyToneForApp=new
				// ContentBuyToneForApp(handler);
				// buyToneForApp.getContentBuyToneForApp(mEtPhone.getText().toString().trim(),
				// info.getToneType(), info.getToneID(),
				// mEtVerCode.getText().toString().trim());
			}
			break;
		case FusionCode.REQUEST_CONTENTBUYTONE:
			ContentBuyToneRsp contentBuyToneRsp = (ContentBuyToneRsp) msg.obj;
			stopPbarU();
			// 普通订购成功处理,登录状态，发送被叫/主叫请求，提示框
			if (!isLogin) {
				resetLogin();
			}
			//
			// if (caller) {
			//
			// sendCaller();
			// }
			// if (calledd) {
			// sendCalled();
			// }

			if (contentBuyToneRsp.getError_code() == 0) {
				// finish();
				initLoginBuyDialog(String.format(getResources().getString(R.string.order_no_pending),
						info.getToneName(), info.getSingerName(), Utils.doCount(info.getPrice().toString().trim())));
			}
			break;
		case FusionCode.REQUEST_GIFTTONE:
			GiftToneRsp GiftToneRsp = (GiftToneRsp) msg.obj;
			stopPbarU();
			if (GiftToneRsp.getError_code() == 0) {
				// finish();
				initLoginBuyDialog(String.format(getResources().getString(R.string.order_no_pending),
						info.getToneName(), info.getSingerName(), Utils.doCount(info.getPrice().toString().trim())));
			}
			break;

		case FusionCode.REQUEST_CONTENTBUYTONEFORAPP:
			ContentBuyToneForAppRsp contentButToneForAppRsp = (ContentBuyToneForAppRsp) msg.obj;
			stopPbarU();
			// 未注册订购成功处理
			// UserVariable.STATUSCALLED="1";
			initNoLoginBuyDialog();

			break;
		// case FusionCode.REQUEST_VERCODEEVT:
		// stopPbarU();
		// time.start();
		// isPressed = true;
		// // mEtVerCode.setEnabled(true);
		// break;
		case FusionCode.REQUEST_VALIDA:
			QryValidaResp qvdQryValidaReq = (QryValidaResp) msg.obj;
			isOwner = qvdQryValidaReq.getError_code();
			break;
		default:
			break;
		}
	}

	/**
	 * 返回错误码处理方法
	 */
	@Override
	protected void reqXmlFail(Message msg) {
		super.reqXmlFail(msg);
		switch (msg.arg1) {
		// case FusionCode.REQUEST_GETPWDEVT:
		// GetPwdRsp rsp=(GetPwdRsp) msg.obj;
		// //获取密码失败，为未注册用户
		// if("301001".equals(rsp.getResult()))
		// {
		// //未注册
		// ContentBuyToneForApp buyToneForApp=new ContentBuyToneForApp(handler);
		// buyToneForApp.getContentBuyToneForApp(mEtPhone.getText().toString().trim(),
		// info.getToneType(), info.getToneID(),
		// mEtVerCode.getText().toString().trim());
		// }
		// else
		// {
		// stopPbarU();
		// Utils.showTextToast(getApplicationContext(),
		// UtlisReturnCode.ReturnCode(rsp.getResult(), mContext));
		// }
		//
		// break;
		case FusionCode.REQUEST_USERSUBSEREVT:
			UserSubSerRsp userSubSerRsp = (UserSubSerRsp) msg.obj;
			stopPbarU();
			Utils.showTextToast(getApplicationContext(),
					UtlisReturnCode.ReturnCode(userSubSerRsp.getResult(), mContext));
			break;
		case FusionCode.REQUEST_CONTENTBUYTONE:

			ContentBuyToneRsp contentBuyToneRsp = (ContentBuyToneRsp) msg.obj;
			stopPbarU();
			if (contentBuyToneRsp.getResult().equals("400031")) {
				// 重复订购

				if (caller) {
					sendCaller();
				}
				if (calledd) {
					sendCalled();
				}

				// initLoginBuyDialog();
				Utils.showTextToast(getApplicationContext(),
						UtlisReturnCode.ReturnCode(contentBuyToneRsp.getResult(), mContext));
				if (!isLogin) {
					resetLogin();
					finishActivity();
				}

			} else {
				Utils.showTextToast(getApplicationContext(), getResources().getString(R.string.error_order_pending));
			}
			break;

		case FusionCode.REQUEST_GIFTTONE:
			GiftToneRsp GiftToneRsp = (GiftToneRsp) msg.obj;
			stopPbarU();
			if (GiftToneRsp.getResult().equals("400031")) {
				// 重复订购

				// if (caller) {
				// sendCaller();
				// }
				// if (calledd) {
				// sendCalled();
				// }

				// initLoginBuyDialog();
				Utils.showTextToast(getApplicationContext(),
						UtlisReturnCode.ReturnCode(GiftToneRsp.getResult(), mContext));
				// if (!isLogin) {
				// resetLogin();
				// finishActivity();
				// }

			} else {
				Utils.showTextToast(getApplicationContext(),
						UtlisReturnCode.ReturnCode(GiftToneRsp.getResult(), mContext));
			}

			break;
		case FusionCode.REQUEST_CONTENTBUYTONEFORAPP:

			ContentBuyToneForAppRsp contentButToneForAppRsp = (ContentBuyToneForAppRsp) msg.obj;
			stopPbarU();
			if (contentButToneForAppRsp.getResult().equals("400031")) {
				// 重复订购
				// UserVariable.STATUSCALLED="1";
				initNoLoginBuyDialog();
			} else {
				Utils.showTextToast(getApplicationContext(),
						UtlisReturnCode.ReturnCode(contentButToneForAppRsp.getResult(), mContext));
			}
			break;
		case FusionCode.REQUEST_VERCODEEVT:
			EntryP ep = (EntryP) msg.obj;
			stopPbarU();
			// mEtVerCode.setEnabled(false);
			Utils.showTextToast(getApplicationContext(), UtlisReturnCode.ReturnCode(ep.getResult(), mContext));

			break;

		case FusionCode.REQUEST_VALIDA:
			QryValidaResp qvdQryValidaReq = (QryValidaResp) msg.obj;
			isOwner = qvdQryValidaReq.getError_code();
			strFailValida = getResources().getString(R.string.error_order_pending);
			break;
		}
	}

	/**
	 * 返回失败处理方法
	 */
	@Override
	protected void reqError(Message msg) {
		super.reqError(msg);
		stopPbarU();
		switch (msg.arg1) {

		case FusionCode.REQUEST_GETPWDEVT:

			break;
		case FusionCode.REQUEST_USERSUBSEREVT:

			break;
		case FusionCode.REQUEST_CONTENTBUYTONE:
			Utils.showTextToast(getApplicationContext(), getResources().getString(R.string.error_order_pending));
			break;
		case FusionCode.REQUEST_CONTENTBUYTONEFORAPP:
			break;
		case FusionCode.REQUEST_VERCODEEVT:
			// mEtVerCode.setEnabled(false);
			break;
		case FusionCode.REQUEST_GIFTTONE:
			ContentBuyToneForAppRsp contentButToneForAppRsp = (ContentBuyToneForAppRsp) msg.obj;
			stopPbarU();

			Utils.showTextToast(getApplicationContext(), getResources().getString(R.string.error_order_pending));

			break;

		case FusionCode.REQUEST_VALIDA:
			QryValidaResp qvdQryValidaReq = (QryValidaResp) msg.obj;
			isOwner = qvdQryValidaReq.getError_code();
			strFailValida = getResources().getString(R.string.error_order_pending);
			break;
		default:
			break;
		}
	}

	// ***********************************************自定义方法*************************************
	/**
	 * 初始化数据
	 */
	private void initDataACT() {
		preferenceService = SharedPreferenceService.getInstance(this);
		// isLogin=preferenceService.get("logined", false);
		isLogin = UserVariable.LOGINED;
		if (null != getIntent().getExtras()) {
			info = (ToneInfo) getIntent().getExtras().get("bean");
			caller = getIntent().getExtras().getBoolean("caller");
			calledd = getIntent().getExtras().getBoolean("called");
			if (info != null && !info.getToneClassID().equals("0"))
				saveDir = Commons.getImageSavedpath() + info.getToneClassID();
		}
		QryValidaReq qvr = new QryValidaReq(handler);
		qvr.sendQryValidaReqReq(UserVariable.CALLNUMBER);
	}

	/**
	 * 初始化view
	 */
	@SuppressLint("NewApi")
	private void initViewACT() {

		mBack = (ImageView) findViewById(R.id.title_iv);
		mBack.setVisibility(View.VISIBLE);
		
		mTitle = (TextView) findViewById(R.id.title_tv);
		mTitle.setText(getIntent().getStringExtra("title"));
		mSong = (TextView) findViewById(R.id.order_song);

		mSong.setText(info.getToneName());
		mSinger = (TextView) findViewById(R.id.order_singer);
		mSinger.setText(info.getSingerName());
		
		mBuy = (TextView) findViewById(R.id.order_buy_tv);
		mBuy.setText(getIntent().getStringExtra("title"));
		
		mPhonenum = (TextView) findViewById(R.id.order_tv_phonenum);
		mPrice = (TextView) findViewById(R.id.order_tv_price);
		Receive_tv_layout = (LinearLayout) findViewById(R.id.Receive_tv_layout);
		order_Reveive_tv = (TextView) findViewById(R.id.order_Reveive_tv);
		order_Reveive_number = (TextView) findViewById(R.id.order_Reveive_number);
		liear_title = (ImageView) findViewById(R.id.liear_title);
		linear_ringtonebg = (LinearLayout) findViewById(R.id.linear_ringtonebg);
		liear_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(OrderActivity.this, MainGroupActivity.class));
				finish();
			}
		});
		// mLogin=(TextView) findViewById(R.id.order_login);
		// mLogin.setVisibility(View.VISIBLE);
		// mHelp=(TextView) findViewById(R.id.order_help);
		// mEtPhone=(EditText) findViewById(R.id.order_et_phonenum);

		// mEtVerCode=(EditText) findViewById(R.id.order_et_vercode);
		//
		// mEtVerCode.setEnabled(false);
		// mGetVerCode=(TextView) findViewById(R.id.order_tv_vercode);

		// mRl=(RelativeLayout) findViewById(R.id.order_relativelayout_login);
		// mLl=(LinearLayout) findViewById(R.id.order_linearlayout_nologin);
		// if(isLogin)
		// {
		mPhonenum.setText(UserVariable.CALLNUMBER);// 号码赋值
		// 价格赋值
		OrdersetText(mPrice, Utils.doCount(info.getPrice().toString().trim()));

		from = (String) getIntent().getExtras().get("from");
		if (from.equals("OrderFragment")) {
			Receive_tv_layout.setVisibility(View.GONE);
		} else {
			Receive_tv_layout.setVisibility(View.VISIBLE);
			String receive_number = (String) getIntent().getExtras().get("receive_number");
			order_Reveive_number.setText(receive_number);

		}
		// mRl.setVisibility(View.VISIBLE);
		// mLl.setVisibility(View.INVISIBLE);
		// mLogin.setVisibility(View.INVISIBLE);
		// mHelp.setVisibility(View.INVISIBLE);
		// }
		// else
		// {
		// mRl.setVisibility(View.INVISIBLE);
		// mLl.setVisibility(View.VISIBLE);
		// mLogin.setVisibility(View.VISIBLE);
		// mHelp.setVisibility(View.VISIBLE);
		// }
		// 初始化计时器
		// if (!(time == null)) {
		// time.onFinish();
		// time.cancel();
		// }
		if (info.getToneClassID() != null && !info.getToneClassID().equals("0")) {
			if (new File(saveDir).exists()) {
				Bitmap bm = ImageDownLoader.getShareImageDownLoader().showCacheBitmap(saveDir);
				Drawable db = new BitmapDrawable(bm);
				linear_ringtonebg.setBackground(db);
			} else {
				linear_ringtonebg.setBackgroundResource(R.drawable.detail_ringtone);
			}
		}
	}

	private void OrdersetText(TextView view, String text) {
		int length = text.length();
		if (length == 0) {
			return;
		}
		SpannableStringBuilder builder = new SpannableStringBuilder(text);

		ForegroundColorSpan yellow = new ForegroundColorSpan(getResources().getColor(R.color.tv_tail));
		String language = preferenceService.get("language", "");

		builder.setSpan(yellow, 0, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		view.setText(builder);
	}

	/**
	 * 初始化点击事件
	 */
	private void setClickListenerACT() {
		ClickACT click = new ClickACT();
		mBuy.setOnClickListener(click);
		mBack.setOnClickListener(click);
		// liear_title.setOnClickListener(click);
		if (!isLogin) {
			// mLogin.setOnClickListener(click);
			// mGetVerCode.setOnClickListener(click);

		}

	}

	/**
	 * 重置登录状态
	 */
	private void resetLogin() {
		UserVariable.CALLNUMBER = mPhonenum.getText().toString().trim();
		UserVariable.USERPWD = pwd;
		UserVariable.LOGINED = true;
		// preferenceService.put("userName",
		// mEtPhone.getText().toString().trim());
		// preferenceService.put("userPwd", pwd);
		// preferenceService.put("logined", true);
	}

	/**
	 * 销毁该界面
	 */
	private void finishActivity() {
		if (isShowing()) {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					OrderActivity.this.finish();
				}
			}, Commons.HIDETIME);
		} else {
			OrderActivity.this.finish();
		}
	}

	/**
	 * 发送主叫
	 */
	private void sendCaller() {
		// 发送主叫接口
		String toneID = info.getToneID();
		// String toneType = info.getToneType();
		String operType = "1";
		String settingID = "0000000000";
		String serviceType = "2";
		new SetToneReq(handler).sendSetToneReq(operType, serviceType, settingID, toneID, "0", "0");
	}

	/**
	 * 发送被叫
	 */
	private void sendCalled() {
		// 发送被叫接口
		String toneID = info.getToneID();
		// String toneType = info.getToneType();
		String operType = "1";
		String settingID = "0000000000";
		String serviceType = "1";
		new SetToneReq(handler).sendSetToneReq(operType, serviceType, settingID, toneID, "0", "0");

	}

	/**
	 * 未登录订购成功弹出对话框
	 */
	private void initNoLoginBuyDialog() {
		CustomDialogUpdate.Builder dialog = new CustomDialogUpdate.Builder(OrderActivity.this);
		dialog.setMessage(getResources().getString(R.string.regist_success_txt));
		dialog.setPositiveButton(getResources().getString(R.string.determine), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				OrderActivity.this.finish();
			}
		});
		dialog.setTitle(getResources().getString(R.string.user_cancellation));
		dialog.create().show();
	}

	//
	// /**
	// * 登录订购成功弹出对话框
	// */
	private void initLoginBuy2Dialog(int id) {
		OrderDialog.Builder dialog = new OrderDialog.Builder(OrderActivity.this);
		dialog.setMessage(getResources().getString(id));
		dialog.setPositiveButton(getResources().getString(R.string.determine), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});

		dialog.setTitle(getResources().getString(R.string.user_cancellation));
		dialog.create().show();

	}

	private void initLoginBuyDialog(String strMsg) {
		OrderDialog.Builder dialog = new OrderDialog.Builder(OrderActivity.this);
		dialog.setMessage(strMsg);
		dialog.setPositiveButton(getResources().getString(R.string.determine), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.dismiss();
				finish();
			}
		});

		dialog.setTitle(getResources().getString(R.string.user_cancellation));
		dialog.create().show();

	}

	/**
	 * 点击事件
	 * 
	 * @author Administrator
	 *
	 */
	public class ClickACT implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 点击时间过快，不操作
			if (Utils.isFastDoubleClick()) {
				return;
			}
			switch (v.getId()) {
			case R.id.order_buy_tv:
				// 已经登录处理
				// if (isLogin) {
				// 发送普通订购请求
				if (isOwner == 0) {
					if (from.equals("OrderFragment")) {
						startPbarU();
						ContentBuyTone buy = new ContentBuyTone(handler);
						buy.getContentBuyTone(mPhonenum.getText().toString().trim(), "1", info.getToneID());
					} else {
						String str_mPhonenum = mPhonenum.getText().toString().trim();
						String str_order_Reveive_number = order_Reveive_number.getText().toString().trim();
						if (str_mPhonenum.equals(str_order_Reveive_number)) {
							Utils.showTextToast(getApplicationContext(),
									getResources().getString(R.string.error_order_pending));
							return;
						}
						startPbarU();
						GiftToneRequest gift = new GiftToneRequest(handler);
						gift.setGiftToneRequest(mPhonenum.getText().toString().trim(), "1", info.getToneID(),
								order_Reveive_number.getText().toString().trim());
					}
				} else {
					initLoginBuyDialog(getResources().getString(R.string.order_pending));
				}
				// }
				// else
				// {
				// //判断输入是否为空
				// if(!Utils.CheckTextNull(mEtPhone.getText().toString().trim()))
				// {
				// Utils.showTextToast(getApplicationContext(),
				// getResources().getString(R.string.phonenumber_check));
				// return;
				// }
				//
				// if(!Utils.CheckTextNull(mEtVerCode.getText().toString().trim()))
				// {
				// Utils.showTextToast(getApplicationContext(),
				// getResources().getString(R.string.vercode_check));
				// return;
				// }

				// 必须点击过后才能输入验证码
				// if(!isPressed)
				// {
				// Utils.showTextToast(getApplicationContext(), "Please verify
				// code");
				// return;
				// }
				// isPressed=false;
				// 发送验证用户是否为注册用户请求，首先获取密码
				// startPbarU();
				// GetPwdReq getPwdReq=new GetPwdReq(handler);
				// getPwdReq.sendGetPwdRequest(mEtPhone.getText().toString().trim());

				// }

				break;
			case R.id.title_iv:
				OrderActivity.this.finish();
				break;
			// case R.id.order_login:
			// Intent intent=new Intent(OrderActivity.this,LoginActivity.class);
			// intent.putExtra("From", "OrderActivity");
			// startActivity(intent);
			// break;
			// case R.id.order_tv_vercode:
			// //获取验证码
			// if(!Utils.CheckTextNull(mEtPhone.getText().toString().trim()))
			// {
			// Utils.showTextToast(getApplicationContext(),
			// getResources().getString(R.string.phonenumber_check));
			// return;
			// }
			// startPbarU();
			// VerCodeReq res=new VerCodeReq(handler);
			// res.sendVerCodeReq(mEtPhone.getText().toString().trim(), "2");
			// time=new TimeCount(120000, 1000, mGetVerCode,mEtVerCode);
			// break;
			default:
				break;
			}
		}

	}

//	@Override
//	public void onStart() {
//		super.onStart();
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//	}
}
