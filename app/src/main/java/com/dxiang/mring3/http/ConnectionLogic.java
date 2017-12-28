package com.dxiang.mring3.http;


import com.dxiang.mring3.threadPool.TaskQueue;


/**
 * 网络连接请求逻辑处理类
 * @author Administrator
 *
 */
public class ConnectionLogic
{
    /**
     * 单实例连接处理对象
     */
    private static ConnectionLogic instance;

    /**
     * 连接队列
     */
    private TaskQueue requestQueue;

    /**
     * 最大任务请求数
     */
    private static final int maxCount = 4;

    /**
     * 私有构造函数
     */
    private ConnectionLogic()
    {
        requestQueue = new TaskQueue(maxCount);
    }

    /**
     * 单实例对象
     * @return      单实例连接处理对象
     */
    public synchronized static ConnectionLogic getInstance()
    {
        if (instance == null)
        {
            instance = new ConnectionLogic();
        }
        return instance;
    }

    /**
     * 增加请求任务
     * @param req      请求任务
     */
    public void addRequest(ConnectionItem req)
    {
        if (requestQueue != null)
        {
            requestQueue.addTask(req);
        }
    }

    public void clearQeq()
    {
        if (requestQueue != null)
        {
            requestQueue.terminateAllThread();
        }
    }

}