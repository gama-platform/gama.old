/*******************************************************************************************************
 *
 * MultiHillClimbing.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.co.hillclimbing;

import java.util.Map;

import core.metamodel.entity.ADemoEntity;
import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;
import gospl.algo.co.metamodel.AMultiLayerOptimizationAlgorithm;
import gospl.algo.co.metamodel.neighbor.MultiPopulationNeighborSearch;
import gospl.algo.co.metamodel.solution.MultiLayerSPSolution;

/**
 * The Class MultiHillClimbing.
 */
public class MultiHillClimbing extends AMultiLayerOptimizationAlgorithm {

	/** The nb iteration. */
	private final int nbIteration;

	/**
	 * Instantiates a new multi hill climbing.
	 *
	 * @param neighborSearch the neighbor search
	 * @param nbIteration the nb iteration
	 * @param fitnessThreshold the fitness threshold
	 */
	public MultiHillClimbing(final MultiPopulationNeighborSearch neighborSearch, final int nbIteration,
			final double fitnessThreshold) {
		this(neighborSearch, nbIteration, 20d / 100, fitnessThreshold);
	}

	/**
	 * Instantiates a new multi hill climbing.
	 *
	 * @param neighborSearch the neighbor search
	 * @param nbIteration the nb iteration
	 * @param k_neighborRatio the k neighbor ratio
	 * @param fitnessThreshold the fitness threshold
	 */
	public MultiHillClimbing(final MultiPopulationNeighborSearch neighborSearch, final int nbIteration,
			final double k_neighborRatio, final double fitnessThreshold) {
		super(neighborSearch, fitnessThreshold);
		this.nbIteration = nbIteration;
		this.setKNeighborRatio(k_neighborRatio);
	}

	@Override
	public MultiLayerSPSolution run(final MultiLayerSPSolution initialSolution) {
		GSPerformanceUtil gspu = new GSPerformanceUtil(
				"Start HIll Climbing Algorithm\n" + "Population size = " + initialSolution.getSolution().size()
						+ "\nSample size = " + super.getSample().size() + "\nMax iteration = " + nbIteration
						+ "\nNeighbor search = " + super.getNeighborSearchAlgorithm().getClass().getSimpleName()
						+ "\nSolution = " + initialSolution.getClass().getSimpleName(),
				Level.DEBUG);
		gspu.setObjectif(nbIteration);

		MultiLayerSPSolution bestSolution = initialSolution;

		gspu.sysoStempMessage("Initial solution has " + bestSolution.getSolution().stream()
				.flatMap(e -> e.getChildren().stream()).map(e -> (ADemoEntity) e).toList().size() + " individuals");

		// WARNING : strong hypothesis in fitness aggregation, better use pareto frontier
		Double bestFitness = this.getFitness(bestSolution.getFitness(this.getLayeredObjectives()));

		gspu.sysoStempPerformance("Compute initial fitness", this);

		MultiPopulationNeighborSearch pns = (MultiPopulationNeighborSearch) super.getNeighborSearchAlgorithm();
		for (Integer layer : super.getLayeredObjectives().keySet()) {
			super.getLayeredObjectives().get(layer).stream().forEach(lObjectif -> pns.addObjectives(lObjectif, layer));
		}

		pns.updatePredicates(initialSolution.getSolution());

		// final IPopulation<ADemoEntity,Attribute<? extends IValue>> initSubPop =
		// initialSolution.getSolution().getSubPopulation(0);

		int iter = 0;
		int buffer = this.computeBuffer(bestFitness, initialSolution);

		gspu.sysoStempMessage("Initial fitness: " + bestFitness);

		while (iter++ < nbIteration && bestFitness > this.getFitnessThreshold()) {

			MultiLayerSPSolution candidateState =
					bestSolution.getRandomNeighbor(super.getNeighborSearchAlgorithm(), buffer);

			gspu.sysoStempPerformance("Elicit a new neighbor candidate", this);

			double currentFitness = this.getFitness(candidateState.getFitness(this.getLayeredObjectives()));

			gspu.sysoStempPerformance("New neighbor candidate fitness " + currentFitness, this);

			if (currentFitness < bestFitness) {
				bestSolution = candidateState;
				bestFitness = currentFitness;

				pns.updatePredicates(bestSolution.getSolution());
				buffer = super.computeBuffer(bestFitness, bestSolution);
			}
			if (iter % (nbIteration / 10) == 0) {
				gspu.sysoStempPerformance(iter / gspu.getObjectif(), this);
				gspu.sysoStempMessage("Best fitness = " + bestFitness + " (buffer = " + buffer + ") | Pop size = "
						+ bestSolution.getSolution().size());
				gspu.sysoStempMessage("BF = " + bestFitness + " | CF = " + currentFitness);
			}
		}

		return bestSolution;
	}

	/**
	 * Gets the fitness.
	 *
	 * @param layeredFitness the layered fitness
	 * @return the fitness
	 */
	/*
	 * Aggregate layer based fitness, must satisfy identity (e.i. if only one fitness return fitness)
	 */
	private double getFitness(final Map<Integer, Double> layeredFitness) {
		return layeredFitness.values().stream().mapToDouble(f -> f).average().getAsDouble();
	}

}
