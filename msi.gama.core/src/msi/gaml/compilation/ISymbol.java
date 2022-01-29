/*******************************************************************************************************
 *
 * ISymbol.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation;

import org.eclipse.emf.common.util.URI;

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

	/**
	 * Dispose.
	 */
	public abstract void dispose();

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public abstract int getOrder();

	/**
	 * Sets the order.
	 *
	 * @param o the new order
	 */
	public abstract void setOrder(int o);

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public abstract IDescription getDescription();

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public URI getURI();

	/**
	 * Returns the expression located at the first facet of 'keys'
	 * 
	 * @param keys
	 * @return
	 */
	public abstract IExpression getFacet(String... keys);

	/**
	 * Checks for facet.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public abstract boolean hasFacet(String key);

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	public abstract void setChildren(Iterable<? extends ISymbol> children);

	/**
	 * Gets the trace.
	 *
	 * @param abstractScope the abstract scope
	 * @return the trace
	 */
	public abstract String getTrace(IScope abstractScope);

	/**
	 * Gets the keyword.
	 *
	 * @return the keyword
	 */
	public abstract String getKeyword();

	/**
	 * Sets the enclosing.
	 *
	 * @param enclosing the new enclosing
	 */
	public abstract void setEnclosing(ISymbol enclosing);

}
