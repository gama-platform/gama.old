/*******************************************************************************************************
 *
 * AWTDisplayView.java, in ummisco.gama.java2d, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.java2d;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import ummisco.gama.java2d.swing.SwingControl;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.displays.LayeredDisplayView;

/**
 * The Class AWTDisplayView.
 */
public class AWTDisplayView extends LayeredDisplayView {

	@Override
	protected Composite createSurfaceComposite(final Composite parent) {

		if (getOutput() == null) return null;

		surfaceComposite = new SwingControl(parent, SWT.NO_FOCUS) {

			@Override
			protected Java2DDisplaySurface createSwingComponent() {
				return (Java2DDisplaySurface) getDisplaySurface();
			}

		};
		surfaceComposite.setEnabled(false);
		// WorkaroundForIssue1594.installOn(this, parent, surfaceComposite, (Java2DDisplaySurface) getDisplaySurface());
		// WorkaroundForIssue2745.installOn(this);
		// WorkaroundForIssue1353.install();
		return surfaceComposite;
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		super.ownCreatePartControl(c);
		if (getOutput().getData().fullScreen() > -1) {
			new Thread(() -> { WorkbenchHelper.runInUI("FS", 1000, m -> toggleFullScreen()); }).start();
		}
	}

	@Override
	public void focusCanvas() {
		WorkbenchHelper.asyncRun(() -> centralPanel.forceFocus());
	}

	@Override
	protected boolean canBeSynchronized() {
		return true;
	}

}