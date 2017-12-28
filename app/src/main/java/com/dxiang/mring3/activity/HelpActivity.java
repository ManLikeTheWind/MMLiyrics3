package com.dxiang.mring3.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.utils.Utils;

/**
 * 帮助界面
 * 
 * @author humingzhuo
 */

@SuppressLint("ValidFragment")
public class HelpActivity extends BaseActivity{

	// 控件
	
	private RelativeLayout rl_title_layout;
	private TextView tv_hple1,mTitle_tv;
	private ImageView mTitle_iv;
	  private ImageView liear_title;
	// 变量

	private WebView web;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
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

//		tv_hple1=(TextView) HelpActivity.this.findViewById(R.id.tv_hple1);//显示文字
		rl_title_layout = (RelativeLayout) HelpActivity.this.findViewById(R.id.rl_title_layout);//返回
		mTitle_iv = (ImageView)HelpActivity.this.findViewById(R.id.title_iv);
		mTitle_tv = (TextView) HelpActivity.this.findViewById(R.id.title_tv);
		liear_title=(ImageView)  HelpActivity.this.findViewById(R.id.liear_title );
		web = (WebView) this.findViewById(R.id.main_webview);
		web.loadUrl("file:///android_asset/html/"+getResources().getString(R.string.help_html));
	}
//	/**
//	 * 初始化显示页面
//	 */
	private void init(){
//		tv_hple1.setText(getResources().getString(R.string.help_content));
		mTitle_tv.setText(getResources().getString(R.string.help));
		mTitle_iv.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 控件绑定事件
	 */
	private void setClickListener() {

		Click click = new Click();
		liear_title.setOnClickListener(click);
		mTitle_iv.setOnClickListener(click);
	}
	
	/**
	 * 处理点击事件
	 */
	private class Click implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(HelpActivity.this,MainGroupActivity.class));
				finish();
				break;
			case R.id.title_iv:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				HelpActivity.this.finish();
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
	}
}
