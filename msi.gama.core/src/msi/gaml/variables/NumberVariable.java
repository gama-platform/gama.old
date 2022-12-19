/*******************************************************************************************************
 *
 * NumberVariable.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.variables;

import static msi.gaml.operators.Cast.asFloat;
import static msi.gaml.operators.Cast.asPoint;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.IType;

/**
 * The Class IntVariable.
 */
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.NEW_VAR_ID,
				optional = false,
				doc = @doc ("The name of the attribute")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.TYPE_ID,
						optional = true,
						doc = @doc ("The type of the attribute, either 'int', 'float', 'point' or 'date'")),
				@facet (
						name = IKeyword.INIT,
						// AD 02/16 TODO Allow to declare ITypeProvider.OWNER_TYPE here
						type = { IType.INT, IType.FLOAT, IType.POINT, IType.DATE },
						optional = true,
						doc = @doc ("The initial value of the attribute. Same as '<-'")),
				@facet (
						name = "<-",
						internal = true,
						// AD 02/16 TODO Allow to declare ITypeProvider.OWNER_TYPE here
						type = { IType.INT, IType.FLOAT, IType.POINT, IType.DATE },
						optional = true,
						doc = @doc ("The initial value of the attribute. Same as 'init:'")),
				@facet (
						name = IKeyword.VALUE,
						type = { IType.INT, IType.FLOAT, IType.POINT, IType.DATE },
						optional = true,
						doc = @doc (
								value = "",
								deprecated = "Use 'update' instead")),
				@facet (
						name = IKeyword.UPDATE,
						type = { IType.INT, IType.FLOAT, IType.POINT, IType.DATE },
						optional = true,
						doc = @doc ("An expression that will be evaluated each cycle to compute a new value for the attribute")),
				@facet (
						name = IKeyword.FUNCTION,
						type = { IType.INT, IType.FLOAT, IType.POINT, IType.DATE },
						optional = true,
						doc = @doc ("Used to specify an expression that will be evaluated each time the attribute is accessed. Equivalent to '->'. This facet is incompatible with both 'init:' and 'update:'")),
				@facet (
						name = "->",
						internal = true,
						type = { IType.INT, IType.FLOAT, IType.POINT, IType.DATE },
						optional = true,
						doc = @doc ("Used to specify an expression that will be evaluated each time the attribute is accessed. Equivalent to 'function:'. This facet is incompatible with both 'init:' and 'update:' and 'on_change:' (or the equivalent final block)")),
				@facet (
						name = IKeyword.CONST,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Indicates whether this attribute can be subsequently modified or not")),
				@facet (
						name = IKeyword.CATEGORY,
						type = IType.LABEL,
						optional = true,
						doc = @doc ("Soon to be deprecated. Declare the parameter in an experiment instead")),
				@facet (
						name = IKeyword.PARAMETER,
						type = IType.LABEL,
						optional = true,
						doc = @doc ("Soon to be deprecated. Declare the parameter in an experiment instead")),
				@facet (
						name = IKeyword.ON_CHANGE,
						type = IType.NONE,
						optional = true,
						doc = @doc ("Provides a block of statements that will be executed whenever the value of the attribute changes")),
				@facet (
						name = IKeyword.MIN,
						type = { IType.INT, IType.FLOAT, IType.POINT, IType.DATE },
						optional = true,
						doc = @doc ("The minimum value this attribute can take. The value will be automatically clamped if it is lower.")),
				@facet (
						name = IKeyword.MAX,
						type = { IType.INT, IType.FLOAT, IType.POINT, IType.DATE },
						optional = true,
						doc = @doc ("The maximum value this attribute can take. The value will be automatically clampled if it is higher.")),
				@facet (
						name = IKeyword.STEP,
						type = { IType.INT, IType.FLOAT, IType.POINT, IType.DATE },
						optional = true,
						doc = @doc ("A discrete step (used in conjunction with min and max) that constrains the values this variable can take")),
				@facet (
						name = IKeyword.AMONG,
						type = IType.LIST,
						optional = true,
						doc = @doc ("A list of constant values among which the attribute can take its value")) },
		omissible = IKeyword.NAME)
@symbol (
		kind = ISymbolKind.Variable.NUMBER,
		with_sequence = false,
		concept = { IConcept.ATTRIBUTE, IConcept.ARITHMETIC })
