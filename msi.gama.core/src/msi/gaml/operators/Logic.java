/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

import msi.gaml.expressions.*;

/**
 * Written by drogoul Modified on 20 déc. 2010
 * 
 * @todo Description
 * 
 */
public class Logic {

	@operator(value = "or", priority = IPriority.BOOLEAN)
	public static Boolean or(final IScope scope, final Boolean left, final IExpression right)
		throws GamaRuntimeException {
		return left != null && left || right != null && Cast.asBool(scope, right.value(scope));
	}

	@operator(value = "and", priority = IPriority.BOOLEAN)
	public static Boolean and(final IScope scope, final Boolean left, final IExpression right)
		throws GamaRuntimeException {
		return left != null && left && right != null && Cast.asBool(scope, right.value(scope));
	}

	@operator(value = { "!", "not" }, can_be_const = true)
	public static Boolean not(final Boolean b) {
		return !b;
	}

	@operator(value = "?", type = ITypeProvider.RIGHT_TYPE, content_type = ITypeProvider.RIGHT_CONTENT_TYPE, priority = IPriority.TERNARY)
	public static Object iff(final IScope scope, final Boolean left, final IExpression right)
		throws GamaRuntimeException {
		// if ( right instanceof IOperator && ((IOperator) right).right() != null ) {
		IOperator expr = (IOperator) right;
		return /* left != null && */left ? expr.left().value(scope) : expr.right().value(scope);
		// }
		// return left ? right.value(scope) : null;
	}

	@operator(value = ":", type = ITypeProvider.BOTH, content_type = ITypeProvider.BOTH, priority = IPriority.TERNARY)
	public static Object then(final IScope scope, final Object a, final Object b) {
		return null;
		// should never be called
	}

}
