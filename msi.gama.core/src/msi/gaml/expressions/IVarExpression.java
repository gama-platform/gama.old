/*******************************************************************************************************
 *
 * msi.gaml.expressions.IVarExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

	int GLOBAL = 0;
	int AGENT = 1;
	int TEMP = 2;
	int EACH = 3;
	int SELF = 4;
	int SUPER = 5;
	int MYSELF = 6;
	// public static final int WORLD = 5;

	void setVal(IScope scope, Object v, boolean create);

	boolean isNotModifiable();

	IExpression getOwner();

	VariableExpression getVar();

}