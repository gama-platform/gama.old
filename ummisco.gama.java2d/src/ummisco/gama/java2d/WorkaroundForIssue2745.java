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

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class WorkaroundForIssue2745 {

	static {
		DEBUG.ON();
	}

	public static void installOn(final AWTDisplayView view) {
		// Install only on MacOS
		if (!PlatformHelper.isMac()) { return; }
		final IPartListener2 pl = new IPartListener2() {

			@Override
			public void partActivated(final IWorkbenchPartReference partRef) {
				if (partRef.getPart(false).equals(view)) {
					DEBUG.OUT("Part becomes activated ");
					DEBUG.OUT("Forcing layout of " + view.toString());
					WorkbenchHelper.asyncRun(() -> view.forceLayout());
				}

			}

			@Override
			public void partClosed(final IWorkbenchPartReference partRef) {
				if (partRef.getPart(false).equals(view)) {
					DEBUG.OUT("Part becomes closed ");
				}
			}

			@Override
			public void partDeactivated(final IWorkbenchPartReference partRef) {
				if (partRef.getPart(false).equals(view)) {
					DEBUG.OUT("Part becomes deactivated ");
				}
			}

			@Override
			public void partOpened(final IWorkbenchPartReference partRef) {
				if (partRef.getPart(false).equals(view)) {
					DEBUG.OUT("Part becomes opened ");
				}
			}

			@Override
			public void partBroughtToTop(final IWorkbenchPartReference part) {
				if (part.getPart(false).equals(view)) {
					DEBUG.OUT("Part becomes brought to top ");
				}
			}

			@Override
			public void partHidden(final IWorkbenchPartReference partRef) {
				if (partRef.getPart(false).equals(view)) {
					DEBUG.OUT("Part becomes hidden ");
				}
			}

			@Override
			public void partVisible(final IWorkbenchPartReference partRef) {
				if (partRef.getPart(false).equals(view)) {
					DEBUG.OUT("Part becomes visible ");
					DEBUG.OUT("Forcing layout of " + view.toString());
					WorkbenchHelper.asyncRun(() -> view.forceLayout());
				}
			}

			@Override
			public void partInputChanged(final IWorkbenchPartReference partRef) {
				if (partRef.getPart(false).equals(view)) {
					DEBUG.OUT("Part has its input changed ");
				}
			}
		};
		WorkbenchHelper.getPage().addPartListener(pl);

	}

}
