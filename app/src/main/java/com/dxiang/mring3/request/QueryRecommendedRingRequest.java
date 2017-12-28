package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

import android.os.Handler;
import android.util.Log;

public class QueryRecommendedRingRequest extends Request{
	


	public QueryRecommendedRingRequest(Handler handler) 
	{
		setHandler(handler);
	}

	public void sendQueryRecommendedRingRequest( String boardType,int showNum, int pageNo) 
	{
		String methodName = "queryRecommendedRing";
		String soapAction = Commons.SOAP_NAMESPACE + "/"+methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		
		rpc2.addProperty("boardType", "201");
		
		rpc2.addProperty("showNum", 100);
		rpc2.addProperty("pageNo", 0);
		addDefaultProperty(rpc2);
		rpc1.addProperty("QryRecommendToneEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim()+"/SysToneManage";
		
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,FusionCode.REQUEST_Recommend, soapAction);

	}

}
