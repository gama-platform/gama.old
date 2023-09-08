/*******************************************************************************************************
 *
 * IConstantAcceptor.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.constants;

/**
 * The Interface IConstantAcceptor.
 */
public interface IConstantAcceptor {

	/**
	 * Adds a constant to this acceptor and returns true if it has been added or false if any problem has prevented the
	 * constant from being added
	 *
	 * @param name
	 *            the name of the constant (cannot be null)
	 * @param value
	 *            the value of the constant
	 * @param doc
	 *            the documentation attached to it (should not be null)
	 * @param deprec
	 *            the explanation if the constant is deprecated (can be null)
	 * @param isTime
	 *            whether this constant is a unit related to time concepts (like #month, etc.)
	 * @param names
	 *            the other names under which this constant can be used in GAML
	 * @return the object
	 */
	@SuppressWarnings ("rawtypes")
	boolean accept(final String name, final Object value, final String doc, final String deprec, final boolean isTime,
			final String... names);

}
