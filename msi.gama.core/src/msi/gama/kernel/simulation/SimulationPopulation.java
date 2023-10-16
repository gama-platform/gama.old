/*******************************************************************************************************
 *
 * SimulationPopulation.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.simulation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IScopedStepable;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.runtime.GAMA;
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

/**
 * The Class SimulationPopulation.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SimulationPopulation extends GamaPopulation<SimulationAgent> {

	/** The current simulation. */
	private SimulationAgent currentSimulation;

	/** The runner. */
	private final SimulationRunner runner;

	/**
	 * Instantiates a new simulation population.
	 *
	 * @param agent
	 *            the agent
	 * @param species
	 *            the species
	 */
	public SimulationPopulation(final ExperimentAgent agent, final ISpecies species) {
		super(agent, species);
		runner = SimulationRunner.of(this);
	}

	/**
	 * Gets the max number of concurrent simulations.
	 *
	 * @return the max number of concurrent simulations
	 */
	public int getMaxNumberOfConcurrentSimulations() {
		return GamaExecutorService.getParallelism(getHost().getScope(), getHost().getSpecies().getConcurrency(),
				Caller.SIMULATION);
	}

	@Override
	public void removeValue(final IScope scope, final Object value) {
		if (value instanceof SimulationAgent sim) {
			int index = indexOf(sim);
			if (index == -1) return;
			setCurrentSimulation(nextSimulationAfter(index));
			super.removeValue(scope, value);
		}
	}

	/**
	 * Next simulation after.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param index
	 *            the index
	 * @return the simulation agent
	 * @date 26 août 2023
	 */
	private SimulationAgent nextSimulationAfter(final int index) {
		if (size() <= 1 || index == -1) return null;
		return get((index + 1) % size());
	}

	/**
	 * Method fireAgentRemoved()
	 *
	 */
	@Override
	protected void fireAgentRemoved(final IScope scope, final IAgent old) {
		super.fireAgentRemoved(scope, old);
		runner.remove((SimulationAgent) old);
	}

	@Override
	public void initializeFor(final IScope scope) {
		super.initializeFor(scope);
		this.currentAgentIndex = 0;
	}

	@Override
	public void dispose() {
		runner.dispose();
		// currentSimulation = null;
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
			scope.getGui().getStatus().waitStatus(scope, "Initializing simulation");
			// Model do not only rely on SimulationAgent
			final IAgentConstructor<SimulationAgent> constr = species.getDescription().getAgentConstructor();
			// currentSimulation = currentAgentIndex++;
			SimulationAgent sim = constr.createOneAgent(this, currentAgentIndex++);
			sim.setScheduled(toBeScheduled);
			sim.setName("Simulation " + sim.getIndex());
			add(sim);
			// Batch experiments now dont allow their simulations to have outputs
			if (!getHost().getSpecies().isBatch()) {
				sim.setOutputs(((ExperimentPlan) host.getSpecies()).getOriginalSimulationOutputs());
			}
			if (scope.interrupted()) return null;
			// Necessary to set it early -- see Issue #3872
			setCurrentSimulation(sim);
			initSimulation(scope, sim, initialValues, i, isRestored, toBeScheduled, sequence);
			if (toBeScheduled) { runner.add(sim); }
			result.add(sim);
		}
		// Linked to Issue #2430. Should not return this, but the newly created simulations
		// return this;
		return result;
	}

	/**
	 * Inits the simulation.
	 *
	 * @param scope
	 *            the scope
	 * @param sim
	 *            the sim
	 * @param initialValues
	 *            the initial values
	 * @param index
	 *            the index
	 * @param isRestored
	 *            the is restored
	 * @param toBeScheduled
	 *            the to be scheduled
	 * @param sequence
	 *            the sequence
	 */
	private void initSimulation(final IScope scope, final SimulationAgent sim,
			final List<? extends Map<String, Object>> initialValues, final int index, final boolean isRestored,
			final boolean toBeScheduled, final RemoteSequence sequence) {
		scope.getGui().getStatus().waitStatus(scope, "Instantiating agents");
		// if (toBeScheduled) { sim.prepareGuiForSimulation(scope); }

		final Map<String, Object> firstInitValues = initialValues.isEmpty() ? null : initialValues.get(index);
		final Object firstValue =
				firstInitValues != null && !firstInitValues.isEmpty() ? firstInitValues.values().toArray()[0] : null;
		if (firstValue instanceof SavedAgent sa) {
			sim.updateWith(scope, sa);
		} else {
			sim.setExternalInits(firstInitValues);
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
	protected boolean allowVarInitToBeOverridenByExternalInit(final IVariable theVar) {
		return switch (theVar.getName()) {
			case IKeyword.SEED, IKeyword.RNG -> !theVar.hasFacet(IKeyword.INIT);
			default -> true;
		};
	}

	@Override
	public ExperimentAgent getHost() { return (ExperimentAgent) super.getHost(); }

	@Override
	public SimulationAgent getAgent(final IScope scope, final GamaPoint value) {
		return get(null, 0);
	}

	/**
	 * Sets the host.
	 *
	 * @param agent
	 *            the new host
	 */
	public void setHost(final ExperimentAgent agent) { host = agent; }

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

	/**
	 * Gets the number of active stepables.
	 *
	 * @return the number of active stepables
	 */
	public Set<IScopedStepable> getActiveStepables() { return runner.getStepable(); }

	/**
	 * Gets the number of active threads.
	 *
	 * @return the number of active threads
	 */
	public int getNumberOfActiveThreads() { return runner.getActiveThreads(); }

	/**
	 * @return
	 */
	public boolean hasScheduledSimulations() {
		return runner.hasSimulations();
	}

	/**
	 * Last simulation created.
	 *
	 * @return the simulation agent
	 */
	public SimulationAgent getCurrentSimulation() { return currentSimulation; }

	/**
	 * Sets the current simulation.
	 *
	 * @param current
	 *            the new current simulation
	 */
	public void setCurrentSimulation(final SimulationAgent current) {
		currentSimulation = current;
		GAMA.changeCurrentTopLevelAgent(current == null ? getHost() : current, false);
	}

}