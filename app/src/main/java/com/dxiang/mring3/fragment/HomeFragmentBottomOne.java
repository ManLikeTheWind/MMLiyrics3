package com.dxiang.mring3.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gem.imgcash.ImageDownLoader;
import com.gem.imgcash.ImageDownLoader.onImageLoaderListener;
import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.MainGroupActivity;
import com.dxiang.mring3.bean.ImageInfo;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.GetimageinforReq;
import com.dxiang.mring3.request.QryRankTone;
import com.dxiang.mring3.request.QryRecommendTone;
import com.dxiang.mring3.response.GetImageInfoRsp;
import com.dxiang.mring3.response.QryRankToneRsp;
import com.dxiang.mring3.response.QryRecommendedToneRsp;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.UtlisReturnCode;
import com.dxiang.mring3.views.refresh.PullToRefreshBase.OnRefreshListener;
import com.dxiang.mring3.views.refresh.PullToRefreshListView;

public class HomeFragmentBottomOne extends BaseFragment {
	int miRecommendListCount = 0;

	// GalleryEx pagerCarrosel;
	//
	// private boolean isSelect = false;
	//
	// private Handler mHandlerPosterAutoScroll = new Handler();
	//
	// private int miBigPosterLastIndex = 0;

	private BaseCommonAdapter mCommonAdapter;

//	private LinearLayout mLinLayoutBigPosterGuidePoint;

	private View rootView;
	private View Rank;

	private PullToRefreshListView refresh;

	private View header;

	// 如果跳转到别的页面不需要刷新
	private boolean needToFresh = true;

	// 保存所有的一级和二级铃音
//	private List<ToneTypeAPPInfo> mAppTones;

	// 用于展示所有的Tone 不管是推荐还是一级
//	private List<Tone> mAllRingTones = new ArrayList<Tone>();

//	private DisplayImageOptions options_Small;
//
//	private DisplayImageOptions options_Banner;

//	private DbHelper mDbHelper;

//	private HashMap<String, List<ToneTypeAPPInfo>> mCategoryTonesMap = new HashMap<String, List<ToneTypeAPPInfo>>();
	
	//用于排行榜四张图片排序
	private HashMap<String, String> imgUrlMap;
	
	private SharedPreferenceService mShared;
	private int showNum = 10;// 一次请求条数
	private int pageNum = 0, mSumCount = 0;// 初始化化页数,总条数
	private List<ToneInfo> mData;
    private TextView tv_Ranking;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_pull, null);
		// Rank = inflater.inflate(R.layout.topper_grally_head, null);
		initView();
		return rootView;

	}
	
	 @Override
	    public void onResume() {
	        super.onResume();
//	        getView().setFocusableInTouchMode(true);
//	        getView().requestFocus();
//	        getView().setOnKeyListener(new View.OnKeyListener() {
//	            @Override
//	            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//	                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//	                    //这里处理返回事件
//	                	Log.e("lc", "cao---------------111----");
//	                }
//	                return false;
//	            }
//	        });
	    }
	 
	private void initView() {
		refresh = (PullToRefreshListView) rootView
				.findViewById(R.id.pullto_refreash);
		final ListView data = refresh.getRefreshableView();
		data.setDividerHeight(30);
		data.setVerticalScrollBarEnabled(false);
		refresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				needToFresh = true;
				// if (null != mHandlerPosterAutoScroll) {
				// mHandlerPosterAutoScroll.removeCallbacks(autoFling);
				// }
				// getFirstLevelDataFromServer(false);
				new RefreshTask().execute();  
			}
		});
		// initTopGallery();
		// data.addHeaderView(Rank);
		data.setAdapter(mCommonAdapter);
		data.setSelector(android.R.color.transparent);
		data.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		// QryRecommendTone qryRecommended=new QryRecommendTone(p_h);
		// qryRecommended.qryRecommendedTone("1", showNum, pageNum);
		tv_Ranking=(TextView) rootView.findViewById(R.id.tooper__grally_tv);
		tv_Ranking.setText(getResources().getString(R.string.ranking_list));
		
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
	    	refresh.onRefreshComplete();
			GetimageinforReq req = new GetimageinforReq(p_h);
			req.sendGetimageinforReqRequest("3");
	    } 
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		mAppTones = new ArrayList<ToneTypeAPPInfo>();
		mShared = SharedPreferenceService.getInstance(getActivity());
		long time = mShared.getLong(Commons.TODAYTIME, 0l);
		boolean needFreash = Utils.isNeedToLoadDatas(time);
		mCommonAdapter = new BaseCommonAdapter();
