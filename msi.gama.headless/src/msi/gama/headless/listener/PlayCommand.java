/*******************************************************************************************************
 *
 * PlayCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
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
 * The Class PlayCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class PlayCommand implements ISocketCommand {

	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {

		final String exp_id = map.get(EXP_ID) != null ? map.get(EXP_ID).toString() : "";
		final boolean sync = map.get(SYNC) != null ? Boolean.parseBoolean("" + map.get(SYNC)) : false;
		final String socket_id = map.get(SOCKET_ID) != null ? map.get(SOCKET_ID).toString() : "" + socket.hashCode();
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get(SERVER);
		DEBUG.OUT("play");
		DEBUG.OUT(map.get("model"));
		DEBUG.OUT(map.get("experiment"));

		if (exp_id == "") return new CommandResponse(GamaServerMessage.Type.MalformedRequest,
				"For 'play', mandatory parameter is: 'exp_id'", map, false);

		var gama_exp = gamaWebSocketServer.getExperiment(socket_id, exp_id);
		if (gama_exp == null || gama_exp.getAgent() == null || gama_exp.getCurrentSimulation() == null)
			return new CommandResponse(GamaServerMessage.Type.UnableToExecuteRequest,
					"Unable to find the experiment or simulation", map, false);
		gama_exp.getAgent().setAttribute("%%playCommand%%", map);
		gama_exp.getController().userStart();
		boolean hasEndCond = map.containsKey(UNTIL) && !map.get(UNTIL).toString().isBlank();
		if (hasEndCond && sync) return null;
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
	}
}

// unt sync nre
// nou sync ret
// nou async ret
// unt async ret