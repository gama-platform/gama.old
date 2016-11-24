/*********************************************************************************************
 *
 * 'ISymbol.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.compilation;

import msi.gama.common.interfaces.INamed;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 19 mars 2010
 * 
 * @todo Description
 * 
 */
public interface ISymbol extends INamed {

	public abstract void dispose();

	public abstract IDescription getDescription();

	/**
	 * Returns the expression located at the first facet of 'keys'
	 * 
	 * @param keys
	 * @return
	 */
	public abstract IExpression getFacet(String... keys);

	public abstract boolean hasFacet(String key);

	public abstract void setChildren(Iterable<? extends ISymbol> children);

	public abstract String getTrace(IScope abstractScope);

	public abstract String getKeyword();

	public abstract void setEnclosing(ISymbol enclosing);

}
