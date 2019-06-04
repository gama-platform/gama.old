/*********************************************************************************************
 *
 * 'WorkaroundForIssue2476.java, in plugin ummisco.gama.java2d, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.java2d;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class WorkaroundForIssue2745 {

	static {
		DEBUG.OFF();
	}

	public static void installOn(final AWTDisplayView view) {
		// Only installs on macOS
		if (!PlatformHelper.isMac()) { return; }
		final IPartListener2 pl = new IPartListener2() {

			void forceLayout() {
				final Control c = view.controlToSetFullScreen();
				if (c == null || c.getParent() == null) { return; }
				c.setVisible(false);
				c.getParent().layout(true, true);
				c.setVisible(true);
			}

			@Override
			public void partActivated(final IWorkbenchPartReference partRef) {
				if (partRef.getPart(false).equals(view)) {
					DEBUG.OUT("Part becomes activated ");
					forceLayout();
				}

			}

			@Override
			public void partClosed(final IWorkbenchPartReference partRef) {
				// if (partRef.getPart(false).equals(view)) {
				// // DEBUG.OUT("Part becomes closed ");
				// }
			}

			@Override
			public void partDeactivated(final IWorkbenchPartReference partRef) {
				// if (partRef.getPart(false).equals(view)) {
				// // DEBUG.OUT("Part becomes deactivated ");
				// }
			}

			@Override
			public void partOpened(final IWorkbenchPartReference partRef) {
				// if (partRef.getPart(false).equals(view)) {
				// DEBUG.OUT("Part becomes opened ");
				// }
			}

			@Override
			public void partBroughtToTop(final IWorkbenchPartReference part) {
				// if (part.getPart(false).equals(view)) {
				// DEBUG.OUT("Part becomes brought to top ");
				// // view.forceLayout();
				// }
			}

			@Override
			public void partHidden(final IWorkbenchPartReference partRef) {
				// if (partRef.getPart(false).equals(view)) {
				// // DEBUG.OUT("Part becomes hidden ");
				// }
			}

			@Override
			public void partVisible(final IWorkbenchPartReference partRef) {
				// if (partRef.getPart(false).equals(view)) {
				// DEBUG.OUT("Part becomes visible ");
				// }
			}

			@Override
			public void partInputChanged(final IWorkbenchPartReference partRef) {
				// if (partRef.getPart(false).equals(view)) {
				// // DEBUG.OUT("Part has its input changed ");
				// }
			}
		};
		WorkbenchHelper.getPage().addPartListener(pl);

	}

}
