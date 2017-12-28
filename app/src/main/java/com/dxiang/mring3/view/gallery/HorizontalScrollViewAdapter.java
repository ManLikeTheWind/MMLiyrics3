package com.dxiang.mring3.view.gallery;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dxiang.mring3.R;
import com.dxiang.mring3.bean.ToneTypeAPPInfo;

public class HorizontalScrollViewAdapter
{

	private LayoutInflater mInflater;
	private List<ToneTypeAPPInfo> mDatas;

	public HorizontalScrollViewAdapter(Context context, List<ToneTypeAPPInfo> mDatas)
	{
		mInflater = LayoutInflater.from(context);
		this.mDatas = mDatas;
		
	}

	public int getCount()
	{
		if(mDatas == null)
		{
			return 0;
		}
		return mDatas.size();
	}

	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.horizon_item_layout, parent, false);
			viewHolder.mText = (TextView) convertView
					.findViewById(R.id.show_horizon_info);

			convertView.setTag(viewHolder);
		} 
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mText.setText(mDatas.get(position).getToneTypeLabel());

		return convertView;
	}

	private class ViewHolder
	{
		TextView mText;
	}

}
