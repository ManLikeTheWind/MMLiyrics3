package com.dxiang.mring3.fragment;

import java.util.regex.Pattern;

import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.DetailsRingtoneActivity;
import com.dxiang.mring3.activity.LoginActivity;
import com.dxiang.mring3.activity.OrderActivity;
import com.dxiang.mring3.adapter.OrderDialog;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.ContentBuyTone;
import com.dxiang.mring3.request.GiftToneRequest;
import com.dxiang.mring3.request.QryValidaReq;
import com.dxiang.mring3.response.ContentBuyToneForAppRsp;
import com.dxiang.mring3.response.ContentBuyToneRsp;
import com.dxiang.mring3.response.GiftToneRsp;
import com.dxiang.mring3.response.QryValidaResp;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsRingtoneActivityGiveringTonesFragment extends BaseFragment {

	private View view;
	private ToneInfo info;
	private boolean isLogin;
	private TextView details_tv_price;
	private EditText details_num;
	private TextView details_give, details_title;
	private SharedPreferenceService preferenceService;
	private DetailsRingtoneActivity activity;
	private String isOwner;// 判断是否拥有

	private String strFailValida;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.details_fragment_givetone, null);
		initView(view);
		setClickListener();
		return view;
	}

	private void setClickListener() {
		Click click = new Click();
		details_give.setOnClickListener(click);

	}

	public void setData(ToneInfo info) {

		this.info = info;

	}

	private void initView(View view) {
		isLogin = UserVariable.LOGINED;
		activity = (DetailsRingtoneActivity) getActivity();
		preferenceService = SharedPreferenceService.getInstance(getActivity());
		details_tv_price = (TextView) view.findViewById(R.id.details_tv_price);
		details_num = (EditText) view.findViewById(R.id.details_num);
		details_give = (TextView) view.findViewById(R.id.details_give);
		details_title = (TextView) view.findViewById(R.id.details_tv_title);
		details_num.setText(activity.getNumber());
		if (info != null) {
			details_title.setText(getResources().getString(R.string.tv_Charges));
			details_tv_price.setText(" " + Utils.doCount(info.getPrice()));
		}
	}

	private void initLoginBuyDialog(String strMsg) {
		OrderDialog.Builder dialog = new OrderDialog.Builder(getActivity());
		dialog.setMessage(strMsg);
		dialog.setPositiveButton(getResources().getString(R.string.determine), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				GiftToneRequest gift = new GiftToneRequest(p_h);
				gift.setGiftToneRequest(UserVariable.CALLNUMBER, "1", info.getToneID(),
						details_num.getText().toString().trim());
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		dialog.setTitle(getResources().getString(R.string.user_cancellation));
		dialog.create().show();

	}

	private void initPengdingDialog(String strMsg) {
		OrderDialog.Builder dialog = new OrderDialog.Builder(getActivity());
		dialog.setMessage(strMsg);
		dialog.setPositiveButton(getResources().getString(R.string.determine), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		dialog.setTitle(getResources().getString(R.string.user_cancellation));
		dialog.create().show();

	}

	@Override
	protected void reqXmlSucessed(Message msg) {
		switch (msg.arg1) {
		case FusionCode.REQUEST_VALIDA:
			QryValidaResp qvdQryValidaReq = (QryValidaResp) msg.obj;
			isOwner = qvdQryValidaReq.getResult();
			strFailValida = getResources().getString(R.string.error_order_pending);
			initLoginBuyDialog(String.format(getResources().getString(R.string.order_no_pending), info.getToneName(),
					info.getSingerName(), Utils.doCount(info.getPrice().toString().trim())));
			break;
		case FusionCode.REQUEST_GIFTTONE:
			GiftToneRsp GiftToneRsp = (GiftToneRsp) msg.obj;
			stopPbarU();
			break;
		default:
			break;
		}
		super.reqXmlSucessed(msg);
	}

	@Override
	protected void reqXmlFail(Message msg) {
		switch (msg.arg1) {
		case FusionCode.REQUEST_VALIDA:
			QryValidaResp qvdQryValidaReq = (QryValidaResp) msg.obj;
			isOwner = qvdQryValidaReq.getResult();
			if (isOwner.equals("400179")) {
				strFailValida = getResources().getString(R.string.order_pending);
				initPengdingDialog(strFailValida);

			} else {
				Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(qvdQryValidaReq.getResult(), mContext));
			}

			break;
		case FusionCode.REQUEST_GIFTTONE:
			GiftToneRsp GiftToneRsp = (GiftToneRsp) msg.obj;
			stopPbarU();

			Utils.showTextToast(mContext, UtlisReturnCode.ReturnCode(GiftToneRsp.getResult(), mContext));

			break;
		default:
			break;
		}
		super.reqXmlFail(msg);
	}

	@Override
	protected void reqError(Message msg) {
		switch (msg.arg1) {
		case FusionCode.REQUEST_VALIDA:
			QryValidaResp qvdQryValidaReq = (QryValidaResp) msg.obj;
			isOwner = qvdQryValidaReq.getResult();
			if (isOwner.equals("400179")) {
				strFailValida = getResources().getString(R.string.order_pending);
				initPengdingDialog(strFailValida);

			} else {
				Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(qvdQryValidaReq.getResult(), mContext));
			}
			break;
		case FusionCode.REQUEST_GIFTTONE:
			ContentBuyToneForAppRsp contentButToneForAppRsp = (ContentBuyToneForAppRsp) msg.obj;
			stopPbarU();

			Utils.showTextToast(mContext, getResources().getString(R.string.error_order_pending));

			break;
		}
		super.reqError(msg);
	}

	public class Click implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.details_give:
				if (!UserVariable.LOGINED) {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					intent.putExtra("from", "DetailsRingtoneActivity");
					startActivity(intent);
				} else {
					if (details_num.getText().toString().equals("") && details_num.getText().length() == 0) {
						Toast.makeText(getActivity(), getResources().getString(R.string.phonenumber_check), 1000)
								.show();
						return;
					}
					if (!isInteger(details_num.getText().toString())) {
						Toast.makeText(getActivity(), getResources().getString(R.string.regist_phone_num_hint), 1000)
								.show();
						return;
					}
					if (details_num.getText().toString().equals(UserVariable.CALLNUMBER)) {
						initLoginBuyDialog(R.string.order_error);
						return;
					}
					// Intent intent = new Intent(getActivity(),
					// OrderActivity.class);
					// intent.putExtra("from", "GiftFragment");
					// String num = details_num.getText().toString();
					// activity.setNumber(num);
					// intent.putExtra("bean", info);
					// intent.putExtra("receive_number", num);
					// intent.putExtra("title",
					// getResources().getString(R.string.giftlogin_title));
					// startActivity(intent);
					QryValidaReq qvr = new QryValidaReq(p_h);
					qvr.sendQryValidaReqReq(UserVariable.CALLNUMBER);
				}
			}
		}

	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	private void initLoginBuyDialog(int id) {
		OrderDialog.Builder dialog = new OrderDialog.Builder(getActivity());
		dialog.setMessage(getResources().getString(id));
		dialog.setPositiveButton(getResources().getString(R.string.determine), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.setTitle(getResources().getString(R.string.user_cancellation));
		dialog.create().show();

	}

}
