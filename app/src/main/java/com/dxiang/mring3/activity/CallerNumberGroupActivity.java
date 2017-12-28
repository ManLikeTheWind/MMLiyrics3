package com.dxiang.mring3.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.dxiang.mring3.R;
import com.dxiang.mring3.adapter.CustomDialog;
import com.dxiang.mring3.bean.UserCallingGrpSeriable;
import com.dxiang.mring3.bean.UserCallingGrps;
import com.dxiang.mring3.bean.UserCallingGrpsMemberInfo;
import com.dxiang.mring3.request.ManagerCallerGroupRequest;
import com.dxiang.mring3.request.QueryCallingGrpRequest;
import com.dxiang.mring3.request.UserCallingGrpMemberRequest;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.response.QueryCallingGrpResponse;
import com.dxiang.mring3.response.UserCallingGrpMemberResponse;
import com.dxiang.mring3.utils.EllipsizeUtils;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

public class CallerNumberGroupActivity extends BaseActivity {

	private RelativeLayout rl_title_layout;
	// 返回标题
	private TextView mTitle_tv;
	// 右边的编辑
	private TextView mEdit_tv;
	// 返回图片
	private ImageView mTitle_iv;
	// 是否正在编辑
	private boolean editing = false;
	/** 是否在请求数据 **/
	private boolean isLoading = false;
	private ListView me_group_list;
	// 分类集合
	private GrpAdapter mgroupAdapter;
	private Button btn_addgroup;
	private ImageView liear_title;
	ArrayList<String> phonelist = new ArrayList<String>();
	private RelativeLayout btn_add;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ring_group_layout);
		initView();
	}

	private void initView() {
		btn_add = (RelativeLayout) findViewById(R.id.btn_add);
		btn_addgroup = (Button) findViewById(R.id.btn_addgroup);
		btn_addgroup.setText(getString(R.string.caller_add_ring_grp));
		btn_addgroup.setOnClickListener(mclick);
		me_group_list = (ListView) findViewById(R.id.me_group_list);
		rl_title_layout = (RelativeLayout) findViewById(R.id.rl_title_layout);// 返回字
		mTitle_iv = (ImageView) findViewById(R.id.title_iv);
		mTitle_iv.setOnClickListener(mclick);
		mTitle_tv = (TextView) findViewById(R.id.title_tv);
		mEdit_tv = (TextView) findViewById(R.id.tv_edit);
		liear_title = (ImageView) findViewById(R.id.liear_title);
		liear_title.setOnClickListener(mclick);
		mEdit_tv.setOnClickListener(mclick);
		seteditstatiu();
		mgroupAdapter = new GrpAdapter();
		me_group_list.setAdapter(mgroupAdapter);
		me_group_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				// 点击时间过快，不操作
				if (Utils.isFastDoubleClick() || editing) {
					return;
				}
				Intent mintent = new Intent(CallerNumberGroupActivity.this, CallerNumberGroupDetailActivity.class);
				Bundle bundle = new Bundle();
				UserCallingGrps grpinfo = UserCallingGrpSeriable.getInstance().getUserCallingGrpsInfo(position);
				bundle.putStringArrayList("useedmember", phonelist);
				bundle.putString("groupname", grpinfo.getCallingName());
				bundle.putString("groupinfo", grpinfo.getCallingGrpID());
				mintent.putExtras(bundle);
				startActivity(mintent);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// editing = false;
		// seteditstatiu();
		startPbarU();
		new QueryCallingGrpRequest(handler).sendQueryCallingGrpRequest(100, 0);
	}

	private OnClickListener mclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_addgroup:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				Intent intent = new Intent(CallerNumberGroupActivity.this, RingGroupAddActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("addgrptype", 1);
				bundle.putBoolean("foradd", true);
				intent.putExtras(bundle);
				intent.putExtra("from", "CallerNumberGroupActivity");
				startActivity(intent);
				break;
			case R.id.title_iv:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				finish();
				break;
			case R.id.tv_edit:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				editing = !editing;
				seteditstatiu();
				mgroupAdapter.notifyDataSetChanged();
				break;
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(CallerNumberGroupActivity.this, MainGroupActivity.class));
				finish();
				break;
			default:
				break;
			}

		}
	};

	private void seteditstatiu() {
		mTitle_iv.setVisibility(View.VISIBLE);
		mEdit_tv.setVisibility(View.VISIBLE);
		if (editing) {
			btn_add.setVisibility(View.GONE);
			mEdit_tv.setText(getString(R.string.tone_ringgroup_noedit));
			mTitle_tv.setText(getString(R.string.callernumber_ringgroup_txt_edit));
//			mTitle_tv.setText(EllipsizeUtils.textEllpsize(mTitle_tv, getString(R.string.callernumber_ringgroup_txt_edit), mContext,250));
			
		} else {
			btn_add.setVisibility(View.VISIBLE);
			mEdit_tv.setText(getString(R.string.tone_ringgroup_edit));
			mTitle_tv.setText(getString(R.string.callernumber_ringgroup_txt));
//			mTitle_tv.setText(EllipsizeUtils.textEllpsize(mTitle_tv, getString(R.string.callernumber_ringgroup_txt), mContext,250));
		}
	}

	int tag = 0;

	@Override
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QRYCALLINGGROUP:
			phonelist.clear();
			UserCallingGrpSeriable.getInstance().cleargroup();
			QueryCallingGrpResponse callingrpresponse = (QueryCallingGrpResponse) msg.obj;
			List<UserCallingGrps> mTempInfos = callingrpresponse.getToneList();
			if (mTempInfos.size() > 0) {
				mEdit_tv.setVisibility(View.VISIBLE);
				tag = 0;
				UserCallingGrpSeriable.getInstance().grouplist.addAll(mTempInfos);
				UserCallingGrps grpinfo = mTempInfos.get(tag);
				new UserCallingGrpMemberRequest(handler).sendUserCallingGrpMemberRequest(grpinfo.getCallingGrpID(), 100,
						0);
			} else {
				mEdit_tv.setVisibility(View.GONE);
			}
			break;
		case FusionCode.REQUEST_QRYCALLINGGROUPMEMBER:
			UserCallingGrpMemberResponse qryGrpMemberRsp = (UserCallingGrpMemberResponse) msg.obj;
			List<UserCallingGrpsMemberInfo> mMemberInfos = qryGrpMemberRsp.getToneList();
			for (int i = 0; i < mMemberInfos.size(); i++) {
				UserCallingGrpsMemberInfo mmember = mMemberInfos.get(i);
				if (mmember.getCallNumber().equalsIgnoreCase("string{}")
						&& mmember.getNumberName().equalsIgnoreCase("string{}")) {
					mMemberInfos.remove(mmember);
				}
			}
			if (mMemberInfos.size() > 0) {
				UserCallingGrpSeriable.getInstance().getUserCallingGrpsInfo(tag).callingmemberlist.addAll(mMemberInfos);
			}
			if (tag >= UserCallingGrpSeriable.getInstance().getGroupsize() - 1) {
				mgroupAdapter.notifyDataSetChanged();
				stopPbarU();
				for (int i = 0; i < UserCallingGrpSeriable.getInstance().getGroupsize(); i++) {
					UserCallingGrps mUserCallingGrps = UserCallingGrpSeriable.getInstance().getUserCallingGrpsInfo(i);
					for (int m = 0; m < mUserCallingGrps.getCallingGroupMembersize(); m++) {
						UserCallingGrpsMemberInfo mUserCallingGrpsMemberInfo = mUserCallingGrps
								.getUserCallingGrpsMemberInfo(m);
						phonelist.add(mUserCallingGrpsMemberInfo.getCallNumber());
					}
				}
			} else {
				tag++;
				UserCallingGrps grpinfo = UserCallingGrpSeriable.getInstance().getUserCallingGrpsInfo(tag);
				new UserCallingGrpMemberRequest(handler).sendUserCallingGrpMemberRequest(grpinfo.getCallingGrpID(), 100,
						0);
			}
			break;
		case FusionCode.REQUEST_MANAGERCALLINGGROUP:
			startPbarU();
			new QueryCallingGrpRequest(handler).sendQueryCallingGrpRequest(100, 0);
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
		case FusionCode.REQUEST_QRYCALLINGGROUP:
			UserCallingGrpSeriable.getInstance().cleargroup();
			mgroupAdapter.notifyDataSetChanged();
			isLoading = false;
			stopPbarU();
			break;
		case FusionCode.REQUEST_MANAGERCALLINGGROUP:
		case FusionCode.REQUEST_QRYGRP:
			isLoading = false;
			stopPbarU();
			break;
		case FusionCode.REQUEST_QRYCALLINGGROUPMEMBER:
			UserCallingGrpMemberResponse qryGrpMemberRsp = (UserCallingGrpMemberResponse) msg.obj;
			List<UserCallingGrpsMemberInfo> mMemberInfos = qryGrpMemberRsp.getToneList();
			UserCallingGrpSeriable.getInstance().getUserCallingGrpsInfo(tag).callingmemberlist.clear();
			if (tag >= UserCallingGrpSeriable.getInstance().getGroupsize() - 1) {
				isLoading = false;
				mgroupAdapter.notifyDataSetChanged();
				stopPbarU();
			} else {
				tag++;
				UserCallingGrps grpinfo = UserCallingGrpSeriable.getInstance().getUserCallingGrpsInfo(tag);
				new UserCallingGrpMemberRequest(handler).sendUserCallingGrpMemberRequest(grpinfo.getCallingGrpID(), 100,
						0);
			}
			break;
		}
		EntryP entryP = (EntryP) msg.obj;
		if (!"400020".equals(entryP.getResult())) {
			Utils.showTextToast(mContext, UtlisReturnCode.ReturnCode(entryP.getResult(), mContext));
		}
	}

	@Override
	protected void reqError(Message msg) {
		// TODO Auto-generated method stub
		super.reqError(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_MANAGERCALLINGGROUP:
		case FusionCode.REQUEST_QRYGRP:
		case FusionCode.REQUEST_QRYGRPMEMBER:
		}
		stopPbarU();
	}

	/**
	 * 铃音库的adapter
	 * 
	 * @author Administrator
	 * 
	 */
	private class GrpAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return UserCallingGrpSeriable.getInstance().getGroupsize();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return UserCallingGrpSeriable.getInstance().getUserCallingGrpsInfo(position);
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
				convertView = mInflater.inflate(R.layout.me_ring_group_item, null);
				holder.folder_library = (TextView) convertView.findViewById(R.id.folder_library);
				holder.arrow = (ImageView) convertView.findViewById(R.id.showrightarrow);
				holder.editlayout = (LinearLayout) convertView.findViewById(R.id.foredit);
				holder.btn_delete = (ImageButton) convertView.findViewById(R.id.btn_delete);
				holder.btn_edit = (ImageButton) convertView.findViewById(R.id.btn_edit);
				convertView.setTag(holder);
			} else {
				holder = (ToneViewHolder) convertView.getTag();
			}
			UserCallingGrps mUserCallingGrps = UserCallingGrpSeriable.getInstance().getUserCallingGrpsInfo(position);
