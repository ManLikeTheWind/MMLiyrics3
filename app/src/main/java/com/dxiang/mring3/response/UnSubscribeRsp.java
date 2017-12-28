package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;


/**
 * 注销铃音
 * @author Administrator
 *
 */
public class UnSubscribeRsp extends EntryP 
{
	public UnSubscribeRsp(){
		returnKey="unSubscribeReturn";
	}
	@Override
	public EntryP getEntryP(SoapObject result) 
	{
		return super.getEntryP(result);
	}

}
