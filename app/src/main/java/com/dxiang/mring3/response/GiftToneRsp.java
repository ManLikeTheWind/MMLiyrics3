package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;
/**
 * 注册用户订购返回
 * @author Administrator
 *
 */
public class GiftToneRsp extends EntryP 
{
	public GiftToneRsp() 
	{
		returnKey="giftToneReturn";
	}
	
	@Override
	public EntryP getEntryP(SoapObject result) 
	{
		return super.getEntryP(result);
	}

}
