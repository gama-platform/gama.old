/*******************************************************************************************************
 *
 * ExitCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
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
import msi.gama.runtime.server.ISocketCommand;
import msi.gama.util.IMap;

/**
 * The Class ExitCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public class ExitCommand implements ISocketCommand {
	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {
		// TODO: just for compilation purposes, but makes no sense
		System.exit(0);
		return new CommandResponse(GamaServerMessage.Type.CommandExecutedSuccessfully, "", map, false);
	}
}
