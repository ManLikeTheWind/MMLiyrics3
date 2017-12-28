package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

/**
 * 查询用户业务的请求类，登陆时使用
 * 
 * @author Administrator
 *
 */
public class UserSubSerReq extends Request {
	private final static String TAG = "UserSubSerRequest";

	/**
	 * 设置handler处理响应
	 * 
	 * @param handler
	 */
	public UserSubSerReq(Handler handler) {
		setHandler(handler);

	}

	/**
	 * 发送请求
	 * 
	 * @param callNumber
	 * @param pwd
	 */
	public void sendUserSubSerRequest(String callNumber, String pwd) {
		String methodName = "userSubserviceInformation";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", callNumber);
		rpc2.addProperty("pwd", pwd);
		addDefaultProperty(rpc2);

		rpc1.addProperty("UserSubSerEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim() + "/UserManage";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_USERSUBSEREVT, soapAction);
	}
}
