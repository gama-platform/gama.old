/*******************************************************************************************************
 *
 * msi.gaml.operators.Points.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.operators;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IOperatorValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 11 dec. 2010
 *
 * @todo Description
 *
 */
public class Points {
	public static class PointValidator implements IOperatorValidator {

		@Override
		public boolean validate(final IDescription context, final EObject emfContext, final IExpression... arguments) {
			for (final IExpression expr : arguments) {
				if (!expr.getGamlType().isNumber()) {
					context.error("Points can only be built with int or float coordinates", WRONG_TYPE, emfContext);
					return false;
				}
			}
			return true;
		}
	}

	@operator (
			value = IKeyword.POINT,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			internal = true)
	@validator (PointValidator.class)
	@no_test
	public static ILocation toPoint(final IScope scope, final IExpression xExp, final IExpression yExp) {
		if (scope != null) {
			scope.setHorizontalPixelContext();
		}
		final double x = Cast.asFloat(scope, xExp.value(scope));
		if (scope != null) {
			scope.setVerticalPixelContext();
		}
		final double y = Cast.asFloat(scope, yExp.value(scope));
		return new GamaPoint(x, y);
	}

	@operator (
			value = IKeyword.POINT,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			internal = true)
	@validator (PointValidator.class)
	@no_test
	public static ILocation toPoint(final IScope scope, final IExpression xExp, final IExpression yExp,
			final IExpression zExp) {
		if (scope != null) {
			scope.setHorizontalPixelContext();
		}
		final double x = Cast.asFloat(scope, xExp.value(scope));
		if (scope != null) {
			scope.setVerticalPixelContext();
		}
		final double y = Cast.asFloat(scope, yExp.value(scope));
		final double z = Cast.asFloat(scope, zExp.value(scope));
		return new GamaPoint(x, y, z);
	}

	@operator (
			value = IKeyword.DIVIDE,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = { IConcept.POINT })
	@doc (
			value = "Returns a point with coordinates divided by the number",
			usages = @usage (
					value = "if the left operand is a point, returns a new point with coordinates divided by the right operand",
					examples = { @example (
							value = "{5, 7.5} / 2.5",
							equals = "{2, 3}"),
							@example (
									value = "{2,5} / 4",
									equals = "{0.5,1.25}") }))
	@test ("{5, 7.5} / 2.5 = {2,3}")
	public static ILocation divide(final IScope scope, final GamaPoint p, final Double d) {
		if (d == 0d) { throw GamaRuntimeException.error("Division by zero", scope); }
		return new GamaPoint(p.x / d, p.y / d, p.z / d);
	}

	@operator (
			value = IKeyword.DIVIDE,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = {})
	@doc (
			value = "Returns a point with coordinates divided by the number")
	@test ("{2,5} / 4 = {0.5,1.25}")
	@test ("is_error({2,5} / 0)")
	public static ILocation divide(final IScope scope, final GamaPoint p, final Integer d) {
		if (d == 0) { throw GamaRuntimeException.error("Division by zero", scope); }
		return new GamaPoint(p.x / d.doubleValue(), p.y / d.doubleValue(), p.z / d.doubleValue());
	}

	@operator (
			value = IKeyword.MULTIPLY,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = { IConcept.POINT })
	@doc (
			value = "Returns a point with coordinates multiplied by a number.")
	@test ("{2,5} * 4.0 = {8.0,20.0}")
	@test ("{2,5} * 0.0 = {0.0,0.0}")
	public static ILocation multiply(final GamaPoint p1, final Double d) {
		if (p1 == null) { return new GamaPoint(); }
		return new GamaPoint(p1.x * d, p1.y * d, p1.z * d);
	}

	@operator (
			value = IKeyword.MULTIPLY,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = {})
	@doc (
			value = "Returns a point with coordinates multiplied by a number.",
			usages = @usage (
					value = "if the left-hand operator is a point and the right-hand a number, "
							+ "returns a point with coordinates multiplied by the number",
					examples = { @example (
							value = "{2,5} * 4",
							equals = "{8.0, 20.0}"),
							@example (
									value = "{2, 4} * 2.5",
									equals = "{5.0, 10.0}") }))
	@test ("{2,5} * 4 = {8,20}")
	@test ("{2,5} * 0 = {0,0}")
	public static ILocation multiply(final GamaPoint p1, final Integer d) {
		if (p1 == null) { return new GamaPoint(); }
		return new GamaPoint(p1.x * d.doubleValue(), p1.y * d.doubleValue(), p1.z * d.doubleValue());
	}

	// ATTENTION: produit scalaire.
	@operator (
			value = IKeyword.MULTIPLY,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = {})
	@doc (
			value = "Returns the scalar product of two points.",
			usages = @usage (
					value = "if both operands are points, returns their scalar product",
					examples = @example (
							value = "{2,5} * {4.5, 5}",
							equals = "34.0")))
	@test ("{2,5} * {4.5, 5} = 34.0")
	public static Double multiply(final GamaPoint p1, final GamaPoint p) {
		if (p1 == null || p == null) { return 0d; }
		return p1.x * p.x + p1.y * p.y + p1.z * p.z;
	}

