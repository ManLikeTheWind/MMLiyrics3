package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;

/**
 * 查询周，月，全部铃音
 * @author Administrator
 *
 */
public class QryRankTone extends Request 
{
	public static final String TAG="QryRankTone";
	
	public QryRankTone(Handler handler)
	{
		this.setHandler(handler);
	}
	
	/**
	 * 排行榜单请求
	 * @param boardType 绑定类型
	 * @param showNum 请求条数
	 * @param pageNo 请求页数
	 * @param type 类型
	 */
	public void qryRankTone(String boardType,int showNum,int pageNo,int type)
	{
		String methodName = "qryRankRing";
		String soapAction = Commons.SOAP_NAMESPACE +"/"+ methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("boardType", boardType);
		rpc2.addProperty("showNum", showNum);
		rpc2.addProperty("pageNo", pageNo);
		addDefaultProperty(rpc2);
		rpc1.addProperty("QryRankToneEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim()+"/SysToneManage";
		Log.i(TAG, interfaceUrl);
		if(type==1)//周
		{
			this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,FusionCode.REQUEST_QRYRANKTONE_WEEK, soapAction);
		}
		else if(type==2)//月
		{
			this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,FusionCode.REQUEST_QRYRANKTONE_MONTH, soapAction);
		}
		else if(type==3)//全部
		{
			this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,FusionCode.REQUEST_QRYRANKTONE_TOTAL, soapAction);
		}
		else if(type==5)//最新
		{
			this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,FusionCode.REQUEST_NEWRING, soapAction);
		}
		
		
	}

}
