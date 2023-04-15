/*******************************************************************************************************
 *
 * WorkaroundForIssue1353.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

import msi.gama.runtime.PlatformHelper;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class WorkaroundForIssue1353. Only for MacOS X, Eclipse Mars and Java 1.7
 *
 * @author drogoul
 * @since 28 déc. 2015
 *
 */
public class WorkaroundForIssue1353 {

	static {
		DEBUG.OFF();
	}

	/**
	 * The listener interface for receiving part events.
	 * The class that is interested in processing a part
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addPartListener<code> method. When
	 * the part event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see PartEvent
	 */
	public static class PartListener implements IPartListener2 {

		@Override
		public void partActivated(final IWorkbenchPartReference partRef) {
			showShell();
		}

	}

	/** The shell. */
	private static Shell shell;
	
	/** The Constant listener. */
	private static final PartListener listener = new PartListener();

	/**
	 * Show shell.
	 */
	public static void showShell() {
		if (shell != null) {
			WorkbenchHelper.asyncRun(() -> {
				DEBUG.OUT("Showing shell");
				getShell().open();
				getShell().setVisible(false);

			});
		}

	}

	/**
	 * Gets the shell.
	 *
	 * @return the shell
	 */
	private static Shell getShell() {
		if (shell == null || shell.isDisposed() || shell.getShell() == null || shell.getShell().isDisposed()) {
			createShell();
		}
		return shell;
	}

	/**
	 * Creates the shell.
	 */
	private static void createShell() {
		DEBUG.OUT("Shell created");
		shell = new Shell(WorkbenchHelper.getShell(), SWT.APPLICATION_MODAL);
		shell.setSize(5, 5);
		shell.setAlpha(0);
		shell.setBackground(IGamaColors.BLACK.color());
	}

	/**
	 * Install.
	 */
	public static void install() {
		if (!PlatformHelper.isMac() || (shell != null)) return;
		DEBUG.OUT(WorkaroundForIssue1353.class.getSimpleName() + " installed");
		WorkbenchHelper.run(() -> {
			createShell();
			WorkbenchHelper.getPage().addPartListener(listener);
		});

	}

	/**
	 * Checks if is installed.
	 *
	 * @return true, if is installed
	 */
	public static boolean isInstalled() { return shell != null; }

	/**
	 * Removes the.
	 */
	public static void remove() { // NO_UCD (unused code)

		if (shell == null) return;
		WorkbenchHelper.run(() -> {
			shell.dispose();
			shell = null;
			WorkbenchHelper.getPage().removePartListener(listener);
		});
		DEBUG.OUT(WorkaroundForIssue1353.class.getSimpleName() + " removed");
	}

}
