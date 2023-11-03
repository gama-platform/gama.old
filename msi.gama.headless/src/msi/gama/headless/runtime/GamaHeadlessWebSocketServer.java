/*******************************************************************************************************
 *
 * GamaHeadlessWebSocketServer.java, in msi.gama.headless, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.server.SSLParametersWebSocketServerFactory;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.server.GamaServerExperimentConfiguration;
import msi.gama.runtime.server.GamaWebSocketServer;

/**
 * The Class GamaWebSocketServer.
 */
public class GamaHeadlessWebSocketServer extends GamaWebSocketServer {

	/** The Constant TLS. */
	static final String TLS = "TLS";

	/** The Constant JKS. */
	static final String JKS = "JKS";

	/** The Constant SUN_X509. */
	static final String SUN_X509 = "SunX509";

	/** The executor. */
	private final ThreadPoolExecutor executor;

	/** The experiments. Only used in the headless version */
	private final Map<String, Map<String, IExperimentPlan>> launchedExperiments = new ConcurrentHashMap<>();

	/**
	 * Start for headless with SSL security on
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param port
	 *            the port to which to listen to
	 * @param runner
	 *            the runner a ThreadPoolExecutor to launch concurrent experiments
	 * @param ssl
	 *            the ssl wether to use ssl or no
	 * @param jksPath
	 *            the jks path the store path
	 * @param spwd
	 *            the spwd the store password
	 * @param kpwd
	 *            the kpwd the key password
	 * @param pingInterval
	 *            the ping interval
	 * @return the gama web socket server
	 * @date 16 oct. 2023
	 */
	public static GamaHeadlessWebSocketServer StartForSecureHeadless(final int port, final ThreadPoolExecutor runner,
			final boolean ssl, final String jksPath, final String spwd, final String kpwd, final int pingInterval) {
		GamaHeadlessWebSocketServer server =
				new GamaHeadlessWebSocketServer(port, runner, ssl, jksPath, spwd, kpwd, pingInterval);
		try {
			server.start();
			return server;
		} finally {
			server.infiniteLoop();
		}
	}

	/**
	 * Start for headless without SSL
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param port
	 *            the port to listen to
	 * @param runner
	 *            the runner
	 * @param pingInterval
	 *            the ping interval
	 * @return the gama web socket server
	 * @date 16 oct. 2023
	 */
	public static GamaHeadlessWebSocketServer StartForHeadless(final int port, final ThreadPoolExecutor runner,
			final int pingInterval) {
		GamaHeadlessWebSocketServer server =
				new GamaHeadlessWebSocketServer(port, runner, false, "", "", "", pingInterval);
		try {
			server.start();
			return server;
		} finally {
			server.infiniteLoop();
		}
	}

	/**
	 * Instantiates a new gama web socket server.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param port
	 *            the port to listen to
	 * @param runner
	 *            the runner
	 * @param ssl
	 *            the ssl
	 * @param jksPath
	 *            the jks path
	 * @param spwd
	 *            the spwd
	 * @param kpwd
	 *            the kpwd
	 * @param interval
	 *            the interval
	 * @date 16 oct. 2023
	 */
	private GamaHeadlessWebSocketServer(final int port, final ThreadPoolExecutor runner, final boolean ssl,
			final String jksPath, final String spwd, final String kpwd, final int interval) {
		super(port, interval);
		executor = runner;
		if (ssl) { configureWebSocketFactoryWithSSL(jksPath, spwd, kpwd); }
	}

	/**
	 * Configure web socket factory.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param keyStore
	 *            the jks path
	 * @param spwd
	 *            the spwd
	 * @param kpwd
	 *            the kpwd
	 * @date 16 oct. 2023
	 */
	private void configureWebSocketFactoryWithSSL(final String keyStore, final String storePassword,
			final String keyPassword) {
		// load up the key store
		KeyStore ks;
		try (FileInputStream fis = new FileInputStream(new File(keyStore))) {
			ks = KeyStore.getInstance(JKS);
			ks.load(fis, storePassword.toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(SUN_X509);
			kmf.init(ks, keyPassword.toCharArray());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(SUN_X509);
			tmf.init(ks);
			SSLContext sslContext = null;
			sslContext = SSLContext.getInstance(TLS);
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			SSLParameters sslParameters = new SSLParameters();
			sslParameters.setNeedClientAuth(false);
			this.setWebSocketFactory(new SSLParametersWebSocketServerFactory(sslContext, sslParameters));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Infinite loop.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 16 oct. 2023
	 */
	public void infiniteLoop() {
		try {
			// empty loop to keep alive the server and catch exceptions
			while (true) {}
		} catch (Exception ex) {
			ex.printStackTrace(); // will be broadcasted to every client
		}
	}

	@Override
	public void onClose(final WebSocket socket, final int code, final String reason, final boolean remote) {
		super.onClose(socket, code, reason, remote);
		String socketId = getSocketId(socket);
		if (getLaunchedExperiments().get(socketId) != null) {
			for (IExperimentPlan e : getLaunchedExperiments().get(socketId).values()) {
				e.getController().processPause(true);
				e.getController().dispose();
			}
			getLaunchedExperiments().get(socketId).clear();
		}
	}

	/**
	 * Gets the all experiments.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the all experiments
	 * @date 15 oct. 2023
	 */
	public Map<String, Map<String, IExperimentPlan>> getAllExperiments() { return launchedExperiments; }

	/**
	 * Gets the experiments of.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @return the experiments of
	 * @date 15 oct. 2023
	 */
	public Map<String, IExperimentPlan> getExperimentsOf(final String socket) {
		return launchedExperiments.get(socket);
	}

	/**
	 * Gets the experiment.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param expid
	 *            the expid
	 * @return the experiment
	 * @date 15 oct. 2023
	 */
	@Override
	public IExperimentPlan getExperiment(final String socket, final String expid) {
		if (launchedExperiments.get(socket) == null) return null;
		return launchedExperiments.get(socket).get(expid);
	}

	/**
	 * Execute.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param executionThread
	 *            the t
	 * @date 15 oct. 2023
	 */
	@Override
	public void execute(final Runnable command) {
		if (executor == null) { command.run(); }
		executor.execute(command);
	}

	/**
	 * Gets the launched experiments.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the launched experiments
	 * @date 2 nov. 2023
	 */
	public Map<String, Map<String, IExperimentPlan>> getLaunchedExperiments() { return launchedExperiments; }

	/**
	 * Adds the experiment.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socketId
	 *            the socket id
	 * @param experimentId
	 *            the experiment id
	 * @date 3 nov. 2023
	 */
	@Override
	public void addExperiment(final String socketId, final String experimentId, final IExperimentPlan plan) {
		Map<String, IExperimentPlan> exps = launchedExperiments.get(socketId);
		if (exps == null) {
			exps = new ConcurrentHashMap<>();
			launchedExperiments.put(socketId, exps);
		}
		exps.put(experimentId, plan);
	}

	/**
	 * Obtain gui server configuration.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the gama server experiment configuration
	 * @date 3 nov. 2023
	 */
	@Override
	public GamaServerExperimentConfiguration obtainGuiServerConfiguration() {
		return GamaServerExperimentConfiguration.NULL;
	}

	@Override
	public void onStart() {}

}
