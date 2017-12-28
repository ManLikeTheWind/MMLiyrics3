package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;

/**
 * 查詢用戶主被叫业务的response，登录时使用
 * @author Administrator
 *
 */
public class UserSubSerRsp extends EntryP {

	private String statusCalled;
	private String statusCalling;

	public UserSubSerRsp() {
		super();
		returnKey = "userSubserviceInformationReturn";
	}

	@Override
	public UserSubSerRsp getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		statusCalled = (String) resultObject.getProperty("statusCalled");
		statusCalling = (String) resultObject.getProperty("statusCalling");
		return this;
	}

	public String getStatusCalled() {
		return statusCalled;
	}

	public void setStatusCalled(String statusCalled) {
		this.statusCalled = statusCalled;
	}

	public String getStatusCalling() {
		return statusCalling;
	}

	public void setStatusCalling(String statusCalling) {
		this.statusCalling = statusCalling;
	}

}
