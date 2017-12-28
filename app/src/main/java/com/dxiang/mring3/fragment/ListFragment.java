package com.dxiang.mring3.fragment;//package com.dxiang.mring3.fragment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.Context;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.dxiang.mring3.R;
//import com.dxiang.mring3.activity.DetailsRingtoneActivity;
//import com.dxiang.mring3.activity.MainGroupActivity;
//import com.dxiang.mring3.bean.ToneInfo;
//import com.dxiang.mring3.request.QryRankTone;
//import com.dxiang.mring3.request.QryRecommendTone;
//import com.dxiang.mring3.response.GetToneListenAddrRsp;
//import com.dxiang.mring3.response.QryRankToneRsp;
//import com.dxiang.mring3.response.QryRecommendedToneRsp;
//import com.dxiang.mring3.utils.FusionCode;
//import com.dxiang.mring3.utils.LocalMediaPlayer;
//import com.dxiang.mring3.utils.UtlisReturnCode;
//import com.dxiang.mring3.utils.LocalMediaPlayer.Complete;
//import com.dxiang.mring3.utils.LocalMediaPlayer.ErrorListener;
//import com.dxiang.mring3.utils.LocalMediaPlayer.onException;
//import com.dxiang.mring3.utils.LocalMediaPlayer.onPrepared;
//import com.dxiang.mring3.utils.Utils;
//import com.dxiang.mring3.view.PullToRefreshBase;
//import com.dxiang.mring3.view.PullToRefreshBase.Mode;
//import com.dxiang.mring3.view.PullToRefreshBase.OnLastItemVisibleListener;
//import com.dxiang.mring3.view.PullToRefreshBase.OnRefreshListener2;
//import com.dxiang.mring3.view.PullToRefreshListView;
///**
// * 
// * 铃音榜单界面
// * @author Administrator
// *
// */
//public class ListFragment extends BaseFragment
//{
//	private View rootView;
//	//返回键
//	private ImageView mIvBack;
//	//标题
//	private TextView mTitle;
//	//列表项
//	private TextView tv_recommend,tv_week,tv_month,tv_total;
//	//listview
//	private PullToRefreshListView mPullToRefreshListView;
//	private ListView listview;
//	//数据子集
//	private List<ToneInfo> mData,mDataSun;
//	private int pageNum = 0, mSumCount=0;//初始化化页数,总条数
//	private int showNum=20;//一次请求条数
//	private boolean isSendRequset=false;//是否已经上拉标志位
//	private ListViewAdapter adapter;
//	private Context mContext;
//	// 铃音列表播放器
//	private LocalMediaPlayer mediaPlayer;// 播放器
//	private boolean isPlayer = false;// 判断是否正在播放 
//
//	private LinearLayout nowifi,data;
//
//	
//	private boolean needBack = false;
//	private String recommend_week_month_total="";//判断推荐，周，月，全榜铃音
//	private String week_month_total="";//判断周，月，全榜铃音
//	
//	/**
//     * 复用返回键处理
//     */
//	public void setNeedBack(boolean back)
//	{
//		needBack = back;
//	}
//	
//	public boolean isNeedBack()
//	{
//	    return needBack;	
//	}
//	
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) 
//	{
//		super.onCreate(savedInstanceState);
//		
//	}
//	
//	
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//    		Bundle savedInstanceState) 
//    {
//    	rootView=inflater.inflate(R.layout.list_fragment_layout, null);
//    	initData();
//    	initView();
//    	setClickListener();
//    	
//    	changeTextViewBackground(1);
//    	return rootView;
//    }
//    
//    @Override
//    public void onResume() 
//    {
//    	super.onResume();
//    	initMediaPlayer();
//    }
//    
//    @Override
//    public void onStop() 
//    {
//    	super.onStop();
//    	if(mediaPlayer == null)
//    	{
//    		return;
//    	}
//    	if(mediaPlayer.isPlaying() || mediaPlayer.getState() == LocalMediaPlayer.PAUSE)
//    	{
//    		pause();
//    	}
//    	else
//    	{
//    		stopMusic();
//    	}
//    }
//    
//    @Override
//    public void onPause() 
//    {
//    	super.onPause();
//    	if(mediaPlayer == null)
//    	{
//    		return;
//    	}
//    	if(mediaPlayer.isPlaying() || mediaPlayer.getState() == LocalMediaPlayer.PAUSE)
//    	{
//    		pause();
//    	}
//    	else
//    	{
//    		stopMusic();
//    	}
//    }
//    
//    /**
//     * 试听暂停方法
//     */
//    private void pause()
//    {
//    	if(mediaPlayer.getState() != LocalMediaPlayer.PAUSE)
//    	{
//    		adapter.old = adapter.pos; 
//    		adapter.pos = -1;
//    		adapter.notifyDataSetChanged();
//    		mediaPlayer.pause();
//    		isPlayer = false;
//    	}
//    }
//    
//    @Override
//    public void onDestroy() 
//    {
//    	super.onDestroy();
//    
//        if(mediaPlayer != null)
//        {
//        	mediaPlayer.uninsMedia();
//        	mediaPlayer = null;
//        }
//    }
//    
//    /**
//     * 返回成功处理
//     */
//    @Override
//    protected void reqXmlSucessed(Message msg) 
//    {
//    	super.reqXmlSucessed(msg);
//    	switch (msg.arg1) 
//    	{
//		case FusionCode.REQUEST_QRYRECOMMENDTONE:
//			if("1".equals(getSelectedList()))
//			{
//			QryRecommendedToneRsp entry=(QryRecommendedToneRsp) msg.obj;
//			pageNum++;//页数自增
//			//mSumCount=mSumCount+entry.list.size();//总条数
//			mSumCount=entry.getAmount();//总条数
//			mData=entry.list;//从服务器获取的数据
//			mDataSun.addAll(mData);//把新数据增加到展示data中去
//			showData();
//			//presentData();     
//			}
//			isSendRequset=false;
//			stopPbarU();
//			break;
//		case FusionCode.REQUEST_QRYTONEBYID:
//			break;
//		case FusionCode.REQUEST_QRYRANKTONE_WEEK:
//			if(week_month_total.equals(getSelectedList()))
//			{
//			QryRankToneRsp entryRankTone=(QryRankToneRsp) msg.obj;
//			pageNum++;//页数自增
//			//mSumCount=mSumCount+entryRankTone.list.size();//总条数
//			mSumCount=entryRankTone.getAmount();//总条数
//			mData=entryRankTone.list;//从服务器获取的数据
//			mDataSun.addAll(mData);//把新数据增加到展示data中去
//			showData();
//			//resetParms();
//			//mData=entryRankTone.list;
//			
//			//adapter.pos=-2;
//			//presentData();
//			}
//			isSendRequset=false;
//			stopPbarU();
//			
//			break;
//		case FusionCode.REQUEST_QRYRANKTONE_MONTH:
//			if(week_month_total.equals(getSelectedList()))
//			{
//			QryRankToneRsp entryRankTone=(QryRankToneRsp) msg.obj;
//			pageNum++;//页数自增
//			//mSumCount=mSumCount+entryRankTone.list.size();//总条数
//			mSumCount=entryRankTone.getAmount();//总条数
//			mData=entryRankTone.list;//从服务器获取的数据
//			mDataSun.addAll(mData);//把新数据增加到展示data中去
//			showData();
//			//resetParms();
//			//mData=entryRankTone.list;
//			
//			//adapter.pos=-2;
//			//presentData();
//			}
//			isSendRequset=false;
//			stopPbarU();
//			
//			break;
//		case FusionCode.REQUEST_QRYRANKTONE_TOTAL:
//			if(week_month_total.equals(getSelectedList()))
//			{
//			QryRankToneRsp entryRankTone=(QryRankToneRsp) msg.obj;
//			pageNum++;//页数自增
//			//mSumCount=mSumCount+entryRankTone.list.size();//总条数
//			mSumCount=entryRankTone.getAmount();//总条数
//			mData=entryRankTone.list;//从服务器获取的数据
//			mDataSun.addAll(mData);//把新数据增加到展示data中去
//			showData();
//			//resetParms();
//			//mData=entryRankTone.list;
//			
//			//adapter.pos=-2;
//			//presentData();
//			}
//			isSendRequset=false;
//			stopPbarU();
//			
//			break;
//		case FusionCode.REQUEST_GETTONELISTENADDREVT:
//			GetToneListenAddrRsp rsp=(GetToneListenAddrRsp) msg.obj;
//			//String url = "http://221.226.179.185/music_mmh/drive.wav";
//			String url=rsp.getToneAddr();
//			if(!Utils.CheckTextNull(url))
//			{
//				Utils.showTextToast(getActivity(), R.string.empty_play_url);
//				return;
//			}
//			mediaPlayer.setDataSource(url);
//            mediaPlayer.prepare();
//			break;
//
//		default:
//			break;
//		}
//    }
//    
//    
//    /**
//     * 返回结果错误码处理
//     */
//    @Override
//    protected void reqXmlFail(Message msg) 
//    {
//    	super.reqXmlFail(msg);
//    	switch (msg.arg1) 
//    	{
//		case FusionCode.REQUEST_QRYRECOMMENDTONE:
//			QryRecommendedToneRsp entry=(QryRecommendedToneRsp) msg.obj;
//			stopPbarU();
//			isSendRequset=false;
//			Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(entry.getResult(), mContext));
//			break;
//		case FusionCode.REQUEST_QRYRANKTONE_WEEK:
//			QryRankToneRsp entryRankTone=(QryRankToneRsp) msg.obj;
//			stopPbarU();
//			isSendRequset=false;
//			Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(entryRankTone.getResult(), mContext));
//			break;
//		case FusionCode.REQUEST_QRYRANKTONE_MONTH:
//			QryRankToneRsp entryRankTone2=(QryRankToneRsp) msg.obj;
//			stopPbarU();
//			isSendRequset=false;
//			Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(entryRankTone2.getResult(), mContext));
//			break;
//		case FusionCode.REQUEST_QRYRANKTONE_TOTAL:
//			QryRankToneRsp entryRankTone3=(QryRankToneRsp) msg.obj;
//			stopPbarU();
//			isSendRequset=false;
//			Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(entryRankTone3.getResult(), mContext));
//			break;
//       case FusionCode.REQUEST_GETTONELISTENADDREVT:
//    	   GetToneListenAddrRsp rsp=(GetToneListenAddrRsp) msg.obj;
//    	    stopMusic();
//			adapter.pos = -1;
//			adapter.notifyDataSetChanged();
//			isPlayer = false;
//       	   Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(rsp.getResult(), mContext));
//			break;
//		default:
//			break;
//		}
//    }
//    
//    /**
//     * 返回异常处理
//     */
//    @Override
//    protected void reqError(Message msg) 
//    {
//    	super.reqError(msg);
//    	stopPbarU();
//    	switch (msg.arg1) 
//    	{
//		case FusionCode.REQUEST_QRYRECOMMENDTONE:
//			isSendRequset=false;
//			break;
//		case FusionCode.REQUEST_QRYRANKTONE_WEEK:
//			isSendRequset=false;
//			break;
//		case FusionCode.REQUEST_QRYRANKTONE_MONTH:
//			isSendRequset=false;
//			break;
//		case FusionCode.REQUEST_QRYRANKTONE_TOTAL:
//			isSendRequset=false;
//			break;
//        case FusionCode.REQUEST_GETTONELISTENADDREVT:
//        	GetToneListenAddrRsp rsp=(GetToneListenAddrRsp) msg.obj;
//        	stopMusic();
//			adapter.pos = -1;
//			adapter.notifyDataSetChanged();
//			isPlayer = false;
//        	Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(rsp.getResult(), mContext));
//			break;
//		default:
//			break;
//		}
//    }
//    
//    //**************************************自定义方法************************************************
//    /**
//     * 初始化数据
//     */
//    private void initData()
//    {
//    	mContext=getActivity();
//    	mData=new ArrayList<ToneInfo>();
//    	mDataSun=new ArrayList<ToneInfo>();
//    	adapter=new ListViewAdapter(mContext);
//    	
////    	animation = new RotateAnimation(0, +360, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
////		animation.setDuration(800);// 动画开始到结束的执行时间(1000 = 1 秒)
////		animation.setRepeatCount(-1);// 动画重复次数(-1 表示一直重复)
////		animation.setRepeatMode(Animation.INFINITE);
////		animation.setInterpolator(new LinearInterpolator());//匀速动画
//		//initMediaPlayer();
//		//默认请求推荐铃音
//		resetParms();//初始化参数
//		resetListView();//清空listview 
//		startPbarU();
//		initDefaultRing();
//		
//    }
//    
//    //初始化上拉加载的参数
//    private void resetParms()
//    {
//    	pageNum=0;
//    	mSumCount=0;
//    	//mPullToRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);//初始化上拉控件模式
//    	if(mData.size()>0)
//    	{
//    		mData.clear();
//    	}
//    	
//    	if(mDataSun.size()>0)
//    	{
//    		mDataSun.clear();
//    	}
//    }
//    //分页展示数据
//    private void showData()
//    {
//    	if(mDataSun.size()>0)
//    	{
//    		if(isUpScroll())
//    		{
//    			mPullToRefreshListView.setMode(Mode.PULL_FROM_END);
//    		}
//    		else
//    		{
//    			mPullToRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);
//    		}
//    		
//    		adapter.notifyDataSetChanged();
//    	}
//    }
//    
//    //是否可以上拉
//    private boolean isUpScroll()
//    {
//    	int all = pageNum * showNum;//当前应有数据条数
//    	
// 		if (all > mSumCount) 
// 		{
// 			return false;
// 		}
// 		return true;
//    	
//    }
//    
//    
//    //显示铃音列表
//    private void presentData()
//    {
//    	
//    	if (mData.size()>0) 
//    	{
//			mSumCount = mData.size();
//			pageNum = 1;
//    		
//			if (mDataSun.size() > 0) 
//			{
//				mDataSun.clear();
//			}
//
//			if (mSumCount <= 10) 
//			{
//				mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
//				mDataSun.addAll(mData);
//			} 
//			else
//			{
//				mPullToRefreshListView.setMode(Mode.BOTH);
//				mDataSun.addAll(mData.subList(0, 10));
//			}
//			adapter.notifyDataSetChanged();
//		}
//    	
//    }
//    
//    //初始化默认推荐铃音
//    private void initDefaultRing()
//    {
//    	
//    	QryRecommendTone qryRecommended=new QryRecommendTone(p_h);
//		qryRecommended.qryRecommendedTone("201", showNum, pageNum);
//    }
//    
//    /**
//	  * 97: Total Rank List
//        2 :Weekly Rank List
//        1 :Monthly Rank List
//        */
//    
//    //初始化周铃音
//    private void initWeekRing()
//    {
//    	QryRankTone qryRank=new QryRankTone(p_h);
//    	qryRank.qryRankTone("2", showNum, pageNum,1);
//    	
//    }
//    
//    //初始化月铃音
//    private void initMonthRing()
//    {
//    	QryRankTone qryRank=new QryRankTone(p_h);
//    	qryRank.qryRankTone("1", showNum, pageNum,2);
//    }
//    
//    //初始化全部铃音
//    private void initTotalRing()
//    {
//    	QryRankTone qryRank=new QryRankTone(p_h);
//    	qryRank.qryRankTone("97", showNum, pageNum,3);
//    }
//    
//    
//    /**
//     * 初始化播放器
//     */
//    private void initMediaPlayer() 
//    {
//		mediaPlayer = LocalMediaPlayer.getInstance();
//		mediaPlayer.setCallback(onComplete);
//		mediaPlayer.setOnPrepared(onPrepred);
//		mediaPlayer.setErrorListener(errorListener);
//		mediaPlayer.setException(exception);
//	}
//    
//    
//    /**
//     * 初始化控件view
//     */
//    private void initView()
//    {
//    	mIvBack=(ImageView) rootView.findViewById(R.id.title_iv);
//    	if(needBack)
//    	{
//    		mIvBack.setVisibility(View.VISIBLE);
//    	}
//    	else
//    	{
//    		mIvBack.setVisibility(View.GONE);
//    	}
//    	mTitle=(TextView) rootView.findViewById(R.id.title_tv);
//    	mTitle.setText(Utils.getResouceStr(mContext, R.string.list));
//    	tv_recommend=(TextView) rootView.findViewById(R.id.textview_recommended);
//    	tv_month=(TextView) rootView.findViewById(R.id.textview_month);
//    	tv_total=(TextView) rootView.findViewById(R.id.textview_total);
//    	tv_week=(TextView) rootView.findViewById(R.id.textview_week);
//    	
//    	mPullToRefreshListView=(PullToRefreshListView) rootView.findViewById(R.id.list_pull_refresh_view);
//    	mPullToRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);//初始化上拉控件模式
//    	listview=mPullToRefreshListView.getRefreshableView();
//    	nowifi=(LinearLayout) rootView.findViewById(R.id.list_nowifi);
//    	data=(LinearLayout) rootView.findViewById(R.id.list_data);
//    	if(Utils.isNetworkAvailable(getActivity()))
//    	{
//    		nowifi.setVisibility(View.GONE);
//    		data.setVisibility(View.VISIBLE);
//    	}
//    	else
//    	{
//    		nowifi.setVisibility(View.VISIBLE);
//    		data.setVisibility(View.GONE);
//    	}
//    }
//    
//    /**
//     * 初始化监听器
//     */
//    private void setClickListener()
//    {
//    	Click click=new Click();
//    	tv_recommend.setOnClickListener(click);
//    	tv_month.setOnClickListener(click);
//    	tv_total.setOnClickListener(click);
//    	tv_week.setOnClickListener(click);
//    	mPullToRefreshListView.setOnLastItemVisibleListener(onLastItemListener);
//    	mPullToRefreshListView.setOnRefreshListener(listener);
//    	listview.setOnItemClickListener(mOnItemClickListener);
//    	mIvBack.setOnClickListener(click);
//    	listview.setAdapter(adapter);
//    	
//    }
//    
//    /**
//     * 播放完成
//     */
//    Complete onComplete = new Complete()
//    {
//
//		@Override
//		public void onComplete() 
//		{
//			adapter.pos = -1;
//			adapter.notifyDataSetChanged();
//			isPlayer = false;
//		}
//    	
//    };
//    
//    /**
//     * 播放准备
//     */
//    onPrepared onPrepred = new onPrepared()
//    {
//
//		@Override
//		public void onPrepared() 
//		{
//			if (adapter.pos != -1 && adapter.pos != -2) 
//			{
//				isPlayer = true;
//				adapter.notifyDataSetChanged();
//			
//			}
//		}
//    };
//    
//    /**
//     * 播放异常
//     */
//    ErrorListener errorListener = new ErrorListener()
//    {
//
//		@Override
//		public void onError(MediaPlayer arg0, int arg1, int arg2) 
//		{
//			stopMusic();
//			adapter.pos = -1;
//			adapter.notifyDataSetChanged();
//			isPlayer = false;
//		}
//    	
//    };
//    
//    /**
//     * 停止播放
//     */
//    private void stopMusic()
//    {
//    	isPlayer = false;
//    	adapter.pos = -1;
//		adapter.notifyDataSetChanged();
//    	if(mediaPlayer == null)
//    	{
//    		return;
//    	}
//		if (mediaPlayer.isPause() || mediaPlayer.isPlaying() || mediaPlayer.isprepared()||mediaPlayer.isLooped())
//		{
//			mediaPlayer.cancelPlayer();
//			initMediaPlayer();
//		}
//    }
//    /**
//     * 清空listview
//     */
//    private void resetListView()
//    {
//    	mDataSun.clear();
//		adapter.pos=-2;
//		adapter.notifyDataSetChanged();
//    }
//    
//    /**
//     * 处理点击事件
//     * @author Administrator
//     *
//     */
//    
//    public class Click implements OnClickListener
//    {
//
//		@Override
//		public void onClick(View v) 
//		{
//			// 点击时间过快，不操作
//			if (Utils.isFastDoubleClick()) 
//			{
//				return;
//			}
//			switch (v.getId()) 
//			{
//			
//			case R.id.textview_recommended:
//				startPbarU();
//				changeTextViewBackground(1);
//				stopMusic();
//				resetParms();//初始化参数
//				
//				resetListView();//清空listview
//				mPullToRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);//初始化上拉控件模式
//				initDefaultRing();
//				
//				break;
//            case R.id.textview_month:
//            	startPbarU();
//            	changeTextViewBackground(3);
//            	stopMusic();
//            	resetParms();//初始化参数
//            	mPullToRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);//初始化上拉控件模式
//            	resetListView();
//            	week_month_total="3";
//				initMonthRing();
//				break;
//            case R.id.textview_total:
//            	startPbarU();
//            	changeTextViewBackground(4);
//            	stopMusic();
//            	
//            	resetParms();//初始化参数
//            	mPullToRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);//初始化上拉控件模式
//            	resetListView();
//            	week_month_total="4";
//				initTotalRing();
//            	
//				break;
//             case R.id.textview_week:
//            	 startPbarU();
//            	 changeTextViewBackground(2);
//            	 stopMusic();
//            	 resetParms();//初始化参数
//            	 mPullToRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);//初始化上拉控件模式
//            	 resetListView();
//            	 week_month_total="2";
// 				 initWeekRing();
//	            break;
//
//             case R.id.title_iv:
//            	 MainGroupActivity activity = (MainGroupActivity) getActivity();
//            	 activity.skipToHome();
//            	 
//            	 break;
//
//			default:
//				break;
//			}
//		}
//    	
//    }
//    
//    /**
//     * 改变选中tv的背景
//     */
//    private void changeTextViewBackground(int i)
//    {
//    	if(i==1)
//    	{
//    		tv_recommend.setBackgroundResource(R.color.list_tv_bkg_selected);
//    		tv_recommend.setTextColor(getResources().getColor(R.color.list_tv_textcolor_selected));
//    		tv_week.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_week.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		tv_month.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_month.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		tv_total.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_total.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		recommend_week_month_total="1";
//    		
//    	}
//    	else if(i==2)
//    	{
//    		tv_week.setBackgroundResource(R.color.list_tv_bkg_selected);
//    		tv_week.setTextColor(getResources().getColor(R.color.list_tv_textcolor_selected));
//    		tv_recommend.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_recommend.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		tv_month.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_month.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		tv_total.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_total.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		recommend_week_month_total="2";
//    	}
//    	else if(i==3)
//    	{
//    		tv_month.setBackgroundResource(R.color.list_tv_bkg_selected);
//    		tv_month.setTextColor(getResources().getColor(R.color.list_tv_textcolor_selected));
//    		tv_week.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_week.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		tv_recommend.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_recommend.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		tv_total.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_total.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		recommend_week_month_total="3";
//    	}
//    	else if(i==4)
//    	{
//    		tv_total.setBackgroundResource(R.color.list_tv_bkg_selected);
//    		tv_total.setTextColor(getResources().getColor(R.color.list_tv_textcolor_selected));
//    		tv_week.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_week.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		tv_month.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_month.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		tv_recommend.setBackgroundResource(R.drawable.list_textview_bkg_selector);
//    		tv_recommend.setTextColor(getResources().getColor(R.color.list_textview_selector));
//    		recommend_week_month_total="4";
//    	}
//    	else
//    	{
//    		
//    	}
//    		
//    }
//    
//    //获取当前选中的list
//    private String getSelectedList()
//    {
//    	return recommend_week_month_total;
//    }
//    
//    // 判断上拉是否可用
// 	private boolean isUpScrollEnable() 
// 	{
// 		int all = pageNum * 10;
// 		if (all >= mSumCount) 
// 		{
// 			return false;
// 		}
// 		return true;
// 	}
//    
//    //刷新
// 	OnRefreshListener2 listener=new OnRefreshListener2() 
// 	{
//
//		@Override
//		public void onPullDownToRefresh(PullToRefreshBase refreshView) 
//		{
//			startPbarU();
//			stopMusic();
//			resetParms();//初始化参数
//			resetListView();//清空listview
//			if(getSelectedList().equals("1"))
//			{
//				initDefaultRing();
//			}
//			else if(getSelectedList().equals("2"))
//			{
//				
//				initWeekRing();
//			}
//			else if(getSelectedList().equals("3"))
//			{
//				
//				initMonthRing();
//			}
//			else if(getSelectedList().equals("4"))
//			{
//				initTotalRing();
//			}
//			
//			
//			//下拉刷新处理
//			mPullToRefreshListView.postDelayed(new Runnable() 
//			{
//				public void run() 
//				{
//					mPullToRefreshListView.onRefreshComplete();
//				}
//			}, 1000);
//		}
//
//		@Override
//		public void onPullUpToRefresh(PullToRefreshBase refreshView) 
//		{
//			//是否已经上拉标志位
//			if(!isSendRequset)
//			{
//			//上拉刷新处理
//			if (isUpScroll()) 
//				{
//				    //上拉推荐铃音
//					if(getSelectedList().equals("1"))
//					{
//						initDefaultRing();
//						isSendRequset=true;
//					}
//					else if(getSelectedList().equals("2"))
//					{
//						initWeekRing();
//						isSendRequset=true;
//					}
//					else if(getSelectedList().equals("3"))
//					{
//						initMonthRing();
//						isSendRequset=true;
//					}
//					else if(getSelectedList().equals("4"))
//					{
//						initTotalRing();
//						isSendRequset=true;
//					}
//				
//					//adapter.notifyDataSetChanged();
//	
//				} 
//			}	
//				
//				mPullToRefreshListView.postDelayed(new Runnable() 
//				{
//					public void run() 
//					{
//						mPullToRefreshListView.onRefreshComplete();
//						if(!isUpScroll())
//						{
//							mPullToRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);				
//						}
//						
//					}
//				}, 1000);
//		}
// 		
// 		
//	};
// 	
//	
//	/**
//     * 滑到最后一条数据处理
//     */
// 	OnLastItemVisibleListener onLastItemListener=new OnLastItemVisibleListener() 
// 	{
//
//		@Override
//		public void onLastItemVisible() 
//		{
//			
//			mPullToRefreshListView.postDelayed(new Runnable() 
//			{
//				public void run() 
//				{
//					mPullToRefreshListView.onRefreshComplete();
//				}
//			}, 1000);
//			if(!isUpScroll())
//			{
//				mPullToRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);				
//			}
//		}
//	};
//    
//	/**
//     * 试听异常处理
//     */
//	onException exception = new onException()
//	{
//
//		@Override
//		public void onException(int code, String des) 
//		{
//			if(Utils.CheckTextNull(des))
//			{
//				Utils.showTextToast(getActivity(), UtlisReturnCode.ReturnCode(des, mContext));
//			}
//			stopMusic();
//			adapter.notifyDataSetChanged();
//			adapter.pos = -1;
//			isPlayer = false;
//		}
//		
//	};
//	
//	/**
//	 * listview 单击item监听
//	 * @author Administrator
//	 *
//	 */
//	OnItemClickListener mOnItemClickListener=new OnItemClickListener() 
//	{
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) 
//		{
//			// 点击时间过快，不操作
//			if (Utils.isFastDoubleClick()) 
//			{
//				return;
//			}
////			if (isPlayer||mediaPlayer.isprepared()) 
////			{
////				adapter.pos = -1;
////				adapter.notifyDataSetChanged();
////				stopMusic();
////			}
//			stopMusic();
//			Intent intent=new Intent(mContext,DetailsRingtoneActivity.class);
//			System.out.println("---position--"+position);
//			intent.putExtra("bean", mDataSun.get(position-1));
//			startActivity(intent);
//			
//		}
//		
//	};
//	
//	public class ViewHolder
//	{
//		private ImageView mIv_Play_Stop_Loading,mHot,mLoading;
//		private TextView song,singer;
//	}
//	
//	/**
//	 * 适配器
//	 * @author Administrator
//	 *
//	 */
//	public class ListViewAdapter extends BaseAdapter
//	{
//
//		private Context context;
//		private LayoutInflater inflater;
//		public ViewHolder viewholder = null;
//		public int pos = -2;
//		
//		public int old;
//		
//		private ListViewAdapter(Context context)
//		{
//			this.context=context;
//			this.inflater=LayoutInflater.from(context);
//		}
//
//		@Override
//		public int getCount() 
//		{
//			if(mDataSun.size()==0)
//			{
//			return 0;
//			}
//			return mDataSun.size();
//		}
//
//		@Override
//		public Object getItem(int position) 
//		{
//			if(mDataSun.size()==0)
//			{
//			return null;
//			}
//			return mDataSun.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) 
//		{
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) 
//		{
//			if(convertView==null)
//			{
//				convertView=inflater.inflate(R.layout.item_list_fragment_layout, null);
//				viewholder=new ViewHolder();
//				viewholder.singer=(TextView) convertView.findViewById(R.id.singer);
//				viewholder.song=(TextView) convertView.findViewById(R.id.song);
//				viewholder.mHot=(ImageView) convertView.findViewById(R.id.hot);
//				viewholder.mIv_Play_Stop_Loading=(ImageView) convertView.findViewById(R.id.music_play_stop_loading);
//				viewholder.mLoading=(ImageView)convertView.findViewById(R.id.music_loading);
//				convertView.setTag(viewholder);
//			}
//			else
//			{
//				viewholder=(ViewHolder) convertView.getTag();
//			}
//			viewholder.singer.setText(mDataSun.get(position).getSingerName());
//			viewholder.song.setText(mDataSun.get(position).getToneName());
//			final int temp=position;
//			if(position<3)
//			{
//				viewholder.mHot.setVisibility(View.VISIBLE);
//			}
//			else
//			{
//				viewholder.mHot.setVisibility(View.INVISIBLE);
//			}
//			
//			viewholder.mIv_Play_Stop_Loading.setOnClickListener(new OnClickListener() 
//			{
//				
//				@Override
//				public void onClick(View v) 
//				{
//					if (Utils.isFastDoubleClick()) 
//					{
//						return;
//					}
//					int oldPos = pos; 
//					pos = temp;
//					// 点击时间过快，不操作
//					
//					//String url = "http://10.32.161.122:8080/backmusic/wav/sys/224.wav";
//
//					if(mediaPlayer == null)
//					{
//					    initMediaPlayer();
//					}
//					
//					if(mediaPlayer.isPause() && -1 == oldPos && temp == old)
//					{
//						mediaPlayer.startPlayer();
//						pos = temp;
//						isPlayer = true;
//						return;
//					}
//					
//					if(oldPos == temp || oldPos == -2 || oldPos == -1)
//					{
//						if(oldPos != temp)
//						{
//							mediaPlayer.cancelPlayer();
//							isPlayer = false;
//							pos = temp;
//							mediaPlayer.getUrlFromServer(mDataSun.get(temp).getToneID(), mDataSun.get(temp).getToneType());
//							notifyDataSetChanged();
//							return;
//						}
//						
//						if (mediaPlayer.isprepared())
//						{
//						    return;
//						}
//						
//						
//					    if (!isPlayer) 
//					    {
////					    	GetToneListenAddrReq req=new GetToneListenAddrReq(p_h);
////							req.sendGetToneListenAddrReq(mDataSun.get(temp).getToneID(), mDataSun.get(temp).getToneType());
//	                        // 播放
////	                        mediaPlayer.setDataSource(url);
////	                        mediaPlayer.prepare();
//					    	mediaPlayer.getUrlFromServer(mData.get(temp).getToneID(), mData.get(temp).getToneType());
//					    	pos = temp;
//	                        notifyDataSetChanged();
//	                    } 
//					    else 
//					    {
//                            old = pos;
//					    	pos = -1;
//	                        notifyDataSetChanged();
//	                        mediaPlayer.pause();
//	                        isPlayer = false;
//	                    }
//					}
//					else
//					{
////						GetToneListenAddrReq req=new GetToneListenAddrReq(p_h);
////						req.sendGetToneListenAddrReq(mDataSun.get(temp).getToneID(), mDataSun.get(temp).getToneType());
//                        mediaPlayer.cancelPlayer();
//                        isPlayer = false;
//                        pos = temp;
//                        mediaPlayer.getUrlFromServer(mDataSun.get(temp).getToneID(), mDataSun.get(temp).getToneType());
////                      mediaPlayer.setDataSource(url);
////                      mediaPlayer.prepare();
//                        notifyDataSetChanged();
//					}
//				}
//			});
//			if (pos == position) 
//			{
//				if (isPlayer)
//				{
////					viewholder.mLoading.setAnimation(null);
//					viewholder.mLoading.setVisibility(View.INVISIBLE);
//					viewholder.mIv_Play_Stop_Loading.setBackgroundResource(R.drawable.music_stop);
//					
//				} 
//				else 
//				{
//					// 处理试听加载效果
//					viewholder.mIv_Play_Stop_Loading.setBackgroundResource(0);
////					if(viewholder.mLoading.getAnimation()==null)
////					{
////					viewholder.mLoading.setAnimation(animation);
////					animation.startNow();
////					}
//					viewholder.mLoading.setVisibility(View.VISIBLE);
//				}
//			} 
//			else 
//			{
////				viewholder.mLoading.setAnimation(null);
//				viewholder.mLoading.setVisibility(View.INVISIBLE);
//				viewholder.mIv_Play_Stop_Loading.setBackgroundResource(R.drawable.music_play);
//				
//			}
//			return convertView;
//		}
//	}
//	@Override
//	public void onStart() {
//		super.onStart();
//	}
//    
//}
