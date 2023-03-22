/*******************************************************************************************************
 *
 * GamaWebSocketServer.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.SSLParametersWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import msi.gama.common.GamlFileExtension;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.GamaServerMessage;
import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.headless.runtime.Application;
import msi.gama.headless.script.ExperimentationPlanFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.json.Jsoner;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaWebSocketServer.
 */
public class GamaWebSocketServer extends WebSocketServer {

	/** The listener. */
	private GamaListener _listener;

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	public GamaListener get_listener() {
		return _listener;
	}

	/**
	 * Sets the listener.
	 *
	 * @param _listener
	 *            the new listener
	 */
	public void set_listener(final GamaListener _listener) {
		this._listener = _listener;
	}

	/** The app. */
	private final Application app;

	/** The cmd helper. */
	CommandExecutor cmdHelper;

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
	public GamaWebSocketServer(final int port, final Application a, final GamaListener l, final boolean ssl, final String jksPath, final String spwd, final String kpwd) {
		super(new InetSocketAddress(port));
		if (a.verbose) { DEBUG.ON(); }
		cmdHelper = new CommandExecutor();
		if (ssl) {
			// load up the key store
			String STORETYPE = "JKS";
//			File currentJavaJarFile =
//					new File(GamaListener.class.getProtectionDomain().getCodeSource().getLocation().getPath());
//			String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();
//			String KEYSTORE = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "") + "/../keystore.jks";
			String KEYSTORE = jksPath;//"/Users/hqn88/git/gama.client/server/.cert/cert.jks";
			String STOREPASSWORD = spwd;//"abcdef";
			String KEYPASSWORD = kpwd;//"abcdef";

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
			    this
			        .setWebSocketFactory(new SSLParametersWebSocketServerFactory(sslContext, sslParameters));
//				this.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		app = a;
		_listener = l;
	}

	@Override
	public void onOpen(final WebSocket conn, final ClientHandshake handshake) {
		// conn.send("Welcome " +
		// conn.getRemoteSocketAddress().getAddress().getHostAddress() + " to the
		// server!");
		// broadcast("new connection: " + handshake.getResourceDescriptor()); // This
		// method sends a message to all clients connected
		DEBUG.OUT(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
		conn.send(Jsoner
				.serialize(new GamaServerMessage(GamaServerMessageType.ConnectionSuccessful, "" + conn.hashCode())));

		// String path = URI.create(handshake.getResourceDescriptor()).getPath();
	}

	/**
	 * Gets the default app.
	 *
	 * @return the default app
	 */
	public Application getDefaultApp() { return app; }

	@Override
	public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
		if (_listener.getLaunched_experiments().get("" + conn.hashCode()) != null) {
			for (ManualExperimentJob e : _listener.getLaunched_experiments().get("" + conn.hashCode()).values()) {
				e.controller.directPause();
				e.dispose();
			}
			_listener.getLaunched_experiments().get("" + conn.hashCode()).clear();
		}
		// broadcast(conn + " has left the room!");
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
			socket.send(Jsoner.serialize(new GamaServerMessage(GamaServerMessageType.MalformedRequest, e1)));
		}
		return map;
	}

	@Override
	public void onMessage(final WebSocket socket, final String message) {
		// server.get_listener().broadcast(message);
		// DEBUG.OUT(socket + ": " + message);
		try {

			IMap<String, Object> map = extractParam(socket, message);
			map.put("server", this);
			DEBUG.OUT(map.get("type"));
			final String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";
			final String socket_id = map.get("socket_id") != null ? map.get("socket_id").toString() : ("" + socket.hashCode());
			if(get_listener().getExperiment(socket_id, exp_id)!=null  && !get_listener().getExperiment(socket_id, exp_id).controller.isPaused() ) {
				get_listener().getExperiment(socket_id, exp_id).controller.getScope().getSimulation().postOneShotAction(scope1 -> {
					cmdHelper.process(socket, map);
//				System.out.println(map.get("type"));
					return null;
				});
			}else {

				cmdHelper.process(socket, map);
			}

		} catch (Exception e1) {
			DEBUG.OUT(e1);
			// e1.printStackTrace();
			socket.send(Jsoner.serialize(new GamaServerMessage(GamaServerMessageType.GamaServerError, e1)));

		}
	}

	@Override
	public void onMessage(final WebSocket conn, final ByteBuffer message) {
		try {
			runCompiledSimulation(this, message);
		} catch (IOException | GamaHeadlessException e) {
			DEBUG.OUT(e);
			conn.send(Jsoner.serialize(new GamaServerMessage(GamaServerMessageType.RuntimeError, e)));

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
	 * Compile gaml simulation.
	 *
	 * @param socket
	 *            the socket
	 * @param args
	 *            the args
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException
	 *             the gama headless exception
	 */
	public void compileGamlSimulation(final WebSocket socket, final List<String> args)
			throws IOException, GamaHeadlessException {
		final String pathToModel = args.get(args.size() - 1);

		if (!GamlFileExtension.isGaml(pathToModel)) { System.exit(-1); }
		final String argExperimentName = args.get(args.size() - 2);
		final String argGamlFile = args.get(args.size() - 1);

		final List<IExperimentJob> jb = ExperimentationPlanFactory.buildExperiment(argGamlFile);
		ExperimentJob selectedJob = null;
		for (final IExperimentJob j : jb) {
			if (j.getExperimentName().equals(argExperimentName)) {
				selectedJob = (ExperimentJob) j;
				break;
			}
		}
		if (selectedJob == null) return;

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(bos)) {
			out.writeObject(selectedJob);
			out.flush();
			byte[] yourBytes = bos.toByteArray();
			socket.send(yourBytes);
		}
	}

	/**
	 * Run compiled simulation.
	 *
	 * @param server
	 *            the server
	 * @param compiledModel
	 *            the compiled model
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException
	 *             the gama headless exception
	 */
	public void runCompiledSimulation(final WebSocketServer server, final ByteBuffer compiledModel)
			throws IOException, GamaHeadlessException {
		ByteArrayInputStream bis = new ByteArrayInputStream(compiledModel.array());
		ObjectInput in = null;
		ExperimentJob selectedJob = null;
		try {
			in = new ObjectInputStream(bis);
			Object o = in.readObject();
			selectedJob = (ExperimentJob) o;
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (in != null) { in.close(); }
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		((GamaWebSocketServer) server).getDefaultApp().processorQueue.execute(selectedJob);

	}

}
