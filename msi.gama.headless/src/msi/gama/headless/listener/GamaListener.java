package msi.gama.headless.listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import msi.gama.headless.common.Globals;
import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.headless.runtime.Application; 
import ummisco.gama.network.websocket.WebSocketPrintStream;

public class GamaListener {
	/** The instance. */
	private GamaWebSocketServer instance;
	/** The simulations. */
	final private ConcurrentHashMap<String, ConcurrentHashMap<String, ManualExperimentJob>> launched_experiments = new ConcurrentHashMap<String, ConcurrentHashMap<String, ManualExperimentJob>>();
	public ConcurrentHashMap<String, ConcurrentHashMap<String, ManualExperimentJob>> getLaunched_experiments() {
		return launched_experiments;
	}

	private static WebSocketPrintStream bufferStream;

	public GamaListener(final int p, final Application a, final boolean secure) {
		File currentJavaJarFile = new File(
				GamaListener.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();

		Globals.TEMP_PATH = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "") + "../temp";

		Globals.IMAGES_PATH = Globals.TEMP_PATH + "\\snapshot";
//		File f = new File(Globals.TEMP_PATH);
//		deleteFolder(f);
//		// check if the directory can be created
//		// using the abstract path name
//		if (f.mkdir()) {
//			System.out.println("TEMP Directory is created");
//		} else {
//			System.out.println("TEMP Directory cannot be created");
//		}
		createSocketServer(p, a, secure);
	}

	void deleteFolder(File file) {
		if (file.listFiles() != null) {
			for (File subFile : file.listFiles()) {
				if (subFile.isDirectory()) {
					deleteFolder(subFile);
				} else {
					subFile.delete();
				}
			}
			file.delete();
		}
	} 
	
	/**
	 * Creates the socket server.
	 *
	 * @throws UnknownHostException the unknown host exception
	 */
	public void createSocketServer(final int port, final Application a, final boolean ssl) {
		instance = new GamaWebSocketServer(port, a, this, ssl);
		instance.start();
//		System.out.println("Gama Listener started on port: " + instance.getPort());
		bufferStream = new WebSocketPrintStream(System.out, instance);
		// System.setOut(bufferStream);

		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
		try {

			while (true) {
//				String in = sysin.readLine();
////				instance.broadcast(in);
//				if ("exit".equals(in)) {
//					instance.stop(1000);
//					break;
//				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ConcurrentHashMap<String, ConcurrentHashMap<String, ManualExperimentJob>> getAllExperiments() {
		return launched_experiments;
	}

	public ConcurrentHashMap<String, ManualExperimentJob> getExperimentsOf(final String socket) {
		return launched_experiments.get(socket);
	}

	public ManualExperimentJob getExperiment(final String socket, final String expid) {
		if (launched_experiments.get(socket) == null)
			return null;
		return launched_experiments.get(socket).get(expid);
	}

}
