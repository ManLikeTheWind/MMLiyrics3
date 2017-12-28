/**
 * <p>
 * Copyright: Copyright (c) 2013
 * Company: ZTE
 * Description: 相册效果类的实现文件
 * </p>
 * @Title GalleryEx.java
 * @Package com.zte.iptvclient.android.androidsdk.ui
 * @version 1.0
 * @author jamesqiao10065075
 * @date 2013年8月6日
 */
package com.dxiang.mring3.view.gallery;


import android.content.Context;
import android.graphics.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;


/** 
 * 相册效果基类
 * @ClassName:GalleryEx 
 * @Description: 相册效果基类，设置基本的行为，例如滑动时的距离通知，透明、渐变、子项大小，并提供一个相机。
 * @author: jamesqiao10065075
 * @date: 2013年8月6日
 *  
 */
public class GalleryEx extends Gallery
{
    /** 日志标签 */
    public static final String LOG_TAG = "GalleryEx";

    /** 照相机视角模拟器 */
    protected Camera mCamera = new Camera();

    /**屏幕X轴方向上的中心位置*/
    protected int miGalleryCenterX = 0;

    /** 子项宽度 */
    protected int miChildWidth = 0;
    /** 子项高度 */
    protected int miChildHeight = 0;

    /** 半屏能够显示的图片个数，是一屏数目除以2，如果有余则加1，默认为一屏3张。 */
    protected int miHalfScreenCount = 2;

    /** 子项最大透明度值，0为完全透明，255为不透明 */
    protected int miChildMaxAlpha = 255;
    /** 子项最小透明度值，0为完全透明，255为不透明 */
    protected int miChildMinAlpha = 125;
    /**滑动速度比例 */
    protected int miReversedFlingSpeed =11;
    /** 是否禁止滑动 */
    protected boolean mbNoFling = false;
    /** 界面效果监听器实例 */
    protected IUIEffectListener minstanceEffectListener = null;

    /**
     * 构造函数
     * @param context 上下文
     */
    public GalleryEx(Context context)
    {
        super(context);

        this.setStaticTransformationsEnabled(true);
        setChildrenDrawingOrderEnabled(true);
    }

    /**
     * 构造函数
     * @param context 上下文
     * @param attrs 属性
     */
    public GalleryEx(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        this.setStaticTransformationsEnabled(true);
        setChildrenDrawingOrderEnabled(true);
    }

    /**
     * 构造函数
     * @param context 上下文
     * @param attrs 属性
     * @param defStyle 样式
     */
    public GalleryEx(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        this.setStaticTransformationsEnabled(true);
        setChildrenDrawingOrderEnabled(true);
    }

    /**
     * 
     * 设置子项透明度范围
     * <p>
     * Description: 设置子项透明度范围（0-255，0为完全透明，255为完全不透明），默认最小为125，最大为255。
     * <p>
     * @date 2013年7月25日 
     * @author jamesqiao10065075
     * @param iMinAlpha 子项最小透明度（0-255）
     * @param iMaxAlpha 子项最大透明度（0-255），必须大于iMinAlpha。
     */
    public void setChildAlphaRange(int iMinAlpha, int iMaxAlpha)
    {
        if (iMinAlpha >= 0 && iMinAlpha <= 255 && iMaxAlpha >= 0 && iMaxAlpha <= 255
            && iMinAlpha < iMaxAlpha)
        {
            this.miChildMinAlpha = iMinAlpha;
            this.miChildMaxAlpha = iMaxAlpha;
        }
        else
        {
            Log.w(LOG_TAG, "Invalid param!Range:0-255,iMinAlpha=" + iMinAlpha
                + ",iMaxAlpha=" + iMaxAlpha);
        }
    }

    /**
     * 
     * 获取子项最小透明度
     * <p>
     * Description: 获取子项最小透明度
     * <p>
     * @date 2013年7月25日 
     * @author jamesqiao10065075
     * @return 子项最小透明度
     */
    public int getChildMinAlpha()
    {
        return miChildMinAlpha;
    }

    /**
     * 
     * 获取子项最大透明度
     * <p>
     * Description: 获取子项最大透明度
     * <p>
     * @date 2013年7月25日 
     * @author jamesqiao10065075
     * @return 子项最大透明度
     */
    public int getChildMaxAlpha()
    {
        return miChildMaxAlpha;
    }

    /**
     * 
     * 设置一屏显示海报的张数
     * <p>
     * Description: 设置一屏显示海报的张数，默认为3张。
     * <p>
     * @date 2013-7-13 
     * @author jamesqiao10065075
     * @param iCount 一屏显示海报的张数，必须大于0。
     */
    public void setDisplayImageCount(int iCount)
    {
        if (iCount > 0)
        {
            miHalfScreenCount = iCount / 2 + (((iCount % 2) != 0) ? 1 : 0);
            Log.d(LOG_TAG, "miHalfScreenCount=" + miHalfScreenCount);
        }
        else
        {
            Log.w(LOG_TAG, "Invalid iCount=" + iCount);
        }
    }

