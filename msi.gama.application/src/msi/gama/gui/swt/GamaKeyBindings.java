package msi.gama.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import msi.gama.gui.swt.swing.Platform;
import msi.gama.runtime.GAMA;

/**
 * The purpose of this class is to install global key bindings that can work in any of the contexts of GAMA (incl. fullscreen)
 * 
 * @author drogoul
 *
 */
public class GamaKeyBindings implements Listener {

	@Override
	public void handleEvent(final Event event) {
		if ( GAMA.getFrontmostController() == null )
			return;
		if ( !ctrl(event) )
			return;
		switch (event.keyCode) {
			// Handles START & RELOAD
			case 'r':
				if ( shift(event) ) {
					consume(event);
					GAMA.reloadFrontmostExperiment();
				} else {
					consume(event);
					GAMA.startFrontmostExperiment();
				}
				break;		
			// Handles PAUSE & STEP
			case 'p':
				if ( shift(event) ) {
					consume(event);
					GAMA.stepFrontmostExperiment();
				} else {
					consume(event);
					GAMA.pauseFrontmostExperiment();
				}
				break;
			// Handles CLOSE
			case 'x':
				if ( shift(event) ) {
					consume(event);
					GAMA.closeAllExperiments(true, false);
				}
		}

	}

	private void consume(final Event event) {
		event.doit = false;
		event.type = SWT.None;
	}

	private final static GamaKeyBindings BINDINGS = new GamaKeyBindings();

	public static void install() {
		PlatformUI.getWorkbench().getDisplay().addFilter(SWT.KeyDown, BINDINGS);
	}

	public static boolean ctrl(final Event e) {
		return Platform.isCocoa() ? (e.stateMask & SWT.COMMAND) != 0 : (e.stateMask & SWT.CTRL) != 0;
	}

	public static boolean ctrl(final KeyEvent e) {
		return Platform.isCocoa() ? (e.stateMask & SWT.COMMAND) != 0 : (e.stateMask & SWT.CTRL) != 0;
	}

	public static boolean ctrl(final MouseEvent e) {
		return Platform.isCocoa() ? (e.stateMask & SWT.COMMAND) != 0 : (e.stateMask & SWT.CTRL) != 0;
	}

	public static boolean shift(final Event e) {
		return (e.stateMask & SWT.SHIFT) != 0;
	}

	public static boolean shift(final KeyEvent e) {
		return (e.stateMask & SWT.SHIFT) != 0;
	}

	public static boolean shift(final MouseEvent e) {
		return (e.stateMask & SWT.SHIFT) != 0;
	}

}
