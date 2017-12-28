package com.dxiang.mring3.http;


import android.os.Handler;


public interface ImgStatusListener
{
    /**
     * 超时处理
     * @param code    状态码
     * @param message 异常信息
     */
    public void onTimeOut(Handler handler, String url);

    /**
     * 网络不可用 
     * @param code    状态码
     * @param message 异常信息 
     */
    public void onConnError(Handler handler, String url);
}
