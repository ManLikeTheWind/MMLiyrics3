package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.utils.Utils;

public class QryPlayModeResp extends EntryP {

	String setType;

	public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}

	public QryPlayModeResp() {
		synchronized (this) {
			returnKey = "qryPlayModeReturn";
			
		}
	}

	@Override
	public EntryP getEntryP(SoapObject result) {
		super.getEntryP(result);
		synchronized (this) {
			
			setType =Utils.reSoapObjectStr(resultObject, "setType");
			return this;
		}
	}

}
