package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

public class StepCommand implements ISocketCommand {
	
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {

		final String	exp_id 		= map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final String 	socket_id 	= map.get("socket_id").toString();
		final String 	cmd_type 	= map.get("type").toString();
		final int 		nb_step		= map.get("nb_step") != null ? (int) map.get("nb_step") : 1 ;
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("launch");
		DEBUG.OUT(map.get("model"));
		DEBUG.OUT(map.get("experiment"));

		if (gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id) != null
				&& gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id).getSimulation() != null) {
			for (int i = 0 ; i < nb_step ; i++) {
				gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id).controller.userStep();				
			}
			return new CommandResponse(	GamaServerMessageType.CommandExecutedSuccessfully,
										"",
										map);
		}
		else {
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, "Unable to find the experiment or simulation", map);
		}	
	}
}
