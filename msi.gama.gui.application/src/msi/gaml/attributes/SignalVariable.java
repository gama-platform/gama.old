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

import msi.gama.environment.GridDiffuser;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.Cast;
import msi.gama.util.matrix.GamaSpatialMatrix;

/**
 * Special variables that hold signals that can be propagated in the environment. Signals have a
 * fixed name, a variable float intensity (represented by their facet 'value'), propagate in only
 * one grid environment (facet 'environment'), can be limited to a specific range (facet 'range', in
 * meter), and can see their intensity decrease over time (facet 'decay').
 * 
 * Signals can be propagated using different combinations of the 'variation', 'proportion' and
 * 'propagation' facets.
 * 
 * 'decay' represents the amount to remove to the intensity of a signal, once dropped on a place, at
 * each time step. It is a percentage between 0 and 1. If 'decay' is not defined, the signal will
 * not be wiped from the places; otherwise, its intensity will be equal to (intensity * decay).
 * 
 * 'proportion' is a value between 0 and 1 that represents the percentage of the intensity which
 * will be shared between the neighbours in the diffusion. For instance, for an intensity of 80, and
 * a proportion of 0.5, in a 4-neighbours environment, each of the neighbouring places will receive
 * an intensity of (80 * 0.5) / 4 = 10.
 * 
 * 'variation' is an absolute decrease of intensity that occurs between each place. It should be a
 * positive number. However, negative numbers are allowed (be aware, in this case, that if no range
 * is defined, the signal will certainly propagate in the whole environment).
 * 
 * As they will be eventually shared by all the agents, 'decay', 'variation' and 'proportion' must
 * either be a constant or depend on a global (world) variable parameter. They cannot depend on an
 * other variable of the agent. [CHANGED : they are now variable].
 * 
 * If no 'proportion' is defined, it is assumed that the propagation corresponds to a diffusion
 * where 100% of the intensity is equally divided between the neighbours. I.e., for an intensity of
 * 100, and 4 neighbours per place, each of them receives a signal with an intensity of 25.
 * 
 * If no 'variation' is defined, it defaults to 1 in the case of a gradient type and 0 in the case
 * of a diffusion.
 * 
 * 'propagation' represents both the way the signal is propagated and the way to treat multiple
 * propagations of the same signal occuring at once from different places.
 * 
 * If propagation equals 'diffusion', the intensity of a signal is shared between its neighbours
 * with respect to 'proportion', 'variation' and the number of neighbours of the environment places
 * (4, 6 or 8). I.e., for a given signal S propagated from place P, the value transmitted to its N
 * neighbours is : S' = (S / N / proportion) - variation. The intensity of S is then diminished by
 * S*proportion on P. In a diffusion, the different signals of the same name see their intensities
 * added to each other on each place.
 * 
 * If propagation equals 'gradient', the original intensity is not modified, and each neighbours
 * receives the intensity : S / proportion - variation. If multiple propagations occur at once, only
 * the maximum intensity is kept on each place.
 * 
 * If 'propagation' is not defined, it is assumed that it is equal to 'diffusion'.
 * 
 * 
 */
@facets({
	@facet(name = ISymbol.NAME, type = IType.NEW_VAR_ID, optional = false),
	@facet(name = ISymbol.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = ISymbol.VALUE, type = IType.FLOAT_STR, optional = false),
	@facet(name = ISymbol.ENVIRONMENT, type = IType.SPECIES_STR, optional = false),
	@facet(name = ISymbol.DECAY, type = IType.FLOAT_STR, optional = false),
	@facet(name = ISymbol.PROPAGATION, type = IType.LABEL, values = { ISymbol.DIFFUSION,
		ISymbol.GRADIENT }, optional = true),
	@facet(name = ISymbol.PROPORTION, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.VARIATION, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.RANGE, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.DEPENDS_ON, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.INITER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.GETTER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.SETTER, type = IType.LABEL, optional = true),
	@facet(name = ISymbol.AMONG, type = IType.LIST_STR, optional = true) })
@symbol(name = ISymbol.SIGNAL, kind = ISymbolKind.VARIABLE)
@inside(kinds = { ISymbolKind.SPECIES })
public class SignalVariable extends NumberVariable {

	// private GridAgentManager environment;
	private final Short signalType;
	private final Double prop, range, variation;
	private final String envName;
	private final IExpression typeExpr, propExpr, rangeExpr, variationExpr;

	// private IVariable var;

	/**
	 * @throws GamaRuntimeException Instantiates a new signal variable.
	 * 
	 * @param sim the sim
	 * @param v the v
	 */
	public SignalVariable(/* final ISymbol enclosingScope, */final IDescription sd)
		throws GamlException, GamaRuntimeException {
		super(/* enclosingScope, */sd);
		type = Types.get(IType.FLOAT);
		contentType = Types.get(IType.FLOAT);
		typeExpr = getFacet(ISymbol.PROPAGATION);
		propExpr = getFacet(ISymbol.PROPORTION);
		variationExpr = getFacet(ISymbol.VARIATION);
		rangeExpr = getFacet(ISymbol.RANGE);
		envName = getLiteral(ISymbol.ENVIRONMENT);
		if ( envName == null || sd.getModelDescription().getSpeciesDescription(envName) == null ) { throw new GamlException(
			"Environment of signal " + this.getName() + " cannot be determined."); }
		signalType = typeExpr == null ? GridDiffuser.DIFFUSION : null;
		prop = propExpr == null ? 1.0 : null;
		variation = variationExpr == null ? signalType == null ? null : 0d : null;
		range =
			rangeExpr == null ? -1000.0 : rangeExpr.isConst() ? Cast.asFloat(rangeExpr.value(GAMA
				.getDefaultScope())) : null;
	}

	@Override
	protected void _setVal(final IAgent agent, final IScope scope, final Object v)
		throws GamaRuntimeException {
		super._setVal(agent, scope, v);
		GamaSpatialMatrix environment =
			(GamaSpatialMatrix) agent.getPopulationFor(envName).getTopology().getPlaces();
		final double result = Cast.asFloat(v);
		if ( result > 0.0 ) {
			short signalType =
				this.signalType == null ? ISymbol.GRADIENT.equals(scope.evaluate(typeExpr, agent))
					? GridDiffuser.GRADIENT : GridDiffuser.DIFFUSION : this.signalType;
			double prop =
				this.prop == null ? Math.min(1.0,
					Math.max(0.0, Cast.asFloat(scope.evaluate(propExpr, agent)))) : this.prop;
			double variation =
				this.variation == null ? Cast.asFloat(scope.evaluate(variationExpr, agent))
					: this.variation;
			double range =
				this.range == null ? Math.max(0.0, Cast.asFloat(scope.evaluate(rangeExpr, agent)))
					: this.range;
			environment.diffuseVariable(scope, getName(), result, signalType, prop, variation,
				agent.getLocation(), range);
		}
	}

}
