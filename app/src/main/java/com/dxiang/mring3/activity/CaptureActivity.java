 package com.dxiang.mring3.activity;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.dxiang.mring3.R;
import com.dxiang.mring3.activity.ShowZxingDialog.NeedSaveCallBack;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.request.QryToneById;
import com.dxiang.mring3.response.QryToneByIdRsp;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.zxing.camera.CameraManager;
import com.dxiang.mring3.zxing.decoding.CaptureActivityHandler;
import com.dxiang.mring3.zxing.decoding.InactivityTimer;
import com.dxiang.mring3.zxing.decoding.ViewfinderView;

public class CaptureActivity extends BaseActivity implements Callback {
	private RelativeLayout rl_title_layout;
	// 返回标题
	private TextView mTitle_tv;
	// 返回图片
	private ImageView mTitle_iv;
	private CaptureActivityHandler handlers;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	private RadioButton light;

	private ShowZxingDialog mZxingDialog;

	private boolean lightFlag = false;

	private String result;
	private ImageView liear_title;
//	private Data data = new Data();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture_activity);
		CameraManager.init(getApplication());
		rl_title_layout = (RelativeLayout) findViewById(R.id.rl_title_layout);// 返回字
		mTitle_iv = (ImageView) findViewById(R.id.title_iv);
		mTitle_iv.setOnClickListener(lightClick);
		mTitle_tv = (TextView) findViewById(R.id.title_tv);
		liear_title=(ImageView) findViewById(R.id.liear_title );
		mTitle_iv.setVisibility(View.VISIBLE);
		mTitle_tv.setText(Utils.getResouceStr(CaptureActivity.this,
				R.string.sweep));
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		light = (RadioButton) findViewById(R.id.open_light);
		light.setOnClickListener(lightClick);
		liear_title.setOnClickListener(lightClick);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		int y = getWindowManager().getDefaultDisplay().getHeight();
		int x = getWindowManager().getDefaultDisplay().getWidth();
		mZxingDialog = new ShowZxingDialog(this, 0, y / 3);
		mZxingDialog.setCallBack(mCall);

	}

	@Override
	protected void reqError(Message msg) {
		// TODO Auto-generated method stub
		super.reqError(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QRYCALLERTONEBYID:
			Utils.showTextToast(CaptureActivity.this,
					R.string.format_error);
			break;

		default:
			break;
		}
	}

	@Override
	protected void reqXmlSucessed(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlSucessed(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QRYTONEBYID:
			QryToneByIdRsp entry = (QryToneByIdRsp) msg.obj;
			if (entry.list.size() != 0) {
				ToneInfo grpcallerToneInfo = entry.list.get(0);
				if(null!=grpcallerToneInfo.getPrice()&&!grpcallerToneInfo.getPrice().equals("string{}")){
						//跳转到铃声详细页面
					Intent intent = new Intent(CaptureActivity.this,
						DetailsRingtoneActivity.class);
				intent.putExtra("bean", grpcallerToneInfo);
				startActivity(intent);
				finish();}
			}else {
				Utils.showTextToast(CaptureActivity.this,
						R.string.format_error);
			}
			
			break;

		default:
			break;
		}
	}

	@Override
	protected void reqXmlFail(Message msg) {
		// TODO Auto-generated method stub
		super.reqXmlFail(msg);
		switch (msg.arg1) {
		case FusionCode.REQUEST_QRYTONEBYID:
			Utils.showTextToast(CaptureActivity.this,
					R.string.format_error);
			break;

		default:
			break;
		}
	}

	private OnClickListener lightClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.title_iv:
				finish();
				break;
			case R.id.open_light:
				if (lightFlag) {
					light.setChecked(false);
					CameraManager.get().closeLight();
					lightFlag = false;
				} else {
					light.setChecked(true);
					CameraManager.get().openLight();
					lightFlag = true;
				}
				break;
			case R.id.liear_title:
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(CaptureActivity.this,MainGroupActivity.class));
				finish();
				break;
			}
		}
	};

	private boolean needGotoDetal(String result) {
		if (!Utils.CheckTextNull(result)) {
			return false;
		}

		if (result.startsWith("result")) {
			return false;
		}
//		String[] temp = result.split(",");
//		if (temp.length != 2) {
//			return false;
//		}
//		
//		if ("tonetype".equalsIgnoreCase(temp[0].split("=")[0])
//				&& "toneid".equalsIgnoreCase(temp[1].split("=")[0])) {
//			data.toneType = temp[0].split("=").length == 2 ? temp[0].split("=")[1]
//					: "";
//			data.toneId = temp[1].split("=").length == 2 ? temp[1].split("=")[1]
//					: "";
			return true;
//		} else {
//			return false;
//		}
	}

