/*******************************************************************************************************
 *
 * IOptimizationAlgorithm.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.co.metamodel;

import java.util.Set;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.algo.co.metamodel.solution.ISyntheticPopulationSolution;
import gospl.distribution.matrix.INDimensionalMatrix;

/**
 * Main interfaces for combinatorial optimization algorithm
 *
 * @author kevinchapuis
 *
 */
public interface IOptimizationAlgorithm<Population extends IPopulation<ADemoEntity, Attribute<? extends IValue>>, Solution extends ISyntheticPopulationSolution<Population>> {

	/**
	 * Execute the algorithm to perform targeted optimization.
	 *
	 * @param {@code initialSolution} the start point of the algorithm
	 * @return the best solution found in the given conditions
	 */
	Solution run(Solution initialSolution);

	/**
	 * Retrieve the set of objectives this optimization algorithm is calibrated with
	 *
	 * @return
	 */
	Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> getObjectives();

	/**
	 * Add objectives to assess solution goodness-of-fit
	 *
	 * @param objectives
	 */
	void addObjectives(INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives);

	/**
	 * Returns the sample used to drive combinatorial optimization algorithme
	 *
	 * @return
	 */
	IPopulation<ADemoEntity, Attribute<? extends IValue>> getSample();

	/**
	 * Set the sample of entity to be used to drive combinatorial optimization algorithm
	 *
	 * @param sample
	 */
	void setSample(Population sample);

	/**
	 * Return the algorithm to be used when asking for neighbor populations
	 *
	 * @return
	 */
	IPopulationNeighborSearch<Population, ?> getNeighborSearchAlgorithm();

	/**
	 * Set the algorithm to be used when searching for neighbor populations
	 *
	 * @param neighborSearch
	 */
	void setNeighborSearch(IPopulationNeighborSearch<Population, ?> neighborSearch);

	/**
	 * Gets the fitness threshold.
	 *
	 * @return the fitness threshold
	 */
	double getFitnessThreshold();

	/**
	 * Sets the fitness threshold.
	 *
	 * @param fitnessThreshold the new fitness threshold
	 */
	void setFitnessThreshold(double fitnessThreshold);

	/**
	 * The k neighbor buffer to size default neighbor search. Put it simply, it is the ratio of entity to be switch to
	 * neighbor population
	 *
	 * @see IPopulationNeighborSearch
	 * @return
	 */
	double getKNeighborRatio();

	/**
	 * Set k neighbor buffer ratio
	 *
	 * @param kNeighborRatio
	 */
	void setKNeighborRatio(double kNeighborRatio);

}
