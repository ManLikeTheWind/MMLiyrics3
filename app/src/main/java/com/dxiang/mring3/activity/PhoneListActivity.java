package com.dxiang.mring3.activity;

import java.util.ArrayList;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dxiang.mring3.R;
import com.dxiang.mring3.bean.UserPhoneInformation;
import com.dxiang.mring3.request.AddCallerNumberToCallerGroupRequest;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

public class PhoneListActivity extends BaseActivity {
	private RelativeLayout rl_title_layout;
	// 返回标题
	private TextView mTitle_tv;
	// 右边的编辑
	private TextView mEdit_tv;
	// 返回图片
	private ImageView mTitle_iv;
	// 是否正在编辑
	/** 是否在请求数据 **/
	private boolean isLoading = false;
	private ListView me_group_list;
	// 分类集合
	private GrpAdapter mgroupAdapter;
	private ArrayList<String> usedphonelist = new ArrayList<String>();
	private ArrayList<UserPhoneInformation> phonelist = new ArrayList<UserPhoneInformation>();
	private ArrayList<UserPhoneInformation> selectedlist = new ArrayList<UserPhoneInformation>();
	private String groupid;
	private ImageView liear_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_list);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			usedphonelist.clear();
			groupid = bundle.getString("groupid");
			usedphonelist = bundle.getStringArrayList("useedmember");
		}
		initView();
		testReadAllContacts();
	}

	private void initView() {
		me_group_list = (ListView) findViewById(R.id.me_group_list);
		rl_title_layout = (RelativeLayout) findViewById(R.id.rl_title_layout);// 返回字
		mTitle_iv = (ImageView) findViewById(R.id.title_iv);
		mTitle_iv.setVisibility(View.VISIBLE);
		mTitle_iv.setOnClickListener(mclick);
		mTitle_tv = (TextView) findViewById(R.id.title_tv);
		mTitle_tv.setVisibility(View.VISIBLE);
		mTitle_tv.setText(getString(R.string.contacts));
		mEdit_tv = (TextView) findViewById(R.id.tv_edit);
		mEdit_tv.setVisibility(View.VISIBLE);
		mEdit_tv.setText(getString(R.string.tone_ringgroup_noedit));
		mEdit_tv.setOnClickListener(mclick);
		liear_title = (ImageView) findViewById(R.id.liear_title);
		liear_title.setOnClickListener(mclick);
		mgroupAdapter = new GrpAdapter();
		me_group_list.setAdapter(mgroupAdapter);
		me_group_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
	}

	private OnClickListener mclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(PhoneListActivity.this,
						MainGroupActivity.class));
				finish();
				break;
			case R.id.tv_edit:
				selectedlist.clear();
				for (int i = 0; i < phonelist.size(); i++) {
					if (isSelected.get(i)) {
						selectedlist.add(phonelist.get(i));
					}
				}
				if (selectedlist.size() < 1) {
					Utils.showTextToast(mContext,
							getString(R.string.check_least_one_contact));
					return;
				}
				// startPbarU();
				addtag = 0;
				addsuccess.clear();
				for (int i = 0; i < selectedlist.size(); i++) {
					UserPhoneInformation mUserPhoneInformation = selectedlist
							.get(i);
					new AddCallerNumberToCallerGroupRequest(handler)
							.sendAddCallerNumberToCallerGroupRequest("1",
									groupid,
									mUserPhoneInformation.getContact_name(),
									mUserPhoneInformation.getContact_number());
				}
				break;
			case R.id.title_iv:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				finish();
				break;
			default:
				break;
			}

		}
	};

	/*
	 * 读取联系人的信息
	 */
	public void testReadAllContacts() {
		phonelist.clear();
		Cursor cursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		int contactIdIndex = 0;
		int nameIndex = 0;

		if (cursor.getCount() > 0) {
			contactIdIndex = cursor
					.getColumnIndex(ContactsContract.Contacts._ID);
			nameIndex = cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		}
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(contactIdIndex);
			String name = cursor.getString(nameIndex);
			// Log.i("phone", contactId);
			// Log.i("phone", name);
			/*
			 * 查找该联系人的phone信息
			 */
			Cursor phones = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ contactId, null, null);
			int phoneIndex = 0;
			if (phones.getCount() > 0) {
				phoneIndex = phones
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			}
			while (phones.moveToNext()) {
				String phoneNumber = phones.getString(phoneIndex);
				// if (!usedphonelist.contains(phoneNumber)) {
				UserPhoneInformation contactinfo = new UserPhoneInformation();
				Log.i("phone", phoneNumber);
				if (name == null || ("").equals(name)) {
					name = phoneNumber;
				}
				contactinfo.setContact_name(name);
				contactinfo.setContact_number(phoneNumber);
				phonelist.add(contactinfo);
				// }
			}
		}
		mgroupAdapter.initDate();
		mgroupAdapter.notifyDataSetChanged();
	}

	private HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();

	private class GrpAdapter extends BaseAdapter {

		private void initDate() {
			for (int i = 0; i < phonelist.size(); i++) {
				getIsSelected().put(i, false);
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return phonelist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return phonelist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ToneViewHolder holder = null;
			if (null == convertView) {
				holder = new ToneViewHolder();
				convertView = mInflater.inflate(R.layout.callermember_item,
						null);
				holder.membername = (TextView) convertView
						.findViewById(R.id.tone_item_songname_tv);
				holder.phonenumber = (TextView) convertView
						.findViewById(R.id.tone_item_songername_tv);
				holder.ck_rember = (CheckBox) convertView
						.findViewById(R.id.ck_rember);
				convertView.setTag(holder);
			} else {
				holder = (ToneViewHolder) convertView.getTag();
			}
			if (phonelist.size() > 0) {
				final int temp = position;
				UserPhoneInformation mUserPhoneInformation = phonelist
						.get(temp);
				holder.ck_rember.setVisibility(View.VISIBLE);
				String number = mUserPhoneInformation.getContact_number();
				if (usedphonelist.contains(number.length() < 7 ? "0" + number
						: number)) {
					holder.ck_rember.setEnabled(false);
				} else {
					holder.ck_rember.setEnabled(true);
				}
				holder.ck_rember.setChecked(getIsSelected().get(position));
				holder.ck_rember.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (getIsSelected().get(temp))
							getIsSelected().put(temp, false);
						else
							getIsSelected().put(temp, true);
					}
				});
				holder.membername.setText(mUserPhoneInformation
						.getContact_name());
				holder.phonenumber.setText(mUserPhoneInformation
						.getContact_number());
			}
			return convertView;
		}

		public HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}

		public void setIsSelected(HashMap<Integer, Boolean> misSelected) {
			isSelected = misSelected;
		}

	}

	private class ToneViewHolder {
		private CheckBox ck_rember;
		private TextView membername;
		private TextView phonenumber;
	}

	private int addtag = 0;
	private ArrayList<String> addsuccess = new ArrayList<String>();

	@Override
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_EDITMANAGERCALLINGGROUP:
			String number = selectedlist.get(addtag).getContact_number();
			addsuccess.add(number.length() < 7 ? "0" + number : number);
			if (addtag >= selectedlist.size() - 1) {
				Intent intent = getIntent();
				if (intent != null) {
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("select", addsuccess);
					intent.putExtras(bundle);
					setResult(RingGroupDetailActivity.ADDNEWTONRETURN, intent);
				}
				// stopPbarU();
				PhoneListActivity.this.finish();
			} else {
				addtag++;
			}
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
			if (addtag >= selectedlist.size() - 1) {
				Intent intent = getIntent();
				if (intent != null) {
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("select", addsuccess);
					intent.putExtras(bundle);
					setResult(RingGroupDetailActivity.ADDNEWTONRETURN, intent);
				}
				// stopPbarU();
				PhoneListActivity.this.finish();
			} else {
				addtag++;
			}
			break;
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
}
