package com.dxiang.mring3.activity;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dxiang.mring3.R;
import com.dxiang.mring3.request.AddCallerNumberToCallerGroupRequest;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

public class SelectCallerNumberActivity extends BaseActivity {
	private RelativeLayout rl_title_layout;
	// 右边的编辑
	private TextView mEdit_tv;
	// 返回标题
	private TextView mTitle_tv;
	// 返回图片
	private ImageView mTitle_iv;
	private EditText set_user_nikename, set_caller_number;
	private ImageView select_contact;
	public static final int SELECTCALLER = 1001;
	public static final int SELECTCONTACTRETURN = 1002;
	private String groupid;
	private ArrayList<String> phonelists = new ArrayList<String>();
	private ImageView liear_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callerselect_layout);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			groupid = bundle.getString("groupid");
			phonelists.clear();
			phonelists = bundle.getStringArrayList("useedmember");
		}
		initview();
		init();
	}

	private void initview() {

		rl_title_layout = (RelativeLayout) findViewById(R.id.rl_title_layout);// 返回字
		mEdit_tv = (TextView) findViewById(R.id.tv_edit);// 返回字
		mEdit_tv.setOnClickListener(mclick);
		mTitle_iv = (ImageView) findViewById(R.id.title_iv);
		mTitle_iv.setOnClickListener(mclick);
		mTitle_tv = (TextView) findViewById(R.id.title_tv);
		set_user_nikename = (EditText) findViewById(R.id.set_caller_name);
		set_caller_number = (EditText) findViewById(R.id.set_caller_number);
		select_contact = (ImageView) findViewById(R.id.select_contact);
		select_contact.setOnClickListener(mclick);
		liear_title = (ImageView) findViewById(R.id.liear_title);
		liear_title.setOnClickListener(mclick);
	}

	private void init() {
		mTitle_iv.setVisibility(View.VISIBLE);
		mEdit_tv.setVisibility(View.VISIBLE);
		mTitle_tv.setText(Utils.getResouceStr(mContext,
				R.string.callernumber_ring_add_name));
		mEdit_tv.setText(getString(R.string.tone_ringgroup_noedit));
	}

	private OnClickListener mclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(SelectCallerNumberActivity.this,
						MainGroupActivity.class));
				finish();
				break;
			case R.id.select_contact:
				Intent intent = new Intent(SelectCallerNumberActivity.this,
						PhoneListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("useedmember", phonelists);
				bundle.putString("groupid", groupid);
				intent.putExtras(bundle);
				startActivityForResult(intent, SELECTCALLER);
				break;
			case R.id.tv_edit:
				if (set_user_nikename.getText().length() == 0) {
					Utils.showTextToast(SelectCallerNumberActivity.this,
							R.string.callername_notnull);
					return;
				}
				if (set_caller_number.getText().length() == 0) {
					Utils.showTextToast(SelectCallerNumberActivity.this,
							R.string.callernumber_notnull);
					return;
				}
				if (phonelists.contains(set_caller_number.getText().toString())) {
					Utils.showTextToast(SelectCallerNumberActivity.this,
							R.string.contacts_notadd);
					return;
				}
				// startPbarU();
				new AddCallerNumberToCallerGroupRequest(handler)
						.sendAddCallerNumberToCallerGroupRequest("1", groupid,
								set_user_nikename.getText().toString(),
								set_caller_number.getText().toString());
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
		case FusionCode.REQUEST_EDITMANAGERCALLINGGROUP:
			// stopPbarU();
			SelectCallerNumberActivity.this.finish();
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
		case FusionCode.REQUEST_EDITMANAGERCALLINGGROUP:
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
		case FusionCode.REQUEST_EDITMANAGERCALLINGGROUP:
		case FusionCode.REQUEST_ADDNEWGROUP:
		}
		// stopPbarU();
	}

	private ArrayList<String> addsuccess = new ArrayList<String>();

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SELECTCALLER:
			if (SELECTCONTACTRETURN == resultCode) {
				addsuccess.clear();
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					Intent intent = getIntent();
					if (bundle.getStringArrayList("select") != null)
						addsuccess.addAll(bundle.getStringArrayList("select"));
					if (intent != null) {
						Bundle mbundle = new Bundle();
						mbundle.putStringArrayList("select", addsuccess);
						intent.putExtras(mbundle);
						setResult(
								CallerNumberGroupDetailActivity.ADDNEWUSERNUMBERRETURN,
								intent);
					}
					finish();
				}
			}
			break;
		}
	}

}
