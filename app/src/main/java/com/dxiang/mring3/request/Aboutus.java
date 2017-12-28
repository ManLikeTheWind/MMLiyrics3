package com.dxiang.mring3.request;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

import com.dxiang.mring3.utils.Commons;


@SuppressWarnings("unused")

public class Aboutus extends Request{
	//private String URL="http://221.226.179.185/update/mobileupdate.txt";
	private String URL="http://221.226.179.185:8080/update/mobileupdate.txt";
//	private String URL="http://221.226.179.185/update_mmh/mobileupdate.txt";
	public Aboutus(Handler hand){
		this.setHandler(hand);
	}
	
	public  JSONObject getAboutus() {
		String myString = null;
		URL aURL = null;
		String name = null;
		JSONObject json = null;
		try {
			aURL = new URL(
					Commons.UPDATEURL);
			// HttpURLConnection conn= (HttpURLConnection)aURL.openConnection();
			// conn.connect();
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(1024);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			myString = new String(baf.toByteArray(), "utf-8");
			json = new JSONObject(myString);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
}
