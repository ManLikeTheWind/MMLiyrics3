package com.dxiang.mring3.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.gem.imgcash.ImageDownLoader;
import com.gem.imgcash.ImageDownLoader.onImageLoaderListener;
import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.CaptureActivity;
import com.dxiang.mring3.activity.DetailsRingtoneActivity;
import com.dxiang.mring3.activity.MainGroupActivity;
import com.dxiang.mring3.activity.SearchActivity;
import com.dxiang.mring3.bean.ImageInfo;
import com.dxiang.mring3.bean.Tone;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.bean.ToneTypeAPPInfo;
import com.dxiang.mring3.db.DBOperator;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.GetimageinforReq;
import com.dxiang.mring3.request.QryToneTypeAPPRequest;
import com.dxiang.mring3.response.GetImageInfoRsp;
import com.dxiang.mring3.response.QryToneTypeAPPResp;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FileUtil;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;
import com.dxiang.mring3.utils.WindowArg;
import com.dxiang.mring3.views.gallery.GalleryEx;

import com.dxiang.mring3.views.refresh.PullToRefreshListView;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NewListFragment extends BaseFragment {
	public static final String TAG = "newListFragment";

	int miRecommendListCount = 0;

	GalleryEx pagerCarrosel;

	private boolean isSelect = false;

	private Handler mHandlerPosterAutoScroll = new Handler();

	private int miBigPosterLastIndex = 0;

	AdapterMovieCarrosel carroselAdapter;

//	private BaseCommonAdapter mCommonAdapter;

	private LinearLayout mLinLayoutBigPosterGuidePoint;

	private View rootView;

	private PullToRefreshListView refresh;

	private View header;

	// 如果跳转到别的页面不需要刷新
	private boolean needToFresh = true;
	Bitmap mBgBitmap;

	// 保存所有的一级和二级铃音
//	private List<ToneTypeAPPInfo> mAppTones;

	// 用于展示所有的Tone 不管是推荐还是一级
//	private List<Tone> mAllRingTones = new ArrayList<Tone>();

//	private DisplayImageOptions options_Small;
//
//	private DisplayImageOptions options_Banner;

//	private DbHelper mDbHelper;

//	private HashMap<String, List<ToneTypeAPPInfo>> mCategoryTonesMap = new HashMap<String, List<ToneTypeAPPInfo>>();

	private SharedPreferenceService mShared;
	private List<ImageInfo> infos;


	// 刷新成功对应不在当前页面不需要刷新，每天首次进入需要刷新
	@SuppressWarnings("deprecation")
	@Override
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);

		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneTypeAPP:
			break;

		case FusionCode.REQUEST_GetImageInfo:
			GetImageInfoRsp rsp = (GetImageInfoRsp) msg.obj;
			infos = rsp.getInfos();

			// stopPbarU();
			// if(!needToFresh)
			// {
			// if (null != mHandlerPosterAutoScroll) {
			// mHandlerPosterAutoScroll.postDelayed(autoFling, 100);
			// }
			// return;
			// }
			// ImageLoader.getInstance().clearDiscCache();
			// ImageLoader.getInstance().clearMemoryCache();
			// mShared.put("isSplashImage", "2");
			// if (carroselAdapter != null) {
			// carroselAdapter.setListBan(infos);
			// carroselAdapter.notifyDataSetChanged();
			// carroselAdapter = null;
			// }
			// if (null != mHandlerPosterAutoScroll
			// && !"".equals(mHandlerPosterAutoScroll)) {
			// mHandlerPosterAutoScroll.removeCallbacks(autoFling);
			// }
			miRecommendListCount = infos.size();
			creatPointGuide(miRecommendListCount);
			setBannerAdapter(infos);
			// miBigPosterLastIndex = miRecommendListCount
			// % Integer.MAX_VALUE / 2;
			mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			isSelect = false;
			// // refresh.onRefreshComplete();
			// QryToneTypeAPPResp resp = (QryToneTypeAPPResp) msg.obj;
			// List<ToneTypeAPPInfo> list = resp.getmToneInfos();
			// mAppTones.clear();
			// mAppTones.addAll(list);
			// new Thread(run).start();
			// processData();
			break;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void reqError(Message msg) {
		super.reqError(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneTypeAPP:
			stopPbarU();
			if (null != mHandlerPosterAutoScroll) {
				mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			}
			// refresh.onRefreshComplete();
//			processData();
			break;

		case FusionCode.REQUEST_GetImageInfo:
			stopPbarU();
			if (null != mHandlerPosterAutoScroll) {
				mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			}
			// refresh.onRefreshComplete();
//			processData();
			mBgBitmap=ImageDownLoader.getShareImageDownLoader().getBitmapFromMemCache("default_banner");
			if(mBgBitmap==null){
				mBgBitmap = FileUtil.decodeSampledBitmapFromResource(
						getResources(), R.drawable.default_banner, w, h / 4);
				ImageDownLoader.getShareImageDownLoader().addBitmapToMemoryCache("default_banner", mBgBitmap);
			}
			pagerCarrosel.setBackground(new BitmapDrawable(mBgBitmap));
			creatPointGuide(0);
			break;
		}
	}

	@Override
	protected void reqXmlFail(Message msg) {
		super.reqXmlFail(msg);

		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneTypeAPP:
			// refresh.onRefreshComplete();
			if (null != mHandlerPosterAutoScroll) {
				mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			}
			QryToneTypeAPPResp resp = (QryToneTypeAPPResp) msg.obj;
			Utils.showTextToast(getActivity(),
					UtlisReturnCode.ReturnCode(resp.getResult(), mContext));
			stopPbarU();
//			getFirstLevelDataFromDB();
//			processData();
			break;

		case FusionCode.REQUEST_GetImageInfo:
			if (null != mHandlerPosterAutoScroll) {
				mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			}
			QryToneTypeAPPResp resp1 = (QryToneTypeAPPResp) msg.obj;
			Utils.showTextToast(getActivity(),
					UtlisReturnCode.ReturnCode(resp1.getResult(), mContext));
			stopPbarU();
//			getFirstLevelDataFromDB();
//			processData();
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		mDbHelper = DbHelper.getInstance(getActivity());
//		mAppTones = new ArrayList<ToneTypeAPPInfo>();
		mShared = SharedPreferenceService.getInstance(getActivity());
		long time = mShared.getLong(Commons.TODAYTIME, 0l);
		boolean needFreash = Utils.isNeedToLoadDatas(time);
//		mCommonAdapter = new BaseCommonAdapter();
		if (needFreash) {
			mShared.putLong(Commons.TODAYTIME, System.currentTimeMillis());
			// getFirstLevelDataFromServer(true);
		} else {
			// getFirstLevelDataFromDB();
			// processData();
		}

//		options_Small = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.default_poster)
//				.showImageForEmptyUri(R.drawable.default_poster)
//				.showImageOnFail(R.drawable.default_poster).cacheInMemory(true)
//				.cacheOnDisk(true).considerExifParams(true)
//				.bitmapConfig(Bitmap.Config.RGB_565).build();
//
//		options_Banner = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.default_banner)
//				.showImageForEmptyUri(R.drawable.default_banner)
//				.showImageOnFail(R.drawable.default_banner).cacheInMemory(true)
//				.cacheOnDisk(true).considerExifParams(true)
//				.bitmapConfig(Bitmap.Config.RGB_565).build();
		GetimageinforReq req = new GetimageinforReq(p_h);
		req.sendGetimageinforReqRequest("1");
	}

	// //服务器获取一级数据
	// private void getFirstLevelDataFromServer(boolean needShow)
	// {
	// if(needShow)
	// {
	// startPbarU();
	// }
	// QryToneTypeAPPRequest tone = new QryToneTypeAPPRequest(p_h);
	// tone.sendQryToneTypeAPPRequest(100, 0);
	// }

