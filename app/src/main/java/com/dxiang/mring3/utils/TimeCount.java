package com.dxiang.mring3.utils;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.app.BaseApp;

@SuppressLint("ResourceAsColor")
public class TimeCount extends CountDownTimer {

	/**
	 * 倒计时公共类
	 * 
	 * @author dingshide
	 */
	private TextView tv;
	private EditText et;

	public TimeCount(long millisInFuture, long countDownInterval, TextView tv,
			EditText et) {
		super(millisInFuture, countDownInterval);
		// TODO Auto-generated constructor stub
		this.tv = tv;
		this.et = et;
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		tv.setText(BaseApp.getInstance().getString(
				R.string.regist_vercode_txt_btn));
		et.setText("");
		et.setEnabled(false);
		tv.setClickable(true);
		tv.setTextColor(BaseApp.getInstance().getApplicationContext()
				.getResources().getColor(R.color.regist_vercod_light));
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onTick(long arg0) {
		// TODO Auto-generated method stub
		tv.setText(BaseApp.getInstance().getString(
				R.string.regist_vercode_txt_btnone)
				+ "\n"
				+ arg0
				/ 1000
				+ BaseApp.getInstance().getString(
						R.string.regist_vercode_txt_btntwo));
		tv.setClickable(false);

		tv.setTextColor(BaseApp.getInstance().getApplicationContext()
				.getResources().getColor(R.color.regist_vercod_dark));
	}

}
