/*******************************************************************************************************
 *
 * AOptimizationAlgorithm.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.metamodel;

import java.util.HashSet;
import java.util.Set;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.algo.co.metamodel.solution.ISyntheticPopulationSolution;
import gospl.distribution.matrix.INDimensionalMatrix;

/**
 * Encapsulation of common properties for all CO algorithm: sample, neighboring search algorithm and objectives
 * <p>
 * Sample: the sample of a portion of the population to draw entity from <br/>
 * Neighbor search algorithm: the algorithm that will be responsible for neighbor population definition <br/>
 * Objectives: the objectives that will make possible fitness computation
 *
 * @author kevinchapuis
 *
 */
public abstract class AOptimizationAlgorithm<Population extends IPopulation<ADemoEntity, Attribute<? extends IValue>>>
		implements IOptimizationAlgorithm<Population, ISyntheticPopulationSolution<Population>> {

	/** The objectives. */
	private final Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives;

	/** The neighbor search. */
	private IPopulationNeighborSearch<Population, ?> neighborSearch;

	/** The sample. */
	private IPopulation<ADemoEntity, Attribute<? extends IValue>> sample;

	/** The fitness threshold. */
	private double fitnessThreshold;

	/** The k neighbor ratio. */
	private double k_neighborRatio = Math.pow(10, -3);

	/**
	 * CO Algorithm without sample to be set futher on
	 *
	 * @param neighborSearch
	 */
	protected AOptimizationAlgorithm(final IPopulationNeighborSearch<Population, ?> neighborSearch,
			final double fitnessThreshold) {
		this.neighborSearch = neighborSearch;
		this.objectives = new HashSet<>();
		this.setFitnessThreshold(fitnessThreshold);
	}

	/**
	 * Instantiates a new a optimization algorithm.
	 *
	 * @param neighborSearch
	 *            the neighbor search
	 * @param sample
	 *            the sample
	 * @param fitnessThreshold
	 *            the fitness threshold
	 */
	protected AOptimizationAlgorithm(final IPopulationNeighborSearch<Population, ?> neighborSearch,
			final Population sample, final double fitnessThreshold) {
		this.neighborSearch = neighborSearch;
		this.setSample(sample);
		this.objectives = new HashSet<>();
		this.setFitnessThreshold(fitnessThreshold);
	}

	@Override
	public Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> getObjectives() { return objectives; }

	@Override
	public void addObjectives(final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> newObjectives) {
		this.objectives.add(newObjectives);
	}

	@Override
	public IPopulation<ADemoEntity, Attribute<? extends IValue>> getSample() { return this.sample; }

	@Override
	public void setSample(final Population sample) {
		this.sample = sample;
		this.neighborSearch.setSample(sample);
	}

	@Override
	public IPopulationNeighborSearch<Population, ?> getNeighborSearchAlgorithm() { return neighborSearch; }

	@Override
	public void setNeighborSearch(final IPopulationNeighborSearch<Population, ?> neighborSearch) {
		this.neighborSearch = neighborSearch;
	}

	@Override
	public double getFitnessThreshold() { return fitnessThreshold; }

	@Override
	public void setFitnessThreshold(final double fitnessThreshold) { this.fitnessThreshold = fitnessThreshold; }

	@Override
	public double getKNeighborRatio() { return k_neighborRatio; }

	@Override
	public void setKNeighborRatio(final double k_neighborRatio) { this.k_neighborRatio = k_neighborRatio; }

	/**
	 * Compute buffer.
	 *
	 * @param fitness
	 *            the fitness
	 * @param solution
	 *            the solution
	 * @return the int
	 */
	public int computeBuffer(final double fitness, final ISyntheticPopulationSolution<Population> solution) {
		return (int) Math
				.round(solution.getSolution().size() * k_neighborRatio * Math.log(1 + fitness * k_neighborRatio));
	}

}
