package com.dxiang.mring3.fragment;//package com.dxiang.mring3.fragment;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.assist.FailReason;
//import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
//import com.dxiang.mring3.R;
//import com.dxiang.mring3.activity.MainGroupActivity;
//import com.dxiang.mring3.bean.ImageInfo;
//import com.dxiang.mring3.bean.Tone;
//import com.dxiang.mring3.bean.ToneInfo;
//import com.dxiang.mring3.bean.ToneTypeAPPInfo;
//import com.dxiang.mring3.db.DBOperator;
//import com.dxiang.mring3.db.DbHelper;
//import com.dxiang.mring3.file.SharedPreferenceService;
//import com.dxiang.mring3.request.GetimageinforReq;
//import com.dxiang.mring3.request.QryToneTypeAPPRequest;
//import com.dxiang.mring3.response.GetImageInfoRsp;
//import com.dxiang.mring3.response.QryToneTypeAPPResp;
//import com.dxiang.mring3.utils.Commons;
//import com.dxiang.mring3.utils.FusionCode;
//import com.dxiang.mring3.utils.Utils;
//import com.dxiang.mring3.utils.UtlisReturnCode;
//import com.dxiang.mring3.views.gallery.GalleryEx;
//import com.dxiang.mring3.views.refresh.PullToRefreshBase.OnRefreshListener;
//import com.dxiang.mring3.views.refresh.PullToRefreshListView;
//
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//public class HomeFragment_bottom_one extends BaseFragment {
//	int miRecommendListCount = 0;
//
//	// GalleryEx pagerCarrosel;
//	//
//	// private boolean isSelect = false;
//	//
//	// private Handler mHandlerPosterAutoScroll = new Handler();
//	//
//	// private int miBigPosterLastIndex = 0;
//
//	private BaseCommonAdapter mCommonAdapter;
//
//	private LinearLayout mLinLayoutBigPosterGuidePoint;
//
//	private View rootView;
//	private View Rank;
//
//	private PullToRefreshListView refresh;
//
//	private View header;
//
//	// 如果跳转到别的页面不需要刷新
//	private boolean needToFresh = true;
//
//	// 保存所有的一级和二级铃音
//	private List<ToneTypeAPPInfo> mAppTones;
//
//	// 用于展示所有的Tone 不管是推荐还是一级
//	private List<Tone> mAllRingTones = new ArrayList<Tone>();
//
//	private DisplayImageOptions options_Small;
//
//	private DisplayImageOptions options_Banner;
//
//	private DbHelper mDbHelper;
//
//	private HashMap<String, List<ToneTypeAPPInfo>> mCategoryTonesMap = new HashMap<String, List<ToneTypeAPPInfo>>();
//
//	private SharedPreferenceService mShared;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		rootView = inflater.inflate(R.layout.fragment_pull, null);
//		Rank = inflater.inflate(R.layout.topper_grally_head, null);
//		initView();
//
//		return rootView;
//
//	}
//
//	private void initView() {
//		refresh = (PullToRefreshListView) rootView.findViewById(R.id.pullto_refreash);
//		final ListView data = refresh.getRefreshableView();
//		data.setDividerHeight(30);
//		data.setVerticalScrollBarEnabled(false);
//		refresh.setOnRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				needToFresh = true;
//				// if (null != mHandlerPosterAutoScroll) {
//				// mHandlerPosterAutoScroll.removeCallbacks(autoFling);
//				// }
//				// getFirstLevelDataFromServer(false);
//				GetimageinforReq req = new GetimageinforReq(p_h);
//				req.sendGetimageinforReqRequest("3");
//			}
//		});
//
//		// initTopGallery();
//
//		data.addHeaderView(Rank);
//		data.setAdapter(mCommonAdapter);
//		data.setSelector(android.R.color.transparent);
//		data.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		mDbHelper = DbHelper.getInstance(getActivity());
//		mAppTones = new ArrayList<ToneTypeAPPInfo>();
//		mShared = SharedPreferenceService.getInstance(getActivity());
//		long time = mShared.getLong(Commons.TODAYTIME, 0l);
//		boolean needFreash = Utils.isNeedToLoadDatas(time);
//		mCommonAdapter = new BaseCommonAdapter();
//		// if(needFreash)
//		// {
//		// mShared.putLong(Commons.TODAYTIME, System.currentTimeMillis());
//		// getFirstLevelDataFromServer(true);
//		// }
//		// else
//		// {
//		// getFirstLevelDataFromDB();
//		// processData();
//		// }
//		GetimageinforReq req = new GetimageinforReq(p_h);
//		req.sendGetimageinforReqRequest("3");
//
//		options_Small = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_poster)
//				.showImageForEmptyUri(R.drawable.default_poster).showImageOnFail(R.drawable.default_poster)
//				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
//				.build();
//
//		options_Banner = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_banner)
//				.showImageForEmptyUri(R.drawable.default_banner).showImageOnFail(R.drawable.default_banner)
//				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
//				.build();
//	}
//
//	protected void reqXmlSucessed(Message msg) {
//		super.reqXmlSucessed(msg);
//
//		switch (msg.arg1) {
//		case FusionCode.REQUEST_QryToneTypeAPP:
//			stopPbarU();
//			//// if(!needToFresh)
//			//// {
//			//// if (null != mHandlerPosterAutoScroll) {
//			//// mHandlerPosterAutoScroll.postDelayed(autoFling, 100);
//			//// }
//			//// return;
//			//// }
//			ImageLoader.getInstance().clearDiscCache();
//			ImageLoader.getInstance().clearMemoryCache();
//			// mShared.put("isSplashImage", "2");
//			//// if (carroselAdapter != null) {
//			//// carroselAdapter.setListBan(Commons.mAdBanners);
//			//// carroselAdapter.notifyDataSetChanged();
//			//// carroselAdapter = null;
//			//// }
//			//// if (null != mHandlerPosterAutoScroll
//			//// && !"".equals(mHandlerPosterAutoScroll)) {
//			//// mHandlerPosterAutoScroll.removeCallbacks(autoFling);
//			//// }
//			// miRecommendListCount = Commons.mAdBanners.size();
//			//// creatPointGuide(miRecommendListCount);
//			//// setBannerAdapter(Commons.mAdBanners);
//			//// miBigPosterLastIndex = miRecommendListCount
//			//// % Integer.MAX_VALUE / 2;
//			//// mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
//			//// isSelect = false;
//			// refresh.onRefreshComplete();
//			// QryToneTypeAPPResp resp = (QryToneTypeAPPResp) msg.obj;
//			// List<ToneTypeAPPInfo> list = resp.getmToneInfos();
//			// mAppTones.clear();
//			// mAppTones.addAll(list);
//			// new Thread(run).start();
//			// processData();
//
//			break;
//
//		case FusionCode.REQUEST_GetImageInfo:
//			GetImageInfoRsp rsp = (GetImageInfoRsp) msg.obj;
//			List<ImageInfo> infos = rsp.getInfos();
//			mCommonAdapter.setImageInfos(infos);
//			mCommonAdapter.notifyDataSetChanged();
//			break;
//		default:
//			break;
//		}
//	}
//
//	@Override
//	protected void reqError(Message msg) {
//		super.reqError(msg);
//		switch (msg.arg1) {
//		case FusionCode.REQUEST_QryToneTypeAPP:
//			stopPbarU();
//			// if (null != mHandlerPosterAutoScroll) {
//			// mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
//			// }
//			refresh.onRefreshComplete();
//			processData();
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	@Override
//	protected void reqXmlFail(Message msg) {
//		super.reqXmlFail(msg);
//
//		switch (msg.arg1) {
//		case FusionCode.REQUEST_QryToneTypeAPP:
//			refresh.onRefreshComplete();
//			// if (null != mHandlerPosterAutoScroll) {
//			// mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
//			// }
//			QryToneTypeAPPResp resp = (QryToneTypeAPPResp) msg.obj;
//			Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(resp.getResult(), mContext));
//			stopPbarU();
//			getFirstLevelDataFromDB();
//			processData();
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	private void getFirstLevelDataFromServer(boolean needShow) {
//		if (needShow) {
//			startPbarU();
//		}
//		QryToneTypeAPPRequest tone = new QryToneTypeAPPRequest(p_h);
//		tone.sendQryToneTypeAPPRequest(100, 0);
//	}
//
//	private Runnable run = new Runnable() {
//		@Override
//		public void run() {
//			setDataIntoDb();
//		}
//	};
//
//	private void setDataIntoDb() {
//		if (mAppTones == null || mAppTones.size() == 0) {
//			return;
//		}
//		SQLiteDatabase db = mDbHelper.getWritableDatabase();
//		DBOperator.deleteRings(db);
//
//		List<ToneTypeAPPInfo> temp = new ArrayList<ToneTypeAPPInfo>();
//		temp.addAll(mAppTones);
//		for (ToneTypeAPPInfo info : temp) {
//			DBOperator.insertData(db, info);
//		}
//		db.close();
//
//	}
//
//	// 从数据库获取一级数据
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
//
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
//					if ("501".equals(id) || "502".equals(id) || "503".equals(id) || "504".equals(id)) {
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
//
//	// 体哪家
//	private void processData() {
//		// 添加推荐Item
//		ToneInfo info = new ToneInfo();
//		String recommended_list = Utils.getResouceStr(getActivity(), R.string.recommended_list);
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
//
//	private class BaseCommonAdapter extends BaseAdapter {
//
//		private List<ImageInfo> Infos;
//
//		public void setImageInfos(List<ImageInfo> infos) {
//			this.Infos = infos;
//		}
//
//		@Override
//		public int getCount() {
//			if (Infos == null) {
//				return 0;
//			}
//
//			return 2;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return 0;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			final ViewHolder holder;
//			if (convertView == null) {
//				holder = new ViewHolder();
//				convertView = getActivity().getLayoutInflater().inflate(R.layout.item_layout, null);
//				holder.item1 = (RelativeLayout) convertView.findViewById(R.id.item1);
//				holder.item2 = (RelativeLayout) convertView.findViewById(R.id.item2);
//				holder.data1 = (ImageView) convertView.findViewById(R.id.data1);
//				holder.data2 = (ImageView) convertView.findViewById(R.id.data2);
//				holder.data1_click = (Button) convertView.findViewById(R.id.data1_click);
//				holder.data2_click = (Button) convertView.findViewById(R.id.data2_click);
//				holder.data1_info = (TextView) convertView.findViewById(R.id.data1_text);
//				holder.data2_info = (TextView) convertView.findViewById(R.id.data2_text);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//				holder.data1.setBackgroundResource(R.drawable.default_poster);
//				holder.data2.setBackgroundResource(R.drawable.default_poster);
//			}
//			final int pos = position;
//			final int temp = position * 2;
//			// final int length = mAllRingTones.size();
//			final int length = Infos.size();
//			// 处理当前的数据条数是整数的部分
//			if (temp + 1 <= length - 1) {
//				// Tone toneFore = mAllRingTones.get(temp);
//				// Tone toneLast = mAllRingTones.get(temp + 1);
//
//				// if(pos == 0)
//				// {
//				// ToneInfo appTone = (ToneInfo) toneFore;
//				// holder.data1_info.setText(appTone.getToneName());
//				// holder.data1.setBackgroundResource(R.drawable.list);
//				// setUrlToImage(holder.data1, "list.png",
//				// options_Small, true, R.drawable.list);
//				// }
//				// else if(pos != 0)
//				// {
//				// System.out.println("--------------------------- pos = " +
//				// pos);
//				// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
//				// holder.data1_info.setText(appTone.getToneTypeLabel());
//				// setUrlToImage(holder.data1, appTone.getPicURL(),
//				// options_Small, true, R.drawable.default_poster);
//				// }
//
//				// if (toneLast instanceof ToneTypeAPPInfo) {
//				// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneLast;
//				// holder.data2_info.setText(appTone.getToneTypeLabel());
//				// setUrlToImage(holder.data2, appTone.getPicURL(),
//				// options_Small, true, R.drawable.default_poster);
//				// }
//				if (position == 0) {
//					ImageInfo info = Infos.get(temp + 1);
//					holder.data2_info.setText(" recommended");
//					setUrlToImage(holder.data2, info.getImageURL(), options_Small, true, R.drawable.default_poster);
//					ImageInfo info2 = Infos.get(temp);
//					holder.data1_info.setText("Top of the moment");
//					setUrlToImage(holder.data2, info2.getImageURL(), options_Small, true, R.drawable.default_poster);
//				} else if (position == 1) {
//					ImageInfo info = Infos.get(temp + 1);
//					holder.data2_info.setText(" The new");
//					setUrlToImage(holder.data2, info.getImageURL(), options_Small, true, R.drawable.default_poster);
//					ImageInfo info2 = Infos.get(temp);
//					holder.data1_info.setText("Top of the week");
//					setUrlToImage(holder.data2, info2.getImageURL(), options_Small, true, R.drawable.default_poster);
//				}
//
//				holder.item2.setVisibility(View.VISIBLE);
//			} else {
//				// Tone toneFore = mAllRingTones.get(length - 1);
//				// if (toneFore instanceof ToneTypeAPPInfo) {
//				// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
//				// holder.data1_info.setText(appTone.getToneTypeLabel());
//				// setUrlToImage(holder.data1, appTone.getPicURL(),
//				// options_Small, true, R.drawable.default_poster);
//				// }
//				// else if(toneFore instanceof ToneInfo)
//				// {
//				// ToneInfo appTone = (ToneInfo) toneFore;
//				// holder.data1_info.setText(appTone.getToneName());
//				// holder.data1.setBackgroundResource(R.drawable.list);
//				// setUrlToImage(holder.data1, "list.png",
//				// options_Small, true, R.drawable.list);
//				// }
//				// holder.item2.setVisibility(View.INVISIBLE);
//				
//
//			}
//
//			holder.data2_click.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					if (Utils.isFastDoubleClick()) {
//						return;
//					}
//					// if (temp + 1 <= length - 1) {
//					// Tone toneFore = mAllRingTones.get(temp + 1);
//					// if (toneFore instanceof ToneTypeAPPInfo) {
//					// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
//					//// Toast.makeText(getActivity(),
//					//// "right " + appTone.getToneTypeLabel(), 1)
//					//// .show();
//					// enterCategoryFragment(appTone.getToneTypeID());
//					// }
//					// }
//
//				}
//			});
//
//			holder.data1_click.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					if (Utils.isFastDoubleClick()) {
//						return;
//					}
//					Tone toneFore = mAllRingTones.get(pos * 2);
//					if (toneFore instanceof ToneTypeAPPInfo) {
//						ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
//						// Toast.makeText(getActivity(),
//						// "left " + appTone.getToneTypeLabel(), 1).show();
//						enterCategoryFragment(appTone.getToneTypeID());
//					} else {
//						// ToneInfo appTone = (ToneInfo) toneFore;
//						// Toast.makeText(getActivity(),
//						// "left " + appTone.getToneName(), 1).show();
//						enterListFragment();
//					}
//				}
//			});
//
//			holder.data1.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					if (Utils.isFastDoubleClick()) {
//						return;
//					}
//					// Tone toneFore = mAllRingTones.get(pos * 2);
//					// if (toneFore instanceof ToneTypeAPPInfo) {
//					// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
//					//// Toast.makeText(getActivity(),
//					//// "left " + appTone.getToneTypeLabel(), 1).show();
//					// enterCategoryFragment(appTone.getToneTypeID());
//					// } else {
//					// // Enter List
//					// enterListFragment();
//					// }
//
//				}
//			});
//
//			holder.data2.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					if (Utils.isFastDoubleClick()) {
//						return;
//					}
//					if (temp + 1 <= length - 1) {
//						Tone toneFore = mAllRingTones.get(temp + 1);
//						ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
//						// Toast.makeText(getActivity(),
//						// "right " + appTone.getToneTypeLabel(), 1)
//						// .show();
//						// 进入铃音列表页面
//						enterCategoryFragment(appTone.getToneTypeID());
//					}
//				}
//			});
//
//			return convertView;
//		}
//
//		class ViewHolder {
//			ImageView data1;
//			ImageView data2;
//			TextView data1_info;
//			TextView data2_info;
//			Button data1_click;
//			Button data2_click;
//			RelativeLayout item1;
//			RelativeLayout item2;
//		}
//
//		private void enterListFragment() {
//			// refresh.onRefreshComplete();
//			needToFresh = false;
//			// if (null != mHandlerPosterAutoScroll) {
//			// mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
//			// }
//			MainGroupActivity activity = (MainGroupActivity) getActivity();
//			activity.skipToListFragment(true);
//		}
//
//		private void enterCategoryFragment(String key) {
//			List<ToneTypeAPPInfo> lists = mCategoryTonesMap.get(key);
//			CategoryRingToneListFragment fragment = new CategoryRingToneListFragment();
//			fragment.setToneList(lists);
//			// int i = 0;
//			// for(; i < mAllRingTones.size(); i++)
//			// {
//			// Tone info = mAllRingTones.get(i);
//			// if(info instanceof ToneTypeAPPInfo)
//			// {
//			// if(((ToneTypeAPPInfo) info).getToneTypeID() == key)
//			// {
//			// fragment.setTitle(((ToneTypeAPPInfo) info).getToneTypeLabel());
//			// break;
//			// }
//			// }
//			// }
//			// if(needToFresh)
//			// {
//			// refresh.onRefreshComplete();
//			// needToFresh = false;
//			// }
//			fragment.setId(key);
//			openFragment(fragment);
//		}
//
//	}
//
//	private void setUrlToImage(final ImageView holder, String url, DisplayImageOptions options, boolean needAdd,
//			final int id) {
//		if (needAdd) {
//			url = Commons.IMAGEURL + "/" + url;
//		}
//		ImageLoader.getInstance().displayImage(url, holder, options, new ImageLoadingListener() {
//
//			@Override
//			public void onLoadingStarted(String arg0, View arg1) {
//				holder.setImageBitmap(null);
//				holder.setBackgroundResource(id);
//			}
//
//			@Override
//			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//				holder.setImageBitmap(null);
//				holder.setBackgroundResource(id);
//			}
//
//			@Override
//			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
//				if (arg2 == null) {
//					arg1.setBackgroundResource(id);
//				} else {
//					arg1.setBackgroundResource(0);
//					holder.setImageBitmap(arg2);
//				}
//			}
//
//			@Override
//			public void onLoadingCancelled(String arg0, View arg1) {
//			}
//		});
//	}
//}
