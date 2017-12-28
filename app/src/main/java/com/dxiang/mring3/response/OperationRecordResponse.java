package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.ksoap2.serialization.SoapObject;
import com.dxiang.mring3.bean.RecordInfo;
import com.dxiang.mring3.utils.Utils;

public class OperationRecordResponse extends EntryP {

	public OperationRecordResponse() {
		returnKey = "getUserOperatingLogReturn";
	}

	private List<RecordInfo> infos;

	@Override
	public OperationRecordResponse getEntryP(SoapObject result) {
		super.getEntryP(result);
		try {
			infos = new ArrayList<RecordInfo>();
			Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
					.getProperty("recordInfos");
			if (null != vector) {
				for (int i = 0; i < vector.size(); i++) {
					SoapObject object = vector.get(i);
					RecordInfo info = new RecordInfo();
					info.setChanel(Utils.reSoapObjectStr(object, "chanel"));
					info.setLogType(Utils.reSoapObjectStr(object, "logType"));
					info.setToneID(Utils.reSoapObjectStr(object, "toneID"));
					info.setUserNumber(Utils.reSoapObjectStr(object,
							"userNumber"));
					info.setOperTime(Utils.reSoapObjectStr(object, "operTime"));
					infos.add(info);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}

		// TODO Auto-generated method stub
		return this;
	}

	public List<RecordInfo> getInfos() {
		return infos;
	}

	public void setInfos(List<RecordInfo> infos) {
		this.infos = infos;
	}

}
