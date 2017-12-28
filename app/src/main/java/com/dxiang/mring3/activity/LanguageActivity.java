package com.dxiang.mring3.activity;


import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.app.BaseApp;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.utils.Utils;

/**
 * 语言更换界面
 * 
 * @author humingzhuo
 */

@SuppressLint("ValidFragment")
public class LanguageActivity extends BaseActivity{

	// 控件
	
	private RelativeLayout ll_language;
	private TextView tv_english,mTitle_tv,tv_spain;
	private ImageView mTitle_iv;
	// 变量
	private SharedPreferenceService preferenceService;
	public static Boolean language=false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language);
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

		tv_english=(TextView) LanguageActivity.this.findViewById(R.id.tv_english);//英语
		ll_language=(RelativeLayout) LanguageActivity.this.findViewById(R.id.rl_title_layout);//返回
		mTitle_iv = (ImageView)LanguageActivity.this.findViewById(R.id.title_iv);
		mTitle_tv = (TextView) LanguageActivity.this.findViewById(R.id.title_tv);
		tv_spain = (TextView) LanguageActivity.this.findViewById(R.id.tv_spain);
		
	}
	/**
	 * 初始化显示页面
	 */
	private void init(){
		mTitle_iv.setVisibility(View.VISIBLE);
		mTitle_tv.setText(getResources().getString(R.string.language));
		preferenceService = SharedPreferenceService.getInstance(this);
	}
	
	/**
	 * 控件绑定事件
	 */
	private void setClickListener() {

		Click click = new Click();
		tv_english.setOnClickListener(click);
		mTitle_iv.setOnClickListener(click);
		tv_spain.setOnClickListener(click);
	}
	
	/**
	 * 处理点击事件
	 */
	private class Click implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.tv_english: //改变英语
				// 点击时间过快，不操作
				if (Utils.isFastDoubleClick()) {
					return;
				}
				switchLanguage("");
				language=true;
				finish();  
			    break;
			case R.id.title_iv: //字返回
				LanguageActivity.this.finish();
				break;
			case R.id.tv_spain: //西班牙
				// 点击时间过快，不操作
				if (Utils.isFastDoubleClick()) {
					return;
				}
				switchLanguage("es");
				language=true;
				finish();
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 设置应用语言变换
	 * @param language
	 */
	public void switchLanguage(String language) {
		//设置应用语言类型
		Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
       if (language.equals("es")) {
            config.locale = new Locale("es", "ES");
        } else {
        	config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);
        
        //保存设置语言的类型
        preferenceService.put("language", language);
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
