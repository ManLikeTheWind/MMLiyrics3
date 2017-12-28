package com.dxiang.mring3.threadpool;


import android.util.Log;


/**
 * 线程对象实现�?
 * @author Administrator
 *
 */
public class TaskThread extends Thread
{
    /**
     * 日志标志
     */
    private static final String TAG = "=TaskThread=";

    /**
     * 在线程中执行的任务对�?
     */
    private TaskQueue taskQueue = null;

    /**
     * 调用interrupt()时是否需要终止线程，如果用户取消任务或任务超时不
     * �?��终止线程
     */
    private boolean isTerminate = false;

    /**
     * 构�?函数
     * @param taskQueue    线程�?��的线程池对象  
     */
    protected TaskThread(TaskQueue taskQueue)
    {
        super();

        this.taskQueue = taskQueue;
    }

    /**
     * 线程执行体函�?
     */
    public void run()
    {
        // 增加空闲线程的计�?
        taskQueue.increaseIdleCount();

        while (true)
        {
            TaskHandleImpl task = null;
            try
            {
                // 如果当前线程个数超过了最大线程数，需要关闭当前线�?
                if (taskQueue.getThreadCount() > taskQueue.getMaxThreadCount())
                {
                    Log.i(TAG,
                        "Currently running thread already has more than the maximum number of threads");
                    break;
                }
                else
                {
                    task = taskQueue.obtainTask(this);
                }
            }
            catch (InterruptedException e)
            {
                break;
            }

            if (task != null)
            {
                // 减小空闲线程的计�?
                taskQueue.decreaseIdleCount();

                // 执行任务
                try
                {
                    TaskObject object = task.getTaskObject();
                    if (object != null)
                    {
                        // 调用任务回调接口启动超时定时�?
                        object.startTimeoutTimer();

                        // 执行任务
                        object.runTask();

                        // 设置任务的状态为执行完毕状�?
                        task.setState(TaskHandleImpl.TASK_STATE_FINISHED);

                        // 增加空闲线程的计�?
                        taskQueue.increaseIdleCount();

                        // 通知任务请求者任务执行完�?
                        object.onTaskResponse(TaskObject.RESPONSE_SUCCESS);

                        task.cancel();

                        task = null;
                    }
                }
                catch (InterruptedException e)
                {
                    if (isTerminate())
                    {
                        break;
                    }
                    else
                    {
                        // 增加空闲线程的计�?
                        taskQueue.increaseIdleCount();
                        continue;
                    }
                }
            }
            else
            {
                break;
            }
        }

        // 减小空闲线程的计�?
        taskQueue.decreaseIdleCount();

        // 从线程队列中删除线程
        taskQueue.deleteThread(this);
    }

    /**
     * 调用interrupt()时是否需要终止线�?
     * @param isTerminate    设置是否终止线程
     */
    public void setTerminate(boolean isTerminate)
    {
        synchronized (this)
        {
            this.isTerminate = isTerminate;
        }
    }

    /**
     * 判断是否�?��终止线程
     * @return    线程收到中断异常时是否关闭线程的标志
     */
    public synchronized boolean isTerminate()
    {
        return isTerminate;
    }
}
