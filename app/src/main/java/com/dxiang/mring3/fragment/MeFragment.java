package com.dxiang.mring3.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gem.imgcash.ImageDownLoader;
import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.CallerNumberGroupActivity;
import com.dxiang.mring3.activity.DetailsRingtoneActivity;
import com.dxiang.mring3.activity.MyringLibraryActivity;
import com.dxiang.mring3.activity.OperationRecordActivity;
import com.dxiang.mring3.activity.RingGroupActivity;
import com.dxiang.mring3.activity.RingGroupDetailActivity;
import com.dxiang.mring3.activity.UserCenterActivity;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.bean.UserToneGrps;
import com.dxiang.mring3.bean.UserToneGrpsSeriable;
import com.dxiang.mring3.bean.UserToneSettingInfo;
import com.dxiang.mring3.camera.CameraLib;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.DelToneReq;
import com.dxiang.mring3.request.DelToneSetReq;
import com.dxiang.mring3.request.QryPlayModeRequest;
import com.dxiang.mring3.request.QryToneById;
import com.dxiang.mring3.request.QryToneGrpRequest;
import com.dxiang.mring3.request.QryToneSetReq;
import com.dxiang.mring3.request.QryUserToneReq;
import com.dxiang.mring3.request.SetToneReq;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.response.GetToneListenAddrRsp;
import com.dxiang.mring3.response.QryPlayModeResp;
import com.dxiang.mring3.response.QryToneByIdRsp;
import com.dxiang.mring3.response.QryToneGrpResp;
import com.dxiang.mring3.response.QryToneSetRsp;
import com.dxiang.mring3.response.QryUserToneRsp;
import com.dxiang.mring3.utils.BitmapHelper;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.EllipsizeUtils;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.LocalMediaPlayer;
import com.dxiang.mring3.utils.LocalMediaPlayer.Complete;
import com.dxiang.mring3.utils.LocalMediaPlayer.ErrorListener;
import com.dxiang.mring3.utils.LocalMediaPlayer.onException;
import com.dxiang.mring3.utils.LocalMediaPlayer.onPrepared;
import com.dxiang.mring3.utils.TimeUtil;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;
import com.dxiang.mring3.view.RoundedImageView;

/**
 * me頁面的Fragment
 * 
 * @author Administrator
 * 
 */
public class MeFragment extends BaseFragment implements OnScrollListener {
	private static final String TAG = "MeFragment";
	private static final int CALLER = 0;
	private static final int CALLED = 1;
	private static final int LIBRARY = 2;
	private static final int HEADSET = 3;

	// 铃音列表组件
	private ListView mToneList;
	// 右箭头
	private ImageView mIvMeInfo;
	// 返回键
	private ImageView mIvBack;
	// 标题
	// private TextView mTitle;
	// 个人信息头部布局
	private RelativeLayout rl_me_user;
	// 长按弹出的popwindow
	private PopupWindow mPopWindow;
	// 铃音集合
	private List<ToneInfo> mToneDataList;

	private String[] objects;

	private String[] catalog_objects;

	private SharedPreferenceService manager;
	/**
	 * 如果为1 表示的是铃音组
	 */
	private String caller_toneType;
	private String called_toneType;

	/**
	 * 如果为2表示随机
	 */
	private String setType;
	ListView popList;
	private PopListAdapter mPopAdapter;
	private TextView mTvPhoneNo;
	// private RotateAnimation animation;
	private LocalMediaPlayer mediaPlayer;
	private boolean isPlayer = false;// 已经在播放
	private ToneAdapter mToneAdapter;

	private ImageView playIv1;
	private ImageView playIv2;
	private ProgressBar loadingIv1;
	private ProgressBar loadingIv2;

	private int flag;
	private int mCurrentPosition = -1;
	private ToneInfo mCalledToneInfo;
	private ToneInfo mCallerToneInfo;
	private View hearder;
	private int mTaskCount = 4;
	private LinearLayout mCallerLL;
	private LinearLayout mCallerLL_group;
	private LinearLayout mCalledLL_group;
	private String caller_group_id;
	private String called_group_id;
	private LinearLayout mCalledLL;
	private TextView callerSongNameTV;
	private TextView arror_group_mCallerLL;
	private TextView arror_group_mCalledLL;
	private TextView callerSongerNameTV;
	private TextView calledSongerNameTV;
	private TextView calledSongNameTV;
	private TextView callertone_end_time;
	private TextView calledtone_end_time;
	private RoundedImageView calledtoneimg;
	private RoundedImageView callertoneimg;
	private String mCallerSetId;
	private String mcalledSetId;
	private UserToneSettingInfo mcallerSettingIfo;
	private UserToneSettingInfo mcalledSettingIfo;
	private String mdeleteSettingIfo;
	private View moreView;
	private int lastItem;
	private int mToneCount = 0;
	private boolean mRefreshAll = true;
	private int showNum = 10;
	private int pageNo = 0;
	private Button footImageView;
	private RotateAnimation rotateAnimation;
	private String url = "";
	// 上次请求的数据数目，用于判断是否可以再获取更多
	private int mLastDataCount = 0;
	// private TextView mTvMyLibrary;
	private View hearder1;
	private View hearder2;
	private LinearLayout mLLNoRing;
	private LinearLayout nowifi;
	/** 是否在请求数据 **/
	private boolean isLoading = false;
	private TextView mTvOprator;
	private RoundedImageView iv_logo;
	public static final int TOMYRING = 2001;
	public static final int RINGGROUP = 2002;
	private LinearLayout tonelist_called_random_ll;
	private TextView mCallerLL_group_name, mCalledLL_group_name;
	private List<UserToneGrps> mTempInfos;
	private boolean mTempInfosFirst = false;
	// 跳转RingGroupDetailActivity
	private String groupid_er;
	private String grouptitle_er;
	private String groupid_ed;
	private String grouptitle_ed;
	private String deleteTone;

	private Handler mHandler = new Handler() {

		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				// 澶勭悊璇曞惉鍔犺浇鏁堟灉
				playIv1.setBackgroundResource(0);
				// if (loadingIv1.getAnimation() == null) {
				// loadingIv1.setAnimation(animation);
				// animation.startNow();
				// }
				loadingIv1.setVisibility(View.VISIBLE);
			} else if (msg.what == 2) {
				// 澶勭悊璇曞惉鍔犺浇鏁堟灉
				playIv2.setBackgroundResource(0);
				// if (loadingIv2.getAnimation() == null) {
				// loadingIv2.setAnimation(animation);
				// animation.startNow();
				// }
				loadingIv2.setVisibility(View.VISIBLE);
			} else if (msg.what == 3) {
				loadMoreData(); // 加载更多数据，这里可以使用异步加载
			} else if (msg.what == 4) {
				playIv2.setVisibility(View.VISIBLE);
				playIv2.setBackgroundResource(R.drawable.music_play);
			} else if (msg.what == 5) {
				playIv2.setVisibility(View.VISIBLE);
				playIv2.setBackgroundResource(R.drawable.music_stop);
			} else if (msg.what == 6) {
				playIv1.setVisibility(View.VISIBLE);
				playIv1.setBackgroundResource(R.drawable.music_play);
			} else if (msg.what == 7) {
				loadingIv1.setVisibility(View.INVISIBLE);
				playIv1.setBackgroundResource(R.drawable.music_stop);
			} else if (msg.what == 8) {
				loadingIv1.setVisibility(View.INVISIBLE);
				playIv1.setBackgroundResource(R.drawable.music_play);
			} else if (msg.what == 20) {// 设置头像
				String headurl = Commons.getImageSavedpath1()
						+ UserVariable.CALLNUMBER + Commons.headname;
				if (new File(headurl).exists()) {
					ImageDownLoader.getShareImageDownLoader().clearCache();
					iv_logo.setImageBitmap(ImageDownLoader
							.getShareImageDownLoader().showCacheBitmap(headurl));

				} else {
					iv_logo.setImageResource(R.drawable.user_imforimgtop);
				}
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		mInflater = inflater;
		mContentView = mInflater.inflate(R.layout.me_fragment_layout, null);
		initViews();
		initListHeader();
		setListener();
		load();
		initPopWindow();
		return mContentView;
	}

