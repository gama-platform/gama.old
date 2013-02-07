package msi.gama.headless.executor;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class BatchExecutor {

	/**
	 * @param args
	 */
	int coreThreadSize = 4;
	ExecutorService executorService;

	public BatchExecutor() {
		coreThreadSize = getSatisfiedThreads();
		executorService =
			new ThreadPoolExecutor(
				coreThreadSize, // core thread pool size
				coreThreadSize, // maximum thread pool size
				1, // time to wait before resizing pool
				TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(coreThreadSize, true),
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public BatchExecutor(final int coreThreadSize) {
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

	public int getCoreThreadSize() {
		return coreThreadSize;
	}

	public void setCoreThreadSize(final int coreThreadSize) {
		this.coreThreadSize = coreThreadSize;
	}

	private int getSatisfiedThreads() {
		int cpus = Runtime.getRuntime().availableProcessors();
		System.out.println("cpus :" + cpus);
		int maxThreads = cpus;
		maxThreads = maxThreads > 0 ? maxThreads : 1;
		if ( coreThreadSize >= maxThreads ) {
			coreThreadSize = maxThreads - 1;
		}
		return coreThreadSize;
	}

	public static double submitExperiment(final String path, final String inpDir,
		final String outDir, final int numberCore) {
		String newOutDir = BatchExecutor.mkDir(outDir);
		BatchExecutor batchExecutor = new BatchExecutor(numberCore);
		List<String> listInputs = BatchExecutor.listDir(inpDir);

		double beginTime = java.util.Calendar.getInstance().get(Calendar.SECOND);

		for ( String inputFile : listInputs ) {
			batchExecutor.submit(path, inputFile, newOutDir);
		}
		batchExecutor.shutdown();

		double endTime = java.util.Calendar.getInstance().get(Calendar.SECOND);
		return endTime - beginTime;
	}

	public void submit(final String path, final String inputFile, final String outDir) {
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					// if it's a file, process it
					launchCommandLineGama(path, inputFile, outDir);
				} catch (Exception ex) {
					// error management logic
				}
			}
		});
	}

	public void launchCommandLineGama(final String path, final String inpFile, String outDir) {
		outDir = outDir + File.separator + getDirName(inpFile);
		System.out.println("inpFile " + inpFile);
		System.out.println("newOutDir " + outDir);
		String os = System.getProperty("os.name");

		List<String> commands;
		if ( os.startsWith("Windows") ) {
			commands =
				new ArrayList<String>(Arrays.asList("cmd.exe", "/C", "start gamaHeadless.bat " +
					inpFile + " " + outDir));
		} else {

			commands =
				new ArrayList<String>(Arrays.asList("sh", "gamaHeadless.sh", inpFile, outDir));
		}

		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.directory(new File(path));
			pb.command(commands);
			Process process = pb.start();
			// process.
			InputStream is = process.getInputStream();
			InputStream err = process.getErrorStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line;
			System.out.printf("Output of running %s is:\n", Arrays.toString(commands.toArray()));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}

			InputStreamReader isrerr = new InputStreamReader(err);
			BufferedReader brerr = new BufferedReader(isrerr);

			System.out.printf("Error of running %s is:\n", Arrays.toString(commands.toArray()));
			while ((line = brerr.readLine()) != null) {
				System.out.println(line);
			}

			// process.
			process.destroy();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// wait for all of the executor threads to finish
	public void shutdown() {
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

	private static String mkDir(final String dir) {
		File file = new File(dir);
		if ( !file.exists() ) {
			file.mkdir();
		}
		return file.getAbsolutePath();
	}

	private static String getDirName(final String fileName) {
		File file = new File(fileName);
		String name = null;
		String s = file.getName();
		int i = s.lastIndexOf('.');
		if ( i > 0 && i < s.length() - 1 ) {
			name = s.substring(0, i);
		}
		return name;
	}

	private static ArrayList<String> listDir(final String dir) {
		ArrayList<String> listFileNames = new ArrayList<String>();

		FilenameFilter textFilter = new FilenameFilter() {

			@Override
			public boolean accept(final File dir, final String name) {
				String lowercaseName = name.toLowerCase();
				if ( lowercaseName.endsWith(".xml") ) { return true; }
				return false;
			}
		};

		File dirFile = new File(dir);
		File[] listFiles = dirFile.listFiles(textFilter);
		for ( File file : listFiles ) {
			listFileNames.add(file.getAbsolutePath());
		}
		return listFileNames;
	}

	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		String path = "/Users/langthang/Desktop/MonGAMA/eclipse";
		String inpDir = "/Users/langthang/Desktop/MonGAMA/eclipse/input";
		String outDir = "/Users/langthang/Desktop/MonGAMA/eclipse/output";
		// String outDir = "out";
		BatchExecutor.submitExperiment(path, inpDir, outDir, 4);

	}

}
