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

import javax.swing.LayoutFocusTraversalPolicy;

/**
 * The Class EmbeddedChildFocusTraversalPolicy.
 */
class EmbeddedChildFocusTraversalPolicy extends LayoutFocusTraversalPolicy {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7708166698501335927L;

	/** The awt handler. */
	private final AwtFocusHandler awtHandler;

	/**
	 * Instantiates a new embedded child focus traversal policy.
	 * 
	 * @param handler
	 *            the handler
	 */
	EmbeddedChildFocusTraversalPolicy(final AwtFocusHandler handler) {
		assert handler != null;
		awtHandler = handler;
	}

	/**
	 * 
	 * @see javax.swing.LayoutFocusTraversalPolicy#getComponentAfter(java.awt.Container,
	 *      java.awt.Component)
	 */
	@Override
	public Component getComponentAfter(final Container container,
			final Component component) {
		assert container != null;
		assert component != null;
		assert awtHandler != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		if (component.equals(getLastComponent(container))) {
			// Instead of cycling around to the first component, transfer to the
			// next SWT component
			awtHandler.transferFocusNext();
			return null;
		}
		return super.getComponentAfter(container, component);
	}

	/**
	 * 
	 * @see javax.swing.LayoutFocusTraversalPolicy#getComponentBefore(java.awt.Container,
	 *      java.awt.Component)
	 */
	@Override
	public Component getComponentBefore(final Container container,
			final Component component) {
		assert container != null;
		assert component != null;
		assert awtHandler != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		if (component.equals(getFirstComponent(container))) {
			// Instead of cycling around to the last component, transfer to the
			// previous SWT component
			awtHandler.transferFocusPrevious();
			return null;
		}
		return super.getComponentBefore(container, component);
	}

	/**
	 * 
	 * @see javax.swing.SortingFocusTraversalPolicy#getDefaultComponent(java.awt.Container)
	 */
	@Override
	public Component getDefaultComponent(final Container container) {
		assert container != null;
		assert awtHandler != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		// This is a hack which depends on knowledge of current JDK
		// implementation to
		// work. The implementation above of getComponentBefore/After
		// properly returns null when transferring to SWT. However, the calling
		// AWT container
		// will then immediately try this method to find the next recipient of
		// focus. But we don't want *any* AWT component to receive focus... it's
		// just
		// been transferred to SWT. So, this method must return null when AWT
		// does
		// not own the focus. When AWT *does* own the focus, behave normally.
		if (awtHandler.awtHasFocus()) // System.out.println("getDefault: super");
			return super.getDefaultComponent(container);
		// System.out.println("getDefault: null");
		return null;
	}

	/**
	 * Gets the current component.
	 * 
	 * @param container
	 *            the container
	 * 
	 * @return the current component
	 */
	public Component getCurrentComponent(final Container container) {
		assert container != null;
		assert awtHandler != null;
		assert EventQueue.isDispatchThread(); // On AWT event thread

		final Component currentAwtComponent = awtHandler.getCurrentComponent();
		if (currentAwtComponent != null
				&& container.isAncestorOf(currentAwtComponent))
			return currentAwtComponent;
		return getDefaultComponent(container);
	}
}