@inside (
		kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@doc ("Declaration of an attribute of a species or an experiment; this type of attributes accepts "
		+ "min:, max: and step: facets, automatically clamping the value if it is lower than min or higher than max.")
public class NumberVariable<T extends Comparable, Step extends Comparable> extends Variable {

	/** The max. */
	private final IExpression min, max, step;

	/** The max val. */
	private GAMA.InScope<T> minVal, maxVal;

	/** The step val. */
	private GAMA.InScope<Step> stepVal;

	/**
	 * Instantiates a new number variable.
	 *
	 * @param sd
	 *            the sd
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@SuppressWarnings ("unchecked")
	public NumberVariable(final IDescription sd) throws GamaRuntimeException {
		super(sd);
		min = getFacet(IKeyword.MIN);
		max = getFacet(IKeyword.MAX);
		step = getFacet(IKeyword.STEP);
		if (min != null && min.isConst()) {
			switch (type.id()) {
				case IType.INT:
					minVal = scope -> (T) Cast.asInt(scope, min.value(scope));
					break;
				case IType.FLOAT:
					minVal = scope -> (T) Cast.asFloat(scope, min.value(scope));
					break;
				case IType.POINT:
					minVal = scope -> (T) Cast.asPoint(scope, min.value(scope));
					break;
				case IType.DATE:
					minVal = scope -> (T) GamaDateType.staticCast(scope, min.value(scope), null, false);
			}
		} else {
			minVal = null;
		}
		if (max != null && max.isConst()) {
			switch (type.id()) {
				case IType.INT:
					maxVal = scope -> (T) Cast.asInt(scope, max.value(scope));
					break;
				case IType.FLOAT:
					maxVal = scope -> (T) Cast.asFloat(scope, max.value(scope));
					break;
				case IType.POINT:
					maxVal = scope -> (T) Cast.asPoint(scope, max.value(scope));
					break;
				case IType.DATE:
					maxVal = scope -> (T) GamaDateType.staticCast(scope, max.value(scope), null, false);
			}
		} else {
			maxVal = null;
		}
		if (step != null && step.isConst()) {
			switch (type.id()) {
				case IType.INT:
					stepVal = scope -> (Step) Cast.asInt(scope, step.value(scope));
					break;
				case IType.FLOAT:
					stepVal = scope -> (Step) Cast.asFloat(scope, step.value(scope));
					break;
				case IType.POINT:
					stepVal = scope -> (Step) Cast.asPoint(scope, step.value(scope));
					break;
				case IType.DATE:
					// Step for dates are durations expressed in seconds ?
					stepVal = scope -> (Step) Cast.asFloat(scope, step.value(scope));
			}
		} else {
			stepVal = null;
		}
	}

	@Override
	public Object coerce(final IAgent agent, final IScope scope, final Object v) throws GamaRuntimeException {
		final Object val = super.coerce(agent, scope, v);
		return switch (type.id()) {
			case IType.INT -> checkMinMax(agent, scope, (Integer) val);
			case IType.FLOAT -> checkMinMax(agent, scope, (Double) val);
			case IType.DATE -> checkMinMax(agent, scope, (GamaDate) val);
			case IType.POINT -> checkMinMax(agent, scope, (GamaPoint) val);
			default -> throw GamaRuntimeException.error("Impossible to create " + getName(), scope);
		};

	}

	/**
	 * Check min max.
	 *
	 * @param agent
	 *            the agent
	 * @param scope
	 *            the scope
	 * @param f
	 *            the f
	 * @return the integer
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected Integer checkMinMax(final IAgent agent, final IScope scope, final Integer f) throws GamaRuntimeException {
		if (min != null) {
			final Integer m = minVal == null ? Cast.asInt(scope, scope.evaluate(min, agent).getValue())
					: (Integer) minVal.run(scope);
			if (f < m) return m;
		}
		if (max != null) {
			final Integer m = maxVal == null ? Cast.asInt(scope, scope.evaluate(max, agent).getValue())
					: (Integer) maxVal.run(scope);
			if (f > m) return m;
		}
		return f;
	}

	/**
	 * Check min max.
	 *
	 * @param agent
	 *            the agent
	 * @param scope
	 *            the scope
	 * @param f
	 *            the f
	 * @return the double
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected Double checkMinMax(final IAgent agent, final IScope scope, final Double f) throws GamaRuntimeException {
		if (min != null) {
			final Double fmin =
					minVal == null ? asFloat(scope, scope.evaluate(min, agent).getValue()) : (Double) minVal.run(scope);
			if (f < fmin) return fmin;
		}
		if (max != null) {
			final Double fmax = maxVal == null ? Cast.asFloat(scope, scope.evaluate(max, agent).getValue())
					: (Double) maxVal.run(scope);
			if (f > fmax) return fmax;
		}
		return f;
	}

	/**
	 * Check min max.
	 *
	 * @param agent
	 *            the agent
	 * @param scope
	 *            the scope
	 * @param f
	 *            the f
	 * @return the gama point
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected GamaPoint checkMinMax(final IAgent agent, final IScope scope, final GamaPoint f)
			throws GamaRuntimeException {
		if (f == null) return null;
		if (min != null) {
			final GamaPoint fmin = (GamaPoint) (minVal == null ? asPoint(scope, scope.evaluate(min, agent).getValue())
					: minVal.run(scope));
			if (f.smallerThan(fmin)) return fmin;
		}
		if (max != null) {
			final GamaPoint fmax = (GamaPoint) (maxVal == null ? asPoint(scope, scope.evaluate(max, agent).getValue())
					: maxVal.run(scope));
			if (f.biggerThan(fmax)) return fmax;
		}
		return f;
	}

	/**
	 * Check min max.
	 *
	 * @param agent
	 *            the agent
	 * @param scope
	 *            the scope
	 * @param f
	 *            the f
	 * @return the gama date
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected GamaDate checkMinMax(final IAgent agent, final IScope scope, final GamaDate f)
			throws GamaRuntimeException {
		if (f == null) return null;
		if (min != null) {
			final GamaDate fmin = (GamaDate) (minVal == null
					? GamaDateType.staticCast(scope, scope.evaluate(min, agent).getValue(), null, false)
					: minVal.run(scope));
			if (f.compareTo(fmin) < 0) return fmin;
		}
		if (max != null) {
			final GamaDate fmax = (GamaDate) (maxVal == null
					? GamaDateType.staticCast(scope, scope.evaluate(max, agent).getValue(), null, false)
					: maxVal.run(scope));
			if (f.compareTo(fmax) > 0) return fmax;
		}
		return f;
	}

	@Override
	public T getMinValue(final IScope scope) {
		return minVal == null ? null : minVal.run(scope);
	}

	@Override
	public T getMaxValue(final IScope scope) {
		return maxVal == null ? null : maxVal.run(scope);
	}

	@Override
	public Step getStepValue(final IScope scope) {
		return stepVal == null ? null : stepVal.run(scope);
	}

	@Override
	public boolean acceptsSlider(final IScope scope) {
		return min != null && max != null && step != null;
	}

}
