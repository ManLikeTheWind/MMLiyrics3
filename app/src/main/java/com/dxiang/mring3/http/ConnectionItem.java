package com.dxiang.mring3.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.bean.UserToneGrpsMember;
import com.dxiang.mring3.request.AddRingGroupRequest;
import com.dxiang.mring3.request.QryPlayModeRequest;
import com.dxiang.mring3.response.ActdeactUserRsp;
import com.dxiang.mring3.response.AddCallerNumberToCallerGroupResponse;
import com.dxiang.mring3.response.AddRingGroupResponse;
import com.dxiang.mring3.response.AddRingtoGroupResponse;
import com.dxiang.mring3.response.ContentBuyToneForAppRsp;
import com.dxiang.mring3.response.ContentBuyToneRsp;
import com.dxiang.mring3.response.EditPwdRsp;
import com.dxiang.mring3.response.EntryP;
import com.dxiang.mring3.response.GetImageInfoRsp;
import com.dxiang.mring3.response.GetPwdRsp;
import com.dxiang.mring3.response.GetToneListenAddrRsp;
import com.dxiang.mring3.response.GiftToneRsp;
import com.dxiang.mring3.response.ManagerCallerGroupResponse;
import com.dxiang.mring3.response.OperationRecordResponse;
import com.dxiang.mring3.response.QryPlayModeResp;
import com.dxiang.mring3.response.QryRankToneRsp;
import com.dxiang.mring3.response.QryRecommendToneResp;
import com.dxiang.mring3.response.QryRecommendedToneRsp;
import com.dxiang.mring3.response.QryToneByIdRsp;
import com.dxiang.mring3.response.QryToneByNameRsp;
import com.dxiang.mring3.response.QryToneBySingerRsp;
import com.dxiang.mring3.response.QryToneByTypeResp;
import com.dxiang.mring3.response.QryToneGrpMemberResp;
import com.dxiang.mring3.response.QryToneGrpResp;
import com.dxiang.mring3.response.QryToneSetRsp;
import com.dxiang.mring3.response.QryToneTypeAPPResp;
import com.dxiang.mring3.response.QryUserToneRsp;
import com.dxiang.mring3.response.QryValidaResp;
import com.dxiang.mring3.response.QueryCallingGrpResponse;
import com.dxiang.mring3.response.SetPlayModeRsp;
import com.dxiang.mring3.response.SetToneRsp;
import com.dxiang.mring3.response.SuggestionFeedbackRsp;
import com.dxiang.mring3.response.UnSubscribeRsp;
import com.dxiang.mring3.response.UserCallingGrpMemberResponse;
import com.dxiang.mring3.response.UserSubSerRsp;
import com.dxiang.mring3.threadPool.TaskObject;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.UtilLog;

