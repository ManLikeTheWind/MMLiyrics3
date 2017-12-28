package com.dxiang.mring3.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gem.imgcash.ImageDownLoader;
import com.dxiang.mring3.R;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.fragment.DetailsRingtoneActivityGiveringTonesFragment;
import com.dxiang.mring3.fragment.DetailsRingtoneActivityOrderFragment;
import com.dxiang.mring3.request.QryToneById;
import com.dxiang.mring3.request.QryToneTypeAPPRequest;
import com.dxiang.mring3.response.GetToneListenAddrRsp;
import com.dxiang.mring3.response.QryToneByIdRsp;
import com.dxiang.mring3.response.QryToneTypeAPPResp;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.LocalMediaPlayer;
import com.dxiang.mring3.utils.LocalMediaPlayer.Complete;
import com.dxiang.mring3.utils.LocalMediaPlayer.ErrorListener;
import com.dxiang.mring3.utils.LocalMediaPlayer.onException;
import com.dxiang.mring3.utils.LocalMediaPlayer.onPrepared;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

/**
 * 铃音详细信息界面
 * 
 * @author Administrator
 * 
 */
@SuppressLint("NewApi")
public class DetailsRingtoneActivity extends BaseActivity {
	private TextView tv_title, tv_song, tv_singer;
	// ,tv_download,tv_price
	private ImageView mBack, mPlay_Stop;
	private ProgressBar mLoading;
	// private CheckBox mCaller,mCalled;

	// private TextView text_caller_default,text_called_default;

	// private LinearLayout ll_caller_default,ll_called_default;

	// private Animation animation;// 旋转动画

