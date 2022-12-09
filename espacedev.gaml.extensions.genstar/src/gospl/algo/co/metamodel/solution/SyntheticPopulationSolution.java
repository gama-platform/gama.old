/*******************************************************************************************************
 *
 * SyntheticPopulationSolution.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA
 * modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.metamodel.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import core.util.random.GenstarRandomUtils;
import gospl.GosplPopulation;
import gospl.algo.co.metamodel.IOptimizationAlgorithm;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.validation.GosplIndicatorFactory;

/**
 * Abstract Combinatorial Optimization solution to be used in {@link IOptimizationAlgorithm}. Provide essential fitness
 * calculation and the solution to combinatorial optimization problem as a {@link IPopulation}
 *
 * @author kevinchapuis
 *
 */
public class SyntheticPopulationSolution implements ISyntheticPopulationSolution<GosplPopulation> {

	/** The population. */
	protected GosplPopulation population;

	/** The fitness. */
	private double fitness = -1;

	/**
	 * Instantiates a new synthetic population solution.
	 *
	 * @param population
	 *            the population
	 */
	public SyntheticPopulationSolution(final GosplPopulation population) {
		this.population = population;
	}

	/**
	 * Instantiates a new synthetic population solution.
	 *
	 * @param population
	 *            the population
	 */
	public SyntheticPopulationSolution(final Collection<ADemoEntity> population) {
		this(new GosplPopulation(population));
	}

	// ----------------------- NEIGHBOR ----------------------- //

	@Override
	public <U> Collection<ISyntheticPopulationSolution<GosplPopulation>>
			getNeighbors(final IPopulationNeighborSearch<GosplPopulation, U> neighborSearch) {
		return getNeighbors(neighborSearch, 1);
	}

	@Override
	public <U> Collection<ISyntheticPopulationSolution<GosplPopulation>>
			getNeighbors(final IPopulationNeighborSearch<GosplPopulation, U> neighborSearch, final int k_neighbors) {
		return neighborSearch.getPredicates().stream()
				.map(u -> new SyntheticPopulationSolution(
						neighborSearch.getNeighbor(this.population, u, k_neighbors, false)))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public <U> SyntheticPopulationSolution
			getRandomNeighbor(final IPopulationNeighborSearch<GosplPopulation, U> neighborSearch) {
		return getRandomNeighbor(neighborSearch, 1);
	}

	@Override
	public <U> SyntheticPopulationSolution getRandomNeighbor(
			final IPopulationNeighborSearch<GosplPopulation, U> neighborSearch, final int k_neighbors) {
		return new SyntheticPopulationSolution(neighborSearch.getNeighbor(this.population,
				GenstarRandomUtils.oneOf(neighborSearch.getPredicates()), k_neighbors, false));
	}

	// ----------------------- FITNESS & SOLUTION ----------------------- //

	@Override
	public Double getFitness(final Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives) {
		// Only compute once
		if (fitness == -1) {
			AFullNDimensionalMatrix<Integer> popMatrix =
					GosplNDimensionalMatrixFactory.getFactory().createContingency(population);
			fitness = objectives.stream()
					.mapToDouble(obj -> GosplIndicatorFactory.getFactory().getIntegerTAE(obj, popMatrix)).sum();
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
	public GosplPopulation getSolution() { return population; }

	// ----------------------- UTILITY ----------------------- //

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(population);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		SyntheticPopulationSolution other = (SyntheticPopulationSolution) obj;
		return Objects.equals(population, other.population);
	}

}
