package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;

public class OperationRecordRequest extends Request {

	private final static String TAG = "OperationRecordRequest";

	/**
	 * 设置handler处理响应
	 * 
	 * @param handler
	 */
	public OperationRecordRequest(Handler handler) {
		setHandler(handler);
	}

	/**
	 * 发送请求
	 * 
	 * @param type
	 *            Operation Type 1 create;2 modify;3 Delete
	 */
	public void sendOperationRecordRequest(int shownum, int pageno) {
		String methodName = "getUserOperatingLog";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("userNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("showNum", shownum);
		rpc2.addProperty("pageNo", pageno);
		addDefaultProperty(rpc2);
		rpc1.addProperty("getUserOperatingLogEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL + "/OtherMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_EDITRECORD, soapAction);
	}
}