	// 铃音列表播放器
	private LocalMediaPlayer mediaPlayer;// 播放器
	private boolean isPlayer = false;// 判断是否正在播放
	private ToneInfo info;
	private String id;
	private boolean flag = false;
	// private boolean isLogin = false;// 是否已经登录
	// private SharedPreferenceService preferenceService;
	private TextView left_tv, right_tv;
	private ImageView left_bt, right_bt;
	// private LinearLayout left_layout,right_layout;
	private TextView left_line, right_line;
	private DetailsRingtoneActivityOrderFragment Fragment_bottom_one;
	private DetailsRingtoneActivityGiveringTonesFragment Fragment_bottom_giveRingTone;
	private LinearLayout linear_ringtonebg;
	FragmentTransaction transtion;
	private ImageView liear_title;
	private int selectFraggment = 1;
	private static int ORDER = 1;
	private static int GIFT = 2;
	private String giftNumber = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.details_activity_layout);
		if (arg0 != null) {

			selectFraggment = arg0.getInt("selectFraggment", ORDER);
			giftNumber = arg0.getString("giftNumber", "");
		}
		initView();
		initData();
		setClickListener();
	}

	public void setNumber(String number) {
		giftNumber = number;
	};

	public String getNumber() {
		if (giftNumber != null) {
			return giftNumber;
		} else {
			return "";
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		// isLogin = UserVariable.LOGINED;
		// initData();
		//

		// textSize();
	}

	/**
	 * 设置TextView字体大小跑马灯
	 */
	// private void textSize(){
	// String language = preferenceService.get("language", "");
	// if(Utils.CheckTextNull(language)){
	// // LinearLayout.LayoutParams layoutParams=new
	// LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
	// // layoutParams.setMargins(40, 0, 40, 0);
	// // ll_caller_default.setLayoutParams(layoutParams);
	// text_caller_default.setVisibility(View.VISIBLE);
	// text_called_default.setVisibility(View.VISIBLE);
	// mCaller.setText("");
	// mCalled.setText("");
	//
	// }
	// }

	@Override
	protected void onPause() {
		super.onPause();

		if (mediaPlayer.isPlaying() || mediaPlayer.getState() == LocalMediaPlayer.PAUSE) {
			pause();
		} else {
			stopMusic();
		}
	}

	/**
	 * 试听暂停方法
	 */
	private void pause() {
		mPlay_Stop.setBackgroundResource(R.mipmap.music_play);
		mediaPlayer.pause();
		isPlayer = false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		stopMusic();
		// if(Fragment_bottom_giveRingTone.isHidden())
	}

	/**
	 * 返回成功处理
	 */
	@SuppressLint("NewApi")
	@Override
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QRYTONEBYID:
			QryToneByIdRsp entry = (QryToneByIdRsp) msg.obj;
			if (entry.list.size() == 0) {
				stopPbarU();
				Utils.showTextToast(DetailsRingtoneActivity.this, getResources().getString(R.string.searcb_no_datas));
				if (isShowing()) {
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							DetailsRingtoneActivity.this.finish();
						}
					}, Commons.HIDETIME);
				} else {
					DetailsRingtoneActivity.this.finish();
				}

			} else {

				info = entry.list.get(0);
				initRspData();

				if (Fragment_bottom_one == null) {
					Fragment_bottom_one = new DetailsRingtoneActivityOrderFragment();
					Fragment_bottom_one.setData(flag, info);
					transtion.replace(R.id.details_FragmeLayout, Fragment_bottom_one).commit();
				} else {
					if (Fragment_bottom_giveRingTone != null) {
						transtion.hide(Fragment_bottom_giveRingTone);
						transtion.show(Fragment_bottom_one);
						transtion.commit();
					} else {
						transtion.replace(R.id.details_FragmeLayout, Fragment_bottom_one).commit();
					}
				}
				stopPbarU();

			}
			if (info != null) {
				String saveDir = Commons.getImageSavedpath() + info.getToneClassID();
				if (new File(saveDir).exists()) {
					Bitmap bm = ImageDownLoader.getShareImageDownLoader().showCacheBitmap(saveDir);
					Drawable db = new BitmapDrawable(bm);
					linear_ringtonebg.setBackground(db);
				} else {
					linear_ringtonebg.setBackgroundResource(R.mipmap.detail_ringtone);
				}
			}
			break;
		case FusionCode.REQUEST_GETTONELISTENADDREVT:
			GetToneListenAddrRsp rsp = (GetToneListenAddrRsp) msg.obj;
			// String url = "http://221.226.179.185/music_mmh/drive.wav";
			String url = rsp.getToneAddr();
			if (!Utils.CheckTextNull(url)) {
				Utils.showTextToast(DetailsRingtoneActivity.this, getResources().getString(R.string.empty_play_url));
				return;
			}

			mediaPlayer.setDataSource(url);
			mediaPlayer.prepare();

			// if (!isPlayer)
			// {
			// if (mediaPlayer.isprepared())
			// {
			// return;
			// }
			// // 播放
			// mPlay_Stop.setBackground(null);
			// if(mLoading.getAnimation()==null)
			// {
			// mLoading.setAnimation(animation);// 设置动画
			// animation.startNow();
			// }
			// mLoading.setVisibility(View.VISIBLE);
			// mediaPlayer.setDataSource(url);
			// mediaPlayer.prepare();
			//
			// }
			// else
			// {
			// isPlayer = false;
			// mediaPlayer.pause();
			// mPlay_Stop.setBackgroundResource(R.drawable.music_play);
			// }
			break;

		default:
			break;
		}
	}

	/**
	 * 返回错误码处理
	 */
	@Override
	protected void reqXmlFail(Message msg) {
		super.reqXmlFail(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QRYTONEBYID:
			QryToneByIdRsp entry = (QryToneByIdRsp) msg.obj;
			stopPbarU();
			Utils.showTextToast(DetailsRingtoneActivity.this, UtlisReturnCode.ReturnCode(entry.getResult(), mContext));
			if (isShowing()) {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						DetailsRingtoneActivity.this.finish();
					}
				}, Commons.HIDETIME);
			} else {
				DetailsRingtoneActivity.this.finish();
			}
			break;
		case FusionCode.REQUEST_GETTONELISTENADDREVT:
			GetToneListenAddrRsp rsp = (GetToneListenAddrRsp) msg.obj;
			stopMusic();
			Utils.showTextToast(getApplicationContext(), UtlisReturnCode.ReturnCode(rsp.getResult(), mContext));
			// String url =
			// "http://10.47.170.121:8080/colorring/wav/sys/173.wav";
			// mediaPlayer.setDataSource(url);
			// mediaPlayer.prepare();
			// System.out.println("-------111");

			break;

		default:
			break;
		}
	}

	/**
	 * 返回失败处理
	 */
	@Override
	protected void reqError(Message msg) {
		super.reqError(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QRYTONEBYID:
			QryToneByIdRsp entry = (QryToneByIdRsp) msg.obj;
			stopPbarU();

			Utils.showTextToast(DetailsRingtoneActivity.this, UtlisReturnCode.ReturnCode(entry.getResult(), mContext));
			if (isShowing()) {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						DetailsRingtoneActivity.this.finish();
					}
				}, Commons.HIDETIME);
			} else {
				DetailsRingtoneActivity.this.finish();
			}
			break;
		case FusionCode.REQUEST_GETTONELISTENADDREVT:
			GetToneListenAddrRsp rsp = (GetToneListenAddrRsp) msg.obj;
			stopMusic();
			Utils.showTextToast(getApplicationContext(), UtlisReturnCode.ReturnCode(rsp.getResult(), mContext));
			break;

		default:
			break;
		}
	}

	// *******************************************自定义方法*******************************************

	/**
	 * 初始化数据
	 */
	private void initData() {

		// preferenceService = SharedPreferenceService.getInstance(this);
		// isLogin=preferenceService.get("logined", false);

		if (null != getIntent().getExtras()) {
			if (null == getIntent().getExtras().get("bean")) {
				id = (String) getIntent().getExtras().get("code");
				// 通过id查询服务器
				startPbarU();
				QryToneById qry = new QryToneById(handler, FusionCode.REQUEST_QRYTONEBYID);
				qry.getQryToneById(id, "1", "1", 100, 0);

			} else {

				if (null != getIntent().getExtras().get("flag")) {
					// 从个人铃音库跳转过来 true
					flag = getIntent().getExtras().getBoolean("flag");
				}

				info = (ToneInfo) getIntent().getExtras().get("bean");
				if (null != info) {
					if ("0".equals(info.getToneClassID().toString())) {
						linear_ringtonebg.setBackgroundResource(R.mipmap.detail_ringtone);
					} else {
						String saveDir = Commons.getImageSavedpath() + info.getToneClassID();
						if (new File(saveDir).exists()) {
							Bitmap bm = ImageDownLoader.getShareImageDownLoader().showCacheBitmap(saveDir);
							Drawable db = new BitmapDrawable(bm);
							linear_ringtonebg.setBackground(db);
						} else {
							linear_ringtonebg.setBackgroundResource(R.mipmap.detail_ringtone);
						}
						initRspData();

					}
				} else {
					linear_ringtonebg.setBackgroundResource(R.mipmap.detail_ringtone);
				}

			}
		} else {

		}
		// animation = new RotateAnimation(0, +360, Animation.RELATIVE_TO_SELF,
		// 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		// // 动画开始到结束的执行时间(1000 = 1 秒)
		// animation.setDuration(800);
		// // 动画重复次数(-1 表示一直重复)
		// animation.setRepeatCount(-1);
		// animation.setRepeatMode(Animation.INFINITE);
		// animation.setInterpolator(new LinearInterpolator());//匀速动画
		initMediaPlayer();

	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("selectFraggment", selectFraggment);
		if (!TextUtils.isEmpty(giftNumber))
			outState.putString("giftNumber", giftNumber);
		super.onSaveInstanceState(outState);
	}

	/**
	 * 播放完成
	 */
	Complete onComplete = new Complete() {

		@Override
		public void onComplete() {
			isPlayer = false;
			mPlay_Stop.setBackgroundResource(R.mipmap.music_play);

		}

	};

	/**
	 * 播放准备
	 */
	onPrepared onPrepred = new onPrepared() {

		@Override
		public void onPrepared() {
			isPlayer = true;
			// mLoading.setAnimation(null);
			mLoading.setVisibility(View.GONE);
			mPlay_Stop.setBackgroundResource(R.mipmap.music_stop);

		}
	};

	/**
	 * 播放异常
	 */
	ErrorListener errorListener = new ErrorListener() {
		@Override
		public void onError(MediaPlayer arg0, int arg1, int arg2) {
			stopMusic();
		}
	};

	/**
	 * 初始化播放器
	 */
	private void initMediaPlayer() {
		mediaPlayer = LocalMediaPlayer.getInstance();
		mediaPlayer.setCallback(onComplete);
		mediaPlayer.setOnPrepared(onPrepred);
		mediaPlayer.setErrorListener(errorListener);
		mediaPlayer.setException(exception);
	}

	/**
	 * 试听停止方法
	 */
	private void stopMusic() {
		if (mediaPlayer == null) {
			return;
		}
		isPlayer = false;
		if (mediaPlayer.isPlaying() || mediaPlayer.isprepared() || mediaPlayer.isLooped()) {

			mediaPlayer.cancelPlayer();
			initMediaPlayer();
		}
		mPlay_Stop.setBackgroundResource(R.mipmap.music_play);
		// mLoading.setAnimation(null);
		mLoading.setVisibility(View.GONE);

	}

	/**
	 * 初始化view
	 */
	@SuppressLint("NewApi")
	private void initView() {
		mBack = (ImageView) findViewById(R.id.title_iv);
		mBack.setVisibility(View.VISIBLE);
		tv_title = (TextView) findViewById(R.id.title_tv);
		tv_title.setText(getResources().getString(R.string.me_pop_details));
		// tv_price = (TextView) findViewById(R.id.details_tv_price);
		// left_layout=(LinearLayout)
		// findViewById(R.id.details_bottom_left_layout);
		// right_layout=(LinearLayout)
		// findViewById(R.id.details_bottom_right_layout);
		left_bt = (ImageView) findViewById(R.id.details_bottom_left_button);
		left_tv = (TextView) findViewById(R.id.details_bottom_left_textview);
		right_bt = (ImageView) findViewById(R.id.details_bottom_right_button);
		right_tv = (TextView) findViewById(R.id.details_bottom_right_textview);
		left_line = (TextView) findViewById(R.id.details_bottom_left_line);
		right_line = (TextView) findViewById(R.id.details_bottom_right_line);

		tv_song = (TextView) findViewById(R.id.details_song);
		tv_singer = (TextView) findViewById(R.id.details_singer);
		mPlay_Stop = (ImageView) findViewById(R.id.details_play_stop_loading);
		mLoading = (ProgressBar) findViewById(R.id.details_loading);

		linear_ringtonebg = (LinearLayout) findViewById(R.id.tone_message_bg);
		liear_title = (ImageView) findViewById(R.id.liear_title);

		// AnimationDrawable animationDrawable = (AnimationDrawable) mLoading
		// .getBackground();
		// animationDrawable.start();
		// tv_download = (TextView) findViewById(R.id.details_download);
		// mCaller = (CheckBox) findViewById(R.id.caller_default);
		// mCalled = (CheckBox) findViewById(R.id.called_default);
		//
		// text_caller_default=(TextView)findViewById(R.id.text_caller_default);
		// text_called_default=(TextView)findViewById(R.id.text_called_default);
		// ll_caller_default=(LinearLayout)findViewById(R.id.ll_caller_default);
		// ll_called_default=(LinearLayout)findViewById(R.id.ll_called_default);
		// 未登录隐藏主叫/被叫铃音选项
		// if (!isLogin) {
		// ll_called_default.setVisibility(View.GONE);
		// ll_caller_default.setVisibility(View.GONE);
		// mCaller.setVisibility(View.GONE);
		// mCalled.setVisibility(View.GONE);
		// tv_download.setVisibility(View.VISIBLE);
		// } else {
		//
		// // 从个人铃音库跳转过来
		// if (flag) {
		// tv_download.setVisibility(View.GONE);
		// ll_called_default.setVisibility(View.GONE);
		// ll_caller_default.setVisibility(View.GONE);
		// mCaller.setVisibility(View.GONE);
		// mCalled.setVisibility(View.GONE);
		//
		// } else {
		// tv_download.setVisibility(View.VISIBLE);
		// if (isActive(UserVariable.STATUSCALLED)) {
		// ll_called_default.setVisibility(View.VISIBLE);
		// mCalled.setVisibility(View.VISIBLE);
		// } else {
		// ll_called_default.setVisibility(View.GONE);
		// mCalled.setVisibility(View.GONE);
		// }
		//
		// if (isActive(UserVariable.STATUSCALLING)) {
		// ll_caller_default.setVisibility(View.VISIBLE);
		// mCaller.setVisibility(View.VISIBLE);
		// } else {
		// ll_caller_default.setVisibility(View.GONE);
		// mCaller.setVisibility(View.GONE);
		// }
		// System.out.println("mcaller " + UserVariable.STATUSCALLING
		// + " mcalled" + UserVariable.STATUSCALLED);
		// // mCalled.setVisibility(View.VISIBLE);
		// // mCaller.setVisibility(View.VISIBLE);
		// // 当前登录和上次设置为同一个号码
		// if (UserVariable.CALLNUMBER.equals(preferenceService.get(
		// "number_checked", ""))) {
		// mCalled.setChecked(preferenceService.get("called", true));
		// mCaller.setChecked(preferenceService.get("caller", true));
		// } else {
		// mCalled.setChecked(true);
		// mCaller.setChecked(true);
		// }
		//
		// }
		// }

	}

	/**
	 * 初始化歌曲和歌手信息
	 */

	private void initRspData() {

		// setText(tv_price, "Charges:"+Utils.doCount(info.getPrice()));

		// setText(tv_price, Utils.getResouceStr(mContext, R.string.tv_price)
		// + Utils.doCount(info.getPrice()));
		tv_song.setText(info.getToneName());
		tv_singer.setText(info.getSingerName());
	}

	// /**
	// * 是否显示主叫被叫选项
	// * str 主叫、被叫的返回值
	// */
	// private boolean isActive(String str)
	// {
	// if(!Utils.CheckTextNull(str))
	// {
	//
	// return false;
	// }
	//
	// if (str.equals("1") || str.equals("2") || str.equals("3")) {
	// return true;
	// }
	//
	// return false;
	// }

	/**
	 * 监听器初始化
	 */
	private void setClickListener() {
		Click click = new Click();

		// tv_download.setOnClickListener(click);
		mPlay_Stop.setOnClickListener(click);
		mBack.setOnClickListener(click);
		left_tv.setOnClickListener(click);
		right_tv.setOnClickListener(click);
		liear_title.setOnClickListener(click);
		if (selectFraggment != GIFT) {
			left_tv.performClick();
		} else if (selectFraggment == GIFT) {
			right_tv.performClick();
		}
		// left_layout.setOnClickListener(click);
		// right_layout.setOnClickListener(click);
		// text_called_default.setOnClickListener(click);
		// text_caller_default.setOnClickListener(click);
		// mCalled.setOnCheckedChangeListener(mOnCheckedChangeListener);
		// mCaller.setOnCheckedChangeListener(mOnCheckedChangeListener);

	}

	/**
	 * checkbox的选择处理
	 */
	// OnCheckedChangeListener mOnCheckedChangeListener=new
	// OnCheckedChangeListener()
	// {
	// @Override
	// public void onCheckedChanged(CompoundButton buttonView,
	// boolean isChecked) {
	// switch (buttonView.getId()) {
	// case R.id.caller_default:
	// if (isChecked) {
	// preferenceService.put("number_checked",
	// UserVariable.CALLNUMBER);
	// preferenceService.put("caller", true);
	// } else {
	// preferenceService.put("number_checked",
	// UserVariable.CALLNUMBER);
	// preferenceService.put("caller", false);
	// }
	// break;
	// case R.id.called_default:
	// if (isChecked) {
	// preferenceService.put("number_checked",
	// UserVariable.CALLNUMBER);
	// preferenceService.put("called", true);
	// } else {
	// preferenceService.put("number_checked",
	// UserVariable.CALLNUMBER);
	// preferenceService.put("called", false);
	// }
	// break;
	//
	// default:
	// break;
	// }
	// }
	// };

	/**
	 * 设置字体
	 * 
	 * @param view
	 * @param text
	 */

	// private void setText(TextView view, String text)
	// {
	// int length = text.length();
	// if (length == 0)
	// {
	// return;
	// }
	// SpannableStringBuilder builder = new SpannableStringBuilder(text);
	// ForegroundColorSpan white = new
	// ForegroundColorSpan(getResources().getColor(R.color.tv_head));
	// ForegroundColorSpan yellow = new
	// ForegroundColorSpan(getResources().getColor(R.color.tv_tail));
	// String language = preferenceService.get("language", "");
	// if(Utils.CheckTextNull(language)){
	// builder.setSpan(white, 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	// builder.setSpan(yellow, 7, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	// }else{
	// builder.setSpan(white, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	// builder.setSpan(yellow, 6, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
	// }
	// view.setText(builder);
	// }

	/**
	 * 试听异常处理方法
	 */
	onException exception = new onException() {
		@Override
		public void onException(int code, String des) {
			if (Utils.CheckTextNull(des)) {
				Utils.showTextToast(DetailsRingtoneActivity.this, UtlisReturnCode.ReturnCode(des, mContext));
				System.out.println(des + "------------------" + code);
			}
			stopMusic();
			isPlayer = false;
		}

	};

	private void selectOrder() {
		selectFraggment = ORDER;
		left_tv.setTextColor(getApplication().getResources().getColor(R.color.details_bottom_select));
		left_bt.setVisibility(View.VISIBLE);
		left_line.setVisibility(View.GONE);
		right_tv.setTextColor(getApplication().getResources().getColor(R.color.details_bottom_black));
		right_bt.setVisibility(View.GONE);
		right_line.setVisibility(View.VISIBLE);
		if (null != info) {
			if (Fragment_bottom_one == null) {
				Fragment_bottom_one = new DetailsRingtoneActivityOrderFragment();
				Fragment_bottom_one.setData(flag, info);
				// transtion.replace(R.id.details_FragmeLayout,
				// Fragment_bottom_one).commit();

				if (Fragment_bottom_giveRingTone != null)
					transtion.hide(Fragment_bottom_giveRingTone);
				transtion.add(R.id.details_FragmeLayout, Fragment_bottom_one).commit();

			} else {
				if (Fragment_bottom_giveRingTone != null) {
					transtion.hide(Fragment_bottom_giveRingTone);
					transtion.show(Fragment_bottom_one);
					transtion.commit();
				} else {
					transtion.replace(R.id.details_FragmeLayout, Fragment_bottom_one).commit();
				}
			}
		}
	}

	private void selectGift() {
		selectFraggment = GIFT;
		left_tv.setTextColor(getApplication().getResources().getColor(R.color.details_bottom_black));
		left_bt.setVisibility(View.GONE);
		left_line.setVisibility(View.VISIBLE);
		right_tv.setTextColor(getApplication().getResources().getColor(R.color.details_bottom_select));
		right_bt.setVisibility(View.VISIBLE);
		right_line.setVisibility(View.GONE);
		if (Fragment_bottom_giveRingTone == null) {
			Fragment_bottom_giveRingTone = new DetailsRingtoneActivityGiveringTonesFragment();
			Fragment_bottom_giveRingTone.setData(info);
			if (Fragment_bottom_one != null)
				transtion.hide(Fragment_bottom_one);
			transtion.add(R.id.details_FragmeLayout, Fragment_bottom_giveRingTone).commit();
		} else {
			if (Fragment_bottom_one != null) {

				transtion.hide(Fragment_bottom_one);
				transtion.show(Fragment_bottom_giveRingTone);
				transtion.commit();
			} else {
				transtion.replace(R.id.details_FragmeLayout, Fragment_bottom_giveRingTone).commit();
			}
		}
	}

	/**
	 * 点击事件处理
	 * 
	 * @author Administrator
	 * 
	 */

	public class Click implements OnClickListener {

		@Override
		public void onClick(View v) {
			transtion = getSupportFragmentManager().beginTransaction();
			switch (v.getId()) {
			case R.id.details_bottom_left_textview:
				selectOrder();
				break;
			case R.id.details_bottom_right_textview:
				selectGift();
				break;
			case R.id.details_play_stop_loading:
				// 点击时间过快，不操作
				if (Utils.isFastDoubleClick()) {
					return;
				}

				// String url = "http://221.226.179.185/music_mmh/drive.wav";
				if (!isPlayer) {
					if (mediaPlayer.isprepared()) {
						return;
					}

					if (mediaPlayer.isPause()) {
						mediaPlayer.startPlayer();
						isPlayer = true;
						return;
					}
					// 播放
					mPlay_Stop.setBackgroundResource(0);
					// if(mLoading.getAnimation()==null)
					// {
					// mLoading.setAnimation(animation);// 设置动画
					// animation.startNow();
					// }
					mLoading.setVisibility(View.VISIBLE);

					mediaPlayer.getUrlFromServer(info.getToneID(), info.getToneType());
					// GetToneListenAddrReq req=new
					// GetToneListenAddrReq(handler);
					// req.sendGetToneListenAddrReq(info.getToneID(),
					// info.getToneType());
					// mediaPlayer.setDataSource(url);
					// mediaPlayer.prepare();

				} else {
					isPlayer = false;
					mediaPlayer.pause();
					mPlay_Stop.setBackgroundResource(R.mipmap.music_play);
				}

				break;
			// case R.id.details_download:
			// // 点击时间过快，不操作
			// if (Utils.isFastDoubleClick()) {
			// return;
			// }
			// stopMusic();
			// Intent intent = new Intent(DetailsRingtoneActivity.this,
			// OrderActivity.class);
			// intent.putExtra("bean", info);
			// if (mCaller.getVisibility() == View.VISIBLE) {
			// intent.putExtra("caller", mCaller.isChecked());
			// } else {
			// intent.putExtra("caller", false);
			// }
			//
			// if (mCalled.getVisibility() == View.VISIBLE) {
			// intent.putExtra("called", mCalled.isChecked());
			// } else {
			// intent.putExtra("called", false);
			// }
			// startActivity(intent);
			// break;
			case R.id.title_iv:
				// 点击时间过快，不操作
				if (Utils.isFastDoubleClick()) {
					return;
				}
				stopMusic();
				DetailsRingtoneActivity.this.finish();
				break;
			// case R.id.text_caller_default:
			// // 点击时间过快，不操作
			// if (Utils.isFastDoubleClick()) {
			// return;
			// }
			// UtilLog.e("Details caller :"+mCaller.isChecked());
			// if (mCaller.isChecked()) {
			// preferenceService.put("number_checked",
			// UserVariable.CALLNUMBER);
			// preferenceService.put("caller", false);
			// mCaller.setChecked(preferenceService.get("caller", false));
			// } else {
			// preferenceService.put("number_checked",
			// UserVariable.CALLNUMBER);
			// preferenceService.put("caller", true);
			// mCaller.setChecked(preferenceService.get("caller", true));
			// }
			// break;
			// case R.id.text_called_default:
			// // 点击时间过快，不操作
			// if (Utils.isFastDoubleClick()) {
			// return;
			// }
			// UtilLog.e("Details called : "+mCalled.isChecked());
			// if (mCalled.isChecked()) {
			// preferenceService.put("number_checked",
			// UserVariable.CALLNUMBER);
			// preferenceService.put("called", false);
			// mCalled.setChecked(preferenceService.get("called", false));
			// } else {
			// preferenceService.put("number_checked",
			// UserVariable.CALLNUMBER);
			// preferenceService.put("called", true);
			// mCalled.setChecked(preferenceService.get("called", true));
			// }
			// break;
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(DetailsRingtoneActivity.this, MainGroupActivity.class));
				finish();
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mediaPlayer.isPlaying() || mediaPlayer.getState() == LocalMediaPlayer.PAUSE) {
			pause();
		} else {
			stopMusic();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mediaPlayer != null) {
			mediaPlayer.uninsMedia();
			mediaPlayer = null;
		}
	}
}
