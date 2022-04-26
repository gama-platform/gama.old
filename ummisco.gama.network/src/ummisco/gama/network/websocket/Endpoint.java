package ummisco.gama.network.websocket;

import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;

public interface Endpoint {
	void onOpen(WebSocket socket);

	void onMessage(IGamaWebSocketServer server, WebSocket socket, String message);

	void onMessage(IGamaWebSocketServer server, WebSocket conn, ByteBuffer message);
}