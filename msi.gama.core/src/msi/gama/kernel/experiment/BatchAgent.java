/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.batch.IExploration;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;
import org.jfree.data.statistics.Statistics;

/**
 * Written by drogoul Modified on 28 mai 2011
 * 
 * @todo Description
 * 
 */

public class BatchAgent extends ExperimentAgent {

	private final IExpression stopCondition;
	private int runNumber, repeatIndex;
	private ParametersSet currentSolution;
	private Double[] seeds;
	private final List<Double> fitnessValues = new ArrayList();

	public BatchAgent(final IPopulation p) throws GamaRuntimeException {
		super(p);
		IScope scope = getSpecies().getExperimentScope();
		IExpression expr = getSpecies().getFacet(IKeyword.REPEAT);
		int innerLoopRepeat = 1;
		if ( expr != null && expr.isConst() ) {
			innerLoopRepeat = Cast.asInt(scope, expr.value(scope));
		}
		setSeeds(new Double[innerLoopRepeat]);
		// expr = getSpecies().getFacet(IKeyword.KEEP_SEED);
		// if ( expr != null && expr.isConst() ) {
		// boolean keepSeed = Cast.asBool(scope, expr.value(scope));
		// if ( keepSeed ) {
		// for ( int i = 0; i < innerLoopRepeat; i++ ) {
		// getSeeds()[i] = GAMA.getRandom().between(0d, Long.MAX_VALUE);
		// }
		// }
		// }

		if ( getSpecies().hasFacet(IKeyword.UNTIL) ) {
			stopCondition = getSpecies().getFacet(IKeyword.UNTIL);
		} else {
			stopCondition = IExpressionFactory.FALSE_EXPR;
		}

	}

	@Override
	public void schedule() {
		super.schedule();
		// Necessary to run it here, as if the seed has been fixed in the experiment, it is now defined and initialized
		IExpression expr = getSpecies().getFacet(IKeyword.KEEP_SEED);
		if ( expr != null && expr.isConst() ) {
			boolean keepSeed = Cast.asBool(getScope(), expr.value(getScope()));
			if ( keepSeed ) {
				for ( int i = 0; i < seeds.length; i++ ) {
					getSeeds()[i] = GAMA.getRandom().between(0d, Long.MAX_VALUE);
				}
			}
		}

	}

	@Override
	public Object _init_(final IScope scope) {

		getSpecies().getExplorationAlgorithm().initializeFor(this);
		return this;
	}

	public ParametersSet getCurrentSolution() {
		return currentSolution;
	}

	@Override
	public void reset() {
		if ( getSimulation() != null ) {
			try { // while the simulation is still "alive"
				double lastFitnessValue = 0;
				IExpression fitness = getSpecies().getExplorationAlgorithm().getFitnessExpression();
				if ( fitness != null ) {
					lastFitnessValue = Cast.asFloat(getScope(), fitness.value(getScope()));
					fitnessValues.add(lastFitnessValue);
				}
				if ( getSpecies().getLog() != null ) {
					getSpecies().getLog().doRefreshWriteAndClose(currentSolution, lastFitnessValue);
				}
			} catch (GamaRuntimeException e) {
				e.addContext("in saving the results of the batch");
				GAMA.reportError(e, true);
			}
		}
		super.reset();
	}

	// void initRandom() {
	//
	// }

	/**
	 * 
	 * Method step()
	 * @see msi.gama.metamodel.agent.GamlAgent#step(msi.gama.runtime.IScope)
	 *      This method, called once by the front controller, actually serves as "launching" the batch process (entirely
	 *      piloted by the exploration algorithm)
	 */
	@Override
	public boolean step(final IScope scope) {
		// We run the exloration algorithm (but dont start() it, as the thread is not used)
		getSpecies().getExplorationAlgorithm().run();
		// Once the algorithm has finished exploring the solutions, the agent is killed.
		GuiUtils.informStatus("Batch over. " + runNumber + " runs, " + runNumber * seeds.length + " simulations.");
		dispose();
		return true;
	}

	public int getRunNumber()
	{
		return this.runNumber;
	}
	
