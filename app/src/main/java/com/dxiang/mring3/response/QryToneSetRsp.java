package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.dxiang.mring3.bean.UserToneSettingInfo;
import com.dxiang.mring3.utils.Utils;

/**
 * 获取主被叫的response
 * 
 * @author Administrator
 * 
 */
public class QryToneSetRsp extends EntryP {

	private List<UserToneSettingInfo> infos;

	public QryToneSetRsp() {
		super();
		infos = new ArrayList<UserToneSettingInfo>();
		returnKey = "qryToneSetReturn";
	}

	@Override
	public QryToneSetRsp getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		// result.ge
		super.getEntryP(result);
		Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
				.getProperty("userToneSettingInfos");
		if (null != vector) {
			for (int i = 0; i < vector.size(); i++) {
				SoapObject object = vector.get(i);
				// UserToneSettingInfo info = new UserToneSettingInfo();
				String endTime = Utils.reSoapObjectStr(object, "endTime");
				String playEndTime = Utils.reSoapObjectStr(object,
						"playEndTime");
				String playStartTime = Utils.reSoapObjectStr(object,
						"playStartTime");
				String settingId = Utils.reSoapObjectStr(object, "settingID");
				String settingObjType = Utils.reSoapObjectStr(object,
						"settingObjType");
				String startTime = Utils.reSoapObjectStr(object, "startTime");
				String timeDesc = Utils.reSoapObjectStr(object, "timedescrip");
				String timeType = Utils.reSoapObjectStr(object, "timeType");
				String toneId = Utils.reSoapObjectStr(object, "toneID");
				String calling = Utils.reSoapObjectStr(object, "calling");
				String toneType = Utils.reSoapObjectStr(object, "toneType");
				UserToneSettingInfo settingInfo = new UserToneSettingInfo(
						settingId, settingObjType, calling, timeType,
						startTime, endTime, playStartTime, playEndTime, toneId,
						toneType, timeDesc);
				infos.add(settingInfo);
			}
		}
		return this;
	}

	public List<UserToneSettingInfo> getInfos() {
		return infos;
	}

	public UserToneSettingInfo getcallerringinfo(String groupid) {
		UserToneSettingInfo info = null;
		if (null != infos && infos.size() > 0) {
			for (int i = 0; i < infos.size(); i++) {
				UserToneSettingInfo mUserToneSettingInfo = infos.get(i);
				Log.e("mUserToneSettingInfoxxxx",
						"mUserToneSettingInfo.getCalling():"
								+ mUserToneSettingInfo.getCalling());
				if (!mUserToneSettingInfo.getCalling().equalsIgnoreCase(// callergroup集合
						"")) {
					if (mUserToneSettingInfo.getCalling().equals(groupid)) {
						info = mUserToneSettingInfo;
					}
				}
			}
		}
		return info;
	}

	public void setInfos(List<UserToneSettingInfo> infos) {
		this.infos = infos;
	}

	public UserToneSettingInfo getSettingInfo() {
		UserToneSettingInfo info = null;
		boolean havedata = false;
		if (null != infos && infos.size() > 0) {
			for (int i = 0; i < infos.size(); i++) {
				info = infos.get(i);
				Log.e("json", "info.getCalling():" + info.getCalling());
				if (info.getTimeType().equals("0")) {
//					if ("0".equals(info.getStartTime())
//							&& (!"3".equals(info.getSettingObjType()))) {
						havedata = true;
						break;
//					}
				} else {
					havedata = false;
				}
			}
		}
		if (!havedata) {
			info = null;
		}
		return info;

	}
}
