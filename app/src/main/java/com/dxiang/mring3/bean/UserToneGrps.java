package com.dxiang.mring3.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserToneGrps implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6768578210262437601L;
	private String userToneGrpID = "";
	private String toneGrpName = "";
	public List<UserToneGrpsMember> tonegrpInfos = new ArrayList<UserToneGrpsMember>();

	public String user() {
		return userToneGrpID;
	}
	public String getUserToneGrpID() {
		return userToneGrpID;
	}
	public void setUserToneGrpID(String userToneGrpID) {
		this.userToneGrpID = userToneGrpID;
	}

	public String getToneGrpName() {
		return toneGrpName;
	}

	public void setToneGrpName(String toneGrpName) {
		this.toneGrpName = toneGrpName;
	}

	public int getMembersize() {
		return tonegrpInfos.size();
	}

	public UserToneGrpsMember getUserToneGrpsMember(int index) {
		if (index >= 0 && index < tonegrpInfos.size()) {
			return tonegrpInfos.get(index);
		}
		return null;
	}

}