//		setDataIntoDb();
		// if(needFreash)
		// {
		// mShared.putLong(Commons.TODAYTIME, System.currentTimeMillis());
		// getFirstLevelDataFromServer(true);
		// }
		// else
		// {
		// getFirstLevelDataFromDB();
		// processData();
		// }
		GetimageinforReq req = new GetimageinforReq(p_h);
		req.sendGetimageinforReqRequest("3");
		// QueryRecommendedRingRequest qp=new QueryRecommendedRingRequest(p_h);
		// qp.sendQueryRecommendedRingRequest("1", 100, 0);

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
	}

	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneTypeAPP:
			stopPbarU();
			// // if(!needToFresh)
			// // {
			// // if (null != mHandlerPosterAutoScroll) {
			// // mHandlerPosterAutoScroll.postDelayed(autoFling, 100);
			// // }
			// // return;
			// // }
//			ImageLoader.getInstance().clearDiscCache();
//			ImageLoader.getInstance().clearMemoryCache();
			// mShared.put("isSplashImage", "2");
			// // if (carroselAdapter != null) {
			// // carroselAdapter.setListBan(Commons.mAdBanners);
			// // carroselAdapter.notifyDataSetChanged();
			// // carroselAdapter = null;
			// // }
			// // if (null != mHandlerPosterAutoScroll
			// // && !"".equals(mHandlerPosterAutoScroll)) {
			// // mHandlerPosterAutoScroll.removeCallbacks(autoFling);
			// // }
			// miRecommendListCount = Commons.mAdBanners.size();
			// // creatPointGuide(miRecommendListCount);
			// // setBannerAdapter(Commons.mAdBanners);
			// // miBigPosterLastIndex = miRecommendListCount
			// // % Integer.MAX_VALUE / 2;
			// // mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			// // isSelect = false;
			// refresh.onRefreshComplete();
			// QryToneTypeAPPResp resp = (QryToneTypeAPPResp) msg.obj;
			// List<ToneTypeAPPInfo> list = resp.getmToneInfos();
			// mAppTones.clear();
			// mAppTones.addAll(list);
			// new Thread(run).start();
			// processData();
			break;

		case FusionCode.REQUEST_GetImageInfo:
			GetImageInfoRsp rsp = (GetImageInfoRsp) msg.obj;
			// Log.e("xxx",
			// "sssss:"+rsp.getResult()+"--"+rsp.getDescription()+"--"+rsp.getInfos().get(0).getImageURL()+"=="+rsp.getInfos().get(0).getNo()+"=="+rsp.getInfos().get(0).getLinkURL());
			List<ImageInfo> infos = rsp.getInfos();
			mCommonAdapter.setImageInfos(infos);
			mCommonAdapter.notifyDataSetChanged();
			break;
		case FusionCode.REQUEST_QRYRANKTONE_MONTH:
			QryRankToneRsp entryRankTone = (QryRankToneRsp) msg.obj;
			// pageNum++;//页数自增
			// mSumCount=mSumCount+entryRankTone.list.size();//总条数
			// mSumCount=entryRankTone.getAmount();//总条数
			mData = entryRankTone.list;// 从服务器获取的数据
			// mDataSun.addAll(mData);//把新数据增加到展示data中去
			// showData();
			EnterCategoryFragment(0);
