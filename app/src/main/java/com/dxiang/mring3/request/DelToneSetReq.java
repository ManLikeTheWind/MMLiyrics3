package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;

/**
 * 刪除主被叫请求类
 * @author Administrator
 *
 */
public class DelToneSetReq extends Request {
	private final static String TAG = "DelToneSetReq";

	/**
	 * 设置handler处理响应
	 * @param handler
	 */
	public DelToneSetReq(Handler handler) {
		setHandler(handler);

	}

	/**
	 * 发送删除请求
	 * @param settingID
	 */
	public void sendDelToneSetReq(String settingID) {
		String methodName = "DelToneSet";
		String soapAction = Commons.SOAP_NAMESPACE + "/"+methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("settingID", settingID);
		addDefaultProperty(rpc2);
		rpc1.addProperty("DelToneSetEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL+"/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_DELTONESETEVT, soapAction);
	}
}
