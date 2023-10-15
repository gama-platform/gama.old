/*******************************************************************************************************
 *
 * GamaWebSocketServer.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
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
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.SSLParametersWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.file.json.Jsoner;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaWebSocketServer.
 */
public class GamaWebSocketServer extends WebSocketServer {

	/** The executor. */
	private final ThreadPoolExecutor executor;

	/** The simulations. */
	final private ConcurrentHashMap<String, ConcurrentHashMap<String, IExperimentPlan>> launchedExperiments =
			new ConcurrentHashMap<>();

	/** The app. */
	// private final Application app;

	/** The cmd helper. */
	CommandExecutor cmdHelper;

	/** The can ping. */
	// variables for the keepalive pings
	public final boolean canPing; // false if pingInterval is negative

	/** The ping interval. */
	public final int pingInterval; // the time interval between two ping requests in ms

	/** The ping timers. */
	protected Map<WebSocket, Timer> pingTimers; // map of all connected clients and their associated timers running ping
												// requests

	/**
	 * Instantiates a new gama web socket server.
	 *
	 * @param port
	 *            the port
	 * @param a
	 *            the a
	 * @param l
	 *            the l
	 * @param ssl
	 *            the ssl
	 */
	public GamaWebSocketServer(final int port, final ThreadPoolExecutor runner, final boolean ssl, final String jksPath,
			final String spwd, final String kpwd, final int ping_interval) {
		super(new InetSocketAddress(port));
		executor = runner;
		canPing = ping_interval >= 0;
		pingInterval = ping_interval;
		pingTimers = new HashMap<>();

		// if (a.verbose) { DEBUG.ON(); }
		cmdHelper = new CommandExecutor();
		if (ssl) {
			// load up the key store
			String STORETYPE = "JKS";
			// File currentJavaJarFile =
			// new File(GamaListener.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			// String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();
			// String KEYSTORE = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "") + "/../keystore.jks";
			String KEYSTORE = jksPath;// "/Users/hqn88/git/gama.client/server/.cert/cert.jks";
			String STOREPASSWORD = spwd;// "abcdef";
			String KEYPASSWORD = kpwd;// "abcdef";

			KeyStore ks;
			try (FileInputStream fis = new FileInputStream(new File(KEYSTORE))) {
				ks = KeyStore.getInstance(STORETYPE);

				ks.load(fis, STOREPASSWORD.toCharArray());

				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, KEYPASSWORD.toCharArray());
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				tmf.init(ks);

				SSLContext sslContext = null;
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				SSLParameters sslParameters = new SSLParameters();

				sslParameters.setNeedClientAuth(false);
				this.setWebSocketFactory(new SSLParametersWebSocketServerFactory(sslContext, sslParameters));
				// this.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		// app = a;
		System.out.println("Gama Listener started on port: " + getPort());

		PrintStream errorStream = new PrintStream(System.err) {

			@Override
			public void println(final String x) {
				super.println(x);
				broadcast(Jsoner.serialize(new GamaServerMessage(GamaServerMessage.Type.GamaServerError, x)));
			}
		};
		System.setErr(errorStream);

		try {

			// empty loop to keep alive the server and catch exceptions
			while (true) {}

		} catch (Exception ex) {
			ex.printStackTrace(); // will be broadcasted to every client
		}
	}

	@Override
	public void onOpen(final WebSocket conn, final ClientHandshake handshake) {
		// conn.send("Welcome " +
		// conn.getRemoteSocketAddress().getAddress().getHostAddress() + " to the
		// server!");
		// broadcast("new connection: " + handshake.getResourceDescriptor()); // This
		// method sends a message to all clients connected
		// DEBUG.OUT(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
		conn.send(Jsoner
				.serialize(new GamaServerMessage(GamaServerMessage.Type.ConnectionSuccessful, "" + conn.hashCode())));

		if (canPing) {
			var timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if (conn.isOpen()) { conn.sendPing(); }
				}
			}, 0, pingInterval);
			pingTimers.put(conn, timer);
		}
		// String path = URI.create(handshake.getResourceDescriptor()).getPath();
	}

	/**
	 * Gets the default app.
	 *
	 * @return the default app
	 */
	// public Application getDefaultApp() { return app; }

	@Override
	public void onWebsocketPing(final WebSocket conn, final Framedata f) {
		// TODO Auto-generated method stub
		super.onWebsocketPing(conn, f);
	}

	@Override
	public void onWebsocketPong(final WebSocket conn, final Framedata f) {
		super.onWebsocketPong(conn, f);
	}

	@Override
	public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {

		var timer = pingTimers.remove(conn);
		if (timer != null) { timer.cancel(); }

		if (getLaunched_experiments().get("" + conn.hashCode()) != null) {
			for (IExperimentPlan e : getLaunched_experiments().get("" + conn.hashCode()).values()) {
				e.getController().directPause();
				e.getController().dispose();
			}
			getLaunched_experiments().get("" + conn.hashCode()).clear();
		}
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

			// DEBUG.OUT(socket + ": " + Jsoner.deserialize(message));
			final Object o = Jsoner.deserialize(message);
			if (o instanceof IMap) {
				map = (IMap<String, Object>) o;
			} else {
				map = GamaMapFactory.create();
				map.put(IKeyword.CONTENTS, o);
			}

		} catch (Exception e1) {
			// e1.printStackTrace();
			DEBUG.OUT(e1.toString());
			socket.send(Jsoner.serialize(new GamaServerMessage(GamaServerMessage.Type.MalformedRequest, e1)));
		}
		return map;
	}

	@Override
	public void onMessage(final WebSocket socket, final String message) {
		// server.broadcast(message);
		// DEBUG.OUT(socket + ": " + message);
		try {

			IMap<String, Object> map = extractParam(socket, message);
			map.put("server", this);
			DEBUG.OUT(map.get("type"));
			DEBUG.OUT(map.get("expr"));
			final String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";
			final String socket_id =
					map.get("socket_id") != null ? map.get("socket_id").toString() : "" + socket.hashCode();
			IExperimentPlan exp = getExperiment(socket_id, exp_id);
			SimulationAgent sim = exp != null && exp.getAgent() != null ? exp.getAgent().getSimulation() : null;
			if (sim != null && !exp.getController().isPaused()) {
				sim.postOneShotAction(scope1 -> {
					cmdHelper.pushCommand(socket, map);
					return null;
				});
			} else {
				cmdHelper.pushCommand(socket, map);
			}

		} catch (Exception e1) {
			DEBUG.OUT(e1);
			// e1.printStackTrace();
			socket.send(Jsoner.serialize(new GamaServerMessage(GamaServerMessage.Type.GamaServerError, e1)));

		}
	}

	@Override
	public void onError(final WebSocket conn, final Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific
			// websocket
		}
	}

	@Override
	public void onStart() {
		DEBUG.OUT("Gama Listener started on port: " + getPort());
		// setConnectionLostTimeout(0);
		// setConnectionLostTimeout(100);
	}

	/**
	 * Gets the all experiments.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the all experiments
	 * @date 15 oct. 2023
	 */
	public ConcurrentHashMap<String, ConcurrentHashMap<String, IExperimentPlan>> getAllExperiments() {
		return launchedExperiments;
	}

	/**
	 * Gets the experiments of.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @return the experiments of
	 * @date 15 oct. 2023
	 */
	public ConcurrentHashMap<String, IExperimentPlan> getExperimentsOf(final String socket) {
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
		// TODO executor should not be null
		if (executor == null) { command.run(); }
		executor.execute(command);
	}

	/**
	 * Gets the simulations.
	 *
	 * @return the simulations
	 */
	public ConcurrentHashMap<String, ConcurrentHashMap<String, IExperimentPlan>> getLaunched_experiments() {
		return launchedExperiments;
	}

}
