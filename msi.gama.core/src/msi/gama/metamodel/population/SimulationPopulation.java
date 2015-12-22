/*********************************************************************************************
 *
 *
 * 'SimulationPopulation.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.metamodel.population;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.AbstractTopology.RootTopology;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;

public class SimulationPopulation extends GamaPopulation {

	public SimulationPopulation(final ISpecies species) {
		super(null, species);
	}

	@Override
	public void killMembers() throws GamaRuntimeException {
		// Simulations should be killed in a more precautious way...
		// for ( final IAgent a : toArray() ) {
		// if ( a.getScope().interrupted() ) {
		// a.dispose();
		// }
		// }
	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final int number,
		final List<? extends Map> initialValues, final boolean toBeScheduled) throws GamaRuntimeException {
		GuiUtils.waitStatus("Initializing simulation");
		final SimulationAgent world = new SimulationAgent(this);
		world.setIndex(currentAgentIndex++);
		world.setScheduled(toBeScheduled);
		add(world);
		getHost().setSimulation(world);
		if ( scope.interrupted() ) { return null; }
		GuiUtils.waitStatus("Instantiating agents");
		createVariablesFor(world.getScope(), Collections.singletonList(world), initialValues);
		if ( toBeScheduled ) {
			world.schedule();
			// world.scheduleAndExecute(sequence);
			// hqnghi if simulation is created manually, it's not scheduled and have to init implicitely
		}
		// AD: Removed because of Issue 1051 (double init).
		// else {
		// world._init_(scope);
		// end-hqnghi
		// }
		// GuiUtils.informStatus("Simulation Ready");
		return this;
	}

	@Override
	public ExperimentAgent getHost() {
		return (ExperimentAgent) super.getHost();
	}

	@Override
	public IAgent getAgent(final IScope scope, final ILocation value) {
		return get(null, 0);
	}

	public void setHost(final ExperimentAgent agent) {
		host = agent;
	}

	public void setTopology(final IScope scope, final IShape shape) {
		IExpression expr = species.getFacet(IKeyword.TORUS);
		final boolean torus = expr == null ? false : Cast.as(expr.value(scope), Boolean.class, false);
		topology = new RootTopology(scope, shape, torus);
	}

	@Override
	public void computeTopology(final IScope scope) throws GamaRuntimeException {
		// Temporary topology set before the world gets a shape
		topology = new AmorphousTopology();
	}

	// @Override
	// public IList<IAgent> computeAgentsToSchedule(final IScope scope) {
	// final int frequency = scheduleFrequency == null ? 1 : Cast.asInt(scope, scheduleFrequency.value(scope));
	// final int step = scope.getClock().getCycle();
	// if ( frequency == 0 || step % frequency != 0 ) { return GamaListFactory.EMPTY_LIST; }
	//
	// }

}