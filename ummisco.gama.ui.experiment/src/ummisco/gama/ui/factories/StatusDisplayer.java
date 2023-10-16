/*******************************************************************************************************
 *
 * StatusDisplayer.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.factories;

import msi.gama.common.StatusMessage;
import msi.gama.common.SubTaskMessage;
import msi.gama.common.UserStatusMessage;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.common.interfaces.IStatusMessage;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import ummisco.gama.ui.controls.StatusControlContribution;
import ummisco.gama.ui.utils.ThreadedUpdater;

/**
 * The Class StatusDisplayer.
 */
public class StatusDisplayer implements IStatusDisplayer {

	/** The status. */
	private final ThreadedUpdater<IStatusMessage> status = new ThreadedUpdater<>("Status refresh");

	/**
	 * Instantiates a new status displayer.
	 */
	StatusDisplayer() {
		status.setTarget(StatusControlContribution.getInstance(), null);
		GAMA.registerTopLevelAgentChangeListener(this);
	}

	/**
	 * Sets the listening agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the new listening agent
	 * @date 14 août 2023
	 */
	@Override
	public void topLevelAgentChanged(final ITopLevelAgent agent) {
		informStatus(null, "overlays/status.clock");
	}

	/**
	 * Wait status.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param string
	 *            the string
	 * @date 14 août 2023
	 */
	@Override
	public void waitStatus(final IScope scope, final String string) {
		setStatus(string, IGui.WAIT);
	}

	/**
	 * Inform status.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param string
	 *            the string
	 * @date 14 août 2023
	 */
	@Override
	public void informStatus(final IScope scope, final String string) {
		setStatus(string, IGui.INFORM);
	}

	/**
	 * Error status.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param error
	 *            the error
	 * @date 14 août 2023
	 */
	@Override
	public void errorStatus(final IScope scope, final String error) {
		setStatus(error, IGui.ERROR);
	}

	/**
	 * Neutral status.
	 *
	 * @param message
	 *            the message
	 * @param scope
	 *            the scope
	 */
	@Override
	public void neutralStatus(final IScope scope, final String message) {
		setStatus(message, IGui.NEUTRAL);
	}

	/**
	 * Sets the status.
	 *
	 * @param msg
	 *            the msg
	 * @param code
	 *            the code
	 */
	private void setStatus(final String msg, final int code) {
		status.updateWith(new StatusMessage(msg, code));
	}

	/**
	 * Sets the status.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param msg
	 *            the msg
	 * @param icon
	 *            the icon
	 * @date 14 août 2023
	 */
	@Override
	public void setStatus(final IScope scope, final String msg, final String icon) {
		setStatusInternal(msg, null, icon);
	}

	/**
	 * Resume status.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 14 août 2023
	 */
	@Override
	public void resumeStatus(final IScope scope) {
		status.resume();
	}

	/**
	 * Sets the sub status completion.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param s
	 *            the new sub status completion
	 * @date 14 août 2023
	 */
	@Override
	public void setSubStatusCompletion(final IScope scope, final double s) {
		status.updateWith(new SubTaskMessage(s));
	}

	/**
	 * Inform status.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param string
	 *            the string
	 * @param icon
	 *            the icon
	 * @date 14 août 2023
	 */
	@Override
	public void informStatus(final IScope scope, final String string, final String icon) {
		status.updateWith(new StatusMessage(string, IGui.INFORM, icon));
	}

	/**
	 * Begin sub status.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @date 14 août 2023
	 */
	@Override
	public void beginSubStatus(final IScope scope, final String name) {
		status.updateWith(new SubTaskMessage(name, true));
	}

	/**
	 * End sub status.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @date 14 août 2023
	 */
	@Override
	public void endSubStatus(final IScope scope, final String name) {
		status.updateWith(new SubTaskMessage(name, false));
	}

	/**
	 * Sets the status internal.
	 *
	 * @param msg
	 *            the msg
	 * @param color
	 *            the color
	 * @param icon
	 *            the icon
	 */
	private void setStatusInternal(final String msg, final GamaColor color, final String icon) {
		status.updateWith(new UserStatusMessage(msg, color, icon));
	}

	/**
	 * Sets the status.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param message
	 *            the message
	 * @param color
	 *            the color
	 * @date 14 août 2023
	 */
	@Override
	public void setStatus(final IScope scope, final String message, final GamaColor color) {
		if (message == null) {
			resumeStatus(scope);
		} else {
			setStatusInternal(message, color, null);
		}

	}

}