package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;

public class GetToneListenAddrReq extends Request{
	private final static String TAG = "GetToneListenAddrReq";
	
	public String id = "";

	public GetToneListenAddrReq(Handler handler) {
		setHandler(handler);
	}
	
	public void sendGetToneListenAddrReq(String toneID,String toneType){
		id = toneID;
		String methodName = "GetToneListenAddr";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("toneID", toneID);
		rpc2.addProperty("toneType", toneType);
		addDefaultProperty(rpc2);
		rpc1.addProperty("GetToneListenAddrEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL + "/SysToneManage";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_GETTONELISTENADDREVT, soapAction);
	}
}