//	class Data {
//		public String toneType;
//
//		public String toneId;
//	}

	private ShowZxingDialog.NeedSaveCallBack mCall = new NeedSaveCallBack() {
		@Override
		public void saveData(boolean flag, String data) {
			if (flag) {
				if (!Utils.isNetworkAvailable(CaptureActivity.this)) {
					Utils.showTextToast(CaptureActivity.this, R.string.nowifi);
					return;
				}
				if (Utils.isEmptyString(result)) {
					if (mZxingDialog.isShowing()) {
						mZxingDialog.dismiss();
					}
					finish();
				} else {//result数据不为空执行下面的东西：也就是不需要dialog时候执行下面的东西。
//					if (needGotoDetal(result)) {
//						Utils.showTextToast(CaptureActivity.this,
//								R.string.format_error);
//						return;
//					}
					if (mZxingDialog.isShowing()) {
						mZxingDialog.dismiss();
					}
					QryToneById qtb=new QryToneById(handler, FusionCode.REQUEST_QRYTONEBYID);
					qtb.getQryToneById(result, "1", "1", 100, 0);
				}
			} else {
				//按取消后，执行这部分代码:重新扫描；
				if (handlers != null) {
					handlers.restartPreviewAndDecode();
				}
				if (data == null) {
					if (mZxingDialog.isShowing()) {
						mZxingDialog.dismiss();
					}
				}
			}

		}
	};
//	String getUrlSplit(String url) {
//		String[] ids = url.split("/", -1);
//		String te = ids[ids.length - 1];
//		String[] is = te.split("\\.", -1);
//		return is[0];
//	}
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handlers != null) {
			handlers.quitSynchronously();
			handlers = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handlers == null) {
			handlers = new CaptureActivityHandler(CaptureActivity.this, decodeFormats,characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handlers;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(final Result obj, Bitmap barcode) {
		result = obj.getText();
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		Drawable drawable = null;
		if (barcode != null) {
			drawable = new BitmapDrawable(barcode);
		}

		/* 将解析后的代码，东西放在这里*/
		
		 if (!Utils.isNetworkAvailable(CaptureActivity.this)) {
			Utils.showTextToast(CaptureActivity.this, R.string.nowifi);
			return;
		}
		if (Utils.isEmptyString(result)) {
//			if (mZxingDialog.isShowing()) {
//				mZxingDialog.dismiss();
//			}
			finish();
		} else {//result数据不为空执行下面的东西：也就是不需要dialog时候执行下面的东西。
//			if (needGotoDetal(result)) {
//				Utils.showTextToast(CaptureActivity.this,
//						R.string.format_error);
//				return;
//			}
			
//			if (mZxingDialog.isShowing()) {
//				mZxingDialog.dismiss();
//			}
			QryToneById qtb=new QryToneById(handler, FusionCode.REQUEST_QRYTONEBYID);
			qtb.getQryToneById(result, "1", "1", 100, 0);
		}
		
	//	执行这部分代码:重新扫描;延迟执行2000毫秒
				if (handlers != null) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handlers.restartPreviewAndDecode();
				}
		
		//下面是弹出dialog的，现在不需要了
		
//		mZxingDialog.setDrawable(drawable);
//		mZxingDialog.setResult(getResources().getString(
//				R.string.scanning_result)
//				+ result);
//		mZxingDialog.show();
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
}