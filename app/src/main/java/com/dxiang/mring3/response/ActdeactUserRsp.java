package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;


/**
 * 激活或去激活
 * @author Administrator
 *
 */
public class ActdeactUserRsp extends EntryP 
{
	public ActdeactUserRsp(){
		returnKey="actDeactUserReturn";
	}
	@Override
	public EntryP getEntryP(SoapObject result) 
	{
		return super.getEntryP(result);
	}

}
