package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.util.IMap;

public class ExitCommand implements ISocketCommand {
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {
		//TODO: just for compilation purposes, but makes no sense
		System.exit(0);
		return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, "" , map, false);
	}
}
