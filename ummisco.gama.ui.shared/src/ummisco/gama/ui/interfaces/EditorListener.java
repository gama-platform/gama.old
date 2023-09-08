/*******************************************************************************************************
 *
 * EditorListener.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.interfaces;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 27 mai 2011
 *
 * @todo Description
 *
 */
public interface EditorListener<T> {

	/**
	 * Value modified.
	 *
	 * @param val
	 *            the val
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	void valueModified(T val) throws GamaRuntimeException;

	/**
	 * The Interface Command.
	 */
	public interface Command extends EditorListener<Object>, SelectionListener {

		@Override
		default void valueModified(final Object o) {
			this.widgetSelected(null);
		}

		@Override
		default void widgetDefaultSelected(final SelectionEvent o) {
			this.widgetSelected(null);
		}

	}

	public interface Static extends EditorListener<Object>, SelectionListener {
		@Override
		default void valueModified(final Object o) {}

		@Override
		default void widgetDefaultSelected(final SelectionEvent o) {}

	}

}
