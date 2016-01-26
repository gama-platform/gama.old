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
package msi.gama.kernel.simulation;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.AbstractTopology.RootTopology;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;

public class SimulationPopulation extends GamaPopulation {

	public SimulationPopulation(final ISpecies species) {
		super(null, species);
	}

	@Override
	public void initializeFor(final IScope scope) {
		super.initializeFor(scope);
		this.currentAgentIndex = 0;
	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final int number,
		final List<? extends Map> initialValues, final boolean toBeScheduled) throws GamaRuntimeException {
		scope.getGui().waitStatus("Initializing simulation");
		final SimulationAgent world = new SimulationAgent(this);
		world.setIndex(currentAgentIndex++);
		world.setScheduled(toBeScheduled);
		world.setName("Simulation #" + world.getIndex() + " of model " +
			getSpecies().getName().replace(ModelDescription.MODEL_SUFFIX, ""));
		add(world);
		getHost().setSimulation(world);
		if ( scope.interrupted() ) { return null; }
		scope.getGui().waitStatus("Instantiating agents");
		createVariablesFor(world.getScope(), Collections.singletonList(world), initialValues);
		if ( toBeScheduled ) {
			world.schedule();
		}
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
	// public SimulationAgent[] toArray() {
	// return (SimulationAgent[]) super.toArray();
	// }

	@Override
	public boolean step(final IScope scope) throws GamaRuntimeException {

		for ( IAgent a : toArray() ) {
			((SimulationAgent) a)._step_(scope);
		}

		return true;
	}

	//
	// A trial on multi-threaded simulations
	// @Override
	// public boolean step(final IScope scope) throws GamaRuntimeException {
	//
	// final IAgent[] agents = toArray();
	// final Semaphore semaphore = new Semaphore(0);
	// final Thread[] threads = new Thread[agents.length];
	// for ( int i = 0; i < agents.length; i++ ) {
	// final IAgent a = agents[i];
	// threads[i] = new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	//
	// a.getScope().step(a);
	// semaphore.release();
	// }
	// });
	// }
	// for ( Thread t : threads ) {
	// t.start();
	// }
	// try {
	// semaphore.acquire(agents.length);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// return true;
	// }

}