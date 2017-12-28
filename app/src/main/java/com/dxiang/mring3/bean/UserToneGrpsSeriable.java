package com.dxiang.mring3.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserToneGrpsSeriable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1783630226014556413L;
	private static UserToneGrpsSeriable instance;
	public List<UserToneGrps> grouplist = new ArrayList<UserToneGrps>();

	public static UserToneGrpsSeriable getInstance() {
		synchronized (UserToneGrpsSeriable.class) {
			if (instance == null) {
				instance = new UserToneGrpsSeriable();
			}
			return instance;
		}
	}

	public void cleargroup() {
		grouplist.clear();
	}

	public int getGroupsize() {
		return grouplist.size();
	}

	public UserToneGrps getUserToneGrpsInfo(int index) {
		if (index >= 0 && index < grouplist.size()) {
			return grouplist.get(index);
		}
		return null;
	}
}
