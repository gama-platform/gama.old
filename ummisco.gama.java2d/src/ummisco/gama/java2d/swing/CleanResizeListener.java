/*********************************************************************************************
 *
 * 'CleanResizeListener.java, in plugin ummisco.gama.java2d, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.java2d.swing;

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class CleanResizeListener extends ControlAdapter {

	private Rectangle oldRect = null;

	@Override
	public void controlResized(final ControlEvent e) {
		assert e != null;
		assert Display.getCurrent() != null; // On SWT event thread

		// Prevent garbage from Swing lags during resize. Fill exposed areas
		// with background color.
		Composite composite = (Composite) e.widget;
		Rectangle newRect = composite.getClientArea();
		if ( oldRect != null ) {
			int heightDelta = newRect.height - oldRect.height;
			int widthDelta = newRect.width - oldRect.width;
			if ( heightDelta > 0 || widthDelta > 0 ) {
				GC gc = new GC(composite);
				try {
					gc.fillRectangle(newRect.x, oldRect.height, newRect.width, heightDelta);
					gc.fillRectangle(oldRect.width, newRect.y, widthDelta, newRect.height);
				} finally {
					gc.dispose();
				}
			}
		}
		oldRect = newRect;
	}
}
