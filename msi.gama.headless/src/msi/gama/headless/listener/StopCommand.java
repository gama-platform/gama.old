package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

public class StopCommand implements ISocketCommand {
	
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {

		final String 	exp_id 		= map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("stop");
		DEBUG.OUT(exp_id);

		if (exp_id == "" ) {
			return new CommandResponse(GamaServerMessageType.MalformedRequest, "For 'stop', mandatory parameter is: 'exp_id'", map, false);
		}
		var gama_exp = gamaWebSocketServer.get_listener().getExperiment("" + socket.hashCode(), exp_id); 
		if (gama_exp != null && gama_exp.getSimulation() != null) {
			gama_exp.controller.directPause();
			gama_exp.controller.dispose();
			return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, "", map, false);
		}
		else {
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, "Unable to find the experiment or simulation", map, false);
		}	
	}
}
