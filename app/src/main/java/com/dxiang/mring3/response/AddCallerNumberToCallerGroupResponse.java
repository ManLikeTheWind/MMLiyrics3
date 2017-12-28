package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;

public class AddCallerNumberToCallerGroupResponse extends EntryP {

	public AddCallerNumberToCallerGroupResponse() {
		super();
		returnKey = "editCallingGrpMemReturn";
	}

	@Override
	public AddCallerNumberToCallerGroupResponse getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		// Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
		// .getProperty("userToneGrps");
		return this;
	}

}
