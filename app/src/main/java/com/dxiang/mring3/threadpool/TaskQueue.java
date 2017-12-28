package com.dxiang.mring3.threadpool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.dxiang.mring3.utils.UtilLog;



/*******************************************************************
  线程池实现类
 ******************************************************************/
public class TaskQueue
{
	/**
	 * 日志标志
	 */
//	private static final String TAG = "=TaskThread=";

	/**
	 * 当前的任务队列
	 */
	private Queue<TaskHandle> tasks = null;

	/**
	 * 存储线程池中的所有的线程
	 */
	private List<TaskThread> threads = null;

	/**
	 * 记录当前所有线程的数目
	 */
	private int threadCount = 0;

	/**
	 * 记录当前空闲线程的数目
	 */
	private int idleCount = 0;

	/**
	 * 线程池中允许的最大线程数
	 */
	private int maxCount = 10;

	/**
	 * 线程队列中的线程最长的空闲等待时间
	 */
	private int maxIdleTime = 100000;

	/**
	 * 线程同步对象
	 */
	private Object syncObject = null;

	/**
	 * 构造函数
	 * @param maxCount    最大线程数
	 */
	public TaskQueue(int maxCount)
	{
		super();
		this.tasks = new LinkedList<TaskHandle>();
		this.threads = new ArrayList<TaskThread>();
		this.syncObject = new Object();
		this.maxCount = maxCount;
	}

	/**
	 * 添加任务请求
	 * @param task   要添加的任务
	 * @return    任务句柄
	 * 备注: 该函数执行添加任务请求，主要是向线程池请求执行任务的线程资源，
	 * 如果当前没有空闲的线程且线程的总数没有达到上限，则创建一个新的线程；
	 * 如果当前有空闲的线程则唤醒空闲的线程，执行任务请求
	 */
	public TaskHandle addTask(TaskObject task)
	{
		TaskHandleImpl taskHandle = null;
		if (task == null)
		{
			return taskHandle;
		}

		// 创建相应的任务控制类对象
		taskHandle = new TaskHandleImpl(this, task);

		// 创建针对该任务的定时器超时任务对象
		TimeoutTask timeoutTask = new TimeoutTask(this, taskHandle);
		task.setTimeoutTask(timeoutTask);
		
		// 是否需要新建线程标志
		boolean needNewThread = false;
		synchronized (tasks)
		{
			// 向任务队列中添加任务
			tasks.add(taskHandle);
		}

		// 设置任务的状态为等待执行状态
		taskHandle.setState(TaskHandleImpl.TASK_STATE_WAITING);

		synchronized (syncObject)
		{
			// 判读是否需要新建线程
			if (idleCount < 1 && threadCount < maxCount)
			{
				threadCount++;
				needNewThread = true;
			}
		}

		// 唤醒一个正在等待的线程或创建一个新线程去执行任务
		notifyThreadToRuning(needNewThread);

		return taskHandle;
	}

	/**
	 * 更新线程池中的最大下载任务数
	 * @param newMaxCount    新指定的最大下载任务数
	 */
	public void updateDownloadMaxCount(int newMaxCount)
	{
		boolean isNeedNewThread = false;
		// 当新指定的最大值大于原来的最大值时，最大限制数的增加值
		int increaseNum = 0;
		if (newMaxCount > maxCount)
		{
			increaseNum = newMaxCount - maxCount;
		}
		// 对最大线程数、线程总数的修正需要使用同步锁进行保护
		synchronized (syncObject)
		{
			// 更新最大下载任务数
			maxCount = newMaxCount;
		}

		// 如果最大线程控制数增加了，激活任务队列中正在等待的任务
		for (int i = 0; i < increaseNum; ++i)
		{
			// 判断是否有任务在排队等待执行
			boolean isTaskWaiting = false;
			synchronized (tasks)
			{
				if (!tasks.isEmpty())
				{
					isTaskWaiting = true;
				}
			}

			// 对最大线程数、线程总数的修正需要使用同步锁进行保护
			synchronized (syncObject)
			{
				// 判读是否需要新建线程
				if (idleCount < 1 && threadCount < maxCount && isTaskWaiting)
				{
					threadCount++;
					isNeedNewThread = true;
				}
			}

			// 如果有任务在等待，需要唤醒一个正在等待的线程或创建一个新线程去执行任务
			if (isTaskWaiting)
			{
				notifyThreadToRuning(isNeedNewThread);
			}
		}
	}

	/**
	 * 唤醒一个正在等待的线程或创建一个新线程去执行任务
	 * @param isNeedNewThread    是否需要新建线程
	 */
	private void notifyThreadToRuning(boolean isNeedNewThread)
	{
		// 如果需要创建新线程则创建一个新的线程
		if (isNeedNewThread)
		{
			TaskThread taskThread = new TaskThread(this);
			synchronized (threads)
			{
				threads.add(taskThread);
			}

			taskThread.start();
		}
		// 通知正在等待任务的线程去执行任务
		else
		{
			synchronized (tasks)
			{
				tasks.notifyAll();
			}
		}
	}

