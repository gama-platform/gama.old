/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.*;

/**
 * Written by drogoul Modified on 20 dec. 2010
 * 
 * @todo Description
 * 
 */
public class Logic {

	@operator(value = "or")
	@doc(value = "a bool value, equal to the logical or between the left-hand operand and the rigth-hand operand.", comment = "both operands are always casted to bool before applying the operator. Thus, an expression like 1 or 0 is accepted and returns true.", see = {
		"bool", "and" })
	public static Boolean or(final IScope scope, final Boolean left, final IExpression right)
		throws GamaRuntimeException {
		return left != null && left || right != null && Cast.asBool(scope, right.value(scope));
	}

	@operator(value = "and")
	@doc(value = "a bool value, equal to the logical and between the left-hand operand and the rigth-hand operand.", comment = "both operands are always casted to bool before applying the operator. Thus, an expression like (1 and 0) is accepted and returns false.", see = {
		"bool", "or" })
	public static Boolean and(final IScope scope, final Boolean left, final IExpression right)
		throws GamaRuntimeException {
		return left != null && left && right != null && Cast.asBool(scope, right.value(scope));
	}

	@operator(value = { "!", "not" }, can_be_const = true)
	@doc(value = "opposite boolean value.", special_cases = { "if the parameter is not boolean, it is casted to a boolean value." }, examples = { "! (true) 		--:	 	false" }, see = "bool")
	public static Boolean not(final Boolean b) {
		return !b;
	}

	@operator(value = "?", type = ITypeProvider.RIGHT_TYPE, content_type = ITypeProvider.RIGHT_CONTENT_TYPE, priority = IPriority.TERNARY)
	@doc(value = "if the left-hand operand evaluates to true, returns the value of the left-hand operand of the :, otherwise that of the right-hand operand of the :", comment = "These functional tests can be combined together.", examples = {
		"[10, 19, 43, 12, 7, 22] collect ((each > 20) ? 'above' : 'below')    --:  ['below', 'below', 'above', 'below', 'below', 'above']",
		"set color value:(food > 5) ? 'red' : ((food >= 2)? 'blue' : 'green');" }, see = ":")
	public static Object iff(final IScope scope, final Boolean left, final IExpression right)
		throws GamaRuntimeException {
		IOperator expr = (IOperator) right;
		return left ? expr.arg(0).value(scope) : expr.arg(1).value(scope);
	}

	@operator(value = ":", type = ITypeProvider.BOTH, content_type = ITypeProvider.BOTH, priority = IPriority.TERNARY)
	@doc(see = "?")
	public static Object then(final IScope scope, final Object a, final Object b) {
		return null;
		// should never be called
	}

}
