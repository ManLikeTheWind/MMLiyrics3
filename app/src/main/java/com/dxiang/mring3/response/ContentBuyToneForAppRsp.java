package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;

/**
 * 非注册订购返回
 * @author Administrator
 *
 */
public class ContentBuyToneForAppRsp extends EntryP 
{

	public ContentBuyToneForAppRsp() 
	{
		returnKey="buyToneAPPReturn";
	}
	
	@Override
	public EntryP getEntryP(SoapObject result) 
	{
		return super.getEntryP(result);
	}
}
