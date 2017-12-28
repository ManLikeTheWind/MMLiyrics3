package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

import android.os.Handler;
import android.util.Log;

public class GiftToneRequest extends Request {

public static final String TAG="ContentBuyTone";
	
	public GiftToneRequest(Handler handler)
	{
		this.setHandler(handler);
	}
	/**
	 * 发送订购请求
	 * @param CallNumber 号码
	 * @param ToneType 铃音type
	 * @param ToneID 铃音id
	 */
	public void setGiftToneRequest(String CallNumber,String ToneType,String ToneID,String Receiver)
	{
		String methodName = "GiftTone";
		String soapAction = Commons.SOAP_NAMESPACE +"/"+ methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", CallNumber);
		rpc2.addProperty("toneType", ToneType);
		rpc2.addProperty("toneID", ToneID);
		rpc2.addProperty("receiver",Receiver);
		addDefaultProperty(rpc2);
		rpc1.addProperty("GiftToneEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim()+"/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,FusionCode.REQUEST_GIFTTONE, soapAction);
		
		
	}
}
