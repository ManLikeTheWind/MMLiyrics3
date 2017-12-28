package com.dxiang.mring3.request;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;

import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.DESUtil;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UtilLog;

/**
 * 修改密码
 * 
 * @author humingzhuo
 * 
 */
public class EditPwd extends Request {

	public EditPwd(Handler hand) {
		this.setHandler(hand);
	}

	/**
	 * OldPwd callNumber NewPwd 
	 * 
	 * @return
	 * @throws
	 */
	public void qryEditPwd(String CallNumber, String OldPwd,
			String NewPwd) {
		String methodName = "editPwd";
		String soapAction = Commons.SOAP_NAMESPACE + "/"+methodName;
		SoapObject rpc = new SoapObject(Commons.SOAP_NAMESPACE, methodName);
		SoapObject rpc2 = new SoapObject(Commons.SOAP_NAMESPACE, "");
//		SoapObject rpc1 = new BaseSoapObject();
//		rpc1.addProperty("channelID", "0010001");
//		rpc.addProperty("inaccessInfo", rpc1);
		DESUtil encryptTool = DESUtil.getInstance();
		String newPwd="";
		String strPwd = "";
		try {
			strPwd = encryptTool.encryptString(OldPwd);
			newPwd = encryptTool.encryptString(NewPwd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		rpc2.addProperty("callNumber", CallNumber);
		rpc2.addProperty("oldPwd", strPwd);
		rpc2.addProperty("newPwd", newPwd);
		addDefaultProperty(rpc2);
		rpc.addProperty("EditPwdEvt", rpc2);
		this.interfaceUrl =  Commons.SOAP_URL.trim()+"/UserManage";
		UtilLog.e("EditPwdEvt",
				this.interfaceUrl + "----" + (this.getHandler() == null));
		this.sendRequest(rpc, FusionCode.CONNECT_TYPE_WEBSERVICE,
				FusionCode.REQUEST_EDITPWD, soapAction);
	}

}
