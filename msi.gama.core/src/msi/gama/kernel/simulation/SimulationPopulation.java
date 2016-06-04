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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.AbstractTopology.RootTopology;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.variables.IVariable;

public class SimulationPopulation extends GamaPopulation {

	private SimulationAgent currentSimulation;

	final ThreadFactory factory = new ThreadFactoryBuilder().setThreadFactory(Executors.defaultThreadFactory())
			.setNameFormat("Simulation thread #%d of experiment " + getSpecies().getName()).build();
	ExecutorService executor;
	Map<SimulationAgent, Callable> runnables = new LinkedHashMap();
	private int activeThreads;

	public SimulationPopulation(final ExperimentAgent agent, final ISpecies species) {
		super(agent, species);

	}

	protected ExecutorService getExecutorService() {
		if (executor == null) {
			final boolean isMultiThreaded = getHost().getSpecies().isMulticore();
			final int numberOfThreads = GamaPreferences.NUMBERS_OF_THREADS.getValue();
			executor = isMultiThreaded ? new ThreadPoolExecutor(1, numberOfThreads, 100L, TimeUnit.MILLISECONDS,
					new SynchronousQueue<Runnable>()) : MoreExecutors.sameThreadExecutor();
			if (executor instanceof ThreadPoolExecutor) {
				final ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
				tpe.setRejectedExecutionHandler(new CallerRunsPolicy());
				tpe.allowCoreThreadTimeOut(true);
			}
		}
		return executor;
	}

	public int getMaxNumberOfConcurrentSimulations() {
		final boolean isMultiThreaded = getHost().getSpecies().isMulticore();
		return isMultiThreaded ? GamaPreferences.NUMBERS_OF_THREADS.getValue() : 1;
	}

	/**
	 * Method fireAgentRemoved()
	 * 
	 * @see msi.gama.metamodel.population.GamaPopulation#fireAgentRemoved(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	protected void fireAgentRemoved(final IAgent agent) {
		super.fireAgentRemoved(agent);
		runnables.remove(agent);
	}

	@Override
	public void initializeFor(final IScope scope) {
		super.initializeFor(scope);
		this.currentAgentIndex = 0;
	}

	@Override
	public void dispose() {
		if (executor != null) {
			executor.shutdown();
			try {
				executor.awaitTermination(1, TimeUnit.SECONDS);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			executor = null;
		}
		currentSimulation = null;
		super.dispose();
	}

	@Override
	public Iterable<SimulationAgent> iterable(final IScope scope) {
		return (Iterable<SimulationAgent>) getAgents(scope);
	}

	@Override
	public IList<? extends IAgent> createAgents(final IScope scope, final int number,
			final List<? extends Map> initialValues, final boolean isRestored, final boolean toBeScheduled)
			throws GamaRuntimeException {
		for (int i = 0; i < number; i++) {
			scope.getGui().waitStatus("Initializing simulation");
			currentSimulation = new SimulationAgent(this);
			currentSimulation.setIndex(currentAgentIndex++);
			currentSimulation.setScheduled(toBeScheduled);
			currentSimulation.setName("Simulation " + currentSimulation.getIndex());
			add(currentSimulation);
			currentSimulation.setOutputs(((ExperimentPlan) host.getSpecies()).getOriginalSimulationOutputs());
			if (scope.interrupted()) {
				return null;
			}
			initSimulation(scope, currentSimulation, initialValues, isRestored, toBeScheduled);
			if (toBeScheduled) {

				// Necessary to put it in a final variable here, so that the
				// runnable does not point on the instance variable (see #1836)
				final SimulationAgent simulation = currentSimulation;
				runnables.put(currentSimulation, new Callable<Object>() {

					@Override
					public Object call() {
						return simulation.step(simulation.getScope());

					}
				});
			}
		}
		return this;
	}

	private void initSimulation(final IScope scope, final SimulationAgent sim, final List<? extends Map> initialValues,
			final boolean isRestored, final boolean toBeScheduled) {
		scope.getGui().waitStatus("Instantiating agents");
		createVariablesFor(sim.getScope(), Collections.singletonList(sim), initialValues);
		if (toBeScheduled) {
			if (isRestored) {
				sim.prepareGuiForSimulation(scope);
				sim.initOutputs();
			} else {
				sim.schedule(scope);
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
	public IAgent getAgent(final IScope scope, final ILocation value) {
		return get(null, 0);
	}

	public void setHost(final ExperimentAgent agent) {
		host = agent;
	}

	public void setTopology(final IScope scope, final IShape shape) {
		final IExpression expr = species.getFacet(IKeyword.TORUS);
		final boolean torus = expr == null ? false : Cast.as(expr.value(scope), Boolean.class, false);
		topology = new RootTopology(scope, shape, torus);
	}

	@Override
	public void computeTopology(final IScope scope) throws GamaRuntimeException {
		// Temporary topology set before the world gets a shape
		topology = new AmorphousTopology();
	}

	@Override
	public boolean step(final IScope scope) throws GamaRuntimeException {
		try {
			getExecutorService().invokeAll(new ArrayList(runnables.values()));
			if (getExecutorService() instanceof ThreadPoolExecutor) {
				final ThreadPoolExecutor e = (ThreadPoolExecutor) executor;
				activeThreads = e.getPoolSize();
			} else {
				activeThreads = 1;
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * This method can be called by the batch experiments to temporarily stop
	 * (unschedule) a simulation
	 * 
	 * @param sim
	 */
	public void unscheduleSimulation(final SimulationAgent sim) {
		runnables.remove(sim);
	}

	public int getNumberOfActiveThreads() {
		return activeThreads;
	}

	/**
	 * @return
	 */
	public boolean hasScheduledSimulations() {
		return runnables.size() > 0;
	}

	public SimulationAgent lastSimulationCreated() {
		return currentSimulation;
	}

}