/*******************************************************************************************************
 *
 * SwingControl.java, in ummisco.gama.java2d, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.java2d.swing;

import java.awt.EventQueue;

import org.eclipse.swt.widgets.Composite;

import msi.gama.runtime.PlatformHelper;
import ummisco.gama.java2d.AWTDisplayView;
import ummisco.gama.java2d.Java2DDisplaySurface;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class SwingControl.
 */
public class SwingControlWin extends SwingControl {

	/**
	 * Instantiates a new swing control.
	 *
	 * @param parent
	 *            the parent
	 * @param awtDisplayView
	 * @param style
	 *            the style
	 */
	public SwingControlWin(final Composite parent, final AWTDisplayView view, final Java2DDisplaySurface component,
			final int style) {
		super(parent, view, component, style);
	}

	/**
	 * Overridden to propagate the size to the embedded Swing component.
	 */
	@Override
	public void setBounds(final int x, final int y, final int width, final int height) {
		// DEBUG.OUT("-- SwingControl bounds set to " + x + " " + y + " | " + width + " " + height);
		populate();
		// See Issue #3426

		super.setBounds(x, y, width, height);
		// Assignment necessary for #3313 and #3239
		WorkbenchHelper.asyncRun(() -> {
			// Solves a problem where the last view on HiDPI screens on Windows would be outscaled
			if (PlatformHelper.isWindows()) { this.requestLayout(); }
			EventQueue.invokeLater(() -> {
				// DEBUG.OUT("Set size sent by SwingControl " + width + " " + height);
				// frame.setBounds(x, y, width, height);
				// frame.setVisible(false);
				surface.setSize(width, height);
				// frame.setVisible(true);
			});

		});

	}

}
