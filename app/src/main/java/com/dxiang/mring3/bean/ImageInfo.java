package com.dxiang.mring3.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class ImageInfo {

	private String no;
	
	private String imageURL;
	private String linkURL;
	
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getLinkURL() {
		return linkURL;
	}
	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}
//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeString(no);
//		dest.writeString(imageURL);
//		dest.writeString(linkURL);
//		
//	}
	
	public ImageInfo(){
		
	}
	
	public ImageInfo(Parcel in){
		no=in.readString();
		imageURL=in.readString();
		linkURL=in.readString();
	}
//	public static final Parcelable.Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
//
//		@Override
//		public ImageInfo createFromParcel(Parcel in) {
//			// TODO Auto-generated method stub
//			return new ImageInfo(in);
//		}
//
//		@Override
//		public ImageInfo[] newArray(int size) {
//			// TODO Auto-generated method stub
//			return new ImageInfo[size];
//		}
//
//	
//	};
	
	
}
