/*********************************************************************************************
 *
 *
 * 'SignalVariable.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.variables;

import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.statements.Facets;
import msi.gaml.types.*;
import msi.gaml.variables.SignalVariable.SignalValidator;

@facets(value = {
	@facet(name = IKeyword.NAME,
		type = IType.NEW_VAR_ID,
		optional = false,
		doc = @doc("The name of the variable that will be introduced to represent this signal on the specified grid") ),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.VALUE,
		type = IType.NONE,
		optional = true,
		doc = @doc(value = "", deprecated = "Use 'update' instead") ),
	@facet(name = IKeyword.UPDATE,
		type = IType.NONE,
		optional = true,
		doc = @doc("An expression that will be evaluated each cycle to update the value of the signal on each grid cell") ),
	@facet(name = IKeyword.ENVIRONMENT,
		type = IType.SPECIES,
		optional = true,
		doc = @doc("The name of the grid species on which this signal will be propagated") ),
	@facet(name = IKeyword.ON,
		type = { IType.SPECIES, IType.CONTAINER },
		optional = true,
		doc = @doc("Either the name of the grid species on which this signal will be propagated (equivalent to 'environment:'), or an expression that returns a subset of the cells of this grid species") ),
	@facet(name = IKeyword.DECAY,
		type = IType.FLOAT,
		optional = false,
		doc = @doc("represents the amount to remove to the intensity of a signal, once dropped on a place, at each time step. It is a percentage between 0 and 1. If 'decay' is not defined, the signal will not be wiped from the places; otherwise, its intensity will be equal to (intensity `*` decay).") ),
	@facet(name = IKeyword.PROPAGATION,
		type = IType.LABEL,
		values = { IKeyword.DIFFUSION, IKeyword.GRADIENT },
		optional = true,
		doc = @doc("represents both the way the signal is propagated and the way to treat multiple propagations of the same signal occuring at once from different places. If propagation equals 'diffusion', the intensity of a signal is shared between its neighbours with respect to 'proportion', 'variation' and the number of neighbours of the environment places (4, 6 or 8). I.e., for a given signal S propagated from place P, the value transmitted to its N neighbours is : S' = (S / N / proportion) - variation. The intensity of S is then diminished by S `*` proportion on P. In a diffusion, the different signals of the same name see their intensities added to each other on each place. If propagation equals 'gradient', the original intensity is not modified, and each neighbours receives the intensity : S / proportion - variation. If multiple propagations occur at once, only the maximum intensity is kept on each place. If 'propagation' is not defined, it is assumed that it is equal to 'diffusion'.") ),
	@facet(name = IKeyword.PROPORTION,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("a value between 0 and 1 that represents the percentage of the intensity which will be shared between the neighbours in the diffusion. For instance, for an intensity of 80, and  a proportion of 0.5, in a 4-neighbours environment, each of the neighbouring places will receive an intensity of (80 `*` 0.5) / 4 = 10. If no 'proportion' is defined, it is assumed that the propagation corresponds to a diffusion where 100% of the intensity is equally divided between the neighbours. I.e., for an intensity of 100, and 4 neighbours per place, each of them receives a signal with an intensity of 25.") ),
	@facet(name = IKeyword.VARIATION,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("an absolute decrease of intensity that occurs between each place. It should be a positive number. However, negative numbers are allowed (be aware, in this case, that if no range is defined, the signal will certainly propagate in the whole environment). If no 'variation' is defined, it defaults to 1 in the case of a gradient type and 0 in the case of a diffusion.") ),
	@facet(name = IKeyword.RANGE,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("Indicates the distance (in meter) at which the signal stops propagating") ),
	@facet(name = IKeyword.AMONG, type = IType.LIST, optional = true) }, omissible = IKeyword.NAME)
@symbol(name = IKeyword.SIGNAL, kind = ISymbolKind.Variable.SIGNAL, with_sequence = false)
@inside(kinds = { ISymbolKind.SPECIES })
@doc("A special attribute that holds signals that can be propagated in the environment. Signals have a fixed name, a variable float intensity (represented by their facet 'value'), propagate in only one grid environment (facet 'environment'), can be limited to a specific range (facet 'range', in meter), and can see their intensity decrease over time (facet 'decay'). Signals can be propagated using different combinations of the 'variation', 'proportion' and 'propagation' facets. ")
@validator(SignalValidator.class)
public class SignalVariable extends NumberVariable {

	public static class SignalValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription d) {
			IExpressionDescription env = d.getFacets().get(IKeyword.ENVIRONMENT);
			IExpressionDescription on = d.getFacets().get(IKeyword.ON);
			if ( env == null ) {
				env = on;
			} else if ( on != null ) {
				IType tenv = env.getExpression().getType().getContentType();
				IType ton = on.getExpression().getType().getContentType();
				if ( !tenv.isAssignableFrom(ton) ) {
					d.warning("'environment:' and 'on:' should be of the same type", IGamlIssue.UNMATCHED_TYPES,
						env.getTarget());
				}
				env = on;
			}

			if ( env == null ) {
				d.error("No suitable grid environment defined for signal " + d.getName());
				return;
			}

			SpeciesDescription s = env.getExpression().getType().getContentType().getSpecies();

			if ( !s.isGrid() ) {
				d.error(s.getName() + " is not a grid. Signals can only be diffused on grids", IGamlIssue.WRONG_TYPE,
					env.getTarget());
			}

			IExpression decay = d.getFacets().getExpr(DECAY);
			if ( decay == null ) {
				decay = GAML.getExpressionFactory().createConst(0.1, Types.FLOAT);
				d.getFacets().put(DECAY, decay);
			}

			IExpression type = d.getFacets().getExpr(PROPAGATION);
			IExpression variation = d.getFacets().getExpr(VARIATION);
			if ( variation != null && variation.isConst() && Cast.as(variation, Double.class, false) < 0 ) {
				d.error("'variation' cannot be negative", IGamlIssue.WRONG_VALUE, VARIATION);
				return;
			}
			IExpression prop = d.getFacets().getExpr(PROPORTION);
			if ( type != null && type.literalValue().equals(GRADIENT) ) {
				if ( prop != null && prop.isConst() && Cast.as(prop, Double.class, false) > 1.0 ) {
					d.error("'proportion' cannot be greater than 1", IGamlIssue.WRONG_VALUE, PROPORTION);
					return;
				}
			}

			final VariableDescription vd = (VariableDescription) DescriptionFactory.create(
				SyntacticFactory.create(IKeyword.FLOAT, new Facets(NAME, d.getName(), MIN, "0.0"), false), s, null);
			s.addChild(vd);
			IExpressionFactory f = GAML.getExpressionFactory();
			IExpression v = s.getVarExpr(d.getName());
			IExpression value =
				f.createOperator("?", s, null, f.createOperator("<", s, null, v, f.createConst(0.1, Types.FLOAT)),
					f.createOperator(":", s, null, f.createConst(0.0, Types.FLOAT), f.createOperator("*", s, null, v,
						f.createOperator("-", s, null, f.createConst(1.0, Types.FLOAT), decay))));
			vd.getFacets().put(UPDATE, value);
			vd.setUpdatable(true);
			s.resortVarName(vd);
		}
	}

	private final short signalType;
	private String envName;
	private final IExpression typeExpr, propExpr, rangeExpr, variationExpr, onExpr;

	public SignalVariable(final IDescription sd) throws GamaRuntimeException {
		super(sd);
		this.
		type = Types.FLOAT;
		typeExpr = getFacet(IKeyword.PROPAGATION);
		propExpr = getFacet(IKeyword.PROPORTION);
		variationExpr = getFacet(IKeyword.VARIATION);
		rangeExpr = getFacet(IKeyword.RANGE);
		onExpr = getFacet(IKeyword.ON);
		envName = getLiteral(IKeyword.ENVIRONMENT);
		if ( envName == null ) {
			SpeciesDescription s = onExpr.getType().getContentType().getSpecies();
			envName = s.getName();
		}
		signalType = typeExpr == null ? IGrid.DIFFUSION
			: IKeyword.GRADIENT.equals(typeExpr.literalValue()) ? IGrid.GRADIENT : IGrid.DIFFUSION;
		// prop = propExpr == null ? 1.0 : null;
		// variation = variationExpr == null ? typeExpr == null ? null : 0d : null;
		// range = rangeExpr == null ? -1000.0 : rangeExpr.isConst() ? Cast.as(rangeExpr, Double.class, false) : null;
	}

	@Override
	protected void _setVal(final IAgent agent, final IScope scope, final Object v) throws GamaRuntimeException {
		super._setVal(agent, scope, v);

		final double result = Cast.asFloat(scope, v);
		if ( result > 0.0 && !agent.dead() ) {
			// final short signalType = this.signalType == null
			// ? IKeyword.GRADIENT.equals(scope.evaluate(typeExpr, agent)) ? IGrid.GRADIENT : IGrid.DIFFUSION
			// : this.signalType;
			final double prop = propExpr == null ? 1.0
				: Math.min(1.0, Math.max(0.0, Cast.asFloat(scope, scope.evaluate(propExpr, agent))));
			// ? Math.min(1.0, Math.max(0.0, Cast.asFloat(scope, scope.evaluate(propExpr, agent)))) : this.prop;
			final double variation =
				variationExpr == null ? 0d : Cast.asFloat(scope, scope.evaluate(variationExpr, agent));
			// this.variation == null ? Cast.asFloat(scope, scope.evaluate(variationExpr, agent)) : this.variation;
			final double range =
				rangeExpr == null ? -1000.0 : Math.max(0.0, Cast.asFloat(scope, scope.evaluate(rangeExpr, agent)));
			// this.range == null ? Math.max(0.0, Cast.asFloat(scope, scope.evaluate(rangeExpr, agent))) : this.range;
			getEnvironment(scope).diffuseVariable(scope, getName(), result, signalType, prop, variation,
				agent.getLocation(), range, onExpr == null ? null : scope.evaluate(onExpr, agent));
		}
	}

	private IGrid getEnvironment(final IScope scope) {
		return (IGrid) scope.getSimulationScope().getPopulationFor(envName).getTopology().getPlaces();
	}

}