    /**
     * 获取屏幕在水平方向上中心点的位置
     * @date 2012-2-11 
     * @return 水平方向的中心点像素
     */
    protected int getCenterXOfGallery()
    {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    /**
     * 获取视图的中心点在屏幕上X轴的像素
     * @date 2012-2-11 
     * @param view 目标视图
     * @return 视图的中心点在屏幕上X轴的像素
     */
    protected int getCenterXOfView(View view)
    {
        return view.getLeft() + view.getWidth() / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        miGalleryCenterX = getCenterXOfGallery();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected int getChildDrawingOrder(int iChildCount, int i)
    {
        int iSelectedIndex = getSelectedItemPosition() - getFirstVisiblePosition();
        if (iSelectedIndex < 0)
        {
            return i;
        }

        if (i < iSelectedIndex)
        {
            return i;
        }
        else
        //if (i >= iSelectedIndex)
        {
            return iChildCount - 1 - i + iSelectedIndex;
        }
    }

    /**
     * 
     * 设置界面效果监听器
     * <p>
     * Description: 设置界面效果监听器，例如渐变、透明等。
     * <p>
     * @date 2013年7月25日 
     * @author jamesqiao10065075
     * @param instance 界面效果监听器实例
     */
    public void setUIEffectListener(IUIEffectListener instance)
    {
        minstanceEffectListener = instance;
    }

    /**
     * 
     * 获取子项宽度
     * <p>
     * Description: 获取子项宽度
     * <p>
     * @date 2013年7月19日 
     * @author jamesqiao10065075
     * @return 子项宽度
     */
    public int getChildWidth()
    {
        return miChildWidth;
    }

    /**
     * 
     * 获取子项高度
     * <p>
     * Description: 获取子项高度
     * <p>
     * @date 2013年7月19日 
     * @author jamesqiao10065075
     * @return 子项高度
     */
    public int getChildHeight()
    {
        return miChildHeight;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY)
    {
    	int kEvent;
        if (isScrollingLeft(e1, e2)) {
            // Check if scrolling left
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        } else {
           // Otherwise scrolling right
           kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return true;
    }
    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();
   }
    /**
     * 
     * 设置是否禁止惯性滑动
     * <p>
     * Description: 设置是否禁止惯性滑动
     * <p>
     * @date 2013年9月18日 
     * @author jamesqiao10065075
     * @param bSetNoFling 是否禁止惯性滑动
     */
    public void setNoFling(boolean bSetNoFling)
    {
        mbNoFling = bSetNoFling;
    }

    /**
     * 
     * 设置滑动速度比例
     * <p>
     * Description: 比例为1速度最快，数值越大滑动越慢
     * <p>
     * @date 2013年9月13日 
     * @author Administrator
     * @param reversedFlingSpeed  滑动速度比例
     */
    public void setReversedFlingSpeed(int reversedFlingSpeed)
    {
        if (reversedFlingSpeed != 0)
        {
            this.miReversedFlingSpeed = reversedFlingSpeed;
        }
        else
        {
            Log.w(LOG_TAG, "invalid param,reversedFlingSpeed=" + reversedFlingSpeed);
        }
    }

    /**
     * 
     * 子项移动的通知方法，由子类实现
     * <p>
     * Description: 子项移动的通知方法，由子类实现
     * <p>
     * @date 2013年8月6日 
     * @author jamesqiao10065075
     * @param iChildDistance2GalleryCenter 子项与中心点的距离
     */
    protected void onChildMoved(Transformation t, int iChildDistance2GalleryCenter)
    {

    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t)
    {
        //取得当前子view的半径值
        final int iChildCenter = getCenterXOfView(child);
        //子视图的宽度
        miChildWidth = child.getMeasuredWidth();
        //子视图的宽度
        miChildHeight = child.getMeasuredHeight();

        //重置转换状态
        t.clear();
        //设置转换类型
        t.setTransformationType(Transformation.TYPE_MATRIX);

        //子项中心距离Gallery中间的偏差距离
        int iChildDistance2GalleryCenter = Math.abs(iChildCenter - miGalleryCenterX);
        Log.d(LOG_TAG, "iChildDistance2GalleryCenter=" + iChildDistance2GalleryCenter);

        //如果图片在中心位置，则需要进行放大缩小
        if (iChildDistance2GalleryCenter < 2)
        {
            if (null != minstanceEffectListener)
            {
                minstanceEffectListener.onSetAlpha(child, miChildMaxAlpha);
            }

            onChildMoved(t, iChildDistance2GalleryCenter);
        }
        else
        {
            //超过1张距离，保持原有大小
            if (iChildDistance2GalleryCenter >= miChildWidth)
            {
                if (null != minstanceEffectListener)
                {
                    minstanceEffectListener.onSetAlpha(child, miChildMinAlpha);
                }
                onChildMoved(t, iChildDistance2GalleryCenter);
            }
            else
            //根据距离远近进行等比例缩放
            {
                if (null != minstanceEffectListener)
                {
                    int iAlpha = miChildMinAlpha + (miChildMaxAlpha - miChildMinAlpha)
                        - iChildDistance2GalleryCenter
                        * (miChildMaxAlpha - miChildMinAlpha) / (miChildWidth);
                    minstanceEffectListener.onSetAlpha(child, iAlpha);
                }

                onChildMoved(t, iChildDistance2GalleryCenter);
            }
        }

        return true;
    }

    /**
     * 
     * 特效接口类
     * @ClassName:onSetAlphaListener 
     * @Description: 特效接口类
     * @author: jamesqiao10065075
     * @date: 2013年7月25日
     *
     */
    public interface IUIEffectListener
    {
        /**
         * 
         * 设置子项的透明度
         * <p>
         * Description: 设置子项的透明度
         * <p>
         * @date 2013年7月25日
         * @param iAlpha 透明度（0-255）
         */
        public void onSetAlpha(View vewChild, int iAlpha);
    }
}
