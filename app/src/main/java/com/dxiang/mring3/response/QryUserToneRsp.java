package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;

import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.utils.Utils;

/**
 * 获取用户铃音的response
 * 
 * @author Administrator
 * 
 */
public class QryUserToneRsp extends EntryP {

	private List<ToneInfo> toneInfos;

	public QryUserToneRsp() {
		super();
		toneInfos = new ArrayList<ToneInfo>();
		returnKey = "qryUserToneReturn";
	}

	@Override
	public QryUserToneRsp getEntryP(SoapObject result) {
		// TODO Auto-generated method stub
		super.getEntryP(result);
		Vector<SoapObject> vector = (Vector<SoapObject>) resultObject
				.getProperty("toneInfos");
		if (null != vector) {
			for (int i = 0; i < vector.size(); i++) {

				SoapObject object = vector.get(i);
				String toneName = Utils.reSoapObjectStr(object, "toneName");
				if (!toneName.equalsIgnoreCase("string{}")) {
					ToneInfo info = new ToneInfo();
					info.setCpID(Utils.reSoapObjectStr(object, "cpID"));
					info.setGiftTimes(Utils
							.reSoapObjectStr(object, "giftTimes"));
					info.setInfo(Utils.reSoapObjectStr(object, "info"));
					info.setOffset(Utils.reSoapObjectStr(object, "offset"));
					info.setPara1(Utils.reSoapObjectStr(object, "para1"));
					info.setPara2(Utils.reSoapObjectStr(object, "para2"));
					info.setPrice(Utils.reSoapObjectStr(object, "price"));
					String singerName = Utils.reSoapObjectStr(object,
							"singerName");
					if (singerName.equalsIgnoreCase("string{}")) {
						singerName = "";
					}
					info.setSingerName(singerName);
					info.setSingerNameLetter(Utils.reSoapObjectStr(object,
							"singerNameLetter"));
					info.setStatus(Utils.reSoapObjectStr(object, "status"));
					info.setToneClassID(Utils.reSoapObjectStr(object,
							"toneClassID"));
					info.setToneID(Utils.reSoapObjectStr(object, "toneID"));
					// info.setToneIndex(Utils.reSoapObjectStr(object,
					// "toneIndex"));
					// info.setToneLong(Utils.reSoapObject(object, "toneLong"));
					info.setToneName(toneName);
					info.setToneNameLetter(Utils.reSoapObjectStr(object,
							"toneNameLetter"));
					info.setTonePreListenAddress(Utils.reSoapObjectStr(object,
							"tonePreListenAddress"));
					info.setToneSize(Utils.reSoapObjectStr(object, "toneSize"));
					String tonetype = Utils.reSoapObjectStr(object, "toneType");
					info.setToneType(tonetype);
					info.setToneValidDay(Utils.reSoapObjectStr(object,
							"toneValidDay"));
					info.setUpdateTime(Utils.reSoapObjectStr(object,
							"updateTime"));
					info.setUseTimes(Utils.reSoapObjectStr(object, "useTimes"));
					if (tonetype.equalsIgnoreCase("1"))
						toneInfos.add(info);
				}
			}
		}
		return this;
	}

	public List<ToneInfo> getToneList() {
		return toneInfos;
	}

	public void setToneList(List<ToneInfo> toneInfos) {
		this.toneInfos = toneInfos;
	}

}
