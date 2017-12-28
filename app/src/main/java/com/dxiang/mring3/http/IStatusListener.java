package com.dxiang.mring3.http;


public interface IStatusListener
{
    /**
     * 超时处理
     * @param code    状态码
     * @param message 异常信息
     */
    public void onTimeOut(int code, String message);

    /**
     * 网络不可用 
     * @param code    状态码
     * @param message 异常信息 
     */
    public void onConnError(int code, String message);
}
