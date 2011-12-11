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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.JPopupMenu;
import javax.swing.text.*;

/**
 * The Class AwtFocusHandler.
 */
class AwtFocusHandler implements FocusListener, ContainerListener, WindowFocusListener {

	/** The frame. */
	private final Frame frame;

	/** The swt handler. */
	private SwtFocusHandler swtHandler;

	/** The awt has focus. */
	private boolean awtHasFocus = false;

	/** The current component. */
	private Component currentComponent = null;

	/**
	 * Instantiates a new awt focus handler.
	 * 
	 * @param frame the frame
	 */
	AwtFocusHandler(final Frame frame) {
		assert frame != null;

		this.frame = frame;
		frame.addContainerListener(new RecursiveContainerListener(this));
		frame.addWindowFocusListener(this);
	}

	/**
	 * Sets the swt handler.
	 * 
	 * @param handler the new swt handler
	 */
	void setSwtHandler(final SwtFocusHandler handler) {
		assert handler != null;
		assert swtHandler == null; // this method is meant to be called once

		swtHandler = handler;
	}

	/**
	 * Gain focus.
	 */
	void gainFocus() {
		assert frame != null;
		// assert !awtHasFocus;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		final FocusTraversalPolicy policy = frame.getFocusTraversalPolicy();
		Component component;
		if ( policy instanceof EmbeddedChildFocusTraversalPolicy ) {
			final EmbeddedChildFocusTraversalPolicy embeddedPolicy =
				(EmbeddedChildFocusTraversalPolicy) policy;
			component = embeddedPolicy.getCurrentComponent(frame);
		} else {
			component = policy.getDefaultComponent(frame);
		}
		if ( component != null ) {
			// +
			// component);
			component.requestFocus();
		}
		// TODO: else case error? If not, consider moving flag setting below
		// into this if
		awtHasFocus = true;
	}

	/**
	 * Moves focus back to the next SWT component.
	 */
	void transferFocusNext() {
		assert swtHandler != null;
		assert awtHasFocus;

		awtHasFocus = false;
		swtHandler.gainFocusNext();
	}

	/**
	 * Moves focus back to the previous SWT component.
	 */
	void transferFocusPrevious() {
		assert swtHandler != null;
		assert awtHasFocus;

		awtHasFocus = false;
		swtHandler.gainFocusPrevious();
	}

	/**
	 * Awt has focus.
	 * 
	 * @return true, if successful
	 */
	boolean awtHasFocus() {
		return awtHasFocus;
	}

	/**
	 * Gets the current component.
	 * 
	 * @return the current component
	 */
	Component getCurrentComponent() {
		return currentComponent;
	}

	// ..................... Listener implementations

	/**
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(final FocusEvent e) {
		assert e != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		System.out.println("gained (awt). component = " + e.getComponent() + ", opposite = " +
			e.getOppositeComponent());
		currentComponent = e.getComponent();
		// Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		// GUI.debug("Focus owner : " + c);
	}

	/**
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(final FocusEvent e) {
		// System.out.println("component focus lost (awt). opposite = " + e.getOppositeComponent());

		// Intentionally leaving currentComponent set. When window focus is
		// lost,
		// it will be needed.
	}

	/**
	 * 
	 * @see java.awt.event.ContainerListener#componentAdded(java.awt.event.ContainerEvent)
	 */
	@Override
	public void componentAdded(final ContainerEvent e) {
		assert e != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread
		// GUI.debug("Component added : " + e.getChild());
		e.getChild().addFocusListener(this);
		// if ( e.getChild() instanceof JComponent ) {
		// ComponentDisplaySurface.addGamaKeysListener(((JComponent) e.getChild()));
		// }

	}

	/**
	 * 
	 * @see java.awt.event.ContainerListener#componentRemoved(java.awt.event.ContainerEvent)
	 */
	@Override
	public void componentRemoved(final ContainerEvent e) {
		assert e != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread
		// GUI.debug("Component removed : " + e.getChild());
		e.getChild().removeFocusListener(this);
	}

