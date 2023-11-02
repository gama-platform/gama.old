/*******************************************************************************************************
 *
 * GamaServerConsoleListener.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import msi.gama.common.interfaces.IConsoleListener;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.util.GamaColor;
import msi.gama.util.file.json.ParseException;

/**
 * The listener interface for receiving gamaServerConsole events.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @see GamaServerConsoleEvent
 * @date 2 nov. 2023
 */
public final class GamaServerConsoleListener extends GamaServerMessager implements IConsoleListener {

	/**
	 * Can send message.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param exp
	 *            the exp
	 * @return true, if successful
	 * @date 2 nov. 2023
	 */
	@Override
	public boolean canSendMessage(final IExperimentAgent exp) {
		var scope = exp.getScope();
		return scope != null && scope.getData("console") != null ? (boolean) scope.getData("console") : true;
	}

	@Override
	public void informConsole(final String s, final ITopLevelAgent root, final GamaColor color) {
		System.out.println(s);
		if (!canSendMessage(root.getExperiment())) return;

		try {
			sendMessage(root.getExperiment(),
					json.parse("{" + "\"message\": \"" + s + "\"," + "\"color\":" + json.valueOf(color) + "}"),
					GamaServerMessage.Type.SimulationOutput);
		} catch (ParseException e) {
			// If for some reason we cannot deserialize, we send it as text
			e.printStackTrace();
			sendMessage(root.getExperiment(), json.object("message", s, "color", color).toString(),
					GamaServerMessage.Type.SimulationOutput);
		}
	}

	@Override
	public void debugConsole(final int cycle, final String s, final ITopLevelAgent root, final GamaColor color) {
		if (!canSendMessage(root.getExperiment())) return;
		try {
			sendMessage(
					root.getExperiment(), json.parse("{" + "\"cycle\":" + cycle + "," + "\"message\": \""
							+ json.valueOf(s) + "\"," + "\"color\":" + json.valueOf(color) + "}"),
					GamaServerMessage.Type.SimulationDebug);
		} catch (ParseException e) {
			// If for some reason we cannot deserialize, we send it as text
			e.printStackTrace();
			sendMessage(root.getExperiment(), json.object("cycle", cycle, "message", s, "color", color).toString(),
					GamaServerMessage.Type.SimulationDebug);
		}
	}
}