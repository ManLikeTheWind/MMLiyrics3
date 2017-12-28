package com.dxiang.mring3.response;

import org.ksoap2.serialization.SoapObject;


/**
 * 意见反馈
 * @author Administrator
 *
 */
public class SuggestionFeedbackRsp extends EntryP 
{
	public SuggestionFeedbackRsp(){
		returnKey="feedBackReturn";
	}
	@Override
	public EntryP getEntryP(SoapObject result) 
	{
		return super.getEntryP(result);
	}

}
