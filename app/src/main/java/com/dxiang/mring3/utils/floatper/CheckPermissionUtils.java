package com.dxiang.mring3.utils.floatper;

import java.lang.reflect.Method;

import com.dxiang.mring3.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;


public class CheckPermissionUtils {
	
	private static Dialog mDialog = null;
	
	public static void applyPermission(Context context,int appOpsManagerConstants){
		if (Build.VERSION.SDK_INT<23) {
			sdkBelowEqual22ApplyPermission(context, appOpsManagerConstants);
		}else {//由于版本目前适配到5.0 所以暂且用不到权限；
            //如果没勾选“不再询问”，向用户发起权限请求
//          ActivityCompat.requestPermissions((Activity) context, new String[]{manifestPermission}, 0);
		}
	}
	
	public static boolean checkPermission(Context context,int appOpsManagerConstants){
		boolean result=false;
		if (Build.VERSION.SDK_INT<23) {
			result=sdkBelowEqual22CheckPermission(context, appOpsManagerConstants);
		}else {//由于版本目前适配到5.0 所以暂且不用到权限；
			result=sdkHighEqual23CheckPermission(context, appOpsManagerConstants);
		}
		return result;
	}
	
    /**
     * 通用 rom 权限申请
     * @param appOpsManagerConstants 
     */
    private  static void sdkBelowEqual22ApplyPermission( Context context, int appOpsManagerConstants) {
        //这里也一样，魅族系统需要单独适配
    	int resPermission=AppOpsManagerConstants.op2StringToast(appOpsManagerConstants);
//    	String msg=context.getString(resPermission);
    	IOnDialogOnClick iOnDialogOnClick=new IOnDialogOnClick(context);
    	showConfirmDialog(context, resPermission, iOnDialogOnClick);
        	
        	
    }
	
	
	@SuppressLint("NewApi")
	private static boolean  sdkBelowEqual22CheckPermission(Context context,int appOpsManagerConstants){
		boolean result=false;
		try {
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
				AppOpsManager appOpsManager=(AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
				Class<AppOpsManager> clajj=AppOpsManager.class;
				Method method=clajj.getDeclaredMethod("checkOp", int.class,int.class,String.class);
				int checkResult=(Integer) method.invoke(appOpsManager, 
						appOpsManagerConstants,
						Binder.getCallingUid(),
						context.getPackageName());
				
				if (checkResult == AppOpsManager.MODE_ALLOWED) {
					result=true;
					Log.e("CheckPermissionUtils", "有权限");
				} else if (checkResult == AppOpsManager.MODE_IGNORED) {
					// TODO: 只需要依此方法判断退出就可以了，这时是没有权限的。
					Log.e("CheckPermissionUtils", "CheckPermissionUtils.sdkBelow22Permission 被禁止了");
				} else if (checkResult == AppOpsManager.MODE_ERRORED) {
					Log.e("CheckPermissionUtils", "CheckPermissionUtils.sdkBelow22Permission 出错了");
				} else
//					if (checkResult == AppOpsManager.MODE_DEFAULT)
					{
					Log.e("CheckPermissionUtils", "CheckPermissionUtils.sdkBelow22Permission 权限需要询问");
				}
			}
		} catch (Exception e) {
			Log.e("CheckPermissionUtils", "sdkBelow23Permission ",e);
		}
		return result;
	}
	
	
	private static boolean sdkHighEqual23CheckPermission(Context context,int appOpsManagerConstants){
		boolean result=true;
//		boolean result=false;
//		String manifestPermission=AppOpsManagerConstants.op2MainifestPer(appOpsManagerConstants);
//		result=ActivityCompat.checkSelfPermission(context, manifestPermission) == PackageManager.PERMISSION_GRANTED;
		return result;
	}
	
    private static void showConfirmDialog(Context context, int message, DialogInterface.OnClickListener result) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = new AlertDialog.Builder(context).setCancelable(true).setTitle("")
                .setMessage(message)
                .setPositiveButton(R.string.ok,result)
                .setNegativeButton(R.string.no,result)
                .create();
        mDialog.show();
    }
    private static  class IOnDialogOnClick implements  DialogInterface.OnClickListener {
    	private Context context;
    	public IOnDialogOnClick setConext(Context context ){
    		 this.context=context;
    		 return this;
    	}
    	
		public IOnDialogOnClick(Context context ) {
			super();
			 this.context=context;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			  dialog.dismiss();
			  System.out
					.println("CheckPermissionUtils.IOnDialogOnClick.onClick() which = "+which);
              mDialog=null;
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE://BUTTON_POSITIVE
				try {
                    // 去应用信息
                    Intent localIntent = new Intent();
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= 9) {
                        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                    } else if (Build.VERSION.SDK_INT <= 8) {
                        localIntent.setAction(Intent.ACTION_VIEW);
                        localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                        localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                    }
                    context.startActivity(localIntent);
//                    Class clazz = Settings.class;
//                    Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
//
//                    Intent intent = new Intent(field.get(null).toString());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setData(Uri.parse("package:" + context.getPackageName()));
//                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.e("commonROMPermissionApply", Log.getStackTraceString(e));
                }
				break;
			default:
				break;
			}
                
			
		}
        
    }
}
