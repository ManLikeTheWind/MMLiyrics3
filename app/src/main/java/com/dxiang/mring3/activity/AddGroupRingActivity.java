package com.dxiang.mring3.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.gem.imgcash.ImageDownLoader;
import com.dxiang.mring3.R;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.bean.UserToneGrps;
import com.dxiang.mring3.bean.UserToneSettingInfo;
import com.dxiang.mring3.request.DelToneSetReq;
import com.dxiang.mring3.request.QryToneGrpRequest;
import com.dxiang.mring3.request.QryToneSetReq;
import com.dxiang.mring3.request.QryUserToneReq;
import com.dxiang.mring3.request.SetToneReq;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.response.QryToneGrpResp;
import com.dxiang.mring3.response.QryToneSetRsp;
import com.dxiang.mring3.response.QryUserToneRsp;
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

public class AddGroupRingActivity extends BaseActivity {

	private RelativeLayout rl_title_layout;
	// 返回标题
	private TextView mTitle_tv;
	// 返回图片
	private ImageView mTitle_iv;
	private ListView mToneList, group_list;
	private int mToneCount = 0;
	private boolean mRefreshAll = true;
	private int showNum = 1000;
	private int pageNo = 0;
	// 上次请求的数据数目，用于判断是否可以再获取更多
	private int mLastDataCount = 0;
	// 铃音集合
	private List<ToneInfo> mToneDataList = new ArrayList<ToneInfo>();
	private LinearLayout mLLNoRing;
	/** 是否在请求数据 **/
	private boolean isLoading = false;
	private LocalMediaPlayer mediaPlayer;
	private boolean isPlayer = false;// 已经在播放
	private ToneAdapter mToneAdapter;
	private GrpAdapter mgroupAdapter;
	private String[] objects;
	private int mCurrentPosition = -1;
	private int mTaskCount = 4;
	private TextView mylibrary_tv, ringgroup_tv;
	private List<UserToneGrps> mGrouplist = new ArrayList<UserToneGrps>();
	private String groupid;
	private boolean havering = false;
	private String toneID;
	private String tonetype;// (0-铃音 1-铃音组)
	private String settingID;
	private ImageView liear_title;
	private String settingmiddleid;
	private int tag = 1;
	private UserToneSettingInfo calledTone;
	private boolean selected = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addgroupring);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			groupid = bundle.getString("groupid");
			toneID = bundle.getString("toneID", null);
			settingID = bundle.getString("settingID", null);
			tonetype = bundle.getString("tonetype", null);
			if (settingID != null) {
				havering = true;
			} else {
				havering = false;
			}
		}
		initView();
		init();
		setListener();
		initListHeader();
	}

	private void initView() {
		rl_title_layout = (RelativeLayout) findViewById(R.id.rl_title_layout);// 返回字
		mTitle_iv = (ImageView) findViewById(R.id.title_iv);
		mTitle_iv.setOnClickListener(mclick);
		mTitle_tv = (TextView) findViewById(R.id.title_tv);
		mToneList = (ListView) findViewById(R.id.me_ringtone_lv);
		group_list = (ListView) findViewById(R.id.group_list);
		mLLNoRing = (LinearLayout) findViewById(R.id.me_no_ring_ll);
		mylibrary_tv = (TextView) findViewById(R.id.mylibrary_tv);
		mylibrary_tv.setOnClickListener(mclick);
		ringgroup_tv = (TextView) findViewById(R.id.ringgroup_tv);
		ringgroup_tv.setOnClickListener(mclick);
		liear_title = (ImageView) findViewById(R.id.liear_title);
		liear_title.setOnClickListener(mclick);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initMediaPlayer();
		mToneDataList.clear();
		startPbarU();
		new QryUserToneReq(handler).sendQryUserToneReq(showNum, 0);
		new QryToneGrpRequest(handler).sendQryToneGrpRequest(100, 0);
		mQryToneSetReq();
	}

	private void mQryToneSetReq() {
		new QryToneSetReq(handler, FusionCode.REQUEST_QRYCALLEDTONESETEVT)
				.sendQryToneSetReq(100, 0, "1");
	}

	private void init() {
		mTitle_iv.setVisibility(View.VISIBLE);
		mTitle_tv.setText(Utils.getResouceStr(mContext, R.string.addgroupring));
	}

	private void updateviewtag() {
		Drawable drawabledown = getResources().getDrawable(
				R.drawable.arrow_down);
		drawabledown.setBounds(0, 0, drawabledown.getMinimumWidth(),
				drawabledown.getMinimumHeight()); // 设置边界
		Drawable drawableup = getResources().getDrawable(R.drawable.arrow_up);
		drawableup.setBounds(0, 0, drawableup.getMinimumWidth(),
				drawableup.getMinimumHeight()); // 设置边界
		if (mToneList.getVisibility() == View.VISIBLE) {
			mToneList.setVisibility(View.GONE);
			group_list.setVisibility(View.VISIBLE);
			mylibrary_tv.setCompoundDrawables(null, null, drawabledown, null);// 画在右边
			ringgroup_tv.setCompoundDrawables(null, null, drawableup, null);// 画在右边
		} else if (group_list.getVisibility() == View.VISIBLE) {
			mToneList.setVisibility(View.VISIBLE);
			group_list.setVisibility(View.GONE);
			mylibrary_tv.setCompoundDrawables(null, null, drawableup, null);// 画在右边
			ringgroup_tv.setCompoundDrawables(null, null, drawabledown, null);// 画在右边
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
				startActivity(new Intent(AddGroupRingActivity.this,
						MainGroupActivity.class));
				finish();
				break;
			case R.id.title_iv:
				finish();
				break;
			case R.id.mylibrary_tv:
			case R.id.ringgroup_tv:
				updateviewtag();
				break;
			default:
				break;
			}

		}
	};

	/**
	 * 初始化listview的头部和尾部，用来展示主叫被叫彩铃和刷新
	 */
	private void initListHeader() {
		mToneAdapter = new ToneAdapter();
		mToneList.setAdapter(mToneAdapter);
		mgroupAdapter = new GrpAdapter();
		group_list.setAdapter(mgroupAdapter);
	}

	@Override
	protected void reqXmlSucessed(Message msg) {
		// TODO Auto-generated method stub
		//
		super.reqXmlSucessed(msg);
		EntryP entryP = (EntryP) msg.obj;
		switch (msg.arg1) {
		
		case FusionCode.REQUEST_QRYCALLEDTONESETEVT:// 查询组的铃音信息
			stopPbarU();
			QryToneSetRsp qryToneSetRsp2 = (QryToneSetRsp) msg.obj;
			calledTone = qryToneSetRsp2.getcallerringinfo(groupid);
			if (calledTone != null) {
				settingID = calledTone.getSettingID();
			}
			break;
		case FusionCode.REQUEST_DELTONESETEVT:
			mQryToneSetReq();
			if (tag == 1)
				new SetToneReq(handler).sendSetGrpToneReq("1", groupid,
						toneID_middle, "0");
			else if (tag == 2)
				new SetToneReq(handler).sendSetGrpToneReq("1", groupid,
						toneID_middle, "1");
			break;
		case FusionCode.REQUEST_SETTONEEVT:
			// settingID = toneID_middle;
			selected = false;
			stopPbarU();
			finish();
			toneID = toneID_middle;
			tonetype = tonetype_middle;
			mToneAdapter.notifyDataSetChanged();
			mgroupAdapter.notifyDataSetChanged();
			break;
		// 成功返回个人铃音库，只第一次发起主叫被叫铃音请求
		case FusionCode.REQUEST_QRYUSERTONEEVT:
			stopPbarU();
			pageNo++;
			QryUserToneRsp qryUserToneRsp = (QryUserToneRsp) msg.obj;
			List<ToneInfo> mTempInfos = qryUserToneRsp.getToneList();
			mLastDataCount = mTempInfos.size();
			mToneCount = mToneCount + mTempInfos.size();
			mToneDataList.addAll(mTempInfos);
			// mToneAdapter.notifyDataSetChanged();
			// if (mToneDataList.size() == 0) {
			// mToneList.setVisibility(View.GONE);
			// mLLNoRing.setVisibility(View.VISIBLE);
			// return;
			// } else {
			// mToneList.setVisibility(View.VISIBLE);
			// mLLNoRing.setVisibility(View.GONE);
			// }
			if (mRefreshAll) {
				pageNo = 1;
				mRefreshAll = false;
			} else {
				isLoading = false;
				// moreView.setVisibility(View.GONE);
				stopMusic();
				mToneAdapter.notifyDataSetChanged();
			}
			mToneAdapter.notifyDataSetChanged();
			// new SetToneReq(p_h).sendSetToneReq("", "", "", "", "0", "0");
			break;
		case FusionCode.REQUEST_QRYGRP:// 查询铃音组信息
			stopPbarU();
			mGrouplist.clear();
			QryToneGrpResp qryGrpToneRsp = (QryToneGrpResp) msg.obj;
			mGrouplist = qryGrpToneRsp.getToneList();
			if (mGrouplist.size() > 0) {
				mgroupAdapter.notifyDataSetChanged();
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
		EntryP entryPs = (EntryP) msg.obj;
		switch (msg.arg1) {
		case FusionCode.REQUEST_SETTONEEVT:
			selected = false;
			stopPbarU();
			break;
		case FusionCode.REQUEST_QRYUSERTONEEVT:
			stopPbarU();
			if (pageNo == 0) {
				mToneList.setVisibility(View.GONE);
				mLLNoRing.setVisibility(View.VISIBLE);
			}
			if (!mRefreshAll) {
				isLoading = false;
			}
			break;
		case FusionCode.REQUEST_QRYGRP:
			isLoading = false;
			stopPbarU();
			break;
		}
		stopPbarU();
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
		case FusionCode.REQUEST_QRYUSERTONEEVT:
		case FusionCode.REQUEST_QRYGRP:
		}
		stopPbarU();
	}

	/**
	 * 刷新页面，mTaskCount计算任务数量
	 */
	private synchronized void refreshList() {
		mTaskCount--;
		if (mTaskCount == 0) {
			mToneList.setVisibility(View.VISIBLE);
			mToneList.setAdapter(mToneAdapter);
			stopMusic();
			mToneAdapter.notifyDataSetChanged();
			mTaskCount = 4;
			stopPbarU();
			mToneList.setVisibility(View.VISIBLE);
		}
		// initListHeader();
	}

	/**
	 * 铃音库的adapter
	 * 
	 * @author Administrator
	 * 
	 */
	private class ToneAdapter extends BaseAdapter {
		public int pos = -2;
		public int old;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mToneDataList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mToneDataList.get(position);
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
						R.layout.addgroupring_ring_item, null);
				holder.playIV = (ImageView) convertView
						.findViewById(R.id.music_play_stop_loading);
				holder.imagebg = (RoundedImageView) convertView
						.findViewById(R.id.imagebg);
				holder.songNameTV = (TextView) convertView
						.findViewById(R.id.tone_item_songname_tv);
				holder.songerNameTV = (TextView) convertView
						.findViewById(R.id.tone_item_songername_tv);
				holder.loadingIV = (ProgressBar) convertView
						.findViewById(R.id.music_loading);
				holder.checked = (ImageView) convertView
						.findViewById(R.id.checked);
				convertView.setTag(holder);
			} else {
				holder = (ToneViewHolder) convertView.getTag();
			}
			final int temp = position;
			holder.checked.setVisibility(View.GONE);
			ToneInfo toneInfo = mToneDataList.get(position);
			String saveDir = Commons.getImageSavedpath()
					+ toneInfo.getToneClassID();
			if (new File(saveDir).exists()) {
				holder.imagebg.setImageBitmap(ImageDownLoader
						.getShareImageDownLoader().showCacheBitmap(saveDir));
			}
			if (tonetype != null)
				if (tonetype.equalsIgnoreCase("0")) {
					if (toneID.equals(toneInfo.getToneID())) {
						holder.checked.setVisibility(View.VISIBLE);
					}
				}
			if (null != toneInfo) {
//				holder.songerNameTV.setText(toneInfo.getSingerName());
				holder.songerNameTV.setText(EllipsizeUtils.textEllpsize(holder.songerNameTV, toneInfo.getSingerName(), mContext,250));
				
//				holder.songNameTV.setText(toneInfo.getToneName());
				holder.songNameTV.setText(EllipsizeUtils.textEllpsize(holder.songNameTV, toneInfo.getToneName(), mContext,250));
			}
			holder.playIV.setOnClickListener(new PlayOnclickListener(temp));
			if (pos == position) {
				if (isPlayer) {
					holder.loadingIV.setVisibility(View.INVISIBLE);
					holder.playIV.setBackgroundResource(R.drawable.music_stop);

				} else {
					holder.playIV.setBackgroundResource(0);
					if (holder.loadingIV.getAnimation() == null) {
						// holder.loadingIV.setAnimation(animation);
						// animation.startNow();
					}
					holder.loadingIV.setVisibility(View.VISIBLE);
				}
			} else {
				holder.loadingIV.setVisibility(View.INVISIBLE);
				holder.playIV.setBackgroundResource(R.drawable.music_play);
				// if(pos == 0){
				// loadingIv2.setAnimation(null);
				// loadingIv2.setVisibility(View.INVISIBLE);
				// playIv2.setBackgroundResource(R.drawable.music_play);
				// }
				// if(pos == 1){
				// loadingIv1.setAnimation(null);
				// loadingIv1.setVisibility(View.INVISIBLE);
				// playIv1.setBackgroundResource(R.drawable.music_play);
				// }
				// 注
				// if (null != mCalledLL && pos != -4) {
				// loadingIv2.setAnimation(null);
				// loadingIv2.setVisibility(View.INVISIBLE);
				// mHandler.sendEmptyMessage(4);
				// // playIv2.setBackgroundResource(R.drawable.music_play);
				// }
				// if (null != mCallerLL && pos != -3) {
				// loadingIv1.setAnimation(null);
				// loadingIv1.setVisibility(View.INVISIBLE);
				// mHandler.sendEmptyMessage(6);
				//
				// }

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
		private RoundedImageView imagebg;
		private ImageView playIV;
		private ProgressBar loadingIV;
		private TextView songNameTV;
		private TextView songerNameTV;
		private ImageView checked;
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
			int oldPos = mToneAdapter.pos;
			mToneAdapter.pos = temp;
			ToneInfo info = null;
			info = mToneDataList.get(temp);
			// 暂停后继续播放
			if (mediaPlayer.isPause() && -1 == oldPos
					&& mToneAdapter.old == temp) {
				mediaPlayer.startPlayer();
				mToneAdapter.pos = temp;
				isPlayer = true;
				return;
			}
			if (oldPos == temp || oldPos == -2 || oldPos == -1) {
				if (oldPos != temp) {
					mediaPlayer.cancelPlayer();
					isPlayer = false;
					mediaPlayer.getUrlFromServer(info.getToneID(),
							info.getToneType());
					mToneAdapter.pos = temp;
					mToneAdapter.notifyDataSetChanged();
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
					mToneAdapter.pos = temp;
					mToneList.setStackFromBottom(false);
					mToneAdapter.notifyDataSetChanged();

				} else {
					// 保存暂停的位置
					mToneAdapter.old = mToneAdapter.pos;
					mToneAdapter.pos = -1;
					mToneList.setStackFromBottom(false);
					mToneAdapter.notifyDataSetChanged();
					mediaPlayer.pause();
					isPlayer = false;
				}
			} else {
				mediaPlayer.cancelPlayer();
				isPlayer = false;
				mediaPlayer.setDataSource(info.getTonePreListenAddress());
				mediaPlayer.getUrlFromServer(info.getToneID(),
						info.getToneType());
				mToneAdapter.pos = temp;
				mToneAdapter.notifyDataSetChanged();
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
			mToneAdapter.pos = -1;
			mToneAdapter.notifyDataSetChanged();
			isPlayer = false;
		}

	};

	/**
	 * 准备播放
	 */
	onPrepared onPrepred = new onPrepared() {

		@Override
		public void onPrepared() {
			if (mToneAdapter.pos != -1 && mToneAdapter.pos != -2) {
				isPlayer = true;
				mToneAdapter.notifyDataSetChanged();

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
			mToneAdapter.pos = -1;
			mToneAdapter.notifyDataSetChanged();
			isPlayer = false;
		}

	};

	/**
	 * 停止播放
	 */
	private void stopMusic() {
		isPlayer = false;
		mToneAdapter.pos = -1;
		mToneAdapter.notifyDataSetChanged();
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
				Utils.showTextToast(AddGroupRingActivity.this,
						UtlisReturnCode.ReturnCode(des, mContext));
			}
			stopMusic();
			mToneAdapter.notifyDataSetChanged();
			mToneAdapter.pos = -1;
			isPlayer = false;
		}

	};

	private void deleteoldring() {
		new DelToneSetReq(handler).sendDelToneSetReq(settingID);
	}

	String toneID_middle = "";
	String tonetype_middle = "";

	private void setListener() {
		mToneList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// 点击时间过快，不操作
				if (Utils.isFastDoubleClick()) {
					return;
				}
				if (!selected) {
					selected = true;
					tag = 1;
					if (havering)
						deleteoldring();
					ToneInfo mToneInfo = (ToneInfo) mToneAdapter
							.getItem(position);
					toneID_middle = mToneInfo.getToneID();
					tonetype_middle = "0";
					if (!havering)
						new SetToneReq(handler).sendSetGrpToneReq("1", groupid,
								toneID_middle, "0");
				}
			}
		});

		group_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// 点击时间过快，不操作
				if (Utils.isFastDoubleClick()) {
					return;
				}
				if (!selected) {
					selected = true;
					tag = 2;
					if (havering)
						deleteoldring();
					UserToneGrps mUserToneGrps = mgroupAdapter
							.getItem(position);
					toneID_middle = mUserToneGrps.getUserToneGrpID();
					tonetype_middle = "1";
					if (!havering)
						new SetToneReq(handler).sendSetGrpToneReq("1", groupid,
								mUserToneGrps.getUserToneGrpID(), "1");
				}
			}
		});
	}

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
			mToneAdapter.old = mToneAdapter.pos;
			mToneAdapter.pos = -1;
			mToneAdapter.notifyDataSetChanged();
			mediaPlayer.pause();
			isPlayer = false;
		}
	}

	private class GrpAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mGrouplist.size();
		}

		@Override
		public UserToneGrps getItem(int position) {
			// TODO Auto-generated method stub
			return mGrouplist.get(position);
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
			GroupHolder holder = null;
			if (null == convertView) {
				holder = new GroupHolder();
				convertView = mInflater.inflate(
						R.layout.addgroupring_group_item, null);
				holder.folder_library = (TextView) convertView
						.findViewById(R.id.folder_library);
				holder.checked = (ImageView) convertView
						.findViewById(R.id.checked);
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}
			holder.checked.setVisibility(View.GONE);
			UserToneGrps mUserToneGrps = getItem(position);
			if (tonetype != null)
				if (tonetype.equalsIgnoreCase("1")) {
					if (toneID.equals(mUserToneGrps.getUserToneGrpID())) {
						holder.checked.setVisibility(View.VISIBLE);
					}
				}
//			holder.folder_library.setText(mUserToneGrps.getToneGrpName());
			holder.folder_library.setText(EllipsizeUtils.textEllpsize(holder.folder_library, mUserToneGrps.getToneGrpName(), mContext,250));
			return convertView;
		}
	}

	private class GroupHolder {
		private TextView folder_library;
		private ImageView checked;
	}
}
