package com.dxiang.mring3.view.gallery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.dxiang.mring3.R;

public class MyHorizontalScrollView extends HorizontalScrollView implements OnClickListener {

	/**
	 * 图片滚动时的回调接口
	 * 
	 * @author zhy
	 * 
	 */
	public interface CurrentImageChangeListener {
		void onCurrentImgChanged(int position, View viewIndicator);
	}

	/**
	 * 条目点击时的回调
	 * 
	 * @author zhy
	 * 
	 */
	public interface OnItemClickListener {
		void onClick(View view, int pos);
	}

	private CurrentImageChangeListener mListener;

	private OnItemClickListener mOnClickListener;

	private static final String TAG = "MyHorizontalScrollView";

	/**
	 * HorizontalListView中的LinearLayout
	 */
	private LinearLayout mContainer;

	/**
	 * 子元素的宽度
	 */
	private int mChildWidth;
	/**
	 * 子元素的高度
	 */
	private int mChildHeight;

	public List<Arce> mAllViewArces;

	/**
	 * 当前最后一张图片的index
	 */
	private int mCurrentIndex;
	/**
	 * 当前第一张图片的下标
	 */
	private int mFristIndex;
	/**
	 * 当前第一个View
	 */
	private View mFirstView;
	/**
	 * 数据适配器
	 */
	private HorizontalScrollViewAdapter mAdapter;
	/**
	 * 每屏幕最多显示的个数
	 */
	private int mCountOneScreen;
	/**
	 * 屏幕的宽度
	 */
	private int mScreenWitdh;

	/**
	 * 保存View与位置的键值对
	 */
	private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获得屏幕宽度
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWitdh = outMetrics.widthPixels;
		mAllViewArces = new ArrayList<Arce>();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer = (LinearLayout) getChildAt(0);
	}

	/**
	 * 滑动时的回调
	 */
	public void notifyCurrentImgChanged(int pos) {
		// 先清除所有的背景色，点击时会设置为蓝色
		for (int i = 0; i < mContainer.getChildCount(); i++) {
			mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
		}
		mListener.onCurrentImgChanged(mFristIndex, mContainer.getChildAt(pos));

	}

	/**
	 * 初始化数据，设置数据适配器
	 * 
	 * @param mAdapter
	 */
	public void initDatas(HorizontalScrollViewAdapter mAdapter,int pos) {
		this.mAdapter = mAdapter;
		mContainer = (LinearLayout) getChildAt(0);

		// 获得适配器中第一个View
		// final View view = mAdapter.getView(0, null, mContainer);
		int count = mAdapter.getCount();
		int length = 0;
		int i = 0;
		int k = 0;
		for (; i < count; i++) {
			final View v = mAdapter.getView(i, null, mContainer);
			int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			v.measure(w, h);

			int he = v.getMeasuredHeight();
			int wi = v.getMeasuredWidth();
		
			length = length + wi;
			if (length > mScreenWitdh && k == 0) {
				k = i;
			}
			Arce arce = new Arce();
			arce.height = he;
			arce.width = wi;
			mAllViewArces.add(arce);
		}

		// 强制计算当前View的宽和高
		// if (mChildWidth == 0 && mChildHeight == 0)
		// {
		// int w = View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED);
		// int h = View.MeasureSpec.makeMeasureSpec(0,
		// View.MeasureSpec.UNSPECIFIED);
		// view.measure(w, h);
		// mChildHeight = view.getMeasuredHeight();
		// mChildWidth = view.getMeasuredWidth();
		// Log.e(TAG, view.getMeasuredWidth() + "," + view.getMeasuredHeight());
		// mChildHeight = view.getMeasuredHeight();
		// // 计算每次加载多少个View
		// mCountOneScreen = mScreenWitdh / mChildWidth+2;
		// if(mCountOneScreen > mAdapter.getCount())
		// {
		// mCountOneScreen = mAdapter.getCount();
		// mChildWidth = mScreenWitdh / mCountOneScreen;
		// }
		// }
		if (k != 0) {
			mCountOneScreen = k;
		} else {
			mCountOneScreen = mAdapter.getCount();
		}

		if (length < mScreenWitdh) {
			for (int j = 0; j < count; j++) {
				Arce arce = mAllViewArces.get(j);
				arce.width = mScreenWitdh / count;
			}
		}
          
		// 初始化第一屏幕的元素
		initFirstScreenChildren(mCountOneScreen,pos);
	}

	class Arce {
		public int height;

		public int width;
	}

	/**
	 * 加载第一屏的View
	 * 
	 * @param mCountOneScreen
	 */
	public void initFirstScreenChildren(final int mCountOneScreen,final int pos) {
		mContainer = (LinearLayout) getChildAt(0);
		mContainer.removeAllViews();
		mViewPos.clear();
		for (int i = 0; i < mAdapter.getCount(); i++) {
			View view = mAdapter.getView(i, null, mContainer);
			Arce arce = mAllViewArces.get(i);
			view.setLayoutParams(new LayoutParams(arce.width, arce.height));
			view.setOnClickListener(this);
			view.setBackgroundColor(Color.WHITE);
			mContainer.addView(view);
			mViewPos.put(view, i);
			mCurrentIndex = i;
		}
             new Handler().post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
	                   if(pos>mCountOneScreen){
	                	   int count=mCountOneScreen;
	   					   int with=mScreenWitdh/count;
	   	                   smoothScrollTo(with*pos, 0);   
	                   }
				}
			});
		if (mListener != null) {
			notifyCurrentImgChanged(pos);
		}

	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// switch (ev.getAction())
		// {
		// case MotionEvent.ACTION_MOVE:
		//// Log.e(TAG, getScrollX() + "");
		//
		// int scrollX = getScrollX();
		// // 如果当前scrollX为view的宽度，加载下一张，移除第一张
		// if (scrollX >= mChildWidth)
		// {
		// loadNextImg();
		// }
		// // 如果当前scrollX = 0， 往前设置一张，移除最后一张
		// if (scrollX == 0)
		// {
		// loadPreImg();
		// }
		// break;
		// }
		return super.onTouchEvent(ev);
	}

	@Override
	public void onClick(View v) {
		if (mOnClickListener != null) {
			for (int i = 0; i < mContainer.getChildCount(); i++) {
				mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
				TextView view = (TextView) (((LinearLayout) mContainer.getChildAt(i)).getChildAt(0));
				view.setTextColor(getResources().getColor(R.color.list_textview_selector));
			}
			mOnClickListener.onClick(v, mViewPos.get(v));
		}
	}
//public void setSelectItem(int pos){
//	mContainer.getChildAt(pos).setBackgroundColor(Color.WHITE);
//	TextView view = (TextView) (((LinearLayout) mContainer.getChildAt(pos)).getChildAt(0));
//	view.setTextColor(getResources().getColor(R.color.list_textview_selector));
//}
	public void setOnItemClickListener(OnItemClickListener mOnClickListener) {
		this.mOnClickListener = mOnClickListener;
	}

	public void setCurrentImageChangeListener(CurrentImageChangeListener mListener) {
		this.mListener = mListener;
	}

}
