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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.layers.AgentLayerStatement.AgentLayerValidator;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.AGENTS, kind = ISymbolKind.LAYER, with_sequence = false)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = { @facet(name = IKeyword.VALUE, type = IType.CONTAINER, optional = false),
	@facet(name = IKeyword.POSITION, type = IType.POINT, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.FOCUS, type = IType.AGENT, optional = true),
	@facet(name = IKeyword.ASPECT, type = IType.ID, optional = true),
	@facet(name = IKeyword.REFRESH, type = IType.BOOL, optional = true) }, omissible = IKeyword.NAME)
@validator(AgentLayerValidator.class)
public class AgentLayerStatement extends AbstractLayerStatement {

	

	public static class AgentLayerValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			// Should be broken down in subclasses
			IExpressionDescription ed = description.getFacets().get(SPECIES);
			if ( ed != null ) {
				String s = ed.compileAsLabel().getExpression().literalValue();
				SpeciesDescription target = description.getSpeciesDescription(s);
				if ( target == null ) {
					description.error(s + " is not the name of a species", IGamlIssue.WRONG_TYPE, ed.getTarget());
					return;
				} else {
					if ( description.getKeyword().equals(GRID_POPULATION) && !target.isGrid() ) {
						description.error(s + " is not a grid", IGamlIssue.WRONG_TYPE, ed.getTarget());
						return;
					} else {
						IExpression expr =
							msi.gama.util.GAML.getExpressionFactory().createConst(s, Types.get(IType.SPECIES));
						description.getFacets().put(SPECIES, expr);
					}
				}
			}
			ed = description.getFacets().get(ASPECT);
			if ( ed != null ) {
				String s = description.getFacets().getLabel(SPECIES);
				String a = description.getFacets().getLabel(ASPECT);
				SpeciesDescription species = description.getSpeciesDescription(s);
				if ( species != null ) {
					if ( species.getAspect(a) != null ) {
						ed.compileAsLabel();
					} else {
						description.error(a + " is not the name of an aspect of " + s, IGamlIssue.GENERAL, description
							.getFacets().get(ASPECT).getTarget());
					}
				}
			}
		}

	}

	private IExpression setOfAgents;
	// final HashSet<IAgent> agents;
	HashSet<IAgent> agentsForLayer = new HashSet();
	protected String constantAspectName = null;
	protected IExpression aspectExpr;
	protected IExpression focusExpr;

	public AgentLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setAgentsExpr(getFacet(IKeyword.VALUE));
		aspectExpr = getFacet(IKeyword.ASPECT);
		if ( aspectExpr != null && aspectExpr.isConst() ) {
			constantAspectName = aspectExpr.literalValue();
		}
		focusExpr = getFacet(IKeyword.FOCUS);
		// agents = new HashSet();
	}

	public synchronized HashSet<IAgent> getAgentsToDisplay() {
		return agentsForLayer;
	}

	public List<? extends IAgent> computeAgents(final IScope sim) throws GamaRuntimeException {
		// Attention ! Si setOfAgents contient un seul agent, ce sont ses
		// composants qui vont �tre affich�s.
		return Cast.asList(sim, getAgentsExpr().value(sim));
	}

	@Override
	public boolean _step(final IScope scope) {
		if ( scope.getClock().getCycle() == 0 || agentsHaveChanged() ) {
			agentsForLayer = new HashSet(computeAgents(scope));
		}
		return true;
	}

	public boolean agentsHaveChanged() {
		return !getAgentsExpr().isConst();
	}

	@Override
	public boolean _init(final IScope scope) {
		computeAspectName(scope);
		return true;
	}

	@Override
	public short getType() {
		return ILayerStatement.AGENTS;
	}

	public void computeAspectName(final IScope scope) throws GamaRuntimeException {
		final String aspectName =
			constantAspectName == null ? aspectExpr == null ? IKeyword.DEFAULT : Cast.asString(scope,
				aspectExpr.value(scope)) : constantAspectName;
		setAspect(aspectName);
	}

	public void setAspect(final String currentAspect) {
		this.constantAspectName = currentAspect;
	}

	public String getAspectName() {
		return constantAspectName;
	}

	public void setAgentsExpr(final IExpression setOfAgents) {
		this.setOfAgents = setOfAgents;
	}

	IExpression getAgentsExpr() {
		return setOfAgents;
	}
}
