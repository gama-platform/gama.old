/*********************************************************************************************
 * 
 *
 * 'ExperimentPopulation.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.species.ISpecies;

public class ExperimentPopulation extends GamaPopulation {

	public ExperimentPopulation(final ISpecies expr) {
		super(null, expr);
	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final int number, final List<Map> initialValues,
		final boolean isRestored) throws GamaRuntimeException {
		if ( size() == 0 ) {
			boolean isBatch = ((ExperimentPlan) getSpecies()).isBatch();
			final ExperimentAgent exp = isBatch ? new BatchAgent(this) : new ExperimentAgent(this);
			// exp.setIndex(0);
			/* agents. */add(exp);
			createVariablesFor(scope, this /* agents */, initialValues);
		}
		return /* agents */this;
	}

	@Override
	public IList<IAgent> computeAgentsToSchedule(final IScope scope) {
		return GamaList.with(/* agents. */get(0));
	}

	@Override
	public IAgent getAgent(final IScope scope, final ILocation value) {
		return get(null, 0);
	}

	@Override
	public IMacroAgent getHost() {
		return null;
	}

	@Override
	public void computeTopology(final IScope scope) throws GamaRuntimeException {
		topology = new AmorphousTopology();
	}

}