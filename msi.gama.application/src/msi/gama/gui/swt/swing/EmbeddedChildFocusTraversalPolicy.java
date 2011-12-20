/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.swing;

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
