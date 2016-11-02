/*********************************************************************************************
 *
 * 'ConsoleDisplayerFactory.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.factories;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.common.interfaces.IConsoleDisplayer;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGamaView.Console;
import msi.gama.common.interfaces.IGui;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gaml.operators.Strings;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class ConsoleDisplayerFactory extends AbstractServiceFactory {

	IConsoleDisplayer displayer = new ConsoleDisplayer();

	class ConsoleDisplayer implements IConsoleDisplayer {

		private final StringBuilder consoleBuffer = new StringBuilder(2000);

		@Override
		public void debugConsole(final int cycle, final String msg, final ITopLevelAgent root) {
			this.debugConsole(cycle, msg, root, null);
		}

		@Override
		public void debugConsole(final int cycle, final String msg, final ITopLevelAgent root, final GamaColor color) {
			writeToConsole("(cycle : " + cycle + ") " + msg + Strings.LN, root, color);
		}

		@Override
		public void informConsole(final String msg, final ITopLevelAgent root) {
			this.informConsole(msg, root, null);
		}

		@Override
		public void informConsole(final String msg, final ITopLevelAgent root, final GamaColor color) {
			writeToConsole(msg + Strings.LN, root, color);
		}

		private void writeToConsole(final String msg, final ITopLevelAgent root, final GamaColor color) {
			final IGamaView.Console console = (Console) WorkbenchHelper.findView(IGui.CONSOLE_VIEW_ID, null, true);
			if (console != null) {
				console.append(msg, root, color);
			} else {
				consoleBuffer.append(msg);
			}
		}

		@Override
		public void eraseConsole(final boolean setToNull) {
			final IGamaView console = (IGamaView) WorkbenchHelper.findView(IGui.CONSOLE_VIEW_ID, null, false);
			if (console != null) {
				WorkbenchHelper.run(new Runnable() {

					@Override
					public void run() {
						console.reset();

					}
				});
			}
		}

		@Override
		public void showConsoleView(final ITopLevelAgent agent) {
			final IGamaView.Console console = (Console) GAMA.getGui().showView(IGui.CONSOLE_VIEW_ID, null,
					IWorkbenchPage.VIEW_VISIBLE);
			if (consoleBuffer.length() > 0 && console != null) {
				console.append(consoleBuffer.toString(), agent, null);
				consoleBuffer.setLength(0);
			}
			final IGamaView.Console icv = (Console) GAMA.getGui().showView(IGui.INTERACTIVE_CONSOLE_VIEW_ID, null,
					IWorkbenchPage.VIEW_VISIBLE);
			if (icv != null)
				icv.append(null, agent, null);
		}
	}

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return displayer;
	}

}
