package com.dxiang.mring3.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;

import com.dxiang.mring3.bean.ToneTypeAPPInfo;
import com.dxiang.mring3.utils.Utils;

public class QryToneTypeAPPResp extends EntryP
{
	private List<ToneTypeAPPInfo > mToneInfos;
	
	public QryToneTypeAPPResp()
	{
        returnKey = "qryToneTypeAPPReturn";		
	}
	
    @Override
    public EntryP getEntryP(SoapObject result) 
    {
    	super.getEntryP(result);
    	mToneInfos = new ArrayList<ToneTypeAPPInfo>();
    	Vector vector = (Vector) resultObject.getProperty("toneTypeInfos");
    	if(vector != null)
    	{
    		int count = vector.size();
    		for(int i = 0; i < count; i++)
    		{
    			SoapObject object = (SoapObject) vector.get(i);
    			if(object == null)
    			{
    				continue;
    			}
    			ToneTypeAPPInfo info = new ToneTypeAPPInfo();
    			try 
    			{
    				String nod = Utils.reSoapObjectStr(object, "ifLeafNod");
    				String parentId = Utils.reSoapObjectStr(object, "parentTypeID");
    				String toneTypeId = Utils.reSoapObjectStr(object, "toneTypeID");
    				String toneTypeLabel = Utils.reSoapObjectStr(object, "toneTypeLabel");
    				String picURL = Utils.reSoapObjectStr(object, "picURL");
    				info.setToneTypeID(toneTypeId);
    				info.setParentTypeID(parentId);
    				info.setIfLeafNod(nod);
    				info.setToneTypeLabel(toneTypeLabel);
    				info.setPicURL(picURL);
				} 
    			catch (Exception e)
				{
    				
				}
    			mToneInfos.add(info);
    		}
    	}
    		
    	return this;
    }

	public List<ToneTypeAPPInfo> getmToneInfos() {
		return mToneInfos;
	}

	public void setmToneInfos(List<ToneTypeAPPInfo> mToneInfos) {
		this.mToneInfos = mToneInfos;
	}
}