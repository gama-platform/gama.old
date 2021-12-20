/*******************************************************************************************************
 *
 * BatchAgent.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import static msi.gaml.operators.Cast.asFloat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jfree.data.statistics.Statistics;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.batch.IExploration;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.FileOutput;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 28 mai 2011
 *
 * @todo Description
 *
 */

@experiment (IKeyword.BATCH)
@doc ("Experiments supporting the execution of several simulations in order to explore parameters or reach a specific state")
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class BatchAgent extends ExperimentAgent {

	/** The stop condition. */
	final IExpression stopCondition;

	/** The run number. */
	private int runNumber;

	/** The current solution. */
	ParametersSet currentSolution;

	/** The last solution. */
	ParametersSet lastSolution;

	/** The last fitness. */
	Double lastFitness;

	/** The seeds. */
	private Double[] seeds;

	/** The fitness values. */
	final List<Double> fitnessValues = new ArrayList<>();

	/**
	 * Instantiates a new batch agent.
	 *
	 * @param p
	 *            the p
	 * @param index
	 *            the index
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public BatchAgent(final IPopulation p, final int index) throws GamaRuntimeException {
		super(p, index);
		final IScope scope = getSpecies().getExperimentScope();
		final IExpression expr = getSpecies().getFacet(IKeyword.REPEAT);
		int innerLoopRepeat = 1;
		if (expr != null && expr.isConst()) { innerLoopRepeat = Cast.asInt(scope, expr.value(scope)); }
		setSeeds(new Double[innerLoopRepeat]);

		if (getSpecies().hasFacet(IKeyword.UNTIL)) {
			stopCondition = getSpecies().getFacet(IKeyword.UNTIL);
		} else {
			stopCondition = defaultStopCondition();
		}

	}

	/**
	 * Default stop condition.
	 *
	 * @return the i expression
	 */
	protected IExpression defaultStopCondition() {
		return IExpressionFactory.FALSE_EXPR;
	}

	@Override
	public void schedule(final IScope scope) {
		super.schedule(scope);
		// Necessary to run it here, as if the seed has been fixed in the
		// experiment, it is now defined and initialized
		if (getSpecies().keepsSeed()) {
			for (int i = 0; i < seeds.length; i++) {
				getSeeds()[i] = getScope().getRandom().between(0d, Long.MAX_VALUE);
			}
		}

	}

	@Override
	public Object _init_(final IScope scope) {
		getSpecies().getExplorationAlgorithm().initializeFor(scope, this);
		// Fix for issue #2088
		// We call super _init_ here, but the result of automaticallyCreateFirstSimulation() will prevent from creating
		// a first simulation (which we dont want as it should be the task of the exploration algorithm)
		super._init_(scope);
		return this;
	}

	@Override
	protected boolean automaticallyCreateFirstSimulation() {
		return false;
	}

	@SuppressWarnings ("null")
	@Override
	public void reset() {
		// We first save the results of the various simulations
		final SimulationPopulation pop = getSimulationPopulation();
		final boolean hasSimulations = pop != null && !pop.isEmpty();
		try {
			if (hasSimulations) {
				for (final IAgent sim : pop.toArray()) { memorizeFitnessAndCloseSimulation(sim, true); }
				pop.clear();
			}

		} catch (final GamaRuntimeException e) {
			e.addContext("in saving the results of the batch");
			GAMA.reportError(getScope(), e, true);
		}
		// We save the clock value first (to address Issue #1592)
		final int cycle = ownClock.getCycle();
		final long totalDuration = ownClock.getTotalDuration();
		final long lastDuration = ownClock.getDuration();

		super.reset();
		ownClock.setCycle(cycle);
		ownClock.setTotalDuration(totalDuration);
		ownClock.setLastDuration(lastDuration);
	}

	/**
	 * Memorize fitness and close simulation.
	 *
	 * @param sim
	 *            the sim
	 */
	public void memorizeFitnessAndCloseSimulation(final IAgent sim, final boolean dispose) {
		fitnessValues.add(computeFitnessAndCloseSimulation(sim, currentSolution, dispose));
	}

	/**
	 * Compute fitness and close simulation.
	 *
	 * @param sim
	 *            the sim
	 * @param sol
	 *            the sol
	 * @return the double
	 */
	private double computeFitnessAndCloseSimulation(final IAgent sim, final ParametersSet sol, final boolean dispose) {
		final IExpression fitness = getSpecies().getExplorationAlgorithm().getFitnessExpression();
		final FileOutput output = getSpecies().getLog();
		double lastFitnessValue = 0;
		if (fitness != null) { lastFitnessValue = Cast.asFloat(sim.getScope(), fitness.value(sim.getScope())); }
		if (output != null) { getSpecies().getLog().doRefreshWriteAndClose(sol, lastFitnessValue); }
		sim.dispose();
		return lastFitnessValue;
	}

	/**
	 *
	 * Method step()
	 *
	 * @see msi.gama.metamodel.agent.GamlAgent#step(msi.gama.runtime.IScope) This method, called once by the front
	 *      controller, actually serves as "launching" the batch process (entirely piloted by the exploration algorithm)
	 */
	@Override
	public boolean step(final IScope scope) {
		// We run the exloration algorithm. The future steps will be called by the exploration algorithm through the
		// launchSimulationsWithSolution() method
		getSpecies().getExplorationAlgorithm().run(scope);
		// Once the algorithm has finished exploring the solutions, the agent is
		// killed.
		scope.getGui().getStatus(scope).informStatus(endStatus());
		// Issue #2426: the agent is killed too soon
		getScope().setInterrupted();
		// dispose();
		GAMA.getGui().updateExperimentState(scope, IGui.FINISHED);
		return true;
	}

	/**
	 * End status.
	 *
	 * @return the string
	 */
	protected String endStatus() {
		return "Batch over. " + runNumber + " runs, " + runNumber * seeds.length + " simulations.";
	}

	/**
	 * Gets the run number.
	 *
	 * @return the run number
	 */
	public int getRunNumber() { return this.runNumber; }

	/**
	 * Creates the simulation.
	 *
	 * @param sim
	 *            the sim
	 * @param simToParameter
	 *            the sim to parameter
	 * @return the simulation agent
	 */
	private SimulationAgent createSimulation(final Map<String, Object> sim,
			final Map<IAgent, ParametersSet> simToParameter) {
		ParametersSet sol = (ParametersSet) sim.get("parameters");
		final SimulationAgent s = createSimulation(sol, true);
		s.setSeed((Double) sim.get("seed"));
		simToParameter.put(s, sol);
		return s;
	}

	/**
	 * Launch simulations with solution.
	 *
	 * @param sols
	 *            the sols
	 * @return the map
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public Map<ParametersSet, Double> launchSimulationsWithSolution(final List<ParametersSet> sols)
			throws GamaRuntimeException {
		// We first reset the currentSolution and the fitness values
		final SimulationPopulation pop = getSimulationPopulation();
		Map<ParametersSet, Double> fitnessRes = GamaMapFactory.create();
		if (pop == null) return fitnessRes;
		final List<Map<String, Object>> sims = new ArrayList<>();

		int numberOfCores = pop.getMaxNumberOfConcurrentSimulations();
		if (numberOfCores == 0) { numberOfCores = 1; }

		Map<ParametersSet, List<Double>> outputs = GamaMapFactory.create();

		// The values present in the solution are passed to the parameters of
		// the experiment
		for (ParametersSet sol : sols) {
			for (int i = 0; i < getSeeds().length; i++) {
				Map<String, Object> sim = new HashMap<>();
				sim.put("parameters", sol);
				sim.put("seed", getSeeds()[i]);
				sims.add(sim);
			}
		}

		int nb = Math.min(sims.size(), numberOfCores);

		List<Map<String, Object>> simsToRun = new Vector<>();

		for (int i = 0; i < nb; i++) { simsToRun.add(sims.remove(0)); }

		Map<IAgent, ParametersSet> simToParameter = GamaMapFactory.create();
		Iterator<Map<String, Object>> it = simsToRun.iterator();
		while (it.hasNext()) { createSimulation(it.next(), simToParameter); }

		while (pop.hasScheduledSimulations() && !dead) {
			// We step all the simulations
			pop.step(getScope());
			for (final IAgent sim : pop.toArray()) {
				final SimulationAgent agent = (SimulationAgent) sim;
				ParametersSet ps = simToParameter.get(agent);
				currentSolution = new ParametersSet(ps);

				// test the condition first in case it is paused
				final boolean stopConditionMet =
						dead || Cast.asBool(sim.getScope(), sim.getScope().evaluate(stopCondition, sim).getValue());
				final boolean mustStop = stopConditionMet || agent.dead() || agent.getScope().isPaused();
				if (mustStop) {
					pop.unscheduleSimulation(agent);
					// if (!getSpecies().keepsSimulations()) {

					double val = computeFitnessAndCloseSimulation(agent, ps, !getSpecies().keepsSimulations());

					if (!outputs.containsKey(ps)) { outputs.put(ps, new ArrayList<>()); }
					outputs.get(ps).add(val);

					if (!sims.isEmpty()) { createSimulation(sims.remove(0), simToParameter); }

				}
			}
			// We then verify that the front scheduler has not been paused
			while (getSpecies().getController().getScheduler().paused && !dead) {
				try {
					Thread.sleep(100);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// When the simulations are finished, we give a chance to the outputs of
		// the experiment and the experiment
		// agent itself to "step" once, effectively emulating what the front
		// scheduler should do. The simulations are
		// still "alive" at this stage (even if they are not scheduled anymore),
		// which allows to retrieve information from them
		super.step(getScope());

		// If the agent is dead, we return immediately
		if (dead) return fitnessRes;
		// We reset the experiment agent to erase traces of the current
		// simulations if any
		this.reset();

		// We then return the combination (average, min or max) of the different
		// fitness values computed by the
		// different simulation.
		final short fitnessCombination = getSpecies().getExplorationAlgorithm().getCombination();

		for (ParametersSet p : outputs.keySet()) {
			lastSolution = p;
			lastFitness = fitnessCombination == IExploration.C_MAX ? Collections.max(outputs.get(p))
					: fitnessCombination == IExploration.C_MIN ? Collections.min(outputs.get(p))
					: Statistics.calculateMean(outputs.get(p));
			fitnessRes.put(p, lastFitness);
			// we update the best solution found so far
			getSpecies().getExplorationAlgorithm().updateBestFitness(lastSolution, lastFitness);

		}

		// At last, we update the parameters (last fitness and best fitness)
		getScope().getGui().showParameterView(getScope(), getSpecies());
		return fitnessRes;

	}

	/**
	 * Launch simulations with solution.
	 *
	 * @param sol
	 *            the sol
	 * @return the double
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public Double launchSimulationsWithSolution(final ParametersSet sol) throws GamaRuntimeException {
		// We first reset the currentSolution and the fitness values
		final SimulationPopulation pop = getSimulationPopulation();
		if (pop == null) return 0d;
		currentSolution = new ParametersSet(sol);
		fitnessValues.clear();
		runNumber = runNumber + 1;
		// The values present in the solution are passed to the parameters of
		// the experiment
		for (final Map.Entry<String, Object> entry : sol.entrySet()) {
			final IParameter p = getSpecies().getExplorableParameters().get(entry.getKey());
			if (p != null) { p.setValue(getScope(), entry.getValue()); }
		}

		// We update the parameters (parameter to explore)
		getScope().getGui().showParameterView(getScope(), getSpecies());

		// We then create a number of simulations with the same solution

		int numberOfCores = pop.getMaxNumberOfConcurrentSimulations();
		if (numberOfCores == 0) { numberOfCores = 1; }
		int repeatIndex = 0;
		while (repeatIndex < getSeeds().length && !dead) {
			for (int coreIndex = 0; coreIndex < numberOfCores; coreIndex++) {
				setSeed(getSeeds()[repeatIndex]);
				createSimulation(currentSolution, true);
				repeatIndex++;
				if (repeatIndex == getSeeds().length || dead) { break; }
			}
			int i = 0;
			while (pop.hasScheduledSimulations() && !dead) {
				// We step all the simulations
				pop.step(getScope());
				// String cycles = "";
				// We evaluate their stopCondition and unschedule the ones who
				// return true
				for (final IAgent sim : pop.toArray()) {
					final SimulationAgent agent = (SimulationAgent) sim;
					// cycles += " " + simulation.getClock().getCycle();
					// test the condition first in case it is paused
					final boolean stopConditionMet =
							dead || Cast.asBool(sim.getScope(), sim.getScope().evaluate(stopCondition, sim).getValue());
					final boolean mustStop = stopConditionMet || agent.dead() || agent.getScope().isPaused();
					if (mustStop) {
						pop.unscheduleSimulation(agent);
						memorizeFitnessAndCloseSimulation(agent, !getSpecies().keepsSimulations());
					}
				}
				// We inform the status line
				if (!dead) {
					getScope().getGui().getStatus(getScope())
							.setStatus(
									"Run " + runNumber + " | " + repeatIndex + "/" + seeds.length
											+ " simulations (using " + pop.getNumberOfActiveThreads() + " threads)",
									"small.batch" + i / 5);
				}
				if (++i == 20) { i = 0; }
				// We then verify that the front scheduler has not been paused
				while (getSpecies().getController().getScheduler().paused && !dead) {
					try {
						Thread.sleep(100);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}

		// When the simulations are finished, we give a chance to the outputs of
		// the experiment and the experiment
		// agent itself to "step" once, effectively emulating what the front
		// scheduler should do. The simulations are
		// still "alive" at this stage (even if they are not scheduled anymore),
		// which allows to retrieve information from them
		super.step(getScope());

		// If the agent is dead, we return immediately
		if (dead) return 0.0;
		// We reset the experiment agent to erase traces of the current
		// simulations if any
		this.reset();

		// We then return the combination (average, min or max) of the different
		// fitness values computed by the
		// different simulation.
		final short fitnessCombination = getSpecies().getExplorationAlgorithm().getCombination();
		lastSolution = currentSolution;
		lastFitness = fitnessCombination == IExploration.C_MAX ? Collections.max(fitnessValues)
				: fitnessCombination == IExploration.C_MIN ? Collections.min(fitnessValues)
				: Statistics.calculateMean(fitnessValues);

		// we update the best solution found so far
		getSpecies().getExplorationAlgorithm().updateBestFitness(lastSolution, lastFitness);

		// At last, we update the parameters (last fitness and best fitness)
		getScope().getGui().showParameterView(getScope(), getSpecies());

		return lastFitness;

	}

	/**
	 * Gets the parameters to explore.
	 *
	 * @return the parameters to explore
	 */
	public List<IParameter.Batch> getParametersToExplore() {
		return new ArrayList(getSpecies().getExplorableParameters().values());
	}

	@Override
	public List<? extends IParameter.Batch> getDefaultParameters() {
		final List<IParameter.Batch> params = (List<Batch>) super.getDefaultParameters();
		for (final IVariable v : getModel().getVars()) {
			if (v.isParameter() && !getSpecies().getExplorableParameters().containsKey(v.getName())) {
				final ExperimentParameter p = new ExperimentParameter(getScope(), v);
				if (p.canBeExplored()) {
					p.setEditable(false);
					p.setCategory(IExperimentPlan.EXPLORABLE_CATEGORY_NAME);
					params.add(p);
				}
			}
		}

		addSpecificParameters(params);
		return params;
	}

	/**
	 * Adds the specific parameters.
	 *
	 * @param params
	 *            the params
	 */
	public void addSpecificParameters(final List<IParameter.Batch> params) {
		params.add(new ParameterAdapter("Stop condition", IExperimentPlan.BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public Object value() {
				return stopCondition != null ? stopCondition.serialize(false) : "none";
			}

		});

		params.add(new ParameterAdapter("Best parameter set found", IExperimentPlan.BATCH_CATEGORY_NAME, "",
				IType.STRING) {

			@Override
			public String value() {
				final IExploration algo = getSpecies().getExplorationAlgorithm();
				if (algo == null) return "";
				final ParametersSet solutions = algo.getBestSolution();
				if (solutions == null) return "";
				return solutions.toString();
			}

		});
		params.add(new ParameterAdapter("Best fitness", IExperimentPlan.BATCH_CATEGORY_NAME, "", IType.STRING) {

			@Override
			public String value() {
				final IExploration algo = getSpecies().getExplorationAlgorithm();
				if (algo == null) return "-";
				final Double best = algo.getBestFitness();
				if (best == null) return "-";
				return best.toString();
			}

		});

		params.add(new ParameterAdapter("Last parameeter set tested", IExperimentPlan.BATCH_CATEGORY_NAME, "",
				IType.STRING) {

			@Override
			public String value() {
				if (lastSolution == null) return "-";
				return lastSolution.toString();
			}

		});

		params.add(new ParameterAdapter("Last fitness", IExperimentPlan.BATCH_CATEGORY_NAME, "", IType.STRING) {

			@Override
			public String value() {
				if (lastFitness == null) return "-";
				return lastFitness.toString();
			}

		});

		params.add(new ParameterAdapter("Parameter space", IExperimentPlan.BATCH_CATEGORY_NAME, "", IType.STRING) {

			@Override
			public String value() {
				final Map<String, IParameter.Batch> explorable = getSpecies().getExplorableParameters();
				if (explorable.isEmpty()) return "1";
				String result = "";
				int dim = 1;
				for (final Map.Entry<String, IParameter.Batch> entry : explorable.entrySet()) {
					result += entry.getKey() + " (";
					final int entryDim = getExplorationDimension(entry.getValue());
					dim = dim * entryDim;
					result += String.valueOf(entryDim) + ") * ";
				}
				result = result.substring(0, result.length() - 2);
				result += " = " + dim;
				return result;
			}

			int getExplorationDimension(final IParameter.Batch p) {
				IScope scope = getScope();

				// AD TODO Issue a warning in the compilation if a batch experiment tries to explore non-int or
				// non-float values
				if (p.getAmongValue(scope) != null) return p.getAmongValue(scope).size();
				return (int) ((asFloat(scope, p.getMaxValue(scope)) - asFloat(scope, p.getMinValue(scope)))
						/ asFloat(scope, p.getStepValue(scope))) + 1;
			}

		});

		getSpecies().getExplorationAlgorithm().addParametersTo(params, this);
	}

	/**
	 * Gets the seeds.
	 *
	 * @return the seeds
	 */
	public Double[] getSeeds() { return seeds; }

	/**
	 * Sets the seeds.
	 *
	 * @param seeds
	 *            the new seeds
	 */
	public void setSeeds(final Double[] seeds) { this.seeds = seeds; }

	@Override
	public void closeSimulations() {
		// We interrupt the simulation scope directly (as it cannot be
		// interrupted by the global scheduler)
		if (getSimulation() != null) { getSimulation().getScope().setInterrupted(); }
	}

}
