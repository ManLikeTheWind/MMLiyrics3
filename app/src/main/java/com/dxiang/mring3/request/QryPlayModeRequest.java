package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;

/**
 * 查询被叫铃音播放模式
 * 
 * @author Administrator
 * 
 */
public class QryPlayModeRequest extends Request {
	public static final String TAG = "QryPlayMode";
	

	public QryPlayModeRequest(Handler handler) {

		this.setHandler(handler);
	}

	public void qryPlayModerep() {
		String methodName = "qryPlayMode";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("operType", "02");
		addDefaultProperty(rpc2);
		rpc1.addProperty("QryPlayModeEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim() + "/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE, FusionCode.REQUEST_QRYPLAYMODE, soapAction);
	}

}
