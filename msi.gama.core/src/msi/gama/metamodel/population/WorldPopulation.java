package msi.gama.metamodel.population;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentatorAgent;
import msi.gama.metamodel.agent.*;
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
	public IList<? extends IAgent> createAgents(final IScope scope, final int number,
		final List<Map> initialValues, final boolean isRestored) throws GamaRuntimeException {
		if ( size() == 0 ) {
			WorldAgent world = new WorldAgent(this);
			world.setTorus(Cast.asBool(scope, species.getFacet(IKeyword.TORUS)));
			world.setIndex(0);
			agents.add(world);
			createVariablesFor(scope, agents, initialValues);
			// March 2013: topology should be initialized by the world agent itself (see
			// setTopology(IScope, IShape, boolean))
			// topology = new ContinuousTopology(scope, world.getGeometry(), false);
		}
		return agents;
	}

	@Override
	public IAgent getAgent(final ILocation value) {
		return get(null, 0);
	}

	public void setHost(final ExperimentatorAgent agent) {
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