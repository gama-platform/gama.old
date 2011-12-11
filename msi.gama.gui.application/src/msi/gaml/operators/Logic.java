/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.Cast;

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
