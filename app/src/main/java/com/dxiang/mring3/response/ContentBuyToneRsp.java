package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;
/**
 * 注册用户订购返回
 * @author Administrator
 *
 */
public class ContentBuyToneRsp extends EntryP 
{
	public ContentBuyToneRsp() 
	{
		returnKey="buyToneReturn";
	}
	
	@Override
	public EntryP getEntryP(SoapObject result) 
	{
		return super.getEntryP(result);
	}

}
