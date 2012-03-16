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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbolKind;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.AGENTS, kind = ISymbolKind.LAYER)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = { @facet(name = IKeyword.VALUE, type = IType.CONTAINER_STR, optional = false),
	@facet(name = IKeyword.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.FOCUS, type = IType.AGENT_STR, optional = true),
	@facet(name = IKeyword.ASPECT, type = IType.ID, optional = true) }, omissible = IKeyword.NAME)
public class AgentDisplayLayer extends AbstractDisplayLayer {

	private IExpression setOfAgents;
	final HashSet<IAgent> agents;
	HashSet<IAgent> agentsForLayer;
	protected String constantAspectName = null;
	protected IExpression aspectExpr;
	protected IExpression focusExpr;

	public AgentDisplayLayer(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		verifyFacetType(IKeyword.VALUE);
		setAgentsExpr(getFacet(IKeyword.VALUE));
		aspectExpr = getFacet(IKeyword.ASPECT);
		if ( aspectExpr != null && aspectExpr.isConst() ) {
			constantAspectName = aspectExpr.literalValue();
		}
		focusExpr = getFacet(IKeyword.FOCUS);
		agents = new HashSet();
	}

	public synchronized HashSet<IAgent> getAgentsToDisplay() {
		return agentsForLayer;
	}

	public List<? extends IAgent> computeAgents(final IScope sim) throws GamaRuntimeException {
		// Attention ! Si setOfAgents contient un seul agent, ce sont ses
		// composants qui vont être affichés.
		return Cast.asList(sim, getAgentsExpr().value(sim));
	}

	@Override
	public void compute(final IScope sim, final long cycle) throws GamaRuntimeException {
		super.compute(sim, cycle);
		// GUI.debug("Computing AgentDisplayLayer " + getName());
		synchronized (agents) {
			if ( cycle == 0l || agentsHaveChanged() ) {
				agents.clear();
				agents.addAll(computeAgents(sim));
			}
			agentsForLayer = (HashSet<IAgent>) agents.clone();
		}
	}

	public boolean agentsHaveChanged() {
		return !getAgentsExpr().isConst();
	}

	@Override
	public void prepare(final IDisplayOutput out, final IScope sim) throws GamaRuntimeException {
		super.prepare(out, sim);
		computeAspectName(sim);
		// compute(sim, 0);
	}

	@Override
	public short getType() {
		return IDisplayLayer.AGENTS;
	}

	public void computeAspectName(final IScope scope) throws GamaRuntimeException {
		String aspectName =
			constantAspectName == null ? aspectExpr == null ? IKeyword.DEFAULT : Cast.asString(
				scope, aspectExpr.value(scope)) : constantAspectName;
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
