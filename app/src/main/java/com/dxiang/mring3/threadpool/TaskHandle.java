package com.dxiang.mring3.threadpool;


/*******************************************************************
 * 文件描述 : 任务控制接口
 ******************************************************************/
public interface TaskHandle
{
    /**
     * 任务的初始状�?
     */
    public static final int TASK_STATE_INITIALIZE = 0;

    /**
     * 任务正在任务队列中等待执�?
     */
    public static final int TASK_STATE_WAITING = 1;

    /**
     * 任务正在执行
     */
    public static final int TASK_STATE_RUNNING = 2;

    /**
     * 任务执行完毕
     */
    public static final int TASK_STATE_FINISHED = 3;

    /**
     * 任务在任务队列中还没有被执行时被取消
     */
    public static final int TASK_STATE_CANCEL = 4;

    /**
     * 任务在任务队列中已经被执行时被取�?
     */
    public static final int TASK_STATE_CANCELRUNNING = 5;

    /**
     * 取消任务
     * @return    取消成功或失�?
     */
    public boolean cancel();

    /**
     * 获取任务对象
     * @return    任务对象
     */
    public TaskObject getTaskObject();

    /**
     * 获取任务的状�?
     * @return    任务状�?
     */
    public int getState();

}
