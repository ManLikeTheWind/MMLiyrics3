package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;

/**
 * 修改密码
 * 
 * @author Administrator
 * 
 */
public class EditPwdRsp extends EntryP {
	public EditPwdRsp() {
		returnKey = "editPwdReturn";
	}

	@Override
	public EntryP getEntryP(SoapObject result) {
		super.getEntryP(result);
		return this;
	}

}
