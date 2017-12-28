package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.utils.Utils;

/**
 * 查询推荐铃音返回
 * 
 * @author Administrator
 * 
 */
public class QryRecommendedToneRsp extends EntryP {
	public List<ToneInfo> list;

	private int amount;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public QryRecommendedToneRsp() {
		returnKey = "qryRecommendRingReturn";
	}

	@Override
	public EntryP getEntryP(SoapObject result) {
		super.getEntryP(result);

		list = new ArrayList<ToneInfo>();
		try {
			amount = Integer.parseInt(resultObject.getProperty("amount")
					.toString());
			Vector toneInfos = (Vector) resultObject.getProperty("toneInfos");
			for (int i = 0; i < toneInfos.size(); i++) {
				SoapObject obj = (SoapObject) toneInfos.get(i);
				String singerName = Utils.reSoapObjectStr(obj, "singerName");
				if (!singerName.equalsIgnoreCase("string{}")) {
					ToneInfo info = new ToneInfo();
					info.setCpID(Utils.reSoapObjectStr(obj, "cpID"));
					info.setGiftTimes(Utils.reSoapObjectStr(obj, "giftTimes"));
					info.setInfo(Utils.reSoapObjectStr(obj, "info"));
					info.setOffset(Utils.reSoapObjectStr(obj, "offset"));
					info.setPara1(Utils.reSoapObjectStr(obj, "para1"));
					info.setPara2(Utils.reSoapObjectStr(obj, "para2"));
					info.setPrice(Utils.reSoapObjectStr(obj, "price"));
					info.setSingerName(singerName);
					info.setStatus(Utils.reSoapObjectStr(obj, "status"));
					info.setToneClassID(Utils.reSoapObjectStr(obj,
							"toneClassID"));
					info.setToneID(Utils.reSoapObjectStr(obj, "toneID"));
					info.setToneIndex(Utils.reSoapObjectStr(obj, "toneIndex"));
					info.setToneLong(Utils.reSoapObjectStr(obj, "toneLong"));
					info.setToneName(Utils.reSoapObjectStr(obj, "toneName"));
					info.setTonePreListenAddress(Utils.reSoapObjectStr(obj,
							"tonePreListenAddress"));
					info.setToneNameLetter(Utils.reSoapObjectStr(obj,
							"toneNameLetter"));
					info.setToneSize(Utils.reSoapObjectStr(obj, "toneSize"));
					info.setToneType(Utils.reSoapObjectStr(obj, "toneType"));

					info.setToneValidDay(Utils.reSoapObjectStr(obj,
							"toneValidDay"));
					info.setUpdateTime(Utils.reSoapObjectStr(obj, "updateTime"));
					info.setUseTimes(Utils.reSoapObjectStr(obj, "useTimes"));
					if (!info.isEmptyRecord()) {
						info.setToneType("1");
						list.add(info);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}

		return this;
	}

}