	@operator (
			value = "norm",
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = { IConcept.POINT })
	@doc (
			value = "the norm of the vector with the coordinates of the point operand.",
			examples = @example (
					value = "norm({3,4})",
					equals = "5.0"))
	@test (
			value = "norm({3,4}) = 5.0",
			name = "Regular")
	@test (
			value = "norm({1,1}) = sqrt(2)",
			name = "Not")
	@test ("norm({0,0}) = 0.0")
	@test ("norm({1,0}) = norm({0,1})")
	public static Double norm(final IScope scope, final GamaPoint p) throws GamaRuntimeException {
		if (p == null) { return 0d; }
		return Maths.sqrt(scope, p.x * p.x + p.y * p.y + p.z * p.z);
	}

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = { IConcept.POINT })
	@doc (
			value = "Returns a point with coordinate summing of the two operands.",
			usages = @usage (
					value = "if both operands are points, returns their sum.",
					examples = @example (
							value = "{1, 2} + {4, 5}",
							equals = "{5.0, 7.0}")))
	@test ("{1, 2} + {4, 5} = {5,7}")
	@test (
			value = "point p <- {1, 2}; p + {0, 0} = p",
			warning = true)
	public static ILocation add(final GamaPoint p1, final GamaPoint p) {
		if (p1 == null) { return p; }
		if (p == null) { return p1; }
		return new GamaPoint(p1.x + p.x, p1.y + p.y, p1.z + p.z);
	}

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = {})
	@doc (
			value = "Returns a point with coordinate summing of the two operands.",
			usages = @usage (
					value = "if the left-hand operand is a point and the right-hand a number, returns a new point with each coordinate as the sum of the operand coordinate with this number.",
					examples = {@example (
									value = "{1, 2} + 4.5",
									equals = "{5.5, 6.5,4.5}") }))
	public static ILocation add(final GamaPoint p1, final Double p) {
		if (p1 == null) { return new GamaPoint(p, p, p); }
		return new GamaPoint(p1.x + p, p1.y + p, p1.z + p);
	}

	@operator (
			value = IKeyword.PLUS,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = {})
	@doc (
			value = "Returns a point with coordinate summing of the two operands.",
			examples = { @example (
					value = "{1, 2} + 4",
					equals = "{5.0, 6.0,4.0}")})
	public static ILocation add(final GamaPoint p1, final Integer p) {
		if (p1 == null) { return new GamaPoint(p, p, p); }
		return new GamaPoint(p1.x + p, p1.y + p, p1.z + p);
	}

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = { IConcept.POINT })
	@doc (
			value = "Returns a point with coordinate resulting from the first operand minus the second operand.",
			usages = @usage (
					value = "if left-hand operand is a point and the right-hand a number, returns a new point with each coordinate as the difference of the operand coordinate with this number.",
					examples = { @example (
							value = "{1, 2} - 4.5",
							equals = "{-3.5, -2.5, -4.5}"),
							@example (
									value = "{1, 2} - 4",
									equals = "{-3.0,-2.0,-4.0}") }))
	public static ILocation subtract(final GamaPoint p1, final Double p) {
		if (p1 == null) { return new GamaPoint(-p, -p, -p); }
		return new GamaPoint(p1.x - p, p1.y - p, p1.z - p);
	}

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = { IConcept.POINT })
	@doc (
			value = "Returns a point with coordinate resulting from the negation of the operand",
			usages = @usage (
					value = "",
					examples = { @example (
							value = "-{3.0,5.0}",
							equals = "{-3.0,-5.0}"),
							@example (
									value = "-{1.0,6.0,7.0}",
									equals = "{-1.0,-6.0,-7.0}") }))
	public static ILocation subtract(final GamaPoint p) {
		return new GamaPoint(-p.x, -p.y, -p.z);
	}

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = {})
	@doc (
			value = "Returns a point with coordinate resulting from the first operand minus the second operand.",
			usages = @usage (
					value = "if both operands are points, returns their difference (coordinates per coordinates).",
					examples = @example (
							value = "{1, 2} - {4, 5}",
							equals = "{-3.0, -3.0}")))
	public static ILocation subtract(final GamaPoint p1, final GamaPoint p) {
		if (p == null) { return p1; }
		if (p1 == null) { return p.negated(); }
		return new GamaPoint(p1.x - p.x, p1.y - p.y, p1.z - p.z);
	}

	@operator (
			value = IKeyword.MINUS,
			can_be_const = true,
			category = IOperatorCategory.POINT,
			concept = {})
	@doc (
			value = "Returns a point with coordinate resulting from the first operand minus the second operand.",
			examples = {
					@example(value="{2.0,3.0,4.0} - 1", equals="{1.0,2.0,3.0}")
			})
	@test ("{2.0,3.0,4.0} - 1 = {1.0,2.0,3.0}")
	public static ILocation subtract(final GamaPoint p1, final Integer p) {
		if (p1 == null) { return new GamaPoint(-p, -p, -p); }
		return new GamaPoint(p1.x - p, p1.y - p, p1.z - p);
	}

	@operator (
			value = "with_precision",
			can_be_const = true,
			concept = { IConcept.POINT })
	@doc (
			value = "Rounds off the ordinates of the left-hand point to the precision given by the value of right-hand operand",
			examples = { @example (
					value = "{12345.78943, 12345.78943, 12345.78943} with_precision 2 ",
					equals = "{12345.79, 12345.79, 12345.79}") },
			see = "round")
	public static ILocation round(final ILocation v, final Integer precision) {
		if (v == null) { return null; }
		return new GamaPoint(Maths.round(v.getX(), precision), Maths.round(v.getY(), precision),
				Maths.round(v.getZ(), precision));
	}

	@operator (
			value = "round",
			can_be_const = true,
			concept = { IConcept.POINT })
	@doc (
			value = "Returns the rounded value of the operand.",
			examples = { @example (
					value = "{12345.78943,  12345.78943, 12345.78943} with_precision 2",
					equals = "{12345.79,12345.79,12345.79}") },
			see = "round")
	@test("{12345.78943,  12345.78943, 12345.78943} with_precision 2 = {12345.79,12345.79,12345.79}")
	public static ILocation round(final ILocation v) {
		if (v == null) { return null; }
		return new GamaPoint(Maths.round(v.getX()), Maths.round(v.getY()), Maths.round(v.getZ()));
	}

}
