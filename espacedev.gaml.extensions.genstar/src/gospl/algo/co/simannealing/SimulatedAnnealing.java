/*******************************************************************************************************
 *
 * SimulatedAnnealing.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.simannealing;

import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;
import core.util.random.GenstarRandom;
import gospl.GosplPopulation;
import gospl.algo.co.metamodel.AOptimizationAlgorithm;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.algo.co.metamodel.neighbor.PopulationEntityNeighborSearch;
import gospl.algo.co.metamodel.solution.ISyntheticPopulationSolution;
import gospl.sampler.IEntitySampler;

/**
 * The simulated annealing algorithm that fit combinatorial optimization framework from synthetic population field of
 * research. It is used by {@link IEntitySampler} to generate a synthetic population based on a data sample and data
 * aggregated constraints (also called marginals)
 * <p>
 * Basic principles of the algorithm are: we start with a synthetic population (may be randomly generated from a sample,
 * the sample itself or any user provided synthetic population) which represents the current state. Each step of the
 * algorithm provide a new population, slightly different from the current state, which represent a "neighbor state".
 * This new candidate state as a probability to be accepted that depend on its own energy (or fitness) and the
 * temperature of the global system. As algorithm iterates, it will be less incline to accept candidate state with lower
 * energy (worst fitness) and more and more rely on the best candidate he has visited.
 * <p>
 * Cooling schedule use logarithmic multiplicative process: T_n = T_init / (1 + alpha * log(1 + n)) with n the number of
 * temperature transition state, alpha the multiplicative cooling rate and T_init the initial temperature
 * <p>
 * The number of k neighbors visited for each temperature transition state to find a new state: start at 4, and each
 * time the k neighbors have been rejected k is multiplied by a factor of two:
 * <p>
 *
 * <pre>
 * {@code
 * State S_c = null
 * while(S_c == null){
 *  for(k neighbors){
 *   if(probaTransition(S_k))
 *    then S_current = S_k
 *  }
 *  if(S_c == null)
 *   then k *= 2
 *  }
 * }
 * </pre>
 *
 * @author kevinchapuis
 *
 */
public class SimulatedAnnealing extends AOptimizationAlgorithm<GosplPopulation> {

	/** The cool temp ratio. */
	private static final double coolTempRatio = 0.06;

	/** The init temp. */
	private int initTemp = 100000;

	/** The cooling rate. */
	private double coolingRate = 3;

	/** The transition length. */
	private static final int transitionLength = 4;

	/** The trans function. */
	private ISimulatedAnnealingTransitionFunction transFunction;

	/**
	 * Instantiates a new simulated annealing.
	 *
	 * @param neighborSearch
	 *            the neighbor search
	 * @param minStateEnergy
	 *            the min state energy
	 * @param initTemp
	 *            the init temp
	 * @param coolingRate
	 *            the cooling rate
	 * @param transFonction
	 *            the trans fonction
	 */
	public SimulatedAnnealing(final IPopulationNeighborSearch<GosplPopulation, ?> neighborSearch,
			final double minStateEnergy, final int initTemp, final double coolingRate,
			final ISimulatedAnnealingTransitionFunction transFonction) {
		super(neighborSearch, minStateEnergy);
		this.initTemp = initTemp;
		this.coolingRate = coolingRate;
	}

	/**
	 * Instantiates a new simulated annealing.
	 *
	 * @param minStateEnergy
	 *            the min state energy
	 * @param initTemp
	 *            the init temp
	 * @param coolingRate
	 *            the cooling rate
	 * @param transFonction
	 *            the trans fonction
	 */
	public SimulatedAnnealing(final double minStateEnergy, final int initTemp, final double coolingRate,
			final ISimulatedAnnealingTransitionFunction transFonction) {
		super(new PopulationEntityNeighborSearch(), minStateEnergy);
		this.initTemp = initTemp;
		this.coolingRate = coolingRate;
	}

	/**
	 * Instantiates a new simulated annealing.
	 */
	public SimulatedAnnealing() {
		super(new PopulationEntityNeighborSearch(), 0d);
		this.transFunction = new SimulatedAnnealingDefaultTransitionFunction();
	}

	@Override
	public ISyntheticPopulationSolution<GosplPopulation>
			run(final ISyntheticPopulationSolution<GosplPopulation> initialSolution) {

		ISyntheticPopulationSolution<GosplPopulation> currentState = initialSolution;
		ISyntheticPopulationSolution<GosplPopulation> bestState = initialSolution;
		this.getNeighborSearchAlgorithm().updatePredicates(initialSolution.getSolution());
		int nBuffer = (int) (super.getNeighborSearchAlgorithm().getPredicates().size() * super.getKNeighborRatio());

		GSPerformanceUtil gspu = new GSPerformanceUtil("Start Simulated annealing algorithm" + "\nPopulation size = "
				+ initialSolution.getSolution().size() + "\nSample size = " + super.getSample().size()
				+ "\nFreezing temperature = " + this.initTemp * coolTempRatio + "\nNeighbor search = "
				+ super.getNeighborSearchAlgorithm().getClass().getSimpleName() + "\nSolution = "
				+ initialSolution.getClass().getSimpleName(), Level.DEBUG);

		double currentEnergy = currentState.getFitness(this.getObjectives());
		double bestEnergy = currentEnergy;

		// Iterate while system temperature is above cool threshold
		// OR while system energy is above minimum state energy
		double temperature = initTemp;
		int stateTransition = 0;
		int localTransitionLength = SimulatedAnnealing.transitionLength;
		double forcedTransition = 1d;
		while (temperature > this.initTemp * coolTempRatio && currentEnergy > super.getFitnessThreshold()) {

			boolean tempTransition = false;

			for (int i = 0; i < localTransitionLength; i++) {
				ISyntheticPopulationSolution<GosplPopulation> systemStateCandidate =
						currentState.getRandomNeighbor(super.getNeighborSearchAlgorithm(), nBuffer);
				double candidateEnergy = systemStateCandidate.getFitness(this.getObjectives());

				// IF probability function elicit transition state
				// THEN change current state to be currentCandidate
				if (transFunction.getTransitionProbability(currentEnergy, candidateEnergy, temperature)) {
					if (stateTransition % 10 == 0) {
						gspu.sysoStempPerformance("Updats energy (TS = " + stateTransition + ") [" + currentEnergy
								+ " -> " + candidateEnergy + "] - " + "Temp = " + Math.round(temperature) + "° - "
								+ "Transition lenght = " + (i + 1), this);
					}
					currentState = systemStateCandidate;
					currentEnergy = candidateEnergy;
					tempTransition = true;
					break;
				}

				// Keep track of best state visited
				if (bestEnergy > currentEnergy) {
					bestState = currentState;
					bestEnergy = currentEnergy;
				}
			}

			double variable = 1 - forcedTransition;

			if (tempTransition || GenstarRandom.getInstance().nextDouble() < variable) {
				this.getNeighborSearchAlgorithm().updatePredicates(currentState.getSolution());
				temperature = this.initTemp / (1 + coolingRate * Math.log(1d + ++stateTransition));
				localTransitionLength = SimulatedAnnealing.transitionLength;
				forcedTransition = 1;
			} else {
				localTransitionLength *= 1.2;
				forcedTransition *= 0.8;
			}
		}
		gspu.sysoStempPerformance(
				"End simulated annealing with: " + "Temperature = " + temperature + " | Energy = " + bestEnergy, this);

		return bestState;
	}

}