	/**
	 * 初始化长按弹出的popupwindow
	 */
	private void initPopWindow() {
		// TODO Auto-generated method stub
		View contentView = mInflater.inflate(
				R.layout.tonelist_popwindow_layout, null);
		objects = getResources()
				.getStringArray(R.array.pop_caller_crbt_strArry);
		popList = (ListView) contentView
				.findViewById(R.id.tonelist_popwindow_lv);

		mPopAdapter = new PopListAdapter();
		popList.setAdapter(mPopAdapter);
		mPopWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopWindow.setOutsideTouchable(true);

		popList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView tv = (TextView) view;
				String itemname = tv.getText().toString();
				// 删除：Delete
				String string = getString(R.string.me_pop_delete);
				if (flag == CALLER && caller_toneType.equals("0")) {
					// 删除铃音
					if (getString(R.string.me_pop_delete).equals(itemname)) {
						if (null != mCallerToneInfo) {
							String toneType = mCallerToneInfo.getToneType();
							String toneID = mCallerToneInfo.getToneID();
							startPbarU();
							new DelToneReq(p_h)
									.sendDelToneReq(toneType, toneID);
						}
					}
					// 删除主叫
					if (getString(R.string.me_pop_delete_caller).equals(
							itemname)) {
						if (null != mcallerSettingIfo) {
							String settingID = mcallerSettingIfo.getSettingID();
							startPbarU();
							new DelToneSetReq(p_h).sendDelToneSetReq(settingID);
						}
					}
					// 设置被叫
					if (getString(R.string.me_pop_set_called).equals(itemname)) {
						if (null != mcallerSettingIfo) {
							String toneID = mcallerSettingIfo.getToneID();
							String operType = "2";
							String settingID = mcallerSettingIfo.getSettingID();
							String serviceType = "1";
							startPbarU();
							new SetToneReq(p_h).sendSetToneReq(operType,
									serviceType, settingID, toneID, "0", "0");
						}
					}
					// 进详情
					if (getString(R.string.me_pop_details).equals(itemname)) {
						skipToDetails(mCallerToneInfo);
					}
				} else if (flag == CALLER && caller_toneType.equals("1")) {

					// 设置被叫
					if (getString(R.string.pop_caller_group_crbt_setcalled)
							.equals(itemname)) {
						if (null != mcallerSettingIfo) {

							startPbarU();
							setGroup(caller_group_id, "1");
						}
					}

					// 删除主叫
					if (getString(R.string.pop_caller_group_crbt_deletecaller)
							.equals(itemname)) {
						if (null != mcallerSettingIfo) {
							String settingID = mcallerSettingIfo.getSettingID();
							startPbarU();
							new DelToneSetReq(p_h).sendDelToneSetReq(settingID);
						}
					}
				} else if (flag == CALLED && called_toneType.equals("0")) {
					// 删除铃音
					if (getString(R.string.me_pop_delete).equals(itemname)) {
						// ToneInfo info = mToneDataList.get(mCurrentPosition);
						if (null != mCalledToneInfo) {
							String toneType = mCalledToneInfo.getToneType();
							String toneID = mCalledToneInfo.getToneID();
							startPbarU();
							new DelToneReq(p_h)
									.sendDelToneReq(toneType, toneID);
						}

					}
					// 设置主叫
					if (getString(R.string.me_pop_set_caller).equals(itemname)) {
						if (null != mcalledSettingIfo) {
							String toneID = mcalledSettingIfo.getToneID();
							String operType = "2";
							String settingID = mcalledSettingIfo.getSettingID();
							String serviceType = "2";
							startPbarU();
							new SetToneReq(p_h).sendSetToneReq(operType,
									serviceType, settingID, toneID, "0", "0");

						}
					}
					// 删除被叫
					if (getString(R.string.me_pop_delete_called).equals(
							itemname)) {
						if (null != mcalledSettingIfo) {
							String settingID = mcalledSettingIfo.getSettingID();
							startPbarU();
							new DelToneSetReq(p_h).sendDelToneSetReq(settingID);

						}
					}
					// 进入详情
					if (getString(R.string.me_pop_details).equals(itemname)) {
						skipToDetails(mCalledToneInfo);
					}
				} else if (flag == CALLED && called_toneType.equals("1")) {
					// 删除被叫
					if (getString(R.string.pop_called_group_crbt_deletecaller)
							.equals(itemname)) {
						if (null != mcalledSettingIfo) {
							String settingID = mcalledSettingIfo.getSettingID();
							startPbarU();
							new DelToneSetReq(p_h).sendDelToneSetReq(settingID);
						}

					}
					// 设置主叫
					if (getString(R.string.pop_called_group_crbt_setcalled)
							.equals(itemname)) {
						if (null != mcalledSettingIfo) {
							startPbarU();
							setGroup(called_group_id, "2");
						}
					}
				} else if (flag == LIBRARY) {
					// 删除铃音
					if (getString(R.string.me_pop_delete).equals(itemname)) {
						ToneInfo info = mToneDataList.get(mCurrentPosition);
						if (null != info) {
							String toneType = info.getToneType();
							String toneID = info.getToneID();
							startPbarU();
							new DelToneReq(p_h)
									.sendDelToneReq(toneType, toneID);
						}

					}
					// 设置主叫
					if (getString(R.string.me_pop_set_caller).equals(itemname)) {
						ToneInfo info = mToneDataList.get(mCurrentPosition);
						if (null != info) {
							String toneID = info.getToneID();
							// String toneType = info.getToneType();
							String operType = "1";
							String settingID = "0000000000";
							String serviceType = "2";
							startPbarU();
							new SetToneReq(p_h).sendSetToneReq(operType,
									serviceType, settingID, toneID, "0", "0");
						}

					}
					// 设置被叫
					if (getString(R.string.me_pop_set_called).equals(itemname)) {
						ToneInfo info = mToneDataList.get(mCurrentPosition);
						if (null != info) {
							String toneID = info.getToneID();
							// String toneType = info.getToneType();
							String operType = "1";
							String settingID = "0000000000";
							String serviceType = "1";
							startPbarU();
							new SetToneReq(p_h).sendSetToneReq(operType,
									serviceType, settingID, toneID, "0", "0");
						}

					}
					// 进入详情
					if (getString(R.string.me_pop_details).equals(itemname)) {
						ToneInfo info = mToneDataList.get(mCurrentPosition);
						skipToDetails(info);
					}
				} else if (flag == HEADSET) {
					if (getString(R.string.me_pop_photographic)
							.equals(itemname)) {// 拍照
						photo();
					} else if (getString(R.string.me_pop_choicephoto).equals(
							itemname)) {// 选择本地图片
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_PICK);
						startActivityForResult(intent, SELECT_PICTURE);
					}
				}
				mPopWindow.dismiss();

			}
		});
	}

	private void load() {
		// TODO Auto-generated method stub
		// mToneAdapter = new ToneAdapter();
		// mToneList.setAdapter(mToneAdapter);
	}

	/**
	 * 璁剧疆鐩戝惉
	 */
	private void setListener() {
		// TODO Auto-generated method stub
		rl_me_user.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 进个人中心
				// MeFragment.this.startActivityForResult(new Intent(
				// getActivity(), UserCenterActivity.class),
				// Activity.RESULT_FIRST_USER);
				if (!Utils.isFastDoubleClick()) {
					// Intent intent = new Intent(mContext,
					// UserCenterActivity.class);
					// MeFragment.this.startActivityForResult(intent,
					// Activity.RESULT_FIRST_USER);
				}
			}
		});
		mTvPhoneNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// MeFragment.this.startActivityForResult(new Intent(
				// getActivity(), UserCenterActivity.class),
				// Activity.RESULT_FIRST_USER);
				if (!Utils.isFastDoubleClick()) {
					// Intent intent = new Intent(mContext,
					// UserCenterActivity.class);
					// startActivity(intent);
				}
			}
		});
		mIvMeInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// MeFragment.this.startActivityForResult(new Intent(
				// getActivity(), UserCenterActivity.class),
				// Activity.RESULT_FIRST_USER);
				if (!Utils.isFastDoubleClick()) {
					Intent intent = new Intent(mContext,
							UserCenterActivity.class);
					startActivityForResult(intent, Activity.RESULT_FIRST_USER);

				}
			}
		});

		// mContent.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// if (mIsChanging) {
		// return true;
		// }
		// return false;
		// }
		// });
		mToneList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// 点击时间过快，不操作
				if (Utils.isFastDoubleClick()) {
					return;
				}
				// 进详情
				if (position == 0) {
					return;
				}
				if (position == 1) {// 跳转到myringlibrary目录列表下
					Intent intent = new Intent(getActivity(),
							MyringLibraryActivity.class);
					startActivityForResult(intent, TOMYRING);
				} else if (position == 2) {// 跳转到Ring group目录列表下
					Intent intent = new Intent(getActivity(),
							RingGroupActivity.class);

					startActivityForResult(intent, RINGGROUP);
				} else if (position == 3) {// 跳转到Caller number group目录列表下
					Intent intent = new Intent(getActivity(),
							CallerNumberGroupActivity.class);
					startActivity(intent);
				} else if (position == 4) {// 跳转到operation record目录列表下
					Intent intent = new Intent(getActivity(),
							OperationRecordActivity.class);
					startActivity(intent);
				}
				// ToneInfo info = mToneDataList.get(position - 1);
				// skipToDetails(info);
			}
		});

		// mToneList.setOnItemLongClickListener(new OnItemLongClickListener() {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		// if (position == 0) {
		// return true;
		// }
		// flag = LIBRARY;
		// mCurrentPosition = position - 1;
		// // 根据用户开通的业务展示不同popwind词条
		// if ("0".equals(UserVariable.STATUSCALLED)) {
		// objects = getResources().getStringArray(
		// R.array.pop_listitem_nocalled_strArry);
		// } else if ("0".equals(UserVariable.STATUSCALLING)) {
		// objects = getResources().getStringArray(
		// R.array.pop_listitem_nocaller_strArry);
		// } else {
		//
		// objects = getResources().getStringArray(
		// R.array.pop_listitem_strArry);
		// }
		// mPopWindow.showAtLocation(mToneList, Gravity.BOTTOM, 0, 0);
		// // mPopAdapter.notifyDataSetChanged();
		// return true;
		// }
		// });

		mToneList.setOnScrollListener(this);
	}

	/**
	 * 初始化组件
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		iv_logo = (RoundedImageView) mContentView.findViewById(R.id.logo);
		iv_logo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!Utils.isFastDoubleClick()) {
					flag = HEADSET;
					objects = getResources().getStringArray(
							R.array.select_header);
					mPopWindow.showAtLocation(mToneList, Gravity.BOTTOM, 0, 0);
				}
			}
		});
		mTvOprator = (TextView) mContentView.findViewById(R.id.me_operator);
		mLLNoRing = (LinearLayout) mContentView
				.findViewById(R.id.me_no_ring_ll);
		mToneList = (ListView) mContentView.findViewById(R.id.me_ringtone_lv);
		mIvMeInfo = (ImageView) mContentView.findViewById(R.id.me_info_iv);
		mTvPhoneNo = (TextView) mContentView.findViewById(R.id.me_num_tv);
		mIvBack = (ImageView) mContentView.findViewById(R.id.title_iv);
		// mTitle = (TextView) mContentView.findViewById(R.id.title_tv);
		rl_me_user = (RelativeLayout) mContentView
				.findViewById(R.id.rl_me_user);
		nowifi = (LinearLayout) mContentView.findViewById(R.id.list_nowifi);
		// mTitle.setText(R.string.me_title_txt);
		mTvPhoneNo.setText(UserVariable.CALLNUMBER);
		if (Utils.isNetworkAvailable(getActivity())) {
			nowifi.setVisibility(View.GONE);
			// mToneList.setVisibility(View.VISIBLE);
		} else {
			nowifi.setVisibility(View.VISIBLE);
		}
		mTvOprator.setText(getString(R.string.operator));
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mRefreshAll = true;
		mToneCount = 0;
		mTaskCount = 4;
		mToneDataList = null;
		mContext = getActivity();
		manager = SharedPreferenceService.getInstance(mContext);
		catalog_objects = getResources()
				.getStringArray(R.array.my_ring_setitem);

		startPbarU();
		// new QryUserToneReq(p_h).sendQryUserToneReq(showNum, 0);
		mToneDataList = new ArrayList<ToneInfo>();

		if (mRefreshAll) {
			mCalledToneInfo = null;
			mCallerToneInfo = null;
			mcallerSettingIfo = null;
			mcalledSettingIfo = null;
			setType = null;
			// 查询主叫和被叫彩铃
			pageNo = 1;
			qryMode();
			new QryToneSetReq(p_h, FusionCode.REQUEST_QRYCALLERTONESETEVT)
					.sendQryToneSetReq(20, 0, "2");
			mRefreshAll = false;
		} else {
			isLoading = false;
			moreView.setVisibility(View.GONE);
			stopMusic();
			mToneAdapter.notifyDataSetChanged();
		}

		new QryUserToneReq(p_h).sendQryUserToneReq(showNum, 0);

		// animation = new RotateAnimation(0, +360, Animation.RELATIVE_TO_SELF,
		// 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		// animation.setDuration(2000);
		// animation.setRepeatCount(-1);
		// animation.setRepeatMode(Animation.INFINITE);
		// animation.setInterpolator(new LinearInterpolator());
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
			return catalog_objects.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return catalog_objects[position];
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
						R.layout.me_fragment_folder_layout, null);
				holder.folder_library = (TextView) convertView
						.findViewById(R.id.folder_library);
				// holder.playIV = (ImageView) convertView
				// .findViewById(R.id.music_play_stop_loading);
				// holder.songNameTV = (TextView) convertView
				// .findViewById(R.id.tone_item_songname_tv);
				// holder.songerNameTV = (TextView) convertView
				// .findViewById(R.id.tone_item_songername_tv);
				// holder.loadingIV = (ProgressBar) convertView
				// .findViewById(R.id.music_loading);
				convertView.setTag(holder);
			} else {
				holder = (ToneViewHolder) convertView.getTag();
			}
			final int temp = position;
			holder.folder_library.setText(catalog_objects[position]);
			// ToneInfo toneInfo = mToneDataList.get(position);
			// if (null != toneInfo) {
			// holder.songerNameTV.setText(toneInfo.getSingerName());
			// holder.songNameTV.setText(toneInfo.getToneName());
			// }
			// holder.playIV.setOnClickListener(new PlayOnclickListener(temp));
			if (pos == -3) {
				if (isPlayer) {

					loadingIv1.setAnimation(null);
					loadingIv1.setVisibility(View.INVISIBLE);
					mHandler.sendEmptyMessage(7);
					// playIv1.setBackgroundResource(R.drawable.music_stop);

				} else {
					// playIv1.setBackground(null);
					// if (loadingIv1.getAnimation() == null) {
					// loadingIv1.setAnimation(animation);
					// animation.startNow();
					// }
					// loadingIv1.setVisibility(View.VISIBLE);
					mHandler.sendEmptyMessage(1);
				}
				if (null != mCalledLL) {
					loadingIv2.setAnimation(null);
					loadingIv2.setVisibility(View.INVISIBLE);
					// playIv2.setVisibility(View.VISIBLE);
					// playIv2.setBackgroundResource(R.drawable.music_play);
					mHandler.sendEmptyMessage(4);
				}
			} else if (pos == -4) {
				if (isPlayer) {

					loadingIv2.setAnimation(null);
					loadingIv2.setVisibility(View.INVISIBLE);
					mHandler.sendEmptyMessage(5);
					// playIv2.setVisibility(View.VISIBLE);
					// playIv2.setBackgroundResource(R.drawable.music_stop);

				} else {
					// playIv2.setBackground(null);
					// if (loadingIv2.getAnimation() == null) {
					// loadingIv2.setAnimation(animation);
					// animation.startNow();
					// }
					// loadingIv2.setVisibility(View.VISIBLE);
					mHandler.sendEmptyMessage(2);
				}
				if (null != mCallerLL) {
					loadingIv1.setAnimation(null);
					// loadingIv1.setVisibility(View.INVISIBLE);
					// playIv1.setBackgroundResource(R.drawable.music_play);
					mHandler.sendEmptyMessage(8);
				}
			}
			if (pos != position) /*
								 * { if (isPlayer) {
								 * holder.loadingIV.setAnimation(null);
								 * holder.loadingIV
								 * .setVisibility(View.INVISIBLE);
								 * holder.playIV.
								 * setBackgroundResource(R.drawable.
								 * music_stop);
								 * 
								 * } else {
								 * holder.playIV.setBackgroundResource(0); if
								 * (holder.loadingIV.getAnimation() == null) {
								 * // holder.loadingIV.setAnimation(animation);
								 * // animation.startNow(); }
								 * holder.loadingIV.setVisibility(View.
								 * VISIBLE); } } else
								 */{
				// holder.loadingIV.setAnimation(null);
				// holder.loadingIV.setVisibility(View.INVISIBLE);
				// holder.playIV.setBackgroundResource(R.drawable.music_play);
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
				if (null != mCalledLL && pos != -4) {
					loadingIv2.setAnimation(null);
					loadingIv2.setVisibility(View.INVISIBLE);
					mHandler.sendEmptyMessage(4);
					// playIv2.setBackgroundResource(R.drawable.music_play);
				}
				if (null != mCallerLL && pos != -3) {
					loadingIv1.setAnimation(null);
					loadingIv1.setVisibility(View.INVISIBLE);
					mHandler.sendEmptyMessage(6);

				}

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
		// private ImageView playIV;
		// private ProgressBar loadingIV;
		// private TextView songNameTV;
		private TextView folder_library;
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
			TextView tv = (TextView) mInflater.inflate(
					R.layout.tonelist_popwindow_list_item, null);
			tv.setText(objects[position]);
			// String language = manager.get("language", "");
			tv.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.list_spain_textview_selector));
			// if(Utils.CheckTextNull(language)){
			// // tv.setText(objects[position]);
			// // tv.setBackgroundResource(R.color.black);
			// // tv.setTextSize(20);
			// }

			return tv;
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
				Utils.showTextToast(getActivity(),
						UtlisReturnCode.ReturnCode(des, mContext));
			}
			stopMusic();
			mToneAdapter.notifyDataSetChanged();
			mToneAdapter.pos = -1;
			isPlayer = false;
		}

	};
	private boolean mIsChanging;

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
			if (temp == -3) {
				if (null != mCallerToneInfo) {
					info = mCallerToneInfo;
				}
			} else if (temp == -4) {
				if (null != mCalledToneInfo) {
					info = mCalledToneInfo;
				}
			} else {
				info = mToneDataList.get(temp);

			}
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
				mediaPlayer.setDataSource(url);
				mediaPlayer.getUrlFromServer(info.getToneID(),
						info.getToneType());
				mToneAdapter.pos = temp;
				mToneAdapter.notifyDataSetChanged();
			}
		}

	}

	/**
	 * 跳转详情
	 */
	private void skipToDetails(ToneInfo info) {
		if (null == info) {
			return;
		}
		stopMusic();

		Intent intent = new Intent(mContext, DetailsRingtoneActivity.class);
		// intent.putExtra("bean", value)
		Bundle bundle = new Bundle();
		bundle.putParcelable("bean", info);
		intent.putExtra("flag", true);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initMediaPlayer();
		mHandler.sendEmptyMessage(20);
		if (manager == null)
			manager = SharedPreferenceService.getInstance(mContext);
		String lastnumber = manager.get("lastnumber", "");
		String mtext = manager.get(UserVariable.CALLNUMBER, "");
		if (!mtext.equals("")) {
			mTvPhoneNo.setText(mtext);
		}
		if (!lastnumber.equalsIgnoreCase(UserVariable.CALLNUMBER)) {
			initData();
			manager.put("lastnumber", UserVariable.CALLNUMBER);
		}
		new QryToneGrpRequest(p_h).sendQryToneGrpRequest(100, 0);
	}

	/**
	 * 查询是否随机
	 */
	private void qryMode() {
		startPbarU();
		new QryPlayModeRequest(p_h).qryPlayModerep();
	}

	// 1.individual called CRBT
	// 2.individual caller CRBT

	private void setGroup(String tondGroupID, String serviceType) {
		new SetToneReq(p_h).sendSetGrpToneReq(tondGroupID, serviceType);
	}

	@Override
	protected void reqXmlSucessed(Message msg) {
		// TODO Auto-generated method stub
		//
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {

		case FusionCode.REQUEST_QRYGRP:
			UserToneGrpsSeriable.getInstance().cleargroup();
			QryToneGrpResp qryGrpToneRsp = (QryToneGrpResp) msg.obj;
			mTempInfos = qryGrpToneRsp.getToneList();
			refreshList();
			break;
		// 成功返回个人铃音库，只第一次发起主叫被叫铃音请求
		case FusionCode.REQUEST_QRYUSERTONEEVT:
			stopPbarU();
			pageNo++;
			mToneDataList.clear();
			QryUserToneRsp qryUserToneRsp = (QryUserToneRsp) msg.obj;
			List<ToneInfo> mTempInfos = qryUserToneRsp.getToneList();
			mLastDataCount = mTempInfos.size();
			mToneCount = mToneCount + mTempInfos.size();
			mToneDataList.addAll(mTempInfos);
			// mToneAdapter.notifyDataSetChanged();
			if (mToneDataList.size() == 0) {
				// mToneList.setVisibility(View.GONE);
				// mLLNoRing.setVisibility(View.VISIBLE);
				return;
			} else {
				// mToneList.setVisibility(View.VISIBLE);
				mLLNoRing.setVisibility(View.GONE);
			}
			// if (mRefreshAll) {
			// mCalledToneInfo = null;
			// mCallerToneInfo = null;
			// // 查询主叫和被叫彩铃
			// pageNo = 1;
			// new QryToneSetReq(p_h, FusionCode.REQUEST_QRYCALLEDTONESETEVT)
			// .sendQryToneSetReq(20, 0, "1");
			// new QryToneSetReq(p_h, FusionCode.REQUEST_QRYCALLERTONESETEVT)
			// .sendQryToneSetReq(20, 0, "2");
			// mRefreshAll = false;
			// } else {
			// isLoading = false;
			// moreView.setVisibility(View.GONE);
			// stopMusic();
			// mToneAdapter.notifyDataSetChanged();
			// }
			// new SetToneReq(p_h).sendSetToneReq("", "", "", "", "0", "0");
			break;
		// 主叫铃音请求回来后，根据toneid查询主叫铃音信息
		case FusionCode.REQUEST_QRYCALLERTONESETEVT:
			stopPbarU();
			// mToneList.setVisibility(View.GONE);
			QryToneSetRsp qryToneSetRsp1 = (QryToneSetRsp) msg.obj;
			// List<UserToneSettingInfo> calledTone = qryToneSetRsp.getInfos();
			UserToneSettingInfo settingInfo1 = qryToneSetRsp1.getSettingInfo();
			if (null != settingInfo1 && settingInfo1.getToneType().equals("0")) {
				caller_toneType = "0";
				String toneId = settingInfo1.getToneID();
				mcallerSettingIfo = settingInfo1;
				QryToneById qry = new QryToneById(p_h,
						FusionCode.REQUEST_QRYCALLERTONEBYID);
				qry.getQryToneById(toneId, "1", "1", 100, 0);
			} else if (null != settingInfo1
					&& settingInfo1.getToneType().equals("1")) {
				mcallerSettingIfo = settingInfo1;
				caller_toneType = "1";
				caller_group_id = settingInfo1.getToneID();

			}

			refreshList();
			break;
		// 被叫铃音请求回来后，根据toneid查询主叫铃音信息
		case FusionCode.REQUEST_QRYCALLEDTONESETEVT:
			stopPbarU();
			// mToneList.setVisibility(View.GONE);
			QryToneSetRsp qryToneSetRsp2 = (QryToneSetRsp) msg.obj;
			// List<UserToneSettingInfo> calledTone = qryToneSetRsp.getInfos();
			UserToneSettingInfo settingInfo2 = qryToneSetRsp2.getSettingInfo();
			if (null != settingInfo2 && settingInfo2.getToneType().equals("0")) {
				String toneId = settingInfo2.getToneID();
				mcalledSettingIfo = settingInfo2;
				QryToneById qry = new QryToneById(p_h,
						FusionCode.REQUEST_QRYCALLEDTONEBYID);
				qry.getQryToneById(toneId, "1", "1", 100, 0);
				called_toneType = "0";

			} else if (null != settingInfo2
					&& settingInfo2.getToneType().equals("1")) {
				mcalledSettingIfo = settingInfo2;
				called_toneType = "1";
				called_group_id = settingInfo2.getToneID();

			}
			// qryMode();
			refreshList();
			break;

		case FusionCode.REQUEST_QRYPLAYMODE:
			stopPbarU();
			QryPlayModeResp rep = (QryPlayModeResp) msg.obj;
			setType = rep.getSetType();
			if (!TextUtils.isEmpty(setType) && setType.equals("1")) {
				new QryToneSetReq(p_h, FusionCode.REQUEST_QRYCALLEDTONESETEVT)
						.sendQryToneSetReq(20, 0, "1");
			} else if (!TextUtils.isEmpty(setType) && setType.equals("2")) {
				refreshList();
			}

			break;
		// 删除铃音后，刷新页面，从查询铃音库开始走流程
		case FusionCode.REQUEST_DELTONEEVT:
			stopPbarU();
			pageNo = 0;
			// mToneList.setVisibility(View.GONE);
			mRefreshAll = true;
			mToneDataList.clear();
			mToneCount = 0;
			initData();
			// 132
			// new QryUserToneReq(p_h).sendQryUserToneReq(showNum, 0);
			break;
		// 删除主被叫铃音,刷新页面，从查询铃音库开始走流程
		case FusionCode.REQUEST_DELTONESETEVT:
			stopPbarU();
			// mToneList.setVisibility(View.GONE);
			pageNo = 0;
			mRefreshAll = true;
			mToneDataList.clear();
			mToneCount = 0;
			initData();
			// new QryUserToneReq(p_h).sendQryUserToneReq(showNum, 0);
			break;
		// 设置主被叫铃音，刷新页面，从查询铃音库开始走流程
		case FusionCode.REQUEST_SETTONEEVT:
			// mToneList.setVisibility(View.GONE);
			pageNo = 0;
			mRefreshAll = true;
			mToneDataList.clear();
			mToneCount = 0;
			initData();
			// new QryUserToneReq(p_h).sendQryUserToneReq(showNum, 0);
			break;

		case FusionCode.REQUEST_GETTONELISTENADDREVT:
			stopPbarU();
			GetToneListenAddrRsp addrRsp = (GetToneListenAddrRsp) msg.obj;
			url = addrRsp.getToneAddr();
			loadingTone();
			break;
		// 查询主叫铃音
		case FusionCode.REQUEST_QRYCALLERTONEBYID:
			stopPbarU();
			QryToneByIdRsp entry1 = (QryToneByIdRsp) msg.obj;
			if (entry1.list.size() != 0) {
				mCallerToneInfo = entry1.list.get(0);
			}
			refreshList();
			break;
		// 查询被叫铃音
		case FusionCode.REQUEST_QRYCALLEDTONEBYID:
			stopPbarU();
			QryToneByIdRsp entry2 = (QryToneByIdRsp) msg.obj;

			if (entry2.list.size() != 0) {
				mCalledToneInfo = entry2.list.get(0);
			}
			refreshList();
			break;
		default:
			break;
		}
	}

	/**
	 * 根据彩铃的id获取完整的铃音信息
	 * 
	 * @param toneId
	 */
	private ToneInfo getToneInfo(String toneId) {
		// TODO Auto-generated method stub
		if (null == toneId) {
			return null;
		}
		if (null != mToneDataList) {
			for (int i = 0; i < mToneDataList.size(); i++) {
				ToneInfo info = mToneDataList.get(i);
				String strToneId = info.getToneID();
				if (null != strToneId && strToneId.equals(toneId)) {
					return info;
				}
			}
		}

		return null;
	}

	@Override
	protected void reqXmlFail(Message msg) {
		// TODO Auto-generated method stub
		stopPbarU();
		super.reqXmlFail(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QRYCALLEDTONESETEVT:
			mTaskCount--;
			refreshList();
			break;
		case FusionCode.REQUEST_QRYGRP:
			refreshList();
			break;
		case FusionCode.REQUEST_QRYCALLERTONESETEVT:
			mTaskCount--;
			refreshList();
			break;
		case FusionCode.REQUEST_QRYCALLERTONEBYID:
			refreshList();
			break;
		case FusionCode.REQUEST_QRYCALLEDTONEBYID:
			refreshList();
			break;
		case FusionCode.REQUEST_QRYPLAYMODE:
			// refreshList();
			break;
		case FusionCode.REQUEST_QRYUSERTONEEVT:
			if (pageNo == 0) {
				// mToneList.setVisibility(View.GONE);
				mLLNoRing.setVisibility(View.VISIBLE);
			}
			if (!mRefreshAll) {
				moreView.setVisibility(View.GONE);
				isLoading = false;
			}
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
		case FusionCode.REQUEST_QRYCALLEDTONESETEVT:
			mTaskCount--;
			refreshList();
			break;
		case FusionCode.REQUEST_QRYCALLERTONESETEVT:
			mTaskCount--;
			refreshList();
			break;
		case FusionCode.REQUEST_QRYCALLERTONEBYID:
			refreshList();
			break;
		case FusionCode.REQUEST_QRYCALLEDTONEBYID:
			refreshList();
			break;
		case FusionCode.REQUEST_QRYUSERTONEEVT:
			// mToneList.setVisibility(View.GONE);
			// mLLNoRing.setVisibility(View.VISIBLE);
			if (!mRefreshAll) {
				moreView.setVisibility(View.GONE);
			}
		}
		stopPbarU();
	}

	@Override
	public void onStart() {
		super.onStart();
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

	/**
	 * 初始化listview的头部和尾部，用来展示主叫被叫彩铃和刷新
	 */
	private void initListHeader() {

		hearder = mInflater.inflate(R.layout.tonelist_header_layout, null);
		// mTvMyLibrary = (TextView)
		// hearder.findViewById(R.id.mylibrary_tv);
		mCallerLL = (LinearLayout) hearder
				.findViewById(R.id.tonelist_caller_ll);
		mCallerLL.setVisibility(View.GONE);
		callertone_end_time = (TextView) mCallerLL
				.findViewById(R.id.tone_end_time);
		callertoneimg = (RoundedImageView) mCallerLL.findViewById(R.id.imagebg);
		callerSongNameTV = (TextView) mCallerLL
				.findViewById(R.id.tone_item_songname_tv);
		callerSongerNameTV = (TextView) mCallerLL
				.findViewById(R.id.tone_item_songername_tv);
		// mTvMyLibrary.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent(getActivity(),
		// MyringLibraryActivity.class);
		// startActivity(intent);
		// }
		// });
		mCallerLL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击时间过快，不操作
				if (Utils.isFastDoubleClick()) {
					return;
				}
				flag = CALLER;
				// if (!"0".equals(UserVariable.STATUSCALLED)) {

				objects = getResources().getStringArray(
						R.array.pop_caller_crbt_strArry);
				// } else {
				// objects = getResources().getStringArray(
				// R.array.pop_caller_nocalled_crbt_strArry);
				// }
				mPopWindow.showAtLocation(mToneList, Gravity.BOTTOM, 0, 0);
			}
		});
		// mCallerLL.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // 点击时间过快，不操作
		// if (Utils.isFastDoubleClick()) {
		// return;
		// }
		// // 跳转详情
		// if (null != mCallerToneInfo) {
		// skipToDetails(mCallerToneInfo);
		// }
		// }
		// });
		playIv1 = (ImageView) mCallerLL
				.findViewById(R.id.music_play_stop_loading);
		loadingIv1 = (ProgressBar) mCallerLL.findViewById(R.id.music_loading);
		playIv1.setOnClickListener(new PlayOnclickListener(-3));
		// ///////////////////////////////////////////////////
		mCalledLL = (LinearLayout) hearder
				.findViewById(R.id.tonelist_called_ll);
		mCalledLL.setVisibility(View.GONE);
		calledtone_end_time = (TextView) mCalledLL
				.findViewById(R.id.tone_end_time);
		calledSongNameTV = (TextView) mCalledLL
				.findViewById(R.id.tone_item_songname_tv);
		calledtoneimg = (RoundedImageView) mCalledLL.findViewById(R.id.imagebg);
		calledSongerNameTV = (TextView) mCalledLL
				.findViewById(R.id.tone_item_songername_tv);
		mCalledLL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击时间过快，不操作
				if (Utils.isFastDoubleClick()) {
					return;
				}
				flag = CALLED;
				// if (!"0".equals(UserVariable.STATUSCALLING)) {

				objects = getResources().getStringArray(
						R.array.pop_called_crbt_strArry);
				// } else {
				// objects = getResources().getStringArray(
				// R.array.pop_called_nocaller_crbt_strArry);
				//
				// }
				mPopWindow.showAtLocation(mToneList, Gravity.BOTTOM, 0, 0);
				// return true;
			}
		});
		// mCalledLL.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // 点击时间过快，不操作
		// if (Utils.isFastDoubleClick()) {
		// return;
		// }
		// // 跳转详情
		// if (null != mCalledToneInfo) {
		// skipToDetails(mCalledToneInfo);
		// }
		// }
		// });
		playIv2 = (ImageView) mCalledLL
				.findViewById(R.id.music_play_stop_loading);
		loadingIv2 = (ProgressBar) mCalledLL.findViewById(R.id.music_loading);
		playIv2.setOnClickListener(new PlayOnclickListener(-4));
		if ("1".equals(UserVariable.STATUSCALLING) && null != mCallerToneInfo) {
			mCallerLL.setVisibility(View.VISIBLE);
		}
		if ("1".equals(UserVariable.STATUSCALLED) && null != mCalledToneInfo) {
			mCalledLL.setVisibility(View.VISIBLE);
		}

		mCallerLL_group = (LinearLayout) hearder
				.findViewById(R.id.tonelist_caller_group_ll);
		mCallerLL_group.setVisibility(View.GONE);
		mCallerLL_group_name = (TextView) mCallerLL_group
				.findViewById(R.id.tone_item_groupname_tv);

		mCallerLL_group.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				flag = CALLER;

				objects = getResources().getStringArray(
						R.array.pop_caller_groupset_crbt_strArry);

				mPopWindow.showAtLocation(mToneList, Gravity.BOTTOM, 0, 0);

			}
		});
		arror_group_mCallerLL = (TextView) mCallerLL_group
				.findViewById(R.id.tone_item_grouparrow_tv);

		arror_group_mCallerLL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mintent = new Intent(getActivity(),
						RingGroupDetailActivity.class);
				Bundle bundle = new Bundle();

				bundle.putString("groupinfo", groupid_er);
				bundle.putString("grouptitle", grouptitle_er);
				bundle.putString("flag", "hide");
				mintent.putExtras(bundle);
				startActivity(mintent);

			}
		});

		mCalledLL_group = (LinearLayout) hearder
				.findViewById(R.id.tonelist_called_group_ll);
		mCalledLL_group.setVisibility(View.GONE);
		mCalledLL_group_name = (TextView) mCalledLL_group
				.findViewById(R.id.tone_item_groupname_tv);
		mCalledLL_group.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				flag = CALLED;

				objects = getResources().getStringArray(
						R.array.pop_called_groupset_crbt_strArry);

				mPopWindow.showAtLocation(mToneList, Gravity.BOTTOM, 0, 0);

			}
		});
		arror_group_mCalledLL = (TextView) mCalledLL_group
				.findViewById(R.id.tone_item_grouparrow_tv);

		arror_group_mCalledLL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mintent = new Intent(getActivity(),
						RingGroupDetailActivity.class);
				Bundle bundle = new Bundle();

				bundle.putString("groupinfo", groupid_ed);
				bundle.putString("grouptitle", grouptitle_ed);
				bundle.putString("flag", "hide");
				mintent.putExtras(bundle);
				startActivity(mintent);
			}
		});

		tonelist_called_random_ll = (LinearLayout) hearder
				.findViewById(R.id.tonelist_called_random_ll);
		// tonelist_called_random_ll.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (mCalledToneInfo == null) {
		// flag = CALLED;
		//
		// objects =
		// getResources().getStringArray(R.array.pop_called_groupset_crbt_strArry);
		//
		// mPopWindow.showAtLocation(mToneList, Gravity.BOTTOM, 0, 0);
		// } else {
		// flag = CALLED;
		//
		// objects =
		// getResources().getStringArray(R.array.pop_called_crbt_strArry);
		//
		// mPopWindow.showAtLocation(mToneList, Gravity.BOTTOM, 0, 0);
		// }
		//
		// }
		// });
		tonelist_called_random_ll.setVisibility(View.GONE);

		mToneList.addHeaderView(hearder);
		moreView = mInflater.inflate(R.layout.refreash_header_layout, null);
		footImageView = (Button) moreView
				.findViewById(R.id.pull_to_refresh_image);
		// headerProgress = (ProgressBar) header
		// .findViewById(R.id.pull_to_refresh_progress);
		// refreshTimeTxt = (TextView)
		// header.findViewById(R.id.refresh_time_text);
		//
		// if (isShowRefreshTimeTextView) {
		// refreshTimeTxt.setVisibility(View.VISIBLE);
		// } else {
		// refreshTimeTxt.setVisibility(View.GONE);
		// }

		rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(1200);
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		rotateAnimation.setRepeatMode(Animation.RESTART);

		rotateAnimation.setInterpolator(new LinearInterpolator());// 匀速动画
		footImageView.setAnimation(rotateAnimation);
		mCalledToneInfo = null;
		moreView.setVisibility(View.GONE);
		mToneList.addFooterView(moreView); // 添加底部view，一定要在setAdapter之前添加，否则会报错。
		mToneAdapter = new ToneAdapter();
		mToneList.setAdapter(mToneAdapter);
	}

	/**
	 * 刷新页面，mTaskCount计算任务数量 0是铃音 1是铃音组
	 */
	private synchronized void refreshList() {
		// if (mTaskCount == 0) {
		// mToneList.setVisibility(View.VISIBLE);
		// mTvMyLibrary.setVisibility(View.VISIBLE);
		if (null != mCallerToneInfo && caller_toneType.equals("0")) {
			mCallerLL.setVisibility(View.VISIBLE);
		} else {
			mCallerLL.setVisibility(View.GONE);
		}
		if (null != mcallerSettingIfo && caller_toneType.equals("1")) {
			mCallerLL_group.setVisibility(View.VISIBLE);
		} else {
			mCallerLL_group.setVisibility(View.GONE);
		}

		if (null != mCalledToneInfo && called_toneType.equals("0")
				&& !TextUtils.isEmpty(setType) && setType.equals("1")) {
			mCalledLL.setVisibility(View.VISIBLE);
		} else {
			mCalledLL.setVisibility(View.GONE);
		}
		if (null != mcalledSettingIfo && called_toneType.equals("1")
				&& !TextUtils.isEmpty(setType) && setType.equals("1")) {
			mCalledLL_group.setVisibility(View.VISIBLE);
		} else {
			mCalledLL_group.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(setType) && setType.equals("2")) {
			if (mCalledLL_group != null)
				mCalledLL_group.setVisibility(View.GONE);
			if (mCalledLL != null)
				mCalledLL.setVisibility(View.GONE);
			tonelist_called_random_ll.setVisibility(View.VISIBLE);
		} else if (TextUtils.isEmpty(setType) || setType.equals("1")) {
			tonelist_called_random_ll.setVisibility(View.GONE);
		}
		if (null != mCallerToneInfo && caller_toneType.equals("0")) {
			String saveDir = Commons.getImageSavedpath()
					+ mCallerToneInfo.getToneClassID();
			if (new File(saveDir).exists()) {
				callertoneimg.setImageBitmap(ImageDownLoader
						.getShareImageDownLoader().showCacheBitmap(saveDir));
			}
			callerSongNameTV.setText(mCallerToneInfo.getToneName());
			callerSongerNameTV.setText(mCallerToneInfo.getSingerName());
			ToneInfo mToneInfo = getToneInfo(mCallerToneInfo.getToneID());
			callertone_end_time.setText(TimeUtil.daysBetween(mToneInfo
					.getToneValidDay()) + " " + getString(R.string.days));
		} else if (null != mcallerSettingIfo && caller_toneType.equals("1")) {

			if (mTempInfos != null) {
				for (UserToneGrps user : mTempInfos) {
					if (user.getUserToneGrpID().equals(
							mcallerSettingIfo.getToneID())) {
						groupid_er = mcallerSettingIfo.getToneID();
						grouptitle_er = user.getToneGrpName();
					}
				}
				String string = EllipsizeUtils.textEllpsize(
						mCallerLL_group_name, grouptitle_er, getActivity(), -1);
				mCallerLL_group_name.setText(string);
				// EllipsizeUtils.textEllpsize(mCallerLL_group_name);
			}

		}
		if (null != mCalledToneInfo && called_toneType.equals("0")) {
			String saveDir = Commons.getImageSavedpath()
					+ mCalledToneInfo.getToneClassID();
			if (new File(saveDir).exists()) {
				calledtoneimg.setImageBitmap(ImageDownLoader
						.getShareImageDownLoader().showCacheBitmap(saveDir));
			}
			String tonename = mCalledToneInfo.getToneName();
			String singername = mCalledToneInfo.getSingerName();
			calledSongNameTV.setText(tonename);
			calledSongerNameTV.setText(singername);
			ToneInfo mToneInfo = getToneInfo(mCalledToneInfo.getToneID());
			calledtone_end_time.setText(TimeUtil.daysBetween(mToneInfo
					.getToneValidDay()) + " " + getString(R.string.days));
		} else if (null != mcalledSettingIfo && called_toneType.equals("1")) {
			if (mTempInfos != null) {
				for (UserToneGrps user : mTempInfos) {
					if (user.getUserToneGrpID().equals(
							mcalledSettingIfo.getToneID())) {
						groupid_ed = mcalledSettingIfo.getToneID();
						grouptitle_ed = user.getToneGrpName();
					}
				}
				String string = EllipsizeUtils.textEllpsize(
						mCallerLL_group_name, grouptitle_ed, getActivity(), -1);
				mCalledLL_group_name.setText(string);
			}
		}
		mToneList.setAdapter(mToneAdapter);
		stopMusic();
		mToneAdapter.notifyDataSetChanged();
		mTaskCount = 4;
		stopPbarU();
		// }
		mToneList.setVisibility(View.VISIBLE);
		// initListHeader();
	}

	private void loadingTone() {
		mediaPlayer.setDataSource(url);
		mediaPlayer.prepare();
		mToneList.setStackFromBottom(false);
		mToneAdapter.notifyDataSetChanged();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		// 下拉到空闲是，且最后一个item的数等于数据的总数时，进行更新
		if (lastItem == mToneCount && scrollState == this.SCROLL_STATE_IDLE) {
			Log.i(TAG, "拉到最底部");
			if (mLastDataCount == showNum) {
				moreView.setVisibility(view.VISIBLE);
				footImageView.startAnimation(rotateAnimation);
				mHandler.sendEmptyMessage(3);
			}

		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		Log.i(TAG, "firstVisibleItem=" + firstVisibleItem
				+ "\nvisibleItemCount=" + visibleItemCount + "\ntotalItemCount"
				+ totalItemCount);

		lastItem = firstVisibleItem + visibleItemCount - 2; // 减2是因为上面加了个addFooterView和header
	}

	/**
	 * 下拉加载更多
	 */
	private void loadMoreData() {
		if (!isLoading) {
			new QryUserToneReq(p_h).sendQryUserToneReq(showNum, pageNo);
			isLoading = true;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);
		// 处理从用户中心返回是，页面的刷新
		if (20 == resultCode) {
			refreshUserData();
		}

		switch (requestCode) {
		case Activity.RESULT_FIRST_USER:
			mRefreshAll = true;
			initData();

			break;
		case TOMYRING:
			initData();
			break;
		case RINGGROUP:
			initData();
			break;
		case TAKE_PICTURE:
			System.out.println("旋转图片保存图片前期0");
			if (-1 == resultCode) {
				System.out.println("旋转图片保存图片前期1");
				
				if (myCameraLib == null) {// 为啥 等于空呢？
					System.out.println("旋转图片保存图片前期2");
					return;
				}
				System.out.println("旋转图片保存图片前期3");
				myCameraLib.onCameraIntentResult(getActivity(), resultCode,
						data);
				System.out.println("旋转图片保存图片前期4");
				final Uri uri = myCameraLib.photoUri;
				// new Thread(new Runnable() {
				//
				// @Override
				// public void run() {
				// // TODO Auto-generated method stub
				System.out.println("旋转图片保存图片前期");
				BitmapHelper.saveBitmap2RotateFilePath(BitmapHelper
						.uri2FilePath(uri));
				System.out.println("旋转图片保存图片后");
				gotoCrop(uri);
				// }
				// }).start();

				// Bitmap photo =
				// CameraLib.zoomBitmapForUri(myCameraLib.photoUri,
				// myCameraLib.rotateXDegrees, 480, 480,
				// Commons.CameraMin);
				//
				// Bitmap bitmap2 = null;
				// if (photo != null) {
				// bitmap2 = bitmapCrop(photo);
				//
				// }
				//
				// String savepath = Commons.getImageSavedpath1() +
				// UserVariable.CALLNUMBER + Commons.headname;
				// if (bitmap2 != null) {
				// CameraLib.savaBitmap(savepath, bitmap2);
				// bitmap2.recycle();
				// bitmap2 = null;
				// photo.recycle();
				// photo = null;
				// } else {
				// Utils.showTextToast(getActivity(), R.string.takephotofail);
				// }
				// mHandler.sendEmptyMessage(20);
			}
			break;
		case SELECT_PICTURE:
			if (resultCode == -1) {
				if (data == null) {
					return;
				}
				Uri uri = data.getData();
				ContentResolver cr = getActivity().getContentResolver();
				filePath = getRealPathFromURI(uri);
				gotoCrop(uri);

				// Bitmap bitmap = HandlerImage.getSmallBitmap(filePath);//
				// 两次是要使用图片加载质量问题; 加载图片地址为空：Intent.ACTION_PICK
				//
				// Bitmap bitmap2 = null;
				// if (bitmap != null) {
				// bitmap2 = bitmapCrop(bitmap);
				// 
				// }
				// String savepath = Commons.getImageSavedpath1() +
				// UserVariable.CALLNUMBER + Commons.headname;
				// if (bitmap2 != null) {
				// CameraLib.savaBitmap(savepath, bitmap2);
				// bitmap.recycle();
				// bitmap = null;
				// bitmap2.recycle();
				// bitmap2 = null;
				// } else {
				// Utils.showTextToast(getActivity(), R.string.takephotofail);
				// }
				// mHandler.sendEmptyMessage(20);
			}
			break;

		case REQUEST_CROP:
			if (data != null) {
				Bitmap bitmap2;
				try {
					Log.e("图片收集存放地址", "file://"
							+ "/"
							+ Environment.getExternalStorageDirectory()
									.getPath() + "/" + "small.png");
					// 图片收集存放地址(30061): file:////storage/emulated/0/small.png
					bitmap2 = BitmapFactory.decodeStream(getActivity()
							.getContentResolver().openInputStream(uritempFile));

					// Bitmap bitmap2 = data.getParcelableExtra("data");
					// 在这里获取到剪裁后的图片，现在要进行判断和保存
					if (bitmap2 != null) {

						bitmap2 = getRoundCornerBitmap(bitmap2, 360);
					}

					String savepath = Commons.getImageSavedpath1()
							+ UserVariable.CALLNUMBER + Commons.headname;
					Log.e("图片加载地址", savepath);
					if (bitmap2 != null) {
						CameraLib.savaBitmap(savepath, bitmap2);
						bitmap2.recycle();
						bitmap2 = null;
					} else {
						Utils.showTextToast(getActivity(),
								R.string.takephotofail);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(20);

			} else {
				return;
			}
			break;
		}

	}

	// 头像处理问题
	// 启动系统剪切的请求码
	private final int REQUEST_CROP = 1102;

	// //打开系统相机
	// private void gotoCamera(String filePath) {
	// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	// //启动时自定义图片保存地址
	// Uri outPutUri = Uri.fromFile(new File(filePath));
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
	// startActivityForResult(intent, TAKE_PICTURE);
	// }
	//
	// //打开系统相册
	// private void gotoGallery() {
	// Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	// intent.setType("image/*");
	// startActivityForResult(intent, TAKE_PICTURE);
	// }

	// 打开图片剪切
	private Uri uritempFile = Uri.parse("file://" + "/"
			+ Environment.getExternalStorageDirectory().getPath() + "/"
			+ "small.png");

	private void gotoCrop(Uri inputRri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(inputRri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1); // 设置剪切框的比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150); // 图片输出大小
		intent.putExtra("outputY", 150); // 图片输出大小
		intent.putExtra("scale", true);
		intent.putExtra("noFaceDetection", true);

		// //这种方法只是用小图
		// intent.putExtra("return-data", true);
		intent.putExtra("return-data", false);

		// 当传递大数据的时候
		// uritempFile为Uri类变量，实例化uritempFile
		// uritempFile = Uri.parse("file://" + "/" +
		// Environment.getExternalStorageDirectory().getPath() + "/" +
		// "small.png");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

		Log.e("图片收集存放地址2", "file://" + "/"
				+ Environment.getExternalStorageDirectory().getPath() + "/"
				+ "small.png");

		startActivityForResult(intent, REQUEST_CROP);
	}

	/**
	 * 获取圆角位图的方法
	 * 
	 * @param bitmap
	 *            数据源
	 * @param pixels
	 *            圆角角度，360是圆形
	 * @return
	 */
	private Bitmap getRoundCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.RGB_565);// 获取高像素的图片
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * Bitmap 处理获取到正方形的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap bitmapCrop(Bitmap bitmap) {
		int w = bitmap.getWidth(); // 得到图片的宽，高
		int h = bitmap.getHeight();

		int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

		int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
		int retY = w > h ? 0 : (h - w) / 2;
		// 下面这句是关键
		return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
	}

	private String filePath;

	public String getRealPathFromURI(Uri contentUri) {
		String filePath = null;
		Cursor cursor = null;
		try {
			String[] paths = { MediaStore.Images.Media.DATA };
			ContentResolver cr = mContext.getContentResolver();
			cursor = cr.query(contentUri, paths, null, null, null);
			cursor.moveToFirst();
			filePath = cursor.getString(cursor.getColumnIndex(paths[0]));
		} catch (Exception e) {
			filePath = contentUri.getPath();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return filePath;
	}

	/**
	 * 处理切换用户后的页面刷新
	 */
	public void refreshUserData() {
		// mToneList.setVisibility(View.GONE);
		startPbarU();
		pageNo = 0;
		mCallerToneInfo = null;
		mCalledToneInfo = null;
		mcalledSettingIfo = null;
		mcallerSettingIfo = null;
		mTvPhoneNo.setText(UserVariable.CALLNUMBER);
		lastItem = 0;
		mLastDataCount = 0;
		mRefreshAll = true;
		mToneCount = 0;
		isLoading = false;
		mTaskCount = 4;
		mToneDataList.clear();
		setType = null;
		new QryUserToneReq(p_h).sendQryUserToneReq(showNum, 0);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (null != mPopWindow) {
			mPopWindow.dismiss();
		}
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

	private static final int TAKE_PICTURE = 1010;
	private static final int TAKE_VIDEO = 1007;
	private static final int SELECT_PICTURE = 1008;
	public static final int TAKE_VIDEO_RETURN = 1009;
	private CameraLib myCameraLib = null;

	public void photo() {
		myCameraLib = new CameraLib();
		Intent intent = myCameraLib.startCameraIntent(getActivity());
		if (intent != null)
			startActivityForResult(intent, TAKE_PICTURE);
	}

}
