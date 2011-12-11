/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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
package msi.gaml.attributes;

import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.Cast;

/**
 * The Class IntVariable.
 */
@facets({ @facet(name = ISymbol.NAME, type = IType.NEW_VAR_ID, optional = false),
	@facet(name = ISymbol.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = ISymbol.INIT, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.VALUE, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.CONST, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.CATEGORY, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.PARAMETER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.MIN, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.MAX, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.STEP, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.DEPENDS_ON, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.INITER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.GETTER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.SETTER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.AMONG, type = IType.LIST_STR, optional = true) })
@symbol(name = { IType.FLOAT_STR, IType.INT_STR }, kind = ISymbolKind.VARIABLE)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT })
public class NumberVariable extends Variable {

	/** The max. */
	private final IExpression min, max, step;
	private final Number minVal, maxVal, stepVal;

	public NumberVariable(/* final ISymbol enclosingScope, */final IDescription sd)
		throws GamlException, GamaRuntimeException {
		super(/* enclosingScope, */sd);
		min = getFacet(ISymbol.MIN);
		max = getFacet(ISymbol.MAX);
		step = getFacet(ISymbol.STEP);
		if ( min != null && min.isConst() ) {
			if ( type.id() == IType.INT ) {
				minVal = Cast.asInt(min.value(GAMA.getDefaultScope()));
			} else {
				minVal = Cast.asFloat(min.value(GAMA.getDefaultScope()));
			}
		} else {
			minVal = null;
		}
		if ( max != null && max.isConst() ) {
			if ( type.id() == IType.INT ) {
				maxVal = Cast.asInt(max.value(GAMA.getDefaultScope()));
			} else {
				maxVal = Cast.asFloat(max.value(GAMA.getDefaultScope()));
			}
		} else {
			maxVal = null;
		}
		if ( step != null && step.isConst() ) {
			if ( type.id() == IType.INT ) {
				stepVal = Cast.asInt(step.value(GAMA.getDefaultScope()));
			} else {
				stepVal = Cast.asFloat(step.value(GAMA.getDefaultScope()));
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
