/*********************************************************************************************
 *
 * 'SizeListener.java, in plugin ummisco.gama.java2d, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.java2d.swing;

import java.util.EventListener;

/**
 * A listener that is notified on events related to
 * the size of the embedded Swing control
 */
public interface SizeListener extends EventListener {

	/**
	 * See {@link SwingControl#preferredSizeChanged(org.eclipse.swt.graphics.Point, org.eclipse.swt.graphics.Point, org.eclipse.swt.graphics.Point)}
	 * for information on when this method is called.
	 * 
	 * @param event
	 */
	void preferredSizeChanged(SizeEvent event);
}
