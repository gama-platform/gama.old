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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

import ummisco.gama.java2d.AWTDisplayView;
import ummisco.gama.java2d.Java2DDisplaySurface;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class SwingControl.
 */
public class SwingControlMac extends SwingControl {

	/**
	 * Instantiates a new swing control.
	 *
	 * @param parent
	 *            the parent
	 * @param awtDisplayView
	 * @param style
	 *            the style
	 */
	public SwingControlMac(final Composite parent, final AWTDisplayView view, final Java2DDisplaySurface component,
			final int style) {
		super(parent, view, component, style);
	}

	@Override
	protected void populate() {
		if (isDisposed()) return;
		if (!populated) {
			populated = true;
			WorkbenchHelper.asyncRun(() -> {
				frame = SWT_AWT.new_Frame(SwingControlMac.this);
				frame.setAlwaysOnTop(false);
				if (swingKeyListener != null) { frame.addKeyListener(swingKeyListener); }
				if (swingMouseListener != null) { frame.addMouseMotionListener(swingMouseListener); }
				frame.add(surface);

				MouseListener ml = new MouseAdapter() {

					@Override
					public void mouseExited(final MouseEvent e) {
						if (surface.isFocusOwner() && !surface.contains(e.getPoint())) {
							frame.setVisible(false);
							frame.setVisible(true);
							WorkbenchHelper.asyncRun(() -> getShell().forceActive());
						}

					}

				};
				frame.addMouseListener(ml);
				surface.addMouseListener(ml);

			});
			addListener(SWT.Dispose, event -> EventQueue.invokeLater(() -> {
				try {
					frame.remove(surface);
				} catch (final Exception e) {}

			}));
		}
	}

	/**
	 * Overridden to propagate the size to the embedded Swing component.
	 */
	@Override
	protected void privateSetDimensions(final int width, final int height) {
		// Assignment necessary for #3313 and #3239
		WorkbenchHelper.asyncRun(() -> {
			// Solves a problem where the last view on HiDPI screens on Windows would be outscaled
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
