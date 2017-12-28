package com.dxiang.mring3.camera;

public class CameraInfo {

	public CameraInfo() {

	}

	public CameraInfo(String path, int angle, long timer) {
		this.cameraPath = path;
		this.cameraAngle = angle;
		this.timer = timer;
	}

	/**
	 * 图片角度
	 */
	private int cameraAngle = 0;
	/**
	 * 图片保存路径
	 */
	private String cameraPath = "";
	/**
	 * 图片生成时间
	 */
	private long timer = 0;

	public String getCameraPath() {
		return cameraPath;
	}

	public void setCameraPath(String cameraPath) {
		this.cameraPath = cameraPath;
	}

	public int getCameraAngle() {
		return cameraAngle;
	}

	public void setCameraAngle(int cameraAngle) {
		this.cameraAngle = cameraAngle;
	}

	public long getTimer() {
		return timer;
	}

	public void setTimer(long timer) {
		this.timer = timer;
	}

}
