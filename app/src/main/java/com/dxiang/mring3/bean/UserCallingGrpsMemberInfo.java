package com.dxiang.mring3.bean;

import java.io.Serializable;

public class UserCallingGrpsMemberInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -710617145395156202L;
	private String callNumber = "";
	private String numberName = "";

	public String getCallNumber() {
		return callNumber;
	}

	public void setCallNumber(String callNumber) {
		this.callNumber = callNumber;
	}

	public String getNumberName() {
		return numberName;
	}

	public void setNumberName(String numberName) {
		this.numberName = numberName;
	}

}
