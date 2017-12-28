package com.dxiang.mring3.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.app.BaseApp;

public class ShowZxingDialog extends Dialog {
	private ImageView logo;

	private Button confirm;

	private Button scan;

	private Window window;

	private NeedSaveCallBack mNeedSaveCallBack;

	private Context mContext;

	private Drawable mDrawable;

	private String result;

	TextView title;

	public void setCallBack(NeedSaveCallBack call) {
		this.mNeedSaveCallBack = call;
	}

	public void setDrawable(Drawable drawable) {
		mDrawable = drawable;
	}

	public void setResult(String drawable) {
		result = drawable;
	}

	public interface NeedSaveCallBack {
		public void saveData(boolean flag, String data);
	}

	public ShowZxingDialog(Context context, int x, int y) {
		super(context, R.style.dialog_bgcolor);
		mContext = context;
		windowDeploy(x, y);
	}

	public ShowZxingDialog(Context context, int style, int x, int y) {
		super(context, R.style.dialog_bgcolor);
		mContext = context;
		windowDeploy(x, y);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		LayoutInflater mInfalter = LayoutInflater.from(mContext);
		View view = mInfalter
				.inflate(R.layout.dialog_zxing_result_layout, null);
		WindowManager wm = (WindowManager) BaseApp.getInstance()
				.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		int weight = wm.getDefaultDisplay().getWidth();
		LayoutParams params = new LayoutParams(weight * 4 / 5, height * 4 / 10);
		addContentView(view, params);
		logo = (ImageView) view.findViewById(R.id.show_zxing_logo);
		confirm = (Button) view.findViewById(R.id.confirm_zxing);
		scan = (Button) view.findViewById(R.id.cancel_zxing);
		title = (TextView) view.findViewById(R.id.title);
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				if (mNeedSaveCallBack != null) {
					mNeedSaveCallBack.saveData(true, null);
//				}
			}
		});

		scan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mNeedSaveCallBack.saveData(false, null);
			}
		});

		title.setText(result);
		logo.setImageDrawable(mDrawable);
		setContentView(view, params);
	}

	// 设置窗口显示
	public void windowDeploy(int x, int y) {
		Log.d("MS", "x = " + x + "  y  " + y);
		window = getWindow(); // 得到对话框
		window.setWindowAnimations(R.style.dialogWindowAnimTitle); // 设置窗口弹出动画
		WindowManager.LayoutParams wl = window.getAttributes();
		// 根据x，y坐标设置窗口需要显示的位置
		// wl.x = x; //x小于0左移，大于0右移
		// wl.y = y; //y小于0上移，大于0下移
		// wl.alpha = 0.6f; //设置透明度
		// wl.gravity = Gravity.BOTTOM; //设置重力
		window.setAttributes(wl);
	}

	public View getView() {
		return logo;
	}

	@Override
	public void dismiss() {
		super.dismiss();

		mNeedSaveCallBack.saveData(false, "need");
	}

	@Override
	public void show() {
		super.show();
		title.setText(result);
		logo.setBackgroundResource(0);
		logo.setImageBitmap(null);
		logo.setImageDrawable(mDrawable);
	}

}
