package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

public class QryToneBySingerRequest extends Request
{
	public QryToneBySingerRequest(Handler handler) {
		setHandler(handler);
	}
	
	public void sendQryToneBySingerRequest(String singerName, String orderType, 
			String orderBy, int showNum, int pageNo) 
	{
		String methodName = "qryToneBySinger";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
        String url = Commons.SOAP_URL + "/SysToneManage";
		SoapObject rpc = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc1.addProperty("showNum", showNum);
		rpc1.addProperty("pageNo", pageNo);
		rpc1.addProperty("singerName", singerName);
		rpc1.addProperty("orderType", "1");
		rpc1.addProperty("orderBy", "1");
		addDefaultProperty(rpc1);
		rpc.addProperty("QryToneBySingerEvt", rpc1);
		this.interfaceUrl = url;
		this.sendRequest(rpc, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_QryToneBySinger, soapAction);

	}
}
