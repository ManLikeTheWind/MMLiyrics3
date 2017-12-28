package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;

/**
 * 获取用户铃音播放地址的response
 * @author Administrator
 *
 */
public class GetToneListenAddrRsp extends EntryP {
	private String toneAddr;

	public String getToneAddr() {
		return toneAddr;
	}

	public void setToneAddr(String toneAddr) {
		this.toneAddr = toneAddr;
	}
	
	public GetToneListenAddrRsp() {
		super();
		returnKey = "getToneListenAddrReturn";
	}
	
	@Override
	public GetToneListenAddrRsp getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		toneAddr = (String) resultObject.getProperty("tonePreListenAddress");
		return this;
	}
}
