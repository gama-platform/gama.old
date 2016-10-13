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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.compilation.kernel.GamaMetaModel;
import msi.gaml.species.ISpecies;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;

public class ExperimentPopulation extends GamaPopulation<ExperimentAgent> {

	public ExperimentPopulation(final ISpecies expr) {
		super(null, expr);
	}

	@Override
	public IList<ExperimentAgent> createAgents(final IScope scope, final int number,
			final List<? extends Map<String, Object>> initialValues, final boolean isRestored,
			final boolean toBeScheduled) throws GamaRuntimeException {
		for (int i = 0; i < number; i++) {
			final ExperimentAgent exp = GamaMetaModel.INSTANCE
					.createExperimentAgent(((ExperimentPlan) getSpecies()).getExperimentType(), this);
			exp.setIndex(currentAgentIndex++);
			add(exp);
			scope.push(exp);
			createVariables(scope, exp, initialValues.isEmpty() ? Collections.EMPTY_MAP : initialValues.get(i));
		}
		return this;
	}

	public void createVariables(final IScope scope, final IAgent a, final Map<String, Object> inits)
			throws GamaRuntimeException {
		final Set<String> names = inits.keySet();
		try {
			for (final String s : orderedVarNames) {
				final IVariable var = species.getVar(s);
				var.initializeWith(scope, a, inits.get(s));
				names.remove(s);
			}
			for (final String s : names) {
				a.getScope().setAgentVarValue(a, s, inits.get(s));
			}
		} finally {
		}

	}

	@Override
	public IList<ExperimentAgent> computeAgentsToSchedule(final IScope scope) {
		return GamaListFactory.create(scope, Types.AGENT, /* agents. */Arrays.asList(get(0)));
	}

	@Override
	public ExperimentAgent getAgent(final IScope scope, final ILocation value) {
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