//			getFirstLevelDataFromDB();
			break;
		case FusionCode.REQUEST_QRYRANKTONE_WEEK:
			QryRankToneRsp entryRankTone1 = (QryRankToneRsp) msg.obj;
			// pageNum++;//页数自增
			// mSumCount=mSumCount+entryRankTone.list.size();//总条数
			// mSumCount=entryRankTone1.getAmount();//总条数
			mData = entryRankTone1.list;// 从服务器获取的数据
			EnterCategoryFragment(1);
			// 把新数据增加到展示data中去
			break;
		case FusionCode.REQUEST_QRYRECOMMENDTONE:
			QryRecommendedToneRsp entry = (QryRecommendedToneRsp) msg.obj;
			// pageNum++;//页数自增
			// mSumCount=mSumCount+entry.list.size();//总条数
			// mSumCount=entry.getAmount();//总条数
			// mData=entry.list;//从服务器获取的数据
			// mDataSun.addAll(mData);//把新数据增加到展示data中去
			mData = entry.list;// 从服务器获取的数据
			EnterCategoryFragment(2);
			// mAllRingTones.add((Tone) mData);//把新数据增加到展示data中去

			// showData();
			break;
		case FusionCode.REQUEST_QRYRANKTONE_TOTAL:
			QryRankToneRsp entryRankTone2 = (QryRankToneRsp) msg.obj;
			// pageNum++;//页数自增
			// mSumCount=mSumCount+entryRankTone.list.size();//总条数
			// mSumCount=entryRankTone2.getAmount();//总条数
			mData = entryRankTone2.list;// 从服务器获取的数据
			EnterCategoryFragment(3);
			// mDataSun.addAll(mData);//把新数据增加到展示data中去
			break;
		case FusionCode.REQUEST_NEWRING:
			QryRankToneRsp entryRankTone3 = (QryRankToneRsp) msg.obj;
			mData = entryRankTone3.list;
			EnterCategoryFragment(4);
			break;
		}

	}

	public void EnterCategoryFragment(int type) {
		// List<ToneTypeAPPInfo> lists = mCategoryTonesMap.get(key);
		CategoryRingToneHomeListFragement fragment = new CategoryRingToneHomeListFragement();
		fragment.setToneList(mData);
		fragment.settype(type);
		int i = 0;
		// for (; i < mAllRingTones.size(); i++) {
		// Tone info = mAllRingTones.get(i);
		// if (info instanceof ToneTypeAPPInfo) {
		// if (((ToneTypeAPPInfo) info).getToneTypeID() == key) {
		// fragment.setTitle(((ToneTypeAPPInfo) info).getToneTypeLabel());
		// break;
		// }
		// }
		// }
		if (needToFresh) {
			refresh.onRefreshComplete();
			needToFresh = false;
		}
		// fragment.setId(key);
		// openFragment(fragment);
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.container_list, fragment,
						"CategoryRingToneHomeListFragement").commit();
	}

	@Override
	protected void reqError(Message msg) {
		super.reqError(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QRYRECOMMENDTONE:
			QryRecommendedToneRsp entry = (QryRecommendedToneRsp) msg.obj;
			Utils.showTextToast(getActivity(),
					UtlisReturnCode.ReturnCode(entry.getResult(), mContext));
			break;
		case FusionCode.REQUEST_QRYRANKTONE_WEEK:
			QryRankToneRsp entryRankTone = (QryRankToneRsp) msg.obj;
			Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(
					entryRankTone.getResult(), mContext));
			break;
		case FusionCode.REQUEST_QRYRANKTONE_MONTH:
			QryRankToneRsp entryRankTone2 = (QryRankToneRsp) msg.obj;
			Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(
					entryRankTone2.getResult(), mContext));
			break;
		case FusionCode.REQUEST_QRYRANKTONE_TOTAL:
			QryRankToneRsp entryRankTone3 = (QryRankToneRsp) msg.obj;
			Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(
					entryRankTone3.getResult(), mContext));
			break;
		case FusionCode.REQUEST_NEWRING:
			QryRankToneRsp entryRankTone4 = (QryRankToneRsp) msg.obj;
			Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(
					entryRankTone4.getResult(), mContext));
			break;
		case FusionCode.REQUEST_GetImageInfo:
			List<ImageInfo> infos = new ArrayList<ImageInfo>();
			ImageInfo imageInfo = new ImageInfo();
			for (int i = 0; i < 3; i++) {
				imageInfo.setImageURL(Commons.SOAP_URL.trim() + "/OtherMan");
				infos.add(imageInfo);
				// infos
			}
			mCommonAdapter.setImageInfos(infos);
			mCommonAdapter.notifyDataSetChanged();
			// Log.e("xxx",
			// "sssss:"+rsp.getResult()+"--"+rsp.getDescription()+"--"+rsp.getInfos().get(0).getImageURL()+"=="+rsp.getInfos().get(0).getNo()+"=="+rsp.getInfos().get(0).getLinkURL());
			// List<ImageInfo> infos = rsp.getInfos();
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
//			QryToneTypeAPPResp resp = (QryToneTypeAPPResp) msg.obj;
//			Utils.showTextToast(getActivity(),
//					UtlisReturnCode.ReturnCode(resp.getResult(), mContext));
			stopPbarU();
