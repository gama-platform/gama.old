package msi.gama.headless.runtime;

import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;

public class CompileEndPoint implements Endpoint{

	@Override
	public void onOpen(WebSocket socket) {
		socket.send("Hello!");
	}

	@Override
	public void onMessage(GamaWebSocketServer server, WebSocket socket, String message) {
		socket.send(message);
		
	}

	@Override
	public void onMessage(GamaWebSocketServer server, WebSocket conn, ByteBuffer message) {
		// TODO Auto-generated method stub
		
	}
	
}