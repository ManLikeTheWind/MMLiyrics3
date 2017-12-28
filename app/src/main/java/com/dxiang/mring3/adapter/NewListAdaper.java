package com.dxiang.mring3.adapter;

import java.util.HashMap;
import java.util.List;

import com.dxiang.mring3.bean.Tone;
import com.dxiang.mring3.bean.ToneTypeAPPInfo;
import com.dxiang.mring3.utils.LocalMediaPlayer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NewListAdaper extends BaseAdapter {
	
	private LocalMediaPlayer mediaPlayer;// 播放器
	private boolean isPlayer = false;// 判断是否正在播放
	private HashMap<String, List<ToneTypeAPPInfo>> mCategoryTonesMap;
	private List<Tone> mAllRingTones;
	
	 public NewListAdaper(HashMap<String, List<ToneTypeAPPInfo>> mCategoryTonesMap,List<Tone> mAllRingTones) {
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