/**
 * 联网
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("unused")
public class ConnectionItem implements TaskObject {

	/**
	 * 日志标题
	 * 
	 */
	private static final String TAG = "--ConnectionItem--";

	/**
	 * 是否已经超时
	 */
	private boolean isTimeOut = false;

	/**
	 * 是否已经被取消的标志
	 */
	private boolean canceled = false;

	/**
	 * 超时定时器
	 */
	private Timer timer;

	/**
	 * 超时定时器任务
	 */
	private TimerTask timerTask;

	/**
	 * 事件处理句柄
	 */
	private Handler handler;

	/**
	 * url地址
	 */
	private String httpUrl;

	/**
	 * 返回码
	 */
	private int responseCode;

	/**
	 * 网络状态回调接口
	 */
	private IStatusListener statusListener;

	private ImgStatusListener imgstatusListener;

	/**
	 * http连接对象
	 */
	private HttpURLConnection conn;

	/**
	 * 输入流
	 */
	private InputStream is = null;

	/**
	 * 当前的连接类型 xml请求、下载文件、下载图片
	 */
	private int connectType = FusionCode.CONNECT_TYPE_WEBSERVICE;

	/**
	 * 当前连接的请求类型(登录,......退出)
	 */
	private int requestType = 0;

	/**
	 * SD卡回调同步锁
	 */
	private Object sdSyn = new Object();

	private SoapObject rpc = null;

	int completetd = 0;

	private String soapAction;

	SoapObject result = null;

	MyAndroidHttpTransport ht = null;

	/**
	 * 默认构造方法
	 */
	public ConnectionItem() {
	}

	/**
	 * 请求下载视频构造方法
	 * 
	 * @param statusListener
	 * @param handler
	 * @param type
	 */
	public ConnectionItem(IStatusListener statusListener, Handler handler, int type) {
		this.statusListener = statusListener;
		this.handler = handler;
		this.connectType = type;
	}

	/**
	 * 取消连接任务
	 */
	public void cancelConnect() {
		canceled = true;
	}

	/**
	 * 运行联网任务
	 */
	public void runTask() {
		try {
			connetionProcess();
		} catch (SecurityException se) {
			UtilLog.e("ConnectionItem.....", "SecurityException");
			hanlderException(se);
		} catch (InterruptedIOException e) {
			UtilLog.e("ConnectionItem.....", "InterruptedIOException");
			if (isTimeOut) {
				setTimeOut(responseCode, "TIMEOUT");
			} else {
				hanlderException(e);
			}
		} catch (InterruptedException e) {
			UtilLog.e("ConnectionItem.....", "InterruptedException");
			if (isTimeOut) {
				setTimeOut(responseCode, "TIMEOUT");
			} else {
				hanlderException(e);
			}
		} catch (SocketException e) {
			// 无网络时会抛出该异常
			UtilLog.e("ConnectionItem.....", "SocketException");
			hanlderException(e);
		} catch (UnsupportedEncodingException e) {
			UtilLog.e("ConnectionItem.....", "UnsupportedEncodingException");
			setConnError(responseCode, e.getMessage());
		} catch (JSONException e) {
			UtilLog.e("ConnectionItem.....", "JSONException");
			setConnError(responseCode, e.getMessage());
		} catch (IOException e) {
			UtilLog.e("ConnectionItem.....", "IOException");
			// 服务器响应异常会抛出该异常
			hanlderException(e);
		} catch (Exception e) {
			UtilLog.e("ConnectionItem.....", "Exception");
			UtilLog.e("Exception", e.getMessage());
			// 其他异常
			hanlderException(e);
		} catch (Error e) {
			// 错误处理
			UtilLog.e("ConnectionItem.....", "Error");
			setConnError(responseCode, e.toString());
		} finally {
			// clearNet();
		}

	}

	/**
	 * 异常处理
	 * 
	 * @param exception
	 *            异常对象
	 */
	private void hanlderException(Exception exception) {
		setConnError(responseCode, exception.toString());
	}

	/**
	 * 实现了联网写读功能
	 */
	private void connetionProcess() throws Exception, Error {
		Commons.lastTime = System.currentTimeMillis();
		SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelop.bodyOut = rpc;
		// envelop.setOutputSoapObject(rpc);
		// envelop.encodingStyle = "UTF-8";
		// envelop.dotNet = false;
		envelop.setAddAdornments(false);
		envelop.setOutputSoapObject(rpc);
		envelop.encodingStyle = "UTF-8";
		envelop.dotNet = false;
		try {
			ht = new MyAndroidHttpTransport(httpUrl);
			UtilLog.e("getdata", "Call pre" + soapAction);
			ht.call(soapAction, envelop);
			UtilLog.e("getdata", "Call aftssss");
			// UtilLog.e("------", (SoapFault)envelop.getResponse()+"");
			UtilLog.e("requestType", envelop.bodyIn + "");
			result = (SoapObject) envelop.bodyIn;

			UtilLog.e("requestType", result + "" + requestType);
			int count = result.getPropertyCount();
			if (null != result) {
				readData();
			}
		} catch (Exception e) {
			UtilLog.e("connetionProcess---Exception-", e.getMessage());
			String message = e.getMessage();
			UtilLog.e(TAG, "[message]" + message);
			if (null != message && message.equals("The operation timed out")) {
			}
			handler.sendMessage(handler.obtainMessage(FusionCode.NETWORK_ERROR, requestType, 0, ""));
		} finally {
			// 关闭连接
			clearNet();
		}
	}

	/**
	 * 实现了联网写读功能
	 */
	// private void connetionProcess() throws Exception, Error {
	// SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
	// SoapEnvelope.VER11);
	// envelop.bodyOut = rpc;
	// envelop.setAddAdornments(false);
	// envelop.setOutputSoapObject(rpc);
	// envelop.encodingStyle = "UTF-8";
	// envelop.dotNet = false;
	// try {
	// ht = new MyAndroidHttpTransport(httpUrl);
	// ht.call(soapAction, envelop);
	// UtilLog.e("requestType", result + "" + " -------- "
	// + envelop.bodyIn);
	// result = (SoapObject) envelop.bodyIn;
	//
	//
	// int count = result.getPropertyCount();
	// if (null != result) {
	// readData();
	// }
	// } catch (Exception e) {
	// UtilLog.e("connetionProcess---Exception-", e.getMessage());
	// String message = e.getMessage();
	// UtilLog.e(TAG, "[message]" + message);
	// if (null != message && message.equals("The operation timed out")) {
	// }
	// handler.sendMessage(handler.obtainMessage(FusionCode.NETWORK_ERROR,
	// requestType, 0, ""));
	// } finally {
	// // 关闭连接
	// clearNet();
	// }
	// }

	/**
	 * 读网络数据
	 * 
	 * @throws IOException
	 *             IO异常类
	 * @throws InterruptedException
	 *             中断异常类
	 */
	private void readData()
			throws Exception, IOException, InterruptedException, UnsupportedEncodingException, JSONException {
		// 获取网络数据输入流
		switch (connectType) {
		// webservice请求
		case FusionCode.CONNECT_TYPE_WEBSERVICE:
			readWebServiceData();
			break;
		// 文件请求
		case FusionCode.CONNECT_TYPE_FILE:
			readFileData(conn.getContentLength());
			break;
		default:
			break;
		}
	}

	/**
	 * 读取文件数据
	 */
	/**
	 * 读取文件数据
	 */
	private void readFileData(int size) throws Exception, InterruptedException, UnsupportedEncodingException {

	}

	/**
	 * 根据不同的请求类型返回不同的ENTRY对象
	 * 
	 * @return
	 */
	// public EntryP getEntry() throws IOException
	// {
	// EntryP entry = null;
	// switch (requestType)
	// {
	// case FusionCode.REQUEST_QRYRECOMMENDTONE:
	// entry = new QryRecommendedToneRsp().getEntryP(result);
	// break;
	// case FusionCode.REQUEST_QRYRANKTONE:
	// entry = new QryRankToneRsp().getEntryP(result);
	// break;
	// case FusionCode.REQUEST_QRYTONEBYID:
	// entry = new QryToneByIdRsp().getEntryP(result);
	// break;
	// case FusionCode.REQUEST_CONTENTBUYTONE:
	// entry = new ContentBuyToneRsp().getEntryP(result);
	// break;
	// case FusionCode.REQUEST_CONTENTBUYTONEFORAPP:
	// entry = new ContentButToneForAppRsp().getEntryP(result);
	// break;
	//
	// default :
	// entry = new EntryP().getEntryP(result);
	// break;
	// }
	// return entry;
	// }

	/**
	 * 读取JSON数据
	 * 
	 * @param dataLen
	 *            数据长度
	 * @throws IOException
	 *             抛出IO异常供调用者捕捉
	 * @throws InterruptedException
	 *             抛出中断异常供调用者捕捉
	 */
	private void readWebServiceData()
			throws Exception, InterruptedException, UnsupportedEncodingException, JSONException {
		try {
			EntryP entry = getEntry();
			UtilLog.e("readXmlData", entry.getError_code() + "==" + requestType);
			switch (entry.getError_code()) {
			// 请求成功处理
			case FusionCode.NETWORK_SUCESSED:
				if (requestType > 2000) {
					handler.sendMessage(handler.obtainMessage(FusionCode.NETWORK_SUCESSED,
							FusionCode.REQUEST_QryToneByType, 0, entry));
				} else {
					handler.sendMessage(handler.obtainMessage(FusionCode.NETWORK_SUCESSED, requestType, 0, entry));
				}
				break;
			// 请求失败处理
			case FusionCode.NETWORK_SUCESSED2:
				if (requestType > 2000) {
					handler.sendMessage(handler.obtainMessage(FusionCode.NETWORK_SUCESSED,
							FusionCode.REQUEST_QryToneByType, 0, entry));
				} else {
					handler.sendMessage(handler.obtainMessage(FusionCode.NETWORK_SUCESSED2, requestType, 0, entry));
				}
				break;
			default:
				if (requestType > 2000) {
					handler.sendMessage(handler.obtainMessage(FusionCode.NETWORK_SUCESSED,
							FusionCode.REQUEST_QryToneByType, 0, entry));
				} else {
					handler.sendMessage(handler.obtainMessage(FusionCode.NETWORK_ERROR, requestType, 0, entry));
				}
				break;
			}
		} finally {
		}
	}

	/**
	 * 根据不同的请求类型返回不同的ENTRY对象
	 * 
	 * @return
	 */
	public EntryP getEntry() throws IOException {
		EntryP entry = null;
		switch (requestType) {
		case FusionCode.REQUEST_EDITRECORD:
			entry = new OperationRecordResponse().getEntryP(result);
			break;
		case FusionCode.REQUEST_EDITMANAGERCALLINGGROUP:
			entry = new AddCallerNumberToCallerGroupResponse().getEntryP(result);
			break;
		case FusionCode.REQUEST_MANAGERCALLINGGROUP:
			entry = new ManagerCallerGroupResponse().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYCALLINGGROUPMEMBER:
			entry = new UserCallingGrpMemberResponse().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYCALLINGGROUP:
			entry = new QueryCallingGrpResponse().getEntryP(result);
			break;
		case FusionCode.REQUEST_DELETERINGTOGROUP:
			entry = new AddRingtoGroupResponse().getEntryP(result);
			break;
		case FusionCode.REQUEST_ADDRINGTOGROUP:
			entry = new AddRingtoGroupResponse().getEntryP(result);
			break;
		case FusionCode.REQUEST_ADDNEWGROUP:
			entry = new AddRingGroupResponse().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYGRPMEMBER:
			entry = new QryToneGrpMemberResp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYGRP:
			entry = new QryToneGrpResp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QryToneTypeAPP:
			entry = new QryToneTypeAPPResp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QryToneByName:
			entry = new QryToneByNameRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QryToneBySinger:
			entry = new QryToneBySingerRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYCALLEDTONESETEVT:
			entry = new QryToneSetRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYCALLERTONESETEVT:
			entry = new QryToneSetRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYUSERTONEEVT:
			entry = new QryUserToneRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_USERSUBSEREVT:
			entry = new UserSubSerRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_GETPWDEVT:
			entry = new GetPwdRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_SETTONEEVT:
			entry = new SetToneRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_ACTDEACTUSER:
			entry = new ActdeactUserRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_EDITPWD:
			entry = new EditPwdRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_SUGGESTIONFEEDBACK:
			entry = new SuggestionFeedbackRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_UNSUBSCRIBE:
			entry = new UnSubscribeRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYPLAYMODE:
			entry = new QryPlayModeResp().getEntryP(result);
			break;
		case FusionCode.REQUEST_SETPLAYMODE:
			entry = new SetPlayModeRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_SETPLAYMODE_DEFAULT:
			entry = new SetPlayModeRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_SETPLAYMODE_RANDOM:
			entry = new SetPlayModeRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYRECOMMENDTONE:
			entry = new QryRecommendedToneRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYRANKTONE_WEEK:
			entry = new QryRankToneRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYRANKTONE_MONTH:
			entry = new QryRankToneRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYRANKTONE_TOTAL:
			entry = new QryRankToneRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_NEWRING:
			entry = new QryRankToneRsp().getEntryP(result);
			break;

		case FusionCode.REQUEST_QRYTONEBYID:
			entry = new QryToneByIdRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYCALLEDTONEBYID:
			entry = new QryToneByIdRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_QRYCALLERTONEBYID:
			entry = new QryToneByIdRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_CONTENTBUYTONE:
			entry = new ContentBuyToneRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_GIFTTONE:
			entry = new GiftToneRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_CONTENTBUYTONEFORAPP:
			entry = new ContentBuyToneForAppRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_GETTONELISTENADDREVT:
			entry = new GetToneListenAddrRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_SUBSCRIBEAPPEVT:
			entry = new EntryP();
			entry.returnKey = "subscribeappReturn";
			entry = entry.getEntryP(result);
			break;
		case FusionCode.REQUEST_DELTONEEVT:
			entry = new EntryP();
			entry.returnKey = "delToneReturn";
			entry = entry.getEntryP(result);
			break;
		case FusionCode.REQUEST_DELTONESETEVT:
			entry = new EntryP();
			entry.returnKey = "delToneSetReturn";
			entry = entry.getEntryP(result);
			break;
		case FusionCode.REQUEST_VERCODEEVT:
			entry = new EntryP();
			entry.returnKey = "sendVerCodeReturn";
			entry = entry.getEntryP(result);
			break;
		case FusionCode.REQUEST_SENDPWDEVT:
			entry = new EntryP();
			entry.returnKey = "sendPwdReturn";
			entry = entry.getEntryP(result);
			break;
		case FusionCode.REQUEST_GetImageInfo:
			entry = new GetImageInfoRsp().getEntryP(result);
			break;
		case FusionCode.REQUEST_Recommend:
			entry = new QryRecommendToneResp().getEntryP(result);
			break;
		case FusionCode.REQUEST_VALIDA:
			entry = new QryValidaResp().getEntryP(result);
			break;
		default:
			entry = new EntryP().getEntryP(result);
			break;
		}
		if (requestType > 2000) {
			System.out.println("--------------------getEntry()---------requestType -" + requestType);
			entry = new QryToneByTypeResp(requestType).getEntryP(result);
		}

		return entry;
	}

	/**
	 * 关闭连接
	 */
	private void clearNet() {
		synchronized (sdSyn) {
			try {
				if (result != null) {
					result = null;
				}
				if (ht != null) {
					ht.getConnection().disconnect();
				}
			} catch (Exception e) {
			} finally {
				result = null;
				ht = null;
			}
		}
	}

	/**
	 * 无网络错误处理
	 * 
	 * @param responseCode
	 *            状态码
	 * @param exception
	 *            异常信息
	 */
	private void setConnError(int responseCode, String exception) {
		// 异常信息打印，正常情况下不会打出该日志
		UtilLog.e("request-onConnError: ", exception);
		if (statusListener != null) {
			statusListener.onConnError(responseCode, exception);
		} else if (imgstatusListener != null) {
			imgstatusListener.onConnError(handler, httpUrl);
		}
	}

	/**
	 * 超时处理
	 * 
	 * @param responseCode
	 *            状态码
	 * @param exception
	 *            异常信息
	 */
	private void setTimeOut(int responseCode, String exception) {
		// 异常信息打印，正常情况下不会打出该日志
		UtilLog.e("request-setTimeOut: ", exception);
		if (statusListener != null) {
			statusListener.onTimeOut(responseCode, exception);
		} else if (imgstatusListener != null) {
			imgstatusListener.onTimeOut(handler, httpUrl);
		}
	}

	/**
	 * 设置回调接口
	 * 
	 * @param handler
	 *            回调句柄
	 * @param fusionCode
	 *            区分联网请求JSON的标志码
	 */
	public void setHandler(Handler handler, int fusionCode) {
		this.handler = handler;
	}

	/**
	 * 设置请求的url
	 * 
	 * @param httpUrl
	 *            连接地址
	 */
	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	/**
	 * 任务取消的回调接口方法
	 */
	public void onCancelTask() {
		canceled = true;
	}

	/**
	 * 获取网络连接任务对象请求的url
	 * 
	 * @return 请求的url
	 */
	public String getHttpUrl() {
		return httpUrl;
	}

	/**
	 * 设置定时器对象
	 * 
	 * @param timer
	 */
	public void setTimer(Timer timer) {
		if (timer != null) {
			this.timer = timer;
		}
	}

	/**
	 * 任务请求响应回调接口
	 * 
	 * @param code
	 *            响应通知码
	 */
	public void onTaskResponse(int code) {
		switch (code) {
		case TaskObject.RESPONSE_TIMEOUT_RUNNING:
			UtilLog.e("ConnectionItem....", "onTaskResponse=====超时========");
			isTimeOut = true;
			clearNet();
			break;
		default:
			break;
		}
	}

	/**
	 * 设置请求类型
	 */
	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	/**
	 * 设置任务的超时定时器任务对象
	 * 
	 * @param timeoutTask
	 *            定时器任务对象
	 */
	public void setTimeoutTask(TimerTask timeoutTask) {
		this.timerTask = timeoutTask;
	}

	/**
	 * 启动超时定时器
	 */
	public void startTimeoutTimer() {
		if (timer != null) {
			UtilLog.e("ConnectionItem....", "startTimeoutTimer");
			timer.schedule(timerTask, Commons.OUTTIME);

		}
	}

	/**
	 * 停止超时定时器
	 */
	public void stopTimeoutTimer() {
		if (timerTask != null) {
			timerTask.cancel();
		}
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	public SoapObject getRpc() {
		return rpc;
	}

	public void setRpc(SoapObject rpc) {
		this.rpc = rpc;
	}

	public void update(Object arg) {
		// TODO Auto-generated method stub

	}
}