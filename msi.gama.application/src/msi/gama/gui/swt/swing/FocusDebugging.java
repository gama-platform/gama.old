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
package msi.gama.gui.swt.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

/**
 * This class contains utility functions for debugging focus issues relating
 * to the SWT_AWT bridge.
 * <p>
 * Debugging focus issues cannot be done in a debugger running on the same
 * machine, because the interactions with the debugger often cause the
 * application window to be deactivated. Therefore a println based approach
 * has been adopted.
 * <p>
 * There are four kinds of events:
 * <ul>
 *   <li>SWT focus events relating to the IlvSwingControl.</li>
 *   <li>AWT window focus events relating to the topmost window under the
 *       IlvSwingControl.</li>
 *   <li>AWT focus events relating to components inside that window.</li>
 *   <li>Property change events of the AWT
 *       <code>KeyboardFocusManager</code>.</li>
 * </ul>
 */
public class FocusDebugging {

   /**
     * Adds listeners for debugging the three first kinds of focus events.
     */
    public static void addFocusDebugListeners(org.eclipse.swt.widgets.Composite control,
                                              Container topLevelComponent) {
        control.addFocusListener(_SWTFocusListener);
        control.addListener(SWT.Activate, _SWTActivationListener);
        control.addListener(SWT.Deactivate, _SWTActivationListener);
        if (topLevelComponent instanceof Window)
            ((Window)topLevelComponent).addWindowFocusListener(_AWTWindowFocusListener);
        addFocusListenerToTree(topLevelComponent);
    }

    /**
     * Shows focus events on the SWT side.
     */
    private static class SWTFocusListener implements org.eclipse.swt.events.FocusListener {
        public void focusGained(org.eclipse.swt.events.FocusEvent event) {
            System.err.println("@"+System.currentTimeMillis()
                               +" SWT focus gained "+event.getSource().hashCode());
        }
        public void focusLost(org.eclipse.swt.events.FocusEvent event) {
            System.err.println("@"+System.currentTimeMillis()
                               +" SWT focus lost "+event.getSource().hashCode());
        }
    }
    private static SWTFocusListener _SWTFocusListener = new SWTFocusListener();

    /**
     * Shows activation events on the SWT side. Note: events that are eaten by the filter
     * in FocusHander will not be displayed here.
     */
    private static class SWTActivationListener implements org.eclipse.swt.widgets.Listener {
        public void handleEvent(Event event) {
            String name = null;
            switch (event.type) {
            case SWT.Deactivate:
                name = "Deactivate";
                break;
                
            case SWT.Activate:
                name = "Activate";
                break;
            }
            System.err.println("@"+System.currentTimeMillis() + 
                    " SWT Event: " + name + " " + System.identityHashCode(event.widget));
        }
    }
    private static SWTActivationListener _SWTActivationListener = new SWTActivationListener();
    
    /**
     * Shows focus events on the top-level window on the AWT side.
     */
    private static class AWTWindowFocusListener implements WindowFocusListener {
        private void showKFMStatus(Window window) {
            KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            System.err.println("               permanentFocusOwner: "+kfm.getPermanentFocusOwner());
            System.err.println("               focusOwner:          "+kfm.getFocusOwner());
            System.err.println("               window's focusOwner: "+window.getFocusOwner());
        }
        public void windowGainedFocus(WindowEvent event) {
            System.err.println("@"+System.currentTimeMillis()
                               +" AWT focus gained by window "+event.getWindow());
            showKFMStatus(event.getWindow());
        }
        public void windowLostFocus(WindowEvent event) {
            System.err.println("@"+System.currentTimeMillis()
                               +" AWT focus lost by window "+event.getWindow());
            showKFMStatus(event.getWindow());
        }
    }
    private static AWTWindowFocusListener _AWTWindowFocusListener = new AWTWindowFocusListener();

    /**
     * Shows focus events on a given component on the AWT side.
     */
    private static class AWTFocusListener implements FocusListener {
        public void focusGained(FocusEvent event) {
            System.err.println("@"+System.currentTimeMillis()
                               +" AWT focus gained "+event.getComponent());
        }
        public void focusLost(FocusEvent event) {
            System.err.println("@"+System.currentTimeMillis()
                               +" AWT focus lost "+event.getComponent());
        }
    }
    private static AWTFocusListener _AWTFocusListener = new AWTFocusListener();

    /**
     * Attaches the AWTFocusListener on each of the components in the component
     * tree under the given component.
     */
    private static class AWTContainerListener implements ContainerListener {
        public void componentAdded(ContainerEvent event) {
            addFocusListenerToTree(event.getChild());
        }

        public void componentRemoved(ContainerEvent event) {
            removeFocusListenerFromTree(event.getChild());
        }
    }
    private static AWTContainerListener _AWTContainerListener = new AWTContainerListener();
    static void addFocusListenerToTree(Component comp) {
        comp.addFocusListener(_AWTFocusListener);
        if (comp instanceof Container) {
            Container cont = (Container)comp;
            // Remember to add the listener to child components that are added later.
            cont.addContainerListener(_AWTContainerListener);
            // Recurse across all child components that are already in the tree now.
            int n = cont.getComponentCount();
            for (int i = 0; i < n; i++)
                addFocusListenerToTree(cont.getComponent(i));
        }
    }
    static void removeFocusListenerFromTree(Component comp) {
        // The exact opposite of addFocusListenerToTree.
        comp.removeFocusListener(_AWTFocusListener);
        if (comp instanceof Container) {
            Container cont = (Container)comp;
            cont.removeContainerListener(_AWTContainerListener);
            int n = cont.getComponentCount();
            for (int i = 0; i < n; i++)
                removeFocusListenerFromTree(cont.getComponent(i));
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Enables logging of events,
     * from the AWT <code>KeyboardFocusManager</code> singleton.
     */
    public static void enableKeyboardFocusManagerLogging() {
        enableFinest("java.awt.focus.KeyboardFocusManager");
        enableFinest("java.awt.focus.DefaultKeyboardFocusManager");
    }
    private static void enableFinest(String name) {
        Logger logger = Logger.getLogger(name);
        logger.setLevel(Level.FINEST);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        logger.addHandler(handler);
    }
}
