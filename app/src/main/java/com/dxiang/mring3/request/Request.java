package com.dxiang.mring3.request;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;

import com.dxiang.mring3.http.ConnectionItem;
import com.dxiang.mring3.http.ConnectionLogic;
import com.dxiang.mring3.http.IStatusListener;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.Encrypt;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UtilLog;
import com.dxiang.mring3.utils.Utils;

/**
 * 数据封装类 修改历史 :
 * 
 * @author Administrator
 * 
 */
public class Request implements IStatusListener {

	/**
	 * 日志类名
	 */
	// private static final String TAG = "=== Request ===";
	/**
	 * 请求地址
	 */
	public String interfaceUrl = null;

	/**
	 * 回调句柄
	 */
	private Handler handler = null;

	/**
	 * 网络对象
	 */
	private ConnectionItem ct = null;

	private String soapAction;

	private Timer timer = new Timer();

	protected final String DID = Commons.DEVICE_TYPE_CODE + "0001";

	protected String SEQ = DID;

	private String DIDPWD;

	/**
	 * 请求类型
	 */
	private int req_type = 0;

	/**
	 * 一期公共节点
	 */
	public String commonStr = "<?xml  version='1.0'  encoding='utf-8'?> <DataRoot>";

	/**
	 * 二期公共节点
	 */
	public String commonmStr = "<?xml version='1.0' encoding='utf-8'?> <data>";

	/**
	 * 默认的构造方法
	 */
	public Request() {
		Commons.SEQCODE++;
		SEQ = SEQ + getCurrPoint() + getRequestNumber();
		DIDPWD = Encrypt.getMD5String(SEQ + Commons.PASSWORD);
	}

	// InaccessInfo
	public void addDefaultProperty(SoapObject rpc) {
		SoapObject object = new SoapObject(Commons.SOAP_NAMESPACE,
				"InaccessInfo");
		// object.addProperty("DID", DID);
		// object.addProperty("SEQ", SEQ);
		// object.addProperty("DIDPWD", DIDPWD);
		// object.addProperty("role", "001");
		// object.addProperty("roleCode", "001");
		// object.addProperty("version", "1.0");
		// rpc.addProperty("inaccessInfo", object);
		object.addProperty("DID", "0080001");
		object.addProperty("SEQ", "10280002014011703153700433146");
		object.addProperty("DIDPWD", "EE01488C002FEE4BBDFA4A62F73D4F91");
		object.addProperty("role", "001");
		object.addProperty("roleCode", "CRBT001");
		object.addProperty("version", "1.0");
		rpc.addProperty("inaccessInfo", object);
	}

	protected final String getCurrPoint() {
		long time = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
		return format.format(new Date(time));
	}

	protected final String getRequestNumber() {
		String code = String.valueOf(Commons.SEQCODE);
		if (!Utils.CheckTextNull(code) || code.length() == 8) {
			return "00000000";
		}
		StringBuffer temp = new StringBuffer();
		if (code.length() != 8) {
			int size = 8 - code.length();
			for (int i = 0; i < size; i++) {
				temp.append("0");
			}
		}
		return temp.toString() + code;
	}

	/**
	 * 发送请求
	 * 
	 * @param req
	 *            抛出异常供调用者捕捉
	 */
	public void sendRequest(SoapObject rpc, int conType, int reqType,
			String soapAction) {
		req_type = reqType;
		this.soapAction = soapAction;
		if (handler == null) {
			UtilLog.e("Request....sendRequest.", "handler == null");
			return;
		}

		// rpc.addProperty("InaccessInfo", addDefaultProperty());
		ct = new ConnectionItem(this, handler, conType);
		ct.setTimer(timer);
		ct.setHttpUrl(interfaceUrl);
		ct.setRequestType(reqType);
		ct.setRpc(rpc);
		ct.setSoapAction(this.soapAction);

		/*
		 * if (rpc != null) { ct.setInfo(rpc); }
		 */
		ConnectionLogic.getInstance().addRequest(ct);
	}

	/**
	 * 取消下载
	 */
	public void cancelUpload() {
		ct.cancelConnect();
	}

	/**
	 * 设置回调句柄
	 * 
	 * @param handler
	 *            回调句柄
	 */
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * 取得回调句柄
	 * 
	 * @param handler
	 *            回调句柄
	 */
	public Handler getHandler() {
		return handler;
	}

	/**
	 * 超时处理
	 * 
	 * @param code
	 *            状态码
	 * @param message
	 *            异常信息
	 */
	public void onTimeOut(int code, String message) {
		// 显示错误
		if (this.handler != null) {
			handler.sendMessage(handler.obtainMessage(
					FusionCode.NETWORK_TIMEOUT, req_type, 0, new EntryP()));
		}
	}

	/**
	 * 网络不可用处理
	 * 
	 * @param code
	 *            状态码
	 * @param message
	 *            异常信息
	 */
	public void onConnError(int code, String message) {
		// 显示错误
		if (this.handler != null) {
			handler.sendMessage(handler.obtainMessage(FusionCode.NETWORK_ERROR,
					req_type, 0, new EntryP()));
		}
	}

	public ConnectionItem getCt() {
		return ct;
	}

}