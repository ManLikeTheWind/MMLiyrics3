package com.dxiang.mring3.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gem.imgcash.ImageDownLoader;
import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.DetailsRingtoneActivity;
import com.dxiang.mring3.activity.MainGroupActivity;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.bean.ToneTypeAPPInfo;
import com.dxiang.mring3.request.GetimageinforReq;
import com.dxiang.mring3.request.QryRankTone;
import com.dxiang.mring3.request.QryRecommendTone;
import com.dxiang.mring3.request.QryToneByTypeRequest;
import com.dxiang.mring3.response.QryRankToneRsp;
import com.dxiang.mring3.response.QryRecommendedToneRsp;
import com.dxiang.mring3.response.QryToneByTypeResp;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.EllipsizeUtils;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.LocalMediaPlayer;
import com.dxiang.mring3.utils.UtlisReturnCode;
import com.dxiang.mring3.utils.LocalMediaPlayer.Complete;
import com.dxiang.mring3.utils.LocalMediaPlayer.ErrorListener;
import com.dxiang.mring3.utils.LocalMediaPlayer.onException;
import com.dxiang.mring3.utils.LocalMediaPlayer.onPrepared;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.view.RoundedImageView;
import com.dxiang.mring3.views.gallery.HorizontalScrollViewAdapter;
import com.dxiang.mring3.views.gallery.MyHorizontalScrollView;
import com.dxiang.mring3.views.gallery.MyHorizontalScrollView.CurrentImageChangeListener;
import com.dxiang.mring3.views.refresh.PullToRefreshBase.OnRefreshListener;
import com.dxiang.mring3.views.refresh.PullToRefreshListView;

public class CategoryRingToneHomeListFragement extends BaseFragment {
	public static final String TAG = "CategoryRingToneHomeListFragement";

	private List<ToneInfo> appTonesList;

	// private ImageView mBackView;
	//
	// private TextView mTitleView;

	private MyHorizontalScrollView mHorizontalScrollView;

	private String title;

	// 铃音列表播放器
	private LocalMediaPlayer mediaPlayer;// 播放器
	private boolean isPlayer = false;// 判断是否正在播放

	// private RotateAnimation animation;

	private PullToRefreshListView mDatasListView;

	// private List<ToneInfo> mAllTones = new ArrayList<ToneInfo>();

	// private HorizontalScrollViewAdapter mAdapter;

	private ListViewAdapter mListViewAdapter;

	private int pageNo = 0;

	private int showNum = 10;

	// 表示当前的数据是不是足够支持上拉加载
	private boolean isNeedToRefresh = true;

	// onscroll 的时候需要判断一下
	private boolean isLoadingDatas = false;

	private int currInCategoryPos = 0;
	private int firstVisibleItems;
	private View footerView;
	private String id = "";
	ListView data;
	public int type;
	public int pageNum = 1;
	private ImageView image;
	boolean isLastRow = false;
	int size = 0;
	private MyPullUpListViewCallBack myPullUpListViewCallBack;

	public void setId(String i) {
		id = i;
	}

	public void setToneList(List<ToneInfo> list) {
		this.appTonesList = list;
	}

	// public void setTitle(String t) {
	// title = t;
	// }
	public void settype(int type) {
		this.type = type;
	}

