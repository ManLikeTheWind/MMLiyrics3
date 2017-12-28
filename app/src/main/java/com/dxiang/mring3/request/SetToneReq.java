package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.bean.UserToneSettingInfo;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UserVariable;

/**
 * 设置主被叫的请求类
 * 
 * @author Administrator
 * 
 */
public class SetToneReq extends Request {
	private final static String TAG = "SetToneReq";

	/**
	 * 设置handler，处理响应
	 * 
	 * @param handler
	 */
	public SetToneReq(Handler handler) {
		setHandler(handler);

	}

	/**
	 * 发送请求
	 * 
	 * @param operType
	 * @param serviceType
	 * @param settingID
	 * @param toneID
	 * @param toneType
	 * @param timeType
	 */
	public void sendSetToneReq(String operType, String serviceType, String settingID, String toneID, String toneType,
			String timeType) {
		String methodName = "setTone";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("operType", "2");
		SoapObject rpc3 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc3.addProperty("settingID", settingID);
		rpc3.addProperty("settingObjType", "1");
		rpc3.addProperty("toneID", toneID);
		rpc3.addProperty("toneType", toneType);
		rpc3.addProperty("timeType", timeType);
		rpc3.addProperty("startTime", "0");
		rpc3.addProperty("calling", "");
		rpc3.addProperty("endTime", "0");
		rpc3.addProperty("playStartTime", "0");
		rpc3.addProperty("playEndTime", "0");
		rpc3.addProperty("timedescrip", "");
		rpc2.addProperty("userToneSettingInfo", rpc3);
		rpc2.addProperty("serviceType", serviceType);
		addDefaultProperty(rpc2);
		rpc1.addProperty("SetToneEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL + "/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE, FusionCode.REQUEST_SETTONEEVT, soapAction);
	}

	// /**
	// * 发送请求
	// *
	// * @param operType
	// * @param serviceType
	// * @param settingID
	// * @param toneID
	// * @param toneType
	// * @param timeType
	// */
	/**
	 * 发送请求
	 * 
	 * @param operType
	 * @param groupid
	 * @param toneID
	 * @param toneType
	 *            toneType = 1;(0-铃音 1-铃音组) };
	 */
	public void sendSetGrpToneReq(String operType, String groupid, String toneID, String toneType) {

		String methodName = "SetTone";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("operType", "1");
		SoapObject rpc3 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc3.addProperty("settingID", "0000000000");
		rpc3.addProperty("settingObjType", "3");
		rpc3.addProperty("toneID", toneID);
		rpc3.addProperty("toneType", toneType);
		rpc3.addProperty("timeType", "4");
		rpc3.addProperty("startTime", "20000101000000");
		rpc3.addProperty("calling", groupid);
		rpc3.addProperty("endTime", "29991230000000");
		rpc3.addProperty("playStartTime", "000");
		rpc3.addProperty("playEndTime", "000");
		rpc3.addProperty("timedescrip", "####");
		rpc2.addProperty("userToneSettingInfo", rpc3);
		rpc2.addProperty("serviceType", "1");
		addDefaultProperty(rpc2);
		rpc1.addProperty("SetToneEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL + "/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE, FusionCode.REQUEST_SETTONEEVT, soapAction);
	}

	
	/**
	 * 2016/7/19
	 * @param toneID
	 * @param serviceType
	 */
	public void sendSetGrpToneReq(String toneID, String serviceType) {

		String methodName = "setTone";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", UserVariable.CALLNUMBER);
		rpc2.addProperty("operType", "2");
		SoapObject rpc3 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc3.addProperty("settingID", "0000000000");
		rpc3.addProperty("settingObjType", "1");
		rpc3.addProperty("toneID", toneID);
		rpc3.addProperty("toneType", "1");
		rpc3.addProperty("timeType", "0");
		rpc3.addProperty("startTime", "0");
		rpc3.addProperty("endTime", "0");
		rpc3.addProperty("playStartTime", "0");
		rpc3.addProperty("playEndTime", "0");
		rpc3.addProperty("timedescrip", "000000000");
		rpc3.addProperty("calling", "000");
		rpc2.addProperty("userToneSettingInfo", rpc3);
		rpc2.addProperty("serviceType", serviceType);
		addDefaultProperty(rpc2);
		rpc1.addProperty("SetToneEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL + "/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE, FusionCode.REQUEST_SETTONEEVT, soapAction);
	}
}
