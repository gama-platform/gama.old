/*******************************************************************************************************
 *
 * msi.gama.kernel.simulation.SimulationPopulation.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.simulation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.concurrent.GamaExecutorService.Caller;
import msi.gama.runtime.concurrent.SimulationRunner;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.compilation.IAgentConstructor;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.RemoteSequence;
import msi.gaml.variables.IVariable;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SimulationPopulation extends GamaPopulation<SimulationAgent> {

	private SimulationAgent currentSimulation;
	private final SimulationRunner runner;

	public SimulationPopulation(final ExperimentAgent agent, final ISpecies species) {
		super(agent, species);
		runner = new SimulationRunner(this);
	}

	public int getMaxNumberOfConcurrentSimulations() {
		if (getHost().getSpecies().isHeadless()) return 1;
		return GamaExecutorService.getParallelism(getHost().getScope(), getSpecies().getConcurrency(),
				Caller.SIMULATION);
	}

	/**
	 * Method fireAgentRemoved()
	 *
	 * @see msi.gama.metamodel.population.GamaPopulation#fireAgentRemoved(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	protected void fireAgentRemoved(final IScope scope, final IAgent agent) {
		super.fireAgentRemoved(scope, agent);
		runner.remove((SimulationAgent) agent);
	}

	@Override
	public void initializeFor(final IScope scope) {
		super.initializeFor(scope);
		this.currentAgentIndex = 0;
	}

	@Override
	public void dispose() {
		runner.dispose();
		currentSimulation = null;
		super.dispose();
	}

	@Override
	public Iterable<SimulationAgent> iterable(final IScope scope) {
		return (Iterable<SimulationAgent>) getAgents(scope);
	}

	@Override
	public IList<SimulationAgent> createAgents(final IScope scope, final int number,
			final List<? extends Map<String, Object>> initialValues, final boolean isRestored,
			final boolean toBeScheduled, final RemoteSequence sequence) throws GamaRuntimeException {
		final IList<SimulationAgent> result = GamaListFactory.create(SimulationAgent.class);

		for (int i = 0; i < number; i++) {
			scope.getGui().getStatus(scope).waitStatus("Initializing simulation");
			// Model do not only rely on SimulationAgent
			final IAgentConstructor<SimulationAgent> constr = species.getDescription().getAgentConstructor();

			currentSimulation = constr.createOneAgent(this, currentAgentIndex++);
			currentSimulation.setScheduled(toBeScheduled);
			currentSimulation.setName("Simulation " + currentSimulation.getIndex());
			add(currentSimulation);
			currentSimulation.setOutputs(((ExperimentPlan) host.getSpecies()).getOriginalSimulationOutputs());
			if (scope.interrupted()) return null;
			initSimulation(scope, currentSimulation, initialValues, i, isRestored, toBeScheduled, sequence);
			if (toBeScheduled) { runner.add(currentSimulation); }
			result.add(currentSimulation);
		}
		// Linked to Issue #2430. Should not return this, but the newly created simulations
		// return this;
		return result;
	}

	private void initSimulation(final IScope scope, final SimulationAgent sim,
			final List<? extends Map<String, Object>> initialValues, final int index, final boolean isRestored,
			final boolean toBeScheduled, final RemoteSequence sequence) {
		scope.getGui().getStatus(scope).waitStatus("Instantiating agents");
		if (toBeScheduled) { sim.prepareGuiForSimulation(scope); }

		final Map<String, Object> firstInitValues = initialValues.isEmpty() ? null : initialValues.get(index);
		final Object firstValue =
				firstInitValues != null && !firstInitValues.isEmpty() ? firstInitValues.values().toArray()[0] : null;
		if (firstValue instanceof SavedAgent) {
			sim.updateWith(scope, (SavedAgent) firstValue);
		} else {
			createVariablesFor(sim.getScope(), Collections.singletonList(sim), Arrays.asList(firstInitValues));
		}

		if (toBeScheduled) {
			if (isRestored || firstValue instanceof SavedAgent) {
				sim.initOutputs();
			} else {
				sim.schedule(scope);
				if (sequence != null && !sequence.isEmpty()) { scope.execute(sequence, sim, null); }
			}
		}
	}

	@Override
	protected boolean allowVarInitToBeOverridenByExternalInit(final IVariable var) {
		switch (var.getName()) {
			case IKeyword.SEED:
			case IKeyword.RNG:
				return !var.hasFacet(IKeyword.INIT);
			default:
				return true;
		}
	}

	@Override
	public ExperimentAgent getHost() {
		return (ExperimentAgent) super.getHost();
	}

	@Override
	public SimulationAgent getAgent(final IScope scope, final ILocation value) {
		return get(null, 0);
	}

	public void setHost(final ExperimentAgent agent) {
		host = agent;
	}

	@Override
	public void computeTopology(final IScope scope) throws GamaRuntimeException {
		// Temporary topology set before the world gets a shape
		topology = new AmorphousTopology();
	}

	@Override
	protected boolean stepAgents(final IScope scope) {
		runner.step();
		return true;
	}

	/**
	 * This method can be called by the batch experiments to temporarily stop (unschedule) a simulation
	 *
	 * @param sim
	 */
	public void unscheduleSimulation(final SimulationAgent sim) {
		runner.remove(sim);
	}

	public int getNumberOfActiveThreads() {
		return runner.getActiveThreads();
	}

	/**
	 * @return
	 */
	public boolean hasScheduledSimulations() {
		return runner.hasSimulations();
	}

	public SimulationAgent lastSimulationCreated() {
		return currentSimulation;
	}

}