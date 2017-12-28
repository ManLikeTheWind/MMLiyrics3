package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;

/**
 * 删除铃音的请求类
 * @author Administrator
 *
 */
public class DelToneReq extends Request {
	private final static String TAG = "DelToneReq";

	/**
	 * 设置handler处理响应
	 * @param handler
	 */
	public DelToneReq(Handler handler) {
		setHandler(handler);
		
	}

	/**
	 * 发起删除请求
	 * @param toneType鈴音类型
	 * @param toneID铃音id
	 */
	public void sendDelToneReq(String toneType, String toneID) {
		String methodName = "DelTone";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("toneType", toneType);
		rpc2.addProperty("toneID", toneID);
		addDefaultProperty(rpc2);
		rpc1.addProperty("DelToneEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL +"/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_DELTONEEVT, soapAction);
	}
}
