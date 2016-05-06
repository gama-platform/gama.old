package msi.gama.gui.swt;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import msi.gama.gui.swt.swing.Platform;
import msi.gama.runtime.GAMA;

/**
 * The purpose of this class is to install global key bindings that can work in any of the contexts of GAMA (incl. fullscreen)
 * 
 * @author drogoul
 *
 */
public class InstallKeyBindings {

	private final static GamaKeyBindings BINDINGS = new GamaKeyBindings();

	private static class GamaKeyBindings implements Listener {

		@Override
		public void handleEvent(final Event event) {
			if ( GAMA.getFrontmostController() == null )
				return;
			if ( !ctrl(event) )
				return;
			boolean handled = false;
			switch (event.character) {
				// Handles START & RELOAD
				case 'r':
					handled = true;
					if ( shift(event) ) {
						GAMA.reloadFrontmostExperiment();
					} else {
						GAMA.startFrontmostExperiment();
					}
					break;
				// Handles RELOAD
				case 'R':
					handled = true;
					GAMA.reloadFrontmostExperiment();
					break;
				// Handles STEP
				case 'P':
					handled = true;
					GAMA.stepFrontmostExperiment();
					break;
				// Handles PAUSE & STEP
				case 'p':
					handled = true;
					if ( shift(event) ) {
						GAMA.stepFrontmostExperiment();
					} else {
						GAMA.pauseFrontmostExperiment();
					}
					break;
				case 'x':
					if ( shift(event) ) {
						handled = true;
						GAMA.closeAllExperiments(true, false);
					}
					break;
				case 'X':
					handled = true;
					GAMA.closeAllExperiments(true, false);

			}
			if ( handled ) {
				event.type = SWT.None;
			}

		}

		private void runQuitCommand(final Event e) {
			final IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
			if ( handlerService != null ) {
				try {
					handlerService.executeCommand("org.eclipse.ui.file.exit", e);
				} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}

	}

	public static void run() {
		PlatformUI.getWorkbench().getDisplay().addFilter(SWT.KeyDown, BINDINGS);
	}

	protected static boolean ctrl(final Event e) {
		return Platform.isCocoa() ? (e.stateMask & SWT.COMMAND) != 0 : (e.stateMask & SWT.CTRL) != 0;
	}

	protected static boolean shift(final Event e) {
		return (e.stateMask & SWT.SHIFT) != 0;
	}

}