	/**
	 * 
	 * @see java.awt.event.WindowFocusListener#windowGainedFocus(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowGainedFocus(final WindowEvent e) {
		assert EventQueue.isDispatchThread(); // On AWT event thread
		// System.out.println("WindowFocusListener.windowGainedFocus");
		awtHasFocus = true;
	}

	/**
	 * 
	 * @see java.awt.event.WindowFocusListener#windowLostFocus(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowLostFocus(final WindowEvent e) {
		assert e != null;
		assert swtHandler != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// System.out.println("WindowFocusListener.windowLostFocus");

		// Dismiss any popup menus that are
		// open when losing focus. This prevents situations where
		// multiple popup menus are visible at the same time. In JDK 1.4 and
		// earlier,
		// the dismissal is not done automatically. In JDK 1.5, this code is
		// unnecessary, but it doesn't seem to hurt anything.
		// TODO: verify this is OK on other windowing systems
		// TODO: disable in post-1.4 environments
		// /* boolean popupShown = */hidePopups();

		// If focus is being lost to the parent SWT composite, then
		// grab it back for AWT and return. Normally the parent SWT composite
		// will
		// do this for us, but it will not see a focus gained event when focus
		// is transferred to it from its AWT frame child.
		// This happens, for example, if an AWT control has focus and the
		// tab of a containing (already active) view is clicked.
		//
		// However, don't grab back focus if a popup was hidden above. The popup
		// area will not be properly redrawn (the popup, or part of it, will
		// appear to be still there.
		// if (!popupShown && swtHandler.hasFocus()) {
		// System.out.println("**** Taking back focus: " + e);
		// This seems to have side effects, so it's commented out for now.
		// (Sometimes, it forces the workbench window to the foreground when
		// another
		// program's window is selected.)
		// TODO: find an alternate approach to reassert focus
		// gainFocus();
		// return;
		// }

		// On a normal change of focus, Swing will turn off any selection
		// in a text field to help indicate focus is lost. This won't happen
		// automatically when transferring to SWT, so turn off the selection
		// manually.
		if ( currentComponent instanceof JTextComponent ) {
			final Caret caret = ((JTextComponent) currentComponent).getCaret();
			if ( caret != null ) {
				caret.setSelectionVisible(false);
			}
		}
		awtHasFocus = false;
	}

	// Returns true if any popup has been hidden
	/**
	 * Hide popups.
	 * 
	 * @return true, if successful
	 */
	private boolean hidePopups() {
		boolean result = false;
		final List popups = new ArrayList();
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// Look for popups inside the frame's component hierarchy.
		// Lightweight popups will be found here.
		findContainedPopups(frame, popups);

		// Also look for popups in the frame's window hierachy.
		// Heavyweight popups will be found here.
		findOwnedPopups(frame, popups);

		// System.out.println("Hiding popups, count=" + popups.size());
		for ( final Iterator iter = popups.iterator(); iter.hasNext(); ) {
			final Component popup = (Component) iter.next();
			if ( popup.isVisible() ) {
				result = true;
				popup.setVisible(false);
			}
		}
		return result;
	}

	/**
	 * Find owned popups.
	 * 
	 * @param window the window
	 * @param popups the popups
	 */
	private void findOwnedPopups(final Window window, final List popups) {
		assert window != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		final Window[] ownedWindows = window.getOwnedWindows();
		for ( int i = 0; i < ownedWindows.length; i++ ) {
			findContainedPopups(ownedWindows[i], popups);
			findOwnedPopups(ownedWindows[i], popups);
		}
	}

	/**
	 * Find contained popups.
	 * 
	 * @param container the container
	 * @param popups the popups
	 */
	private void findContainedPopups(final Container container, final List popups) {
		assert container != null;
		assert popups != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		final Component[] components = container.getComponents();
		for ( int i = 0; i < components.length; i++ ) {
			final Component c = components[i];
			// JPopupMenu is a container, so check for it first
			if ( c instanceof JPopupMenu ) {
				popups.add(c);
			} else if ( c instanceof Container ) {
				findContainedPopups((Container) c, popups);
			}
		}
	}

	/**
	 * Post hide popups.
	 */
	void postHidePopups() {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				hidePopups();
			}
		});
	}
}
