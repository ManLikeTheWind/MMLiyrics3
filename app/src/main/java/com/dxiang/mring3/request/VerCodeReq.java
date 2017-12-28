package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

/**
 * 获取验证码
 * @author Administrator
 *
 */
public class VerCodeReq extends Request {
	private final static String TAG = "VerCodeReq";

	public VerCodeReq(Handler handler) {
		setHandler(handler);

	}
/**
 * 发送验证码请求
 * @param userNumber 手机号码
 * @param operType  操作类型
 */
	public void sendVerCodeReq(String userNumber, String operType) {
		String methodName = "sendVerCode";
		String soapAction = Commons.SOAP_NAMESPACE + "/"+methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		// inaccessInfo
		rpc2.addProperty("userNumber", userNumber);
		//rpc2.addProperty("operType", operType);
		addDefaultProperty(rpc2);
		rpc1.addProperty("VerCodeEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim() +"/OtherMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_VERCODEEVT, soapAction);
	}
}
