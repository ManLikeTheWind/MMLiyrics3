package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UtilLog;

/**
 * 注销
 * 
 * @author humingzhuo
 * 
 */
public class ActDeactUser extends Request {

	public ActDeactUser(Handler hand) {
		this.setHandler(hand);
	}

	/**
	 * accountType callNumber reserve1 保留字段1
	 * 
	 * @return
	 * @throws
	 */
	public void qryActDeactUser(String CallNumber, String Pwd,
			String OperType, String ServiceType) {
		String methodName = "actDeactUser";
		String soapAction = Commons.SOAP_NAMESPACE + "/"+methodName;
		SoapObject rpc = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
//		SoapObject rpc1 = new BaseSoapObject();
//		rpc1.addProperty("channelID", "0010001");
//		rpc.addProperty("inaccessInfo", rpc1);
//		DESUtil encryptTool = DESUtil.getInstance();
//		String strPwd = "";
//		try {
//			strPwd = encryptTool.encryptString(Pwd);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		rpc2.addProperty("callNumber", CallNumber);
		rpc2.addProperty("pwd", Pwd);
		rpc2.addProperty("operType", OperType);
		rpc2.addProperty("serviceType", ServiceType);
		addDefaultProperty(rpc2);
		rpc.addProperty("ActDeactUserEvt", rpc2);
		this.interfaceUrl = Commons.SOAP_URL.trim()+"/UserManage";
		UtilLog.e("ActDeactUserEvt",
				this.interfaceUrl + "----" + (this.getHandler() == null));
		this.sendRequest(rpc, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_ACTDEACTUSER, soapAction);
	}

}
