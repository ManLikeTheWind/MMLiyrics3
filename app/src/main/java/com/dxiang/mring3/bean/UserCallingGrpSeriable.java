package com.dxiang.mring3.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserCallingGrpSeriable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4824259233272553197L;
	private static UserCallingGrpSeriable instance;
	public List<UserCallingGrps> grouplist = new ArrayList<UserCallingGrps>();

	public static UserCallingGrpSeriable getInstance() {
		synchronized (UserCallingGrpSeriable.class) {
			if (instance == null) {
				instance = new UserCallingGrpSeriable();
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

	public UserCallingGrps getUserCallingGrpsInfo(int index) {
		if (index >= 0 && index < grouplist.size()) {
			return grouplist.get(index);
		}
		return null;
	}
}
