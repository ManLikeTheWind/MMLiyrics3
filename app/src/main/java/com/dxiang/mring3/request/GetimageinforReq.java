package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

import android.os.Handler;
import android.util.Log;

public class GetimageinforReq extends Request {

	private final static String TAG = "GetimageinforRequest";

	public GetimageinforReq(Handler handler) {
		this.setHandler(handler);

	}

	public void sendGetimageinforReqRequest(String imageType) {
		String methodName = "getImageInfo";
		String soapAction = Commons.SOAP_NAMESPACE + methodName;
		String url = Commons.SOAP_URL.trim() + "/OtherMan";
		SoapObject rpc = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc1.addProperty("imageType", imageType);
		addDefaultProperty(rpc1);
		rpc.addProperty("GetImageInfoEvt", rpc1);
		this.interfaceUrl = url;
		this.sendRequest(rpc, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_GetImageInfo, soapAction);
	}

}
