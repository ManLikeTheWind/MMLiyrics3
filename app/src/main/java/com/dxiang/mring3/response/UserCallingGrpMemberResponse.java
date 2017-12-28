package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;
import com.dxiang.mring3.bean.UserCallingGrpsMemberInfo;
import com.dxiang.mring3.utils.Utils;

public class UserCallingGrpMemberResponse extends EntryP {

	private List<UserCallingGrpsMemberInfo> tonegrpInfos;

	public UserCallingGrpMemberResponse() {
		super();
		tonegrpInfos = new ArrayList<UserCallingGrpsMemberInfo>();
		returnKey = "qryCallingGrpMemReturn";
	}

	@Override
	public UserCallingGrpMemberResponse getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
				.getProperty("callingGrpMems");
		if (null != vector) {
			for (int i = 0; i < vector.size(); i++) {
				SoapObject object = vector.get(i);
				UserCallingGrpsMemberInfo info = new UserCallingGrpsMemberInfo();
				info.setCallNumber(Utils.reSoapObjectStr(object, "callNumber"));
				info.setNumberName(Utils.reSoapObjectStr(object, "numberName"));
				tonegrpInfos.add(info);
			}
		}
		return this;
	}

	public List<UserCallingGrpsMemberInfo> getToneList() {
		return tonegrpInfos;
	}

	public void setToneList(List<UserCallingGrpsMemberInfo> toneInfos) {
		this.tonegrpInfos = toneInfos;
	}

}
