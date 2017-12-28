package com.dxiang.mring3.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.adapter.CustomDialogUpdate;
import com.dxiang.mring3.adapter.OrderDialog;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.SuggestionFeedback;
import com.dxiang.mring3.response.SuggestionFeedbackRsp;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

/**
 * 意见反馈界面
 * 
 * @author humingzhuo
 */

@SuppressLint("ValidFragment")
@SuppressWarnings("unused")
public class FeedBackActivity extends BaseActivity {

	// 控件
	private EditText et_account;
	private Button btn_determine;
	private EditText et_content;
	private LinearLayout feedback;
	private RelativeLayout feedback_back;
	private ImageView mTitle_iv;
	private TextView mTitle_tv;
	// 变量
	private String account;
	private String content;
	private SuggestionFeedback suggestion;
	private SharedPreferenceService preferenceService;
	private ImageView liear_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		initWidget();
		setClickListener();
		init();
		// hintTextSize();
	}

	private void hintTextSize() {
		preferenceService = SharedPreferenceService.getInstance(this);
		String language = preferenceService.get("language", "");
		if (Utils.CheckTextNull(language)) {
			Utils.hintSize(et_account, 10);
		}
	}

	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 判断手机或内容是否为空
	 */
	private boolean Check() {
		account = et_account.getText().toString().trim();
		content = et_content.getText().toString().trim();
		if (!Utils.CheckTextNull(account)) {
			showTextToast(
					mContext,
					getResources()
							.getString(
									R.string.phone_number_or_email_address_can_not_be_empty));
			return false;
		} else if (!Utils.CheckTextNull(content)) {
			showTextToast(
					mContext,
					getResources().getString(
							R.string.the_content_can_not_be_empty));
			return false;
		}
		return true;

	}

	/**
	 * 初始化页面
	 */
	private void init() {
		mTitle_iv.setVisibility(View.VISIBLE);
		mTitle_tv.setText(getResources().getString(R.string.feeback));
		if (UserVariable.LOGINED) {
			et_account.setText(UserVariable.CALLNUMBER);
			et_account.setEnabled(false);
		}
	}

	/**
	 * 初始化系统控件
	 */
	private void initWidget() {
		et_account = (EditText) FeedBackActivity.this
				.findViewById(R.id.et_account);// 账号
		et_content = (EditText) FeedBackActivity.this
				.findViewById(R.id.et_content);// 内容
		btn_determine = (Button) FeedBackActivity.this
				.findViewById(R.id.ibtn_determine);// 确定
		feedback = (LinearLayout) FeedBackActivity.this
				.findViewById(R.id.ll_feedback);// 隐藏键盘
//		feedback_back = (RelativeLayout) FeedBackActivity.this
//				.findViewById(R.id.rl_title_layout);// 返回
		mTitle_iv = (ImageView) FeedBackActivity.this
				.findViewById(R.id.title_iv);
		mTitle_tv = (TextView) FeedBackActivity.this
				.findViewById(R.id.title_tv);
		liear_title = (ImageView) FeedBackActivity.this
				.findViewById(R.id.liear_title);
	}

	/**
	 * 控件绑定事件
	 */
	private void setClickListener() {

		Click click = new Click();
		btn_determine.setOnClickListener(click);
//		feedback_back.setOnClickListener(click);
		feedback.setOnClickListener(click);
		mTitle_iv.setOnClickListener(click);
	}

	/**
	 * 处理点击事件
	 */
	private class Click implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.ibtn_determine: // 确定
				if (!Utils.isFastDoubleClick()) {
					if (Check()) {
						// method stub
						String account = et_account.getText().toString().trim();
						String content = et_content.getText().toString().trim();
						UtilLog.e("---------------------------------");
						suggestion = new SuggestionFeedback(handler);
						suggestion.getSuggestionFeedback(account, content);
						startPbarU();
					}
				}
				break;
			case R.id.ll_feedback: // 隐藏键盘
				hideInput(mContext);
				break;
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(FeedBackActivity.this,
						MainGroupActivity.class));
				finish();
				break;
			case R.id.title_iv:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				hideInput(mContext);
				FeedBackActivity.this.finish();
				break;
			default:
				break;
			}
		}

	}

	public final static void hideInput(Context context) {
		try {
			((InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(((Activity) context)
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {

		}
	}

	public final static void showInput(Context context, EditText edit) {
		try {
			((InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.showSoftInput(edit, 0);
		} catch (Exception e) {

		}
	}

	/**
	 * 请求xml成功后的处理
	 */
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_SUGGESTIONFEEDBACK:
			SuggestionFeedbackRsp en = (SuggestionFeedbackRsp) msg.obj;
			stopPbarU();
			// Utils.showTextToast(mContext,getResources().getString(R.string.send_success));
			// FeedBackActivity.this.finish();
			CustomDialogUpdate.Builder builder = new CustomDialogUpdate.Builder(
					mContext);
			// builder.setMessage(R.string.send_success);
			// builder.setTitle(R.string.regitst_success_title_txt);
			// builder.setPositiveButton(R.string.determine,
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog, int which) {
			// dialog.dismiss();
			// FeedBackActivity.this.finish();
			// }
			// });
			// builder.create().show();
			initLoginBuyDialog(R.string.send_success);
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
		case FusionCode.REQUEST_SUGGESTIONFEEDBACK:
			SuggestionFeedbackRsp en = (SuggestionFeedbackRsp) msg.obj;
			stopPbarU();
			showTextToast(mContext,
					UtlisReturnCode.ReturnCode(en.getResult(), mContext));
			break;
		default:
			break;
		}
	}

	private void initLoginBuyDialog(int id) {
		OrderDialog.Builder dialog = new OrderDialog.Builder(this);
		dialog.setMessage(getResources().getString(id));
		dialog.setPositiveButton(getResources().getString(R.string.determine),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						FeedBackActivity.this.finish();
					}
				});
		dialog.setTitle(getResources().getString(
				R.string.user_cancellation));
		dialog.create().show();

	}

	@Override
	protected void reqError(Message msg) {
		stopPbarU();
		super.reqError(msg);

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