//	private Runnable run = new Runnable() {
//		@Override
//		public void run() {
//			setDataIntoDb();
//		}
//	};

//	private void setDataIntoDb() {
//		if (mAppTones == null || mAppTones.size() == 0) {
//			return;
//		}
//		SQLiteDatabase db = mDbHelper.getReadableDatabase();
//		DBOperator.deleteRings(db);
//		db.close();

//		List<ToneTypeAPPInfo> temp = new ArrayList<ToneTypeAPPInfo>();
//		temp.addAll(mAppTones);
//		SQLiteDatabase db2 = mDbHelper.getReadableDatabase();
//		for (ToneTypeAPPInfo info : temp) {
//			DBOperator.insertData(db2, info);
//		}
//		db2.close();
//	}

	// 从数据库获取一级数据
//	private int getFirstLevelDataFromDB() {
//		SQLiteDatabase db = mDbHelper.getReadableDatabase();
//		Cursor cursor = DBOperator.queryAllRings(db);
//		if (cursor == null || cursor.getCount() == 0) {
//			return -1;
//		}
//		int ifleafnod = cursor.getColumnIndex(DbHelper.IFLEAFNOD);
//		int parenttypeid = cursor.getColumnIndex(DbHelper.PARENTTYPEID);
//		int picurl = cursor.getColumnIndex(DbHelper.PICURL);
//		int tonetypeid = cursor.getColumnIndex(DbHelper.TONETYPEID);
//		int tonetypelabel = cursor.getColumnIndex(DbHelper.TONETYPELABEL);
//		if (mAppTones != null && mAppTones.size() > 0) {
//			mAppTones.clear();
//		}
//		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//			ToneTypeAPPInfo info = new ToneTypeAPPInfo();
//			String nod = cursor.getString(ifleafnod);
//			String parentid = cursor.getString(parenttypeid);
//			String url = cursor.getString(picurl);
//			String id = cursor.getString(tonetypeid);
//			String label = cursor.getString(tonetypelabel);
//			info.setIfLeafNod(nod);
//			info.setParentTypeID(parentid);
//			info.setPicURL(url);
//			info.setToneTypeID(id);
//			info.setToneTypeLabel(label);
//
//			mAppTones.add(info);
//		}
//		cursor.close();
//		db.close();
//		return 0;
//	}

	// 体哪家
