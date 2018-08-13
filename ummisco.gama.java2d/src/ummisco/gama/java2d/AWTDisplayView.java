/*********************************************************************************************
 *
 * 'AWTDisplayView.java, in plugin ummisco.gama.java2d, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.java2d;

import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import ummisco.gama.java2d.swing.SwingControl;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.displays.LayeredDisplayView;

public class AWTDisplayView extends LayeredDisplayView {

	public static long REALIZATION_TIME_OUT = 1000;
	public boolean isVisible;

	@Override
	public Java2DDisplaySurface getDisplaySurface() {
		return (Java2DDisplaySurface) super.getDisplaySurface();
	}

	@Override
	protected Composite createSurfaceComposite(final Composite parent) {

		if (getOutput() == null) { return null; }

		surfaceComposite = new SwingControl(parent, SWT.NO_FOCUS) {

			@Override
			protected JComponent createSwingComponent() {
				return getDisplaySurface();
			}

			@Override
			protected void preferredSizeChanged(final Point minSize, final Point prefSize, final Point maxSize) {
				WorkbenchHelper.asyncRun(() -> {
					surfaceComposite.setSize(prefSize);
					parent.layout(true, true);
				});

			}

			@Override
			public Composite getLayoutAncestor() {
				// AD 02/16 Seems necessary to return null for displays to show
				// up and correctly initialize their graphics environment
				return null;
			}

			@Override
			public boolean isSwtTabOrderExtended() {
				return false;
			}

			@Override
			public void afterComponentCreatedSWTThread() {}

			@Override
			public void checkWidget() {

			}

			@Override
			public void afterComponentCreatedAWTThread() {}
		};
		surfaceComposite.setEnabled(false);
		WorkaroundForIssue1594.installOn(AWTDisplayView.this, parent, surfaceComposite, getDisplaySurface());

		return surfaceComposite;
	}

	/**
	 * Wait for the AWT environment to be initialized, preventing a thread lock when two views want to open at the same
	 * time. Must not be called in neither the AWT or the SWT thread. A configurable timeout is applied, so that other
	 * views are not blocked. It remains to be seen what to do if this times out, as we should normally cancel the view.
	 * 
	 * @see msi.gama.common.interfaces.IGamaView#waitToBeRealized()
	 */
	//
	// @Override
	// public void waitToBeRealized() {
	// // if (PlatformHelper.isWin32()) { return; }
	// final long start = System.currentTimeMillis();
	// final long now = start;
	// final boolean openable = false;
	//
	// // while (/* isVisible && */ !openable) {
	// // try {
	// // Thread.sleep(GamaPreferences.Displays.CORE_OUTPUT_DELAY.getValue());
	// // } catch (final InterruptedException e) {
	// // e.printStackTrace();
	// // }
	// // now = System.currentTimeMillis();
	// // openable = now - start > REALIZATION_TIME_OUT || this.getDisplaySurface().isRealized();
	// // }
	// // DEBUG.LOG("Realized in " + (now - start) + "ms");
	//
	// }

	@Override
	public List<String> getCameraNames() {
		return Collections.EMPTY_LIST;
	}

}