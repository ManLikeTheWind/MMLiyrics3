package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Handler;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UtilLog;

public class SuggestionFeedback extends Request{
	public SuggestionFeedback(Handler hand) {
		this.setHandler(hand);
	}
	/**
	 * 
	 * @return
	 * @throws XmlPullParserException
	 */
	public void getSuggestionFeedback( String callNumber,String suggestion) {
		String methodName = "feedBack";
		String soapAction = Commons.SOAP_NAMESPACE + "/"+methodName;
		SoapObject rpc = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
//		SoapObject rpc1 = new BaseSoapObject();
//		rpc1.addProperty("channelID", "0010001");
//		rpc.addProperty("inaccessInfo", rpc1);
		rpc2.addProperty("callNumber", callNumber);
		rpc2.addProperty("suggestion", suggestion);
		addDefaultProperty(rpc2);
		rpc.addProperty("SuggestionFeedbackEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim()+"/OtherMan";
		UtilLog.e("SuggestionFeedbackEvt",
				this.interfaceUrl + "----" + (this.getHandler() == null));
		this.sendRequest(rpc, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_SUGGESTIONFEEDBACK, soapAction);
	}
}
