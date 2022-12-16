package ummisco.gama.ui.factories;

import msi.gama.common.StatusMessage;
import msi.gama.common.SubTaskMessage;
import msi.gama.common.UserStatusMessage;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.common.interfaces.IStatusMessage;
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
	}

	@Override
	public void waitStatus(final IScope scope, final String string) {
		setStatus(scope, string, IGui.WAIT);
	}

	@Override
	public void informStatus(final IScope scope, final String string) {
		setStatus(scope, string, IGui.INFORM);
	}

	@Override
	public void errorStatus(final IScope scope, final String error) {
		setStatus(scope, error, IGui.ERROR);
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
		setStatus(scope, message, IGui.NEUTRAL);
	}

	/**
	 * Sets the status.
	 *
	 * @param msg
	 *            the msg
	 * @param code
	 *            the code
	 */
	private void setStatus(final IScope scope, final String msg, final int code) {
		status.updateWith(new StatusMessage(msg, code));
	}

	@Override
	public void setStatus(final IScope scope, final String msg, final String icon) {
		setStatusInternal(msg, null, icon, scope);
	}

	@Override
	public void resumeStatus(final IScope scope) {
		status.resume();
	}

	@Override
	public void setSubStatusCompletion(final IScope scope, final double s) {
		status.updateWith(new SubTaskMessage(s));
	}

	@Override
	public void informStatus(final IScope scope, final String string, final String icon) {
		status.updateWith(new StatusMessage(string, IGui.INFORM, icon));
	}

	@Override
	public void beginSubStatus(final IScope scope, final String name) {
		status.updateWith(new SubTaskMessage(name, true));
	}

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
	private void setStatusInternal(final String msg, final GamaColor color, final String icon, final IScope scope) {
		status.updateWith(new UserStatusMessage(msg, color, icon));
	}

	@Override
	public void setStatus(final IScope scope, final String message, final GamaColor color) {
		if (message == null) {
			resumeStatus(scope);
		} else {
			setStatusInternal(message, color, null, scope);
		}

	}

}