//			holder.folder_library.setText(dealDetailString(holder.folder_library, mUserCallingGrps.getCallingName()));
			holder.folder_library.setText( EllipsizeUtils.textEllpsize(holder.folder_library, mUserCallingGrps.getCallingName(), mContext,240) );
//			EllipsizeUtils.textEllpsize(holder.folder_library, mUserCallingGrps.getCallingName(), mContext,250);
			holder.btn_delete.setVisibility(View.GONE);
			if (editing) {
				holder.arrow.setVisibility(View.GONE);
				holder.editlayout.setVisibility(View.VISIBLE);
				if (mUserCallingGrps.getCallingGroupMembersize() == 0) {
					holder.btn_delete.setVisibility(View.VISIBLE);
					holder.btn_delete.setOnClickListener(new buttonListener(holder, position));
				} else {
					holder.btn_delete.setVisibility(View.GONE);
				}
				holder.btn_edit.setOnClickListener(new buttonListener(holder, position));
			} else {
				holder.arrow.setVisibility(View.VISIBLE);
				holder.editlayout.setVisibility(View.GONE);
			}
			return convertView;
		}

		/**
		 * 解决某些手机尾部省略号只显示一个点或两个点的问题
		 * 
		 * @param folder_library
		 * @param mUserToneGrps
		 * @return
		 */
		public String dealDetailString(TextView folder_library, String grouptitle) {
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			String strs = grouptitle;
			int ScreenWidth = metrics.widthPixels;
			TextPaint paint = folder_library.getPaint();
			float strwidth = paint.measureText(strs);

			if (strwidth > ScreenWidth / 2) {
				// 根据长度截取出剪裁后的文字
				String ellipsizeStr = (String) TextUtils.ellipsize(strs, (TextPaint) paint, ScreenWidth * 2 / 3,
						TextUtils.TruncateAt.END);

				return ellipsizeStr;

			}

			return strs;
		}

		class buttonListener implements OnClickListener {
			private int position;
			private ToneViewHolder viewHolder;

			public buttonListener(ToneViewHolder vh, int pos) {
				viewHolder = vh;
				position = pos;
			}

			public void onClick(View view) {
				int vid = view.getId();
				if (vid == viewHolder.btn_delete.getId()) {
					if (!Utils.isFastDoubleClick()) {
						CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
						builder.setMessage(R.string.suredelete, Gravity.CENTER);
						builder.setTitle(R.string.user_cancellation);
						builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});

						builder.setNegativeButton(R.string.determine,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										UserCallingGrps mUserCallingGrps = UserCallingGrpSeriable.getInstance()
												.getUserCallingGrpsInfo(position);
										startPbarU();
										new ManagerCallerGroupRequest(handler).sendAddCallerGroupRequest("3",
												mUserCallingGrps.getCallingGrpID(), mUserCallingGrps.getCallingName());
									}
								});

						builder.create().show();
					}
				} else if (vid == viewHolder.btn_edit.getId()) {
					UserCallingGrps mUserCallingGrps = UserCallingGrpSeriable.getInstance()
							.getUserCallingGrpsInfo(position);
					Intent intent = new Intent(CallerNumberGroupActivity.this, RingGroupAddActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("addgrptype", 1);
					bundle.putString("groupname", mUserCallingGrps.getCallingName());
					bundle.putString("groupid", mUserCallingGrps.getCallingGrpID());
					bundle.putBoolean("foradd", false);
					intent.putExtras(bundle);
					startActivity(intent);
					// editing = false;
				}
			}
		}

	}

	private class ToneViewHolder {
		private TextView folder_library;
		private ImageView arrow;
		private LinearLayout editlayout;
		private ImageButton btn_delete;
		private ImageButton btn_edit;
	}

}
