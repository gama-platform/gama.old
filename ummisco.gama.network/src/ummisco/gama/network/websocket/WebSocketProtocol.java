package ummisco.gama.network.websocket;

import java.nio.ByteBuffer;

import ummisco.gama.network.common.socket.AbstractProtocol;

public class WebSocketProtocol extends AbstractProtocol{

	@Override
	public void onOpen(AbstractProtocol conn) {
		// TODO Auto-generated method stub
		_connector.getSocketService();
	}

	@Override
	public void onClose(AbstractProtocol conn, int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(AbstractProtocol conn, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(AbstractProtocol conn, ByteBuffer message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(AbstractProtocol conn, Exception ex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	} 
}
