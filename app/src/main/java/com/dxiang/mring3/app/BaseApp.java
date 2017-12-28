package com.dxiang.mring3.app;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.flurry.android.FlurryAgent;

public class BaseApp extends Application {
	/**
	 * application 唯一实例
	 */
	private static Application mInstance;
	public static final String TAG=BaseApp.class.getSimpleName();
	int appCount=0;
	int destorycount=0;
    Intent intent;
	public void init() {
		mInstance = this;
		// FlurryAgent.setLogEnabled(false);
		// FlurryAgent.init(this, Utils.API_KEY);
		// FlurryAgent.setLogEnabled(false);
		// // init Flurry
		// FlurryAgent.init(this,Utils.API_KEY);
		// Flurry 3.4.0调用
		// Init Flurry
		FlurryAgent.setLogEnabled(true);
//		SystemPropertyUtil.initSystemProperties(this, null);
//		SystemPropertyUtil.initHomeAds(this);
		UnCatchException catchExcep = new UnCatchException(this);
	    Thread.setDefaultUncaughtExceptionHandler(catchExcep);
//	    intent = new Intent(getBaseContext().getApplicationContext(), FxService.class);
//	    startService(intent);
	}
	
	private boolean isServiceConnectSuccess=false;
	
	@Override
	public void onCreate() {
		super.onCreate();
//		if (isServiceWork(getApplicationContext(), "com.dxiang.mring3.app.FxService")) {
//			stopServiceModel();
//		}
		this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			
			@Override
			public void onActivityStopped(Activity activity) {
	            Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityStopped mFinalCount = "+appCount+";activity = "+activity.getClass().getName());
				appCount--;
			     if(appCount==0){//如果appCount ==0，说明是前台到后台
					if (isServiceConnectSuccess) {
						hideServiceAD();
					}
			     }
			}
			
			@Override
			public void onActivityStarted(Activity activity) {
				 appCount++;
		        //如果appCount ==1，说明是从后台到前台
	            Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityStarted mFinalCount = "+appCount+";activity = "+activity.getClass().getName());
	            if (appCount == 1){
	                Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityStarted mFinalCount ==1 说明是从后台到前台");		
					if (isServiceConnectSuccess) {
						showServiceAD();
					}
	            }
			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle arg1) {
	            Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivitySaveInstanceState mFinalCount = "+appCount+";activity = "+activity.getClass().getName());
				
			}
			
			@Override
			public void onActivityResumed(Activity activity) {
	            Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityResumed mFinalCount = "+appCount+";activity = "+activity.getClass().getName());
				
			}
			
			@Override
			public void onActivityPaused(Activity activity) {
	            Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityPaused mFinalCount = "+appCount+";activity = "+activity.getClass().getName());
				
			}
			
			@Override
			public void onActivityDestroyed(Activity activity) {//应该在这里吧子线程关掉
	            Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityDestroyed mFinalCount = "+appCount+";activity = "+activity.getClass().getName());
				destorycount--;//窗体销毁，就要销毁一次
				if(destorycount==0){
					unBindServiceModel();//此处是让Application销毁的时候，把服务关闭掉，使得后台进程把Application关闭的时候，、
									//服务依然存在，再次启动Application的时候，服务生命周期不create
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}
			
			@Override
			public void onActivityCreated(Activity activity, Bundle arg1) {
	            Log.e(TAG,TAG+".registerActivityLifeCallbasks.onActivityCreated mFinalCount = "+appCount+";activity = "+activity.getClass().getName());
				destorycount++;//窗体创建，就要销毁+1
				if (destorycount==1) {
					bindServiceModel();
				}
			}
		});
		//初始化并获得Application的对象，并开启  FxService 
		init();
	}
	
    private void iteratorService(){
		ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = activityManager.getRunningServices(1000);
		Log.e(TAG, TAG+",iteratorService,遍历开始");
		for (int i = 0; i < runningServices.size(); i++) {
			String className = runningServices.get(i).service.getClassName();
			if (className.equals(FxService.class.getName())) {
				Log.e(TAG, TAG+",iteratorService,"+className);
			}
		}
	}
	
	
   public void unBindServiceModel(){
	   if (isServiceConnectSuccess) {
		   messenger=null;
		   unbindService(conn);
	   }
   }
   
   public void bindServiceModel(){
	    intent = new Intent(getBaseContext().getApplicationContext(), FxService.class);
	    bindService(intent, conn, Context.BIND_AUTO_CREATE);
   }
   public void startServiceModel(){
	   intent = new Intent(getBaseContext().getApplicationContext(), FxService.class);
	   startService(intent);
   }
   
   public void stopServiceModel(){
	 	intent = new Intent(getApplicationContext(), FxService.class);//此处是关闭。服务，并不能关闭窗体；
	 	stopService(intent);
   }
   
   public void hideServiceAD(){
		try {
			messenger.send(oHandler.obtainMessage(FxService.AD_MSG_HIDE));
		} catch (RemoteException e) {
			Log.e(TAG, TAG+"_ServiceConnection.onServiceConnected", e);
		}
   }
   public void showServiceAD(){
		try {
			messenger.send(oHandler.obtainMessage(FxService.AD_MSG_SHOW));
		} catch (RemoteException e) {
			Log.e(TAG, TAG+"_ServiceConnection.onServiceConnected", e);
		}
   }
   
   