	public Double launchSimulationsWithSolution(final ParametersSet sol) throws GamaRuntimeException {
		// We first reset the currentSolution and the fitness values
		currentSolution = new ParametersSet(sol);
		fitnessValues.clear();
		runNumber = runNumber + 1;
		// The values present in the solution are passed to the parameters of the experiment
		for ( Map.Entry<String, Object> entry : sol.entrySet() ) {
			IParameter p = getSpecies().getExplorableParameters().get(entry.getKey());
			if ( p != null ) {
				p.setValue(entry.getValue());
			}
		}
		// We then run a number of simulations with the same solution
		for ( repeatIndex = 0; repeatIndex < getSeeds().length; repeatIndex++ ) {
			setSeed(getSeeds()[repeatIndex]);
			createSimulation(currentSolution, false);
			GuiUtils.prepareForSimulation(simulation);
			IScope scope = simulation.getScope();
			simulation.getScheduler().insertAgentToInit(simulation, scope);
			// We manually init the scheduler of the simulation (so as to enable recursive inits for sub-agents)
			simulation.getScheduler().init(scope);

			// This inner while loop runs the simulation and controls its execution
			while (simulation != null && simulation.step(scope)) {
				boolean mustStop = Cast.asBool(scope, scope.evaluate(stopCondition, simulation));
				if ( mustStop ) {
					break;
				}
				GuiUtils.informStatus("Run " + runNumber + " | Simulation " + (repeatIndex + 1) + "/" +
					getSeeds().length + " | Cycle " + simulation.getClock().getCycle());
				// TODO This is where any update of the outputs of simulations should be introduced
			}
			// When a simulation is finished, we give a chance to the outputs of the experiment and the experiment
			// agent itself to "step" once, effectively emulating what the front scheduler should do. The simulation is
			// still "alive" at this stage, which allows to retrieve information from it
			super.step(getScope());
			getSpecies().getExperimentOutputs().step(getScope());
			// We then verify that the front scheduler has not been paused
			while (GAMA.controller.getScheduler().paused && !dead) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// If the agent is dead, we return immediately
			if ( dead ) { return 0.0; }
			// We reset the experiment agent to erase traces of the current simulation
			reset();
			// We update the parameters
			GuiUtils.showParameterView(getSpecies());
		}
		// We then return the combination (average, min or max) of the different fitness values computed by the
		// different simulation.
		short fitnessCombination = getSpecies().getExplorationAlgorithm().getCombination();
		return fitnessCombination == IExploration.C_MAX ? Collections.max(fitnessValues)
			: fitnessCombination == IExploration.C_MIN ? Collections.min(fitnessValues) : Statistics
				.calculateMean(fitnessValues);

	}

	public List<IParameter.Batch> getParametersToExplore() {
		List<IParameter.Batch> result = new ArrayList(getSpecies().getExplorableParameters().values());
		return result;
	}

	@Override
	public List<? extends IParameter.Batch> getDefaultParameters() {
		List<IParameter.Batch> params = (List<Batch>) super.getDefaultParameters();
		for ( IVariable v : getModel().getVars() ) {
			if ( v.isParameter() && !getSpecies().getExplorableParameters().containsKey(v.getName()) ) {
				ExperimentParameter p = new ExperimentParameter(getScope(), v);
				if ( p.canBeExplored() ) {
					p.setEditable(false);
					p.setCategory(IExperimentSpecies.EXPLORABLE_CATEGORY_NAME);
					params.add(p);
				}
			}
		}

		params.add(new ParameterAdapter("Stop condition", IExperimentSpecies.BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public Object value() {
				return stopCondition != null ? stopCondition.toGaml() : "none";
			}

		});

		params.add(new ParameterAdapter("Best fitness", IExperimentSpecies.BATCH_CATEGORY_NAME, "", IType.STRING) {

			@Override
			public String getUnitLabel() {
				IExploration algo = getSpecies().getExplorationAlgorithm();
				if ( algo == null ) { return ""; }
				ParametersSet params = algo.getBestSolution();
				if ( params == null ) { return ""; }
				return "with " + params;
			}

			@Override
			public String value() {
				IExploration algo = getSpecies().getExplorationAlgorithm();
				if ( algo == null ) { return "-"; }
				Double best = algo.getBestFitness();
				if ( best == null ) { return "-"; }
				return best.toString();
			}

		});

		params.add(new ParameterAdapter("Last fitness", IExperimentSpecies.BATCH_CATEGORY_NAME, "", IType.STRING) {

			@Override
			public String getUnitLabel() {
				if ( currentSolution == null ) { return ""; }
				return "with " + currentSolution.toString();
			}

			@Override
			public String value() {
				if ( fitnessValues.isEmpty() ) { return "-"; }
				return fitnessValues.get(fitnessValues.size() - 1).toString();
			}

		});

		params.add(new ParameterAdapter("Parameter space", IExperimentSpecies.BATCH_CATEGORY_NAME, "", IType.STRING) {

			@Override
			public String value() {
				Map<String, IParameter.Batch> params = getSpecies().getExplorableParameters();
				String result = "";
				int dim = 1;
				for ( Map.Entry<String, IParameter.Batch> entry : params.entrySet() ) {
					result += entry.getKey() + " (";
					int entryDim = getExplorationDimension(entry.getValue());
					dim = dim * entryDim;
					result += String.valueOf(entryDim) + ") * ";
				}
				result = result.substring(0, result.length() - 2);
				result += " = " + dim;
				return result;
			}

			int getExplorationDimension(final IParameter.Batch p) {
				if ( p.getAmongValue() != null ) { return p.getAmongValue().size(); }
				return (int) ((p.getMaxValue().doubleValue() - p.getMinValue().doubleValue()) / p.getStepValue()
					.doubleValue()) + 1;
			}

		});

		getSpecies().getExplorationAlgorithm().addParametersTo(params, this);
		return params;
	}

	public Double[] getSeeds() {
		return seeds;
	}

	public void setSeeds(final Double[] seeds) {
		this.seeds = seeds;
	}

	@Override
	public void closeSimulation() {
		// We interrupt the simulation scope directly (as it cannot be interrupted by the global scheduler)
		if ( getSimulation() != null ) {
			getSimulation().getScope().setInterrupted(true);
		}
	}

}
