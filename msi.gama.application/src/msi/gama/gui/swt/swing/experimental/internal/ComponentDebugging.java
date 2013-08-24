/*******************************************************************************
 * Copyright (c) 2007-2008 SAS Institute Inc., ILOG S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SAS Institute Inc. - initial API and implementation
 *     ILOG S.A. - initial API and implementation
 *******************************************************************************/
package msi.gama.gui.swt.swing.experimental.internal;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * This class contains utility functions for debugging issues at Component
 * level, relating to the SWT_AWT bridge.
 */
public class ComponentDebugging {

    /**
     * Adds a listener for debugging size at Component events.
     */
    public static void addComponentSizeDebugListeners(final Component comp) {
        comp.addComponentListener(
            new ComponentListener() {
                private void log(ComponentEvent event) {
                    System.err.println("Size: "+comp.getWidth()+" x "+comp.getHeight()+" after "+event);
                }
                public void componentHidden(ComponentEvent event) {
                    log(event);
                }
                public void componentMoved(ComponentEvent event) {
                    log(event);
                }
                public void componentResized(ComponentEvent event) {
                    log(event);
                }
                public void componentShown(ComponentEvent event) {
                    log(event);
                }
            });
    }
}
