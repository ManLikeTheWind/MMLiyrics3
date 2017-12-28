package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;
/**
 * 获取密码返回
 * @author Administrator
 *
 */
public class GetPwdRsp extends EntryP {

	// 0-Not exist
	// 1-activated called RBT user
	// 2-deactivated called RBT user
	// 3-Force deactivated called RBT user

	private String callNo;
	private String pwd;

	public GetPwdRsp() {
		super();
		returnKey = "getPwdReturn";
	}

	@Override
	public GetPwdRsp getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		pwd = (String) resultObject.getProperty("pwd");
		callNo = (String) resultObject.getProperty("callNumber");
		return this;
	}

	public String getCallNo() {
		return callNo;
	}

	public void setCallNo(String callNo) {
		this.callNo = callNo;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
