package com.dxiang.mring3.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class UserToneGrpsMember implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3044631237046321726L;
	private String ToneID = "";
	private String ToneName = "";
	public ArrayList<ToneInfo> tonInfoslist = new ArrayList<ToneInfo>();

	public String getToneID() {
		return ToneID;
	}

	public void setToneID(String toneID) {
		ToneID = toneID;
	}

	public String getToneName() {
		return ToneName;
	}

	public void setToneName(String toneName) {
		ToneName = toneName;
	}

}
