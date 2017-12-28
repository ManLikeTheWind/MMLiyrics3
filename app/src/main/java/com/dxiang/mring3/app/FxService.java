package com.dxiang.mring3.app;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.R.integer;
import android.R.menu;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Configuration;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gem.imgcash.ImageDownLoader;
import com.gem.imgcash.ImageDownLoader.onImageLoaderListener;
import com.dxiang.mring3.R;
import com.dxiang.mring3.bean.ImageInfo;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.GetimageinforReq;
import com.dxiang.mring3.response.GetImageInfoRsp;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.CustomProgressDialog;
import com.dxiang.mring3.utils.FileUtil;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.utils.WindowArg;
import com.dxiang.mring3.views.gallery.GalleryEx;

public class FxService extends Service {
	
	public static final int AD_MSG_CLOSE=0;
	public static final int AD_MSG_HIDE=1;
	public static final int AD_MSG_SHOW=2;
	public static final int AD_MSG_HANDLER_CONNECT_SUCCESS=3;
	

	// 定义浮动窗口布局
	LinearLayout mFloatLayout;
	LayoutParams wmParams;
	// 创建浮动窗口设置布局参数的对象
	WindowManager mWindowManager;

	private CustomProgressDialog progressDialog;

	AdapterMovieCarrosel carroselAdapter;
	private boolean needFresh = true;
	ImageView iv_bg;
	Button mFloatView;
	protected long responseTime = -1l;
	private static final String TAG = "FxService";
	GalleryEx pagerCarrosel;
	private List<ImageInfo> infos;
	private Handler mHandlerPosterAutoScroll = new Handler();
	private boolean isSelect = false;
	int miRecommendListCount = 0;
	Bitmap mBgBitmap;
	private LinearLayout mLinLayoutBigPosterGuidePoint;
	private int miBigPosterLastIndex = 0;
	private boolean needToFresh = true;
	private ImageView iv_close_window;
	private FrameLayout header;
	public int w, h;
	protected Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				int what = msg.what;
				Log.e("message", "what==" + what);
				responseTime = System.currentTimeMillis();
				switch (what) {
				case FusionCode.NETWORK_SUCESSED:
					reqXmlSucessed(msg);
					break;
				// 内容格式错误
				case FusionCode.NETWORK_SUCESSED2:
					reqXmlFail(msg);
					break;
				case FusionCode.NETWORK_ERROR:// 网络失败
				case FusionCode.NETWORK_TIMEOUT:
				case FusionCode.NETWORK_BUSY:
				case FusionCode.PARSER_ERROR:
					reqError(msg);
					break;
				default:// 处理其它
					reqOther(msg);
					break;
				}
			} catch (Exception e) {
				UtilLog.e("UiParent", "Exception: " + e.getMessage());
			}
		}
	};

	GetimageinforReq req = new GetimageinforReq(handler);

	public void reHandlerRequest() {
		req.sendGetimageinforReqRequest("2");
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// Log.i(TAG, "oncreat");
		// if (Commons.WIDTH == 0) {
		// Utils.initScreenProperties(getApplicationContext());
		// }
		// if (TextUtils.isEmpty(Commons.FILE_PATH)) {
		// SystemPropertyUtil.initSystemProperties(getApplicationContext(),
		// null);
		// }
		// Toast.makeText(FxService.this, "create FxService",
		// Toast.LENGTH_LONG);
		reHandlerRequest();

		if (null != mHandlerPosterAutoScroll) {
			mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
		}
		w = WindowArg.getInstance(getApplicationContext()).getWindowWidth();
		h = WindowArg.getInstance(getApplicationContext()).getWindowHeigt();
		createFloatView();
		needFresh = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_NOT_STICKY;
		// return START_REDELIVER_INTENT;
	}

	@Override
	public IBinder onBind(Intent intent) {
		IBinder iBinder=messenger.getBinder();
		
		return iBinder;
	}
	
