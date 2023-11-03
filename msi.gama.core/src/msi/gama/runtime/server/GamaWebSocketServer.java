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

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import msi.gama.common.interfaces.IConsoleListener;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.file.json.Json;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaWebSocketServer.
 */
public abstract class GamaWebSocketServer extends WebSocketServer {

	/** The Constant SOCKET_ID. */
	static final String SOCKET_ID = "socket_id";

	/** The Constant TLS. */
	static final String TLS = "TLS";

	/** The Constant JKS. */
	static final String JKS = "JKS";

	/** The Constant SUN_X509. */
	static final String SUN_X509 = "SunX509";

	/** The Constant DEFAULT_PING_INTERVAL. */
	public static final int DEFAULT_PING_INTERVAL = 10000;

	/** The cmd helper. */
	protected final CommandExecutor cmdHelper = new CommandExecutor();

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
	protected GamaWebSocketServer(final int port, final int interval) {
		super(new InetSocketAddress(port));
		canPing = interval >= 0;
		pingInterval = interval;
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
			if (exp != null) {
				// In order to sync the command with the experiment cycles
				ExperimentAgent agent = exp.getAgent();
				if (agent != null) {
					agent.postOneShotAction(scope1 -> {
						cmdHelper.pushCommand(socket, map);
						return null;
					});
					return;
				}
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
	public abstract IExperimentPlan getExperiment(final String socket, final String expid);

	/**
	 * Execute.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param executionThread
	 *            the t
	 * @date 15 oct. 2023
	 */
	public abstract void execute(final Runnable command);

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
	public abstract void addExperiment(final String socketId, final String experimentId, final IExperimentPlan plan);

	/**
	 * Obtain gui server configuration.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the gama server experiment configuration
	 * @date 3 nov. 2023
	 */
	public abstract GamaServerExperimentConfiguration obtainGuiServerConfiguration();

}
