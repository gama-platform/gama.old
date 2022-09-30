package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

public class StepCommand implements ISocketCommand {
	
	@Override
	public CommandResponse execute(final WebSocket socket, IMap<String, Object> map) {


		final String 	exp_id 		= map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final Object 	socket_id 	= map.get("socket_id");
		final int		nb_step		= map.get("nb_step") != null ? (int) map.get("nb_step") : 1; 
		final boolean 	sync 		= map.get("sync") != null ? (boolean) map.get("sync") : false;
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("step");
		DEBUG.OUT(exp_id);
		DEBUG.OUT(socket_id);

		if (exp_id == "" || socket_id == null) {
			return new CommandResponse(GamaServerMessageType.MalformedRequest, "For 'step', mandatory parameters are: 'exp_id' and 'socket_id' ", null, false);
		}

		var gama_exp = gamaWebSocketServer.get_listener().getExperiment(socket_id.toString(), exp_id); 
		if (gama_exp != null && gama_exp.getSimulation() != null) {
			for (int i = 0 ; i < nb_step ; i++) {
				try {
					if (sync) {
						gama_exp.controller._job.doStep();
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
