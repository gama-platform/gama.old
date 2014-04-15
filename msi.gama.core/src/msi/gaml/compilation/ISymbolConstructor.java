/*********************************************************************************************
 * 
 *
 * 'ISymbolConstructor.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import msi.gaml.descriptions.IDescription;

/**
 * Written by drogoul Modified on 29 ao�t 2010
 * 
 * @todo Description
 * 
 */
public interface ISymbolConstructor {

	public ISymbol create(IDescription description);

}
