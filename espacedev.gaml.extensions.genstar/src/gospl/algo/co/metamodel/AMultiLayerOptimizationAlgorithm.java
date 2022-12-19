/*******************************************************************************************************
 *
 * AMultiLayerOptimizationAlgorithm.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA
 * modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.metamodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.GosplMultitypePopulation;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.algo.co.metamodel.solution.ISyntheticPopulationSolution;
import gospl.algo.co.metamodel.solution.MultiLayerSPSolution;
import gospl.distribution.matrix.INDimensionalMatrix;

/**
 * The Class AMultiLayerOptimizationAlgorithm.
 */
public abstract class AMultiLayerOptimizationAlgorithm
		implements IOptimizationAlgorithm<GosplMultitypePopulation<ADemoEntity>, MultiLayerSPSolution> {

	/** The layered objectives. */
	/*
	 * Constraint on searching for an optimal solution
	 */
	private final Map<Integer, Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>>> layeredObjectives;

	/** The layer size constraint. */
	private final Map<Integer, Integer> layerSizeConstraint;

	/** The sample. */
	private GosplMultitypePopulation<ADemoEntity> sample;

	/** The neighbor search. */
	private IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>, ?> neighborSearch;

	/** The layer. */
	private int layer;

	/** The fitness threshold. */
	private double fitnessThreshold;

	/** The k neighbor ratio. */
	private double kNeighborRatio;

	/**
	 * Instantiates a new a multi layer optimization algorithm.
	 *
	 * @param neighborSearch
	 *            the neighbor search
	 * @param fitnessThreshold
	 *            the fitness threshold
	 */
	protected AMultiLayerOptimizationAlgorithm(
			final IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>, ?> neighborSearch,
			final double fitnessThreshold) {
		this.neighborSearch = neighborSearch;
		this.fitnessThreshold = fitnessThreshold;
		this.layeredObjectives = new HashMap<>();
		this.layerSizeConstraint = new HashMap<>();
	}

	@Override
	public Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> getObjectives() {
		return layeredObjectives.get(0);
	}

	/**
	 * Gets the layered objectives.
	 *
	 * @return the layered objectives
	 */
	public Map<Integer, Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>>> getLayeredObjectives() {
		return layeredObjectives;
	}

	@Override
	public void addObjectives(final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives) {
		this.addObjectives(0, objectives);
	}

	/**
	 * Adds the objectives.
	 *
	 * @param layerNum
	 *            the layer
	 * @param objectives
	 *            the objectives
	 */
	public void addObjectives(final int layerNum,
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives) {
		if (!layeredObjectives.containsKey(layerNum)) { this.layeredObjectives.put(layerNum, new HashSet<>()); }
		this.layeredObjectives.get(layerNum).add(objectives);
		this.layerSizeConstraint.put(layerNum, objectives.getVal().getValue());
	}

	@Override
	public IPopulation<ADemoEntity, Attribute<? extends IValue>> getSample() { return sample; }

	/**
	 * Gets the sample.
	 *
	 * @param layerNum
	 *            the layer
	 * @return the sample
	 */
	public IPopulation<ADemoEntity, Attribute<? extends IValue>> getSample(final int layerNum) {
		return this.sample.getSubPopulation(layerNum);
	}

	@Override
	public void setSample(final GosplMultitypePopulation<ADemoEntity> sample) {
		this.sample = sample;
		this.neighborSearch.setSample(sample);
	}

	@Override
	public IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>, ?> getNeighborSearchAlgorithm() {
		return neighborSearch;
	}

	@Override
	public void setNeighborSearch(
			final IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>, ?> neighborSearch) {
		this.neighborSearch = neighborSearch;
	}

	@Override
	public double getFitnessThreshold() { return fitnessThreshold; }

	@Override
	public void setFitnessThreshold(final double fitnessThreshold) { this.fitnessThreshold = fitnessThreshold; }

	@Override
	public double getKNeighborRatio() { return kNeighborRatio; }

	@Override
	public void setKNeighborRatio(final double k_neighborRatio) { this.kNeighborRatio = k_neighborRatio; }

	/**
	 * Gets the sampled layer.
	 *
	 * @return the sampled layer
	 */
	public int getSampledLayer() { return layer; }

	/**
	 * Sets the sampled layer.
	 *
	 * @param layer
	 *            the new sampled layer
	 */
	public void setSampledLayer(final int layer) { this.layer = layer; }

	/**
	 * Compute buffer.
	 *
	 * @param fitness
	 *            the fitness
	 * @param solution
	 *            the solution
	 * @return the int
	 */
	public int computeBuffer(final double fitness,
			final ISyntheticPopulationSolution<GosplMultitypePopulation<ADemoEntity>> solution) {
		return (int) Math.round(solution.getSolution().getSubPopulationSize(layer)
				* kNeighborRatio /* Math.log(1+fitness*k_neighborRatio) */);
	}

}