//   start 服务连接器，连接Service  是的进程之间通讯==========================
   private Messenger messenger;
   private OutgoingHandler oHandler=new OutgoingHandler();
   private Messenger mOutMessenger=new Messenger(oHandler);
   private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e(TAG,TAG+"_连接成功_ServiceConnection.onServiceConnected");
			messenger=new Messenger(service);
			Message message=new Message();
			message.obj=mOutMessenger;
			message.what=FxService.AD_MSG_HANDLER_CONNECT_SUCCESS;
			try {
				messenger.send(message);
			} catch (RemoteException e) {
				Log.e(TAG, TAG+"_ServiceConnection.onServiceConnected", e);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e(TAG,TAG+"_连接断开_ServiceConnection.onServiceDisconnected");
		}

	};
	
	
	
	private class OutgoingHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FxService.AD_MSG_CLOSE:
//				iteratorService();
				unBindServiceModel();
				isServiceConnectSuccess=false;
//				iteratorService();
				break;
			case FxService.AD_MSG_HIDE:
				
				break;
			case FxService.AD_MSG_SHOW:
				
				break;
			case FxService.AD_MSG_HANDLER_CONNECT_SUCCESS:
				Log.e(TAG, TAG+",FxService.AD_MSG_HANDLER_CONNECT_SUCCESS");
				isServiceConnectSuccess=true;
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
		
		
	}
	
	

//  end 服务连接器，连接Service  是的进程之间通讯==========================
	
	
	
	
   

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
//		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
//				context);
//		config.threadPriority(Thread.NORM_PRIORITY - 2);
//		config.denyCacheImageMultipleSizesInMemory();
//		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
//		config.discCache(new UnlimitedDiskCache(StorageUtils.getCacheDirectory(context)));
//		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
//		config.tasksProcessingOrder(QueueProcessingType.LIFO);
//		config.writeDebugLogs(); // Remove for release app
//
//		// Initialize ImageLoader with configuration.
//		ImageLoader.getInstance().init(config.build());
	}

	/**
	 * @return 获取application实例
	 */
	public static Application getInstance() {

		return mInstance;

	}

	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}
    /** 
     * 判断某个服务是否正在运行的方法 
     *  
     * @param mContext 
     * @param serviceName 
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService） 
     * @return true代表正在运行，false代表服务没有正在运行 
     */  
    public boolean isServiceWork(Context mContext, String serviceName) {  
        boolean isWork = false;  
        ActivityManager myAM = (ActivityManager) mContext  
                .getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningServiceInfo> myList = myAM.getRunningServices(40);  
        if (myList.size() <= 0) {  
            return false;  
        }  
        for (int i = 0; i < myList.size(); i++) {  
            String mName = myList.get(i).service.getClassName().toString();  
            if (mName.equals(serviceName)) {  
                isWork = true;  
                break;  
            }  
        }  
        return isWork;  
    }  
	

}
