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
package msi.gaml.variables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * The Class IntVariable.
 */
@facets(value = { @facet(name = IKeyword.NAME, type = IType.NEW_VAR_ID, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INIT, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.VALUE, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.CONST, type = IType.BOOL_STR, optional = true),
	@facet(name = IKeyword.CATEGORY, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.PARAMETER, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.MIN, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.MAX, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.STEP, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.DEPENDS_ON, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.INITER, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.GETTER, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.SETTER, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.AMONG, type = IType.LIST_STR, optional = true) }, omissible = IKeyword.NAME)
@symbol(name = { IType.FLOAT_STR, IType.INT_STR }, kind = ISymbolKind.VARIABLE)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT })
public class NumberVariable extends Variable {

	/** The max. */
	private final IExpression min, max, step;
	private final Number minVal, maxVal, stepVal;

	public NumberVariable(/* final ISymbol enclosingScope, */final IDescription sd)
		throws GamlException, GamaRuntimeException {
		super(/* enclosingScope, */sd);
		IScope scope = GAMA.getDefaultScope();
		min = getFacet(IKeyword.MIN);
		max = getFacet(IKeyword.MAX);
		step = getFacet(IKeyword.STEP);
		if ( min != null && min.isConst() ) {
			if ( type.id() == IType.INT ) {
				minVal = Cast.asInt(scope, min.value(GAMA.getDefaultScope()));
			} else {
				minVal = Cast.asFloat(scope, min.value(GAMA.getDefaultScope()));
			}
		} else {
			minVal = null;
		}
		if ( max != null && max.isConst() ) {
			if ( type.id() == IType.INT ) {
				maxVal = Cast.asInt(scope, max.value(GAMA.getDefaultScope()));
			} else {
				maxVal = Cast.asFloat(scope, max.value(GAMA.getDefaultScope()));
			}
		} else {
			maxVal = null;
		}
		if ( step != null && step.isConst() ) {
			if ( type.id() == IType.INT ) {
				stepVal = Cast.asInt(scope, step.value(GAMA.getDefaultScope()));
			} else {
				stepVal = Cast.asFloat(scope, step.value(GAMA.getDefaultScope()));
			}
		} else {
			stepVal = null;
		}
	}

	@Override
	public Object coerce(final IAgent agent, final IScope scope, final Object v)
		throws GamaRuntimeException {
		Object val = super.coerce(agent, scope, v);
		if ( type.id() == IType.INT ) {
			Integer result = checkMinMax(agent, scope, (Integer) val);
			return result;
		}
		Double result = checkMinMax(agent, scope, (Double) val);
		return result;
	}

	protected Integer checkMinMax(final IAgent agent, final IScope scope, final Integer f)
		throws GamaRuntimeException {
		if ( min != null ) {
			final Integer m =
				minVal == null ? Cast.asInt(scope, scope.evaluate(min, agent)) : (Integer) minVal;
			if ( f < m ) { return m; }
		}
		if ( max != null ) {
			final Integer m =
				maxVal == null ? Cast.asInt(scope, scope.evaluate(max, agent)) : (Integer) maxVal;
			if ( f > m ) { return m; }
		}
		return f;
	}

	protected Double checkMinMax(final IAgent agent, final IScope scope, final Double f)
		throws GamaRuntimeException {
		if ( min != null ) {
			final Double fmin =
				minVal == null ? Cast.asFloat(scope, scope.evaluate(min, agent)) : (Double) minVal;
			if ( f < fmin ) { return fmin; }
		}
		if ( max != null ) {
			final Double fmax =
				maxVal == null ? Cast.asFloat(scope, scope.evaluate(max, agent)) : (Double) maxVal;
			if ( f > fmax ) { return fmax; }
		}
		return f;
	}

	@Override
	public Number getMinValue() {
		return minVal;
	}

	@Override
	public Number getMaxValue() {
		return maxVal;
	}

	@Override
	public Number getStepValue() {
		return stepVal;
	}

}