	@Override
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);
		pageNum++;
		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneByType:
			// QryToneByTypeResp resp = (QryToneByTypeResp) msg.obj;
			// pageNo++;
			// int pos = currInCategoryPos + 1 + 2000;
			// stopPbarU();
			// if (pos == resp.getCode()) {
			//
			// if (resp.getmToneInfos().size() == 1
			// && resp.getmToneInfos().get(0).isEmptyRecord()) {
			// Log.d(TAG,
			// "----------REQUEST_QryToneByName--------------------Empty
			// record");
			// checkScrollable();
			// isLoadingDatas = false;
			// if (appTonesList.size() == 0) {
			// Utils.showTextToast(getActivity(),
			// R.string.searcb_no_datas);
			// }
			// } else {
			// onDataFromServer(resp.getmToneInfos());
			// }
			// }
			break;
		case FusionCode.REQUEST_QRYRANKTONE_MONTH:
			QryRankToneRsp entryRankTone = (QryRankToneRsp) msg.obj;
			// pageNum++;//页数自增
			// mSumCount=mSumCount+entryRankTone.list.size();//总条数
			// mSumCount=entryRankTone.getAmount();//总条数
			appTonesList = entryRankTone.list;// 从服务器获取的数据
			// mDataSun.addAll(mData);//把新数据增加到展示data中去
			// showData();
			// getFirstLevelDataFromDB();
			// data.setAdapter(mListViewAdapter);
			break;
		case FusionCode.REQUEST_QRYRANKTONE_WEEK:
			QryRankToneRsp entryRankTone1 = (QryRankToneRsp) msg.obj;
			// pageNum++;//页数自增
			// mSumCount=mSumCount+entryRankTone.list.size();//总条数
			// mSumCount=entryRankTone1.getAmount();//总条数
			appTonesList = entryRankTone1.list;// 从服务器获取的数据
			// data.setAdapter(mListViewAdapter);
			// 把新数据增加到展示data中去
			break;
		case FusionCode.REQUEST_QRYRECOMMENDTONE:
			QryRecommendedToneRsp entry = (QryRecommendedToneRsp) msg.obj;
			// pageNum++;//页数自增
			// mSumCount=mSumCount+entry.list.size();//总条数
			// mSumCount=entry.getAmount();//总条数
			// mData=entry.list;//从服务器获取的数据
			// mDataSun.addAll(mData);//把新数据增加到展示data中去
			appTonesList = entry.list;// 从服务器获取的数据
			// data.setAdapter(mListViewAdapter);
			// mAllRingTones.add((Tone) mData);//把新数据增加到展示data中去

			// showData();
			break;
		case FusionCode.REQUEST_QRYRANKTONE_TOTAL:
			QryRankToneRsp entryRankTone2 = (QryRankToneRsp) msg.obj;
			// pageNum++;//页数自增
			// mSumCount=mSumCount+entryRankTone.list.size();//总条数
			// mSumCount=entryRankTone2.getAmount();//总条数
			appTonesList = entryRankTone2.list;// 从服务器获取的数据
			// data.setAdapter(mListViewAdapter);
			// mDataSun.addAll(mData);//把新数据增加到展示data中去
			break;
		case FusionCode.REQUEST_NEWRING:
			QryRankToneRsp entryRankTone3 = (QryRankToneRsp) msg.obj;
			appTonesList = entryRankTone3.list;
			break;

		default:
			break;
		}
		footerView.setVisibility(View.GONE);
		this.footerView.setPadding(0, -footerView.getHeight(), 0, 0);
		mListViewAdapter.notifyDataSetChanged();

	}

	@Override
	protected void reqError(Message msg) {
		super.reqError(msg);
		stopPbarU();
		footerView.setVisibility(View.GONE);
		mListViewAdapter.notifyDataSetChanged();
		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneByType:
			QryToneByTypeResp resp = (QryToneByTypeResp) msg.obj;
			Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(resp.getResult(), mContext));
			isLoadingDatas = false;
			isNeedToRefresh = false;
			checkScrollable();
			break;
		default:
			break;
		}
	}

	@Override
	protected void reqXmlFail(Message msg) {
		super.reqXmlFail(msg);
		footerView.setVisibility(View.GONE);
		mListViewAdapter.notifyDataSetChanged();
		stopPbarU();
		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneByType:
			QryToneByTypeResp resp = (QryToneByTypeResp) msg.obj;
			Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(resp.getResult(), mContext));
			isLoadingDatas = false;
			isNeedToRefresh = false;
			pageNo++;
			checkScrollable();
			break;

		default:
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.category_list_home_tone, null);

		initViews(view);

		mListViewAdapter = new ListViewAdapter(getActivity());
		mDatasListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mListViewAdapter.notifyDataSetChanged();
				mDatasListView.onRefreshComplete();
			}
		});

		data = mDatasListView.getRefreshableView();
		if (footerView == null) {
			footerView = LayoutInflater.from(getActivity()).inflate(R.layout.listview_rush, null);
		}
		data.addFooterView(footerView);
		data.setAdapter(mListViewAdapter);
		data.setOnScrollListener(onScrollListener);
		data.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (Utils.isFastDoubleClick()) {
					return;
				}
				stopMusic();
				// viewholder.mLoading.setAnimation(null);
				ToneInfo info = appTonesList.get(arg2);
				Intent intent = new Intent(getActivity(), DetailsRingtoneActivity.class);
				intent.putExtra("bean", info);
				startActivity(intent);
			}
		});
		setMyPullUpListViewCallBack(new MyPullUpListViewCallBack() {

			@Override
			public void scrollBottomState() {
				// TODO Auto-generated method stub
				switch (type) {
				case 1:
					QryRankTone qryRank1 = new QryRankTone(p_h);
					qryRank1.qryRankTone("2", pageNum * showNum, 0, 1);
					break;
				case 2:
					QryRecommendTone qryRecommended = new QryRecommendTone(p_h);
					qryRecommended.qryRecommendedTone("3", pageNum * showNum, 0);
					break;
				case 3:
					QryRankTone qryRank2 = new QryRankTone(p_h);
					qryRank2.qryRankTone("97", pageNum * showNum, 0, 3);
					break;
				case 4:
					QryRankTone qryRank3 = new QryRankTone(p_h);
					qryRank3.qryRankTone("5", pageNum * showNum, 0, 5);
					break;

				default:
					break;
				}

			}
		});

		// data.setOnScrollListener(new OnScrollListener() {
		//
		// @Override
		// public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// // TODO Auto-generated method stub
		// //当滑动到底部时
		// if (arg1 == OnScrollListener.SCROLL_STATE_IDLE
		// && firstVisibleItem != 0) {
		// getDataFromServer(id, 1, true);
		// }
		// }
		//
		// @Override
		// public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3)
		// {
		// // TODO Auto-generated method stub
		// arg1 = firstVisibleItem;
		// }
		// });
		// mAdapter = new HorizontalScrollViewAdapter(getActivity(),
		// appTonesList);
		// mHorizontalScrollView
		// .setCurrentImageChangeListener(new CurrentImageChangeListener() {
		// @Override
		// public void onCurrentImgChanged(int position,
		// View viewIndicator) {
		// if(viewIndicator == null)
		// {
		// return;
		// }
		// Button bt=(Button) viewIndicator.findViewById(R.id.horizon_bt);
		// bt.setVisibility(View.VISIBLE);
		// viewIndicator.findViewById(R.id.straight).setVisibility(View.GONE);;
		// viewIndicator.setBackgroundColor(Color.WHITE);
		// TextView text = (TextView) viewIndicator
		// .findViewById(R.id.show_horizon_info);
		// text.setTextColor(getResources().getColor(
		// R.color.list_tv_textcolor_selected));
		// }
		// });
		//
		// mHorizontalScrollView.setOnItemClickListener(new
		// OnItemClickListener() {
		//
		// @Override
		// public void onClick(View view, int position) {
		// if(Utils.isFastDoubleClick())
		// {
		// return;
		// }
		// LinearLayout line=(LinearLayout) mHorizontalScrollView.getChildAt(0);
		// int count=line.getChildCount();
		// for(int i=0;i<count;i++){
		// if(i!=position){
		// View v=line.getChildAt(i);
		// Button bt=(Button) v.findViewById(R.id.horizon_bt);
		// bt.setVisibility(View.GONE);
		// v.findViewById(R.id.straight).setVisibility(View.VISIBLE);
		// }
		// }
		// view.setBackgroundColor(Color.WHITE);
		// TextView text = (TextView) view
		// .findViewById(R.id.show_horizon_info);
		// text.setTextColor(getResources().getColor(
		// R.color.list_tv_textcolor_selected));
		// Button bt=(Button) view.findViewById(R.id.horizon_bt);
		// bt.setVisibility(View.VISIBLE);
		// view.findViewById(R.id.straight).setVisibility(View.VISIBLE);
		// currInCategoryPos = position;
		// mAllTones.clear();
		// mListViewAdapter.notifyDataSetChanged();
		// mDatasListView.setSelection(0);
		// pageNo = 0;
		// isLoadingDatas = true;
		// int pos = position + 1;
		// stopMusic();
		// getDataFromServer(appTonesList.get(position).getToneTypeID(), pos,
		// true);
		// }
		// });
		//
		// mHorizontalScrollView.initDatas(mAdapter);

		return view;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e("lc", "cao-nima--wanle--");
		MainGroupActivity.fragments.add("1");
		initDefault();
	}

	private void initDefault() {
		// startPbarU();
		if (appTonesList != null && appTonesList.size() > 0) {
			id = appTonesList.get(0).getCpID();
			mHorizontalScrollView.setVisibility(View.VISIBLE);
		} else {
			mHorizontalScrollView.setVisibility(View.GONE);
		}
		getDataFromServer(id, 1, true);
	}

	private void getDataFromServer(final String id, int pos, boolean needShow) {
		isLoadingDatas = true;
		// if (needShow) {
		// startPbarU();
		// }
		final int ids = pos + 2000;
		QryToneByTypeRequest request = new QryToneByTypeRequest(p_h);
		request.sendQryToneByTypeRequest(id, pageNo, showNum, ids);
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.title_iv:
				finish();
				break;

			default:
				break;
			}
		}
	};

	private void onDataFromServer(List<ToneInfo> tones) {
		// if(tones != null && tones.size() > 0)
		// {
		// mAllTones.addAll(tones);
		// }
		//
		// for (int i = 0; i < mAllTones.size(); i++) {
		// }
		checkScrollable();
		isLoadingDatas = false;
		mListViewAdapter.notifyDataSetChanged();
	}

	private void checkScrollable() {
		int temp = pageNo * showNum;
		int all = appTonesList.size();
		if (all >= temp) {
			isNeedToRefresh = true;
		} else {
			isNeedToRefresh = false;
		}
	}

	private OnScrollListener onScrollListener = new OnScrollListener() {
		private int state = -1;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (isLastRow && scrollState == OnScrollListener.SCROLL_STATE_IDLE && firstVisibleItems != 0) {
				if (size != appTonesList.size()) {
					size = appTonesList.size();
					myPullUpListViewCallBack.scrollBottomState();
					footerView.setVisibility(View.VISIBLE);
				} else {
					// footerView.setVisibility(View.GONE);
					data.removeFooterView(footerView);
					mListViewAdapter.notifyDataSetChanged();
				}
				isLastRow = false;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// if (isNeedToRefresh
			// && (state == SCROLL_STATE_IDLE || state == SCROLL_STATE_FLING)
			// && !isLoadingDatas) {
			// int last = firstVisibleItem + visibleItemCount;
			// int delt = totalItemCount - last;
			// if (delt == 4) {
			// if (isNeedToRefresh) {
			// if (appTonesList != null && appTonesList.size() > 0) {
			// getDataFromServer(
			// appTonesList.get(currInCategoryPos)
			// .getCpID(), currInCategoryPos + 1,
			// false);
			// } else {
			// getDataFromServer(id, 1, false);
			// }
			// }
			// }
			// }
			if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
				isLastRow = true;
			}
			firstVisibleItems = firstVisibleItem;

			// if (footerView != null) {
			// //判断可视Item是否能在当前页面完全显示
			// if (visibleItemCount == totalItemCount) {
			// // removeFooterView(footerView);
			// footerView.setVisibility(View.GONE);//隐藏底部布局
			// } else {
			// // addFooterView(footerView);
			// footerView.setVisibility(View.VISIBLE);//显示底部布局
			// }
			// }
		}
	};

	public void setMyPullUpListViewCallBack(MyPullUpListViewCallBack myPullUpListViewCallBack) {
		this.myPullUpListViewCallBack = myPullUpListViewCallBack;
	}

	/**
	 * 上拉刷新的ListView的回调监听
	 * 
	 * @author xiejinxiong
	 * 
	 */
	public interface MyPullUpListViewCallBack {

		void scrollBottomState();
	}

	/**
	 * 初始化播放器
	 */
	private void initMediaPlayer() {
		mediaPlayer = LocalMediaPlayer.getInstance();
		mediaPlayer.setCallback(onComplete);
		mediaPlayer.setOnPrepared(onPrepred);
		mediaPlayer.setErrorListener(errorListener);
		mediaPlayer.setException(exception);
	}

	onException exception = new onException() {

		@Override
		public void onException(int code, String des) {
			if (Utils.CheckTextNull(des)) {
				Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(des, mContext));
			}
			stopMusic();
			mListViewAdapter.notifyDataSetChanged();
			mListViewAdapter.pos = -1;
			isPlayer = false;
		}

	};

	/**
	 * 播放完成
	 */
	Complete onComplete = new Complete() {

		@Override
		public void onComplete() {
			mListViewAdapter.pos = -1;
			mListViewAdapter.notifyDataSetChanged();
			isPlayer = false;
		}

	};

	/**
	 * 播放准备
	 */
	onPrepared onPrepred = new onPrepared() {

		@Override
		public void onPrepared() {
			if (mListViewAdapter.pos != -1 && mListViewAdapter.pos != -2) {
				isPlayer = true;
				mListViewAdapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * 播放异常
	 */
	ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onError(MediaPlayer arg0, int arg1, int arg2) {
			stopMusic();
			mListViewAdapter.pos = -1;
			mListViewAdapter.notifyDataSetChanged();
			isPlayer = false;
		}

	};

	/**
	 * 停止播放
	 */
	/**
	 * 停止播放
	 */
	private void stopMusic() {
		isPlayer = false;
		mListViewAdapter.pos = -1;
		mListViewAdapter.notifyDataSetChanged();
		if (mediaPlayer == null) {
			return;
		}
		if (mediaPlayer.isPause() || mediaPlayer.isPlaying() || mediaPlayer.isprepared() || mediaPlayer.isLooped()) {
			mediaPlayer.cancelPlayer();
			initMediaPlayer();
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		pageNum = 1;
	}

	@Override
	public void onResume() {
		super.onResume();
		mListViewAdapter.notifyDataSetChanged();
		mDatasListView.onRefreshComplete();
		data.setAdapter(mListViewAdapter);
		
		initMediaPlayer();
		if (appTonesList.size() == 0) {
			image.setVisibility(View.VISIBLE);
		}
	}

	private void initViews(View view) {
		// mBackView = (ImageView) view.findViewById(R.id.title_iv);
		// mTitleView = (TextView) view.findViewById(R.id.title_tv);
		// mBackView.setOnClickListener(listener);
		// mTitleView.setText(title);
		// mBackView.setVisibility(View.VISIBLE);
		mHorizontalScrollView = (MyHorizontalScrollView) view.findViewById(R.id.id_horizontalScrollView);
		mDatasListView = (PullToRefreshListView) view.findViewById(R.id.list_pull_refresh_view);
		image = (ImageView) view.findViewById(R.id.list_image);
	}

	public class ListViewAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;
		public ViewHolder viewholder = null;

		public int pos = -2;

		public int old;

		private ListViewAdapter(Context context) {
			this.context = context;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (appTonesList == null) {

				return 0;
			}

			// else if(appTonesList.size()>10){
			// return 10;
			// }
			return appTonesList.size();
		}

		@Override
		public Object getItem(int position) {
			if (appTonesList == null) {
				return null;
			}
			return appTonesList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_list_fragment_layout, null);
				viewholder = new ViewHolder();
				viewholder.all_item = (RelativeLayout) convertView.findViewById(R.id.all_item);
				viewholder.singer = (TextView) convertView.findViewById(R.id.singer);
				viewholder.song = (TextView) convertView.findViewById(R.id.song);
				viewholder.mHot = (ImageView) convertView.findViewById(R.id.hot);
				viewholder.mIv_Play_Stop_Loading = (ImageView) convertView.findViewById(R.id.music_play_stop_loading);
				viewholder.mLoading = (ProgressBar) convertView.findViewById(R.id.music_loading);
				viewholder.round_bg = (RoundedImageView) convertView.findViewById(R.id.logo);
				convertView.setTag(viewholder);
			} else {
				viewholder = (ViewHolder) convertView.getTag();
			}
			viewholder.singer.setText(appTonesList.get(position).getSingerName());
			
//			viewholder.song.setText(appTonesList.get(position).getToneName());//Home Fragment 的排行的Adapter+"2222222"
			
//			EllipsizeUtils.textEllpsize(viewholder.song);
			viewholder.song.setText(EllipsizeUtils.textEllpsize(viewholder.song, appTonesList.get(position).getToneName(), mContext,240));
			Log.e("viewholder.song = ", viewholder.song.getText().toString()); 
			
			String saveDir = Commons.getImageSavedpath() + appTonesList.get(position).getToneClassID();
			if (new File(saveDir).exists()) {
				Bitmap bm = ImageDownLoader.getShareImageDownLoader().showCacheBitmap(saveDir);
				viewholder.round_bg.setImageBitmap(bm);
			}
			final int temp = position;
			if (position < 3) {
				viewholder.mHot.setVisibility(View.VISIBLE);
			} else {
				viewholder.mHot.setVisibility(View.GONE);
			}

			// viewholder.all_item.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			//
			// }
			// });

			viewholder.mIv_Play_Stop_Loading.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (Utils.isFastDoubleClick()) {
						return;
					}
					if (appTonesList == null)
						return;
					if (mediaPlayer == null) {
						initMediaPlayer();
					}

					int oldPos = pos;
					pos = temp;
					// 点击时间过快，不操作
					if (mediaPlayer.isPause() && oldPos == -1 && temp == old) {
						mediaPlayer.startPlayer();
						pos = temp;
						isPlayer = true;
						return;
					}
					if (oldPos == temp || oldPos == -2 || oldPos == -1) {
						if (oldPos != temp) {
							mediaPlayer.cancelPlayer();
							isPlayer = false;
							pos = position;
							mediaPlayer.getUrlFromServer(appTonesList.get(position).getToneID(),
									appTonesList.get(position).getToneType());
							notifyDataSetChanged();
							return;
						}
						if (mediaPlayer.isprepared()) {
							return;
						}
						if (!isPlayer) {
							pos = position;
							mediaPlayer.getUrlFromServer(appTonesList.get(position).getToneID(),
									appTonesList.get(position).getToneType());
							notifyDataSetChanged();

						} else {
							old = pos;
							pos = -1;
							notifyDataSetChanged();
							mediaPlayer.pause();
							isPlayer = false;
						}
					} else {
						mediaPlayer.cancelPlayer();
						pos = position;
						isPlayer = false;
						mediaPlayer.getUrlFromServer(appTonesList.get(position).getToneID(),
								appTonesList.get(position).getToneType());
						notifyDataSetChanged();
					}
				}
			});
			if (pos == position) {
				if (isPlayer) {
					// viewholder.mLoading.setAnimation(null);
					viewholder.mLoading.setVisibility(View.INVISIBLE);
					viewholder.mIv_Play_Stop_Loading.setBackgroundResource(R.drawable.music_stop);

				} else {
					// 处理试听加载效果
					viewholder.mIv_Play_Stop_Loading.setBackgroundResource(0);
					// if (viewholder.mLoading.getAnimation() == null) {
					// viewholder.mLoading.setAnimation(animation);
					// animation.startNow();
					// }
					viewholder.mLoading.setVisibility(View.VISIBLE);
				}
			} else {
				// viewholder.mLoading.setAnimation(null);
				viewholder.mLoading.setVisibility(View.INVISIBLE);
				viewholder.mIv_Play_Stop_Loading.setBackgroundResource(R.drawable.music_play);

			}
			return convertView;
		}
	}

	// private void packageData()
	// {
	// List<ToneInfo> lists = new ArrayList<ToneInfo>();
	// if(pageNo != 7)
	// {
	// for(int i = 0; i < 20; i++)
	// {
	// ToneInfo info = new ToneInfo();
	// String url = "http://221.226.179.185/music_mmh/drive.wav";
	// info.setSingerName("Jay");
	// info.setToneName("十月围城");
	// if(i % 2 == 0)
	// {
	// info.setSingerName("陈慧琳");
	// info.setToneName("记事本");
	// url = "http://221.226.179.185/music_mmh/advring/20150505145856896.wav";
	// }
	// info.setTonePreListenAddress(url);
	// lists.add(info);
	// }
	// }
	// else
	// {
	// for(int i = 0; i < 3; i++)
	// {
	// ToneInfo info = new ToneInfo();
	// info.setToneName("Recommond1");
	// info.setInfo(Constants.IMAGES[13]);
	// lists.add(info);
	// }
	// }
	//
	// onDataFromServer(lists);
	// }

	public class ViewHolder {
		RelativeLayout all_item;
		private ImageView mIv_Play_Stop_Loading, mHot;
		ProgressBar mLoading;
		private TextView song, singer;
		private RoundedImageView round_bg;
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mediaPlayer == null) {
			return;
		}
		if (mediaPlayer.isPlaying() || mediaPlayer.getState() == LocalMediaPlayer.PAUSE) {
			pause();
		} else {
			stopMusic();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mediaPlayer == null) {
			return;
		}
		if (mediaPlayer.isPlaying() || mediaPlayer.getState() == LocalMediaPlayer.PAUSE) {
			pause();
		} else {
			stopMusic();
		}
	}

	private void pause() {
		if (mediaPlayer.getState() != LocalMediaPlayer.PAUSE) {
			mListViewAdapter.old = mListViewAdapter.pos;
			mListViewAdapter.pos = -1;
			mListViewAdapter.notifyDataSetChanged();
			mediaPlayer.pause();
			isPlayer = false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MainGroupActivity.fragments.remove("1");
		if (mediaPlayer != null) {
			mediaPlayer.uninsMedia();
			mediaPlayer = null;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}
}
