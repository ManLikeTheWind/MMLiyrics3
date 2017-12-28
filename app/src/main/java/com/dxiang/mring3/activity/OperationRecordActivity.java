package com.dxiang.mring3.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.dxiang.mring3.R;
import com.dxiang.mring3.bean.RecordInfo;
import com.dxiang.mring3.bean.UserCallingGrpSeriable;
import com.dxiang.mring3.request.OperationRecordRequest;
import com.dxiang.mring3.request.QryToneById;
import com.dxiang.mring3.response.OperationRecordResponse;
import com.dxiang.mring3.response.QryToneByIdRsp;
import com.dxiang.mring3.utils.EllipsizeUtils;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.Utils;

public class OperationRecordActivity extends BaseActivity {
	private RelativeLayout rl_title_layout;
	// 返回标题
	private TextView mTitle_tv;
	// 右边的编辑
	private TextView mEdit_tv;
	// 返回图片
	private ImageView mTitle_iv;
	private RadioGroup group;
	ArrayList<RecordInfo> selectedshowlist = new ArrayList<RecordInfo>();
	ArrayList<RecordInfo> middleshowlist = new ArrayList<RecordInfo>();
	ArrayList<RecordInfo> list1 = new ArrayList<RecordInfo>();
	ArrayList<RecordInfo> list2 = new ArrayList<RecordInfo>();
	ArrayList<RecordInfo> list3 = new ArrayList<RecordInfo>();
	ArrayList<RecordInfo> list4 = new ArrayList<RecordInfo>();
	HashMap<Integer, UserCallingGrpSeriable> mlistmap = new HashMap<Integer, UserCallingGrpSeriable>();
	private int selectedpoi = 0;
	private ImageView liear_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_operation_record);
		initview();
		group = (RadioGroup) findViewById(R.id.add_radioGourp);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if (arg1 == R.id.type1) {
					selectedpoi = 0;
				} else if (arg1 == R.id.type2) {
					selectedpoi = 1;
				} else if (arg1 == R.id.type3) {
					selectedpoi = 2;
				} else if (arg1 == R.id.type4) {
					selectedpoi = 3;
				}
				initrecorddata(selectedpoi);
			}
		});
	}

	private void initrecorddata(int type) {
		middleshowlist.clear();
		if (selectedshowlist.size() > 0) {
			if (list1.size() > 0 || list2.size() > 0 || list3.size() > 0
					|| list4.size() > 0) {

			} else {
				for (int i = 0; i < selectedshowlist.size(); i++) {
					RecordInfo mRecordInfo = selectedshowlist.get(i);
					int logtype = Integer.valueOf(mRecordInfo.getLogType());
					if (logtype == 1) {
						list1.add(mRecordInfo);
					} else if (logtype == 2) {
						list2.add(mRecordInfo);
					} else if (logtype == 3) {
						list3.add(mRecordInfo);
					} else if (logtype == 4) {
						list4.add(mRecordInfo);
					}
				}
			}
			if (type == 0) {
				middleshowlist.addAll(list1);
			} else if (type == 1) {
				middleshowlist.addAll(list2);
			} else if (type == 2) {
				middleshowlist.addAll(list3);
			} else if (type == 3) {
				middleshowlist.addAll(list4);
			}
			mRecordAdapter.notifyDataSetChanged();
		}
	}

	RecordAdapter mRecordAdapter;
	ListView re_listview;
	int tag = 0;

	private void initview() {
		rl_title_layout = (RelativeLayout) findViewById(R.id.rl_title_layout);// 返回字
		liear_title = (ImageView) findViewById(R.id.liear_title);
		liear_title.setOnClickListener(mclick);
		mTitle_iv = (ImageView) findViewById(R.id.title_iv);
		mTitle_iv.setOnClickListener(mclick);
		mTitle_tv = (TextView) findViewById(R.id.title_tv);
		mTitle_iv.setVisibility(View.VISIBLE);
		mTitle_tv.setText(getString(R.string.recordtitle));
		re_listview = (ListView) findViewById(R.id.me_ringtone_lv);
		mRecordAdapter = new RecordAdapter();
		re_listview.setAdapter(mRecordAdapter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startPbarU();
		new OperationRecordRequest(handler).sendOperationRecordRequest(100, 0);
	}

	private OnClickListener mclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.title_iv:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				finish();
				break;
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(OperationRecordActivity.this,
						MainGroupActivity.class));
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
		case FusionCode.REQUEST_EDITRECORD:
			selectedshowlist.clear();
			OperationRecordResponse entry1 = (OperationRecordResponse) msg.obj;
			selectedshowlist.addAll(entry1.getInfos());
			if (selectedshowlist.size() > 0) {
				tag = 0;
				new QryToneById(handler, FusionCode.REQUEST_QRYCALLERTONEBYID)
						.getQryToneById(selectedshowlist.get(tag).getToneID(),
								"1", "1", 100, 0);
			}
			initrecorddata(selectedpoi);
			break;
		case FusionCode.REQUEST_QRYCALLERTONEBYID:
			QryToneByIdRsp entry2 = (QryToneByIdRsp) msg.obj;
			selectedshowlist.get(tag).setmToneInfo(entry2.list.get(0));
			if (tag >= selectedshowlist.size() - 1) {
				initrecorddata(selectedpoi);
				stopPbarU();
			} else {
				tag++;
				new QryToneById(handler, FusionCode.REQUEST_QRYCALLERTONEBYID)
						.getQryToneById(selectedshowlist.get(tag).getToneID(),
								"1", "1", 100, 0);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 铃音库的adapter
	 * 
	 * @author Administrator
	 * 
	 */
	private class RecordAdapter extends BaseAdapter {
		public int pos = -2;
		public int old;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return middleshowlist.size();
		}

		@Override
		public RecordInfo getItem(int position) {
			// TODO Auto-generated method stub
			return middleshowlist.get(position);
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
				convertView = mInflater.inflate(R.layout.operrecord_item, null);
				holder.tonename = (TextView) convertView
						.findViewById(R.id.tonename);
				holder.phonenumber = (TextView) convertView
						.findViewById(R.id.phonenumber);
				holder.cannel = (TextView) convertView
						.findViewById(R.id.cannel);
				holder.date = (TextView) convertView.findViewById(R.id.date);
				convertView.setTag(holder);
			} else {
				holder = (ToneViewHolder) convertView.getTag();
			}
			if (getCount() > 0 && getItem(position).havetoneInfo()) {
				RecordInfo mRecordInfo = getItem(position);
//				holder.tonename.setText(mRecordInfo.getmToneInfo().getToneName());
				holder.tonename.setText(EllipsizeUtils.textEllpsize(holder.tonename,mRecordInfo.getmToneInfo().getToneName(), mContext,250));	
				if (mRecordInfo.getLogType().equalsIgnoreCase("2")
						|| mRecordInfo.getLogType().equalsIgnoreCase("3")) {
					holder.phonenumber.setText(mRecordInfo.getUserNumber());
				} else {
					holder.phonenumber.setText(UserVariable.CALLNUMBER);
				}
				holder.cannel.setText(mRecordInfo.getChanel());
				holder.date.setText(mRecordInfo.getOperTime());
			}
			return convertView;
		}
	}

	/**
	 * 铃音viewholder
	 * 
	 * @author Administrator
	 * 
	 */
	private class ToneViewHolder {
		private TextView tonename;
		private TextView phonenumber;
		private TextView cannel;
		private TextView date;
	}

}
