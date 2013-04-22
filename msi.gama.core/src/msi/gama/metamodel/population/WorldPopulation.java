package msi.gama.metamodel.population;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.continuous.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;

public class WorldPopulation extends GamaPopulation {

	public WorldPopulation(final ISpecies species) {
		super(null, species);
	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final int number, final List<Map> initialValues,
		final boolean isRestored) throws GamaRuntimeException {
		if ( size() == 0 ) {
			GamlSimulation world = new GamlSimulation(this);
			finishInitializeWorld(scope, world, initialValues);
			// March 2013: topology should be initialized by the
			// simulation agent itself (see
			// setTopology(IScope, IShape, boolean))
			// topology = new ContinuousTopology(scope, world.getGeometry(), false);
		}
		return agents;
	}

	public void finishInitializeWorld(IScope scope, ISimulationAgent world, List<Map> initialValues) {
		world.setTorus(Cast.asBool(scope, species.getFacet(IKeyword.TORUS)));
		world.setIndex(0);
		agents.add(world);
		createVariablesFor(scope, agents, initialValues);
	}

	@Override
	public IAgent getAgent(final ILocation value) {
		return get(null, 0);
	}

	public void setHost(final ExperimentAgent agent) {
		host = agent;
	}

	public void setTopology(IScope scope, IShape shape, boolean torus) {
		topology = new ContinuousTopology(scope, shape, torus);
	}

	@Override
	public void computeTopology(final IScope scope) throws GamaRuntimeException {
		// Temporary topology set before the world gets a shape
		topology = new AmorphousTopology();
	}

}