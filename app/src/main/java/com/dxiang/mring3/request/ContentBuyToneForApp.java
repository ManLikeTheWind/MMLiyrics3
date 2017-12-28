package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.DESUtil;
import com.dxiang.mring3.utils.FusionCode;

/**
 * 非注册用户订购
 * @author Administrator
 *
 */
public class ContentBuyToneForApp extends Request 
{
	public static final String TAG="ContentBuyToneForApp";
	
	public ContentBuyToneForApp(Handler handler)
	{
		this.setHandler(handler);
	}
	
	/**
	 * 非注册用户订购
	 * @param CallNumber 手机号码
	 * @param ToneType 铃音type
	 * @param ToneID 铃音id
	 * @param Verificationcode 验证码
	 */
	
	public void getContentBuyToneForApp(String CallNumber,String ToneType,String ToneID,String Verificationcode)
	{
		String methodName = "buyToneAPP";
		String soapAction = Commons.SOAP_NAMESPACE +"/"+ methodName;
		SoapObject rpc1 = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		DESUtil encryptTool = DESUtil.getInstance();
		String strVerificationcode = "";
		try {
			strVerificationcode = encryptTool.encryptString(Verificationcode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// inaccessInfo
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
		rpc2.addProperty("callNumber", CallNumber);
		rpc2.addProperty("toneType", ToneType);
		rpc2.addProperty("toneID", ToneID);
		rpc2.addProperty("verificationcode", strVerificationcode);
		addDefaultProperty(rpc2);
		rpc1.addProperty("BuyToneAPPEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim()+"/UserToneMan";
		Log.i(TAG, interfaceUrl);
		this.sendRequest(rpc1, FusionCode.CONNECT_TYPE_WEBSERVICE,FusionCode.REQUEST_CONTENTBUYTONEFORAPP, soapAction);
	}

}
