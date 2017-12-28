package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;

import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.utils.Utils;

public class QryToneByNameRsp extends EntryP {
	public QryToneByNameRsp() {
		returnKey = "qryToneByNameReturn";
	}

	private List<ToneInfo> mToneInfos;

	@Override
	public EntryP getEntryP(SoapObject result) {
		super.getEntryP(result);
		mToneInfos = new ArrayList<ToneInfo>();
		Vector object = (Vector) resultObject.getProperty("toneInfos");
		if (object != null && object.size() > 0) {
			int count = object.size();
			for (int i = 0; i < count; i++) {
				SoapObject temp = (SoapObject) object.get(i);
				String singerName = Utils.reSoapObjectStr(temp,
						"singerNameLetter");
				if (!singerName.equalsIgnoreCase("string{}")) {
					ToneInfo info = Utils.getToneInfos(temp);
					mToneInfos.add(info);
				}
			}
		}
		return this;
	}

	public List<ToneInfo> getmToneInfos() {
		return mToneInfos;
	}

	public void setmToneInfos(List<ToneInfo> mToneInfos) {
		this.mToneInfos = mToneInfos;
	}
}
