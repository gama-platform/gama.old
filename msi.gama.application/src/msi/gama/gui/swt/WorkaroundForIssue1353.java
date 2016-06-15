/**
 * Created by drogoul, 28 déc. 2015
 *
 */
package msi.gama.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.Platform;

/**
 * Class WorkaroundForIssue1353. Only for MacOS X, Eclipse Mars and Java 1.7
 *
 * @author drogoul
 * @since 28 déc. 2015
 *
 */
public class WorkaroundForIssue1353 {

	public static class PartListener implements IPartListener2 {

		@Override
		public void partActivated(final IWorkbenchPartReference partRef) {
			// System.out.println("Activating " + partRef.getPartName());
			showShell();
		}

		@Override
		public void partClosed(final IWorkbenchPartReference partRef) {}

		@Override
		public void partDeactivated(final IWorkbenchPartReference partRef) {
			// System.out.println("Deactivating " + partRef.getPartName());
		}

		@Override
		public void partOpened(final IWorkbenchPartReference partRef) {}

		@Override
		public void partBroughtToTop(final IWorkbenchPartReference part) {}

		@Override
		public void partHidden(final IWorkbenchPartReference partRef) {}

		@Override
		public void partVisible(final IWorkbenchPartReference partRef) {}

		@Override
		public void partInputChanged(final IWorkbenchPartReference partRef) {}
	}

	private static Shell shell;
	private final static PartListener listener = new PartListener();

	public static void showShell() {
		if ( shell != null ) // The fix has been installed
		{
			SwtGui.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					// System.out.println("Displaying invisible shell");
					shell.open();
					shell.setVisible(false);

				}
			});

		}
	}

	private static void createShell() {
		if ( shell == null ) {
			shell = new Shell(SwtGui.getDisplay(), SWT.APPLICATION_MODAL);
			shell.setSize(5, 5);
			shell.setAlpha(0);
			shell.setBackground(IGamaColors.BLACK.color());
		}

	}

	public static void install() {
		if ( !Platform.isCocoa() ) { return; }
		createShell();
		SwtGui.getPage().addPartListener(listener);

	}

}
