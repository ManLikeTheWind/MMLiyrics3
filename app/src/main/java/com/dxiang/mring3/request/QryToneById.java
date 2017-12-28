package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

/**
 * 根据id查询铃音
 * 
 * @author Administrator
 * 
 */
public class QryToneById extends Request {
	public static final String TAG = "QryToneById";
	private int mFusioncode;

	public QryToneById(Handler handler, int fusioncode) {
		this.mFusioncode = fusioncode;
		this.setHandler(handler);
	}

	/**
	 * 根据id查询铃音请求
	 * 
	 * @param ToneID
	 *            铃音id
	 * @param OrderType
	 *            类型
	 * @param OrderBy
	 *            排序类型
	 * @param showNum
	 *            请求条数
	 * @param pageNo
	 *            页数
	 */
	public void getQryToneById(String ToneID, String OrderType, String OrderBy,
			int showNum, int pageNo) {
		String methodName = "qryToneById";
		String soapAction = Commons.SOAP_NAMESPACE + "/" + methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("toneID", ToneID);
		rpc2.addProperty("orderType", OrderType);
		rpc2.addProperty("orderBy", OrderBy);
		rpc2.addProperty("showNum", showNum);
		rpc2.addProperty("pageNo", pageNo);
		addDefaultProperty(rpc2);
		rpc1.addProperty("QryToneByIdEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim() + "/SysToneManage";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE, mFusioncode,
				soapAction);
	}

}
