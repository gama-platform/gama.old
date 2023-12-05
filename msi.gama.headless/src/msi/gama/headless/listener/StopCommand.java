/*******************************************************************************************************
 *
 * StopCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import org.java_websocket.WebSocket;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.server.CommandResponse;
import msi.gama.runtime.server.GamaServerMessage;
import msi.gama.runtime.server.GamaWebSocketServer;
import msi.gama.runtime.server.ISocketCommand;
import msi.gama.util.IMap;

/**
 * The Class StopCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class StopCommand implements ISocketCommand {

	@Override
	public CommandResponse execute(final GamaWebSocketServer server, final WebSocket socket,
			final IMap<String, Object> map) {
		IExperimentPlan plan;
		try {
			plan = server.retrieveExperimentPlan(STOP, socket, map);
		} catch (CommandException e) {
			return e.getResponse();
		}
		plan.getController().processPause(true);
		plan.getController().dispose();
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
	}
}
