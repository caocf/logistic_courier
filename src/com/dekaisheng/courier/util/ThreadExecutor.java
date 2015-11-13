package com.dekaisheng.courier.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dekaisheng.courier.util.log.Log;

public class ThreadExecutor {
	
	private String TAG = "ThreadExecutor";
	private ExecutorService executorService = null;
	private static ThreadExecutor instance = null;

	private ThreadExecutor() {
		if (this.executorService == null) {
			int count = Math.min(3, (int) (Runtime.getRuntime()
					.availableProcessors() * 1.2 + 1));
//			this.executorService = new ThreadPoolExecutor(count, count, 10000,
//					TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(50,
//							true));
			this.executorService = new ThreadPoolExecutor(count, count, 10000,
			TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		}
	}
	
	public void doTask(Runnable task){
		if(!executorService.isShutdown()){
			this.executorService.execute(task);			
		}else{
			Log.i(TAG,"ExecutorService was already shutdown!");
		}
	}
	
	public <T> Future<T> doTask(Callable<T> task){
		FutureTask<T> future = new FutureTask<T>(task);
		this.executorService.execute(future);
		return future;
	}

	public void shutdown() {
		this.executorService.shutdown();
	}

	public static ThreadExecutor defaultInstance() {
		if (instance == null)
			instance = new ThreadExecutor();
		return instance;
	}
}
