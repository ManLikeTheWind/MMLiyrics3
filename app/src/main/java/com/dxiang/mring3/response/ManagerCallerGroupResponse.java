package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;

public class ManagerCallerGroupResponse extends EntryP {

	public ManagerCallerGroupResponse() {
		super();
		returnKey = "manCallingGrpReturn";
	}

	@Override
	public ManagerCallerGroupResponse getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		// Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
		// .getProperty("userToneGrps");
		return this;
	}

}