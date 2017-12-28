package com.dxiang.mring3;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.BaseActivity;
import com.dxiang.mring3.activity.OrderActivity;
import com.dxiang.mring3.adapter.OrderDialog;
import com.dxiang.mring3.request.QryPlayModeRequest;
import com.dxiang.mring3.request.QryToneSetReq;
import com.dxiang.mring3.request.SetPlayModeRequest;
import com.dxiang.mring3.response.QryPlayModeResp;
import com.dxiang.mring3.response.QryToneGrpResp;
import com.dxiang.mring3.response.SetPlayModeRsp;
import com.dxiang.mring3.response.UserSubSerRsp;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;;

public class SetingRandomDefaultActivity extends BaseActivity implements OnClickListener {

	// 控件
	private RelativeLayout rl_title_layout;
	// 返回标题
	private TextView mTitle_tv;
	// 返回图片
	private ImageView mTitle_iv;

	private LinearLayout ll_SetDefault;
	private LinearLayout ll_SetRandom;
	
	private ImageView iv_SetDefault;
	private ImageView iv_SetRandom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seting_random_default);

		initView();
		initData();
		initClick();

	}

	private void initClick() {
		mTitle_iv.setOnClickListener(this);
		ll_SetDefault.setOnClickListener(this);
		ll_SetRandom.setOnClickListener(this);
		// mTitle_iv.setOnClickListener(this);
		// mTitle_iv.setOnClickListener(this);
		// mTitle_iv.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_iv:
			finish();
			break;
		// 选择了默认
		case R.id.ll_setdefault:
			iv_SetDefault.setEnabled(true);
			iv_SetRandom.setEnabled(false);
			setMode("1", FusionCode.REQUEST_SETPLAYMODE_DEFAULT);
			/* 接口上传数据 */

			break;

		// 选择随机
		case R.id.ll_setrandom:
			iv_SetDefault.setEnabled(false);
			iv_SetRandom.setEnabled(true);
			setMode("2", FusionCode.REQUEST_SETPLAYMODE_RANDOM);
			/* 接口上传数据 */
			break;
		// case R.id.title_iv:
		// default:
		// case R.id.title_iv:
		// default:
		}
	}

	private void initDialog() {
		OrderDialog.Builder dialog = new OrderDialog.Builder(SetingRandomDefaultActivity.this);
		dialog.setMessage(getResources().getString(R.string.default_dialog));
		dialog.setPositiveButton(getResources().getString(R.string.determine), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		dialog.setTitle(getResources().getString(R.string.dialog_title));
		dialog.create().show();

	}

	private void setMode(String type, int Mode) {
		startPbarU();
		new SetPlayModeRequest(handler, Mode).sendSetGrpToneReq(type);
	}

	private void QryMode() {
		startPbarU();
		new QryPlayModeRequest(handler).qryPlayModerep();
	}

	private void initData() {
		mTitle_tv.setText(getResources().getString(R.string.random_default_setting));
		mTitle_iv.setVisibility(View.VISIBLE);

		QryMode();
		/*
		 * 接口获取网络数据进行初始化设置 iv_SetDefault.setEnabled(true);//再进行获取网络数据
		 * iv_SetRandom.setEnabled(false);
		 */

	}

	private void initView() {
		rl_title_layout = (RelativeLayout) SetingRandomDefaultActivity.this.findViewById(R.id.rl_title_layout);// 返回字
		mTitle_tv = (TextView) SetingRandomDefaultActivity.this.findViewById(R.id.title_tv);
		mTitle_iv = (ImageView) SetingRandomDefaultActivity.this.findViewById(R.id.title_iv);

		ll_SetDefault = (LinearLayout) SetingRandomDefaultActivity.this.findViewById(R.id.ll_setdefault);
		ll_SetRandom = (LinearLayout) SetingRandomDefaultActivity.this.findViewById(R.id.ll_setrandom);

		iv_SetDefault = (ImageView) SetingRandomDefaultActivity.this.findViewById(R.id.iv_setdefault);
		iv_SetRandom = (ImageView) SetingRandomDefaultActivity.this.findViewById(R.id.iv_setrandom);
		iv_SetDefault.setEnabled(false);
		iv_SetRandom.setEnabled(false);
	}

	@Override
	protected void reqXmlSucessed(Message msg) {
		switch (msg.arg1) {
		case FusionCode.REQUEST_SETPLAYMODE_DEFAULT:
			stopPbarU();
			Toast.makeText(getApplicationContext(), R.string.set_success, Toast.LENGTH_SHORT).show();
			initDialog();
			break;
		case FusionCode.REQUEST_SETPLAYMODE_RANDOM:
			stopPbarU();
			Toast.makeText(getApplicationContext(), R.string.set_success, Toast.LENGTH_SHORT).show();
			break;
		case FusionCode.REQUEST_QRYPLAYMODE:
			stopPbarU();
			QryPlayModeResp rep = (QryPlayModeResp) msg.obj;
			String setType = rep.getSetType();

			if (setType != null && setType.equals("2")) {
				iv_SetDefault.setEnabled(false);
				iv_SetRandom.setEnabled(true);
			} else if (setType != null && setType.equals("1")) {
				iv_SetDefault.setEnabled(true);
				iv_SetRandom.setEnabled(false);
			}
			break;
		}
		super.reqXmlSucessed(msg);
	}

	@Override
	protected void reqXmlFail(Message msg) {
		switch (msg.arg1) {

		case FusionCode.REQUEST_SETPLAYMODE_DEFAULT:
			stopPbarU();
			SetPlayModeRsp set = (SetPlayModeRsp) msg.obj;

			Utils.showTextToast(getApplicationContext(), UtlisReturnCode.ReturnCode(set.getResult(), mContext));
			break;
		case FusionCode.REQUEST_SETPLAYMODE_RANDOM:
			stopPbarU();
			SetPlayModeRsp set2 = (SetPlayModeRsp) msg.obj;

			Utils.showTextToast(getApplicationContext(), UtlisReturnCode.ReturnCode(set2.getResult(), mContext));
			break;
		case FusionCode.REQUEST_QRYPLAYMODE:
			stopPbarU();
			QryPlayModeResp rep = (QryPlayModeResp) msg.obj;
			iv_SetDefault.setEnabled(true);
			iv_SetRandom.setEnabled(false);
			Utils.showTextToast(getApplicationContext(), UtlisReturnCode.ReturnCode(rep.getResult(), mContext));
			break;
		}
		// TODO Auto-generated method stub
		super.reqXmlFail(msg);
	}

	@Override
	protected void reqError(Message msg) {
		switch (msg.arg1) {
		case FusionCode.REQUEST_QRYPLAYMODE:

			iv_SetDefault.setEnabled(true);
			iv_SetRandom.setEnabled(false);

			break;
		case FusionCode.REQUEST_SETPLAYMODE:
			stopPbarU();
			break;
		default:
			break;
		}

		super.reqError(msg);
	}
}
