package ummisco.gama.network.websocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public abstract class IGamaWebSocketServer extends WebSocketServer {

	public IGamaWebSocketServer(InetSocketAddress inetSocketAddress) {
		super(inetSocketAddress);
	} 
 
}
