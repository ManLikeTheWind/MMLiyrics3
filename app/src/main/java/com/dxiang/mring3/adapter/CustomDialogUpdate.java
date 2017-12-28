package com.dxiang.mring3.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.app.BaseApp;

/**
 * 	强制升级显示弹出框
 * @author humingzhuo
 *
 */
public class CustomDialogUpdate extends Dialog {

	public CustomDialogUpdate(Context context) {
		super(context);
	}

	public CustomDialogUpdate(Context context, int theme) {
		super(context, theme);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public static class Builder {
		private Context context;
		private String title;
		private String message;
		@SuppressWarnings("unused")
		private String update_text;
		private String positiveButtonText;
		private View contentView;
		private OnClickListener positiveButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		
		

		@SuppressWarnings("deprecation")
		public CustomDialogUpdate create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialogUpdate dialog = new CustomDialogUpdate(context,R.style.dialog_bgcolor);
			View layout = inflater.inflate(R.layout.dialog_normal_layout_update, null);
			WindowManager wm = (WindowManager) BaseApp.getInstance()
					.getSystemService(Context.WINDOW_SERVICE);
			int height = wm.getDefaultDisplay().getHeight();
			int weight = wm.getDefaultDisplay().getWidth();
			LayoutParams params = new LayoutParams(weight * 4 / 5,
					height * 4 / 10);
			dialog.addContentView(layout,params);
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			
			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
				
			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT));
			}
			dialog.setContentView(layout,params);
			dialog.setCancelable(false);
			return dialog;
		}

	}
}
