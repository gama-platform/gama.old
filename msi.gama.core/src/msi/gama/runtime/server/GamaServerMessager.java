/*******************************************************************************************************
 *
 * GamaServerMessager.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import org.java_websocket.WebSocket;

import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.util.file.json.Json;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamaServerMessager.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 2 nov. 2023
 */
public abstract class GamaServerMessager {

	/** The json. */
	Json json = Json.getNew();

	/**
	 * Can send message.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param exp
	 *            the exp
	 * @return true, if successful
	 * @date 2 nov. 2023
	 */
	public abstract boolean canSendMessage(final IExperimentAgent exp);

	/**
	 * Send message.
	 *
	 * @param exp
	 *            the exp
	 * @param m
	 *            the m
	 * @param type
	 *            the type
	 */
	public void sendMessage(final IExperimentAgent exp, final Object m, final GamaServerMessage.Type type) {

		try {

			if (exp == null) {
				DEBUG.OUT("No experiment, unable to send message: " + m);
				return;
			}

			var scope = exp.getScope();
			if (scope == null) {
				DEBUG.OUT("No scope, unable to send message: " + m);
				return;
			}
			var socket = (WebSocket) scope.getData("socket");
			if (socket == null) {
				DEBUG.OUT("No socket found, maybe the client is already disconnected. Unable to send message: " + m);
				return;
			}
			socket.send(json.valueOf(new GamaServerMessage(type, m, (String) scope.getData("exp_id"))).toString());

		} catch (Exception ex) {
			ex.printStackTrace();
			DEBUG.OUT("Unable to send message:" + m);
			DEBUG.OUT(ex.toString());
		}
	}

}