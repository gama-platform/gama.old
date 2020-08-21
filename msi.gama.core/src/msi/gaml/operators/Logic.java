/*******************************************************************************************************
 *
 * msi.gaml.operators.Logic.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.operators;

import java.util.Objects;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IOperator;

/**
 * Written by drogoul Modified on 20 dec. 2010
 * 
 * @todo Description
 * 
 */
public class Logic {

	@operator (
			value = "xor",
			category = IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL })
	@doc (
			value = "a bool value, equal to the logical xor between the left-hand operand and the right-hand operand."
					+ " False when they are equal",
			comment = "both operands are always casted to bool before applying the operator."
					+ " Thus, an expression like 1 xor 0 is accepted and returns true.",
			see = { "or", "and", "!" },
			examples= {
					@example(value="xor(true,false)", equals="true"),
					@example(value="xor(false,false)", equals="false"),
					@example(value="xor(false,true)", equals="true"),
					@example(value="xor(true,true)", equals="false"),
					@example(value="true xor true", equals="false"),
			})
	public static Boolean xor(final IScope scope, final Boolean left, final Boolean right) throws GamaRuntimeException {
		return !Objects.equals(left, right);
	}

	@operator (
			value = "or",
			category = IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL })
	@doc (
			value = "a bool value, equal to the logical or between the left-hand operand and the right-hand operand.",
			comment = "both operands are always casted to bool before applying the operator. Thus, an expression "
					+ "like 1 or 0 is accepted and returns true.",
			see = { "bool", "and", "!" },
					examples= {
							@example(value="true or false", equals="true"),
							@example(" int a <-3 ; int b <- 4; int c <- 7;"),
							@example(value="((a+b) = c ) or ((a+b) > c )", equals="true"),	
					})
	@test("false or false = false")
	@test("false or true")
	@test("true or true")
	public static Boolean or(final IScope scope, final Boolean left, final IExpression right)
			throws GamaRuntimeException {
		return left != null && left || right != null && Cast.asBool(scope, right.value(scope));
	}

	@operator (
			value = "and",
			category = IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL })
	@doc (
			value = "a bool value, equal to the logical and between the left-hand operand and the right-hand operand.",
			comment = "both operands are always casted to bool before applying the operator. "
					+ "Thus, an expression like (1 and 0) is accepted and returns false.",
			see = { "bool", "or", "!" },
					examples= {
							@example(value="true and false", equals="false"),
							@example(value="false and false", equals="false"),
							@example(value="false and true", equals="false"),
							@example(value="true and true", equals="true"),
							@example(" int a <-3 ; int b <- 4; int c <- 7;"),
							@example(value="((a+b) = c ) and ((a+b) > c )", equals="false"),	
					})
	public static Boolean and(final IScope scope, final Boolean left, final IExpression right)
			throws GamaRuntimeException {
		return left != null && left && right != null && Cast.asBool(scope, right.value(scope));
	}

	@operator (
			value = { "!", "not" },
			can_be_const = true,
			category = IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL })
	@doc (
			value = "opposite boolean value.",
			special_cases = { "if the parameter is not boolean, it is casted to a boolean value." },
			examples = { @example (
					value = "! (true)",
					equals = "false") },
			see = { "bool", "and", "or" })
	public static Boolean not(final Boolean b) {
		return !b;
	}

	@operator (
			value = "?",
			type = ITypeProvider.TYPE_AT_INDEX + 2,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			category = IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL, IConcept.TERNARY })
	@doc (
			value = "It is used in combination with the : operator: if the left-hand operand evaluates to true,"
					+ " returns the value of the left-hand operand of the :, "
					+ "otherwise that of the right-hand operand of the :",
			comment = "These functional tests can be combined together.",
			examples = { @example (
					value = "[10, 19, 43, 12, 7, 22] collect ((each > 20) ? 'above' : 'below')",
					returnType = "list<string>",
					equals = "['below', 'below', 'above', 'below', 'below', 'above']"),
					@example ("rgb col <- (flip(0.3) ? #red : (flip(0.9) ? #blue : #green));") },
			see = ":")
	public static Object iff(final IScope scope, final Boolean left, final IExpression right)
			throws GamaRuntimeException {
		final IOperator expr = (IOperator) right;
		return left ? expr.arg(0).value(scope) : expr.arg(1).value(scope);
	}

	@operator (
			value = ":",
			type = ITypeProvider.BOTH,
			content_type = ITypeProvider.BOTH,
			index_type = ITypeProvider.BOTH,
			category = IOperatorCategory.LOGIC,
			concept = { IConcept.LOGICAL, IConcept.TERNARY })
	
			@doc (	value = "It is used in combination with the ? operator. If the left-hand of ? operand evaluates to true,"
					+ " returns the value of the left-hand operand of the :, "
					+ "otherwise that of the right-hand operand of the :",
					examples = { @example (
							value = "[10, 19, 43, 12, 7, 22] collect ((each > 20) ? 'above' : 'below')",
							returnType = "list<string>",
							equals = "['below', 'below', 'above', 'below', 'below', 'above']"),
							 },
			see = "?")
	public static Object then(final IScope scope, final Object a, final Object b) {
		return null;
		// should never be called
	}

}
