package com.dxiang.mring3.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.BaseActivity;
import com.dxiang.mring3.adapter.CustomDialogUpdate;
import com.dxiang.mring3.adapter.CustomDialogUpdatePt;
import com.dxiang.mring3.app.BaseApp;
import com.dxiang.mring3.request.Aboutus;
import com.dxiang.mring3.utils.Utils;

/**
 * 
 * 升级公共
 * 
 * 
 */
@SuppressLint("HandlerLeak")
@SuppressWarnings("unused")
public class UpdateCode extends BaseActivity{

	// 定义常量
	private static Dialog dlg;
	private int code; // 当前版本
	private double name;
	private Dialog dialog;
	private TextView textView, update_text, update, textView_title,textView_number;
	private Button positiveButton;
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	
	/* 进行弹出dialog */
	private static final int POPOU_DIALOG = 3;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	public static Context context;
//	private Context mContext;
	/* 更新进度条 */
	private ProgressBar mProgress;
	
	public static boolean ISABOUTUS=false;
	
	private JSONObject aboutus;
	
	/**
	 * handler:跳转不同的类
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				textView_number.setText(Integer.toString(progress) + "%");
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk(BaseApp.getInstance());
				break;
			case POPOU_DIALOG:
				dialogUpdatePopUp(context);
				break;
			default:
				break;
			}
		};
	};

	

	/**
	 * 显示软件下载对话框
	 */
	public void showDownloadDialog(Context context,boolean flag) {
		// 构造软件下载对话框
		dialog=new Dialog(context, R.style.Translucent_NoTitle);
		dialog.setCancelable(false);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		textView = (TextView) v.findViewById(R.id.progressCount_text);
		textView_title = (TextView) v.findViewById(R.id.progressCount_title);
		textView_number= (TextView) v.findViewById(R.id.progressCount_number);
		positiveButton= (Button) v.findViewById(R.id.positiveButton);
		textView_title.setText(R.string.update_version);
		textView.setText(Utils.getResouceStr(context, R.string.the_update_to_the_latest_version)+
				Utils.latest);
		//下载进度条
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		dialog.setContentView(v);
		if(flag==true){
			positiveButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					cancelUpdate = true;
//					Intent intent = new Intent(Intent.ACTION_MAIN);  
//                    intent.addCategory(Intent.CATEGORY_HOME);  
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
//                    startActivity(intent);  
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);  
				}
			});
		}else{
			positiveButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					cancelUpdate = true;
				}
			});
		}
		dialog.show();

		// 现在文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(Utils.httpurl);
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, "crbtStar.apk");
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 取消下载对话框显示
			dialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk(Context context) {
		File apkfile = new File(mSavePath, "crbtStar.apk");
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
	}

	/**
	 * 提示框提示和升级关于
	 * 
	 * @param id
	 * @param context
	 * @return
	 */

	public void onCreateDialogPt(final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Aboutus about = new Aboutus(handler);
					aboutus = about.getAboutus();
					if(aboutus!=null){
						
						Utils.lowest = (double) Double.parseDouble((String) aboutus
								.getJSONArray("Lowest").get(0));// 最低版本
						Utils.latest = (double) Double.parseDouble((String) aboutus
								.getJSONArray("Latest").get(0));
						Utils.storeurl = (String) aboutus.getJSONArray("StoreURL").get(0);
						Utils.httpurl = (String) aboutus.getJSONArray("HttpURL").get(0);
						Utils.updateinfoub = (String) aboutus.getJSONArray("UpdateInfo")
								.getJSONObject(0).getJSONArray("ub").get(0);
						Utils.updateinfofp = (String) aboutus.getJSONArray("UpdateInfo")
								.getJSONObject(0).getJSONArray("fb").getString(0);
						Utils.updateinfofp1 = (String) aboutus.getJSONArray("UpdateInfo")
								.getJSONObject(0).getJSONArray("ub").getString(1);
//
						code= Utils.getVersionCode(context);
						name = Double.parseDouble(Utils.getVersionName(context).trim());
						UpdateCode.context=context;
						mHandler.sendEmptyMessage(POPOU_DIALOG);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * 按钮 只有确定键 强制升级
	 * @param context
	 */
	private void dialogForceUpdate(final Context context){
		try {
			CustomDialogUpdate.Builder builder = new CustomDialogUpdate.Builder(
					context);
			builder.setMessage(Html.fromHtml("<b>" + Utils.updateinfofp
					+ "</b>\n" + Utils.updateinfofp1 + "\n")
					+ "");
			builder.setTitle(R.string.the_new_version_is_detected);
			builder.setPositiveButton(R.string.determine,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 设置你的操作事项
							showDownloadDialog(context,true);
						}
					});
			builder.create().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 普通升级 
	 * @param context
	 */
	private void dialogOrdinaryUpdate(final Context context){
		try {
			CustomDialogUpdatePt.Builder builder = new CustomDialogUpdatePt.Builder(
					context);
			builder.setMessage(
					Utils.getResouceStr(context, R.string.new_features),
					Utils.updateinfoub + "\n" + Utils.updateinfofp1 + "\n");
			builder.setTitle(R.string.the_new_version_is_detected);
			builder.setPositiveButton(R.string.update_now,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 设置你的操作事项

							showDownloadDialog(context,false);
						}
					});
			builder.setNegativeButton(R.string.next_time,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
		
	
	public void dialogUpdatePopUp(final Context context){
		try {
			if(ISABOUTUS){
				ISABOUTUS=false;
				if (name < Utils.lowest) {
					
					// 按钮 只有确定键 强制升级
					dialogForceUpdate(context);
					
				} else if (name >= Utils.lowest && name < Utils.latest) {
					//普通升级 
					dialogOrdinaryUpdate(context);
					
				} else {
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
