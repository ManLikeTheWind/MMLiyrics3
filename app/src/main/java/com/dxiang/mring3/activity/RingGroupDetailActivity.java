package com.dxiang.mring3.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gem.imgcash.ImageDownLoader;
import com.dxiang.mring3.R;
import com.dxiang.mring3.adapter.CustomDialog;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.bean.UserToneGrps;
import com.dxiang.mring3.bean.UserToneGrpsMember;
import com.dxiang.mring3.bean.UserToneGrpsSeriable;
import com.dxiang.mring3.request.AddRingGroupRequest;
import com.dxiang.mring3.request.QryToneById;
import com.dxiang.mring3.request.QryToneGrpMemberRequest;
import com.dxiang.mring3.request.AddRingtoGroupRsquest;
import com.dxiang.mring3.request.QryToneGrpRequest;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.response.QryToneByIdRsp;
import com.dxiang.mring3.response.QryToneGrpMemberResp;
import com.dxiang.mring3.response.QryToneGrpResp;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.EllipsizeUtils;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.LocalMediaPlayer;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;
import com.dxiang.mring3.utils.LocalMediaPlayer.Complete;
import com.dxiang.mring3.utils.LocalMediaPlayer.ErrorListener;
import com.dxiang.mring3.utils.LocalMediaPlayer.onException;
import com.dxiang.mring3.utils.LocalMediaPlayer.onPrepared;
import com.dxiang.mring3.view.RoundedImageView;

