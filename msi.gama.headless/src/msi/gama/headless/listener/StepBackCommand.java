package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

public class StepBackCommand implements ISocketCommand {
	
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {


		final String 	exp_id 		= map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final Object 	socket_id 	= map.get("socket_id");
		final int		nb_step		= map.get("nb_step") != null ? (int) map.get("nb_step") : 1; 
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("stepBack");
		DEBUG.OUT(exp_id);
		DEBUG.OUT(socket_id);
		

		if (exp_id == "" || socket_id == null) {
			return new CommandResponse(GamaServerMessageType.MalformedRequest, "For 'stepBack', mandatory parameters are: 'exp_id' and 'socket_id' ", map, false);
		}

		var gama_exp = gamaWebSocketServer.get_listener().getExperiment(socket_id.toString(), exp_id); 
		if (gama_exp != null && gama_exp.getSimulation() != null) {
			for (int i = 0 ; i < nb_step ; i++) {
				gama_exp.controller.userStepBack();
			}
			return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, "", map, false);
		}
		else {
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, "Unable to find the experiment or simulation", map, false);
		}		
	}
}
