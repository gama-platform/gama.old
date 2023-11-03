/*******************************************************************************************************
 *
 * GamaServerStatusDisplayer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime.server;

import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;

/**
 * The Class GamaServerStatusDisplayer.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 2 nov. 2023
 */
public final class GamaServerStatusDisplayer extends GamaServerMessager implements IStatusDisplayer {

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
		if (exp == null) return false;
		var scope = exp.getScope();
		return scope != null && scope.getServerConfiguration().status();
	}

	@Override
	public void informStatus(final IScope scope, final String string) {
		if (!canSendMessage(scope.getExperiment())) return;
		sendMessage(scope.getExperiment(), "{" + "\"message\": \"" + string + "\"" + "}",
				GamaServerMessage.Type.SimulationStatusInform);
	}

	@Override
	public void errorStatus(final IScope scope, final String message) {
		if (!canSendMessage(scope.getExperiment())) return;
		sendMessage(scope.getExperiment(), "{" + "\"message\": \"" + message + "\"" + "}",
				GamaServerMessage.Type.SimulationStatusError);
	}

	@Override
	public void setStatus(final IScope scope, final String msg, final GamaColor color) {
		if (!canSendMessage(scope.getExperiment())) return;
		sendMessage(scope.getExperiment(), json.object("message", msg, "color", color).toString(),
				GamaServerMessage.Type.SimulationStatus);
	}

	@Override
	public void informStatus(final IScope scope, final String message, final String icon) {
		if (!canSendMessage(scope.getExperiment())) return;
		sendMessage(scope.getExperiment(), json.object("message", message, "icon", icon).toString(),
				GamaServerMessage.Type.SimulationStatusInform);
	}

	@Override
	public void setStatus(final IScope scope, final String msg, final String icon) {
		if (!canSendMessage(scope.getExperiment())) return;
		sendMessage(scope.getExperiment(), json.object("message", msg, "icon", icon).toString(),
				GamaServerMessage.Type.SimulationStatus);

	}

	@Override
	public void neutralStatus(final IScope scope, final String string) {
		if (!canSendMessage(scope.getExperiment())) return;
		sendMessage(scope.getExperiment(), json.object("message", string).toString(),
				GamaServerMessage.Type.SimulationStatusNeutral);
	}
}