public class RingGroupDetailActivity extends BaseActivity {
	private RelativeLayout rl_title_layout;
	// 右边的编辑
	private TextView mEdit_tv;
	// 返回标题
	private TextView mTitle_tv;
	// 返回图片
	private ImageView mTitle_iv;
	// 是否正在编辑
	private boolean editing = false;
	UserToneGrps grpinfo;
	Button btn_addtone;
	private boolean isPlayer = false;// 已经在播放
	private LocalMediaPlayer mediaPlayer;
	public static final int ADDNEWTON = 1001;
	public static final int ADDNEWTONRETURN = 1002;
	private ArrayList<String> toneidlist = new ArrayList<String>();
	public String groupid;
	public String grouptitle;
	private ImageView liear_title;
	private LinearLayout mLLNoRing;
	private Bundle bundle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ring_group_layout);
		bundle = getIntent().getExtras();
		if (bundle != null) {
			groupid = bundle.getString("groupinfo");
			grouptitle = bundle.getString("grouptitle");

		}
		initview();
	}

	int tag = 0;
	int deletetag = 0;
	int addtag = 0;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initMediaPlayer();
		querytonlistdata();
	}

	private void querytonlistdata() {
		startPbarU();
		new QryToneGrpMemberRequest(handler).sendQryToneGrpMemberRequest(
				groupid, 100, 0);
	}

	@Override
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_ADDRINGTOGROUP:
			if (deletetag >= (idlist.size() - 1)) {
				stopPbarU();
				querytonlistdata();
			} else {
				deletetag++;
			}
			editing = false;
			mgroupmemberAdapter.notifyDataSetChanged();
			break;
		case FusionCode.REQUEST_DELETERINGTOGROUP:
			grpinfo.tonegrpInfos.remove(putlist.get(deletetag));
			if (deletetag >= (putlist.size() - 1)) {
				// querytonlistdata();
				stopPbarU();
				if (grpinfo.tonegrpInfos.size() == 0) {
					mToneList.setVisibility(View.GONE);
					mLLNoRing.setVisibility(View.VISIBLE);
					mEdit_tv.setVisibility(View.GONE);
				}
			} else {
				deletetag++;
			}
			editing = false;
			mgroupmemberAdapter.notifyDataSetChanged();
			break;
		case FusionCode.REQUEST_QRYCALLERTONEBYID:
			QryToneByIdRsp entry1 = (QryToneByIdRsp) msg.obj;
			if (entry1.list.size() != 0) {
				grpinfo.tonegrpInfos.get(tag).tonInfoslist.add(entry1.list
						.get(0));
			} else {
				grpinfo.tonegrpInfos.get(tag).tonInfoslist.clear();
			}
			if (tag >= grpinfo.tonegrpInfos.size() - 1) {
				stopPbarU();
				mgroupmemberAdapter.notifyDataSetChanged();
			} else {
				tag++;
				new QryToneById(handler, FusionCode.REQUEST_QRYCALLERTONEBYID)
						.getQryToneById(grpinfo.tonegrpInfos.get(tag)
								.getToneID(), "1", "1", 100, 0);
			}
			break;
		case FusionCode.REQUEST_QRYGRPMEMBER:
			stopPbarU();
			QryToneGrpMemberResp qryGrpMemberToneRsp = (QryToneGrpMemberResp) msg.obj;
			List<UserToneGrpsMember> mMemberInfos = qryGrpMemberToneRsp
					.getToneList();
			grpinfo = new UserToneGrps();
			grpinfo.tonegrpInfos.clear();
			grpinfo.tonegrpInfos.addAll(mMemberInfos);
			grpinfo.setUserToneGrpID(groupid);
			if (mMemberInfos.size() == 0) {
				mToneList.setVisibility(View.GONE);
				mLLNoRing.setVisibility(View.VISIBLE);
				mEdit_tv.setVisibility(View.GONE);
				return;
			} else {
				mToneList.setVisibility(View.VISIBLE);
				mLLNoRing.setVisibility(View.GONE);
				if (bundle.getString("flag") != null
						&& bundle.getString("flag").equals("hide")) {
					mEdit_tv.setVisibility(View.GONE);
				} else {
					mEdit_tv.setVisibility(View.VISIBLE);
				}

			}
			if (grpinfo.tonegrpInfos.size() > 0) {
				tag = 0;
				startPbarU();
				new QryToneById(handler, FusionCode.REQUEST_QRYCALLERTONEBYID)
						.getQryToneById(grpinfo.tonegrpInfos.get(tag)
								.getToneID(), "1", "1", 100, 0);
			} else {
				mgroupmemberAdapter.notifyDataSetChanged();
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
		case FusionCode.REQUEST_ADDRINGTOGROUP:
			if (deletetag >= (idlist.size() - 1)) {
				stopPbarU();
				querytonlistdata();
			} else {
				deletetag++;
			}
			break;
		case FusionCode.REQUEST_DELETERINGTOGROUP:
			if (deletetag >= (putlist.size() - 1)) {
				stopPbarU();
				querytonlistdata();
			} else {
				deletetag++;
			}
			break;
		case FusionCode.REQUEST_QRYCALLERTONEBYID:
			grpinfo.tonegrpInfos.get(tag).tonInfoslist.clear();
			if (tag >= grpinfo.tonegrpInfos.size() - 1) {
				mgroupmemberAdapter.notifyDataSetChanged();
				stopPbarU();
			} else {
				tag++;
				UserToneGrps grpinfo = UserToneGrpsSeriable.getInstance()
						.getUserToneGrpsInfo(tag);
				new QryToneById(handler, FusionCode.REQUEST_QRYCALLERTONEBYID)
						.getQryToneById(grpinfo.tonegrpInfos.get(tag)
								.getToneID(), "1", "1", 100, 0);
			}
			break;
		case FusionCode.REQUEST_ADDNEWGROUP:
		case FusionCode.REQUEST_QRYGRP:
			stopPbarU();
			break;
		case FusionCode.REQUEST_QRYGRPMEMBER:
			QryToneGrpMemberResp qryGrpMemberToneRsp = (QryToneGrpMemberResp) msg.obj;
			List<UserToneGrpsMember> mMemberInfos = qryGrpMemberToneRsp
					.getToneList();
			grpinfo = new UserToneGrps();
			grpinfo.tonegrpInfos.clear();
			if (mMemberInfos.size() == 0) {
				stopPbarU();
				mToneList.setVisibility(View.GONE);
				mLLNoRing.setVisibility(View.VISIBLE);
				mEdit_tv.setVisibility(View.GONE);
				return;
			} else {
				mToneList.setVisibility(View.VISIBLE);
				mLLNoRing.setVisibility(View.GONE);
				mEdit_tv.setVisibility(View.VISIBLE);
			}
			stopPbarU();
			if (!"400020".equals(((EntryP) msg.obj).getResult())) {
				grpinfo.tonegrpInfos.addAll(mMemberInfos);
				grpinfo.setUserToneGrpID(groupid);
				if (grpinfo.tonegrpInfos.size() > 0) {
					tag = 0;
					startPbarU();
					new QryToneById(handler,
							FusionCode.REQUEST_QRYCALLERTONEBYID)
							.getQryToneById(grpinfo.tonegrpInfos.get(tag)
									.getToneID(), "1", "1", 100, 0);
				} else {
					mgroupmemberAdapter.notifyDataSetChanged();
				}
			}
			break;
		}
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
		case FusionCode.REQUEST_QRYGRPMEMBER:
		case FusionCode.REQUEST_ADDRINGTOGROUP:
		case FusionCode.REQUEST_ADDNEWGROUP:
		case FusionCode.REQUEST_QRYGRP:
			stopPbarU();
			break;
		}
	}

	private ListView mToneList;
	ToneAdapter mgroupmemberAdapter;

	private void initview() {
		mLLNoRing = (LinearLayout) findViewById(R.id.me_no_ring_ll);
		mToneList = (ListView) findViewById(R.id.me_group_list);
		btn_addtone = (Button) findViewById(R.id.btn_addgroup);
		btn_addtone.setText(getString(R.string.add_ring_tone));
		btn_addtone.setOnClickListener(mclick);
		rl_title_layout = (RelativeLayout) findViewById(R.id.rl_title_layout);// 返回字
		mEdit_tv = (TextView) findViewById(R.id.tv_edit);// 返回字
		mEdit_tv.setOnClickListener(mclick);
		mTitle_iv = (ImageView) findViewById(R.id.title_iv);
		mTitle_iv.setOnClickListener(mclick);
		mTitle_tv = (TextView) findViewById(R.id.title_tv);
		liear_title = (ImageView) findViewById(R.id.liear_title);
		liear_title.setOnClickListener(mclick);
		// mTitle_tv.setText(dealDetailString(mTitle_tv, grouptitle));
		mTitle_tv.setText(EllipsizeUtils.textEllpsize(mTitle_tv, grouptitle,
				mContext, -1));
		mTitle_iv.setVisibility(View.VISIBLE);
		mEdit_tv.setVisibility(View.VISIBLE);
		mEdit_tv.setText(getString(R.string.group_detail_delete));
		mgroupmemberAdapter = new ToneAdapter();
		mToneList.setAdapter(mgroupmemberAdapter);

		if (bundle.getString("flag") != null
				&& bundle.getString("flag").equals("hide")) {
			btn_addtone.setVisibility(View.GONE);
			mEdit_tv.setVisibility(View.GONE);
		}
	}

	private void seteditstatiu() {
		if (editing) {
			btn_addtone.setText(getString(R.string.group_detail_delete));
		} else {
			btn_addtone.setText(getString(R.string.add_ring_tone));
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
				startActivity(new Intent(RingGroupDetailActivity.this,
						MainGroupActivity.class));
				finish();
				break;
			case R.id.btn_addgroup:
				if (editing) {// 为了删除
					putlist.clear();
					for (int i = 0; i < grpinfo.getMembersize(); i++) {
						if (isSelected.get(i)) {
							putlist.add(grpinfo.getUserToneGrpsMember(i));
						}
					}
					if (putlist.size() < 1) {
						Utils.showTextToast(mContext,
								getString(R.string.check_least_one_new));
						return;
					}
					if (!Utils.isFastDoubleClick()) {
						CustomDialog.Builder builder = new CustomDialog.Builder(
								mContext);
						builder.setMessage(R.string.suredelete, Gravity.CENTER);
						builder.setTitle(R.string.user_cancellation);
						builder.setPositiveButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});

						builder.setNegativeButton(
								R.string.determine,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										startPbarU();
										deletetag = 0;
										for (int i = 0; i < putlist.size(); i++) {
											UserToneGrpsMember member = putlist
													.get(i);
											new AddRingtoGroupRsquest(handler)
													.sendAddRingtoGroupRsquest(
															FusionCode.REQUEST_DELETERINGTOGROUP,
															"2",
															grpinfo.getUserToneGrpID(),
															member.getToneID());
										}
										editing = false;
										mEdit_tv.setText(getString(R.string.group_detail_delete));
										mgroupmemberAdapter
												.notifyDataSetChanged();
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
					if (grpinfo != null)
						for (int i = 0; i < grpinfo.getMembersize(); i++) {
							toneidlist.add(grpinfo.getUserToneGrpsMember(i)
									.getToneID());
						}
					Intent intent = new Intent(RingGroupDetailActivity.this,
							MyRingForSelectActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("groupid", groupid);
					bundle.putStringArrayList("groupinfo", toneidlist);
					intent.putExtras(bundle);
					startActivityForResult(intent, ADDNEWTON);
				}
				break;
			case R.id.title_iv:
				if (mEdit_tv
						.getText()
						.toString()
						.equalsIgnoreCase(
								getString(R.string.group_detail_delete_all))
						|| mEdit_tv
								.getText()
								.toString()
								.equalsIgnoreCase(
										getString(R.string.group_detail_delete_none))) {
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
				if (mEdit_tv
						.getText()
						.toString()
						.equalsIgnoreCase(
								getString(R.string.group_detail_delete))
						&& ((grpinfo != null) ? grpinfo.tonegrpInfos.size() : 0) > 0) {// 初始未选中状态
					editing = true;
					mEdit_tv.setText(getString(R.string.group_detail_delete_all));
					mgroupmemberAdapter.initDate(false);
				} else if (mEdit_tv
						.getText()
						.toString()
						.equalsIgnoreCase(
								getString(R.string.group_detail_delete_all))) {// 选中状态为All的时候
					mgroupmemberAdapter.initDate(true);
					mEdit_tv.setText(getString(R.string.group_detail_delete_none));
				} else if (mEdit_tv
						.getText()
						.toString()
						.equalsIgnoreCase(
								getString(R.string.group_detail_delete_none))) {// 选中状态为None的时候
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

	private List<UserToneGrpsMember> putlist = new ArrayList<UserToneGrpsMember>();
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
		private void initDate(boolean isselect) {
			if (grpinfo != null)
				for (int i = 0; i < grpinfo.tonegrpInfos.size(); i++) {
					getIsSelected().put(i, isselect);
				}
			if (isselect) {
				checkedsize = grpinfo.tonegrpInfos.size();
			} else {
				checkedsize = 0;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return (grpinfo != null) ? grpinfo.tonegrpInfos.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return grpinfo.tonegrpInfos.get(position);
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
				convertView = mInflater.inflate(
						R.layout.activity_ringgroup_tonelistitem, null);
				holder.imagebg = (RoundedImageView) convertView
						.findViewById(R.id.imagebg);
				holder.playIV = (ImageView) convertView
						.findViewById(R.id.music_play_stop_loading);
				holder.songNameTV = (TextView) convertView
						.findViewById(R.id.tone_item_songname_tv);
				holder.songerNameTV = (TextView) convertView
						.findViewById(R.id.tone_item_songername_tv);
				holder.loadingIV = (ProgressBar) convertView
						.findViewById(R.id.music_loading);
				holder.showrightarrow = (ImageView) convertView
						.findViewById(R.id.showrightarrow);
				holder.ck_rember = (CheckBox) convertView
						.findViewById(R.id.ck_rember);
				convertView.setTag(holder);
			} else {
				holder = (ToneViewHolder) convertView.getTag();
			}
			final int temp = position;
			holder.showrightarrow.setVisibility(View.GONE);
			if (grpinfo != null) {
				if (grpinfo.tonegrpInfos.size() > 0) {
					if (editing) {
						holder.ck_rember.setVisibility(View.VISIBLE);

						holder.ck_rember.setChecked(getIsSelected().get(
								position));
						holder.ck_rember
								.setOnClickListener(new OnClickListener() {
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
						// holder.showrightarrow.setVisibility(View.VISIBLE);
					}
					if (grpinfo.tonegrpInfos.get(position).tonInfoslist.size() > 0) {
						ToneInfo toneInfo = grpinfo.tonegrpInfos.get(position).tonInfoslist
								.get(0);
						String saveDir = Commons.getImageSavedpath()
								+ toneInfo.getToneClassID();
						if (new File(saveDir).exists()) {
							holder.imagebg.setImageBitmap(ImageDownLoader
									.getShareImageDownLoader().showCacheBitmap(
											saveDir));
						}
						if (null != toneInfo) {
							holder.songerNameTV.setText(toneInfo.getSingerName());

							holder.songNameTV.setText(EllipsizeUtils.textEllpsize(holder.songNameTV, toneInfo.getToneName(), getApplicationContext(),250));
//							EllipsizeUtils.textEllpsize(holder.songNameTV, toneInfo.getToneName(), getApplicationContext(),250);
						}
						holder.playIV
								.setOnClickListener(new PlayOnclickListener(
										temp));
						if (pos == position) {
							if (isPlayer) {
								holder.loadingIV.setVisibility(View.INVISIBLE);
								holder.playIV
										.setBackgroundResource(R.drawable.music_stop);
							} else {
								holder.playIV.setBackgroundResource(0);
								holder.loadingIV.setVisibility(View.VISIBLE);
							}
						} else {
							holder.loadingIV.setVisibility(View.INVISIBLE);
							holder.playIV
									.setBackgroundResource(R.drawable.music_play);
						}
					}
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
		private RoundedImageView imagebg;
		private CheckBox ck_rember;
		private ImageView showrightarrow;
		private ImageView playIV;
		private ProgressBar loadingIV;
		private TextView songNameTV;
		private TextView songerNameTV;
	}

	/**
	 * 播放暂停按钮的监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private class PlayOnclickListener implements OnClickListener {
		private int temp;

		public PlayOnclickListener(int temp) {
			this.temp = temp;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// 防止重复点击
			if (Utils.isFastDoubleClick()) {
				return;
			}
			int oldPos = mgroupmemberAdapter.pos;
			mgroupmemberAdapter.pos = temp;
			ToneInfo info = null;
			info = grpinfo.tonegrpInfos.get(temp).tonInfoslist.get(0);
			// 暂停后继续播放
			if (mediaPlayer.isPause() && -1 == oldPos
					&& mgroupmemberAdapter.old == temp) {
				mediaPlayer.startPlayer();
				mgroupmemberAdapter.pos = temp;
				isPlayer = true;
				return;
			}
			if (oldPos == temp || oldPos == -2 || oldPos == -1) {
				if (oldPos != temp) {
					mediaPlayer.cancelPlayer();
					isPlayer = false;
					mediaPlayer.getUrlFromServer(info.getToneID(),
							info.getToneType());
					mgroupmemberAdapter.pos = temp;
					mgroupmemberAdapter.notifyDataSetChanged();
					return;
				}
				if (mediaPlayer.isprepared()) {
					return;
				}
				if (!isPlayer) {

					// new
					// GetToneListenAddrReq(p_h).sendGetToneListenAddrReq(toneID,
					// toneType);
					// 发起播放请求
					mediaPlayer.getUrlFromServer(info.getToneID(),
							info.getToneType());
					// mediaPlayer.prepare();
					mgroupmemberAdapter.pos = temp;
					mToneList.setStackFromBottom(false);
					mgroupmemberAdapter.notifyDataSetChanged();
				} else {
					// 保存暂停的位置
					mgroupmemberAdapter.old = mgroupmemberAdapter.pos;
					mgroupmemberAdapter.pos = -1;
					mToneList.setStackFromBottom(false);
					mgroupmemberAdapter.notifyDataSetChanged();
					mediaPlayer.pause();
					isPlayer = false;
				}
			} else {
				mediaPlayer.cancelPlayer();
				isPlayer = false;
				mediaPlayer.setDataSource(info.getTonePreListenAddress());
				mediaPlayer.getUrlFromServer(info.getToneID(),
						info.getToneType());
				mgroupmemberAdapter.pos = temp;
				mgroupmemberAdapter.notifyDataSetChanged();
			}
		}

	}

	/**
	 * 初始化播放器
	 */
	private void initMediaPlayer() {
		mediaPlayer = LocalMediaPlayer.getInstance();
		mediaPlayer.setCallback(onComplete);
		mediaPlayer.setOnPrepared(onPrepred);
		mediaPlayer.setException(exception);
		mediaPlayer.setErrorListener(errorListener);
	}

	/**
	 * 播放完成
	 */
	Complete onComplete = new Complete() {

		@Override
		public void onComplete() {
			mgroupmemberAdapter.pos = -1;
			mgroupmemberAdapter.notifyDataSetChanged();
			isPlayer = false;
		}

	};

	/**
	 * 准备播放
	 */
	onPrepared onPrepred = new onPrepared() {

		@Override
		public void onPrepared() {
			if (mgroupmemberAdapter.pos != -1 && mgroupmemberAdapter.pos != -2) {
				isPlayer = true;
				mgroupmemberAdapter.notifyDataSetChanged();

			}
		}
	};

	/**
	 * 播放器出错回调
	 */
	ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onError(MediaPlayer arg0, int arg1, int arg2) {
			stopMusic();
			mgroupmemberAdapter.pos = -1;
			mgroupmemberAdapter.notifyDataSetChanged();
			isPlayer = false;
		}

	};

	/**
	 * 停止播放
	 */
	private void stopMusic() {
		isPlayer = false;
		mgroupmemberAdapter.pos = -1;
		mgroupmemberAdapter.notifyDataSetChanged();
		if (mediaPlayer == null) {
			return;
		}
		mediaPlayer.cancelPlayer();
	}

	/**
	 * 播发器的exception
	 */
	onException exception = new onException() {

		@Override
		public void onException(int code, String des) {
			if (Utils.CheckTextNull(des)) {
				Utils.showTextToast(RingGroupDetailActivity.this,
						UtlisReturnCode.ReturnCode(des, mContext));
			}
			stopMusic();
			mgroupmemberAdapter.notifyDataSetChanged();
			mgroupmemberAdapter.pos = -1;
			isPlayer = false;
		}

	};

	@Override
	public void onStop() {
		super.onStop();
		if (mediaPlayer == null) {
			return;
		}
		// 如果正在播放就暂停，否则停止
		if (mediaPlayer.isPlaying()
				|| mediaPlayer.getState() == LocalMediaPlayer.PAUSE) {
			pause();
		} else {
			stopMusic();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mediaPlayer != null) {
			mediaPlayer.uninsMedia();
			mediaPlayer = null;
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mediaPlayer == null) {
			return;
		}
		if (mediaPlayer.isPlaying()
				|| mediaPlayer.getState() == LocalMediaPlayer.PAUSE) {
			pause();
		} else {
			stopMusic();
		}
	}

	private void pause() {
		if (mediaPlayer.getState() != LocalMediaPlayer.PAUSE) {
			mgroupmemberAdapter.old = mgroupmemberAdapter.pos;
			mgroupmemberAdapter.pos = -1;
			mgroupmemberAdapter.notifyDataSetChanged();
			mediaPlayer.pause();
			isPlayer = false;
		}
	}

	ArrayList<String> idlist = new ArrayList<String>();

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ADDNEWTON:
			// if (ADDNEWTONRETURN == resultCode) {
			// Bundle bundle = data.getExtras();
			// if (bundle != null) {
			// idlist.clear();
			// idlist.addAll(bundle.getStringArrayList("checkidlist"));
			// deletetag = 0;
			// for (int i = 0; i < idlist.size(); i++) {
			// new AddRingtoGroupRsquest(handler)
			// .sendAddRingtoGroupRsquest(
			// FusionCode.REQUEST_ADDRINGTOGROUP, "1",
			// groupid, idlist.get(i));
			// }
			// }
			// }
			break;
		}
	}
}
