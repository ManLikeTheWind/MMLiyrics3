package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

/**
 * 找回密码的请求类
 * @author Administrator
 *
 */
public class SendPwdReq extends Request {
	private final static String TAG = "GetPwdRequest";

	/**
	 * 设置handler，处理响应
	 * @param handler
	 */
	public SendPwdReq(Handler handler) {
		setHandler(handler);

	}

	/**
	 * 发送请求
	 * @param callNumber
	 */
	public void sendGetPwdRequest(String callNumber) {
		String methodName = "sendPwd";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", callNumber);
		addDefaultProperty(rpc2);
		rpc1.addProperty("SendPwdEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim() + "/UserManage";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_SENDPWDEVT, soapAction);
	}
}
