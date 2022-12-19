/*******************************************************************************************************
 *
 * ISyntheticPopulationSolution.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.co.metamodel.solution;

import java.util.Collection;
import java.util.Set;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.distribution.matrix.INDimensionalMatrix;

/**
 * Represents a solution (or state) of the combinatorial optimization algorithm. It encapsulates a {@link IPopulation}
 * and gives two main insights about it:
 * <p>
 * <ul>
 * <li>the fitness (or energy): how it fits optimization requirement
 * <li>the neighbors: a somehow close but different solution.
 * </ul>
 * <p>
 *
 * WARNING: To define neighboring is the most important challenge in the synthetic population domain. Should it be a
 * population that shift one or more entity ? that shift should concern only one attribute per entity ? should it only
 * concern attribute (have less of this attribute value and more of this other one) ? etc.
 * 
 * @author kevinchapuis
 *
 */
public interface ISyntheticPopulationSolution<Population extends IPopulation<ADemoEntity, Attribute<? extends IValue>>> {

	/**
	 * Get the overall collection of neighbors
	 *
	 * @return
	 */
	<Predicate> Collection<ISyntheticPopulationSolution<Population>>
			getNeighbors(IPopulationNeighborSearch<Population, Predicate> neighborSearch);

	/**
	 * Get the overall collection of neighbors with k neighbors
	 *
	 * @param neighborSearch
	 * @param kNeighbors
	 * @return
	 */
	<Predicate> Collection<ISyntheticPopulationSolution<Population>>
			getNeighbors(IPopulationNeighborSearch<Population, Predicate> neighborSearch, int kNeighbors);

	/**
	 * Get one random neighbor from all possible neighbors
	 *
	 * @return
	 */
	<Predicate> ISyntheticPopulationSolution<Population>
			getRandomNeighbor(IPopulationNeighborSearch<Population, Predicate> neighborSearch);

	/**
	 * Get one random neighbor within a k neighbor
	 *
	 * @param kNeighbors
	 * @return
	 */
	<Predicate> ISyntheticPopulationSolution<Population>
			getRandomNeighbor(IPopulationNeighborSearch<Population, Predicate> neighborSearch, int kNeighbors);

	/**
	 * The fitness of the population or how it fits the requirement of the optimization algorithm. By convention, 0 mean
	 * complete fit and the more it is the worse it is in term of fit
	 *
	 * @param objectives
	 * @return the fitness of this solution
	 */
	Double getFitness(Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives);

	/**
	 * The Absolute error for this solution on any marginals : the vector of absolute difference between population and
	 * targeted marginals
	 *
	 * @param objectives
	 * @return
	 */
	INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> getAbsoluteErrors(
			INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> errorMatrix,
			Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives);

	/**
	 * The synthetic population this solution represent
	 *
	 * @return
	 */
	Population getSolution();

}
