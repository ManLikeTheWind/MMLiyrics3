package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

/**
 * 获取推荐铃音
 * @author Administrator
 *
 */
public class QryRecommendTone extends Request 
{
	public final static String TAG = "QryRecommendTone";
	
	public QryRecommendTone(Handler handler)
	{
		this.setHandler(handler);
	}
	
	/**
	 * 推荐榜单请求
	 * @param boardType 推荐类型
	 * @param showNum 请求条数
	 * @param pageNo 页数
	 */
	public void qryRecommendedTone(String boardType,int showNum,int pageNo)
	{
		String methodName = "qryRecommendRing";
		String soapAction = Commons.SOAP_NAMESPACE +"/"+ methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("boardType", boardType);
		rpc2.addProperty("showNum", showNum);
		rpc2.addProperty("pageNo", pageNo);
		addDefaultProperty(rpc2);
		rpc1.addProperty("QryRecommendToneEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim()+"/SysToneManage";
		Log.i(TAG, interfaceUrl);
		Log.i(TAG, soapAction);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,FusionCode.REQUEST_QRYRECOMMENDTONE, soapAction);
	}
	

}
