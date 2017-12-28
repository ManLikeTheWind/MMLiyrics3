package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;

public class AddRingtoGroupResponse extends EntryP {

	public AddRingtoGroupResponse() {
		super();
		returnKey = "manToneGrpMemReturn";
	}

	@Override
	public AddRingtoGroupResponse getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		// Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
		// .getProperty("userToneGrps");
		return this;
	}

}
