package msi.gama.headless.multicore.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import msi.gama.runtime.IScope;

public class MulticoreFactory  {

	
	
	public static MulticoreRuntime loadMulticoreRuntime(IScope gamaScope)
	{
		
		
		
		return null;
	}
	
	
	
	
	
	
	
	
	/*
	
	
	
	
	
	
	
	
	static int coreThreadSize = 1;
	static ExecutorService executorService;
	static String path;

	public static void configure()
	{
		coreThreadSize = getSatisfiedThreads();
		executorService =
			new ThreadPoolExecutor(
				coreThreadSize, // core thread pool size
				coreThreadSize, // maximum thread pool size
				1, // time to wait before resizing pool
				TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(coreThreadSize, true),
				new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	public static String getPath() {
		return path;
	}
	
	public static void setPath(String path) {
		MulticoreFactory.path = path;
	}

	public int getCoreThreadSize() {
		return coreThreadSize;
	}

	public void setCoreThreadSize(int coreThreadSize) {
		this.coreThreadSize = coreThreadSize;
	}

	private static int getSatisfiedThreads() {
		int cpus = Runtime.getRuntime().availableProcessors();
		System.out.println("cpus :" + cpus);
		int maxThreads = cpus;
		maxThreads = (maxThreads > 0 ? maxThreads : 1);
		return maxThreads;
	}
	
	public static Future<MulticoreTask> submit(MulticoreTask task) {
		return executorService.submit(task, task);
	}
	
	// wait for all of the executor threads to finish
	public static void shutdown() {
		executorService.shutdown();
		try {
			if ( !executorService.awaitTermination(60, TimeUnit.SECONDS) ) {
				// pool didn't terminate after the first try
				executorService.shutdownNow();
			}

			if ( !executorService.awaitTermination(60, TimeUnit.SECONDS) ) {
				// pool didn't terminate after the second try
			}
		} catch (InterruptedException ex) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
	
	
	public static void submitOneExperiment(MulticoreRuntime mr)
	{
		System.out.println("one more task called");
		executorService = Executors.newCachedThreadPool();
		MulticoreTask task = new MulticoreTask(mr, MulticoreFactory.getPath(), mr.getInputPath(), mr.getOutputPath());
		MulticoreFactory.submit(task);
	}
*/
}
