package com.dxiang.mring3.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecordTypeInfoSeriabel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4441863274572922746L;
	public List<RecordInfo> mRecordInfo = new ArrayList<RecordInfo>();
	private static RecordTypeInfoSeriabel instance;

	public static RecordTypeInfoSeriabel getInstance() {
		synchronized (RecordTypeInfoSeriabel.class) {
			if (instance == null) {
				instance = new RecordTypeInfoSeriabel();
			}
			return instance;
		}
	}

	public int getRecordInfosize() {
		return mRecordInfo.size();
	}

	public RecordInfo getRecordInfo(int index) {
		if (index >= 0 && index < mRecordInfo.size()) {
			return mRecordInfo.get(index);
		}
		return null;
	}

}
