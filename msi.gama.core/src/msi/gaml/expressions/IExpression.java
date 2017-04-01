/*********************************************************************************************
 *
 * 'IExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.ITyped;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 25 d�c. 2010
 * 
 * @todo Description
 * 
 */
public interface IExpression extends IGamlDescription, ITyped, IDisposable {

	public abstract Object value(final IScope scope) throws GamaRuntimeException;

	public abstract boolean isConst();

	public abstract String literalValue();

	/**
	 * Returns an expression where all the temp variables belonging to the scope passed in parameter are replaced by
	 * constants representing their values
	 */
	public abstract IExpression resolveAgainst(IScope scope);

	public abstract boolean shouldBeParenthesized();

	public abstract void collectUsedVarsOf(IDescription species, ICollector<VariableDescription> result);

	/**
	 * Returns, by default, the type of the expression. Specialized in some cases (ie. TypeExpression)
	 * 
	 * @return
	 */
	public default IType<?> getDenotedType() {
		return getType();
	}

}