/*******************************************************************************************************
 *
 * ISocketCommand.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import org.java_websocket.WebSocket;

import msi.gama.util.IMap;

/**
 * The Interface ISocketCommand.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 15 oct. 2023
 */
public interface ISocketCommand {

	/**
	 * Execute.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param socket
	 *            the socket
	 * @param map
	 *            the map
	 * @return the gama server message
	 * @date 15 oct. 2023
	 */
	GamaServerMessage execute(final WebSocket socket, final IMap<String, Object> map);

}
