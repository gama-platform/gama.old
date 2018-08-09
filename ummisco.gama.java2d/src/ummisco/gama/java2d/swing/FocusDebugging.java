/*********************************************************************************************
 *
 * 'FocusDebugging.java, in plugin ummisco.gama.java2d, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.java2d.swing;

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
 * This class contains utility functions for debugging focus issues relating to the SWT_AWT bridge.
 * <p>
 * Debugging focus issues cannot be done in a debugger running on the same machine, because the interactions with the
 * debugger often cause the application window to be deactivated. Therefore a println based approach has been adopted.
 * <p>
 * There are four kinds of events:
 * <ul>
 * <li>SWT focus events relating to the IlvSwingControl.</li>
 * <li>AWT window focus events relating to the topmost window under the IlvSwingControl.</li>
 * <li>AWT focus events relating to components inside that window.</li>
 * <li>Property change events of the AWT <code>KeyboardFocusManager</code>.</li>
 * </ul>
 */
public class FocusDebugging {

	/**
	 * Adds listeners for debugging the three first kinds of focus events.
	 */
	public static void addFocusDebugListeners(final org.eclipse.swt.widgets.Composite control,
			final Container topLevelComponent) {
		control.addFocusListener(_SWTFocusListener);
		control.addListener(SWT.Activate, _SWTActivationListener);
		control.addListener(SWT.Deactivate, _SWTActivationListener);
		if (topLevelComponent instanceof Window) {
			((Window) topLevelComponent).addWindowFocusListener(_AWTWindowFocusListener);
		}
		addFocusListenerToTree(topLevelComponent);
	}

	/**
	 * Shows focus events on the SWT side.
	 */
	static class SWTFocusListener implements org.eclipse.swt.events.FocusListener {

		@Override
		public void focusGained(final org.eclipse.swt.events.FocusEvent event) {
			System.err.println("@" + System.currentTimeMillis() + " SWT focus gained " + event.getSource());
		}

		@Override
		public void focusLost(final org.eclipse.swt.events.FocusEvent event) {
			System.err.println("@" + System.currentTimeMillis() + " SWT focus lost " + event.getSource());
		}
	}

	static SWTFocusListener _SWTFocusListener = new SWTFocusListener();

	/**
	 * Shows activation events on the SWT side. Note: events that are eaten by the filter in FocusHander will not be
	 * displayed here.
	 */
	static class SWTActivationListener implements org.eclipse.swt.widgets.Listener {

		@Override
		public void handleEvent(final Event event) {
			String name = null;
			switch (event.type) {
				case SWT.Deactivate:
					name = "Deactivate";
					break;

				case SWT.Activate:
					name = "Activate";
					break;
			}
			System.err.println("@" + System.currentTimeMillis() + " SWT Event: " + name + " on " + event.widget);
		}
	}

	static SWTActivationListener _SWTActivationListener = new SWTActivationListener();

	/**
	 * Shows focus events on the top-level window on the AWT side.
	 */
	static class AWTWindowFocusListener implements WindowFocusListener {

		private void showKFMStatus(final Window window) {
			final KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			System.err.println("               permanentFocusOwner: " + kfm.getPermanentFocusOwner());
			System.err.println("               focusOwner:          " + kfm.getFocusOwner());
			System.err.println("               window's focusOwner: " + window.getFocusOwner());
		}

		@Override
		public void windowGainedFocus(final WindowEvent event) {
			System.err.println("@" + System.currentTimeMillis() + " AWT focus gained by window " + event.getWindow());
			showKFMStatus(event.getWindow());
		}

		@Override
		public void windowLostFocus(final WindowEvent event) {
			System.err.println("@" + System.currentTimeMillis() + " AWT focus lost by window " + event.getWindow());
			showKFMStatus(event.getWindow());
		}
	}

	static AWTWindowFocusListener _AWTWindowFocusListener = new AWTWindowFocusListener();

	/**
	 * Shows focus events on a given component on the AWT side.
	 */
	static class AWTFocusListener implements FocusListener {

		@Override
		public void focusGained(final FocusEvent event) {
			System.err.println("@" + System.currentTimeMillis() + " AWT focus gained " + event.getComponent());
		}

		@Override
		public void focusLost(final FocusEvent event) {
			System.err.println("@" + System.currentTimeMillis() + " AWT focus lost " + event.getComponent());
		}
	}

	static AWTFocusListener _AWTFocusListener = new AWTFocusListener();

	/**
	 * Attaches the AWTFocusListener on each of the components in the component tree under the given component.
	 */
	static class AWTContainerListener implements ContainerListener {

		@Override
		public void componentAdded(final ContainerEvent event) {
			addFocusListenerToTree(event.getChild());
		}

		@Override
		public void componentRemoved(final ContainerEvent event) {
			removeFocusListenerFromTree(event.getChild());
		}
	}

	static AWTContainerListener _AWTContainerListener = new AWTContainerListener();

	static void addFocusListenerToTree(final Component comp) {
		comp.addFocusListener(_AWTFocusListener);
		if (comp instanceof Container) {
			final Container cont = (Container) comp;
			// Remember to add the listener to child components that are added later.
			cont.addContainerListener(_AWTContainerListener);
			// Recurse across all child components that are already in the tree now.
			final int n = cont.getComponentCount();
			for (int i = 0; i < n; i++) {
				addFocusListenerToTree(cont.getComponent(i));
			}
		}
	}

	static void removeFocusListenerFromTree(final Component comp) {
		// The exact opposite of addFocusListenerToTree.
		comp.removeFocusListener(_AWTFocusListener);
		if (comp instanceof Container) {
			final Container cont = (Container) comp;
			cont.removeContainerListener(_AWTContainerListener);
			final int n = cont.getComponentCount();
			for (int i = 0; i < n; i++) {
				removeFocusListenerFromTree(cont.getComponent(i));
			}
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Enables logging of events, from the AWT <code>KeyboardFocusManager</code> singleton.
	 */
	public static void enableKeyboardFocusManagerLogging() {
		enableFinest("java.awt.focus.KeyboardFocusManager");
		enableFinest("java.awt.focus.DefaultKeyboardFocusManager");
	}

	private static void enableFinest(final String name) {
		final Logger logger = Logger.getLogger(name);
		logger.setLevel(Level.FINEST);
		final ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.FINEST);
		logger.addHandler(handler);
	}
}
