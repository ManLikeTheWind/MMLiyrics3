package com.dxiang.mring3.bean;

import java.text.ParseException;

import com.dxiang.mring3.utils.TimeUtil;

public class RecordInfo {
	private String chanel;
	private String logType;
	private String toneID;
	private String userNumber;
	private String operTime;
	ToneInfo mToneInfo;

	public boolean havetoneInfo() {
		boolean have = false;
		if (mToneInfo != null) {
			have = true;
		} else {
			have = false;
		}
		return have;
	}

	public ToneInfo getmToneInfo() {
		return mToneInfo;
	}

	public void setmToneInfo(ToneInfo mToneInfo) {
		this.mToneInfo = mToneInfo;
	}

	/*
	 * 1：web 2：IVR 3：APP 7：SMS 9：Copy
	 */
	public String getChanel() {
		String can = "";
		if (chanel != null) {
			int canel = Integer.valueOf(chanel);
			if (1 == canel) {
				can = "web";
			} else if (canel == 2) {
				can = "IVR";
			} else if (canel == 3) {
				can = "APP";
			} else if (canel == 7) {
				can = "SMS";
			} else if (canel == 9) {
				can = "Copy";
			}
		}
		return can;
	}

	public void setChanel(String chanel) {
		this.chanel = chanel;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getToneID() {
		return toneID;
	}

	public void setToneID(String toneID) {
		this.toneID = toneID;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getOperTime() {
		String backtime = "";
		if (operTime != null && !operTime.equals("")) {
			try {
				backtime = TimeUtil.bracelettime(operTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return backtime;
	}

	public void setOperTime(String operTime) {
		this.operTime = operTime;
	}

}
