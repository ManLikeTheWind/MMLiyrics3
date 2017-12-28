package com.dxiang.mring3.bean;

public class ToneTypeAPPInfo implements Tone
{
    private String toneTypeID;
    
    private String toneTypeLabel;
    
    private String parentTypeID;
    
    private String ifLeafNod;
    
    private String picURL;

	public String getToneTypeID() 
	{
		return toneTypeID;
	}

	public void setToneTypeID(String toneTypeID) 
	{
		this.toneTypeID = toneTypeID;
	}

	public String getToneTypeLabel() 
	{
		return toneTypeLabel;
	}

	public void setToneTypeLabel(String toneTypeLabel) 
	{
		this.toneTypeLabel = toneTypeLabel;
	}

	public String getParentTypeID() 
	{
		return parentTypeID;
	}

	public void setParentTypeID(String parentTypeID) 
	{
		this.parentTypeID = parentTypeID;
	}

	public String getIfLeafNod() 
	{
		return ifLeafNod;
	}

	public void setIfLeafNod(String ifLeafNod) 
	{
		this.ifLeafNod = ifLeafNod;
	}

	public String getPicURL() 
	{
		return picURL;
	}

	public void setPicURL(String picURL) 
	{
		this.picURL = picURL;
	}
}
