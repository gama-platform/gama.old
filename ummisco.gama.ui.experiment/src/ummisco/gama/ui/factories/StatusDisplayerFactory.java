package ummisco.gama.ui.factories;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.common.IStatusMessage;
import msi.gama.common.StatusMessage;
import msi.gama.common.SubTaskMessage;
import msi.gama.common.UserStatusMessage;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.util.GamaColor;
import ummisco.gama.ui.controls.StatusControlContribution;
import ummisco.gama.ui.utils.ThreadedUpdater;

public class StatusDisplayerFactory extends AbstractServiceFactory {

	IStatusDisplayer displayer = new StatusDisplayer();

	class StatusDisplayer implements IStatusDisplayer {

		private final ThreadedUpdater<IStatusMessage> status = new ThreadedUpdater<>("Status refresh");

		StatusDisplayer() {
			status.setTarget(StatusControlContribution.getInstance(), null);
		}

		@Override
		public void waitStatus(final String string) {
			setStatus(string, IGui.WAIT);
		}

		@Override
		public void informStatus(final String string) {
			setStatus(string, IGui.INFORM);
		}

		@Override
		public void errorStatus(final String error) {
			setStatus(error, IGui.ERROR);
		}

		public void neutralStatus(final String message) {
			setStatus(message, IGui.NEUTRAL);
		}

		public void setStatus(final String msg, final int code) {
			status.updateWith(new StatusMessage(msg, code));
		}

		@Override
		public void setStatus(final String msg, final String icon) {
			setStatusInternal(msg, null, icon);
		}

		@Override
		public void resumeStatus() {
			status.resume();
		}

		@Override
		public void setSubStatusCompletion(final double s) {
			status.updateWith(new SubTaskMessage(s));
		}

		@Override
		public void informStatus(final String string, final String icon) {
			status.updateWith(new StatusMessage(string, IGui.INFORM, icon));
		}

		@Override
		public void beginSubStatus(final String name) {
			status.updateWith(new SubTaskMessage(name, true));
		}

		@Override
		public void endSubStatus(final String name) {
			status.updateWith(new SubTaskMessage(name, false));
		}

		private void setStatusInternal(final String msg, final GamaColor color, final String icon) {
			status.updateWith(new UserStatusMessage(msg, color, icon));
		}

		@Override
		public void setStatus(final String message, final GamaColor color) {
			if (message == null) {
				resumeStatus();
			} else {
				setStatusInternal(message, color, null);
			}

		}

	}

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return displayer;
	}

}