	/**
	 * 获取最大线程数
	 * @return    最大线程数
	 */
	public int getMaxThreadCount()
	{
		return maxCount;
	}

	/**
	 * 终止所有线程
	 */
	public void terminateAllThread()
	{
		try{
		TaskThread thread = null;
		TaskHandleImpl taskHandle = null;

		// 终止所有线程
		synchronized (threads)
		{
			// 销毁数据
			Iterator<TaskThread> itr = threads.iterator();
			while (itr.hasNext())
			{
				thread = itr.next();
				thread.setTerminate(true);
				thread.interrupt();
			}
		}

		// 通知上层取消任务
		synchronized (tasks)
		{
			Iterator<TaskHandle> taskItr = tasks.iterator();
			while (taskItr.hasNext())
			{
				taskHandle = (TaskHandleImpl) taskItr.next();
				taskHandle.cancel();
			}
		}
		}catch(Exception e){
			UtilLog.e("TaskQueue...........", e.getMessage() + "");
		}
	}

	/**
	 * 设置线程池中的线程的最长空闲时间
	 * 如果一个线程空闲等待超时后将强行终止该线程，释放线程占用的资源
	 * @param maxIdleTime    线程的最长空闲时间
	 */
	public void setMaxIdleTime(int maxIdleTime)
	{
		this.maxIdleTime = maxIdleTime;
	}

	/**
	 * 增加空闲线程计数
	 */
	protected void increaseIdleCount()
	{
		synchronized (syncObject)
		{
			idleCount++;
		}
	}

	/**
	 * 减小空闲线程的计数 
	 */
	protected void decreaseIdleCount()
	{
		synchronized (syncObject)
		{
			idleCount--;
		}
	}

	/**
	 * 从任务队列中获取任务
	 * @param thread    调用该方法获取任务的线程
	 * @return    返回获取到的任务对象
	 * @throws InterruptedException  当调用interrupt()终止等待的线程时会
	 *                               抛出该异常
	 */
	protected TaskHandleImpl obtainTask(TaskThread thread)
			throws InterruptedException
	{
		TaskHandleImpl task = null;
		synchronized (tasks)
		{
			if (tasks.isEmpty())
			{
				tasks.wait(maxIdleTime);
			}

			if (!tasks.isEmpty())
			{
				task = (TaskHandleImpl) tasks.poll();
			}
		}

		if (task != null)
		{
			// 保存执行任务的线程对象
			task.setTaskThread(thread);

			// 设置任务的状态为正在执行态
			task.setState(TaskHandleImpl.TASK_STATE_RUNNING);
		}

		return task;
	}

	/**
	 * 从任务队列中将任务移除，放弃该任务的执行
	 * @param taskHandle   需要移除的任务
	 * @return    删除任务成功或失败标志
	 * 备注: 任务超时后，如果任务还没有执行，即还在任务队列中则从任务队列中删除；
	 * 任务在任务队列中还没有被执行时，用户取消任务也将从任务队列中删除任务
	 */
	protected boolean removeTask(TaskHandleImpl taskHandle)
	{
		boolean ret = false;
		if (taskHandle == null)
		{
			return ret;
		}

		synchronized (tasks)
		{
			if (!tasks.isEmpty())
			{
				ret = tasks.remove(taskHandle);
			}
		}
		taskHandle.setState(TaskHandleImpl.TASK_STATE_CANCEL);

		return ret;
	}

	/**
	 * 终止任务执行
	 * @param taskThread    执行任务的线程对象
	 * @param taskHandle    要终止的任务句柄
	 * @return              终止任务成功或失败
	 * 备注: 任务超时或者用户取消了任务，如果任务正在执行，则终止该任务的执行，
	 * 此时线程不终止，继续执行下一个任务  
	 */
	protected boolean terminateTaskRunning(TaskThread taskThread,
			TaskHandleImpl taskHandle)
	{
		boolean ret = false;
		if (taskThread == null)
		{
			return ret;
		}
		if (taskHandle != null)
		{
			taskHandle.setState(TaskHandle.TASK_STATE_CANCELRUNNING);
		}
		taskThread.setTerminate(false);
		taskThread.interrupt();
		ret = true;

		return ret;
	}

	/**
	 * 删除线程
	 * @param taskThread    要删除的线程
	 * 备注: 线程结束后需要将线程计数减一，同时从线程队列中移除
	 */
	protected void deleteThread(TaskThread taskThread)
	{
		synchronized (syncObject)
		{
			threadCount--;
		}
		synchronized (threads)
		{
			threads.remove(taskThread);
		}
		taskThread = null;
	}

	/**
	 * 获取线程总数
	 * @return    线程总数
	 */
	public int getThreadCount()
	{
		return threadCount;
	}

	/**
	 * 获取空闲线程的个数
	 * @return    空闲线程的个数
	 */
	public int getIdleCount()
	{
		return idleCount;
	}

}
