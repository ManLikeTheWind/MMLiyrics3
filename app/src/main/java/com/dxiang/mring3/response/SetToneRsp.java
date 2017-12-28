package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;

/**
 * 设置用户主被叫的response
 * @author Administrator
 *
 */
public class SetToneRsp extends EntryP {

	private String settingID;

	public SetToneRsp() {
		super();
		returnKey = "setToneReturn";
	}

	@Override
	public SetToneRsp getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		settingID = (String) resultObject.getProperty("settingID");
		return this;
	}

	public String getSettingID() {
		return settingID;
	}

	public void setSettingID(String settingID) {
		this.settingID = settingID;
	}

}
