/*******************************************************************************************************
 *
 * msi.gaml.variables.NumberVariable.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.variables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
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
						doc = @doc ("The type of the attribute, either 'int' or 'float'")),
				@facet (
						name = IKeyword.INIT,
						// AD 02/16 TODO Allow to declare ITypeProvider.OWNER_TYPE here
						type = { IType.INT, IType.FLOAT },
						optional = true,
						doc = @doc ("The initial value of the attribute")),
				@facet (
						name = IKeyword.VALUE,
						type = { IType.INT, IType.FLOAT },
						optional = true,
						doc = @doc (
								value = "",
								deprecated = "Use 'update' instead")),
				@facet (
						name = IKeyword.UPDATE,
						type = { IType.INT, IType.FLOAT },
						optional = true,
						doc = @doc ("An expression that will be evaluated each cycle to compute a new value for the attribute")),
				@facet (
						name = IKeyword.FUNCTION,
						type = { IType.INT, IType.FLOAT },
						optional = true,
						doc = @doc ("Used to specify an expression that will be evaluated each time the attribute is accessed. This facet is incompatible with both 'init:' and 'update:'")),
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
						type = { IType.INT, IType.FLOAT },
						optional = true,
						doc = @doc ("The minimum value this attribute can take")),
				@facet (
						name = IKeyword.MAX,
						type = { IType.INT, IType.FLOAT },
						optional = true,
						doc = @doc ("The maximum value this attribute can take. ")),
				@facet (
						name = IKeyword.STEP,
						type = IType.INT,
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
@doc ("Allows to declare an attribute of a species or experiment")
public class NumberVariable extends Variable {

	/** The max. */
	private final IExpression min, max, step;
	private final Number minVal, maxVal, stepVal;

	public NumberVariable(final IDescription sd) throws GamaRuntimeException {
		super(sd);
		final IScope scope = null;
		// IScope scope = GAMA.obtainNewScope();
		min = getFacet(IKeyword.MIN);
		max = getFacet(IKeyword.MAX);
		step = getFacet(IKeyword.STEP);
		if (min != null && min.isConst()) {
			if (type.id() == IType.INT) {
				minVal = Cast.asInt(scope, min.value(scope));
			} else {
				minVal = Cast.asFloat(scope, min.value(scope));
			}
		} else {
			minVal = null;
		}
		if (max != null && max.isConst()) {
			if (type.id() == IType.INT) {
				maxVal = Cast.asInt(scope, max.value(scope));
			} else {
				maxVal = Cast.asFloat(scope, max.value(scope));
			}
		} else {
			maxVal = null;
		}
		if (step != null && step.isConst()) {
			if (type.id() == IType.INT) {
				stepVal = Cast.asInt(scope, step.value(scope));
			} else {
				stepVal = Cast.asFloat(scope, step.value(scope));
			}
		} else {
			stepVal = null;
		}
		// GAMA.releaseScope(scope);
	}

	@Override
	public Object coerce(final IAgent agent, final IScope scope, final Object v) throws GamaRuntimeException {
		final Object val = super.coerce(agent, scope, v);
		if (type.id() == IType.INT) {
			final Integer result = checkMinMax(agent, scope, (Integer) val);
			return result;
		}
		final Double result = checkMinMax(agent, scope, (Double) val);
		return result;
	}

	protected Integer checkMinMax(final IAgent agent, final IScope scope, final Integer f) throws GamaRuntimeException {
		if (min != null) {
			final Integer m =
					minVal == null ? Cast.asInt(scope, scope.evaluate(min, agent).getValue()) : (Integer) minVal;
			if (f < m) { return m; }
		}
		if (max != null) {
			final Integer m =
					maxVal == null ? Cast.asInt(scope, scope.evaluate(max, agent).getValue()) : (Integer) maxVal;
			if (f > m) { return m; }
		}
		return f;
	}

	protected Double checkMinMax(final IAgent agent, final IScope scope, final Double f) throws GamaRuntimeException {
		if (min != null) {
			final Double fmin =
					minVal == null ? Cast.asFloat(scope, scope.evaluate(min, agent).getValue()) : (Double) minVal;
			if (f < fmin) { return fmin; }
		}
		if (max != null) {
			final Double fmax =
					maxVal == null ? Cast.asFloat(scope, scope.evaluate(max, agent).getValue()) : (Double) maxVal;
			if (f > fmax) { return fmax; }
		}
		return f;
	}

	@Override
	public Number getMinValue(final IScope scope) {
		return minVal;
	}

	@Override
	public Number getMaxValue(final IScope scope) {
		return maxVal;
	}

	@Override
	public Number getStepValue(final IScope scope) {
		return stepVal;
	}

	@Override
	public boolean acceptsSlider(final IScope scope) {
		return min != null && max != null && step != null;
	}

}
