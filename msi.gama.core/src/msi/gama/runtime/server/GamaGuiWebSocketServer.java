/*******************************************************************************************************
 *
 * GamaGuiWebSocketServer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IExperimentStateListener;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaWebSocketServer.
 */
public class GamaGuiWebSocketServer extends GamaWebSocketServer implements IExperimentStateListener {

	/** The current server config. */
	private GamaServerExperimentConfiguration currentServerConfig = GamaServerExperimentConfiguration.GUI;

	/**
	 * Start for GUI. No SSL and a default ping interval
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param port
	 *            the port
	 * @return the gama web socket server
	 * @date 16 oct. 2023
	 */
	public static GamaGuiWebSocketServer StartForGUI(final int port) {
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
	public static GamaGuiWebSocketServer StartForGUI(final int port, final int pingInterval) {
		GamaGuiWebSocketServer server = new GamaGuiWebSocketServer(port, pingInterval);
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
	private GamaGuiWebSocketServer(final int port, final int interval) {
		super(port, interval);
	}

	@Override
	public void onOpen(final WebSocket socket, final ClientHandshake handshake) {
		currentServerConfig = currentServerConfig.withSocket(socket);
		GAMA.getGui().getConsole().addConsoleListener(console);
		super.onOpen(socket, handshake);
	}

	@Override
	public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
		super.onClose(conn, code, reason, remote);
		GAMA.getGui().getConsole().removeConsoleListener(console);
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
		return GAMA.getExperiment();
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
		command.run();
	}

	@Override
	public void updateStateTo(final IExperimentPlan experiment, final State state) {
		// Does nothing for the moment (but could send messages corresponding to the clients ?)
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
		return currentServerConfig;
	}

	@Override
	public void addExperiment(final String socketId, final String experimentId, final IExperimentPlan plan) {}

}
