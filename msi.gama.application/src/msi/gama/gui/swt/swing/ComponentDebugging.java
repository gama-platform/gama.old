/*******************************************************************************
 * Copyright (c) 2007-2008 SAS Institute Inc., ILOG S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * SAS Institute Inc. - initial API and implementation
 * ILOG S.A. - initial API and implementation
 *******************************************************************************/
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
