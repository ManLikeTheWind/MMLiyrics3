package com.dxiang.mring3.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.dxiang.mring3.R;
@SuppressWarnings("unused")
/**
 * 加载框
 * @author Administrator
 *
 */
public class CustomProgressDialog extends Dialog {

	private Context context = null;
    private boolean forceCloseWiddow;
    private  Animation animation;
    ImageView imageView;
     
    public CustomProgressDialog(Context context){
        super(context);
        this.context = context;
    }
     
    public CustomProgressDialog(Context context, int theme,boolean forceCloseWiddow) {
        super(context, theme);
        this.forceCloseWiddow=forceCloseWiddow;
        //getWindow().getWindowManager().
    }
  
    /**
     *
     * [Summary]
     *       setTitile 标题        
     * @param strTitle
     * @return
     *
     */
    public CustomProgressDialog setTitile(String strTitle){
        return this;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	//return super.onTouchEvent(event);
    	return false;
    }
     
    /**
     *
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public CustomProgressDialog setMessage(String strMessage){
        //TextView tvMsg = (TextView)this.findViewById(R.id.load_tv);
         
//        if (tvMsg != null){
//            tvMsg.setText(strMessage);
//        }
         
        return this;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    
        setContentView(R.layout.customprogressdialog);
        imageView = (ImageView) findViewById(R.id.load_iv);
//        animation = new RotateAnimation(0, +360, Animation.RELATIVE_TO_SELF,
//				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		// 动画开始到结束的执行时间(1000 = 1 秒)
//		animation.setDuration(5000);
//		// 动画重复次数(-1 表示一直重复)
//		animation.setRepeatCount(-1);
//		animation.setRepeatMode(Animation.INFINITE);
//		animation.setInterpolator(new LinearInterpolator());//匀速动画
//		imageView.setAnimation(animation);
//        animation.startNow();
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
    
    @Override
    public void show() 
    {
    	super.show();
    
    	try {
//    		imageView.setAnimation(animation);
//    		animation.startNow();
    		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
            animationDrawable.start();
		} catch (Exception e) {
		}
    }
    
    @Override
    public void dismiss() {
    	// TODO Auto-generated method stub
    	super.dismiss();
    }
}
