package com.dxiang.mring3.fragment;

import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.DetailsRingtoneActivity;
import com.dxiang.mring3.activity.LoginActivity;
import com.dxiang.mring3.activity.OrderActivity;
import com.dxiang.mring3.activity.DetailsRingtoneActivity.Click;
import com.dxiang.mring3.adapter.OrderDialog;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.ContentBuyTone;
import com.dxiang.mring3.request.QryValidaReq;
import com.dxiang.mring3.response.ContentBuyToneRsp;
import com.dxiang.mring3.response.QryValidaResp;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.UtilLog;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DetailsRingtoneActivityOrderFragment extends BaseFragment {
	// private CheckBox mCaller,mCalled;

	// private TextView text_caller_default,text_called_default;
	private TextView tv_price, details_title;

	// private LinearLayout ll_caller_default,ll_called_default;

	private TextView tv_download;
	// 从个人铃音库跳转过来 true
	private boolean flag = false;
	// 是否登陆录
	private boolean isLogin = false;

	private SharedPreferenceService preferenceService;

	private ToneInfo info;

	private View view;

	private String isOwner;// 判断是否拥有

	private String strFailValida;

	public DetailsRingtoneActivityOrderFragment() {
		// TODO Auto-generated constructor stub
	}

	public void setData(boolean flag, ToneInfo info) {
		this.flag = flag;
		this.info = info;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.details_fragment_one, null);
		initView(view);
		setClickListener();
		// textSize();
		return view;
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	private void initView(View view) {
		preferenceService = SharedPreferenceService.getInstance(getActivity());
		isLogin = UserVariable.LOGINED;
		tv_download = (TextView) view.findViewById(R.id.details_download);

		tv_price = (TextView) view.findViewById(R.id.details_tv_price);
		details_title = (TextView) view.findViewById(R.id.details_tv_title);

		if (null != info) {
			initRspData();
		}

	}

	private boolean isActive(String str) {
		if (!Utils.CheckTextNull(str)) {

			return false;
		}

		if (str.equals("1") || str.equals("2") || str.equals("3")) {
			return true;
		}

		return false;
	}

	private void initRspData() {

		details_title.setText(getResources().getString(R.string.tv_Charges));
		tv_price.setText(" " + Utils.doCount(info.getPrice()));

	}

	private void setClickListener() {
		Click click = new Click();

		tv_download.setOnClickListener(click);

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

	private void initLoginBuyDialog(String strMsg) {
		OrderDialog.Builder dialog = new OrderDialog.Builder(getActivity());
		dialog.setMessage(strMsg);
		dialog.setPositiveButton(getResources().getString(R.string.determine), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ContentBuyTone buy = new ContentBuyTone(p_h);
				buy.getContentBuyTone(UserVariable.CALLNUMBER, "1", info.getToneID());
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
		case FusionCode.REQUEST_CONTENTBUYTONE:
			ContentBuyToneRsp contentBuyToneRsp = (ContentBuyToneRsp) msg.obj;
			stopPbarU();

			//
			// if (caller) {
			//
			// sendCaller();
			// }
			// if (calledd) {
			// sendCalled();
			// }

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
		case FusionCode.REQUEST_CONTENTBUYTONE:
			ContentBuyToneRsp contentBuyToneRsp = (ContentBuyToneRsp) msg.obj;
			stopPbarU();

			Utils.showTextToast(mContext, getResources().getString(R.string.error_order_pending));

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
		case FusionCode.REQUEST_CONTENTBUYTONE:
			Utils.showTextToast(mContext, getResources().getString(R.string.error_order_pending));
			break;
		}
		super.reqError(msg);
	}

	public class Click implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.details_download:
				// 点击时间过快，不操作
				if (Utils.isFastDoubleClick()) {
					return;
				}
				if (!UserVariable.LOGINED) {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					intent.putExtra("from", "DetailsRingtoneActivity");

					startActivity(intent);
				} else {
					// Intent intent = new Intent(getActivity(),
					// OrderActivity.class);
					// intent.putExtra("from", "OrderFragment");
					// intent.putExtra("bean", info);
					// intent.putExtra("title",
					// getResources().getString(R.string.orderlogin_title));
					// startActivity(intent);

					QryValidaReq qvr = new QryValidaReq(p_h);
					qvr.sendQryValidaReqReq(UserVariable.CALLNUMBER);
				}
				// if(isLogin){
				// Intent intent = new Intent(getActivity(),
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
				// intent.putExtra("from", "OrderFragment");
				// startActivity(intent);
				// }else{
				// Intent intent =new Intent(getActivity(),LoginActivity.class);
				// intent.putExtra("from", "DetailsRingtoneActivity");
				// startActivity(intent);
				// }
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

			default:
				break;
			}
		}

	}
}
