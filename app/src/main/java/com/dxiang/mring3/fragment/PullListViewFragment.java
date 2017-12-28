package com.dxiang.mring3.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.gem.imgcash.ImageDownLoader;
import com.gem.imgcash.ImageDownLoader.onImageLoaderListener;
import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.MainGroupActivity;
import com.dxiang.mring3.bean.Tone;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.bean.ToneTypeAPPInfo;
import com.dxiang.mring3.db.DBOperator;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.fragment.HomeFragmentBottomOne.RefreshTask;
import com.dxiang.mring3.fragment.NewListFragment.AdapterMovieCarrosel;
import com.dxiang.mring3.request.GetimageinforReq;
import com.dxiang.mring3.request.QryToneTypeAPPRequest;
import com.dxiang.mring3.request.QueryRecommendedRingRequest;
import com.dxiang.mring3.response.GetImageInfoRsp;
import com.dxiang.mring3.response.QryToneTypeAPPResp;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;
import com.dxiang.mring3.utils.WindowArg;
import com.dxiang.mring3.views.refresh.PullToRefreshBase.OnRefreshListener;
import com.dxiang.mring3.views.refresh.PullToRefreshGridView;
import com.dxiang.mring3.views.refresh.PullToRefreshListView;

import android.database.Cursor;
import android.database.CursorJoiner.Result;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PullListViewFragment extends BaseFragment {
//	private List<Tone> mAllRingTones = new ArrayList<Tone>();
	private PullToRefreshListView refresh;
	private BaseCommonAdapter mCommonAdapter;
	// private DisplayImageOptions options_Small;
	// private DisplayImageOptions options_Banner;
	private boolean needToFresh = true;
	private List<ToneTypeAPPInfo> list;
	private SharedPreferenceService mShared;
	private PullToRefreshGridView mPullRefreshGridView;
	private LinearLayout linear_failed;
	private GridView mGridView;
	// private View head;
	private boolean isFirstEnter = true;
	private int mFirstVisibleItem;
	private int mVisibleItemCount;
	View v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = inflater.inflate(R.layout.fragment_list_pull, null);
		// head = inflater.inflate(R.layout.topper_grally_head, null);
		initView();
		return v;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		mShared = SharedPreferenceService.getInstance(getActivity());
		long time = mShared.getLong(Commons.TODAYTIME, 0l);
		boolean needFreash = Utils.isNeedToLoadDatas(time);
		// mCommonAdapter = new BaseCommonAdapter();
		// GetimageinforReq rep = new GetimageinforReq(p_h);
		// rep.sendGetimageinforReqRequest("2");
		// QueryRecommendedRingRequest re=new QueryRecommendedRingRequest(p_h);
		// re.sendQueryRecommendedRingRequest(2+"", 100, 0);

		if (needFreash) {
			mShared.putLong(Commons.TODAYTIME, System.currentTimeMillis());
			getFirstLevelDataFromServer(needFreash);
		} else {
			// getFirstLevelDataFromDB();
			processData();
			QryToneTypeAPPRequest tone = new QryToneTypeAPPRequest(p_h);
			tone.sendQryToneTypeAPPRequest(100, 0);
		}
//		setDataIntoDb();
		// options_Small = new
		// DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_poster)
		// .showImageForEmptyUri(R.drawable.default_poster).showImageOnFail(R.drawable.default_poster)
		// .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
		// .build();
		//
		// options_Banner = new
		// DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_banner)
		// .showImageForEmptyUri(R.drawable.default_banner).showImageOnFail(R.drawable.default_banner)
		// .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
		// .build();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void reqXmlSucessed(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlSucessed(msg);

		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneTypeAPP:
			stopPbarU();
			// if(!needToFresh)
			// {
			// if (null != mHandlerPosterAutoScroll) {
			// mHandlerPosterAutoScroll.postDelayed(autoFling, 100);
			// }
			// return;
			// }

			// ImageLoader.getInstance().clearDiscCache();
			// ImageLoader.getInstance().clearMemoryCache();
			mShared.put("isSplashImage", "2");
			linear_failed.setVisibility(View.GONE);
			mPullRefreshGridView.setVisibility(View.VISIBLE);
			// if (carroselAdapter != null) {
			// carroselAdapter.setListBan(Commons.mAdBanners);
			// carroselAdapter.notifyDataSetChanged();
			// carroselAdapter = null;
			// }
			// if (null != mHandlerPosterAutoScroll
			// && !"".equals(mHandlerPosterAutoScroll)) {
			// mHandlerPosterAutoScroll.removeCallbacks(autoFling);
			// }
			// miRecommendListCount = Commons.mAdBanners.size();
			// creatPointGuide(miRecommendListCount);
			// setBannerAdapter(Commons.mAdBanners);
			// miBigPosterLastIndex = miRecommendListCount
			// % Integer.MAX_VALUE / 2;
			// mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			// isSelect = false;
			// if (refresh != null) {
			// refresh.onRefreshComplete();
			// }
			needToFresh = true;
			// getFirstLevelDataFromServer(false);
			QryToneTypeAPPResp resp = (QryToneTypeAPPResp) msg.obj;
			list = resp.getmToneInfos();
//			addFirstToAll();
			// mAppTones.clear();
			// mAppTones.addAll(list);
			mCommonAdapter = new BaseCommonAdapter();
			mGridView.setAdapter(mCommonAdapter);
			// new Thread(run).start();
			processData();
			// Looper.prepare();
			// showTextToast(getActivity(), "req");
			// Looper.loop();
			break;

		default:
			break;
		}

	}

	private void processData() {
		// 添加推荐Item
		ToneInfo info = new ToneInfo();
		String recommended_list = Utils.getResouceStr(getActivity(),
				R.string.recommended_list);
		info.setToneName(recommended_list.toString());

//		if (mAllRingTones.size() > 0) {
//			mAllRingTones.clear();
//		}
//		// mCategoryTonesMap.clear();
//		mAllRingTones.add(info);
//		addFirstToAll();
		// mCommonAdapter.notifyDataSetChanged();
	}

	
