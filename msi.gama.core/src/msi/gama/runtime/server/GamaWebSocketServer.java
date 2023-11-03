/*******************************************************************************************************
 *
 * GamaWebSocketServer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.SSLParametersWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import msi.gama.common.interfaces.IConsoleListener;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IExperimentStateListener;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.file.json.Json;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaWebSocketServer.
 */
public class GamaWebSocketServer extends WebSocketServer implements IExperimentStateListener {

	/** The Constant SOCKET_ID. */
	private static final String SOCKET_ID = "socket_id";

	/** The Constant TLS. */
	private static final String TLS = "TLS";

	/** The Constant JKS. */
	private static final String JKS = "JKS";

	/** The Constant SUN_X509. */
	private static final String SUN_X509 = "SunX509";

	/** The Constant DEFAULT_PING_INTERVAL. */
	public static final int DEFAULT_PING_INTERVAL = 10000;

	/** The current server config. */
	private GamaServerExperimentConfiguration currentServerConfig = GamaServerExperimentConfiguration.NULL;

	/** The executor. */
	private final ThreadPoolExecutor executor;

	/** The experiments. Only used in the headless version */
	private final Map<String, Map<String, IExperimentPlan>> launchedExperiments = new ConcurrentHashMap<>();

	/** The cmd helper. */
	private final CommandExecutor cmdHelper = new CommandExecutor();

	/** The can ping. false if pingInterval is negative */
	public final boolean canPing;

	/** The ping interval. the time interval between two ping requests in ms */
	public final int pingInterval;

	/** The ping timers. map of all connected clients and their associated timers running ping requests */
	protected final Map<WebSocket, Timer> pingTimers = new HashMap<>();

	/** The json err. */
	protected Json jsonErr = Json.getNew();

