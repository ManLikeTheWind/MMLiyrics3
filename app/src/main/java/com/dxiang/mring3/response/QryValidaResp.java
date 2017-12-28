package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.dxiang.mring3.bean.ImageInfo;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.utils.Utils;

public class QryValidaResp extends EntryP {
	private List<String> resultcode;

	public QryValidaResp() {
		//你这块写错了，这块不是瞎写的
		returnKey = "validaReturn";
	}

	@Override
	public EntryP getEntryP(SoapObject result) {
		super.getEntryP(result);
		return this;
	}

}
