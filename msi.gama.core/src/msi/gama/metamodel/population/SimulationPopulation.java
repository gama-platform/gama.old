package msi.gama.metamodel.population;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.AbstractTopology.RootTopology;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;

public class SimulationPopulation extends GamaPopulation {

	public SimulationPopulation(final ISpecies species) {
		super(null, species);
	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final int number, final List<Map> initialValues,
		final boolean isRestored) throws GamaRuntimeException {
		if ( size() == 0 ) {
			SimulationAgent world = new SimulationAgent(this);
			IAgent a = getHost();
			if ( a instanceof ExperimentAgent ) {
				((ExperimentAgent) a).setSimulation(world);
			}

			world.setIndex(0);
			agents.add(world);
			createVariablesFor(world.getScope(), agents, initialValues);
			if ( a instanceof ExperimentAgent ) {
				((ExperimentAgent) a).scheduleSimulation(world);
			}
		}
		return agents;
	}

	@Override
	public IAgent getAgent(final ILocation value) {
		return get(null, 0);
	}

	public void setHost(final ExperimentAgent agent) {
		host = agent;
	}

	public void setTopology(IScope scope, IShape gisShape, IShape shape) {
		boolean torus = Cast.asBool(scope, species.getFacet(IKeyword.TORUS));
		topology = new RootTopology(scope, gisShape, shape, torus);
	}

	@Override
	public void computeTopology(final IScope scope) throws GamaRuntimeException {
		// Temporary topology set before the world gets a shape
		topology = new AmorphousTopology();
	}

	@Override
	public List<IAgent> computeAgentsToSchedule(IScope scope) {
		return Collections.EMPTY_LIST;
	}

}