//			getFirstLevelDataFromDB();
			// processData();
			break;
			
		case FusionCode.REQUEST_QRYRANKTONE_MONTH:
			QryRankToneRsp entryRankTone = (QryRankToneRsp) msg.obj;		
			mData = entryRankTone.list;// 从服务器获取的数据			
			EnterCategoryFragment(0);
			break;
		case FusionCode.REQUEST_QRYRANKTONE_WEEK:
			QryRankToneRsp entryRankTone1 = (QryRankToneRsp) msg.obj;		
			mData = entryRankTone1.list;// 从服务器获取的数据
			EnterCategoryFragment(1);	
			break;
		case FusionCode.REQUEST_NEWRING:
			QryRankToneRsp entryRankTone3 = (QryRankToneRsp) msg.obj;
			mData = entryRankTone3.list;
			EnterCategoryFragment(4);
			break;
		case FusionCode.REQUEST_QRYRECOMMENDTONE:
			QryRecommendedToneRsp entry = (QryRecommendedToneRsp) msg.obj;
		
			mData = entry.list;
			EnterCategoryFragment(2);			
			break;
		}
	}

	// private void getFirstLevelDataFromServer(boolean needShow) {
	// if (needShow) {
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

	private void setDataIntoDb() {
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

	}

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

	// private void addFirstToAll() {
	// if (mAppTones == null || mAppTones.size() == 0) {
	// return;
	// }
	//
	// for (int i = 0; i < mAppTones.size(); i++) {
	// ToneTypeAPPInfo tone = mAppTones.get(i);
	// String key = tone.getParentTypeID();
	// String id = tone.getToneTypeID();
	// // 判断parenttypeid 是 0 就是一级还需要过滤掉ToneTypeID =
	// // 501,502,503,504对应的榜单数据，不是
	// if ("0".equals(key)) {
	// if (Utils.CheckTextNull(id)) {
	// if ("501".equals(id) || "502".equals(id) || "503".equals(id) ||
	// "504".equals(id)) {
	// continue;
	// } else {
	// mAllRingTones.add(tone);
	// }
	// }
	// } else {
	// if (mCategoryTonesMap.containsKey(key)) {
	// List<ToneTypeAPPInfo> temp = mCategoryTonesMap.get(key);
	// temp.add(tone);
	// } else {
	// List<ToneTypeAPPInfo> temp = new ArrayList<ToneTypeAPPInfo>();
	// temp.add(tone);
	// mCategoryTonesMap.put(key, temp);
	// }
	// }
	// }
	// }

	// 体哪家
	// private void processData() {
	// // 添加推荐Item
	// ToneInfo info = new ToneInfo();
	// String recommended_list = Utils.getResouceStr(getActivity(),
	// R.string.recommended_list);
	// info.setToneName(recommended_list.toString());
	//
	// if (mAllRingTones.size() > 0) {
	// mAllRingTones.clear();
	// }
	// // mCategoryTonesMap.clear();
	// mAllRingTones.add(info);
	// // addFirstToAll();
	// mCommonAdapter.notifyDataSetChanged();
	// }

	private class BaseCommonAdapter extends BaseAdapter {

		private List<ImageInfo> Infos;

		public void setImageInfos(List<ImageInfo> infos) {
			this.Infos = infos;
		}

		@Override
		public int getCount() {
			if (Infos == null) {
				return 0;
			}

			return 2;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.item_layout, null);
				holder.item1 = (RelativeLayout) convertView
						.findViewById(R.id.item1);
				holder.item2 = (RelativeLayout) convertView
						.findViewById(R.id.item2);
				holder.data1 = (ImageView) convertView.findViewById(R.id.data1);
				holder.data2 = (ImageView) convertView.findViewById(R.id.data2);
				holder.data1_click = (Button) convertView
						.findViewById(R.id.data1_click);
				holder.data2_click = (Button) convertView
						.findViewById(R.id.data2_click);
				holder.data1_info = (TextView) convertView
						.findViewById(R.id.data1_text);//两个标题1
				holder.data2_info = (TextView) convertView
						.findViewById(R.id.data2_text);//两个标题2
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
				// holder.data1.setBackgroundResource(R.drawable.default_poster);
				// holder.data2.setBackgroundResource(R.drawable.default_poster);
			}
			final int pos = position;
			final int temp = position * 2;
			// final int length = mAllRingTones.size();
			int length = Infos.size();
			// 处理当前的数据个数  是整数的部分:position:0-1;temp:0-3
			
			if (temp + 1 <= length - 1) {
				
				//获得全部图片地址，保存到map中，无须不能重复
				imgUrlMap=new HashMap<String, String>();
					for (int i = 0; i <Infos.size(); i++) {
						Log.e("==="+i, i+"*****");
						String url1=Infos.get(i).getImageURL();//0 2
						String key1=getPicName(url1);//13
						imgUrlMap.put(key1,url1);
					}
					
				if (position == 0) {//0 1
					ImageInfo info = Infos.get(temp);
					
					String url=info.getImageURL();
						holder.data1_info.setText(getResources().getString(R.string.top_of_the_moment));
						setUrlToImage(holder.data1, imgUrlMap.get("1.jpg"),
								 true, R.drawable.default_poster);
						Log.e("打印图片地址info 1", info.getImageURL());
						holder.data1.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (Utils.isFastDoubleClick()) {
									return;
								}
								
								QryRankTone qryRank = new QryRankTone(p_h);
								qryRank.qryRankTone("97", showNum, pageNum, 3);
								
								
								// resetParms();

								// Tone toneFores = mAllRingTones.get(pos);
								// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo)
								// toneFores;
								// Toast.makeText(getActivity(),
								// "right " + appTone.getToneTypeLabel(), 1)
								// .show();
								// 进入铃音列表页面
							}
						});
					

					ImageInfo info2 = Infos.get(temp+1);
					
						holder.data2_info.setText(getResources().getString(R.string.recommendeds));
						setUrlToImage(holder.data2, imgUrlMap.get("2.jpg"),
								 true, R.drawable.default_poster);
						Log.e("打印图片地址info 2", info.getImageURL());
						holder.data2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (Utils.isFastDoubleClick()) {
									return;
								}
								QryRecommendTone qryRecommended = new QryRecommendTone(
										p_h);
								qryRecommended.qryRecommendedTone("3", showNum,
										pageNum);
							
								// ImageInfo toneFore = Infos.get(temp + 1);
								// enterCategoryFragment(toneFore.getNo());

							}
						});
					
				} else if (position == 1) {
					ImageInfo info = Infos.get(temp);

						holder.data1_info.setText(getResources().getString(R.string.top_of_the_week));
						setUrlToImage(holder.data1, imgUrlMap.get("3.jpg"),
								 true, R.drawable.default_poster);
						Log.e("打印图片地址info 3 ", info.getImageURL());
						holder.data1.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (Utils.isFastDoubleClick()) {
								return;
								}
								QryRankTone qryRank = new QryRankTone(p_h);
								qryRank.qryRankTone("2", showNum, pageNum, 1);
								// ImageInfo toneFore = Infos.get(temp + 1);
								// enterCategoryFragment(toneFore.getNo());
								
							}
						});
					
					ImageInfo info2 = Infos.get(temp+1);
					
						holder.data2_info.setText(getResources().getString(R.string.the_new));
						setUrlToImage(holder.data2, imgUrlMap.get("4.jpg"),
								 true, R.drawable.default_poster);
						holder.data2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (Utils.isFastDoubleClick()) {
									return;
								}
								QryRankTone qryRank = new QryRankTone(p_h);
								qryRank.qryRankTone("5", showNum, pageNum, 5);
							
								// ImageInfo toneFore = Infos.get(temp + 1);
								// enterCategoryFragment(toneFore.getNo());
							}
						});	
				}
				holder.item2.setVisibility(View.VISIBLE);
			} else {
				// Tone toneFore = mAllRingTones.get(length - 1);
				// if (toneFore instanceof ToneTypeAPPInfo) {
				// ToneTypeAPPInfo appTone = (ToneTypeAPPInfo) toneFore;
				// holder.data1_info.setText(appTone.getToneTypeLabel());
				// setUrlToImage(holder.data1, appTone.getPicURL(),
				// options_Small, true, R.drawable.default_poster);
				// }
				// else if(toneFore instanceof ToneInfo)
				// {
				// ToneInfo appTone = (ToneInfo) toneFore;
				// holder.data1_info.setText(appTone.getToneName());
				// holder.data1.setBackgroundResource(R.drawable.list);
				// setUrlToImage(holder.data1, "list.png",
				// options_Small, true, R.drawable.list);
				// }
				// holder.item2.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
		public String getPicName(String url){
			String[] ids = url.split("/", -1);
			String te = ids[ids.length - 1];
			return te;
		}

		// 初始化上拉加载的参数
		private void resetParms() {
			pageNum = 0;
			mSumCount = 0;
			// mPullToRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);//初始化上拉控件模式
			if (mData.size() > 0) {
				mData.clear();
			}

			// if(mDataSun.size()>0)
			// {
			// mDataSun.clear();
			// }
		}

		class ViewHolder {
			ImageView data1;
			ImageView data2;
			TextView data1_info;
			TextView data2_info;
			Button data1_click;
			Button data2_click;
			RelativeLayout item1;
			RelativeLayout item2;
		}

		private void enterListFragment() {
			// refresh.onRefreshComplete();
			needToFresh = false;
			// if (null != mHandlerPosterAutoScroll) {
			// mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			// }
			MainGroupActivity activity = (MainGroupActivity) getActivity();
			activity.skipToListFragment(true);
		}

	}

	private void setUrlToImage(final ImageView holder, String url,boolean needAdd, final int id) {
			 
		// if (needAdd) {
		// url = Commons.IMAGEURL + "/" + url;
		// }
		if (url==null) {
			return;
		}
		  String saveDir = Commons.getImageSavedpath()+url.replaceAll("[^\\w]", "");
		Bitmap bm=ImageDownLoader.getShareImageDownLoader().showCacheBitmap((saveDir));
		if(bm!=null){
			holder.setImageBitmap(bm);
		}else{
			holder.setBackgroundResource(id);
			ImageDownLoader.getShareImageDownLoader().downloadImage(saveDir, url,w/2,h/4, new onImageLoaderListener() {
				
				@Override
				public void onImageLoader(Bitmap arg0, String arg1) {
					// TODO Auto-generated method stub
					holder.setImageBitmap(arg0);
				}
			});
		}
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
	}
}
