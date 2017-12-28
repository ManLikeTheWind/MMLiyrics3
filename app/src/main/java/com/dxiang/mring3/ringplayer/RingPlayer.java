/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dxiang.mring3.ringplayer;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;

@SuppressLint("NewApi")
public class RingPlayer implements OnCompletionListener, OnErrorListener,
		OnBufferingUpdateListener, OnPreparedListener {

	private static final String SAMPLE_PATH_KEY = "sample_path";
	//
	// private static final String SAMPLE_LENGTH_KEY = "sample_length";

	// public static final String SAMPLE_DEFAULT_DIR = "/sound_recorder";

	public static final int IDLE_STATE = 0;

	public static final int PLAYING_STATE = 1;

	public static final int PLAYING_PAUSED_STATE = 2;

	public static final int PLAYING_STOP_STATE = 3;

	private int mState = IDLE_STATE;

	public static final int NO_ERROR = 0;

	public static final int STORAGE_ACCESS_ERROR = 1;

	public static final int INTERNAL_ERROR = 2;

	public static final int IN_CALL_RECORD_ERROR = 3;

	public static String strTitleName = "";// 获取标题名全局变量lc

	public interface OnStateChangedListener {
		public void onStateChanged(int state);

		/**
		 * 开始播放事件
		 * 
		 * @param url
		 *            播放地址
		 */
		public void onStartOnlinePlay(String url);

		/**
		 * 暂停事件，
		 * 
		 * @param url
		 *            播放地址
		 * @param percentage
		 *            进度
		 */
		public void onPauseEvent(String url, float percentage);

		public void onOnlinePlayStop();

		public void onError(int error);
	}

	private OnStateChangedListener mOnStateChangedListener = null;

	private OnStateChangedListener mGolbalOnStateChangedListener = null;

	private OnStateChangedListener mFloatWindowChangedListener = null;

	private long mSampleStart = 0; // time at which latest record or play
									// operation started

	// private int mSampleLength = 0; // length of current sample

	private File mSampleFile = null;

	private MediaPlayer mPlayer = null;

	public static final int PLAY_MODE_MANUAL = 0;

	public static final int PLAY_MODE_AUTO = 1;

	public static final int PLAY_MODE_OTHER = 2;

	private int playMode = PLAY_MODE_MANUAL;

	private static RingPlayer shareRingPlayer = null;

	public String currentPlayPath = "";

	public String[] strFilePath;// 非景点音频播放地址集合

	public static RingPlayer getShareRingPlayer() {
		synchronized (RingPlayer.class) {
			if (shareRingPlayer == null) {
				shareRingPlayer = new RingPlayer();
			}
		}
		return shareRingPlayer;
	}

	public RingPlayer() {
	}

	public boolean syncStateWithService() {
		if (mSampleFile != null) {
			return false;
		}
		return true;
	}

	public void saveState(Bundle recorderState) {
		recorderState.putString(SAMPLE_PATH_KEY, mSampleFile.getAbsolutePath());
		// recorderState.putInt(SAMPLE_LENGTH_KEY, mSampleLength);
	}

	public void restoreState(Bundle recorderState) {
		String samplePath = recorderState.getString(SAMPLE_PATH_KEY);
		if (samplePath == null)
			return;
		// int sampleLength = recorderState.getInt(SAMPLE_LENGTH_KEY, -1);
		// if (sampleLength == -1)
		// return;

		File file = new File(samplePath);
		if (!file.exists())
			return;
		if (mSampleFile != null
				&& mSampleFile.getAbsolutePath().compareTo(
						file.getAbsolutePath()) == 0)
			return;

		mSampleFile = file;
		// mSampleLength = sampleLength;

		signalStateChanged(IDLE_STATE);
	}

	public void setOnStateChangedListener(OnStateChangedListener listener) {
		mOnStateChangedListener = listener;
	}

	public void setOnGolbalStateChangedListener(OnStateChangedListener listener) {
		mGolbalOnStateChangedListener = listener;
	}

	public void setOnFloatWindowStateChangedListener(
			OnStateChangedListener listener) {
		mFloatWindowChangedListener = listener;
	}

	public synchronized int state() {
		if (this.mPlayer == null)
			return RingPlayer.IDLE_STATE;
		if (mPlayer.isPlaying())
			return RingPlayer.PLAYING_STATE;
		else
			return mState;

	}

	public int progress() {
		if (mState == PLAYING_STATE || mState == PLAYING_PAUSED_STATE) {
			if (mPlayer != null) {
				return (mPlayer.getCurrentPosition() / 1000);
			}
		}
		return 0;
	}

	public String progressformation() {
		if (mState == PLAYING_STATE || mState == PLAYING_PAUSED_STATE) {
			if (mPlayer != null) {
				return (getCurPlayerDurationFormat(mPlayer.getCurrentPosition() / 1000));
			}
		}
		return "00:00";
	}

	public int Duration() {
		if (mPlayer != null)
			return mPlayer.getDuration() / 1000;
		return 0;
	}

	public String getPlayProgress() {
		if (mState == PLAYING_STATE || mState == PLAYING_PAUSED_STATE) {
			if (mPlayer != null) {
				return getCurPlayerDurationFormat(mPlayer.getCurrentPosition() / 1000)
						+ "/"
						+ getCurPlayerDurationFormat(mPlayer.getDuration() / 1000);
			}
		}
		return "0:00";
	}

	public String getCurPlayerDurationFormat(int iDuration) {
		try {

			int m = iDuration / 60;
			String mm = "";
			if (m > 9)
				mm = m + ":";
			else
				mm = "0" + m + ":";
			int s = iDuration % 60;
			String ss = "";
			if (s > 9)
				ss = s + "";
			else
				ss = "0" + s;
			return mm + ss;
			// return OtherUtil.FormatDuration(iDuration/1000);
		} catch (IllegalStateException e) {

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return "";
	}

	public float playProgress() {
		if (mPlayer != null) {
			return ((float) mPlayer.getCurrentPosition())
					/ mPlayer.getDuration();
		}
		return 0.0f;
	}

	// public int sampleLength() {
	// return mSampleLength;
	// }

	public File sampleFile() {
		return mSampleFile;
	}

	public boolean getSampleStatus() {
		if (mSampleFile == null)
			return false;
		return mSampleFile.exists();
	}

	public void clear() {
		stop();
		signalStateChanged(IDLE_STATE);
	}

	public void reset() {
		stop();
		mSampleFile = null;
		mState = IDLE_STATE;
		signalStateChanged(IDLE_STATE);
	}

	/**
	 * 设置播放文件
	 * 
	 * @param filePath
	 */
	public synchronized void setPlayFile(String filePath) {
		currentPlayPath = filePath;
		mSampleFile = new File(filePath);
	}

	public synchronized void startPlayback(String url, String name,
			float percentage) {
		strTitleName = name;
		Log.e("test", "startPlayback percentage = " + percentage + " url "
				+ url);
		if (state() == PLAYING_PAUSED_STATE
				&& currentPlayPath.equalsIgnoreCase(url)) {
			if (mPlayer != null) {
				mSampleStart = System.currentTimeMillis()
						- mPlayer.getCurrentPosition();
				mPlayer.seekTo((int) (percentage * mPlayer.getDuration()));
				mPlayer.start();
				setState(PLAYING_STATE);
			}
		} else {
			setPlayFile(url);
			stop();
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(url);
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mPlayer.setOnCompletionListener(this);
				mPlayer.setOnErrorListener(this);
				mPlayer.setOnBufferingUpdateListener(this);
				mPlayer.setOnPreparedListener(this);
				mPlayer.prepare();
				mPlayer.seekTo((int) (percentage * mPlayer.getDuration()));
				mPlayer.start();

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				setError(INTERNAL_ERROR);
				mPlayer = null;
				return;
			} catch (IOException e) {
				e.printStackTrace();
				setError(STORAGE_ACCESS_ERROR);
				mPlayer = null;
				return;
			}
			mSampleStart = System.currentTimeMillis();
			setState(PLAYING_STATE);
			signalStateChanged(PLAYING_STATE);
		}
	}

	public synchronized void startPlayback(float percentage) {
		setPlayMode(PLAY_MODE_AUTO);
		// App.showLog("mSampleFile = "+mSampleFile.getAbsolutePath());
		Log.e("test", "startPlayback percentage = " + percentage);
		if (state() == PLAYING_PAUSED_STATE) {
			if (mPlayer != null) {
				mSampleStart = System.currentTimeMillis()
						- mPlayer.getCurrentPosition();
				mPlayer.seekTo((int) (percentage * mPlayer.getDuration()));
				mPlayer.start();
				setState(PLAYING_STATE);
			}
		} else {
			stop();
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(currentPlayPath);
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mPlayer.setOnCompletionListener(this);
				mPlayer.setOnErrorListener(this);
				mPlayer.setOnBufferingUpdateListener(this);
				mPlayer.setOnPreparedListener(this);
				mPlayer.prepare();
				// Log.e("test",
				// "seekTo = " + percentage + "  " + mPlayer.getDuration());
				mPlayer.seekTo((int) (percentage * mPlayer.getDuration()));

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				setError(INTERNAL_ERROR);
				mPlayer = null;
				return;
			} catch (IOException e) {
				e.printStackTrace();
				setError(STORAGE_ACCESS_ERROR);
				mPlayer = null;
				return;
			}
			mSampleStart = System.currentTimeMillis();
			setState(PLAYING_STATE);
			signalStateChanged(PLAYING_STATE);
		}
	}

	/**
	 * 开始播放网络歌曲(在线试听)
	 */
	public synchronized void onlinePaly(String url, String name, int mode) {
		strTitleName = name;
		setPlayMode(mode);
		stop();
		// if(!url.equalsIgnoreCase(currentPlayPath))
		currentPlayPath = url;
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(url);
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setOnCompletionListener(this);
			mPlayer.setOnErrorListener(this);
			mPlayer.setOnBufferingUpdateListener(this);
			mPlayer.setOnPreparedListener(this);
			mPlayer.prepareAsync();
			// mPlayer.seekTo((int) (percentage * mPlayer.getDuration()));
			// mPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			setError(INTERNAL_ERROR);
			// System.out.println("INTERNAL_ERROR:" + INTERNAL_ERROR);
			mPlayer = null;
		} catch (Exception e) {
			e.printStackTrace();
			setError(STORAGE_ACCESS_ERROR);
			// System.out.println("STORAGE_ACCESS_ERROR:" +
			// STORAGE_ACCESS_ERROR);
			mPlayer = null;
		}
	}

	public synchronized void pausePlayback() {
		if (mPlayer == null) {
			setState(IDLE_STATE);
			return;
		}
		mPlayer.pause();
		setState(PLAYING_PAUSED_STATE);
		onPauseEvent();
	}

	public synchronized void stopPlayback() {
		if (mPlayer == null) {
			setState(IDLE_STATE);
			return;
		}
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
		setState(PLAYING_STOP_STATE);
		onStopEvent();
	}

	// public void stopOnlinePlayback() {
	// if (mPlayer == null){
	// setState(IDLE_STATE);
	// return;
	// }
	//
	// mPlayer.stop();
	// mPlayer.release();
	// mPlayer = null;
	// setState(PLAYING_STOP_STATE);
	// }

	public void stop() {
		stopPlayback();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		stop();
		// App.showLog("onError...");
		setError(STORAGE_ACCESS_ERROR);
		return true;
	}

	public synchronized void onStartPlayEvent() {
		if (mOnStateChangedListener != null)
			mOnStateChangedListener.onStartOnlinePlay(currentPlayPath);
		if (mGolbalOnStateChangedListener != null)
			mGolbalOnStateChangedListener.onStartOnlinePlay(currentPlayPath);
		if (mFloatWindowChangedListener != null)
			mFloatWindowChangedListener.onStartOnlinePlay(currentPlayPath);
	}

	public synchronized void onPauseEvent() {
		if (mOnStateChangedListener != null)
			mOnStateChangedListener.onPauseEvent(currentPlayPath,
					playProgress());
		if (mGolbalOnStateChangedListener != null)
			mGolbalOnStateChangedListener.onPauseEvent(currentPlayPath,
					playProgress());
		if (mFloatWindowChangedListener != null)
			mFloatWindowChangedListener.onPauseEvent(currentPlayPath,
					playProgress());
	}

	public synchronized void onStopEvent() {

		if (mOnStateChangedListener != null)
			mOnStateChangedListener.onOnlinePlayStop();
		if (mGolbalOnStateChangedListener != null)
			mGolbalOnStateChangedListener.onOnlinePlayStop();
		if (mFloatWindowChangedListener != null)
			mFloatWindowChangedListener.onOnlinePlayStop();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		stop();
	}

	public void setState(int state) {
		if (state == mState)
			return;
		mState = state;
		signalStateChanged(mState);
	}

	private void signalStateChanged(int state) {
		if (mOnStateChangedListener != null)
			mOnStateChangedListener.onStateChanged(state);
		if (mGolbalOnStateChangedListener != null)
			mGolbalOnStateChangedListener.onStateChanged(state);
		// mFloatWindowChangedListener
		if (mFloatWindowChangedListener != null)
			mFloatWindowChangedListener.onStateChanged(state);
	}

	public void setError(int error) {
		if (mOnStateChangedListener != null)
			mOnStateChangedListener.onError(error);
		if (mGolbalOnStateChangedListener != null)
			mGolbalOnStateChangedListener.onError(error);
		if (mFloatWindowChangedListener != null)
			mFloatWindowChangedListener.onError(error);
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		if (arg0 != null) {
			Log.e("test", "**************onPrepared.....");
			if (arg0 == mPlayer) {
				arg0.start();
				// Log.e("test", "****OK...");
				mSampleStart = System.currentTimeMillis();
				setState(PLAYING_STATE);
				signalStateChanged(PLAYING_STATE);
				onStartPlayEvent();
			}
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub
		// Log.e("test", "onBufferingUpdate arg1 = " + arg1);
	}

	public int getPlayMode() {
		return playMode;
	}

	public void setPlayMode(int playMode) {
		this.playMode = playMode;
	}

}
