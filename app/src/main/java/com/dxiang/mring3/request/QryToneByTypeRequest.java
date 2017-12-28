package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

public class QryToneByTypeRequest extends Request
{
	private final static String TAG = "QryToneBySingerRequest";

	public QryToneByTypeRequest(Handler handler) {
		setHandler(handler);
	}
	
	public void sendQryToneByTypeRequest(String id, int pageNo, int showNum, int fusion) 
	{
		String methodName = "qryToneByType";
		 String soapAction = Commons.SOAP_NAMESPACE + methodName;
	     String url = Commons.SOAP_URL + "/SysToneManage";
			SoapObject rpc = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
			// inaccessInfo
			SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, "");
			rpc1.addProperty("showNum", showNum);
			rpc1.addProperty("pageNo", pageNo);
			int num = 0;
			try 
			{
				num = Integer.parseInt(id);
			}
			catch (Exception e)
			{
			}
			
			rpc1.addProperty("toneTypeID", num);
			rpc1.addProperty("orderType", "1");
			rpc1.addProperty("orderBy", "1");
			
			addDefaultProperty(rpc1);
			rpc.addProperty("QryToneByTypeEvt", rpc1);
			this.interfaceUrl = url;
			this.sendRequest(rpc, FusionCode.CONNECT_TYPE_WEBSERVICE,
					fusion, soapAction);
	}
}
