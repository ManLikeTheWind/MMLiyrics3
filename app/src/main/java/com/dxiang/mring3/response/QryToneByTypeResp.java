package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.utils.Utils;

public class QryToneByTypeResp extends EntryP
{
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	private int code = 0;
	
	public QryToneByTypeResp(int co)
	{
		synchronized (this) {
			returnKey = "qryToneByTypeReturn";
			mToneInfos = new ArrayList<ToneInfo>();
			code = co;
		}
	}	
	
	private List<ToneInfo> mToneInfos;
	
    @Override
    public EntryP getEntryP(SoapObject result) 
    {
    	super.getEntryP(result);
    	synchronized (this)
    	{
    		Vector object = (Vector) resultObject.getProperty("toneInfos");
    		if(object != null && object.size() > 0)
    		{
    			int count = object.size();
    			for(int i = 0; i < count; i++)
    			{
    				SoapObject temp = (SoapObject) object.get(i);
    				String singerName = Utils.reSoapObjectStr(temp, "singerName");
    				String toneName = Utils.reSoapObjectStr(temp, "toneName");
    				if(!singerName.equalsIgnoreCase("string{}")&&!toneName.equalsIgnoreCase("string{}"))
    				mToneInfos.add(Utils.getToneInfos(temp));
    			}
    		}
    		return this;
		}
    }

	public List<ToneInfo> getmToneInfos() {
		return mToneInfos;
	}

	public void setmToneInfos(List<ToneInfo> mToneInfos) {
		this.mToneInfos = mToneInfos;
	}
}
