/*********************************************************************************************
 *
 * 'SwtInputBlocker.java, in plugin ummisco.gama.java2d, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.java2d.swing;

import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import ummisco.gama.ui.utils.PlatformHelper;

/**
 * This class, together with {@link AwtDialogListener}, ensures the proper modal
 * behavior of Swing dialogs when running within a SWT environment. It allows to
 * block SWT input while the AWT/Swing modal dialog is visible.
 *
 * @see AwtDialogListener
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SwtInputBlocker {

	static private volatile SwtInputBlocker instance = null;
	static private int blockCount = 0;
	private Shell shell;
	private AwtDialogListener dialogListener = null;
	private final Shell parentShell;
	private Stack /* of Shell */ shellsWithActivateListener;

	private SwtInputBlocker(final Shell parent, final AwtDialogListener dialogListener) {
		this.parentShell = parent;
		this.dialogListener = dialogListener;
	}

	private final Listener activateListener = event -> ThreadingHandler.getInstance().asyncExec(shell.getDisplay(),
			() -> {
				// On some platforms (e.g. Linux/GTK), the 0x0 shell still
				// appears as a dot
				// on the screen, so make it invisible by moving it below
				// other windows. This
				// is unnecessary under Windows and causes a flash, so only
				// make the call when necessary.
				// note: would like to do this too:
				// parentShell.moveBelow(null);, but see bug 170774
				shell.moveBelow(null);
				if (dialogListener != null)
					dialogListener.requestFocus();
			});

	private final FocusListener focusListener = new FocusAdapter() {

		@Override
		public void focusGained(final FocusEvent e) {
			dialogListener.requestFocus();
		}
	};

	private void open() {
		assert Display.getCurrent() != null; // On SWT event thread

		// TODO: Will SWT.NO_FOCUS help in any way here?
		// TODO: Another shell is not necessary here if
		// AwtEnvironment.createDialogParentFrame is used.
		// Construct with the current display, rather than parent. This reduces
		// problems where
		// the AWT dialog gets covered or does not have focus when opened.
		// Use ON_TOP to prevent a Windows task bar button
		shell = new Shell(Display.getCurrent(), SWT.APPLICATION_MODAL | SWT.NO_TRIM | SWT.ON_TOP);
		shell.setSize(0, 0);

		// Add listener(s) to force focus back to the AWT dialog if SWT gets
		// control
		if (PlatformHelper.isGtk()) {
			// Under GTK, focus events are not available to detect this
			// condition,
			// so use the activate event.
			// TODO: is it necessary to do this for all parents?
			shellsWithActivateListener = new Stack();
			Shell shell = parentShell;
			while (shell != null) {
				shell.addListener(SWT.Activate, activateListener);
				shellsWithActivateListener.push(shell);
				final Composite composite = shell.getParent();
				shell = composite != null ? composite.getShell() : null;
			}
		} else {
			// Otherwise, restore focus to awt if the shell gets focus
			// TODO: test on MacOS and Motif
			shell.addFocusListener(focusListener);
		}
		shell.open();

		final Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// If windows from other applications have been opened while SWT was
		// being blocked,
		// the original parent shell can get lost under those windows after the
		// blocking
		// is stopped. Force the parent shell back to the front here.
		if (!parentShell.isDisposed()) {
			parentShell.forceActive();
		}
	}

	private void close() {
		assert shell != null;

		if (PlatformHelper.isGtk()) {
			while (!shellsWithActivateListener.isEmpty()) {
				final Shell shell = (Shell) shellsWithActivateListener.pop();
				if (!shell.isDisposed()) {
					shell.removeListener(SWT.Activate, activateListener);
				}
			}
		}
		shell.dispose();
	}

	public static void unblock() {
		assert blockCount >= 0;
		assert Display.getCurrent() != null; // On SWT event thread

		// System.out.println("Deleting SWT blocker");
		if (blockCount == 0) {
			return;
		}
		if (blockCount == 1 && instance != null) {
			instance.close();
			instance = null;
		}
		blockCount--;
	}

	public static void block(final AwtDialogListener dialogListener) {
		assert blockCount >= 0;

		// System.out.println("Creating SWT blocker");
		final Display display = Display.getCurrent();
		assert display != null; // On SWT event thread

		blockCount++;
		if (blockCount == 1) {

			// get a shell to parent the blocking dialog
			final Shell shell = getShell(display);

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
			// setEnabled(false) is another option, but
			// on some platforms that will grey the disabled controls.
			if (shell != null) {
				instance = new SwtInputBlocker(shell, dialogListener);
				instance.open();
			}
		}
	}

	// Find a shell to use, giving preference to the active shell.
	static private Shell getShell(final Display display) {
		Shell shell = display.getActiveShell();
		if (shell == null) {
			final Shell[] allShells = display.getShells();
			if (allShells.length > 0) {
				shell = allShells[0];
			}
		}
		return shell;
	}
}
