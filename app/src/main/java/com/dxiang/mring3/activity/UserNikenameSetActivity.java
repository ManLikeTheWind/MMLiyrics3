package com.dxiang.mring3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dxiang.mring3.R;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.Utils;

public class UserNikenameSetActivity extends BaseActivity {
	private RelativeLayout rl_title_layout;
	// 返回标题
	private TextView mTitle_tv;
	// 返回图片
	private ImageView mTitle_iv;
	private SharedPreferenceService preferenceService;
	private EditText set_user_nikename;
	private TextView number;
	private Button ibtn_determine;
	private ImageView liear_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nikename_set);
		initview();
		init();
	}

	private void initview() {
		rl_title_layout = (RelativeLayout) findViewById(R.id.rl_title_layout);// 返回字
		mTitle_iv = (ImageView) findViewById(R.id.title_iv);
		mTitle_iv.setOnClickListener(mclick);
		mTitle_tv = (TextView) findViewById(R.id.title_tv);
		liear_title = (ImageView) findViewById(R.id.liear_title);
		set_user_nikename = (EditText) findViewById(R.id.set_user_nikename);
		number = (TextView) findViewById(R.id.number);
		ibtn_determine = (Button) findViewById(R.id.ibtn_determine);
		ibtn_determine.setOnClickListener(mclick);
		liear_title.setOnClickListener(mclick);
	}

	private void init() {
		mTitle_iv.setVisibility(View.VISIBLE);
		mTitle_tv.setText(Utils.getResouceStr(mContext,
				R.string.set_usernikename));
		preferenceService = SharedPreferenceService.getInstance(this);
		String mtext = preferenceService.get(UserVariable.CALLNUMBER, "");
		if (!mtext.equals("")) {
			set_user_nikename.setText(mtext);
		}
		number.setText(getString(R.string.nikename_logion_number) + " :  "
				+ UserVariable.CALLNUMBER);
	}

	private OnClickListener mclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.title_iv:
				finish();
				break;
			case R.id.ibtn_determine:
				if (set_user_nikename.getText().toString().trim().length() == 0) {
					Utils.showTextToast(UserNikenameSetActivity.this,
							R.string.nikename_check);
					return;
				}
				preferenceService.put(UserVariable.CALLNUMBER,
						set_user_nikename.getText().toString().trim());
				finish();
				break;
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(UserNikenameSetActivity.this,
						MainGroupActivity.class));
				finish();
				break;
			default:
				break;
			}

		}
	};
}
