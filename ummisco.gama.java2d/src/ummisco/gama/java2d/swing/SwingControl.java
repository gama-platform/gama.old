/*******************************************************************************************************
 *
 * SwingControl.java, in ummisco.gama.java2d, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.java2d.swing;

import java.awt.Frame;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

import msi.gama.runtime.PlatformHelper;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.java2d.AWTDisplayView;
import ummisco.gama.java2d.Java2DDisplaySurface;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class SwingControl.
 */
public abstract class SwingControl extends Composite {

	static {
		DEBUG.OFF();
	}

	/**
	 * Creates the.
	 *
	 * @param parent
	 *            the parent
	 * @param view
	 *            the view
	 * @param surface
	 *            the surface
	 * @param style
	 *            the style
	 * @return the composite
	 */
	public static Composite create(final Composite parent, final AWTDisplayView view,
			final Java2DDisplaySurface surface, final int style) {
		if (PlatformHelper.isLinux()) return new SwingControlLinux(parent, view, surface, style);
		if (PlatformHelper.isWindows()) return new SwingControlWin(parent, view, surface, style);
		if (PlatformHelper.isMac()) return new SwingControlMac(parent, view, surface, style);
		return null;
	}

	/** The multi listener. */
	KeyListener swingKeyListener;

	/** The swing mouse listener. */
	MouseMotionListener swingMouseListener;

	/** The frame. */
	Frame frame;

	/** The surface. */
	final Java2DDisplaySurface surface;

	/** The populated. */
	volatile boolean populated = false;

	/** The visible. */
	volatile boolean visible = false;

	/**
	 * Instantiates a new swing control.
	 *
	 * @param parent
	 *            the parent
	 * @param awtDisplayView
	 * @param style
	 *            the style
	 */
	public SwingControl(final Composite parent, final AWTDisplayView view, final Java2DDisplaySurface component,
			final int style) {
		super(parent, style | ((style & SWT.BORDER) == 0 ? SWT.EMBEDDED : 0) | SWT.NO_BACKGROUND);
		setEnabled(false);
		this.surface = component;
		IPartListener2 listener = new IPartListener2() {

			@Override
			public void partHidden(final IWorkbenchPartReference partRef) {
				if (partRef.getPart(false).equals(view)) {
					// DEBUG.OUT("Hidden event received for " + view.getTitle());
					visible = false;
				}
			}

			@Override
			public void partVisible(final IWorkbenchPartReference partRef) {
				// DEBUG.OUT("Visible event received for " + view.getTitle());
				if (partRef.getPart(false).equals(view)) { visible = true; }
			}
		};
		WorkbenchHelper.getPage().addPartListener(listener);
		addListener(SWT.Dispose, event -> { WorkbenchHelper.getPage().removePartListener(listener); });
		setLayout(new FillLayout());
	}

	@Override
	public void checkWidget() {}

	@Override
	public boolean isFocusControl() {
		boolean result = false;
		try {
			result = super.isFocusControl();
		} catch (final Exception e) {
			// Nothing. Eliminates annoying exceptions when closing Java2D displays.
		}
		return result;
	}

	/**
	 * Populate.
	 */
	protected abstract void populate();

	/**
	 * Overridden to propagate the size to the embedded Swing component.
	 */
	@Override
	public final void setBounds(final int x, final int y, final int width, final int height) {
		// DEBUG.OUT("-- SwingControl bounds set to " + x + " " + y + " | " + width + " " + height);
		populate();
		// See Issue #3426
		super.setBounds(x, y, width, height);
		this.privateSetDimensions(width, height);
	}

	/**
	 * Private set dimensions.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	protected void privateSetDimensions(final int width, final int height) {}

	/**
	 * Sets the key listener.
	 *
	 * @param adapter
	 *            the new key listener
	 */
	public void setKeyListener(final KeyListener adapter) { swingKeyListener = adapter; }

	/**
	 * Sets the mouse listener.
	 *
	 * @param adapter
	 *            the new mouse listener
	 */
	public void setMouseListener(final MouseMotionListener adapter) { swingMouseListener = adapter; }

}
