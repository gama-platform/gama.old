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

import java.awt.EventQueue;
import msi.gama.gui.swt.SwtGui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * The Class SwtFocusHandler.
 */
class SwtFocusHandler implements FocusListener, KeyListener {

	/** The composite. */
	private final Composite composite;

	/** The display. */
	private final Display display;

	/** The awt handler. */
	private AwtFocusHandler awtHandler;

	/**
	 * Instantiates a new swt focus handler.
	 * 
	 * @param composite the composite
	 */
	SwtFocusHandler(final Composite composite) {
		assert composite != null;
		assert SwtGui.getDisplay() != null; // On SWT event thread

		this.composite = composite;
		display = composite.getDisplay();
		composite.addFocusListener(this);
		composite.addKeyListener(this);
	}

	/**
	 * Sets the awt handler.
	 * 
	 * @param handler the new awt handler
	 */
	void setAwtHandler(final AwtFocusHandler handler) {
		assert handler != null;
		assert awtHandler == null; // this method is meant to be called once
		assert composite != null;
		assert SwtGui.getDisplay() != null; // On SWT event thread

		awtHandler = handler;

		// Dismiss Swing popups when the main window is moved. (It would be
		// better to dismiss popups whenever the titlebar is clicked, but
		// there does not seem to be a way.)
		final ControlAdapter controlAdapter = new ControlAdapter() {

			@Override
			public void controlMoved(final ControlEvent e) {
				assert awtHandler != null;
				awtHandler.postHidePopups();
			}
		};
		final Shell shell = composite.getShell();
		shell.addControlListener(controlAdapter);

		// Cleanup listeners on dispose
		composite.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
				shell.removeControlListener(controlAdapter);
			}
		});
	}

	/**
	 * Gain focus next.
	 */
	void gainFocusNext() {
		traverse(SWT.TRAVERSE_TAB_NEXT);
	}

	/**
	 * Gain focus previous.
	 */
	void gainFocusPrevious() {
		traverse(SWT.TRAVERSE_TAB_PREVIOUS);
	}

	/**
	 * Traverse.
	 * 
	 * @param traversal the traversal
	 */
	private void traverse(final int traversal) {
		assert composite != null;

		// Tab from the containing SWT component while
		// running on the SWT thread
		final Runnable r = new Runnable() {

			@Override
			public void run() {
				composite.traverse(traversal);
			}
		};
		display.asyncExec(r);
	}

	// boolean hasFocus() {
	// assert composite != null;
	//
	// // This will return true if the composite has focus, or if any
	// // foreign (e.g. AWT) child of the composite has focus.
	// if (display.isDisposed()) {
	// return false;
	// }
	// final boolean[] result = new boolean[1];
	// display.syncExec(new Runnable() {
	// public void run() {
	// result[0] = (!composite.isDisposed() &&
	// (display.getFocusControl() == composite));
	// }
	// });
	// return result[0];
	// }

	// ..................... Listener implementations

	/**
	 * 
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusGained(final FocusEvent e) {
		assert awtHandler != null;
		assert SwtGui.getDisplay() != null; // On SWT event thread

		// System.out.println("Gained: " + e.toString() + " (" + e.widget.getClass().getName() +
		// ")");
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				awtHandler.gainFocus();
			}
		});
	}

	/**
	 * 
	 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusLost(final FocusEvent e) {
		// System.out.println("Lost: " + e.toString() + " (" +
		// e.widget.getClass().getName() + ")");
	}

	/**
	 * 
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyPressed(final KeyEvent e) {
		assert SwtGui.getDisplay() != null; // On SWT event thread
		// GUI.debug("Key pressed (in SwtFocusHandler)=" + e.toString());
		// If the embedded swing root pane has no components to receive focus,
		// then there will be cases where the parent SWT composite will keep
		// focus. (For example, when tabbing into the root pane container).
		// By default, in these cases, the focus is swallowed by the Composite
		// and never escapes. This code allows tab and back-tab to do the
		// proper traversal to other SWT components from the composite.
		// TODO: other keys?
		if ( e.keyCode == SWT.TAB ) {
			// TODO: In some cases, this gobbles up all the tabs, even from AWT
			// children.
			// Find a more selective way.
			/*
			 * if (e.stateMask == SWT.NONE) { traverse(SWT.TRAVERSE_TAB_NEXT); } else if
			 * (e.stateMask == SWT.SHIFT) { traverse(SWT.TRAVERSE_TAB_PREVIOUS); }
			 */
		}
	}

	/**
	 * 
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	@Override
	public void keyReleased(final KeyEvent e) {}

}
