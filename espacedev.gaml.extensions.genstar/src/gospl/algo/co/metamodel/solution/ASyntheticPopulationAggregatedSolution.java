/*******************************************************************************************************
 *
 * ASyntheticPopulationAggregatedSolution.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.metamodel.solution;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.validation.GosplIndicatorFactory;

/**
 * Solution of optimization process which encapsulate a IPopulation. Fitness computation is based on aggregated
 * marginals, that is marginals are not recomputed each time but
 *
 * @author kevinchapuis
 *
 */
public abstract class ASyntheticPopulationAggregatedSolution<Population extends IPopulation<ADemoEntity, Attribute<? extends IValue>>>
		implements ISyntheticPopulationSolution<Population> {

	/** The population. */
	private Population population;

	/** The marginals. */
	private AFullNDimensionalMatrix<Integer> marginals;

	/** The fitness. */
	private double fitness = -1;

	/**
	 * Instantiates a new a synthetic population aggregated solution.
	 *
	 * @param population
	 *            the population
	 * @param marginals
	 *            the marginals
	 */
	protected ASyntheticPopulationAggregatedSolution(final Population population,
			final AFullNDimensionalMatrix<Integer> marginals) {
		this.population = population;
		this.marginals = marginals;
	}

	/**
	 * Instantiates a new a synthetic population aggregated solution.
	 *
	 * @param population
	 *            the population
	 */
	protected ASyntheticPopulationAggregatedSolution(final Population population) {
		this(population, new GosplNDimensionalMatrixFactory().createContingency(population));
	}

	// ----------------------- //

	@Override
	public <U> Collection<ISyntheticPopulationSolution<Population>>
			getNeighbors(final IPopulationNeighborSearch<Population, U> neighborSearch) {
		return getNeighbors(neighborSearch, 1);
	}

	/*
	 * @Override public <U> Collection<ISyntheticPopulationSolution<Population>>
	 * getNeighbors(IPopulationNeighborSearch<Population, U> neighborSearch, int k_neighbors) {
	 *
	 * Collection<ISyntheticPopulationSolution<Population>> neighbors = new ArrayList<>();
	 *
	 * for(U predicate : neighborSearch.getPredicates()) { Map<ADemoEntity, ADemoEntity> theSwitch =
	 * neighborSearch.getPairwisedEntities(this.population, predicate, k_neighbors);
	 *
	 * neighbors.add(new ASyntheticPopulationAggregatedSolution<>( neighborSearch.getNeighbor(this.population,
	 * theSwitch), this.makeSwitch(new GosplNDimensionalMatrixFactory() .cloneContingency(this.marginals), theSwitch)));
	 * }
	 *
	 * return neighbors; }
	 */

	@Override
	public <U> ISyntheticPopulationSolution<Population>
			getRandomNeighbor(final IPopulationNeighborSearch<Population, U> neighborSearch) {
		return getRandomNeighbor(neighborSearch, 1);
	}

	/*
	 * @Override public <U> ISyntheticPopulationSolution<Population>
	 * getRandomNeighbor(IPopulationNeighborSearch<Population, U> neighborSearch, int k_neighbors) { Map<ADemoEntity,
	 * ADemoEntity> theSwitch = neighborSearch.getPairwisedEntities(this.population,
	 * GenstarRandomUtils.oneOf(neighborSearch.getPredicates()), k_neighbors);
	 *
	 * return new ASyntheticPopulationAggregatedSolution<>(neighborSearch.getNeighbor(this.population, theSwitch),
	 * this.makeSwitch(new GosplNDimensionalMatrixFactory().cloneContingency(this.marginals), theSwitch)); }
	 */

	// ----------------------- //

	/**
	 * Sp solution provider.
	 *
	 * @return the a synthetic population aggregated solution
	 */
	public abstract ASyntheticPopulationAggregatedSolution<Population> spSolutionProvider();

	// ----------------------- //

	@Override
	public Double getFitness(final Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives) {
		// Only compute once
		if (fitness == -1) {
			fitness = objectives.stream()
					.mapToDouble(obj -> GosplIndicatorFactory.getFactory().getIntegerTAE(obj, marginals)).sum();
		}
		return fitness;
	}

	@Override
	public INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> getAbsoluteErrors(
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> errorMatrix,
			final Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives) {
		throw new UnsupportedOperationException("Ask dev to code it ;)");
	}

	@Override
	public Population getSolution() { return population; }

	// ------------------------ //

	/**
	 * Make switch.
	 *
	 * @param matrix
	 *            the matrix
	 * @param theSwitch
	 *            the the switch
	 * @return the a full N dimensional matrix
	 */
	protected AFullNDimensionalMatrix<Integer> makeSwitch(final AFullNDimensionalMatrix<Integer> matrix,
			final Map<ADemoEntity, ADemoEntity> theSwitch) {

		for (Entry<ADemoEntity, ADemoEntity> entry : theSwitch.entrySet()) {
			matrix.getVal(matrix.getCoordinate(new HashSet<>(entry.getKey().getValues()))).add(-1);
			matrix.getVal(matrix.getCoordinate(new HashSet<>(entry.getValue().getValues()))).add(1);
		}

		return matrix;
	}

}
