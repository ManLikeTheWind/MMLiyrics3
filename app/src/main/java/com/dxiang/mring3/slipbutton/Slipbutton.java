package com.dxiang.mring3.slipbutton;




import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.dxiang.mring3.R;

/**
 * 自定义滑动按钮
 * @author Administrator
 *
 */
public class Slipbutton extends View implements OnTouchListener {

	private Bitmap bg_on,bg_off,slipper_btn;
	private float downX,nowX;
	private boolean onSlip=false;
	private boolean nowStatus=false;
	
	private OnChangedListener listener;
	
	public Slipbutton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public Slipbutton(Context context,AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init()
	{
		bg_on=BitmapFactory.decodeResource(getResources(),R.mipmap.off);
		bg_off=BitmapFactory.decodeResource(getResources(), R.mipmap.on);
		slipper_btn=BitmapFactory.decodeResource(getResources(), R.mipmap.slip);
		
		setOnTouchListener(this);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Matrix matrix = new Matrix();  
        Paint paint = new Paint();  
        float x = 0;  
          
        //  
        if (nowX < (bg_on.getWidth()/3)){  
            canvas.drawBitmap(bg_off, matrix, paint);//
        }else{  
            canvas.drawBitmap(bg_on, matrix, paint);//  
        }  
          
        if (onSlip) {//    
            if(nowX >= bg_on.getWidth())//
                x = bg_on.getWidth() - slipper_btn.getWidth()/2;//
            else  
                x = nowX - slipper_btn.getWidth()/2;  
        }else {  
            if(nowStatus){// 
                x = bg_on.getWidth() - slipper_btn.getWidth();  
            }else{  
                x = 0;  
            }  
        }  
          
        //
        if (x < 0 ){  
            x = 0;  
        }  
        else if(x > bg_on.getWidth() - slipper_btn.getWidth()){  
            x = bg_on.getWidth() - slipper_btn.getWidth();  
        }  
          
        //
        canvas.drawBitmap(slipper_btn, x , 0, paint); 
		
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub
		 switch(event.getAction()){  
	        case MotionEvent.ACTION_DOWN:{  
	            if (event.getX() > bg_off.getWidth() || event.getY() > bg_off.getHeight()){  
	                return false;  
	            }else{  
	                onSlip = true;  
	                downX = event.getX();  
	                nowX = downX;  
	            }  
	            break;  
	        }  
	        case MotionEvent.ACTION_MOVE:{  
	            nowX = event.getX();  
	            break;  
	        }  
	        case MotionEvent.ACTION_UP:{  
	            onSlip = false;  
	            boolean lastchoosse=nowStatus;
	            if(event.getX() >= (bg_on.getWidth()/2)){  
	                nowStatus = true;  
	                nowX = bg_on.getWidth() - slipper_btn.getWidth();  
	            }else{  
	                nowStatus = false;  
	                nowX = 0;  
	            }  
	              
	            if((listener != null)&&(lastchoosse!=nowStatus)){  
	                listener.OnChanged(Slipbutton.this, nowStatus);  
	            }  
	            break;  
	        }  
	        }  
	        //  
	        invalidate();  
	        return true;
	}
	
	/**
	 * 滑动事件回调
	 */
	public void setOnChangedListener(OnChangedListener listener){  
        this.listener = listener;  
    }
	/**
	 * 设置初始值
	 */
	public void setChecked(boolean checked){  
        if(checked){  
            nowX = bg_off.getWidth();  
        }else{  
            nowX = 0;  
        }  
        nowStatus = checked;  
    }  
	
	/**
	 * 获取当前状态值
	 */
    public boolean getChecked(){
		
		return nowStatus;
		
	}
	
	public interface OnChangedListener{
		
		public void OnChanged(Slipbutton slipbtn, boolean checkstate);
	}

}
