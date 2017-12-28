package com.dxiang.mring3.activity;

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
import com.dxiang.mring3.request.ManagerCallerGroupRequest;
import com.dxiang.mring3.request.AddRingGroupRequest;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

public class RingGroupAddActivity extends BaseActivity {
	private RelativeLayout rl_title_layout;
	// 右边的编辑
	private TextView mEdit_tv;
	// 返回标题
	private TextView mTitle_tv;
	// 返回图片
	private ImageView mTitle_iv;
	private EditText set_user_nikename;
	private String groupname;
	private String groupid;
	private boolean foradd = true;
	private int grouptype;
	private String grouptitle;
	private ImageView liear_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ringgroupadd_layout);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			foradd = bundle.getBoolean("foradd");
			grouptype = bundle.getInt("addgrptype");
			if (!foradd) {
				groupname = bundle.getString("groupname");
				groupid = bundle.getString("groupid");
				grouptitle = bundle.getString("title");
			}
		}
		initview();
		init();
	}

	private void initview() {
		liear_title = (ImageView) findViewById(R.id.liear_title);
		liear_title.setOnClickListener(mclick);
		rl_title_layout = (RelativeLayout) findViewById(R.id.rl_title_layout);// 返回字
		mEdit_tv = (TextView) findViewById(R.id.tv_edit);// 返回字
		mEdit_tv.setOnClickListener(mclick);
		mTitle_iv = (ImageView) findViewById(R.id.title_iv);
		mTitle_iv.setOnClickListener(mclick);
		mTitle_tv = (TextView) findViewById(R.id.title_tv);
		set_user_nikename = (EditText) findViewById(R.id.set_user_nikename);
	}

	private void init() {
		mTitle_iv.setVisibility(View.VISIBLE);
		mEdit_tv.setVisibility(View.VISIBLE);
		if (grouptype == 0) {
			mTitle_tv.setText(Utils.getResouceStr(mContext,
					R.string.add_ring_grp));
			mEdit_tv.setText(getString(R.string.tone_ringgroup_noedit));
			set_user_nikename.setHint(getString(R.string.enter_ringgroup));
			if (!foradd) {
				mTitle_tv.setText(Utils.getResouceStr(mContext,
						R.string.tone_ringgroup_txt_edit));
			}
		} else if (grouptype == 1) {
			mTitle_tv.setText(Utils.getResouceStr(mContext,
					R.string.callernumber_ringgroup_add_name));
			mEdit_tv.setText(getString(R.string.tone_ringgroup_noedit));
			set_user_nikename
					.setHint(getString(R.string.callernumber_ringgroup_add_hint));
			if (!foradd) {
				mTitle_tv.setText(Utils.getResouceStr(mContext,
						R.string.callernumber_ringgroup_txt_edit));
			}
		}
		if (!foradd) {
			set_user_nikename.setText(groupname);
		}
	}

	private OnClickListener mclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(RingGroupAddActivity.this,
						MainGroupActivity.class));
				finish();
				break;
			case R.id.tv_edit:
//				if (set_user_nikename.getText().toString().trim().length() == 0) {
				if (set_user_nikename.getText().toString().trim().equals("")) {
					if (grouptype == 0) {
						Utils.showTextToast(RingGroupAddActivity.this,
								R.string.ring_group_add_empty);
					} else if (grouptype == 1) {
						Utils.showTextToast(RingGroupAddActivity.this,
								R.string.ring_group_add_empty);
					}
					return;
				}
				// startPbarU();
				if (grouptype == 0) {
					if (foradd) {
						new AddRingGroupRequest(handler)
								.sendAddRingGroupRequest("1", "",
										set_user_nikename.getText().toString()
												.trim());
					} else {
						new AddRingGroupRequest(handler)
								.sendAddRingGroupRequest("2", groupid,
										set_user_nikename.getText().toString()
												.trim());
					}
				} else if (grouptype == 1) {
					if (foradd) {
						new ManagerCallerGroupRequest(handler)
								.sendAddCallerGroupRequest("1",
										"00000000000000000000",
										set_user_nikename.getText().toString()
												.trim());
					} else {
						new ManagerCallerGroupRequest(handler)
								.sendAddCallerGroupRequest("2", groupid,
										set_user_nikename.getText().toString()
												.trim());
					}
				}
				break;
			case R.id.title_iv:
				finish();
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_MANAGERCALLINGGROUP:
		case FusionCode.REQUEST_ADDNEWGROUP:
			// stopPbarU();
			// stopPbarU();
			RingGroupAddActivity.this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void reqXmlFail(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlFail(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_MANAGERCALLINGGROUP:
		case FusionCode.REQUEST_ADDNEWGROUP:
			break;
		}
		// stopPbarU();
		EntryP entryP = (EntryP) msg.obj;
		if (!"400020".equals(entryP.getResult())) {
			Utils.showTextToast(mContext,
					UtlisReturnCode.ReturnCode(entryP.getResult(), mContext));
		}
	}

	@Override
	protected void reqError(Message msg) {
		// TODO Auto-generated method stub
		super.reqError(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_MANAGERCALLINGGROUP:
		case FusionCode.REQUEST_ADDNEWGROUP:
		}
		// stopPbarU();
	}

}