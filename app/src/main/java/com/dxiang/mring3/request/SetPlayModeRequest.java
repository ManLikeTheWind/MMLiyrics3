package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;

/**
 * 设置随机模式
 * 
 * @author Administrator
 * 
 */
public class SetPlayModeRequest extends Request {
	private final static String TAG = "SetPlayModeReq";
	private int mode;

	/**
	 * 设置handler，处理响应
	 * 
	 * @param handler
	 */
	public SetPlayModeRequest(Handler handler, int mode) {
		setHandler(handler);
		this.mode = mode;
	}

	@Override
	public void sendRequest(SoapObject rpc, int conType, int reqType, String soapAction) {
		// TODO Auto-generated method stub
		super.sendRequest(rpc, conType, reqType, soapAction);
	}

	/**
	 * PlayMode 1 Play the default song which subscriber customized as default
	 * setting. 2 Play the songs randomly which subscriber has been purchased.
	 * 
	 * @param PlayMode
	 */
	public void sendSetGrpToneReq(String SetType) {

		String methodName = "UpTonePlay";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("operType", "2");
		rpc2.addProperty("setType", SetType);
		addDefaultProperty(rpc2);
		rpc1.addProperty("UpTonePlayEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL + "/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE, mode, soapAction);
	}
}
