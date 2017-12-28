package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;

/**
 * 查询主被叫铃音的请求类
 * 
 * @author Administrator
 * 
 */
public class QryToneSetReq extends Request {
	// ServiceType
	// 1.individual called CRBT
	// 2.individual caller CRBT

	private final static String TAG = "QryToneSetReq";
	// 用来区分请求
	private int mFusioncode = 0;

	/**
	 * 设置handler处理响应
	 * 
	 * @param handler
	 * @param fusioncode
	 */
	public QryToneSetReq(Handler handler, int fusioncode) {
		setHandler(handler);
		mFusioncode = fusioncode;
	}

	/**
	 * 发送查询请求
	 * 
	 * @param showNum
	 * @param pageNo
	 * @param serviceType
	 * Service type
	 * 1.individual called CRBT
	 * 2.individual caller CRBT
	 */
	public void sendQryToneSetReq(int showNum, int pageNo, String serviceType) {
		String methodName = "qryToneSet";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("showNum", showNum);
		rpc2.addProperty("pageNo", pageNo);
		rpc2.addProperty("serviceType", serviceType);
		addDefaultProperty(rpc2);
		rpc1.addProperty("QryToneSetEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL + "/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE, mFusioncode,
				soapAction);
	}
}
