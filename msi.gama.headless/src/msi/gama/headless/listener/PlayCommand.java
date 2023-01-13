package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

public class PlayCommand implements ISocketCommand {

	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {


		final String 	exp_id 		= map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final boolean 	sync 		= map.get("sync") != null ? Boolean.parseBoolean("" + map.get("sync")) : false;
		final String 	socket_id 	= map.get("socket_id") != null ? map.get("socket_id").toString() : ("" + socket.hashCode());
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("play");
		DEBUG.OUT(map.get("model"));
		DEBUG.OUT(map.get("experiment"));
		

		if (exp_id == "") {
			return new CommandResponse(GamaServerMessageType.MalformedRequest, "For 'play', mandatory parameter is: 'exp_id'", map, false);
		}


		var gama_exp = gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id); 
		if (gama_exp != null && gama_exp.getSimulation() != null) {
			gama_exp.playCommand = map;
			gama_exp.controller.userStart();

			if (!"".equals(gama_exp.endCond) && sync) {
				return null;
			} else {
				return new CommandResponse(GamaServerMessageType.CommandExecutedSuccessfully, "", map, false);
			}
		} else {
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest,
					"Unable to find the experiment or simulation", map, false);
		}
	}
}

//unt sync nre
//nou sync ret
//nou async ret
//unt async ret