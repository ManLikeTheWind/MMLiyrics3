package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.DESUtil;
import com.dxiang.mring3.utils.FusionCode;

/**
 * 注册业务的请求类
 * 
 * @author Administrator
 * 
 */
public class SubscribeReq extends Request {
	private final static String TAG = "SubscribeReq";

	/**
	 * 设置handler处理响应
	 * 
	 * @param handler
	 */
	public SubscribeReq(Handler handler) {
		setHandler(handler);

	}

	/**
	 * 发送请求
	 * 
	 * @param callNumber
	 * @param freeTrialPeriod
	 * @param serviceType
	 * @param preFlag
	 * @param level
	 * @param verificationcode
	 */
	public void sendSubscribeReq(String callNumber, String freeTrialPeriod,
			String serviceType, String preFlag, String level,
			String verificationcode) {
		String methodName = "subscribeapp";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		Log.e("murl", "mirl:" + Commons.SOAP_URL);
		// inaccessInfo
		rpc2.addProperty("callNumber", callNumber);
		rpc2.addProperty("freeTrialPeriod", freeTrialPeriod);
		rpc2.addProperty("serviceType", serviceType);
		// rpc2.addProperty("preFlag", preFlag);
		rpc2.addProperty("level", level);
		DESUtil encryptTool = DESUtil.getInstance();
		String strPwd = "";
		try {
			strPwd = encryptTool.encryptString(verificationcode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rpc2.addProperty("verificationcode", strPwd);
		addDefaultProperty(rpc2);
		rpc1.addProperty("subscribeappEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL + "/UserManage";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_SUBSCRIBEAPPEVT, soapAction);
	}
}
