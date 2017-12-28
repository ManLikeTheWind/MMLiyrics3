package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.ksoap2.serialization.SoapObject;
import com.dxiang.mring3.bean.UserToneGrps;
import com.dxiang.mring3.bean.UserToneSettingInfo;
import com.dxiang.mring3.utils.Utils;

public class QryToneGrpResp extends EntryP {
	private List<UserToneGrps> tonegrpInfos;

	public QryToneGrpResp() {
		super();
		tonegrpInfos = new ArrayList<UserToneGrps>();
		returnKey = "qryToneGrpReturn";
	}

	@Override
	public QryToneGrpResp getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
				.getProperty("userToneGrps");
		if (null != vector) {
			for (int i = 0; i < vector.size(); i++) {
				SoapObject object = vector.get(i);
				UserToneGrps info = new UserToneGrps();
				info.setToneGrpName(Utils
						.reSoapObjectStr(object, "toneGrpName"));
				info.setUserToneGrpID(Utils.reSoapObjectStr(object,
						"userToneGrpID"));
				tonegrpInfos.add(info);
			}
		}
		return this;
	}

	public List<UserToneGrps> getToneList() {
		return tonegrpInfos;
	}

	public void setToneList(List<UserToneGrps> toneInfos) {
		this.tonegrpInfos = toneInfos;
	}
}
