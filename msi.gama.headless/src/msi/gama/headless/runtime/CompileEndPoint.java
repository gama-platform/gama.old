package msi.gama.headless.runtime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaHeadlessException;

public class CompileEndPoint implements Endpoint{

	@Override
	public void onOpen(WebSocket socket) {
		socket.send("Hello!");
	}

	@Override
	public void onMessage(GamaWebSocketServer server, WebSocket socket, String message) {
		socket.send(message);
		
	}
	
}