package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.dxiang.mring3.bean.UserToneGrpsMember;
import com.dxiang.mring3.utils.Utils;

public class QryToneGrpMemberResp extends EntryP {
	private List<UserToneGrpsMember> tonegrpInfos;

	public QryToneGrpMemberResp() {
		super();
		tonegrpInfos = new ArrayList<UserToneGrpsMember>();
		returnKey = "qryToneGrpMemReturn";
	}

	@Override
	public QryToneGrpMemberResp getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
				.getProperty("toneBasicInfos");
		if (null != vector) {
			for (int i = 0; i < vector.size(); i++) {
				SoapObject object = vector.get(i);
				String tonename = Utils.reSoapObjectStr(object, "toneName");
				if (!tonename.equalsIgnoreCase("string{}")) {
					UserToneGrpsMember info = new UserToneGrpsMember();
					info.setToneID(Utils.reSoapObjectStr(object, "toneID"));
					info.setToneName(Utils.reSoapObjectStr(object, "toneName"));
					tonegrpInfos.add(info);
				}
			}
		}
		return this;
	}

	public List<UserToneGrpsMember> getToneList() {
		return tonegrpInfos;
	}

	public void setToneList(List<UserToneGrpsMember> toneInfos) {
		this.tonegrpInfos = toneInfos;
	}

}
