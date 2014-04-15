/*********************************************************************************************
 * 
 *
 * 'ComponentDebugging.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.swing;

import java.awt.Component;
import java.awt.event.*;

/**
 * This class contains utility functions for debugging issues at Component
 * level, relating to the SWT_AWT bridge.
 */
public class ComponentDebugging {

	/**
	 * Adds a listener for debugging size at Component events.
	 */
	public static void addComponentSizeDebugListeners(final Component comp) {
		comp.addComponentListener(new ComponentListener() {

			private void log(final ComponentEvent event) {
				System.err.println("Size: " + comp.getWidth() + " x " + comp.getHeight() + " after " + event);
			}

			@Override
			public void componentHidden(final ComponentEvent event) {
				log(event);
			}

			@Override
			public void componentMoved(final ComponentEvent event) {
				log(event);
			}

			@Override
			public void componentResized(final ComponentEvent event) {
				log(event);
			}

			@Override
			public void componentShown(final ComponentEvent event) {
				log(event);
			}
		});
	}
}
