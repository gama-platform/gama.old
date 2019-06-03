/*********************************************************************************************
 *
 * 'SwingControl.java, in plugin ummisco.gama.java2d, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.java2d.swing;

import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.LayoutFocusTraversalPolicy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

public abstract class SwingControl extends Composite {

	JApplet applet;
	Frame frame;
	boolean populated = false;

	public SwingControl(final Composite parent, final int style) {
		super(parent, style | ((style & SWT.BORDER) == 0 ? SWT.EMBEDDED : 0) | SWT.NO_BACKGROUND);
		setLayout(new FillLayout());
		addListener(SWT.Dispose, event -> EventQueue.invokeLater(() -> {
			try {
				frame.remove(applet);
			} catch (final Exception e) {}

		}));
	}

	protected void populate() {
		if (isDisposed()) { return; }
		if (!populated) {
			populated = true;
			frame = SWT_AWT.new_Frame(this);
			EventQueue.invokeLater(() -> {
				applet = new JApplet();
				if (PlatformHelper.isWindows()) {
					applet.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
				}
				frame.add(applet);
				applet.getRootPane().getContentPane().add(createSwingComponent());
				WorkbenchHelper.asyncRun(() -> SwingControl.this.getParent().layout(true, true));
			});
		}
	}

	/**
	 * Creates the embedded Swing component. This method is called from the AWT event thread.
	 *
	 * @return a non-null Swing component
	 */
	protected abstract JComponent createSwingComponent();

	/**
	 * Overridden to propagate the size to the embedded Swing component.
	 */
	@Override
	public void setBounds(final Rectangle rect) {
		populate();
		super.setBounds(rect);
	}

	/**
	 * Overridden to propagate the size to the embedded Swing component.
	 */
	@Override
	public void setBounds(final int x, final int y, final int width, final int height) {
		populate();
		super.setBounds(x, y, width, height);
	}

	public JApplet getTopLevelContainer() {
		return applet;
	}

}
