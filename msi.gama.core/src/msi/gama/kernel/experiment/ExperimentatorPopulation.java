package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.species.ISpecies;

public class ExperimentatorPopulation extends GamaPopulation {

	/**
	 * @param expr
	 */
	public ExperimentatorPopulation(final ISpecies expr) {
		super(null, expr);
	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final int number,
		final List<Map> initialValues, final boolean isRestored) throws GamaRuntimeException {
		if ( size() == 0 ) {
			ExperimentatorAgent exp = new ExperimentatorAgent(this);
			exp.setIndex(0);
			agents.add(exp);
			createVariablesFor(scope, agents, initialValues);
		}
		return agents;

	}

	@Override
	public IAgent getAgent(final ILocation value) {
		return get(null, 0);
	}

	@Override
	public IAgent getHost() {
		return null;
	}

	@Override
	public void computeTopology(final IScope scope) throws GamaRuntimeException {
		topology = new AmorphousTopology();
	}

}