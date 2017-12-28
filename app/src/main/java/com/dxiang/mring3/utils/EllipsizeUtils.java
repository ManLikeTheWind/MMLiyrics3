package com.dxiang.mring3.utils;

import android.R.integer;
import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.widget.TextView;

//(textview_content); 
public class EllipsizeUtils {

	private static int getAvailableWidth(TextView folder_library) {
		return folder_library.getWidth() - folder_library.getPaddingLeft()
				- folder_library.getPaddingRight();// 控件宽度-左边内间距-右边内间距==TextView的内容间距
	}

	public static boolean isOverFlowed(TextView folder_library) {
		TextPaint paint = folder_library.getPaint();
		float widthPaint = paint.measureText(folder_library.getText().toString().trim());// 获取字符串的长度(距离，不是字符个数)
//			Log.e("widthPaint  WidthTextView", 
//					"widthPaint = "+widthPaint+
//					" WidthTextView = "+getAvailableWidth(folder_library));
		if (widthPaint >= getAvailableWidth(folder_library))

			return true;
		//还有一种情况会出现，就是   数字符串很长，还是会出现一个点，下面的方法 已经解决了
		return false;
	}
	/**
	 * 使用方法：
	 * 	1.把 xml 的 Ellpsize 去掉，Singleline=true 
	 * 	3.传入一个有内容的 TextView，若字符串过长，设置成指定 省略符号的字符串
	 * 
	 * @param textContent
	 */
//	public  static void  textEllpsize(TextView textContent) {
//		if (TextUtils.isEmpty(textContent.getText())) {
//			return;
//		}
//		
//		
//		// 获得 首尾没有空格的字符串 的长度
//		String strEllpsize = textContent.getText().toString().trim();
//		Log.e("啊啊111111textEllpsize=",  strEllpsize);
//		if (isOverFlowed(textContent)) {
//			int indexLocation=getAvailableWidth(textContent);
//			System.out.println("indexlocation:" + indexLocation);
//			TextPaint paint = textContent.getPaint();
//			System.out.println("paint:"+paint);
//			String ellipsizeStr = TextUtils.ellipsize(strEllpsize,paint, indexLocation,TextUtils.TruncateAt.END).toString();//
//			Log.e("啊啊222222textEllpsize=",  ellipsizeStr+"*********");
////			String ellipsizeStr2 = ellipsizeStr.substring(0, ellipsizeStr.length()- 3) + "***";// 如何获取开始 省略位置的脚标		
//			
//			// 此时将TextView 设置成指定省略 符号的 字符串
//			textContent.setText(ellipsizeStr);
//			Log.e("nowEllpsizeContent", "nowEllpsizeContent = "+ellipsizeStr);
//		}
//	}
	
	public static String  textEllpsize(TextView textContent,String textStr,Context context,int dp) {
//		if (textContent!=null) {
////			textStr="";
//			return "";
//		}
		int defaultDP=200;
		if (dp>0) {
			defaultDP =dp;
		}
		 float scale = context.getResources().getDisplayMetrics().density;
		 	
//		 	int widthPx=200*2;
		 float widthPx=defaultDP*scale + 0.5f;
		 Log.e("*widthPx*********", "widthPx = "+widthPx+"textStr = "+textStr+"scale = "+scale);//800.5 400 
		 
		TextPaint paint = textContent.getPaint();
		textStr=textStr.trim();
		float strwidth = paint.measureText(textStr);
		 Log.e("*strwidth***widthPx******", strwidth+"**********"+widthPx);
		if (strwidth > widthPx){
			// 根据长度截取出剪裁后的文字    *0.8
			String ellipsizeStr = (String) TextUtils.ellipsize(textStr, paint, widthPx*0.9f,TextUtils.TruncateAt.END);
//			Log.e("*ellipsizeStr******","=="+ellipsizeStr);
			ellipsizeStr=ellipsizeStr.substring(0, ellipsizeStr.length()-3)+"...";
			Log.e("*ellipsizeStr******","=="+ellipsizeStr);
			return ellipsizeStr;
		
		}
		return textStr;
	}

}
