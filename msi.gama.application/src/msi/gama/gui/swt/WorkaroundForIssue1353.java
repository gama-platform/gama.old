/**
 * Created by drogoul, 28 déc. 2015
 *
 */
package msi.gama.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import msi.gama.gui.swt.swing.Platform;
import msi.gama.gui.views.LayeredDisplayView;

/**
 * Class WorkaroundForIssue1353. Only for MacOS X, Eclipse Mars and Java 1.7
 *
 * @author drogoul
 * @since 28 déc. 2015
 *
 */
public class WorkaroundForIssue1353 {

	private static Shell shell;

	private static void createShell() {
		if ( shell == null ) {
			shell = new Shell(SwtGui.getDisplay(), SWT.APPLICATION_MODAL);
			shell.setSize(5, 5);
			shell.setAlpha(0);
			shell.setBackground(IGamaColors.BLACK.active);
		}

	}

	public static void fixViewLosingMouseTrackEvents() {
		if ( shell != null ) // The fix has been installed
		{
			shell.open();
			shell.setVisible(false);
		}
	}

	// private static MouseTrackListener getDisplayListener() {
	// if ( displayExitEnterTracker == null ) {
	// displayExitEnterTracker = new MouseTrackListener() {
	//
	// @Override
	// public void mouseHover(final MouseEvent e) {}
	//
	// @Override
	// public void mouseExit(final MouseEvent e) {
	// // System.out.println("Opening/closing shell");
	// manipulateShell();
	// }
	//
	// @Override
	// public void mouseEnter(final MouseEvent e) {}
	// };
	// }
	// return displayExitEnterTracker;
	// }

	// private static MouseTrackListener getEditorListener(final Composite control) {
	// if ( editorExitEnterTracker == null ) {
	// editorExitEnterTracker = new MouseTrackListener() {
	//
	// @Override
	// public void mouseHover(final MouseEvent e) {}
	//
	// @Override
	// public void mouseExit(final MouseEvent e) {
	// System.out.println("Leaving editor");
	// // if ( exitedOnce ) {
	// // manipulateShell();
	// // }
	// }
	//
	// @Override
	// public void mouseEnter(final MouseEvent e) {
	// System.out.println("Entering editor");
	// // shellCreated = true;
	// // manipulateShell();
	// // control.getShell().forceActive();
	// // control.setFocus();
	// }
	// };
	// }
	// return editorExitEnterTracker;
	// }

	public static void installOn(final Composite control, final LayeredDisplayView view) {
		if ( !Platform.isCocoa() ) { return; }
		createShell();
		// final MouseTrackListener mlt = getDisplayListener();
		// control.addMouseTrackListener(mlt);
		// control.addDisposeListener(new DisposeListener() {
		//
		// @Override
		// public void widgetDisposed(final DisposeEvent e) {
		// control.removeMouseTrackListener(mlt);
		// control.removeDisposeListener(this);
		// }
		// });
	}

	// static boolean filterInstalled = false;

	public static void installOn(final Composite control) {
		if ( !Platform.isCocoa() ) { return; }
		createShell();
		// final MouseTrackListener mlt = getEditorListener(control);
		// control.addMouseTrackListener(mlt);
		// control.addDisposeListener(new DisposeListener() {
		//
		// @Override
		// public void widgetDisposed(final DisposeEvent e) {
		// control.removeMouseTrackListener(mlt);
		// control.removeDisposeListener(this);
		// }
		// });
		// if ( !filterInstalled ) {
		// filterInstalled = true;
		// final Listener listener = new Listener() {
		//
		// @Override
		// public void handleEvent(final Event event) {
		// System.out.println("Mouse exit from: " + event.widget + "at location" + event.x + " " + event.y);
		// }
		// };
		//
		// SwtGui.getDisplay().addFilter(SWT.MouseExit, listener);
		// }
		// }
	}

}
