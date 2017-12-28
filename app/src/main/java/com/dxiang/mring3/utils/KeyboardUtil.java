package com.dxiang.mring3.utils;

import com.dxiang.mring3.R;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class KeyboardUtil {
	private KeyboardView keyboardView;
	private Keyboard k;
	private EditText ed;
	public boolean keyBoardShow = false;

	public KeyboardUtil(Context ctx, EditText edit, KeyboardView keyboardView) {
		this.ed = edit;
		k = new Keyboard(ctx, R.layout.keyboard_symbols);
		// keyboardView = (KeyboardView) act.findViewById(R.id.keyboard_view);
		this.keyboardView = keyboardView;
		keyboardView.setKeyboard(k);

		keyboardView.setEnabled(true);
		// keyboardView.setPreviewEnabled(true);

		keyboardView.setOnKeyboardActionListener(listener);
		keyboardView.setPreviewEnabled(false);
		
	}

	public void setEd(EditText ed) {
		this.ed = ed;
	}

	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			Editable editable = ed.getText();
			int start = ed.getSelectionStart();
			if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
				hideKeyboard();

			} else if (primaryCode == Keyboard.KEYCODE_DELETE) {// ����
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			} else if (primaryCode == 4896) {
				editable.clear();
			} else {
				editable.insert(start, Character.toString((char) primaryCode));
			}
		}
	};

	public void showKeyboard() {
		int visibility = keyboardView.getVisibility();
		if (visibility == View.GONE || visibility == View.INVISIBLE || keyBoardShow == false) {
			if (keyboardView != null)
				keyboardView.setVisibility(View.VISIBLE);
			keyBoardShow = true;
		}
	}

	public void hideKeyboard() {
		int visibility = keyboardView.getVisibility();
		if (visibility == View.VISIBLE && keyBoardShow == true && keyboardView != null) {
			keyboardView.setVisibility(View.GONE);
			keyBoardShow = false;
		}
	}
}
