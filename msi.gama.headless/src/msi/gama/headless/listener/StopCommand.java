package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

public class StopCommand implements ISocketCommand {
	
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {

		String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		String socket_id = map.get("socket_id").toString();
		final String cmd_type = map.get("type").toString();
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("launch");
		DEBUG.OUT(map.get("model"));
		DEBUG.OUT(map.get("experiment"));

		if (gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id) != null
				&& gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id).getSimulation() != null) {
			gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id).controller.directPause();
			gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id).controller.dispose();
			return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, "", map, false);
		}
		else {
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, "Unable to find the experiment or simulation", map, false);
		}	
	}
}