//	private void processData() {
		// 添加推荐Item
//		ToneInfo info = new ToneInfo();
//		String recommended_list = Utils.getResouceStr(getActivity(),
//				R.string.recommended_list);
//		info.setToneName(recommended_list.toString());
//
//		if (mAllRingTones.size() > 0) {
//			mAllRingTones.clear();
//		}
//		mCategoryTonesMap.clear();
//		mAllRingTones.add(info);
//		addFirstToAll();
//		mCommonAdapter.notifyDataSetChanged();
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater
					.inflate(R.layout.activity_newlistfragment, null);
			// pullFragment=new PullFragment();
			initView();
			initCLickListener();
			PullListViewFragment pull = new PullListViewFragment();
			getChildFragmentManager()
					.beginTransaction()
					.replace(R.id.container_list, pull,
							"pullistListViewFragment").commit();
		}

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

//	private void addFirstToAll() {
//		if (mAppTones == null || mAppTones.size() == 0) {
//			return;
//		}
//
//		for (int i = 0; i < mAppTones.size(); i++) {
//			ToneTypeAPPInfo tone = mAppTones.get(i);
//			String key = tone.getParentTypeID();
//			String id = tone.getToneTypeID();
//			// 判断parenttypeid 是 0 就是一级还需要过滤掉ToneTypeID =
//			// 501,502,503,504对应的榜单数据，不是
//			if ("0".equals(key)) {
//				if (Utils.CheckTextNull(id)) {
//					if ("501".equals(id) || "502".equals(id)
//							|| "503".equals(id) || "504".equals(id)) {
//						continue;
//					} else {
//						mAllRingTones.add(tone);
//					}
//				}
//			} else {
//				if (mCategoryTonesMap.containsKey(key)) {
//					List<ToneTypeAPPInfo> temp = mCategoryTonesMap.get(key);
//					temp.add(tone);
//				} else {
//					List<ToneTypeAPPInfo> temp = new ArrayList<ToneTypeAPPInfo>();
//					temp.add(tone);
//					mCategoryTonesMap.put(key, temp);
//				}
//			}
//		}
//	}

	private void initView() {
		initTopGallery();
	}

	private void initCLickListener() {
		rootView.findViewById(R.id.search_button).setOnClickListener(
				mAllViewListener);
		rootView.findViewById(R.id.search_container).setOnClickListener(
				mAllViewListener);
		rootView.findViewById(R.id.zxing).setOnClickListener(mAllViewListener);
		rootView.findViewById(R.id.zxing_container).setOnClickListener(
				mAllViewListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (null != mHandlerPosterAutoScroll) {
			mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
		}
		
		//此处使用来刷新里面的TopView
//		initView();
		
	}

	@SuppressWarnings("deprecation")
	private View initTopGallery() {
		if (header == null) {
			// header = getActivity().getLayoutInflater().inflate(
			// R.layout.topper_gallery_layout, null);
			header = rootView.findViewById(R.id.gallery_topper);
			pagerCarrosel = (GalleryEx) header
					.findViewById(R.id.gallery_gallery);
			mLinLayoutBigPosterGuidePoint = (LinearLayout) header
					.findViewById(R.id.guide_activity_newvod_point);
			pagerCarrosel.setAnimationDuration(800);
			pagerCarrosel.setNoFling(true);
			pagerCarrosel.dispatchSetSelected(false);
			pagerCarrosel.setCallbackDuringFling(true);
			pagerCarrosel
					.setOnItemSelectedListener(new OnPosterSelectedListener());
			pagerCarrosel.setOnTouchListener(touch);
			pagerCarrosel.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					int pos = arg2 % miRecommendListCount;
					final String baner = infos.get(pos).getImageURL();
					if(getUrlSplit(baner).length()>4){
					Intent intent = new Intent(getActivity(),
							DetailsRingtoneActivity.class);
					intent.putExtra("code", getUrlSplit(baner));
					intent.putExtra("type", 1);
					startActivity(intent);}
				}
			});
			creatPointGuide(HomeFragment.PointGuideCount);
		}

		setBannerAdapter(infos);

		return header;
	}

	private void setBannerAdapter(List<ImageInfo> infos) {
		if (infos == null || infos.size() == 0) {
			return;
		}
		if (getActivity() == null) {
			return;
		}
		if (carroselAdapter == null) {
			carroselAdapter = new AdapterMovieCarrosel(getActivity(), infos);
			int iCurSelItem = Integer.MAX_VALUE / 2;
			if (0 > miRecommendListCount) {
				iCurSelItem = 0;
			} else if (miRecommendListCount == 1) {
				iCurSelItem = 0;
			} else if (1 < miRecommendListCount) {
				int iCurTemp = iCurSelItem % miRecommendListCount;

				if (0 != iCurTemp) {
					iCurSelItem = iCurSelItem - iCurTemp;
				}
			}
			pagerCarrosel.setSelection(iCurSelItem);
		}
		pagerCarrosel.setAdapter(carroselAdapter);
		// mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
	}

	private OnClickListener mAllViewListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.zxing:
			case R.id.zxing_container:
				enterZxing();
				break;
			case R.id.search_button:
			case R.id.search_container:
				enterSearch();
			default:
				break;
			}
		}

		private void enterSearch() {
			if (needToFresh) {
				// refresh.onRefreshComplete();
				needToFresh = false;
			}
			if (null != mHandlerPosterAutoScroll) {
				mHandlerPosterAutoScroll.removeCallbacks(autoFling);
			}
			Intent intent = new Intent(getActivity(), SearchActivity.class);
			getActivity().startActivity(intent);
		}

		private void enterZxing() {
			if (needToFresh) {
				// refresh.onRefreshComplete();
				needToFresh = false;
			}
			if (null != mHandlerPosterAutoScroll) {
				mHandlerPosterAutoScroll.removeCallbacks(autoFling);
			}
			Intent intent = new Intent(getActivity(), CaptureActivity.class);
			startActivity(intent);
		}
	};

	private Runnable autoFling = new Runnable() {
		@SuppressWarnings("deprecation")
		public void run() {
			if (isSelect) {
				return;
			}
			if (pagerCarrosel == null) {
				pagerCarrosel = new GalleryEx(getActivity());
			}

			if (infos!=null) {
				pagerCarrosel.onKeyDown(
						android.view.KeyEvent.KEYCODE_DPAD_RIGHT, null);
			}
			mHandlerPosterAutoScroll.removeCallbacks(autoFling);
			mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
		};

	};

	private OnTouchListener touch = new OnTouchListener() {

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			switch (view.getId()) {
			case R.id.gallery_gallery: {
				if (0 == miRecommendListCount) {
					break;
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					isSelect = true;
					if (null != mHandlerPosterAutoScroll) {
						mHandlerPosterAutoScroll.removeCallbacks(autoFling);
					}
				} else if (event.getAction() == MotionEvent.ACTION_SCROLL) {
					isSelect = false;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					isSelect = false;
					if (null != mHandlerPosterAutoScroll) {
						mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
					}
				}
				break;
			}
			default:
				break;
			}

			return false;
		}
	};

	private void creatPointGuide(int iPointNum) {
		LinearLayout layoutPointGuide = mLinLayoutBigPosterGuidePoint;
		if (null == layoutPointGuide) {
			return;
		} else {
			layoutPointGuide.removeAllViews();
		}
		for (int i = 0; i < iPointNum; i++) {
			if (getActivity() == null) {
				continue;
			}
			ImageView imageView = new ImageView(getActivity());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(10, 0, 10, 0);
			imageView.setLayoutParams(layoutParams);
			if (i == 0 && i < iPointNum) {
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.vpi__tab_selected_holo));
			} else {
				imageView.setImageDrawable(getResources().getDrawable(
						R.drawable.vpi__tab_unselected_holo));
			}
			layoutPointGuide.addView(imageView);
		}

	}

	// Gallery Adapter
	@SuppressLint("NewApi")
	class AdapterMovieCarrosel extends BaseAdapter {
		private List<ImageInfo> infos;

		private LayoutInflater mInflater;

//		int x;
//
//		int y;

		Queue<ViewHolder> queue = new LinkedList<ViewHolder>();

		@SuppressWarnings("deprecation")
		public AdapterMovieCarrosel(Context context, List<ImageInfo> infos) {
			this.infos = infos;
			mContext = context;
			mInflater = LayoutInflater.from(context);
			miRecommendListCount = infos == null ? 0 : infos.size();
//			x = getActivity().getWindowManager().getDefaultDisplay().getWidth();
//			y = getActivity().getWindowManager().getDefaultDisplay()
//					.getHeight();
		}

		public void setListBan(List<ImageInfo> infos) {
			this.infos = infos;
			notifyDataSetChanged();
			miRecommendListCount = infos == null ? 0 : infos.size();
		}

		@Override
		public int getCount() {
			if (infos == null) {
				return 0;
			} else if (infos.size() == 1) {
				return 1;
			}
			return Integer.MAX_VALUE;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
				final ViewHolder holder;
				// if(queue.size()<2)
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.item_ad_layout,
							null);
					if (queue.size() < 2) {
						holder = new ViewHolder();
					} else {
						holder = queue.poll();
					}
					holder.imgImageView = (ImageView) convertView
							.findViewById(R.id.logo);
					holder.imgImageView
							.setLayoutParams(new android.widget.Gallery.LayoutParams(
									w, w / 5 * 2 - 20));
					convertView.setTag(holder);
					queue.add(holder);
				} else {
					// convertView = queue.poll();
					Log.d("test", "===convetView==222=");
					holder = (ViewHolder) convertView.getTag();
				}

				int pos = position % miRecommendListCount;
				final String baner = infos.get(pos).getImageURL();
