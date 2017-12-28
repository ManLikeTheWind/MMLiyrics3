package com.dxiang.mring3.bean;

public class UserToneSettingInfo {
	private String settingID = "";
	private String settingObjType = "";
	private String calling = "";
	private String timeType = "";
	private String startTime = "";
	private String endTime = "";
	private String playStartTime = "";
	private String playEndTime = "";
	private String toneID = "";
	private String toneType = "";
	private String timedescrip = "";

	public String getToneType() {
		return toneType;
	}

	public void setToneType(String toneType) {
		this.toneType = toneType;
	}

	public String getSettingID() {
		return settingID;
	}

	public void setSettingID(String settingID) {
		this.settingID = settingID;
	}

	public String getSettingObjType() {
		return settingObjType;
	}

	public void setSettingObjType(String settingObjType) {
		this.settingObjType = settingObjType;
	}

	public String getCalling() {
		return calling;
	}

	public void setCalling(String calling) {
		this.calling = calling;
	}

	public String getTimeType() {
		return timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPlayStartTime() {
		return playStartTime;
	}

	public void setPlayStartTime(String playStartTime) {
		this.playStartTime = playStartTime;
	}

	public String getPlayEndTime() {
		return playEndTime;
	}

	public void setPlayEndTime(String playEndTime) {
		this.playEndTime = playEndTime;
	}

	public String getToneID() {
		return toneID;
	}

	public void setToneID(String toneID) {
		this.toneID = toneID;
	}

	public String getTimedescrip() {
		return timedescrip;
	}

	public void setTimedescrip(String timedescrip) {
		this.timedescrip = timedescrip;
	}

	public UserToneSettingInfo(String settingID, String settingObjType,
			String calling, String timeType, String startTime, String endTime,
			String playStartTime, String playEndTime, String toneID,
			String toneType, String timedescrip) {
		super();
		this.settingID = settingID;
		this.settingObjType = settingObjType;
		this.calling = calling;
		this.timeType = timeType;
		this.startTime = startTime;
		this.endTime = endTime;
		this.playStartTime = playStartTime;
		this.playEndTime = playEndTime;
		this.toneID = toneID;
		this.toneType = toneType;
		this.timedescrip = timedescrip;
	}

	public UserToneSettingInfo() {
		super();
	}

}
