/*******************************************************************************************************
 *
 * msi.gaml.compilation.ISymbolConstructor.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

	public ISymbol create(IDescription description);

}
