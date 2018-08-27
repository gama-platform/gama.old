/*******************************************************************************************************
 *
 * msi.gaml.expressions.IVarExpression.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.expressions;

import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;

/**
 * VariableExpression.
 * 
 * @author drogoul 4 sept. 07
 */
public interface IVarExpression extends IExpression {

	public interface Agent extends IVarExpression {

		IDescription getDefinitionDescription();
	}

	public static final int GLOBAL = 0;
	public static final int AGENT = 1;
	public static final int TEMP = 2;
	public static final int EACH = 3;
	public static final int SELF = 4;
	public static final int SUPER = 5;
	// public static final int WORLD = 5;

	public abstract void setVal(IScope scope, Object v, boolean create);

	public abstract boolean isNotModifiable();

	public abstract IExpression getOwner();

	public VariableExpression getVar();

}