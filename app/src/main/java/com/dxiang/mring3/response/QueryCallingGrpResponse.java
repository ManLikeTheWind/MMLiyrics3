package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.ksoap2.serialization.SoapObject;
import com.dxiang.mring3.bean.UserCallingGrps;
import com.dxiang.mring3.utils.Utils;

public class QueryCallingGrpResponse extends EntryP {

	private List<UserCallingGrps> tonegrpInfos;

	public QueryCallingGrpResponse() {
		super();
		tonegrpInfos = new ArrayList<UserCallingGrps>();
		returnKey = "qryCallingGrpReturn";
	}

	@Override
	public QueryCallingGrpResponse getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
				.getProperty("userCallingGrps");
		if (null != vector) {
			for (int i = 0; i < vector.size(); i++) {
				SoapObject object = vector.get(i);
				UserCallingGrps info = new UserCallingGrps();
				info.setCallingName(Utils
						.reSoapObjectStr(object, "callingName"));
				info.setCallingGrpID(Utils.reSoapObjectStr(object,
						"callingGrpID"));
				tonegrpInfos.add(info);
			}
		}
		return this;
	}

	public List<UserCallingGrps> getToneList() {
		return tonegrpInfos;
	}

	public void setToneList(List<UserCallingGrps> toneInfos) {
		this.tonegrpInfos = toneInfos;
	}

}
