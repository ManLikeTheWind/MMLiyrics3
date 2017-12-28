package com.dxiang.mring3.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.AboutusActivity;
import com.dxiang.mring3.activity.FeedBackActivity;
import com.dxiang.mring3.activity.HelpActivity;
import com.dxiang.mring3.activity.LanguageActivity;
import com.dxiang.mring3.activity.MainGroupActivity;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;

/**
 * 关于界面
 * 
 * @author humingzhuo
 */

@SuppressLint("ValidFragment")
@SuppressWarnings("unused")
public class MoreFragment extends BaseFragment {

	// 控件
	private TextView tv_language;
	private TextView tv_aboutus;
	private TextView tv_help, tv_feedback, tv_more;

	// 变亮
	public final String TAG = "MoreFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootview = inflater.inflate(R.layout.activity_more, container,
				false);
		initWidget(rootview);
		setClickListener();
		// init();
		return rootview;
	}

	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (LanguageActivity.language) {
			LanguageActivity.language = false;

			// 启动新Activity 刷新页面
			Intent intent = new Intent(getActivity(), MainGroupActivity.class);
			intent.putExtra("setLanguage", true);
			getActivity().startActivity(intent);
			getActivity().finish();
		}
	}

	/**
	 * 初始化页面
	 */
	private void init() {
		// tv_more.setText(getResources().getString(R.string.more));
	}

	/**
	 * 初始化系统控件
	 */
	private void initWidget(View rootview) {
		tv_aboutus = (TextView) rootview.findViewById(R.id.tv_aboutus); // 关于
		tv_feedback = (TextView) rootview.findViewById(R.id.tv_feedback);// 意见反馈
		tv_help = (TextView) rootview.findViewById(R.id.tv_help);// 帮助
		tv_language = (TextView) rootview.findViewById(R.id.tv_language);// 语言
		// tv_more = (TextView) rootview.findViewById(R.id.title_tv);
	}

	/**
	 * 控件绑定事件
	 */
	private void setClickListener() {

		Click click = new Click();
		tv_aboutus.setOnClickListener(click);
		tv_feedback.setOnClickListener(click);
		tv_help.setOnClickListener(click);
		tv_language.setOnClickListener(click);
	}

	/**
	 * 处理点击事件
	 */
	private class Click implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.tv_aboutus: // 关于
				if (!Utils.isFastDoubleClick()) {
					startActivity(new Intent(getActivity(),
							AboutusActivity.class));
				}
				break;
			case R.id.tv_language: // 语言
				if (!Utils.isFastDoubleClick()) {
					startActivity(new Intent(getActivity(),
							LanguageActivity.class));
				}
				break;
			case R.id.tv_feedback: // 意见反馈
				if (!Utils.isFastDoubleClick()) {
					startActivity(new Intent(getActivity(),
							FeedBackActivity.class));
				}
				break;
			case R.id.tv_help: // 帮助
				if (!Utils.isFastDoubleClick()) {
					startActivity(new Intent(getActivity(), HelpActivity.class));
				}
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