//				setUrlToView(holder.imgImageView, baner, options_Banner, false,
//						R.drawable.default_banner);
//				holder.imgImageView.setTag(baner);
			    String saveDir = Commons.getImageSavedpath()
							+ baner.replaceAll("[^\\w]", "");
		        Bitmap bm=ImageDownLoader.getShareImageDownLoader().showCacheBitmap(saveDir);
                if(bm==null){
                	holder.imgImageView.setBackgroundResource(R.drawable.default_banner);
//                	final ImageView iv=(ImageView) convertView.findViewWithTag(baner);
                	ImageDownLoader.getShareImageDownLoader().downloadImage(saveDir, baner,w,h/4, new onImageLoaderListener() {
                		
                		@Override
                		public void onImageLoader(Bitmap arg0, String arg1) {
                			// TODO Auto-generated method stub
                			holder.imgImageView.setImageBitmap(arg0);
                		}
                	});
                }else{
                	holder.imgImageView.setImageBitmap(bm);
                }

			return convertView;
		}

		@Override
		public Object getItem(int position) {
			if (infos == null) {
				return null;
			} else {
				return infos.get(position % miRecommendListCount);
			}
		}

		class ViewHolder {
			ImageView imgImageView;
		}
	}

	String getUrlSplit(String url) {
		String[] ids = url.split("/", -1);
		String te = ids[ids.length - 1];
		String[] is = te.split("\\.", -1);
		return is[0];
	}