//  start 服务连接器，连接Service将handler 传递过来  是的进程之间通讯==========================
	private IncomingHandler iHandler=new IncomingHandler();
	private Messenger messenger=new Messenger(new IncomingHandler());
	private Messenger mOutgoingHandler;
	
	private class IncomingHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FxService.AD_MSG_HIDE:
				hideWindow();
				break;
			case FxService.AD_MSG_SHOW:
				showWindow();
				break;
			case FxService.AD_MSG_HANDLER_CONNECT_SUCCESS:
				Log.e(TAG, TAG+",FxService.AD_MSG_HANDLER_CONNECT_SUCCESS");
				if (msg.obj!=null) {
					mOutgoingHandler=(Messenger) msg.obj;
					try {
						mOutgoingHandler.send(iHandler.obtainMessage(FxService.AD_MSG_HANDLER_CONNECT_SUCCESS));
					} catch (RemoteException e) {
						Log.e(TAG, TAG+",FxService.AD_MSG_HANDLER_CONNECT_SUCCESS",e);
					}
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}
	private void sendOutGoingUnbindService(){
		try {
			mOutgoingHandler.send(iHandler.obtainMessage(FxService.AD_MSG_CLOSE));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
//	private MyBinder myBinder = new MyBinder();
//	public class MyBinder extends Binder {
//		public FxService getService() {
//			return FxService.this;
//		}
//	}
	// public GalleryEx getPagerCarrosel() {
	// return pagerCarrosel;
	// }	
//  end   服务连接器，连接Service将handler 传递过来  是的进程之间通讯==========================
	public void hideWindow() {
			mHandlerPosterAutoScroll.removeCallbacks(autoFling);
			header.setVisibility(View.GONE);
			mFloatLayout.setVisibility(View.GONE);
	}

	public void showWindow() {
			mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			header.setVisibility(View.VISIBLE);
			mFloatLayout.setVisibility(View.VISIBLE);
	}

	public void createFloatView() {
		wmParams = new LayoutParams();
		// 获取WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getBaseContext().getApplicationContext()
				.getSystemService(getBaseContext().getApplicationContext().WINDOW_SERVICE);
		// 设置window type
		if (android.os.Build.MODEL.startsWith("m")) {
			wmParams.type = LayoutParams.TYPE_PHONE;
		} else {
			wmParams.type = LayoutParams.TYPE_TOAST;
		}
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmParams.flags =
		// LayoutParams.FLAG_NOT_TOUCH_MODAL |
		LayoutParams.FLAG_NOT_FOCUSABLE;
		// LayoutParams.FLAG_NOT_TOUCHABLE
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.BOTTOM;
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = 0;
		wmParams.y = 0;
		// 设置悬浮窗口长宽数据
		wmParams.width = LayoutParams.MATCH_PARENT;
		wmParams.height = w / 4;

		LayoutInflater inflater = LayoutInflater.from(getBaseContext().getApplicationContext());
		// 获取浮动窗口视图所在布局
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.bottomer_gallery_point, null);

		// iv_bg = (ImageView) mFloatLayout
		// .findViewById(R.id.iv_bg);
		iv_close_window = (ImageView) mFloatLayout.findViewById(R.id.iv_close_window);
		iv_close_window.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				SharedPreferenceService.getInstance(getApplicationContext()).put("ad", true);// 此处是关闭
//				// 窗体。:True:
//				hideWindow();
//				SharedPreferenceService.getInstance(getApplicationContext()).put("adOpen", false);
//				isFirst = false;
				 if (mFloatLayout != null) {
					 mWindowManager.removeView(mFloatLayout);
					 mFloatLayout=null;
				 }
				 sendOutGoingUnbindService();
				 
			}
		});
		initTopGallery();
		// 添加mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);
		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
	}

	public void bottomADRemove() {

		mHandlerPosterAutoScroll.removeCallbacks(autoFling);
		header.setVisibility(View.GONE);
		mFloatLayout.setVisibility(View.GONE);

		// if (mFloatLayout != null) {
		// mWindowManager.removeView(mFloatLayout);
		// }

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mFloatLayout != null) {
			mWindowManager.removeView(mFloatLayout);
			// mWindowManager.
		}
		if (null != mHandlerPosterAutoScroll) {
			mHandlerPosterAutoScroll.removeCallbacks(autoFling);
		}
		if (infos != null) {
			infos.clear();
		}

		SharedPreferenceService.getInstance(getApplicationContext()).put("adOpen", false);
	}

	/**
	 * 网络异常处理方法
	 */
	public void error(Message msg) {
		String result = Utils.getResouceStr(getApplicationContext(), R.string.nowifi);
		showTextToast(getApplicationContext(), result);
	}

	public void showTextToast(Context context, String msg) {
		Utils.showTextToast(context, msg);
	}

	/**
	 * 请求网络失败处理
	 */
	@SuppressLint("NewApi")
	protected void reqError(Message msg) {
		error(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_GetImageInfo:
			mBgBitmap = ImageDownLoader.getShareImageDownLoader().getBitmapFromMemCache("default_banner");
			if (mBgBitmap == null) {
				mBgBitmap = FileUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.default_banner, w,
						w / 4);
				ImageDownLoader.getShareImageDownLoader().addBitmapToMemoryCache("default_banner", mBgBitmap);
			}
			// pagerCarrosel.setLayoutParams(new
			// FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			mFloatLayout.setBackground(new BitmapDrawable(mBgBitmap));
			// iv_bg.setScaleType(ScaleType.FIT_XY);
			// pagerCarrosel.setsc
			creatPointGuide(0);
			// mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			// isSelect = false;
			break;
		}
	}

	/**
	 * 请求xml成功后的处理
	 */
	protected void reqXmlSucessed(Message msg) {
		switch (msg.arg1) {
		case FusionCode.REQUEST_GetImageInfo:
			GetImageInfoRsp rsp = (GetImageInfoRsp) msg.obj;
			infos = rsp.getInfos();
			miRecommendListCount = infos.size();
			creatPointGuide(miRecommendListCount);
			setBannerAdapter(infos);
			mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
			isSelect = false;
			break;
		}
	}

	/**
	 * 请求xml成功处理
	 * 
	 */
	protected void reqXmlFail(Message msg) {
	}

	/**
	 * 处理其它请求返回
	 */
	protected void reqOther(Message msg) {

	}

	protected void startPbarU() {
		if (progressDialog == null) {
			progressDialog = new CustomProgressDialog(this, R.style.CustomProgressDialog, true);
			// progressDialog.setMessage("正在加载中...");
		}
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (Math.abs(responseTime - Commons.lastTime) < Commons.HIDETIME) {
					return;
				}
				if (!needFresh) {
					return;
				}

				if (progressDialog != null && !progressDialog.isShowing()) {
					progressDialog.show();
				}
			}
		}, Commons.HIDETIME);
		// CustomProgressDialog.getInstance().show();
	}

	protected void setOnDismissListener(OnDismissListener listener) {
		progressDialog.setOnDismissListener(listener);
	}

	protected void stopPbarU() {
		if (progressDialog != null && progressDialog.isShowing()) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
				}
			}, Commons.HIDETIME);
		}
	}

	protected boolean isShowing() {
		if (progressDialog != null && progressDialog.isShowing()) {
			return true;
		}
		return false;
	}

	private Runnable autoFling = new Runnable() {
		@SuppressWarnings("deprecation")
		public void run() {
			if (isSelect) {
				return;
			}
			if (pagerCarrosel == null) {
				pagerCarrosel = new GalleryEx(getBaseContext().getApplicationContext());
			}

			if (infos != null) {
				pagerCarrosel.onKeyDown(android.view.KeyEvent.KEYCODE_DPAD_RIGHT, null);
			}
			mHandlerPosterAutoScroll.removeCallbacks(autoFling);
			mHandlerPosterAutoScroll.postDelayed(autoFling, 5000);
		};

	};

	private View initTopGallery() {
		if (header == null) {
			// header = getActivity().getLayoutInflater().inflate(
			// R.layout.topper_gallery_layout, null);
			header = (FrameLayout) mFloatLayout.findViewById(R.id.gallery_topper);
			pagerCarrosel = (GalleryEx) mFloatLayout.findViewById(R.id.gallery_gallerys);

			mLinLayoutBigPosterGuidePoint = (LinearLayout) mFloatLayout.findViewById(R.id.guide_activity_newvod_point);
			setBannerAdapter(infos);
			pagerCarrosel.setAnimationDuration(800);
			pagerCarrosel.setNoFling(true);
			pagerCarrosel.dispatchSetSelected(false);
			pagerCarrosel.setCallbackDuringFling(true);
			pagerCarrosel.setOnItemSelectedListener(new OnPosterSelectedListener());
			pagerCarrosel.setOnTouchListener(touch);
			pagerCarrosel.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// TODO Auto-generated method stub
					int pos = arg2 % miRecommendListCount;
					final String baner = infos.get(pos).getLinkURL();
					Uri uri = Uri.parse(baner);
					Intent it = new Intent(Intent.ACTION_VIEW, uri);
					it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplicationContext().startActivity(it);
				}
			});
		}

		return header;
	}

	// Gallery Adapter
	public class AdapterMovieCarrosel extends BaseAdapter {
		private List<ImageInfo> infos;

		private LayoutInflater mInflater;

		// int x;
		//
		// int y;

		Queue<ViewHolder> queue = new LinkedList<ViewHolder>();

		@SuppressWarnings("deprecation")
		public AdapterMovieCarrosel(Context context, List<ImageInfo> infos) {
			this.infos = infos;
            Log.e(TAG,TAG+".AdapterMovieCarrosel.AdapterMovieCarrosel().infos.size = "+infos.size());
			mInflater = LayoutInflater.from(context);
			miRecommendListCount = infos == null ? 0 : infos.size();
		}

		public void setListBan(List<ImageInfo> infos) {
			this.infos = infos;
			notifyDataSetChanged();
			miRecommendListCount = infos == null ? 0 : infos.size();
            Log.e(TAG,TAG+".AdapterMovieCarrosel.setListBan.infos.size = "+infos.size());
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

		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
            Log.e(TAG,TAG+".AdapterMovieCarrosel.getView.infos.size = "+infos.size()+";position = "+position);
			final ViewHolder holder;
			// if(queue.size()<2)
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_ad_layout, null);
				if (queue.size() < 2) {
					holder = new ViewHolder();
				} else {
					holder = queue.poll();
				}
				holder.imgImageView = (ImageView) convertView.findViewById(R.id.logo);
				holder.imgImageView.setLayoutParams(new android.widget.Gallery.LayoutParams(w, w / 4));
				convertView.setTag(holder);
				queue.add(holder);
			} else {
				// convertView = queue.poll();
				Log.d("test", "===convetView==222=");
				holder = (ViewHolder) convertView.getTag();
			}

			Log.e("TAGserviceGETVIEW", "RunTime");
			int pos = position % miRecommendListCount;
			final String baner = infos.get(pos).getLinkURL();//java.lang.IndexOutOfBoundsException: Invalid index 2, size is 0 
															//java.lang.IndexOutOfBoundsException: 
			// setUrlToView(holder.imgImageView, baner, options_Banner, false,
			// R.drawable.default_banner);
			holder.imgImageView.setTag(baner);
			String saveDir = Commons.getImageSavedpath() + baner.replaceAll("[^\\w]", "");
			Bitmap bm = ImageDownLoader.getShareImageDownLoader().getBitmapFromMemCache(saveDir);
			if (bm == null) {
				holder.imgImageView.setBackgroundResource(R.drawable.default_banner);
				ImageDownLoader.getShareImageDownLoader().downloadImage(saveDir, baner, w, w / 4,
						new onImageLoaderListener() {

							@Override
							public void onImageLoader(Bitmap arg0, String arg1) {
								// TODO Auto-generated method stub
								holder.imgImageView.setImageBitmap(arg0);
							}
						});
			} else {
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

	private OnTouchListener touch = new OnTouchListener() {

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			float DownX = 0, DownY = 0, moveX = 0, moveY = 0;
			switch (view.getId()) {
			case R.id.gallery_gallerys: {
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
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
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

	private class OnPosterSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int iposition, long arg3) {
			Log.d("home", "===enter===");
			ImageView imageViewFoc = (ImageView) mLinLayoutBigPosterGuidePoint.getChildAt(miBigPosterLastIndex);
			if (imageViewFoc == null) {
				return;
			}
			imageViewFoc.setImageResource(R.drawable.vpi__tab_unselected_holo);

			//
			final int position = iposition % miRecommendListCount;
			miBigPosterLastIndex = position;
			ImageView imageView = (ImageView) mLinLayoutBigPosterGuidePoint.getChildAt(miBigPosterLastIndex);
			imageView.setImageResource(R.drawable.vpi__tab_selected_holo);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private void setBannerAdapter(List<ImageInfo> infos) {
		if (infos == null || infos.size() == 0) {
			return;
		}
		if (this == null) {
			return;
		}
		if (carroselAdapter == null) {
			carroselAdapter = new AdapterMovieCarrosel(getApplicationContext(), infos);
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
		carroselAdapter.notifyDataSetChanged();
	}

	private void creatPointGuide(int iPointNum) {
		LinearLayout layoutPointGuide = mLinLayoutBigPosterGuidePoint;
		if (null == layoutPointGuide) {
			return;
		} else {
			layoutPointGuide.removeAllViews();
		}
		for (int i = 0; i < iPointNum; i++) {
			if (this == null) {
				continue;
			}
			ImageView imageView = new ImageView(getApplicationContext());
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(10, 0, 10, 0);
			imageView.setLayoutParams(layoutParams);
			if (i == 0 && i < iPointNum) {
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.vpi__tab_selected_holo));
			} else {
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.vpi__tab_unselected_holo));
			}
			layoutPointGuide.addView(imageView);
		}

	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.e(TAG, "onConfigurationChanged "+newConfig);
		super.onConfigurationChanged(newConfig);
	}
}
