/*********************************************************************************************
 *
 * 'Logic.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.operators;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.example;
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

	@operator(value = "or", category=IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL })
	@doc(value = "a bool value, equal to the logical or between the left-hand operand and the right-hand operand.", 
		comment = "both operands are always casted to bool before applying the operator. Thus, an expression like 1 or 0 is accepted and returns true.", see = {
		"bool", "and", "!" })
	public static Boolean or(final IScope scope, final Boolean left, final IExpression right)
		throws GamaRuntimeException {
		return left != null && left || right != null && Cast.asBool(scope, right.value(scope));
	}

	@operator(value = "and", category=IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL })
	@doc(value = "a bool value, equal to the logical and between the left-hand operand and the right-hand operand.", 
		comment = "both operands are always casted to bool before applying the operator. Thus, an expression like (1 and 0) is accepted and returns false.", see = {
		"bool", "or", "!" })
	public static Boolean and(final IScope scope, final Boolean left, final IExpression right)
		throws GamaRuntimeException {
		return left != null && left && right != null && Cast.asBool(scope, right.value(scope));
	}

	@operator(value = { "!", "not" }, can_be_const = true, category=IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL })
	@doc(value = "opposite boolean value.", special_cases = { "if the parameter is not boolean, it is casted to a boolean value." }, examples = { @example(value="! (true)", equals="false") }, see = {"bool", "and", "or"})
	public static Boolean not(final Boolean b) {
		return !b;
	}

	@operator(value = "?", type = ITypeProvider.SECOND_TYPE, content_type = ITypeProvider.SECOND_CONTENT_TYPE, category=IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL, IConcept.TERNARY })
	@doc(value = "It is used in combination with the : operator: if the left-hand operand evaluates to true, returns the value of the left-hand operand of the :, otherwise that of the right-hand operand of the :", comment = "These functional tests can be combined together.", examples = {
		@example(value="[10, 19, 43, 12, 7, 22] collect ((each > 20) ? 'above' : 'below')", returnType="list<string>", equals="['below', 'below', 'above', 'below', 'below', 'above']"),
		@example("rgb color <- (flip(0.3) ? #red : (flip(0.9) ? #blue : #green));") }, see = ":")
	public static Object iff(final IScope scope, final Boolean left, final IExpression right)
		throws GamaRuntimeException {
		IOperator expr = (IOperator) right;
		return left ? expr.arg(0).value(scope) : expr.arg(1).value(scope);
	}

	@operator(value = ":", type = ITypeProvider.BOTH, content_type = ITypeProvider.BOTH, index_type = ITypeProvider.BOTH, category=IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL, IConcept.TERNARY })
	@doc(see = "?")
	public static Object then(final IScope scope, final Object a, final Object b) {
		return null;
		// should never be called
	}

}
