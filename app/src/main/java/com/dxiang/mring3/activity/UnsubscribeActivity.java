package com.dxiang.mring3.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.UnSubscribe;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.response.UnSubscribeRsp;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

/**
 * 注销彩铃界面
 * 
 * @author humingzhuo
 */

@SuppressLint("ValidFragment")
public class UnsubscribeActivity extends BaseActivity{

	// 控件
	
	private RelativeLayout rl_title_layout;
	private EditText et_phonenumber,et_password;
	private Button btn_determine;
	private TextView mTitle_tv;
	private ImageView mTitle_iv;
	// 变量
	public static String id="";
	
	private UnSubscribe unsubscribe;
	
	private SharedPreferenceService preferenceService;
	  private ImageView liear_title;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unsubscribe);
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
	}

	/**
	 * 初始化系统控件
	 */
	private void initWidget() {

		rl_title_layout=(RelativeLayout) UnsubscribeActivity.this.findViewById(R.id.rl_title_layout);//返回字
		et_phonenumber=(EditText) UnsubscribeActivity.this.findViewById(R.id.et_phonenumber);//输入手机号
		et_password=(EditText) UnsubscribeActivity.this.findViewById(R.id.et_password);//输入密码
		btn_determine=(Button) UnsubscribeActivity.this.findViewById(R.id.btn_determine);//确定
		mTitle_iv = (ImageView)UnsubscribeActivity.this.findViewById(R.id.title_iv);
		mTitle_tv = (TextView) UnsubscribeActivity.this.findViewById(R.id.title_tv);
		liear_title=(ImageView) UnsubscribeActivity.this.findViewById(R.id.liear_title );
	}
	/**
	 * 初始化显示页面
	 */
	
	private void init(){
		mTitle_iv.setVisibility(View.VISIBLE);
		preferenceService = SharedPreferenceService.getInstance(this);
		if("2".equals(id)){
			mTitle_tv.setText(getResources().getString(R.string.unsubscribe_cakker_CRBT));
			btn_determine.setText(getResources().getString(R.string.unsubscribe_cakker_CRBT));
		}else if("1".equals(id)){
			btn_determine.setText(getResources().getString(R.string.unsubscribe_cakked_CRBT));
			mTitle_tv.setText(getResources().getString(R.string.unsubscribe_cakked_CRBT));
		}
		UtilLog.e(UserVariable.CALLNUMBER+"");
		if(Utils.CheckTextNull(UserVariable.CALLNUMBER)){
			et_phonenumber.setText(UserVariable.CALLNUMBER);
		}
	}
	
	/**
	 * 控件绑定事件
	 */
	private void setClickListener() {

		Click click = new Click();
		btn_determine.setOnClickListener(click);
		mTitle_iv.setOnClickListener(click);
		liear_title.setOnClickListener(click);
	}
	/**
	 * 判断当前密码是否为空和密码的长度是否正确
	 * @return
	 */
	private boolean checkUnSubscribe(){
		if (!Utils.CheckTextNull(et_password.getText().toString())) {
			showTextToast(mContext,Utils.getResouceStr(mContext, R.string.password_can_not_be_empty));
			return false;
		} 
		if (!Utils.IsPasswLength(et_password.getText().toString())) {
			showTextToast(mContext,Utils.getResouceStr(mContext, R.string.password_can_not_be_less_than_6_numbers));
			return false;
		} 
		return true;
	}
	
	/**
	 * 处理点击事件
	 */
	private class Click implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.title_iv: //字返回
				UnsubscribeActivity.this.finish();
			    break;
			case R.id.btn_determine: //确定
				if(!Utils.isFastDoubleClick()){
					if(checkUnSubscribe()){
						UtilLog.e("---------------------------------------");
						unsubscribe=new UnSubscribe(handler);
						unsubscribe.qryUnSubscribe(UserVariable.CALLNUMBER, et_password.getText().toString(), id,true);
						startPbarU();
					}
				}
				break;
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(UnsubscribeActivity.this,MainGroupActivity.class));
				finish();
				break;
			default:
				break;
			}
		}
	}
	/**
	 * 请求xml成功后的处理
	 */
	protected void reqXmlSucessed(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_UNSUBSCRIBE:
			EntryP en=new EntryP();
			if("0".equals(UserVariable.STATUSCALLED) && !"0".equals(UserVariable.STATUSCALLING)){
				UserCenterActivity.ISCHECKUSER=true;
				preferenceService.put("isAutoLogin", false);
				UserVariable.LOGINED=false;
			}
			if(!"0".equals(UserVariable.STATUSCALLED) && "0".equals(UserVariable.STATUSCALLING)){
				UserCenterActivity.ISCHECKUSER=true;
				preferenceService.put("isAutoLogin", false);
				UserVariable.LOGINED=false;
			}
			UserCenterActivity.ISUSERPASSWORDUNSUBSCRIBE=true;
			stopPbarU();
			if(isShowing())
			{
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						UnsubscribeActivity.this.finish();
					}
				}, Commons.HIDETIME);
			}
			else
			{
				UnsubscribeActivity.this.finish();
			}
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
		case FusionCode.REQUEST_UNSUBSCRIBE:
			UnSubscribeRsp en=(UnSubscribeRsp) msg.obj;
			UtilLog.e(en.description+"unsubscribe");
			stopPbarU();
			showTextToast(mContext, UtlisReturnCode.ReturnCode(en.getResult(), mContext));
			break;
		default:
			break;
		}
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
