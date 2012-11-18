package msi.gama.hpc.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MulticoreFactory  {

	/**
	 * @param args
	 */
	int coreThreadSize = 1;
	static ExecutorService executorService;
	static String path;
	
	static List<Future<MulticoreTask>> futures = new ArrayList<Future<MulticoreTask>>();

	public MulticoreFactory() {
		coreThreadSize = getSatisfiedThreads();
		executorService =
			new ThreadPoolExecutor(
				coreThreadSize, // core thread pool size
				coreThreadSize, // maximum thread pool size
				1, // time to wait before resizing pool
				TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(coreThreadSize, true),
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public MulticoreFactory(int coreThreadSize) {
		this.setCoreThreadSize(coreThreadSize);
		this.coreThreadSize = getSatisfiedThreads();
		executorService =
			new ThreadPoolExecutor(
				this.coreThreadSize, // core thread pool size
				this.coreThreadSize, // maximum thread pool size
				1, // time to wait before resizing pool
				TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(this.coreThreadSize, true),
				new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	public static String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public int getCoreThreadSize() {
		return coreThreadSize;
	}

	public void setCoreThreadSize(int coreThreadSize) {
		this.coreThreadSize = coreThreadSize;
	}

	private int getSatisfiedThreads() {
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
		MulticoreTask task = new MulticoreTask(mr, MulticoreFactory.getPath(), mr.getInputPath(), mr.getOutputPath());
		
		task.startObservator();
		futures.add(MulticoreFactory.submit(task));
		
		for(Future<MulticoreTask> oneFuture : futures){
			try {
				MulticoreTask oneTask = oneFuture.get();
				System.out.println("task " + oneTask.getName()  + "finished");
				oneTask.stopObservator();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		MulticoreFactory.shutdown();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "/Users/langthang/Desktop/MonGAMA/eclipse";
		String inpDir = "/Users/langthang/Desktop/MonGAMA/eclipse/input";
		String outDir = "/Users/langthang/Desktop/MonGAMA/eclipse/output";
		// String outDir = "out";
		//MulticoreFactory.submitExperiment(path, inpDir, outDir, 4);

	}

}
