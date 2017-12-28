package com.dxiang.mring3.activity;

import java.util.List;

import com.dxiang.mring3.R;
import com.dxiang.mring3.adapter.CustomDialog;
import com.dxiang.mring3.bean.UserToneGrps;
import com.dxiang.mring3.bean.UserToneGrpsMember;
import com.dxiang.mring3.bean.UserToneGrpsSeriable;
import com.dxiang.mring3.request.AddRingGroupRequest;
import com.dxiang.mring3.request.QryToneGrpMemberRequest;
import com.dxiang.mring3.request.QryToneGrpRequest;
import com.dxiang.mring3.request.SetToneReq;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.response.QryToneGrpMemberResp;
import com.dxiang.mring3.response.QryToneGrpResp;
import com.dxiang.mring3.utils.EllipsizeUtils;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RingGroupActivity extends BaseActivity {
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
	private RelativeLayout btn_add;
	boolean showtag = false;
	private PopupWindow mPopWindow;
	private String[] objects;
	private ListView popList;
	private PopListAdapter mPopAdapter;
	// 铃音组集合
	private String[] toneGroup = new String[3];
	private int mCurrentPosition = 2;

	private List<UserToneGrps> mTempInfos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ring_group_layout);
		initView();
		initData();
		initPopWindow();
	}

	private void initData() {
		toneGroup[0] = getResources().getString(R.string.me_pop_set_caller_group);
		toneGroup[1] = getResources().getString(R.string.me_pop_set_called_group);
		toneGroup[2] = null;

	}

	private void initView() {
		btn_addgroup = (Button) findViewById(R.id.btn_addgroup);
		btn_add = (RelativeLayout) findViewById(R.id.btn_add);
		btn_addgroup.setText(getString(R.string.add_ring_grp));
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
		// me_group_list.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		// // 点击时间过快，不操作
		// if (Utils.isFastDoubleClick() || editing) {
		// return;
		// }
		// Intent mintent = new Intent(RingGroupActivity.this,
		// RingGroupDetailActivity.class);
		// Bundle bundle = new Bundle();
		// UserToneGrps grpinfo = UserToneGrpsSeriable.getInstance()
		// .getUserToneGrpsInfo(position);
		// bundle.putString("groupinfo", grpinfo.getUserToneGrpID());
		// bundle.putString("grouptitle", grpinfo.getToneGrpName());
		// mintent.putExtras(bundle);
		// startActivity(mintent);
		// }
		// });
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showtag = false;
		// editing = false;
		seteditstatiu();
		startPbarU();
		// mToneDataList.clear();
		new QryToneGrpRequest(handler).sendQryToneGrpRequest(100, 0);
	}

	private OnClickListener mclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_addgroup:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				Intent intent = new Intent(RingGroupActivity.this, RingGroupAddActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("addgrptype", 0);
				bundle.putBoolean("foradd", true);
				intent.putExtras(bundle);
				intent.putExtra("from", "RingGroupActivity");
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
				startActivity(new Intent(RingGroupActivity.this, MainGroupActivity.class));
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
			mTitle_tv.setText(getString(R.string.tone_ringgroup_txt_edit));
		} else {
			btn_add.setVisibility(View.VISIBLE);
			mEdit_tv.setText(getString(R.string.tone_ringgroup_edit));
			mTitle_tv.setText(getString(R.string.tone_ringgroup_txt));
		}
	}

	int tag = 0;

	@Override
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_SETTONEEVT:
			stopPbarU();
			Toast.makeText(getApplicationContext(), R.string.set_success, Toast.LENGTH_SHORT).show();
			break;
		case FusionCode.REQUEST_QRYGRP:
			UserToneGrpsSeriable.getInstance().cleargroup();
			QryToneGrpResp qryGrpToneRsp = (QryToneGrpResp) msg.obj;
			mTempInfos = qryGrpToneRsp.getToneList();
			if (mTempInfos.size() > 0) {
				mEdit_tv.setVisibility(View.VISIBLE);
				tag = 0;
				UserToneGrpsSeriable.getInstance().grouplist.addAll(mTempInfos);
				UserToneGrps grpinfo = mTempInfos.get(tag);
				new QryToneGrpMemberRequest(handler).sendQryToneGrpMemberRequest(grpinfo.getUserToneGrpID(), 100, 0);
			} else {
				mEdit_tv.setVisibility(View.GONE);
			}
			stopPbarU();
			break;
		case FusionCode.REQUEST_QRYGRPMEMBER:
			QryToneGrpMemberResp qryGrpMemberToneRsp = (QryToneGrpMemberResp) msg.obj;
			List<UserToneGrpsMember> mMemberInfos = qryGrpMemberToneRsp.getToneList();
			UserToneGrpsSeriable.getInstance().getUserToneGrpsInfo(tag).tonegrpInfos.addAll(mMemberInfos);
			if (tag >= UserToneGrpsSeriable.getInstance().getGroupsize() - 1) {
				stopPbarU();
				mgroupAdapter.notifyDataSetChanged();
			} else {
				tag++;
				UserToneGrps grpinfo = UserToneGrpsSeriable.getInstance().getUserToneGrpsInfo(tag);
				new QryToneGrpMemberRequest(handler).sendQryToneGrpMemberRequest(grpinfo.getUserToneGrpID(), 100, 0);
			}
			break;
		case FusionCode.REQUEST_ADDNEWGROUP:
			startPbarU();
			new QryToneGrpRequest(handler).sendQryToneGrpRequest(100, 0);
			break;
		default:
			break;
		}
		if (showtag) {
			showtag = false;
			EntryP entryP = (EntryP) msg.obj;
			if ("400020".equals(entryP.getResult())) {
				Utils.showTextToast(mContext, UtlisReturnCode.ReturnCode(entryP.getResult(), mContext));
			}
		}
	}

	@Override
	protected void reqXmlFail(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlFail(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_ADDNEWGROUP:
			isLoading = false;
			stopPbarU();
			break;
		case FusionCode.REQUEST_SETTONEEVT:
			stopPbarU();
			EntryP entryP = (EntryP) msg.obj;
			Utils.showTextToast(getApplicationContext(), UtlisReturnCode.ReturnCode(entryP.getResult(), mContext));
			break;
		case FusionCode.REQUEST_QRYGRP:
			UserToneGrpsSeriable.getInstance().cleargroup();
			mgroupAdapter.notifyDataSetChanged();
			isLoading = false;
			stopPbarU();
			break;
		case FusionCode.REQUEST_QRYGRPMEMBER:
			QryToneGrpMemberResp qryGrpMemberToneRsp = (QryToneGrpMemberResp) msg.obj;
			List<UserToneGrpsMember> mMemberInfos = qryGrpMemberToneRsp.getToneList();
			UserToneGrpsSeriable.getInstance().getUserToneGrpsInfo(tag).tonegrpInfos.clear();
			if (tag >= UserToneGrpsSeriable.getInstance().getGroupsize() - 1) {
				isLoading = false;
				mgroupAdapter.notifyDataSetChanged();
				stopPbarU();
			} else {
				tag++;
				UserToneGrps grpinfo = UserToneGrpsSeriable.getInstance().getUserToneGrpsInfo(tag);
				new QryToneGrpMemberRequest(handler).sendQryToneGrpMemberRequest(grpinfo.getUserToneGrpID(), 100, 0);
			}
			break;
		}
		if (showtag) {
			showtag = false;
			EntryP entryP = (EntryP) msg.obj;
			if ("400020".equals(entryP.getResult())) {
				Utils.showTextToast(mContext, UtlisReturnCode.ReturnCode(entryP.getResult(), mContext));
			}
		}
	}

	@Override
	protected void reqError(Message msg) {
		// TODO Auto-generated method stub
		super.reqError(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_ADDNEWGROUP:
		case FusionCode.REQUEST_QRYGRP:
		case FusionCode.REQUEST_QRYGRPMEMBER:
			stopPbarU();
			break;
		}
	}

	// private ListView mToneList;
	private void initPopWindow() {
		// TODO Auto-generated method stub
		View contentView = mInflater.inflate(R.layout.tonelist_popwindow_layout, null);
		objects = getResources().getStringArray(R.array.pop_caller_group_crbt_strArry);
		// .getStringArray(R.array.pop_caller_crbt_strArry);
		popList = (ListView) contentView.findViewById(R.id.tonelist_popwindow_lv);

		mPopAdapter = new PopListAdapter();
		popList.setAdapter(mPopAdapter);
		mPopWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopWindow.setOutsideTouchable(true);

		popList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				TextView tv = (TextView) view;
				String itemname = tv.getText().toString();
				//

				// 设置主叫组
				if (getString(R.string.me_pop_set_caller_group).equals(itemname)) {

					startPbarU();

					// (operType,serviceType, settingID, toneID, "0", "0");
					new SetToneReq(handler).sendSetGrpToneReq(mTempInfos.get(mCurrentPosition).getUserToneGrpID(), "2");

					Log.i("UserVariable.CALLNUMBER  ", UserVariable.CALLNUMBER);

				}
				// 设置被叫组
				if (getString(R.string.me_pop_set_called_group).equals(itemname)) {

					new SetToneReq(handler).sendSetGrpToneReq(mTempInfos.get(mCurrentPosition).getUserToneGrpID(), "1");
				}
				mPopWindow.dismiss();

			}
		});
	}

	/**
	 * popwindwo的listview的适配器
	 *
	 * @author Administrator
	 *
	 */
	@SuppressLint("ResourceAsColor")
	private class PopListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return objects.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return objects[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView tv = (TextView) mInflater.inflate(R.layout.tonelist_popwindow_list_item, null);

			tv.setText(objects[position]);
			// String language = manager.get("language", "");
			tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_spain_textview_selector));
			// if(Utils.CheckTextNull(language)){
			// // tv.setText(objects[position]);
			// // tv.setBackgroundResource(R.color.black);
			// // tv.setTextSize(20);
			// }
			return tv;
		}

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
			return UserToneGrpsSeriable.getInstance().getGroupsize();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return UserToneGrpsSeriable.getInstance().getUserToneGrpsInfo(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
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
			UserToneGrps mUserToneGrps = UserToneGrpsSeriable.getInstance().getUserToneGrpsInfo(position);

			// holder.folder_library.setText(mUserToneGrps.getToneGrpName() + "
			// ");

//			holder.folder_library.setText(dealDetailString(holder.folder_library, mUserToneGrps.getToneGrpName()));
			

//			String string=EllipsizeUtils.textEllpsize(holder.folder_library, mUserToneGrps.getToneGrpName(), getApplicationContext(),250);
//			Log.e("返回的string的值是：", string);
			holder.folder_library.setText(EllipsizeUtils.textEllpsize(holder.folder_library, mUserToneGrps.getToneGrpName(), getApplicationContext(),240));
			
//			holder.folder_library.setText( mUserToneGrps.getToneGrpName());
			holder.folder_library.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (!editing) {
						mCurrentPosition = position;

						Log.e("me_group_list", me_group_list.toString());
						Log.e("mPopWindow", mPopWindow.toString());

						mPopWindow.showAtLocation(me_group_list, Gravity.BOTTOM, 0, 0);
						mPopAdapter.notifyDataSetChanged();
					}
				}
			});

			holder.arrow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (Utils.isFastDoubleClick() || editing) {
						return;
					}
					Intent mintent = new Intent(RingGroupActivity.this, RingGroupDetailActivity.class);
					Bundle bundle = new Bundle();
					UserToneGrps grpinfo = UserToneGrpsSeriable.getInstance().getUserToneGrpsInfo(position);
					bundle.putString("groupinfo", grpinfo.getUserToneGrpID());
					bundle.putString("grouptitle", grpinfo.getToneGrpName());
					mintent.putExtras(bundle);
					startActivity(mintent);
				}
			});

			holder.btn_delete.setVisibility(View.GONE);
			if (editing) {
				holder.arrow.setVisibility(View.GONE);
				holder.editlayout.setVisibility(View.VISIBLE);
				if (mUserToneGrps.tonegrpInfos.size() == 0) {
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
				String ellipsizeStr = (String) TextUtils.ellipsize(strs, (TextPaint) paint, ScreenWidth * 5 / 8,
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
										UserToneGrps mUserToneGrps = UserToneGrpsSeriable.getInstance()
												.getUserToneGrpsInfo(position);
										startPbarU();
										showtag = true;
										new AddRingGroupRequest(handler).sendAddRingGroupRequest("3",
												mUserToneGrps.getUserToneGrpID(), mUserToneGrps.getToneGrpName());
									}
								});

						builder.create().show();
					}
				} else if (vid == viewHolder.btn_edit.getId()) {
					UserToneGrps mUserToneGrps = UserToneGrpsSeriable.getInstance().getUserToneGrpsInfo(position);
					Intent intent = new Intent(RingGroupActivity.this, RingGroupAddActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("addgrptype", 0);
					bundle.putString("groupname", mUserToneGrps.getToneGrpName());
					bundle.putString("groupid", mUserToneGrps.getUserToneGrpID());
					bundle.putString("title", getString(R.string.tone_ringgroup_txt_edit));
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
