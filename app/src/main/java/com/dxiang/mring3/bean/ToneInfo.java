package com.dxiang.mring3.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.dxiang.mring3.utils.TimeUtil;
import com.dxiang.mring3.utils.Utils;

public class ToneInfo implements Tone, Parcelable {
	private String toneID;

	private String toneName;

	private String toneNameLetter;

	private String singerName;

	private String singerNameLetter;

	private String cpID;

	private String price;

	private String updateTime;

	private String useTimes;

	private String toneValidDay;

	private String info;

	private String tonePreListenAddress;

	private String status;

	private String toneType;

	private String offset;

	private String para1;

	private String para2;

	private String giftTimes;

	private String toneSize;

	private String toneLong;

	private String toneClassID;

	private String toneIndex;

	public ToneInfo() {

	}

	public ToneInfo(Parcel in) {
		toneID = in.readString();
		toneName = in.readString();
		toneNameLetter = in.readString();
		singerName = in.readString();
		singerNameLetter = in.readString();
		cpID = in.readString();
		price = in.readString();
		updateTime = in.readString();
		toneValidDay = in.readString();
		info = in.readString();

		tonePreListenAddress = in.readString();
		status = in.readString();
		toneType = in.readString();
		offset = in.readString();
		para1 = in.readString();
		para2 = in.readString();
		giftTimes = in.readString();
		toneSize = in.readString();
		toneLong = in.readString();
		toneClassID = in.readString();
		toneIndex = in.readString();
		useTimes = in.readString();
	}

	public boolean isEmptyRecord() {
		if (!Utils.CheckTextNull(toneName) && !Utils.CheckTextNull(toneID)
				&& !Utils.CheckTextNull(toneNameLetter)
				&& Utils.CheckTextNull(singerName) && " ".equals(singerName)
				&& !Utils.CheckTextNull(singerNameLetter)
				&& !Utils.CheckTextNull(cpID) && !Utils.CheckTextNull(price)
				&& !Utils.CheckTextNull(updateTime)
				&& !Utils.CheckTextNull(toneValidDay)
				&& !Utils.CheckTextNull(info)
				&& !Utils.CheckTextNull(tonePreListenAddress)
				&& !Utils.CheckTextNull(status)
				&& !Utils.CheckTextNull(toneType)
				&& !Utils.CheckTextNull(offset) && !Utils.CheckTextNull(para1)
				&& !Utils.CheckTextNull(para2)
				&& !Utils.CheckTextNull(giftTimes)
				&& !Utils.CheckTextNull(toneSize)
				&& Utils.CheckTextNull(toneLong)
				&& Utils.CheckTextNull(toneClassID)
				&& Utils.CheckTextNull(toneIndex)
				&& !Utils.CheckTextNull(useTimes) && "0".equals(toneClassID)
				&& "0".equals(toneIndex) && "0".equals(toneLong)) {
			return true;
		}
		return false;
	}

	public String getToneID() {
		return toneID;
	}

	public void setToneID(String toneID) {
		this.toneID = toneID;
	}

	public String getToneName() {
		return toneName;
	}

	public void setToneName(String toneName) {
		this.toneName = toneName;
	}

	public String getToneNameLetter() {
		return toneNameLetter;
	}

	public void setToneNameLetter(String toneNameLetter) {
		this.toneNameLetter = toneNameLetter;
	}

	public String getSingerName() {
		return singerName;
	}

	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}

	public String getSingerNameLetter() {
		return singerNameLetter;
	}

	public void setSingerNameLetter(String singerNameLetter) {
		this.singerNameLetter = singerNameLetter;
	}

	public String getCpID() {
		return cpID;
	}

	public void setCpID(String cpID) {
		this.cpID = cpID;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getUseTimes() {
		return useTimes;
	}

	public void setUseTimes(String useTimes) {
		this.useTimes = useTimes;
	}

	public String getToneValidDay() {
		return toneValidDay;
	}

	public void setToneValidDay(String toneValidDay) {
		this.toneValidDay = toneValidDay;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTonePreListenAddress() {
		return tonePreListenAddress;
	}

	public void setTonePreListenAddress(String tonePreListenAddress) {
		this.tonePreListenAddress = tonePreListenAddress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getToneType() {
		return toneType;
	}

	public void setToneType(String toneType) {
		this.toneType = toneType;
	}

	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public String getPara1() {
		return para1;
	}

	public void setPara1(String para1) {
		this.para1 = para1;
	}

	public String getPara2() {
		return para2;
	}

	public void setPara2(String para2) {
		this.para2 = para2;
	}

	public String getGiftTimes() {
		return giftTimes;
	}

	public void setGiftTimes(String giftTimes) {
		this.giftTimes = giftTimes;
	}

	public String getToneSize() {
		return toneSize;
	}

	public void setToneSize(String toneSize) {
		this.toneSize = toneSize;
	}

	public String getToneLong() {
		return toneLong;
	}

	public void setToneLong(String toneLong) {
		this.toneLong = toneLong;
	}

	public String getToneClassID() {
		return toneClassID;
	}

	public void setToneClassID(String toneClassID) {
		this.toneClassID = toneClassID;
	}

	public String getToneIndex() {
		return toneIndex;
	}

	public void setToneIndex(String toneIndex) {
		this.toneIndex = toneIndex;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(toneID);
		dest.writeString(toneName);
		dest.writeString(toneNameLetter);
		dest.writeString(singerName);
		dest.writeString(singerNameLetter);
		dest.writeString(cpID);
		dest.writeString(price);
		dest.writeString(updateTime);
		dest.writeString(toneValidDay);
		dest.writeString(info);

		dest.writeString(tonePreListenAddress);
		dest.writeString(status);
		dest.writeString(toneType);
		dest.writeString(offset);
		dest.writeString(para1);
		dest.writeString(para2);
		dest.writeString(giftTimes);
		dest.writeString(toneSize);
		dest.writeString(toneLong);
		dest.writeString(toneClassID);
		dest.writeString(toneIndex);
		dest.writeString(useTimes);
	}

	public static final Creator<ToneInfo> CREATOR = new Creator<ToneInfo>() {
		@Override
		public ToneInfo[] newArray(int size) {
			return new ToneInfo[size];
		}

		@Override
		public ToneInfo createFromParcel(Parcel in) {
			return new ToneInfo(in);
		}
	};
}
