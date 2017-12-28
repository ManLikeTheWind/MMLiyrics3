package com.dxiang.mring3.view.refresh;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

import com.dxiang.mring3.R;

public class LoadingLayout extends FrameLayout {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	private final Button headerImage;
//	private final ProgressBar headerProgress;
//	// private final TextView headerText;
//	private final TextView refreshTimeTxt;
//
	private Animation rotateAnimation, resetRotateAnimation;

//	private TextView headerText;

	public LoadingLayout(Context context, final int mode, String releaseLabel,
			String pullLabel, String refreshingLabel,
			boolean isShowRefreshTimeTextView) {
		super(context);
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.refreash_header_layout, this);
//		headerText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
		headerImage = (Button) header
				.findViewById(R.id.pull_to_refresh_image);
//		headerProgress = (ProgressBar) header
//				.findViewById(R.id.pull_to_refresh_progress);
//		refreshTimeTxt = (TextView) header.findViewById(R.id.refresh_time_text);
//
//		if (isShowRefreshTimeTextView) {
//			refreshTimeTxt.setVisibility(View.VISIBLE);
//		} else {
//			refreshTimeTxt.setVisibility(View.GONE);
//		}

		
		rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(1200);
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		rotateAnimation.setRepeatMode(Animation.RESTART);
		
//		rotateAnimation = new RotateAnimation(0, +360, Animation.RELATIVE_TO_SELF,
//				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		// 动画开始到结束的执行时间(1000 = 1 秒)
//		rotateAnimation.setDuration(2000);
//		// 动画重复次数(-1 表示一直重复)
//		rotateAnimation.setRepeatCount(-1);
//		rotateAnimation.setRepeatMode(Animation.INFINITE);
		rotateAnimation.setInterpolator(new LinearInterpolator());//匀速动画
//
//		resetRotateAnimation = new RotateAnimation(-180, 0,
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//				0.5f);
//		resetRotateAnimation.setInterpolator(interpolator);
//		resetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
//		resetRotateAnimation.setFillAfter(true);
//		
		headerImage.setAnimation(rotateAnimation);

		switch (mode) {
		case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH:
			headerImage.setBackgroundResource(R.mipmap.refreshicon);
			break;
		case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH:
		default:
			headerImage.setBackgroundResource(R.mipmap.refreshicon);
			break;
		}
	}

	public void setRefreshTime(String refresh_time) {
//		refreshTimeTxt.setText("Refresh time: "
//				+ getRefrshTime(Long.parseLong(refresh_time)));
	}

	public void reset() {
//		headerText.setText(pullLabel);
//		headerImage.setVisibility(View.VISIBLE);
//		headerProgress.setVisibility(View.GONE);
	}

	public void releaseToRefresh() {
//		headerText.setText(releaseLabel);
//		headerImage.clearAnimation();
//		headerImage.startAnimation(rotateAnimation);
	}

	public void setPullLabel(String pullLabel) {
//		this.pullLabel = pullLabel;
	}

	public void refreshing() {
//		headerText.setText(refreshingLabel);
//		headerImage.startAnimation(rotateAnimation);
//		headerProgress.setVisibility(View.VISIBLE);
	}

	public void setRefreshingLabel(String refreshingLabel) {
//		this.refreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {
//		this.releaseLabel = releaseLabel;
	}

	public void pullToRefresh() {
//		headerText.setText(pullLabel);
		headerImage.startAnimation(rotateAnimation);
	}

	public void setTextColor(int color) {
//		refreshTimeTxt.setTextColor(color);
	}

	private String getRefrshTime(long time) {
//		String tag = "";
//		if (time == 0) {
//			tag = "never";
//			return tag;
//		}
//		long cur_time = System.currentTimeMillis();
//		long del_time = cur_time - time;
//		long sec = del_time / 1000;
//		long min = sec / 60;
//		if (min < 1440) {
//			tag = "Today ";
//			tag += dateformate2(time);
//		} else if (1440 < min && min < 2880) {
//			tag = "Yesterday ";
//			tag += dateformate2(time);
//
//		} else {
//			tag = dateformate1(time);
//		}
		return "";
//		return tag;
	}

	private String dateformate1(long time) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		return sdf.format(date);
	}

	private String dateformate2(long time) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(date);
	}
}
