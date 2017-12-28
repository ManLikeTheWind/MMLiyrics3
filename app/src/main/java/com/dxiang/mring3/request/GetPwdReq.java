package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

/**
 * 获取用户密码
 * @author Administrator
 *
 */
public class GetPwdReq extends Request {
	private final static String TAG = "GetPwdRequest";

	public GetPwdReq(Handler handler) {
		setHandler(handler);
		
	}
	
	
/**
 *  获取密码请求
 * @param callNumber 手机号码
 */
	public void sendGetPwdRequest(String callNumber) {
		String methodName = "getPwd";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", callNumber);
		addDefaultProperty(rpc2);
		rpc1.addProperty("GetPwdEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim() + "/UserManage";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_GETPWDEVT, soapAction);
	}
}