//	private void setUrlToView(final ImageView view, String url,
//			DisplayImageOptions options, boolean needAdd, final int id) {
//		if (needAdd) {
//			url = Commons.IMAGEURL + "/" + url;
//		}
//		ImageLoader.getInstance().displayImage(url, view, options,
//				new ImageLoadingListener() {
//
//					@Override
//					public void onLoadingStarted(String arg0, View arg1) {
//					}
//
//					@Override
//					public void onLoadingFailed(String arg0, View arg1,
//							FailReason arg2) {
//						view.setBackgroundResource(id);
//						view.setImageBitmap(null);
//					}
//
//					@Override
//					public void onLoadingComplete(String arg0, View arg1,
//							Bitmap arg2) {
//						if (arg2 == null) {
//							arg1.setBackgroundResource(id);
//						} else {
//							arg1.setBackgroundResource(0);
//							view.setImageBitmap(arg2);
//						}
//					}
//
//					@Override
//					public void onLoadingCancelled(String arg0, View arg1) {
//					}
//				});
//	}
//
//	private void setUrlToImage(final ImageView holder, String url,
//			DisplayImageOptions options, boolean needAdd, final int id) {
//		if (needAdd) {
//			url = Commons.IMAGEURL + "/" + url;
//		}
//		ImageLoader.getInstance().displayImage(url, holder, options,
//				new ImageLoadingListener() {
//
//					@Override
//					public void onLoadingStarted(String arg0, View arg1) {
//						holder.setImageBitmap(null);
//						holder.setBackgroundResource(id);
//					}
//
//					@Override
//					public void onLoadingFailed(String arg0, View arg1,
//							FailReason arg2) {
//						holder.setImageBitmap(null);
//						holder.setBackgroundResource(id);
//					}
//
//					@Override
//					public void onLoadingComplete(String arg0, View arg1,
//							Bitmap arg2) {
//						if (arg2 == null) {
//							arg1.setBackgroundResource(id);
//						} else {
//							arg1.setBackgroundResource(0);
//							holder.setImageBitmap(arg2);
//						}
//					}
//
//					@Override
//					public void onLoadingCancelled(String arg0, View arg1) {
//					}
//				});
//	}

	private class OnPosterSelectedListener implements
			AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int iposition, long arg3) {
			Log.d("home", "===enter===");

			ImageView imageViewFoc = (ImageView) mLinLayoutBigPosterGuidePoint
					.getChildAt(miBigPosterLastIndex);
			if (imageViewFoc == null) {
				return;
			}
			imageViewFoc.setImageResource(R.drawable.vpi__tab_unselected_holo);

			//
			final int position = iposition % miRecommendListCount;
			miBigPosterLastIndex = position;
			ImageView imageView = (ImageView) mLinLayoutBigPosterGuidePoint
					.getChildAt(miBigPosterLastIndex);
			imageView.setImageResource(R.drawable.vpi__tab_selected_holo);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		// refresh.onRefreshComplete();
		needToFresh = false;
		if (null != mHandlerPosterAutoScroll) {
			mHandlerPosterAutoScroll.removeCallbacks(autoFling);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (null != mHandlerPosterAutoScroll) {
			mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
		}
	}

}
