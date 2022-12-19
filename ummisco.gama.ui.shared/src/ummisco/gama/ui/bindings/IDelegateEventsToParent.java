/*******************************************************************************************************
 *
 * IDelegateEventsToParent.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.bindings;

import org.eclipse.swt.widgets.Composite;

/**
 * An interface for controls/composites that delegate all of their mouse/key events to their immediate parent
 *
 * @author drogoul
 *
 */
public interface IDelegateEventsToParent {

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	Composite getParent();

}
