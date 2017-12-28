package com.dxiang.mring3.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dxiang.mring3.R;

public class OrderLoginActivity extends BaseActivity {

	private ImageView mIvBack;
	private TextView mTitle;
	private TextView mTvSongName;
	private TextView mTvSongerName;
	private EditText mEtPhoneNo;
	private EditText mEtVerCode;
	private Button mBtnVerCode;
	private Button mBtnLogin;
	private TextView mTvBuy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_login);
		initViews();
		setListener();
	}

	private void setListener() {
		// TODO Auto-generated method stub
		mIvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mTvBuy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		mBtnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				startFragment(new LoginFragment());
			}
		});

		mBtnVerCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new TimeCount(120000, 1000).start();
				// 获取验证码
			}
		});
	}

	private void initViews() {
		// TODO Auto-generated method stub
		mIvBack = (ImageView) findViewById(R.id.title_iv);
		mTitle = (TextView) findViewById(R.id.title_tv);
		mBtnVerCode = (Button) findViewById(R.id.orderlogin_vercode_btn);
		mTvSongName = (TextView) findViewById(R.id.orderlogin_songname_tv);
		mTvSongerName = (TextView) findViewById(R.id.orderlogin_songername_tv);
		mTvBuy = (TextView) findViewById(R.id.orderlogin_buy_tv);
		mBtnLogin = (Button) findViewById(R.id.orderlogin_btn);

		mTitle.setText(R.string.orderlogin_title);

	}

	private class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			mBtnVerCode.setTextColor(getResources().getColor(
					R.color.regist_vercod_dark));
			mBtnVerCode.setClickable(false);
			mBtnVerCode.setText("Get back after" + "\n" + millisUntilFinished
					/ 1000 + " seconds");
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			mBtnVerCode.setClickable(true);
			mBtnVerCode.setText(R.string.regist_vercode_txt_btn);
			mBtnVerCode.setTextColor(getResources().getColor(
					R.color.regist_vercod_light));
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
}
