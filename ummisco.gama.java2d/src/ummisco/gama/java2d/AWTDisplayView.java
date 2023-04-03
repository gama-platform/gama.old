/*******************************************************************************************************
 *
 * AWTDisplayView.java, in ummisco.gama.java2d, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.java2d;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.runtime.PlatformHelper;
import ummisco.gama.java2d.swing.SwingControl;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.displays.LayeredDisplayView;
import ummisco.gama.ui.views.displays.SWTLayeredDisplayMultiListener;

/**
 * The Class AWTDisplayView.
 */
public class AWTDisplayView extends LayeredDisplayView {

	@Override
	protected Composite createSurfaceComposite(final Composite parent) {
		if (getOutput() == null) return null;
		surfaceComposite = SwingControl.create(parent, AWTDisplayView.this, (Java2DDisplaySurface) getDisplaySurface(),
				SWT.NO_FOCUS);
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
	public void setFocus() {
		// Uncommenting this method seems to fix #3325. Should be tested !
		// DEBUG.OUT("Part " + getTitle() + " gaining focus");
		if (getParentComposite() != null && !getParentComposite().isDisposed()
				&& !getParentComposite().isFocusControl()) {
			getParentComposite().forceFocus(); // Necessary ?
		}
	}

	@Override
	public void focusCanvas() {
		WorkbenchHelper.asyncRun(() -> centralPanel.forceFocus());
	}


	@Override
	public IDisposable getMultiListener() {
		SWTLayeredDisplayMultiListener listener = (SWTLayeredDisplayMultiListener) super.getMultiListener();
		if (PlatformHelper.isMac() || PlatformHelper.isLinux()) {
			// See Issue #3426
			SwingControl control = (SwingControl) surfaceComposite;
			control.setKeyListener(listener.getKeyAdapterForAWT());
			if (PlatformHelper.isLinux()) { control.setMouseListener(listener.getMouseAdapterForAWT()); }
		}
		return listener;
	}

	@Override
	public boolean is2D() {
		return true;
	}

}