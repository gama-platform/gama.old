/*******************************************************************************************************
 *
 * ILastResortJSonConverter.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.serialize;

import msi.gama.runtime.IScope;

/**
 * The Interface ILastResortJSonConverter.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 28 oct. 2023
 */
public interface ILastResortJSonConverter {

	/**
	 * To Json.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return the string
	 * @date 28 oct. 2023
	 */
	String toJSon(final IScope scope, final Object o);
}
