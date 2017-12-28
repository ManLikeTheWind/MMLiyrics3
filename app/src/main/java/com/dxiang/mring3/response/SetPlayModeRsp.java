package com.dxiang.mring3.response;

import java.util.Vector;
import org.ksoap2.serialization.SoapObject;

public class SetPlayModeRsp extends EntryP {

	public SetPlayModeRsp() {
		super();
		returnKey = "upTonePlayReturn";
	}

	@Override
	public SetPlayModeRsp getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
//		Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
//				.getProperty("userToneGrps");
		return this;
	}

}
