package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;

public class AddRingGroupRequest extends Request {
	private final static String TAG = "AddRingGroupRequest";

	/**
	 * 设置handler处理响应
	 * 
	 * @param handler
	 */
	public AddRingGroupRequest(Handler handler) {
		setHandler(handler);
	}

	/**
	 * 发送请求
	 * 
	 * @param type
	 *            Operation Type 1 create;2 modify;3 Delete
	 */
	public void sendAddRingGroupRequest(String type, String grpid,
			String grpname) {
		String methodName = "ManToneGrp";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("operType", type);
		rpc2.addProperty("callNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("userToneGrpID", grpid);
		rpc2.addProperty("toneGrpName", grpname);
		addDefaultProperty(rpc2);
		rpc1.addProperty("ManToneGrpEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL + "/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_ADDNEWGROUP, soapAction);
	}
}
