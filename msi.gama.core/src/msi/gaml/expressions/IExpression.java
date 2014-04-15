/*********************************************************************************************
 * 
 * 
 * 'IExpression.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.interfaces.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IGamlDescription;

/**
 * Written by drogoul Modified on 25 d�c. 2010
 * 
 * @todo Description
 * 
 */
public interface IExpression extends IGamlDescription, ITyped, IDisposable {

	public abstract Object value(final IScope scope) throws GamaRuntimeException;

	public abstract boolean isConst();

	public abstract String toGaml();

	public abstract String literalValue();

	/*
	 * Returns an expression where all the temp variables belonging to the scope passed in parameter
	 * are replaced by constants representing their values
	 */
	public abstract IExpression resolveAgainst(IScope scope);

	/*
	 * FIXME Highly exploratory !
	 * Tries to gather the key and content types of the elements contained in this expression, if
	 * any. The type of the elements is provided by getContentType(), but these methods try to
	 * return the key and content types of the elements themselves when they are available.
	 */

	// public abstract IType getElementsContentType();

	// public abstract IType getElementsKeyType();

	// public abstract void setElementsContentType(IType t);

	// public abstract void setElementsKeyType(IType t);

	public abstract boolean shouldBeParenthesized();

}