package com.dxiang.mring3.response;

import java.util.Vector;
import org.ksoap2.serialization.SoapObject;

public class AddRingGroupResponse extends EntryP {

	public AddRingGroupResponse() {
		super();
		returnKey = "manToneGrpReturn";
	}

	@Override
	public AddRingGroupResponse getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
//		Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
//				.getProperty("userToneGrps");
		return this;
	}

}
