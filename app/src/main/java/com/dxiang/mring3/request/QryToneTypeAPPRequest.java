package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

public class QryToneTypeAPPRequest extends Request {

	public QryToneTypeAPPRequest(Handler handler) {
		setHandler(handler);
	}

	public void sendQryToneTypeAPPRequest(int showNum, int pageNo) {
		String methodName = "qryToneTypeAPP";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
        String url = Commons.SOAP_URL + "/SysToneManage";
		SoapObject rpc = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc1.addProperty("showNum", showNum);
		rpc1.addProperty("pageNo", pageNo);
		addDefaultProperty(rpc1);
		rpc.addProperty("QryToneTypeAPPEvt", rpc1);
		this.interfaceUrl = url;
		this.sendRequest(rpc, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_QryToneTypeAPP, soapAction);

	}

}
