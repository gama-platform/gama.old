/*******************************************************************************************************
 *
 * TabuSearch.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.tabusearch;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import core.util.GSPerformanceUtil;
import core.util.random.GenstarRandom;
import gospl.GosplPopulation;
import gospl.algo.co.metamodel.AOptimizationAlgorithm;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.algo.co.metamodel.neighbor.PopulationAttributeNeighborSearch;
import gospl.algo.co.metamodel.solution.ISyntheticPopulationSolution;

/**
 * Default implementation of the Tabu Search algorithm
 * <p>
 * 1) added k neighbor exploration algorithm @see {@link IPopulationNeighborSearch} <br/>
 * 2) also added mid-term memory process with a random jump when no improvement have been made for a number of iteration
 * equal to 10% of tabulist size
 *
 * @author Alex Ferreira
 * @author modified by kevinchapuis
 *
 */
public class TabuSearch extends AOptimizationAlgorithm<GosplPopulation> {

	/** The tabu list. */
	private final ITabuList tabuList;

	/** The max iterations. */
	private final int maxIterations;

	/**
	 * Construct a {@link TabuSearch} object
	 *
	 * @param tabuList
	 *            the tabu list used in the algorithm to handle tabus
	 * @param stopCondition
	 *            the algorithm stop condition
	 * @param solutionLocator
	 *            the best neightbor solution locator to be used in each algortithm iteration
	 */
	public TabuSearch(final ITabuList tabulist, final double fitnessThreshold, final int maxIterations) {
		this(new PopulationAttributeNeighborSearch(), tabulist, fitnessThreshold, maxIterations);
	}

	/**
	 * Instantiates a new tabu search.
	 *
	 * @param neighborSearch
	 *            the neighbor search
	 * @param tabuList
	 *            the tabu list
	 * @param fitnessThreshold
	 *            the fitness threshold
	 * @param maxIterations
	 *            the max iterations
	 */
	public TabuSearch(final IPopulationNeighborSearch<GosplPopulation, ?> neighborSearch, final ITabuList tabuList,
			final double fitnessThreshold, final int maxIterations) {
		super(neighborSearch, fitnessThreshold);
		this.tabuList = tabuList;
		this.maxIterations = maxIterations;
	}

	@Override
	public ISyntheticPopulationSolution<GosplPopulation>
			run(final ISyntheticPopulationSolution<GosplPopulation> initialSolution) {
		ISyntheticPopulationSolution<GosplPopulation> bestSolution = initialSolution;
		ISyntheticPopulationSolution<GosplPopulation> currentSolution = initialSolution;
		this.getNeighborSearchAlgorithm().updatePredicates(initialSolution.getSolution());

		double bestFitness = initialSolution.getFitness(this.getObjectives());
		double stempFitness = bestFitness;

		GSPerformanceUtil gspu = new GSPerformanceUtil(
				"Start Tabu Search algorithm" + "\nPopulation size = " + initialSolution.getSolution().size()
						+ "\nSample size = " + super.getSample().size() + "\nMax iteration = " + this.maxIterations
						+ "\nNeighbor search = " + super.getNeighborSearchAlgorithm().getClass().getSimpleName()
						+ "\nSolution = " + initialSolution.getClass().getSimpleName());
		gspu.setObjectif(this.maxIterations);

		gspu.sysoStempPerformance(0d, this);
		gspu.sysoStempMessage("Random start solution fitness is " + bestFitness);

		int currentIteration = 0;
		int stuckIdx = 0;
		while (currentIteration++ < this.maxIterations && bestFitness > this.getFitnessThreshold()) {

			if (currentIteration % (this.maxIterations / 10d) == 0 || stempFitness - bestFitness > stempFitness / 10) {
				gspu.sysoStempPerformance(currentIteration / gspu.getObjectif(), this);
				gspu.sysoStempMessage("Current fitness is " + bestFitness);
				gspu.sysoStempMessage(
						"Tabu size " + tabuList.getSize() + " " + "| number of stucked iteration " + stuckIdx
								+ " | Current buffer " + super.computeBuffer(bestFitness * stuckIdx, currentSolution));
				stempFitness = bestFitness;
			}

			// gspu.sysoStempPerformance("Retrieve neighbors from current solution", this);
			Collection<ISyntheticPopulationSolution<GosplPopulation>> neighbors =
					currentSolution.getNeighbors(super.getNeighborSearchAlgorithm());

			/*
			 * Optional<ISyntheticPopulationSolution> optionalBestSolution = neighbors.stream() .filter(candidate ->
			 * !this.tabuList.contains(candidate)) .sorted((s1, s2) ->
			 * s1.getFitness(this.getObjectives()).compareTo(s2.getFitness(this.getObjectives()))) .findFirst();
			 *
			 * if(optionalBestSolution.isPresent()) { double candidateFitness =
			 * optionalBestSolution.get().getFitness(this.getObjectives()); if(candidateFitness <
			 * bestSolution.getFitness(this.getObjectives())) { bestSolution = optionalBestSolution.get(); bestFitness =
			 * candidateFitness; stuckIdx = 0; } }
			 *
			 */

			// gspu.sysoStempPerformance("Start eliciting best neighbors", this);
			Map<ISyntheticPopulationSolution<GosplPopulation>, Double> neighborsFitness =
					neighbors.stream().filter(candidate -> !this.tabuList.contains(candidate)).collect(Collectors
							.toMap(Function.identity(), candidate -> candidate.getFitness(this.getObjectives())));

			double candidateFitness = Collections.min(neighborsFitness.values());
			if (candidateFitness < bestFitness) {
				bestSolution =
						neighborsFitness.entrySet().stream().filter(entry -> entry.getValue() == candidateFitness)
								.map(Entry::getKey).findFirst().orElse(null);
				bestFitness = candidateFitness;
				stuckIdx = 0;
			}

			double variable = Math.log1p(stuckIdx++) / Math.log(tabuList.maxSize());

			if (GenstarRandom.getInstance().nextDouble() < variable) {

				currentSolution = currentSolution.getRandomNeighbor(super.getNeighborSearchAlgorithm(),
						super.computeBuffer(bestFitness * stuckIdx, currentSolution));
				stuckIdx = 0;

			}

			tabuList.add(currentSolution);

		}

		return bestSolution;
	}

}
