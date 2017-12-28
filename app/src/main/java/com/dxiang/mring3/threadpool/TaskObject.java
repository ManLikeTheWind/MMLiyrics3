package com.dxiang.mring3.threadpool;


import java.util.TimerTask;


/*******************************************************************
 * 文件描述 : 请求任务接口
 ******************************************************************/
public interface TaskObject
{
    /**
     * 任务请求响应通知�?   任务执行成功
     */
    public static final int RESPONSE_SUCCESS = 0;

    /**
     * 任务请求响应通知�?   任务超时（未执行�?
     */
    public static final int RESPONSE_TIMEOUT_ONRUN = 1;

    /**
     * 任务请求响应通知�?   任务超时（正在执行）
     */
    public static final int RESPONSE_TIMEOUT_RUNNING = 2;

    /**
     * 任务执行体接口方�?
     * @throws InterruptedException    抛出中断异常供调用�?捕捉 
     */
    public void runTask() throws InterruptedException;

    /**
     * 任务请求响应回调接口
     * @param code    任务请求响应通知�?
     */
    public void onTaskResponse(int code);

    /**
     * 任务取消的回调接口方法，供线程管理模块调�?
     */
    public void onCancelTask();

    /**
     * 设置任务的超时定时器任务对象
     * @param timeoutTask    定时器任务对�? 
     */
    public void setTimeoutTask(TimerTask timeoutTask);

    /**
     * 启动超时定时�?
     */
    public void startTimeoutTimer();

    /**
     * 停止超时定时�?
     */
    public void stopTimeoutTimer();

}
