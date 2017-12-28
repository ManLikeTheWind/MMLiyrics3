package com.dxiang.mring3.utils;

import com.dxiang.mring3.R;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**轮播指示器配置*/
public class GetViewPagerPoint {
static ImageView[]  tips;
static ImageView imageView;
public static void creatPoint(Context context,int count,LinearLayout pointgroup){
	tips = new ImageView[count];  
    for(int i=0; i<tips.length; i++){  
        imageView = new ImageView(context);  
        imageView.setLayoutParams(new LayoutParams(30,30));  
        tips[i] = imageView;  
        if(i == 0){  
            tips[i].setBackgroundResource(R.mipmap.vpi__tab_selected_holo);
        }else{  
            tips[i].setBackgroundResource(R.mipmap.vpi__tab_unselected_holo);
        }  
          
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));  
        layoutParams.leftMargin = 5;  
        layoutParams.rightMargin = 5;  
        pointgroup.addView(imageView, layoutParams);  
    }  
}
}
