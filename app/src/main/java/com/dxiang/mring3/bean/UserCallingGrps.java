package com.dxiang.mring3.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserCallingGrps implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5803053725046001678L;
	private String callingGrpID = "";
	private String callingName = "";
	public List<UserCallingGrpsMemberInfo> callingmemberlist = new ArrayList<UserCallingGrpsMemberInfo>();

	public String getCallingGrpID() {
		return callingGrpID;
	}

	public void setCallingGrpID(String callingGrpID) {
		this.callingGrpID = callingGrpID;
	}

	public String getCallingName() {
		return callingName;
	}

	public void setCallingName(String callingName) {
		this.callingName = callingName;
	}

	public int getCallingGroupMembersize() {
		return callingmemberlist.size();
	}

	public UserCallingGrpsMemberInfo getUserCallingGrpsMemberInfo(int index) {
		if (index >= 0 && index < callingmemberlist.size()) {
			return callingmemberlist.get(index);
		}
		return null;
	}
}
