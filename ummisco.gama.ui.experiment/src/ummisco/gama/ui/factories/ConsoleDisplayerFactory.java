/*******************************************************************************************************
 *
 * ConsoleDisplayerFactory.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.factories;

import static ummisco.gama.ui.utils.ViewsHelper.hideView;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.application.workbench.PerspectiveHelper;
import msi.gama.common.interfaces.IConsoleDisplayer;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGamaView.Console;
import msi.gama.common.interfaces.IGui;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gaml.operators.Strings;
import ummisco.gama.ui.utils.ViewsHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * A factory for creating ConsoleDisplayer objects.
 */
public class ConsoleDisplayerFactory extends AbstractServiceFactory {

	/** The displayer. */
	IConsoleDisplayer displayer = new ConsoleDisplayer();

	/**
	 * The Class ConsoleDisplayer.
	 */
	static class ConsoleDisplayer implements IConsoleDisplayer {

		/** The console buffers. */
		Map<GamaColor, StringBuilder> consoleBuffers = new HashMap<>();

		@Override
		public void informConsole(final String msg, final ITopLevelAgent root, final GamaColor color) {
			IGamaView.Console[] console = new IGamaView.Console[1];
			try {
				console[0] = (Console) ViewsHelper.findView(IGui.CONSOLE_VIEW_ID, null, true);
			} catch (final ConcurrentModificationException e) {
				// See Issue #2812. With concurrent views opening, the view might be impossible to find
			}
			if (console[0] == null) {
				WorkbenchHelper.run(() -> {
					if (!PerspectiveHelper.showConsoles()) return;
					console[0] = (Console) GAMA.getGui().showView(null, IGui.CONSOLE_VIEW_ID, null,
							IWorkbenchPage.VIEW_VISIBLE);
				});
			}
			if (console[0] != null) {
				console[0].append(msg + Strings.LN, root, color);
			} else { // DO WE KEEP THIS ? NOT HAVING BUFFERS MEANS THAT IF A CONSOLE IS OPENED AFTERWARDS, NOTHING WILL
				// APPEAR ON IT
				GamaColor c = color == null ? root == null ? GamaColor.getInt(0) : root.getColor() : color;
				StringBuilder sb = consoleBuffers.get(c);
				if (sb == null) {
					sb = new StringBuilder(2000);
					consoleBuffers.put(c, sb);
				}
				sb.append(msg + Strings.LN);
			}

		}

		@Override
		public void eraseConsole(final boolean setToNull) {
			final IGamaView console = (IGamaView) ViewsHelper.findView(IGui.CONSOLE_VIEW_ID, null, false);
			if (console != null) { WorkbenchHelper.run(() -> console.reset()); }
			consoleBuffers.clear();
		}

		/**
		 * Toogle console views.
		 *
		 * @param agent
		 *            the agent
		 */
		@Override
		public void toggleConsoleViews(final ITopLevelAgent agent, final boolean show) {
			if (!show) {
				hideView(IGui.CONSOLE_VIEW_ID);
				hideView(IGui.INTERACTIVE_CONSOLE_VIEW_ID);
			} else {
				final IGamaView.Console icv = (Console) GAMA.getGui().showView(null, IGui.INTERACTIVE_CONSOLE_VIEW_ID,
						null, IWorkbenchPage.VIEW_VISIBLE);
				if (icv != null) { icv.append(null, agent, null); }
				final IGamaView.Console console =
						(Console) GAMA.getGui().showView(null, IGui.CONSOLE_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
				consoleBuffers.forEach((c, sb) -> {
					if (sb.length() > 0 && console != null) {
						console.append(sb.toString(), agent, c);
						sb.setLength(0);
					}
				});
			}
		}

	}

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return displayer;
	}

}
