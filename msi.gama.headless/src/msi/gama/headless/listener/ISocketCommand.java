package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaServerMessage;
import msi.gama.util.IMap;

public interface ISocketCommand {
 

	public GamaServerMessage execute(final WebSocket socket, final IMap<String, Object> map);

	
}
