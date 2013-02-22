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
package msi.gama.metamodel.population;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.ScheduledAction;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaTopologyType;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 8 nov. 2010
 * 
 * @todo Description
 * 
 */
public class GamlPopulation extends SinglePopulation implements IGamlPopulation {

	IExpression scheduleFrequency = new JavaConstExpression(1);

	class PopulationManagement extends ScheduledAction {

		final IExpression listOfTargetAgents;

		PopulationManagement(final IExpression exp) {
			listOfTargetAgents = exp;
		}

		@Override
		public void execute(final IScope scope) throws GamaRuntimeException {
			IPopulation pop = GamlPopulation.this;
			IList<IAgent> targets = Cast.asList(scope, listOfTargetAgents.value(scope));
			IList<IAgent> toKill = new GamaList();
			for ( IAgent agent : pop ) {
				IAgent target = Cast.asAgent(scope, agent.getAttribute("target"));
				if ( targets.contains(target) ) {
					targets.remove(target);
				} else {
					toKill.add(agent);
				}
			}
			for ( IAgent agent : toKill ) {
				agent.die();
			}
			List<Map<String, Object>> attributes = new ArrayList();
			for ( IAgent target : targets ) {
				Map<String, Object> att = new HashMap();
				att.put("target", target);
				attributes.add(att);
			}
			pop.createAgents(scope, targets.size(), attributes, false);
		}

	}

	public GamlPopulation(final IAgent host, final ISpecies species) {
		super(host, species);
		if ( species.isMirror() ) {
			host.getScheduler().insertEndAction(
				new PopulationManagement(species.getFacet(IKeyword.MIRRORS)));
		}

		// Add an attribute to the agents (dans SpeciesDescription)
		IExpression exp = species.getFrequency();
		if ( exp != null ) {
			scheduleFrequency = exp;
		}
	}

	@Override
	protected void computeTopology(final IScope scope) throws GamaRuntimeException {
		IExpression expr = species.getFacet(IKeyword.TOPOLOGY);
		if ( expr == null ) {
			super.computeTopology(scope);
		} else {
			// FIXME Should be caught before as a compilation error (and more detailed)
			if ( species.isGlobal() || species.isGraph() || species.isGrid() ) {
				super.computeTopology(scope);
				throw new GamaRuntimeException("Impossible to assign a topology to " +
					species.getName() + " as it already defines one.", true);
			}

			// System.out.println("host : " + host);
			topology = GamaTopologyType.staticCast(scope, scope.evaluate(expr, host), null);
		}
	}

	@Override
	public void step(final IScope scope) throws GamaRuntimeException {
		IArchitecture c = species.getArchitecture();
		c.executeOn(scope);
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		IArchitecture control = species.getArchitecture();
		control.init(scope);
	}

	@Override
	public void createVariablesFor(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		for ( final String s : orderedVarNames ) {
			final IVariable var = species.getVar(s);
			var.initializeWith(scope, agent, null);
		}
	}

	@Override
	public void updateVariablesFor(final IScope scope, final IAgent agent)
		throws GamaRuntimeException {
		for ( int i = 0; i < updatableVars.length; i++ ) {
			updatableVars[i].updateFor(scope, agent);
		}
	}

	/**
	 * 
	 * @see msi.gama.interfaces.IPopulation#computeAgentsToSchedule(msi.gama.interfaces.IScope,
	 *      msi.gama.util.GamaList)
	 */
	@Override
	public void computeAgentsToSchedule(final IScope scope, final IList list)
		throws GamaRuntimeException {
		int frequency = Cast.asInt(scope, scheduleFrequency.value(scope));
		int step = scope.getClock().getCycle();
		IExpression ags = getSpecies().getSchedule();
		List<IAgent> allAgents = getAgentsList();
		List<IAgent> agents = ags == null ? allAgents : Cast.asList(scope, ags.value(scope));
		if ( step % frequency == 0 ) {
			list.addAll(agents);
		}
		if ( species.hasMicroSpecies() ) {
			for ( IAgent agent : allAgents ) {
				// FIXME: shouldn't it be "agents" rather than "allAgents" ?
				agent.computeAgentsToSchedule(scope, list);
			}
		}
	}

}
