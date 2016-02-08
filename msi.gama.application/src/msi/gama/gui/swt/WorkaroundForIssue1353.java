/**
 * Created by drogoul, 28 déc. 2015
 *
 */
package msi.gama.gui.swt;

import org.eclipse.swt.widgets.Composite;
import msi.gama.gui.views.LayeredDisplayView;

/**
 * Class WorkaroundForIssue1353. Only for MacOS X, Eclipse Mars and Java 1.7
 *
 * @author drogoul
 * @since 28 déc. 2015
 *
 */
public class WorkaroundForIssue1353 {

	// private static Shell invisibleShell;
	// private static MouseTrackListener displayExitEnterTracker, editorExitEnterTracker;
	// private static boolean exitedOnce = false;
	//
	// private static Shell getShell() {
	// if ( invisibleShell == null ) {
	// invisibleShell = new Shell(SwtGui.getShell(), SWT.APPLICATION_MODAL);
	// }
	// invisibleShell.setAlpha(0);
	// return invisibleShell;
	// }

	// private static void manipulateShell() {
	// // System.out.println("Manipulating Shell");
	// if ( !getShell().isFocusControl() ) {
	// getShell().open();
	// }
	// getShell().setSize(20, 20);
	// getShell().setVisible(false);
	// }

	// private static MouseTrackListener getDisplayListener() {
	// if ( displayExitEnterTracker == null ) {
	// displayExitEnterTracker = new MouseTrackListener() {
	//
	// @Override
	// public void mouseHover(final MouseEvent e) {}
	//
	// @Override
	// public void mouseExit(final MouseEvent e) {
	// exitedOnce = true;
	// manipulateShell();
	// }
	//
	// @Override
	// public void mouseEnter(final MouseEvent e) {}
	// };
	// }
	// return displayExitEnterTracker;
	// }

	// private static MouseTrackListener getEditorListener() {
	// if ( editorExitEnterTracker == null ) {
	// editorExitEnterTracker = new MouseTrackListener() {
	//
	// @Override
	// public void mouseHover(final MouseEvent e) {}
	//
	// @Override
	// public void mouseExit(final MouseEvent e) {
	// if ( exitedOnce ) {
	// manipulateShell();
	// }
	// }
	//
	// @Override
	// public void mouseEnter(final MouseEvent e) {}
	// };
	// }
	// return editorExitEnterTracker;
	// }

	public static void installOn(final Composite control, final LayeredDisplayView view) {
		if ( true ) { return; }
		// if ( !Platform.isCocoa() ) { return; }
		// control.addMouseTrackListener(getDisplayListener());
		// control.addDisposeListener(new DisposeListener() {
		//
		// @Override
		// public void widgetDisposed(final DisposeEvent e) {
		// control.removeMouseTrackListener(getDisplayListener());
		// control.removeDisposeListener(this);
		// }
		// });
	}

	public static void installOn(final Composite control) {
		if ( true ) { return; }
		// if ( !Platform.isCocoa() ) { return; }
		// control.addMouseTrackListener(getEditorListener());
		// control.addDisposeListener(new DisposeListener() {
		//
		// @Override
		// public void widgetDisposed(final DisposeEvent e) {
		// control.removeMouseTrackListener(getEditorListener());
		// control.removeDisposeListener(this);
		// }
		// });
	}

}
