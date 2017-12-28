package com.dxiang.mring3.utils;

import android.content.Context;
import android.view.WindowManager;

public class WindowArg {
	WindowManager wm;
	static WindowArg windowArg;

	static public WindowArg getInstance(Context context) {
		if (windowArg == null) {
			windowArg = new WindowArg(context);
		}
		return windowArg;
	}

	public WindowArg(Context context) {
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	}

	public int getWindowWidth() {
		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}

	public int getWindowHeigt() {
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}
}
