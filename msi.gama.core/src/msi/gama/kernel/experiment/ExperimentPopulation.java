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
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;

public class ExperimentPopulation extends GamaPopulation {

	public ExperimentPopulation(final ISpecies expr) {
		super(null, expr);
	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final int number,
		final List<? extends Map> initialValues, final boolean isRestored, final boolean toBeScheduled) throws GamaRuntimeException {
		for ( int i = 0; i < number; i++ ) {
			boolean isBatch = ((ExperimentPlan) getSpecies()).isBatch();
			final ExperimentAgent exp = isBatch ? new BatchAgent(this) : new ExperimentAgent(this);
			exp.setIndex(currentAgentIndex++);
			/* agents. */add(exp);
			createVariables(scope, exp, initialValues.isEmpty() ? Collections.EMPTY_MAP : initialValues.get(i));
		}
		return /* agents */this;
	}

	public void createVariables(final IScope scope, final IAgent a, final Map<String, Object> inits)
		throws GamaRuntimeException {
		// IAgent a = get(0);
		Set<String> names = inits.keySet();
		try {
			a.acquireLock();
			for ( final String s : orderedVarNames ) {
				final IVariable var = species.getVar(s);
				var.initializeWith(scope, a, inits.get(s));
				names.remove(s);
			}
			for ( final String s : names ) {
				a.getScope().setAgentVarValue(a, s, inits.get(s));
			}
		} finally {
			a.releaseLock();
		}

	}

	@Override
	public IList<IAgent> computeAgentsToSchedule(final IScope scope) {
		return GamaListFactory.create(scope, Types.AGENT, /* agents. */Arrays.asList(get(0)));
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