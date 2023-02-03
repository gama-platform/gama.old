/*******************************************************************************************************
 *
 * SwingControlLinux.java, in ummisco.gama.java2d, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.java2d.swing;

import java.awt.EventQueue;

import javax.swing.JApplet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

import ummisco.gama.java2d.AWTDisplayView;
import ummisco.gama.java2d.Java2DDisplaySurface;
import ummisco.gama.java2d.WorkaroundForIssue2476;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class SwingControl.
 */
public class SwingControlLinux extends SwingControl {

	/**
	 * Instantiates a new swing control.
	 *
	 * @param parent
	 *            the parent
	 * @param awtDisplayView
	 * @param style
	 *            the style
	 */
	public SwingControlLinux(final Composite parent, final AWTDisplayView view, final Java2DDisplaySurface component,
			final int style) {
		super(parent, view, component, style);
	}

	@Override
	protected void populate() {
		if (isDisposed()) return;
		if (!populated) {
			populated = true;
			WorkbenchHelper.asyncRun(() -> {
				JApplet applet = new JApplet();
				frame = SWT_AWT.new_Frame(SwingControlLinux.this);
				frame.setAlwaysOnTop(false);
				if (swingKeyListener != null) { frame.addKeyListener(swingKeyListener); }
				if (swingMouseListener != null) { applet.addMouseMotionListener(swingMouseListener); }
				surface.setVisibility(() -> visible);
				applet.getContentPane().add(surface);
				WorkaroundForIssue2476.installOn(applet, surface);
				frame.add(applet);
				addListener(SWT.Dispose, event -> EventQueue.invokeLater(() -> {
					try {
						applet.getContentPane().remove(surface);
						frame.remove(applet);
						surface.dispose();
						frame.dispose();
					} catch (final Exception e) {}

				}));
			});

		}
	}

}
