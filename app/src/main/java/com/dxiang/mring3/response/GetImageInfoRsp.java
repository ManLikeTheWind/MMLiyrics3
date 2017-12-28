package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.util.Log;

import com.dxiang.mring3.bean.ImageInfo;
import com.dxiang.mring3.utils.Utils;

public class GetImageInfoRsp extends EntryP {

	 public GetImageInfoRsp()
	 {
	 returnKey = "getImageInfoReturn";
	 }

	private List<ImageInfo> infos;

	@Override
	public EntryP getEntryP(SoapObject result) {
		super.getEntryP(result);
		try {
			infos = new ArrayList<ImageInfo>();
			Vector object = (Vector) resultObject.getProperty("imageInfos");
			if (object != null && object.size() > 0) {
				int count = object.size();
				for (int i = 0; i < count; i++) {
					SoapObject temp = (SoapObject) object.get(i);
					ImageInfo info = Utils.getImageInfo(temp);
					infos.add(info);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}

		// TODO Auto-generated method stub
		return this;
	}

	public List<ImageInfo> getInfos() {
		return infos;
	}

	public void setInfos(List<ImageInfo> infos) {
		this.infos = infos;
	}

}
