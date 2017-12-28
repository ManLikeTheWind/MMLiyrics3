package com.dxiang.mring3.file;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author C
 * 
 */
public class SharedPreferenceService {

	SharedPreferences sp;
	SharedPreferences.Editor editor;

	private static SharedPreferenceService manager = null;

	private SharedPreferenceService(Context c) {
		sp = c.getSharedPreferences("setting", 0);
		editor = sp.edit();
	}

	public static SharedPreferenceService getInstance(Context c) {
		if (manager == null) {
			manager = new SharedPreferenceService(c);
		}
		return manager;
	}

	public void put(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void put(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void put(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public String get(String key, String defValue) {
		return sp.getString(key, defValue);
	}

	public int get(String key, int defValue) {
		return sp.getInt(key, defValue);
	}

	public boolean get(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}
	
	public long getLong(String key, long defValue) {
		return sp.getLong(key, defValue);
	}
	
	public void putLong(String key, long value)
	{
		editor.putLong(key, value);
		editor.commit();
	}

	public void clear() {
		editor.clear();
		editor.commit();
	}
}
