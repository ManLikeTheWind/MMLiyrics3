package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

public class QryToneByNameRequest extends Request
{
	private final static String TAG = "QueryToneByNameRequest";

	public QryToneByNameRequest(Handler handler) 
	{
		setHandler(handler);
	}

	public void sendQueryToneByNameRequest(String toneName, String orderType, 
			String orderBy, int showNum, int pageNo) 
	{
		String methodName = "qryToneByName";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
        String url = Commons.SOAP_URL + "/SysToneManage";
		SoapObject rpc = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc1.addProperty("showNum", showNum);
		rpc1.addProperty("pageNo", pageNo);
		rpc1.addProperty("toneName", toneName);
		rpc1.addProperty("orderType", "1");
		rpc1.addProperty("orderBy", "1");
		addDefaultProperty(rpc1);
		rpc.addProperty("QryToneByNameEvt", rpc1);
		this.interfaceUrl = url;
		this.sendRequest(rpc, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_QryToneByName, soapAction);

	}
}
