/*******************************************************************************************************
 *
 * ISymbolConstructor.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation;

import msi.gaml.descriptions.IDescription;

/**
 * Written by drogoul Modified on 29 ao�t 2010
 * 
 * @todo Description
 * 
 */
@FunctionalInterface
public interface ISymbolConstructor {

	/**
	 * Creates the.
	 *
	 * @param description the description
	 * @return the i symbol
	 */
	public ISymbol create(IDescription description);

}
