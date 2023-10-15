/*******************************************************************************************************
 *
 * PauseCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.runtime.server.CommandResponse;
import msi.gama.runtime.server.GamaServerMessage;
import msi.gama.runtime.server.GamaWebSocketServer;
import msi.gama.runtime.server.ISocketCommand;
import msi.gama.util.IMap;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class PauseCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class PauseCommand implements ISocketCommand {

	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {

		final String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final String socket_id =
				map.get("socket_id") != null ? map.get("socket_id").toString() : "" + socket.hashCode();
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("pause");
		DEBUG.OUT(exp_id);
		DEBUG.OUT(socket_id);

		if (exp_id == "" || socket_id == null) return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For 'pause', mandatory parameter is: 'exp_id'", map, false);
		var exp = gamaWebSocketServer.getExperiment(socket_id, exp_id);
		if (exp != null && exp.getCurrentSimulation() != null) {
			exp.getController().directPause();
			return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
		}
		return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
				"Unable to find the experiment or simulation", map, false);
	}
}
