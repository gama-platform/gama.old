package msi.gama.headless.listener;

import java.io.File;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import msi.gama.headless.common.Globals;
import msi.gama.headless.core.GamaServerMessage;
import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.headless.core.GamaServerGUIHandler;
import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.headless.runtime.Application;
import msi.gama.runtime.GAMA;
import msi.gama.util.file.json.Jsoner;


/**
 * Class in charge of creating the socket server and handling top-level exceptions for gama-server
 *
 */
public class GamaListener {
	
	/** The instance. */
	private GamaWebSocketServer instance;
	
	/** The simulations. */
	final private ConcurrentHashMap<String, ConcurrentHashMap<String, ManualExperimentJob>> launched_experiments = new ConcurrentHashMap<String, ConcurrentHashMap<String, ManualExperimentJob>>();
	public ConcurrentHashMap<String, ConcurrentHashMap<String, ManualExperimentJob>> getLaunched_experiments() {
		return launched_experiments;
	}

	private static PrintStream errorStream;

	public GamaListener(final int p, final Application a, final boolean secure) {
		File currentJavaJarFile = new File(
				GamaListener.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();

		Globals.TEMP_PATH = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "") + "../temp";

		Globals.IMAGES_PATH = Globals.TEMP_PATH + "\\snapshot";
		GAMA.setHeadLessMode(true, new GamaServerGUIHandler()); //todo: done here and in headless simulation loader, should be refactored
		createSocketServer(p, a, secure);
	}

	//TODO: delete ?
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
		System.out.println("Gama Listener started on port: " + instance.getPort());
		
		errorStream =  new PrintStream(System.err) {
			
			@Override
			public void println(String x) {
				super.println(x);
				instance.broadcast(Jsoner.serialize(new GamaServerMessage(GamaServerMessageType.GamaServerError , x)));
			}
		};
		System.setErr(errorStream);
		
		try {

			//empty loop to keep alive the server and catch exceptions
			while (true) {
			}
			
		} catch (Exception ex) {
			ex.printStackTrace(); //will be broadcasted to every client
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