	/** The console. */
	protected final IConsoleListener console = new GamaServerConsoleListener();

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
	public static GamaWebSocketServer StartForSecureHeadless(final int port, final ThreadPoolExecutor runner,
			final boolean ssl, final String jksPath, final String spwd, final String kpwd, final int pingInterval) {
		GamaWebSocketServer server = new GamaWebSocketServer(port, runner, ssl, jksPath, spwd, kpwd, pingInterval);
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
	public static GamaWebSocketServer StartForHeadless(final int port, final ThreadPoolExecutor runner,
			final int pingInterval) {
		GamaWebSocketServer server = new GamaWebSocketServer(port, runner, false, "", "", "", pingInterval);
		try {
			server.start();
			return server;
		} finally {
			server.infiniteLoop();
		}
	}

	/**
	 * Start for GUI. No SSL and a default ping interval
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param port
	 *            the port
	 * @return the gama web socket server
	 * @date 16 oct. 2023
	 */
	public static GamaWebSocketServer StartForGUI(final int port) {
		return StartForGUI(port, DEFAULT_PING_INTERVAL);
	}

	/**
	 * Start for GUI.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param port
	 *            the port to which to listen to
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
	public static GamaWebSocketServer StartForGUI(final int port, final int pingInterval) {
		GamaWebSocketServer server = new GamaWebSocketServer(port, null, false, "", "", "", pingInterval);
		server.currentServerConfig = GamaServerExperimentConfiguration.GUI;
		server.start();
		return server;
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
	private GamaWebSocketServer(final int port, final ThreadPoolExecutor runner, final boolean ssl,
			final String jksPath, final String spwd, final String kpwd, final int interval) {
		super(new InetSocketAddress(port));
		executor = runner;
		canPing = interval >= 0;
		pingInterval = interval;
		if (ssl) { configureWebSocketFactoryWithSSL(jksPath, spwd, kpwd); }
		configureErrorStream();
	}

	/**
	 * Configure error stream so as to broadcast errors
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 16 oct. 2023
	 */
	private void configureErrorStream() {
		PrintStream errorStream = new PrintStream(System.err) {

			@Override
			public void println(final String x) {
				super.println(x);
				broadcast(jsonErr.valueOf(new GamaServerMessage(GamaServerMessage.Type.GamaServerError, x)).toString());
			}
		};
		System.setErr(errorStream);
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

	/**
	 * Gets the socket id.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @return the socket id
	 * @date 3 nov. 2023
	 */
	public static String getSocketId(final WebSocket socket) {
		return String.valueOf(socket.hashCode());
	}

	@Override
	public void onOpen(final WebSocket socket, final ClientHandshake handshake) {
		currentServerConfig = currentServerConfig.withSocket(socket);

		// DEBUG.OUT(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
		GAMA.getGui().getConsole().addConsoleListener(console);
		socket.send(Json.getNew().valueOf(
				new GamaServerMessage(GamaServerMessage.Type.ConnectionSuccessful, String.valueOf(socket.hashCode())))
				.toString());
		if (canPing) {
			var timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if (socket.isOpen()) { socket.sendPing(); }
				}
			}, 0, pingInterval);
			pingTimers.put(socket, timer);
		}
	}

	@Override
	public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
		var timer = pingTimers.remove(conn);
		if (timer != null) { timer.cancel(); }
		if (getLaunchedExperiments().get("" + conn.hashCode()) != null) {
			for (IExperimentPlan e : getLaunchedExperiments().get(String.valueOf(conn.hashCode())).values()) {
				e.getController().processPause(true);
				e.getController().dispose();
			}
			getLaunchedExperiments().get("" + conn.hashCode()).clear();
		}
		GAMA.getGui().getConsole().removeConsoleListener(console);
		DEBUG.OUT(conn + " has left the room!");
	}

	/**
	 * Extract param.
	 *
	 * @param socket
	 *            the socket
	 * @param message
	 *            the message
	 * @return the i map
	 */
	@SuppressWarnings ("unchecked")
	public IMap<String, Object> extractParam(final WebSocket socket, final String message) {
		IMap<String, Object> map = null;
		try {
			final Object o = Json.getNew().parse(message).toGamlValue(GAMA.getRuntimeScope());
			if (o instanceof IMap) {
				map = (IMap<String, Object>) o;
			} else {
				map = GamaMapFactory.create();
				map.put(IKeyword.CONTENTS, o);
			}
		} catch (Exception e1) {
			DEBUG.OUT(e1.toString());
			socket.send(jsonErr.valueOf(new GamaServerMessage(GamaServerMessage.Type.MalformedRequest, e1)).toString());
		}
		return map;
	}

	@Override
	public void onMessage(final WebSocket socket, final String message) {
		// DEBUG.OUT(socket + ": " + message);
		try {
			IMap<String, Object> map = extractParam(socket, message);
			map.put("server", this);
			DEBUG.OUT(map.get("type"));
			DEBUG.OUT(map.get("expr"));
			final String exp_id =
					map.get(ISocketCommand.EXP_ID) != null ? map.get(ISocketCommand.EXP_ID).toString() : "";
			final String socketId = map.get(SOCKET_ID) != null ? map.get(SOCKET_ID).toString() : getSocketId(socket);
			IExperimentPlan exp = getExperiment(socketId, exp_id);
			SimulationAgent sim = exp != null && exp.getAgent() != null ? exp.getAgent().getSimulation() : null;
			if (sim != null && exp != null && !exp.getController().isPaused()) {
				sim.postOneShotAction(scope1 -> {
					cmdHelper.pushCommand(socket, map);
					return null;
				});
			} else {
				cmdHelper.pushCommand(socket, map);
			}

		} catch (Exception e1) {
			DEBUG.OUT(e1);
			socket.send(jsonErr.valueOf(new GamaServerMessage(GamaServerMessage.Type.GamaServerError, e1)).toString());

		}
	}

	@Override
	public void onError(final WebSocket conn, final Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	@Override
	public void onStart() {
		GAMA.addExperimentStateListener(this);
		DEBUG.BANNER("GAMA: Gama Server started", "at port", "" + this.getPort());
	}

	@Override
	public void stop() throws InterruptedException {
		super.stop();
		GAMA.removeExperimentStateListener(this);
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

	@Override
	public void updateStateTo(final IExperimentPlan experiment, final State state) {
		// Does nothing for the moment (but could send messages corresponding to the clients ?)
	}

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
	public GamaServerExperimentConfiguration obtainGuiServerConfiguration() {
		return currentServerConfig;
	}

}