//	private void addFirstToAll() {
//		if (list == null || list.size() == 0) {
//			return;
//		}
//
//		for (int i = 0; i < list.size(); i++) {
//			ToneTypeAPPInfo tone = list.get(i);
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

	// private Runnable run = new Runnable() {
	// @Override
	// public void run() {
	// // setDataIntoDb();
	// }
	// };
	//
//	private void setDataIntoDb() {
//		if (list == null || list.size() == 0) {
//			return;
//		}
//		SQLiteDatabase db = mDbHelper.getWritableDatabase();
//		DBOperator.deleteRings(db);
//
//		List<ToneTypeAPPInfo> temp = new ArrayList<ToneTypeAPPInfo>();
//		temp.addAll(list);
//		for (ToneTypeAPPInfo info : temp) {
//			DBOperator.insertData(db, info);
//		}
//		db.close();
//	}

	@Override
	protected void reqError(Message msg) {
		super.reqError(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneTypeAPP:
//			getFirstLevelDataFromDB();
			mGridView.setAdapter(mCommonAdapter);
			linear_failed.setVisibility(View.VISIBLE);
			mPullRefreshGridView.setVisibility(View.GONE);
			stopPbarU();
			// if (null != mHandlerPosterAutoScroll) {
			// mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			// }
			// refresh.onRefreshComplete();
			processData();
			break;

		default:
			break;
		}
	}

	@Override
	protected void reqXmlFail(Message msg) {
		super.reqXmlFail(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneTypeAPP:
			refresh.onRefreshComplete();
			// if (null != mHandlerPosterAutoScroll) {
			// mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			// }
			QryToneTypeAPPResp resp = (QryToneTypeAPPResp) msg.obj;
			Utils.showTextToast(getActivity(),
					UtlisReturnCode.ReturnCode(resp.getResult(), mContext));
			stopPbarU();
			// getFirstLevelDataFromDB();
			processData();
			break;

		default:
			break;
		}
	}

	// public void setData(List<Tone> mAllRingTones,
	// HashMap<String, List<ToneTypeAPPInfo>> mCategoryTonesMap) {
	// this.mAllRingTones = mAllRingTones;
	// this.mCategoryTonesMap = mCategoryTonesMap;
	//
	// };
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
//		if (list != null && list.size() > 0) {
//			list.clear();
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
//			list.add(info);
//		}
//		cursor.close();
//		db.close();
//		return 0;
//	}

	private void getFirstLevelDataFromServer(boolean needShow) {
//		if (needShow) {
//			startPbarU();
//		}
		QryToneTypeAPPRequest tone = new QryToneTypeAPPRequest(p_h);
		tone.sendQryToneTypeAPPRequest(100, 0);
	}

	private void initView() {
		mPullRefreshGridView = (PullToRefreshGridView) v
				.findViewById(R.id.pull_refresh_list);
		linear_failed=(LinearLayout) v.findViewById(R.id.linear_failed);
		mGridView = mPullRefreshGridView.getRefreshableView();
		if(Utils.isNetworkAvailable(getActivity())){
			linear_failed.setVisibility(View.GONE);
			mPullRefreshGridView.setVisibility(View.VISIBLE);
		}else{
			linear_failed.setVisibility(View.VISIBLE);
			mPullRefreshGridView.setVisibility(View.GONE);
		}
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				// new GetDataTask().execute();
				// QryToneTypeAPPRequest tone = new QryToneTypeAPPRequest(p_h);
				// tone.sendQryToneTypeAPPRequest(100, 0);
//				mCommonAdapter.notifyDataSetChanged();
//				isFirstEnter=true;
//				if(mCommonAdapter==null){
				new RefreshTask().execute();  
			}
		});
		// mCommonAdapter=new BaseCommonAdapter();
		// mGridView.setAdapter(mCommonAdapter);
		// final ListView data = refresh.getRefreshableView();
		// data.setDividerHeight(30);
		// data.setVerticalScrollBarEnabled(false);
		// refresh.setOnRefreshListener(new OnRefreshListener() {
		//
		// @Override
		// public void onRefresh() {
		// needToFresh = true;
		// // if (null != mHandlerPosterAutoScroll) {
		// // mHandlerPosterAutoScroll.removeCallbacks(autoFling);
		// // }
		// getFirstLevelDataFromServer(false);
		// }
		// });
		//
		// // initTopGallery();
		//
		// // data.addHeaderView(head);
		// data.setAdapter(mCommonAdapter);
		// data.setSelector(android.R.color.transparent);
		// data.setBackgroundColor(getResources().getColor(android.R.color.transparent));

	}
	class RefreshTask extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			Commons.delSavepath();
			return null;
		}  
		  
	    @Override  
	    protected void onPostExecute(Boolean result) {  
			getFirstLevelDataFromServer(false);
		    mPullRefreshGridView.onRefreshComplete();
	    } 
	}
	class BaseCommonAdapter extends BaseAdapter implements OnScrollListener {
		public BaseCommonAdapter() {
			mGridView.setOnScrollListener(this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			String mImageUrl = list.get(arg0).getPicURL().toString();
			String title = list.get(arg0).getToneTypeLabel().toString();
			if (arg1 == null) {
				viewHolder = new ViewHolder();
				arg1 = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_list_layout, null);
				viewHolder.item1 = (RelativeLayout) arg1.findViewById(R.id.item1);
				viewHolder.data1 = (ImageView) arg1.findViewById(R.id.data1);
				viewHolder.data1_info = (TextView) arg1.findViewById(R.id.data1_text);
				arg1.setTag(viewHolder);
			} else {
				 viewHolder = (ViewHolder) arg1.getTag();
				 viewHolder.data1.setImageDrawable(getResources().getDrawable(
							R.drawable.default_poster));
				
			}
			viewHolder.data1.setTag(mImageUrl);
			viewHolder.data1_info.setText(title);
			final int pos = arg0;
			viewHolder.item1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg3) {
					// TODO Auto-generated method stub
					enterCategoryFragment(list.get(pos).getParentTypeID(),pos);
				}
			});
			String saveDir = Commons.getImageSavedpath()
					+ list.get(pos).getToneTypeID().toString();
			Bitmap bitmap = ImageDownLoader.getShareImageDownLoader()
					.showCacheBitmap(saveDir);
			if (bitmap != null) {
				viewHolder.data1.setImageBitmap(bitmap);
			} else {
				viewHolder.data1.setImageDrawable(getResources().getDrawable(
						R.drawable.default_poster));
			}
			return arg1;
		}
		  class ViewHolder
		    {
				TextView data1_info;
				ImageView data1;
				RelativeLayout item1;
		    }
		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			mFirstVisibleItem = arg1;
			mVisibleItemCount = arg2;
			// 因此在这里为首次进入程序开启下载任务。
			if (isFirstEnter && arg3 > 0) {
				showImage(mFirstVisibleItem, mVisibleItemCount);
				isFirstEnter = false;
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg1 == OnScrollListener.SCROLL_STATE_IDLE) {
				showImage(mFirstVisibleItem, mVisibleItemCount);
			} else {
				// cancelTask();
			}
		}

		private void showImage(int firstVisibleItem, int visibleItemCount) {
			Bitmap bitmap = null;
			for (int i = firstVisibleItem; i < firstVisibleItem+ visibleItemCount; i++) {
				// File cacheDir = new File(
				// StorageUtils.getCacheDirectory(getActivity()
				// .getApplicationContext()) + "/ListPicCahe",
				// list.get(i).getToneTypeID().toString());
				// String saveDir = cacheDir.getAbsolutePath().toString();
				String saveDir = Commons.getImageSavedpath()
						+ list.get(i).getToneTypeID().toString();
				String mImageUrl = list.get(i).getPicURL();
				final ImageView mImageView = (ImageView)mGridView.getChildAt(0)
						.findViewWithTag(mImageUrl);
				bitmap = ImageDownLoader.getShareImageDownLoader()
						.downloadImage(saveDir, mImageUrl,w/2,h/5,
								new onImageLoaderListener() {

									@Override
									public void onImageLoader(Bitmap arg0,
											String arg1) {
										// TODO Auto-generated method stub
										if (mImageView != null && arg0 != null) {
											mImageView.setImageBitmap(arg0);
										}
									}
								});
				if(bitmap==null){
					mCommonAdapter.notifyDataSetChanged();
				}
			}
			// public void cancelTask(){
			// ImageDownLoader.getShareImageDownLoader().clearCache();
			// }
		}

		// private class BaseCommonAdapter extends BaseAdapter {
		//
		// @Override
		// public int getCount() {
		// if (mAllRingTones == null) {
		// return 0;
		// }
		// int count = 0;
		// if (mAllRingTones.size() % 2 == 0) {
		// count = mAllRingTones.size() / 2;
		// } else {
		// count = mAllRingTones.size() / 2 + 1;
		// }
		// return count;
		// }
		//
		// @Override
		// public Object getItem(int position) {
		// return null;
		// }
		//
		// @Override
		// public long getItemId(int position) {
		// return 0;
		// }
		//
		// @Override
		// public View getView(int position, View convertView, ViewGroup parent)
		// {
		// final ViewHolder holder;
		// if (convertView == null) {
		// holder = new ViewHolder();
		// convertView =
		// getActivity().getLayoutInflater().inflate(R.layout.item_layout,
		// null);
		// holder.item1 = (RelativeLayout) convertView.findViewById(R.id.item1);
		// holder.item2 = (RelativeLayout) convertView.findViewById(R.id.item2);
		// holder.data1 = (ImageView) convertView.findViewById(R.id.data1);
		// holder.data2 = (ImageView) convertView.findViewById(R.id.data2);
		// holder.data1_click = (Button)
		// convertView.findViewById(R.id.data1_click);
		// holder.data2_click = (Button)
		// convertView.findViewById(R.id.data2_click);
		// holder.data1_info = (TextView)
		// convertView.findViewById(R.id.data1_text);
		// holder.data2_info = (TextView)
		// convertView.findViewById(R.id.data2_text);
		// convertView.setTag(holder);
		// } else {
		// holder = (ViewHolder) convertView.getTag();
		// // holder.data1.setBackgroundResource(R.drawable.default_poster);
		// // holder.data2.setBackgroundResource(R.drawable.default_poster);
		// }
		// final int pos = position;
		// final int temp = position * 2;
		// final int length = mAllRingTones.size();
		// // 处理当前的数据条数是整数的部分
		// if (temp + 1 <= length - 1) {
		// Tone toneFore = mAllRingTones.get(temp);
		// Tone toneLast = mAllRingTones.get(temp + 1);
		//
		// if (pos == 0) {
		// ToneInfo appTone = (ToneInfo) toneFore;
		// holder.data1_info.setText(appTone.getToneName());
		// holder.data1.setBackgroundResource(R.drawable.list);
		// setUrlToImage(holder.data1, "list.png", options_Small, true,
		// R.drawable.list);
		// } else if (pos != 0) {
		// System.out.println("--------------------------- pos  = " + pos);
		// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
		// holder.data1_info.setText(appTone.getToneTypeLabel());
		// holder.data1.setBackgroundResource(R.drawable.list);
		// setUrlToImage(holder.data1, appTone.getPicURL(), options_Small, true,
		// R.drawable.default_poster);
		// }
		//
		// if (toneLast instanceof ToneTypeAPPInfo) {
		// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneLast;
		// holder.data2_info.setText(appTone.getToneTypeLabel());
		// holder.data1.setBackgroundResource(R.drawable.list);
		// setUrlToImage(holder.data2, appTone.getPicURL(), options_Small, true,
		// R.drawable.default_poster);
		// }
		// holder.item2.setVisibility(View.VISIBLE);
		// } else {
		// Tone toneFore = mAllRingTones.get(length - 1);
		// if (toneFore instanceof ToneTypeAPPInfo) {
		// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
		// holder.data1_info.setText(appTone.getToneTypeLabel());
		// holder.data1.setBackgroundResource(R.drawable.list);
		// setUrlToImage(holder.data1, appTone.getPicURL(), options_Small, true,
		// R.drawable.default_poster);
		// } else if (toneFore instanceof ToneInfo) {
		// ToneInfo appTone = (ToneInfo) toneFore;
		// holder.data1_info.setText(appTone.getToneName());
		// holder.data1.setBackgroundResource(R.drawable.list);
		// setUrlToImage(holder.data1, "list.png", options_Small, true,
		// R.drawable.list);
		// }
		// holder.item2.setVisibility(View.INVISIBLE);
		// }
		//
		// holder.data2_click.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// if (Utils.isFastDoubleClick()) {
		// return;
		// }
		// if (temp + 1 <= length - 1) {
		// Tone toneFore = mAllRingTones.get(temp + 1);
		// if (toneFore instanceof ToneTypeAPPInfo) {
		// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
		// // Toast.makeText(getActivity(),
		// // "right " + appTone.getToneTypeLabel(), 1)
		// // .show();
		// enterCategoryFragment(appTone.getToneTypeID());
		// }
		// }
		// }
		// });
		//
		// holder.data1_click.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// if (Utils.isFastDoubleClick()) {
		// return;
		// }
		// Tone toneFore = mAllRingTones.get(pos * 2);
		// if (toneFore instanceof ToneTypeAPPInfo) {
		// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
		// // Toast.makeText(getActivity(),
		// // "left " + appTone.getToneTypeLabel(), 1).show();
		// enterCategoryFragment(appTone.getToneTypeID());
		// } else {
		// // ToneInfo appTone = (ToneInfo) toneFore;
		// // Toast.makeText(getActivity(),
		// // "left " + appTone.getToneName(), 1).show();
		// enterListFragment();
		// }
		// }
		// });
		//
		// holder.data1.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// if (Utils.isFastDoubleClick()) {
		// return;
		// }
		// Tone toneFore;
		// if(pos==0){
		// toneFore = mAllRingTones.get( 2);
		// }else{
		// toneFore = mAllRingTones.get(pos * 2);}
		// if (toneFore instanceof ToneTypeAPPInfo) {
		// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
		// // Toast.makeText(getActivity(),
		// // "left " + appTone.getToneTypeLabel(), 1).show();
		// enterCategoryFragment(appTone.getToneTypeID());
		// } else {
		// // Enter List
		// enterListFragment();
		// }
		// }
		// });
		//
		// holder.data2.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// if (Utils.isFastDoubleClick()) {
		// return;
		// }
		// if (temp + 1 <= length - 1) {
		// Tone toneFore = mAllRingTones.get(temp + 1);
		// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
		// // Toast.makeText(getActivity(),
		// // "right " + appTone.getToneTypeLabel(), 1)
		// // .show();
		// // 进入铃音列表页面
		// enterCategoryFragment(appTone.getToneTypeID());
		// }
		// }
		// });
		//
		// return convertView;
		// }

		// private void showImage(){
		//
		//
		// }
		// private void setUrlToImage(final ImageView holder, String url,
		// DisplayImageOptions options, boolean needAdd,
		// final int id) {
		// // if (needAdd) {
		// // url = Commons.IMAGEURL + "/" + url;
		// // }
		// ImageLoader.getInstance().displayImage(url, holder, options, new
		// ImageLoadingListener() {
		//
		// @Override
		// public void onLoadingStarted(String arg0, View arg1) {
		// holder.setImageBitmap(null);
		// holder.setBackgroundResource(id);
		// }
		//
		// @Override
		// public void onLoadingFailed(String arg0, View arg1, FailReason arg2)
		// {
		// holder.setImageBitmap(null);
		// holder.setBackgroundResource(id);
		// }
		//
		// @Override
		// public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		// if (arg2 == null) {
		// arg1.setBackgroundResource(id);
		// } else {
		// arg1.setBackgroundResource(0);
		// holder.setImageBitmap(arg2);
		// }
		// }
		//
		// @Override
		// public void onLoadingCancelled(String arg0, View arg1) {
		// }
		// });
		// }
		//
		// class ViewHolder {
		// ImageView data1;
		// ImageView data2;
		// Button data1_click;
		// Button data2_click;
		// TextView data1_info;
		// TextView data2_info;
		// RelativeLayout item1;
		// RelativeLayout item2;
		// }

		private void enterListFragment() {
			refresh.onRefreshComplete();
			needToFresh = false;
			// if (null != mHandlerPosterAutoScroll) {
			// mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			// }
			
			MainGroupActivity activity = (MainGroupActivity) getActivity();
			activity.skipToListFragment(true);
		}

		private void enterCategoryFragment(String key,int pos) {
			Log.i("enterCategoryFragmentList   ", list.toString());
			List<ToneTypeAPPInfo> lists = list;
			CategoryRingToneListFragment fragment = new CategoryRingToneListFragment();
			fragment.setToneList(lists);
			fragment.setSelectItem(pos);
			fragment.scroll(pos);
//			int i = 0;
			// for (; i < mAllRingTones.size(); i++) {
			// Tone info = mAllRingTones.get(i);
			// if (info instanceof ToneTypeAPPInfo) {
			// if (((ToneTypeAPPInfo) info).getToneTypeID() == key) {
			// fragment.setTitle(((ToneTypeAPPInfo) info).getToneTypeLabel());
			// break;
			// }
			// }
			// }
			// if (needToFresh) {
			// refresh.onRefreshComplete();
			// needToFresh = false;
			// }
			fragment.setId(key);
			// openFragment(fragment);
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.container_list, fragment,
							"CategoryRingToneListFragment").commit();
		}
	}
}
