package msi.gama.headless.runtime;
/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import msi.gama.headless.common.Globals;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.ManualExperimentJob;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
interface Endpoint {
	void onOpen(WebSocket socket);

	void onMessage(GamaWebSocketServer server, WebSocket socket, String message);

	void onMessage(GamaWebSocketServer server, WebSocket conn, ByteBuffer message);
}

public class GamaWebSocketServer extends WebSocketServer {
	Map<String, Endpoint> endpoints = Collections.synchronizedMap(new HashMap<>());
	Map<WebSocket, Endpoint> saved_endpoints = Collections.synchronizedMap(new HashMap<>());

	private Application app;
	/** The instance. */
	private static GamaWebSocketServer instance;
	/** The simulations. */
	final private ConcurrentHashMap<String, ConcurrentHashMap<String, ExperimentJob>> launched_experiments = new ConcurrentHashMap<String, ConcurrentHashMap<String, ExperimentJob>>();
	private static WebSocketPrintStream bufferStream;

	public GamaWebSocketServer(int port, Application a) {
		super(new InetSocketAddress(port));
		app = a;
		File currentJavaJarFile = new File(
				GamaWebSocketServer.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();

		Globals.TEMP_PATH = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "") + "/temp";

		File f = new File(Globals.TEMP_PATH);
		deleteFolder(f);
		// check if the directory can be created
		// using the abstract path name
		if (f.mkdir()) {
			System.out.println("TEMP Directory is created");
		} else {
			System.out.println("TEMP Directory cannot be created");
		}
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

	public Application getDefaultApp() {
		return app;
	}

	/**
	 * Gets the single instance of GamaWebSocketServer.
	 *
	 * @return single instance of GamaWebSocketServer
	 */
	public static GamaWebSocketServer newInstance(final int p, final Application a) {
		if (instance == null) {
			createSocketServer(p, a);
		}
		return instance;
	}

	/**
	 * Creates the socket server.
	 *
	 * @throws UnknownHostException the unknown host exception
	 */
	public static void createSocketServer(final int port, final Application a) {
		instance = new GamaWebSocketServer(port, a);
		instance.endpoints.put("/compile", new CompileEndPoint());
		instance.endpoints.put("/launch", new LaunchEndPoint());
		instance.endpoints.put("/output", new OutputEndPoint());
		instance.start();
		System.out.println("ChatServer started on port: " + instance.getPort());
		bufferStream = new WebSocketPrintStream(System.out, instance);
		// System.setOut(bufferStream);

		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
		try {

			while (true) {
				String in = sysin.readLine();
//				instance.broadcast(in);
				if ("exit".equals(in)) {
					instance.stop(1000);
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conn.send("Welcome " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + " to the server!");
		broadcast("new connection: " + handshake.getResourceDescriptor()); // This method sends a message to all clients
																			// connected
		System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");

		String path = URI.create(handshake.getResourceDescriptor()).getPath();
		Endpoint endpoint = endpoints.get(path);
		if (endpoint != null) {
			saved_endpoints.put(conn, endpoint);
			endpoint.onOpen(conn);
		}
	}

	public ConcurrentHashMap<String, ConcurrentHashMap<String, ExperimentJob>> getAllExperiments() {
		return launched_experiments;
	}

	public ConcurrentHashMap<String, ExperimentJob> getExperimentsOf(final String socket) {
		return launched_experiments.get(socket);
	}

	public ExperimentJob getExperiment(final String socket, final String expid) {
		return launched_experiments.get(socket).get(expid);
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		if(launched_experiments.get(""+conn.hashCode())!=null) {			
			for (ExperimentJob e : launched_experiments.get(""+conn.hashCode()).values()) {
				((ManualExperimentJob) e).paused = true;

				e.dispose();
			}
			launched_experiments.get(""+conn.hashCode()).clear();
		}
		broadcast(conn + " has left the room!");
		System.out.println(conn + " has left the room!");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		saved_endpoints.get(conn).onMessage(this, conn, message);
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		saved_endpoints.get(conn).onMessage(this, conn, message);
		// broadcast(message.array());
		// System.out.println(conn + ": " + message);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific
			// websocket
		}
	}

	@Override
	public void onStart() {
		System.out.println("Server started!");
		// setConnectionLostTimeout(0);
		// setConnectionLostTimeout(100);
	}

}
