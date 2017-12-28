package com.dxiang.mring3.ringplayer;

public class AutoPlayLock {

	public AutoPlayLock() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 空闲状态
	 */
	public static final int IDLE_STATE = 0;
	/**
	 * 播放状态
	 */
	public static final int PLAYING_STATE = 1;
	/**
	 * 暂停状态
	 */
	public static final int PLAYING_PAUSED_STATE = 2;
	/**
	 * 停止状态
	 */
	public static final int PLAYING_STOP_STATE = 3;
	/**
	 * 准备播放状态
	 */
	public static final int PREPARE_STOP_STATE = 3;

	private int state = IDLE_STATE;

	private String playUrl = "";

	public String getPlayUrl() {
		return playUrl;
	}

	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
