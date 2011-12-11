/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.util.*;
import msi.gama.gui.displays.IDisplay;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.LayerDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.Cast;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = ISymbol.AGENTS, kind = ISymbolKind.LAYER)
@inside(symbols = ISymbol.DISPLAY)
@facets({ @facet(name = ISymbol.VALUE, type = IType.LIST_STR, optional = false),
	@facet(name = ISymbol.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.SIZE, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.NAME, type = IType.LABEL, optional = false),
	@facet(name = ISymbol.FOCUS, type = IType.AGENT_STR, optional = true),
	@facet(name = ISymbol.ASPECT, type = IType.ID, optional = true) })
public class AgentDisplayLayer extends AbstractDisplayLayer {

	private IExpression setOfAgents;
	final HashSet<IAgent> agents;
	HashSet<IAgent> agentsForLayer;
	protected String constantAspectName = null;
	protected IExpression aspectExpr;
	protected IExpression focusExpr;

	public AgentDisplayLayer(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setAgentsExpr(getFacet(ISymbol.VALUE));
		aspectExpr = getFacet(ISymbol.ASPECT);
		if ( aspectExpr != null && aspectExpr.isConst() ) {
			constantAspectName = aspectExpr.literalValue();
		}
		focusExpr = getFacet(ISymbol.FOCUS);
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
	public void prepare(final LayerDisplayOutput out, final IScope sim) throws GamaRuntimeException {
		super.prepare(out, sim);
		computeAspectName(sim);
		// compute(sim, 0);
	}

	@Override
	public short getType() {
		return IDisplay.AGENTS;
	}

	public void computeAspectName(final IScope sim) throws GamaRuntimeException {
		String aspectName =
			constantAspectName == null ? (aspectExpr == null ? ISymbol.DEFAULT : Cast
				.asString(aspectExpr.value(sim))) : constantAspectName;
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
