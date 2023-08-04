package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

public class StepCommand implements ISocketCommand {
	
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {


		final String 	exp_id 		= map.get("exp_id") 	!= null ? map.get("exp_id").toString() : "";
		final int		nb_step		= map.get("nb_step") 	!= null ? Integer.parseInt("" + map.get("nb_step")) : 1; 
		final String 	socket_id	= map.get("socket_id") 	!= null ? map.get("socket_id").toString() : ("" + socket.hashCode());
		final boolean 	sync 		= map.get("sync") 		!= null ? Boolean.parseBoolean("" + map.get("sync")) : false;
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("step");
		DEBUG.OUT(exp_id);

		if (exp_id == "" ) {
			return new CommandResponse(GamaServerMessageType.MalformedRequest, "For 'step', mandatory parameter is: 'exp_id'", map, false);
		}

		var gama_exp = gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id); 
		if (gama_exp != null && gama_exp.getSimulation() != null) {
			for (int i = 0 ; i < nb_step ; i++) {
				try {
					if (sync) {
						gama_exp.doStep();
					} else {
						gama_exp.controller.userStep();				
					}
				} catch (RuntimeException e) {
					DEBUG.OUT(e.getStackTrace());
					return new CommandResponse(	GamaServerMessageType.GamaServerError, e, map, false);
				}
				
			}
			return new CommandResponse(	GamaServerMessageType.CommandExecutedSuccessfully,
										"",
										map, false);
		}
		else {
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest, "Unable to find the experiment or simulation", map, false);
		}	
	}
}
