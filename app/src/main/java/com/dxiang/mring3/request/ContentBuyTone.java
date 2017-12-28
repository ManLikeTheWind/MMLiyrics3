package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;


/**
 * 已经注册的订购
 * @author Administrator
 *
 */
public class ContentBuyTone extends Request 
{
	public static final String TAG="ContentBuyTone";
	
	public ContentBuyTone(Handler handler)
	{
		this.setHandler(handler);
	}
	/**
	 * 发送订购请求
	 * @param CallNumber 号码
	 * @param ToneType 铃音type
	 * @param ToneID 铃音id
	 */
	public void getContentBuyTone(String CallNumber,String ToneType,String ToneID)
	{
		String methodName = "buyTone";
		String soapAction = Commons.SOAP_NAMESPACE +"/"+ methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", CallNumber);
		rpc2.addProperty("toneType", ToneType);
		rpc2.addProperty("toneID", ToneID);
		addDefaultProperty(rpc2);
		rpc1.addProperty("BuyToneEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim()+"/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,FusionCode.REQUEST_CONTENTBUYTONE, soapAction);
		
		
	}

}
