/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.util.swing;

import msi.gama.gui.application.GUI;

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Composite;

/**
 * The listener interface for receiving cleanResize events. The class that is
 * interested in processing a cleanResize event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addCleanResizeListener<code> method. When
 * the cleanResize event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see CleanResizeEvent
 */
class CleanResizeListener extends ControlAdapter {

	/** The old rect. */
	private Rectangle oldRect = null;

	/**
	 * 
	 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
	 */
	@Override
	public void controlResized(final ControlEvent e) {
		assert e != null;
		assert GUI.getDisplay() != null; // On SWT event thread

		// Prevent garbage from Swing lags during resize. Fill exposed areas
		// with background color.
		final Composite composite = (Composite) e.widget;
		// Rectangle newRect = composite.getBounds();
		// newRect = composite.getDisplay().map(composite.getParent(),
		// composite,
		// newRect);
		final Rectangle newRect = composite.getClientArea();
		if (oldRect != null) {
			final int heightDelta = newRect.height - oldRect.height;
			final int widthDelta = newRect.width - oldRect.width;
			if (heightDelta > 0 || widthDelta > 0) {
				final GC gc = new GC(composite);
				try {
					gc.fillRectangle(newRect.x, oldRect.height, newRect.width,
							heightDelta);
					gc.fillRectangle(oldRect.width, newRect.y, widthDelta,
							newRect.height);
				} finally {
					gc.dispose();
				}
			}
		}
		oldRect = newRect;
	}
}