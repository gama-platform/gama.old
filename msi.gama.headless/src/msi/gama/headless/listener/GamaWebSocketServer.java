package msi.gama.headless.listener;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.headless.runtime.Application;
import ummisco.gama.network.websocket.Endpoint;
import ummisco.gama.network.websocket.IGamaWebSocketServer;

public class GamaWebSocketServer extends IGamaWebSocketServer{

	private GamaListener _listener;
	public GamaListener get_listener() {
		return _listener;
	}

	public void set_listener(GamaListener _listener) {
		this._listener = _listener;
	}

	private Application app;
	Map<String, Endpoint> endpoints = Collections.synchronizedMap(new HashMap<>());
	Map<WebSocket, Endpoint> saved_endpoints = Collections.synchronizedMap(new HashMap<>());  

	public GamaWebSocketServer(int port, Application a, GamaListener l) {  
		super(new InetSocketAddress(port));
		app = a;
		_listener=l;
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


	public Application getDefaultApp() {
		return app;
	}
	 

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		if (_listener.getLaunched_experiments().get("" + conn.hashCode()) != null) {
			for (ManualExperimentJob e : _listener.getLaunched_experiments().get("" + conn.hashCode()).values()) {
				e.directPause();
				e.dispose();
			}
			_listener.getLaunched_experiments().get("" + conn.hashCode()).clear();
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
