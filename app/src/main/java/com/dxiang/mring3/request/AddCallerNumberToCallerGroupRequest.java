package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;
import android.os.Handler;
import android.util.Log;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;

public class AddCallerNumberToCallerGroupRequest extends Request {

	private final static String TAG = "AddCallerNumberToCallerGroupRequest";

	/**
	 * 设置handler处理响应
	 * 
	 * @param handler
	 */
	public AddCallerNumberToCallerGroupRequest(Handler handler) {
		setHandler(handler);
	}

	/**
	 * 发送请求
	 * 
	 * @param type
	 *            Operation 1 Add 2 Delete
	 * @param grpid
	 * @param callingname
	 * @param callingnumber
	 */
	public void sendAddCallerNumberToCallerGroupRequest(String type,
			String grpid, String callingname, String callingnumber) {
		String methodName = "EditCallingGrpMem";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("operType", type);
		rpc2.addProperty("callNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("callingGrpID", grpid);
		rpc2.addProperty("callingNumber", callingnumber);
		rpc2.addProperty("numberName", callingname);
		addDefaultProperty(rpc2);
		rpc1.addProperty("EditCallingGrpMemEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL + "/UserGrpManage";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_EDITMANAGERCALLINGGROUP, soapAction);
	}

}
