package com.dxiang.mring3.activity;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.adapter.CustomDialog;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.bean.UserCallingGrps;
import com.dxiang.mring3.bean.UserCallingGrpsMemberInfo;
import com.dxiang.mring3.bean.UserToneGrps;
import com.dxiang.mring3.bean.UserToneSettingInfo;
import com.dxiang.mring3.request.AddCallerNumberToCallerGroupRequest;
import com.dxiang.mring3.request.AddRingtoGroupRsquest;
import com.dxiang.mring3.request.QryToneById;
import com.dxiang.mring3.request.QryToneGrpRequest;
import com.dxiang.mring3.request.QryToneSetReq;
import com.dxiang.mring3.request.UserCallingGrpMemberRequest;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.response.QryToneByIdRsp;
import com.dxiang.mring3.response.QryToneGrpResp;
import com.dxiang.mring3.response.QryToneSetRsp;
import com.dxiang.mring3.response.UserCallingGrpMemberResponse;
import com.dxiang.mring3.utils.EllipsizeUtils;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

public class CallerNumberGroupDetailActivity extends BaseActivity {

	private RelativeLayout rl_title_layout;
	// 右边的编辑
	private TextView mEdit_tv;
	// 返回标题
	private TextView mTitle_tv;
	// 返回图片
	private ImageView mTitle_iv;
	// 是否正在编辑
	private boolean editing = false;
	private TextView setringtonename;
	UserCallingGrps grpcallinginfo;
	Button btn_addtone;
	public static final int ADDNEWTON = 1001;
	public static final int ADDNEWTONRETURN = 1002;
	public static final int ADDNEWUSERNUMBER = 1003;
	public static final int ADDNEWUSERNUMBERRETURN = 1004;
	private ArrayList<String> toneidlist = new ArrayList<String>();
	private ArrayList<String> usedphonelist = new ArrayList<String>();
	public String groupid;
	public String groupname;
	private RelativeLayout setinfo;
	private UserToneSettingInfo calledTone;
	private ImageView liear_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_caller_detail_layout);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			groupid = bundle.getString("groupinfo");
			groupname = bundle.getString("groupname");
			usedphonelist = bundle.getStringArrayList("useedmember");
		}
		grpcallinginfo = new UserCallingGrps();
		initview();
	}

	int tag = 0;
	int deletetag = 0;
	int addtag = 0;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		querytonlistdata();
	}

	private void querytonlistdata() {
		startPbarU();
		new UserCallingGrpMemberRequest(handler).sendUserCallingGrpMemberRequest(groupid, 100, 0);
		new QryToneSetReq(handler, FusionCode.REQUEST_QRYCALLEDTONESETEVT).sendQryToneSetReq(100, 0, "1");
	}

	private List<UserToneGrps> mGrouplist = new ArrayList<UserToneGrps>();

	@Override
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		// 查询被叫铃音
		case FusionCode.REQUEST_QRYCALLEDTONEBYID:// 根据id查询铃音详情
			stopPbarU();
			QryToneByIdRsp entry2 = (QryToneByIdRsp) msg.obj;
			if (entry2.list.size() != 0) {
				ToneInfo grpcallerToneInfo = entry2.list.get(0);
//				setringtonename.setText(grpcallerToneInfo.getToneName());
				setringtonename.setText(EllipsizeUtils.textEllpsize(setringtonename, grpcallerToneInfo.getToneName(), mContext,80));
				
			}
			break;
		case FusionCode.REQUEST_QRYCALLEDTONESETEVT:// 查询组的铃音信息
			stopPbarU();
			QryToneSetRsp qryToneSetRsp2 = (QryToneSetRsp) msg.obj;
			calledTone = qryToneSetRsp2.getcallerringinfo(groupid);
			if (calledTone != null) {
				// 0-铃音 1-铃音组
				if (calledTone.getToneType().equals("0")) {
					QryToneById qry = new QryToneById(handler, FusionCode.REQUEST_QRYCALLEDTONEBYID);
					qry.getQryToneById(calledTone.getToneID(), "1", "1", 100, 0);
				} else if (calledTone.getToneType().equals("1")) {
					new QryToneGrpRequest(handler).sendQryToneGrpRequest(100, 0);
				}
			}
			break;
		case FusionCode.REQUEST_QRYGRP:// 查询铃音组信息
			stopPbarU();
			QryToneGrpResp qryGrpToneRsp = (QryToneGrpResp) msg.obj;
			mGrouplist.clear();
			mGrouplist = qryGrpToneRsp.getToneList();
			if (mGrouplist.size() > 0) {
				for (int m = 0; m < mGrouplist.size(); m++) {
					UserToneGrps mUserToneGrps = mGrouplist.get(m);
					if (mUserToneGrps.getUserToneGrpID()
							.equalsIgnoreCase((calledTone != null) ? calledTone.getToneID() : "0"))
//						setringtonename.setText(mUserToneGrps.getToneGrpName());
					setringtonename.setText(EllipsizeUtils.textEllpsize(setringtonename, mUserToneGrps.getToneGrpName(), mContext,80));

				}
			}
			break;
		case FusionCode.REQUEST_ADDRINGTOGROUP:
			if (deletetag >= (putlist.size() - 1)) {
				querytonlistdata();
				stopPbarU();
			} else {
				deletetag++;
			}
			editing = false;
			seteditstatiu();
			mgroupmemberAdapter.notifyDataSetChanged();
			break;
		case FusionCode.REQUEST_EDITMANAGERCALLINGGROUP:
			grpcallinginfo.callingmemberlist.remove(putlist.get(deletetag));
			if (deletetag >= (putlist.size() - 1)) {
				querytonlistdata();
				stopPbarU();
			} else {
				deletetag++;
			}
			editing = false;
			mgroupmemberAdapter.notifyDataSetChanged();
			break;
		case FusionCode.REQUEST_QRYCALLINGGROUPMEMBER:
			stopPbarU();
			UserCallingGrpMemberResponse qryGrpMemberRsp = (UserCallingGrpMemberResponse) msg.obj;
			List<UserCallingGrpsMemberInfo> mMemberInfos = qryGrpMemberRsp.getToneList();
			grpcallinginfo.callingmemberlist.clear();
			for (int i = 0; i < mMemberInfos.size(); i++) {
				UserCallingGrpsMemberInfo mmember = mMemberInfos.get(i);
				if (mmember.getCallNumber().equalsIgnoreCase("string{}")
						&& mmember.getNumberName().equalsIgnoreCase("string{}")) {
					mMemberInfos.remove(mmember);
				}
			}
			if (mMemberInfos.size() > 0) {
				mEdit_tv.setVisibility(View.VISIBLE);
				grpcallinginfo.callingmemberlist.addAll(mMemberInfos);
			} else {
				mEdit_tv.setVisibility(View.GONE);
			}
			mgroupmemberAdapter.notifyDataSetChanged();
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
		case FusionCode.REQUEST_ADDRINGTOGROUP:
			if (deletetag >= (putlist.size() - 1)) {
				querytonlistdata();
				stopPbarU();
			} else {
				deletetag++;
			}
			editing = false;
			seteditstatiu();
			mgroupmemberAdapter.notifyDataSetChanged();
			break;
		case FusionCode.REQUEST_QRYGRPMEMBER:
		case FusionCode.REQUEST_ADDNEWGROUP:
		case FusionCode.REQUEST_QRYGRP:
			stopPbarU();
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
		case FusionCode.REQUEST_QRYGRPMEMBER:
		case FusionCode.REQUEST_ADDRINGTOGROUP:
		case FusionCode.REQUEST_ADDNEWGROUP:
		case FusionCode.REQUEST_QRYGRP:
		}
		stopPbarU();
	}

	private ListView mToneList;
	ToneAdapter mgroupmemberAdapter;
	TextView number_group;

	private void initview() {
		number_group = (TextView) findViewById(R.id.number_group);
		number_group.setOnClickListener(mclick);
		setringtonename = (TextView) findViewById(R.id.setringtonename);
		setinfo = (RelativeLayout) findViewById(R.id.setinfo);
		mToneList = (ListView) findViewById(R.id.me_group_list);
		btn_addtone = (Button) findViewById(R.id.btn_addgroup);
		btn_addtone.setOnClickListener(mclick);
		rl_title_layout = (RelativeLayout) findViewById(R.id.rl_title_layout);// 返回字
		mEdit_tv = (TextView) findViewById(R.id.tv_edit);// 返回字
		mEdit_tv.setOnClickListener(mclick);
		mTitle_iv = (ImageView) findViewById(R.id.title_iv);
		mTitle_iv.setOnClickListener(mclick);
		mTitle_tv = (TextView) findViewById(R.id.title_tv);
		liear_title = (ImageView) findViewById(R.id.liear_title);
		liear_title.setOnClickListener(mclick);
//		mTitle_tv.setText(dealDetailString(mTitle_tv, groupname));
		mTitle_tv.setText(groupname);
		mTitle_tv.setText(EllipsizeUtils.textEllpsize(mTitle_tv, groupname, mContext,250));
		
		mTitle_iv.setVisibility(View.VISIBLE);
		mEdit_tv.setVisibility(View.VISIBLE);
		mEdit_tv.setText(getString(R.string.group_detail_delete));
		mgroupmemberAdapter = new ToneAdapter();
		mToneList.setAdapter(mgroupmemberAdapter);
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
			String ellipsizeStr = (String) TextUtils.ellipsize(strs, (TextPaint) paint, ScreenWidth * 3 / 5,
					TextUtils.TruncateAt.END);

			return ellipsizeStr;

		}

		return strs;
	}

	private void seteditstatiu() {
		if (editing) {
			setinfo.setVisibility(View.GONE);
			btn_addtone.setText(getString(R.string.group_detail_delete));
		} else {
			setinfo.setVisibility(View.VISIBLE);
			btn_addtone.setText(getString(R.string.callernumber_ringgroup_add_callernumber));
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
				startActivity(new Intent(CallerNumberGroupDetailActivity.this, MainGroupActivity.class));
				finish();
				break;
			case R.id.number_group:
				Intent intent = new Intent(CallerNumberGroupDetailActivity.this, AddGroupRingActivity.class);
				Bundle bundles = new Bundle();
				bundles.putString("groupid", groupid);
				if (calledTone != null) {
					bundles.putString("toneID", calledTone.getToneID());
					bundles.putString("settingID", calledTone.getSettingID());
					bundles.putString("tonetype", calledTone.getToneType());
				}
				intent.putExtras(bundles);
				startActivity(intent);
				break;
			case R.id.btn_addgroup:
				if (editing) {// 删除
					putlist.clear();
					for (int i = 0; i < grpcallinginfo.getCallingGroupMembersize(); i++) {
						if (isSelected.get(i)) {
							putlist.add(grpcallinginfo.getUserCallingGrpsMemberInfo(i));
						}
					}
					if (putlist.size() < 1) {
						Utils.showTextToast(mContext, getString(R.string.check_least_one_contact));
						return;
					}

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
								startPbarU();
								deletetag = 0;
								for (int i = 0; i < putlist.size(); i++) {
									UserCallingGrpsMemberInfo member = putlist.get(i);
									String numbs = member.getCallNumber();
									usedphonelist.remove(numbs.length() < 7 ? "0" + numbs : numbs);
									startPbarU();
									new AddCallerNumberToCallerGroupRequest(handler)
											.sendAddCallerNumberToCallerGroupRequest("2", groupid,
													member.getNumberName(), member.getCallNumber());
								}
								editing = false;
								mEdit_tv.setText(getString(R.string.group_detail_delete));
								mgroupmemberAdapter.notifyDataSetChanged();
								seteditstatiu();
							}
						});

						builder.create().show();
					}
				} else {// 添加新成员
					if (Utils.isFastDoubleClick()) {
						return;
					}
					toneidlist.clear();
					Intent mintent = new Intent(CallerNumberGroupDetailActivity.this, SelectCallerNumberActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("groupid", groupid);
					bundle.putStringArrayList("useedmember", usedphonelist);
					mintent.putExtras(bundle);
					// startActivity(mintent);
					startActivityForResult(mintent, ADDNEWUSERNUMBER);
				}
				break;
			case R.id.title_iv:
				if (mEdit_tv.getText().toString().equalsIgnoreCase(getString(R.string.group_detail_delete_all))
						|| mEdit_tv.getText().toString()
								.equalsIgnoreCase(getString(R.string.group_detail_delete_none))) {
					editing = false;
					mEdit_tv.setText(getString(R.string.group_detail_delete));
					mgroupmemberAdapter.notifyDataSetChanged();
					seteditstatiu();
				} else {
					finish();
				}
				break;
			case R.id.tv_edit:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				if (mEdit_tv.getText().toString().equalsIgnoreCase(getString(R.string.group_detail_delete))) {// 初始未选中状态
					editing = true;
					mEdit_tv.setText(getString(R.string.group_detail_delete_all));
					mgroupmemberAdapter.initDate(false);
				} else if (mEdit_tv.getText().toString()
						.equalsIgnoreCase(getString(R.string.group_detail_delete_all))) {// 选中状态为All的时候
					mgroupmemberAdapter.initDate(true);
					mEdit_tv.setText(getString(R.string.group_detail_delete_none));
				} else if (mEdit_tv.getText().toString()
						.equalsIgnoreCase(getString(R.string.group_detail_delete_none))) {// 选中状态为None的时候
					mgroupmemberAdapter.initDate(false);
					mEdit_tv.setText(getString(R.string.group_detail_delete_all));
				}
				seteditstatiu();
				mgroupmemberAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}

		}
	};
	private int checkedsize = 0;
	private List<UserCallingGrpsMemberInfo> putlist = new ArrayList<UserCallingGrpsMemberInfo>();
	private HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();

	/**
	 * 铃音库的adapter
	 * 
	 * @author Administrator
	 * 
	 */
	private class ToneAdapter extends BaseAdapter {
		public int pos = -2;
		public int old;

		// 初始化isSelected的数据
		private void initDate(boolean seltag) {
			if (grpcallinginfo != null)
				for (int i = 0; i < grpcallinginfo.getCallingGroupMembersize(); i++) {
					getIsSelected().put(i, seltag);
				}
			if (seltag) {
				checkedsize = grpcallinginfo.getCallingGroupMembersize();
			} else {
				checkedsize = 0;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return grpcallinginfo.getCallingGroupMembersize();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return grpcallinginfo.getUserCallingGrpsMemberInfo(position);
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
				convertView = mInflater.inflate(R.layout.callermember_item, null);
				holder.membername = (TextView) convertView.findViewById(R.id.tone_item_songname_tv);
				holder.phonenumber = (TextView) convertView.findViewById(R.id.tone_item_songername_tv);
				holder.ck_rember = (CheckBox) convertView.findViewById(R.id.ck_rember);
				convertView.setTag(holder);
			} else {
				holder = (ToneViewHolder) convertView.getTag();
			}
			final int temp = position;

			if (grpcallinginfo != null) {
				if (grpcallinginfo.getCallingGroupMembersize() > 0) {
					if (editing) {
						holder.ck_rember.setVisibility(View.VISIBLE);
						holder.ck_rember.setChecked(getIsSelected().get(position));
						holder.ck_rember.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (getIsSelected().get(temp)) {
									getIsSelected().put(temp, false);
									checkedsize--;
									if (checkedsize < getCount()) {
										mEdit_tv.setText(getString(R.string.group_detail_delete_all));
									}
								} else {
									getIsSelected().put(temp, true);
									checkedsize++;
									if (checkedsize == getCount()) {
										mEdit_tv.setText(getString(R.string.group_detail_delete_none));
									}
								}
							}
						});
					} else {
						holder.ck_rember.setVisibility(View.GONE);
					}
					UserCallingGrpsMemberInfo mUserCallingGrpsMemberInfo = grpcallinginfo
							.getUserCallingGrpsMemberInfo(temp);
//					holder.membername.setText(mUserCallingGrpsMemberInfo.getNumberName());
					holder.membername.setText(EllipsizeUtils.textEllpsize(holder.membername, mUserCallingGrpsMemberInfo.getNumberName(), mContext,250));
					
//					holder.phonenumber.setText(mUserCallingGrpsMemberInfo.getCallNumber());
					holder.phonenumber.setText(EllipsizeUtils.textEllpsize(holder.phonenumber,mUserCallingGrpsMemberInfo.getCallNumber(), mContext,250));
				}
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

	/**
	 * 铃音viewholder
	 * 
	 * @author Administrator
	 * 
	 */
	private class ToneViewHolder {
		private CheckBox ck_rember;
		private TextView membername;
		private TextView phonenumber;
	}

	ArrayList<String> idlist = new ArrayList<String>();

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ADDNEWUSERNUMBER:
			if (ADDNEWUSERNUMBERRETURN == resultCode) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					if (bundle.getStringArrayList("select") != null) {
						usedphonelist.addAll(bundle.getStringArrayList("select"));
					}
				}
			}
			break;
		case ADDNEWTON:
			if (ADDNEWTONRETURN == resultCode) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					idlist.clear();
					idlist.addAll(bundle.getStringArrayList("checkidlist"));
					deletetag = 0;
					for (int i = 0; i < idlist.size(); i++) {
						new AddRingtoGroupRsquest(handler).sendAddRingtoGroupRsquest(FusionCode.REQUEST_ADDRINGTOGROUP,
								"1", groupid, idlist.get(i));
					}
				}
			}
			break;
		}
	}
}
