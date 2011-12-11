/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.util.swing;

import msi.gama.gui.application.GUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * The Class SwtInputBlocker.
 */
class SwtInputBlocker extends Dialog {

	/** The instance. */
	static private SwtInputBlocker instance = null;

	/** The block count. */
	static private int blockCount = 0;

	/** The shell. */
	private Shell shell;

	/**
	 * Instantiates a new swt input blocker.
	 * 
	 * @param parent
	 *            the parent
	 */
	private SwtInputBlocker(final Shell parent) {
		super(parent, SWT.NONE);
	}

	/**
	 * Open.
	 * 
	 * @return the object
	 */
	private Object open() {
		assert GUI.getDisplay() != null; // On SWT event thread

		final Shell parent = getParent();
		shell = new Shell(parent, SWT.APPLICATION_MODAL);
		shell.setSize(0, 0);
		shell.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(final FocusEvent e) {
				// On some platforms (e.g. Linux/GTK), the 0x0 shell still
				// appears as a
				// dot
				// on the screen, so make it invisible by moving it below other
				// windows.
				// This
				// is unnecessary under Windows and causes a flash, so only make
				// the call
				// when necessary.
				if (Platform.isGtk()) shell.moveBelow(null);
				AwtEnvironment.getInstance(shell.getDisplay())
						.requestAwtDialogFocus();
			}
		});
		shell.open();

		final Display display = parent.getDisplay();
		while (!shell.isDisposed())
			if (!display.readAndDispatch()) display.sleep();
		return null;
	}

	/**
	 * Close.
	 */
	private void close() {
		assert shell != null;

		shell.dispose();
	}

	/**
	 * Unblock.
	 */
	static void unblock() {
		assert blockCount >= 0;
		assert GUI.getDisplay() != null; // On SWT event thread

		// System.out.println("Deleting SWT blocker");
		if (blockCount == 0) return;
		if (blockCount == 1 && instance != null) {
			instance.close();
			instance = null;
		}
		blockCount--;
	}

	/**
	 * Block.
	 */
	static void block() {
		assert blockCount >= 0;

		// System.out.println("Creating SWT blocker");
		final Display display = GUI.getDisplay();
		assert display != null; // On SWT event thread

		blockCount++;
		if (blockCount == 1) {
			assert instance == null; // should be no existing blocker

			// get a shell to parent the blocking dialog
			final Shell shell = AwtEnvironment.getInstance(display).getShell();

			// If there is a shell to block, block input now. If there are no
			// shells,
			// then there is no input to block. In the case of no shells, we are
			// not
			// protecting against a shell that might get created later. This is
			// a rare
			// enough case to skip, at least for now. In the future, a listener
			// could be
			// added to cover it.
			// TODO: if (shell==null) add listener to block shells created
			// later?
			//
			// Block is implemented with a hidden modal dialog. Using
			// setEnabled(false) is
			// another option, but
			// on some platforms that will grey the disabled controls.
			if (shell != null) {
				instance = new SwtInputBlocker(shell);
				instance.open();
			}
		}
	}

}
