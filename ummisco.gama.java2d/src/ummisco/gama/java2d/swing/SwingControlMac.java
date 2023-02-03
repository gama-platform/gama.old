/*******************************************************************************************************
 *
 * SwingControlMac.java, in ummisco.gama.java2d, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
			WorkbenchHelper.asyncRun(() -> {
				frame = SWT_AWT.new_Frame(SwingControlMac.this);
				frame.setAlwaysOnTop(false);
				if (swingKeyListener != null) { frame.addKeyListener(swingKeyListener); }
				if (swingMouseListener != null) { frame.addMouseMotionListener(swingMouseListener); }
				frame.add(surface);

				frame.addMouseListener(ml);
				surface.addMouseListener(ml);

			});
			addListener(SWT.Dispose, event -> EventQueue.invokeLater(() -> {
				try {
					frame.removeMouseListener(ml);
					if (swingKeyListener != null) { frame.removeKeyListener(swingKeyListener); }
					if (swingMouseListener != null) { frame.removeMouseMotionListener(swingMouseListener); }
					surface.removeMouseListener(ml);
					frame.remove(surface);
					surface.dispose();
					frame.dispose();
				} catch (final Exception e) {}

			}));
		}
	}

}
