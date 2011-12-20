/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import org.eclipse.swt.widgets.Display;

/**
 * A listener that insures the proper modal behavior of Swing dialogs when running within a SWT
 * environment. When initialized, it blocks and unblocks SWT input as modal Swing dialogs are shown
 * and hidden.
 */
class AwtDialogListener implements AWTEventListener, ComponentListener {

	// modalDialogs should be accessed only from the AWT thread, so no
	// synchronization is needed.
	/** The modal dialogs. */
	private final List modalDialogs = new ArrayList();

	/** The display. */
	private final Display display;

	/**
	 * Registers this object as an AWT event listener so that Swing dialogs have the proper modal
	 * behavior in the containing SWT environment. This is called automatically when you construct a
	 * {@link EmbeddedSwingComposite}, and it need not be called separately in that case.
	 * 
	 * @param display the display
	 */
	AwtDialogListener(final Display display) {
		assert display != null;

		this.display = display;
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.WINDOW_EVENT_MASK);
	}

	private/**
			 * Handle removed dialog.
			 * 
			 * @param awtDialog
			 *            the awt dialog
			 * @param removeListener
			 *            the remove listener
			 */
	void handleRemovedDialog(final Dialog awtDialog, final boolean removeListener) {
		assert awtDialog != null;
		assert modalDialogs != null;
		assert display != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// System.out.println("Remove dialog: " + awtDialog);
		if ( removeListener ) {
			awtDialog.removeComponentListener(this);
		}
		// Note: there is no isModal() check here because the dialog might
		// have been changed from modal to non-modal after it was opened. In
		// this case
		// the currently visible dialog would still act modal and we'd need to
		// unblock
		// SWT here when it goes away.
		if ( modalDialogs.remove(awtDialog) ) {
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					SwtInputBlocker.unblock();
				}
			});
		}
	}

	/**
	 * Handle added dialog.
	 * 
	 * @param awtDialog the awt dialog
	 */
	private void handleAddedDialog(final Dialog awtDialog) {
		assert awtDialog != null;
		assert modalDialogs != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// System.out.println("Add dialog: " + awtDialog);
		if ( modalDialogs.contains(awtDialog) || !awtDialog.isModal() || !awtDialog.isVisible() ) { return; }
		modalDialogs.add(awtDialog);
		awtDialog.addComponentListener(this);
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				SwtInputBlocker.block();
			}
		});
	}

	/**
	 * Request focus.
	 */
	void requestFocus() {
		// TODO: this does not always bring the dialog to the top
		// under some Linux desktops/window managers (e.g. metacity under
		// GNOME).
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				assert modalDialogs != null;

				final int size = modalDialogs.size();
				if ( size > 0 ) {
					final Dialog awtDialog = (Dialog) modalDialogs.get(size - 1);

					// In one case, a call to requestFocus() alone does not
					// bring the AWT dialog to the top. This happens if the
					// dialog is given a null parent frame. When opened, the
					// dialog
					// can be hidden by the SWT window even when it obtains
					// focus.
					// Calling toFront() solves the problem, but...
					//
					// There are still problems if the Metal look and feel is in
					// use.
					// The SWT window will hide the dialog the first time it is
					// selected. Once the dialog is brought back to the front by
					// the user, there is no further problem.
					//
					// Why? It looks like SWT is not being notified of lost
					// focus when
					// the Metal dialog first opens; subsequently, when focus is
					// regained, the
					// focus gain event is not posted to the SwtInputBlocker.
					//
					// The workaround is to use Windows look and feel, rather
					// than Metal.
					// System.out.println("Bringing to front");

					awtDialog.requestFocus();
					awtDialog.toFront();
				}
			}
		});
	}

	/**
	 * Handle opened window.
	 * 
	 * @param event the event
	 */
	private void handleOpenedWindow(final WindowEvent event) {
		assert event != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		final Window window = event.getWindow();
		if ( window instanceof Dialog ) {
			handleAddedDialog((Dialog) window);
		}
	}

	/**
	 * Handle closed window.
	 * 
	 * @param event the event
	 */
	private void handleClosedWindow(final WindowEvent event) {
		assert event != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// Dispose-based close
		final Window window = event.getWindow();
		if ( window instanceof Dialog ) {
			handleRemovedDialog((Dialog) window, true);
		}
	}

	/**
	 * Handle closing window.
	 * 
	 * @param event the event
	 */
	private void handleClosingWindow(final WindowEvent event) {
		assert event != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// System-based close
		final Window window = event.getWindow();
		if ( window instanceof Dialog ) {
			final Dialog dialog = (Dialog) window;
			// Defer until later. Bad things happen if
			// handleRemovedDialog() is called directly from
			// this event handler. The Swing dialog does not close
			// properly and its modality remains in effect.
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					// Remove dialog and component listener
					handleRemovedDialog(dialog, true);
				}
			});
		}
	}

	/**
	 * 
	 * @see java.awt.event.AWTEventListener#eventDispatched(java.awt.AWTEvent)
	 */
	@Override
	public void eventDispatched(final AWTEvent event) {
		assert event != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		switch (event.getID()) {
			case WindowEvent.WINDOW_OPENED:
				handleOpenedWindow((WindowEvent) event);
			break;

			case WindowEvent.WINDOW_CLOSED:
				handleClosedWindow((WindowEvent) event);
			break;

			case WindowEvent.WINDOW_CLOSING:
				handleClosingWindow((WindowEvent) event);
			break;

			default:
			break;
		}
	}

	/**
	 * 
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentHidden(final ComponentEvent e) {
		assert e != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// System.out.println("Component hidden");
		final Object obj = e.getSource();
		if ( obj instanceof Dialog ) {
			// so that we know if/when
			// it is set visible
			handleRemovedDialog((Dialog) obj, false);
		}
	}

	/**
	 * 
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentShown(final ComponentEvent e) {
		assert e != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// System.out.println("Component shown");
		final Object obj = e.getSource();
		if ( obj instanceof Dialog ) {
			handleAddedDialog((Dialog) obj);
		}
	}

	/**
	 * 
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(final ComponentEvent e) {}

	/**
	 * 
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentMoved(final ComponentEvent e) {}

}
