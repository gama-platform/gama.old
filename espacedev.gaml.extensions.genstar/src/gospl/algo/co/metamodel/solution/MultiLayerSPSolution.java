/*******************************************************************************************************
 *
 * MultiLayerSPSolution.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo.co.metamodel.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;
import core.util.random.GenstarRandomUtils;
import gospl.GosplMultitypePopulation;
import gospl.algo.co.metamodel.neighbor.IPopulationNeighborSearch;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.validation.GosplIndicatorFactory;

/**
 * A Synthetic population that provides a ready to access exploration feature in term of fitness and neighbors
 *
 * move fitness computation from layer 0 to multi-layered fitness : requires to create a new type of optimization
 * algorithm (or extend the current 3)
 *
 * @author kevinchapuis
 *
 */
public class MultiLayerSPSolution implements ISyntheticPopulationSolution<GosplMultitypePopulation<ADemoEntity>> {

	/** The population. */
	private final GosplMultitypePopulation<ADemoEntity> population;

	/** The layered fitness. */
	private final Map<Integer, Double> layeredFitness;

	/** The layer. */
	private final int layer;

	/** The sub population constant. */
	private final boolean subPopulationConstant;

	/**
	 * Instantiates a new multi layer SP solution.
	 *
	 * @param population
	 *            the population
	 * @param layer
	 *            the layer
	 * @param subPopulationConstant
	 *            the sub population constant
	 */
	public MultiLayerSPSolution(final GosplMultitypePopulation<ADemoEntity> population, final int layer,
			final boolean subPopulationConstant) {
		this.population = population;
		this.layer = layer;
		this.subPopulationConstant = subPopulationConstant;
		this.layeredFitness = new HashMap<>();
	}

	/**
	 * Instantiates a new multi layer SP solution.
	 *
	 * @param population
	 *            the population
	 * @param layer
	 *            the layer
	 * @param subPopulationConstant
	 *            the sub population constant
	 */
	public MultiLayerSPSolution(final Collection<ADemoEntity> population, final int layer,
			final boolean subPopulationConstant) {
		this.population = GosplMultitypePopulation.getMultiPopulation(population, false);
		this.layer = layer;
		this.subPopulationConstant = subPopulationConstant;
		this.layeredFitness = new HashMap<>();
	}

	// ----------------------- NEIGHBOR ----------------------- //

	@Override
	public <U> Collection<ISyntheticPopulationSolution<GosplMultitypePopulation<ADemoEntity>>>
			getNeighbors(final IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>, U> neighborSearch) {
		return this.getNeighbors(neighborSearch, 1);
	}

	@Override
	public <U> Collection<ISyntheticPopulationSolution<GosplMultitypePopulation<ADemoEntity>>> getNeighbors(
			final IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>, U> neighborSearch,
			final int k_neighbors) {
		return neighborSearch.getPredicates().stream()
				.map(u -> new MultiLayerSPSolution(neighborSearch.getNeighbor(this.population, u, k_neighbors, true),
						this.layer, this.subPopulationConstant))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public <U> MultiLayerSPSolution getRandomNeighbor(
			final IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>, U> neighborSearch) {
		return getRandomNeighbor(neighborSearch, 1);
	}

	@Override
	public <U> MultiLayerSPSolution getRandomNeighbor(
			final IPopulationNeighborSearch<GosplMultitypePopulation<ADemoEntity>, U> neighborSearch,
			final int k_neighbors) {
		return new MultiLayerSPSolution(neighborSearch.getNeighbor(this.population,
				GenstarRandomUtils.oneOf(neighborSearch.getPredicates()), k_neighbors, true), this.layer,
				this.subPopulationConstant);
	}

	// ----------------------- FITNESS & SOLUTION ----------------------- //

	@Override
	public Double getFitness(final Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives) {
		return this.getFitness(0, objectives);
	}

	/**
	 * Return the fitness for a given layer
	 *
	 * @param layerNum
	 * @param objectives
	 * @return
	 */
	public Double getFitness(final int layerNum,
			final Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives) {
		if (layeredFitness.containsKey(layerNum)) return layeredFitness.get(layerNum);
		final GSPerformanceUtil gspu = new GSPerformanceUtil("== Fitness Computation ==", Level.TRACE);
		double t = System.currentTimeMillis();

		Set<Attribute<? extends IValue>> objAtt =
				objectives.stream().flatMap(obj -> obj.getDimensions().stream()).collect(Collectors.toSet());

		IPopulation<ADemoEntity, Attribute<? extends IValue>> popLayer = this.getSolution().getSubPopulation(layerNum);
		objAtt = objAtt.stream().filter(att -> popLayer.getPopulationAttributes().stream().anyMatch(att::isLinked))
				.collect(Collectors.toSet());

		gspu.sysoStempMessage("Convert population of " + popLayer.size()
				+ " individual into a contingency based on the distribution of "
				+ objAtt.stream().map(Attribute::getAttributeName).collect(Collectors.joining(", ")) + " attributes",
				this.getClass());

		if (objAtt.isEmpty()) throw new IllegalArgumentException(
				"Population attribute set does not match objectives attributes: " + "\nMarginals: "
						+ objectives.stream().flatMap(obj -> obj.getDimensions().stream())
								.map(Attribute::getAttributeName).collect(Collectors.joining("; "))
						+ "\nPopulation: " + popLayer.getPopulationAttributes().stream()
								.map(Attribute::getAttributeName).collect(Collectors.joining("; ")));
		/*
		 * turn the method into a : "passed this population into this matrix" to be sure to keep same ACoordinate
		 */
		AFullNDimensionalMatrix<Integer> popMatrix =
				GosplNDimensionalMatrixFactory.getFactory().createContingency(objAtt, popLayer);

		gspu.sysoStempMessage("Build population contingency (" + popMatrix.getVal().getValue() + ") for attributes: "
				+ popMatrix.getDimensions().stream().map(Attribute::getAttributeName).collect(Collectors.joining(", "))
				+ " (" + (System.currentTimeMillis() - t) / 1000 + "s)");

		for (IValue val : objAtt.stream().map(att -> att.getValueSpace().getValues().stream().findFirst().get())
				.toList()) {
			gspu.sysoStempMessage("Exemple comparison on value " + val.getStringValue() + ": " + "POP="
					+ popMatrix.getVal(val, true) + " | MARGINAL=" + objectives.iterator().next().getVal(val, true));
		}

		double t1 = System.currentTimeMillis();
		double fitness = objectives.stream()
				.mapToDouble(obj -> GosplIndicatorFactory.getFactory().getIntegerTAE(obj, popMatrix)).sum();
		gspu.sysoStempMessage("Compute fitness for given contingency: " + fitness + " ("
				+ (System.currentTimeMillis() - t1) / 1000 + "s)");

		layeredFitness.put(layerNum, fitness);
		return fitness;
	}

	@Override
	public INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> getAbsoluteErrors(
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> errorMatrix,
			final Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives) {
		return this.getAbsoluteErrors(0, errorMatrix, objectives);
	}

	/**
	 * Return the absolute error on each control marginals
	 *
	 * @param layerNum
	 * @param errorMatrix
	 * @param objectives
	 * @return
	 */
	public INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> getAbsoluteErrors(final int layerNum,
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> errorMatrix,
			final Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> objectives) {

		return GosplIndicatorFactory.getFactory().getAbsoluteErrors(this.getSolution().getSubPopulation(layerNum),
				errorMatrix, objectives);

	}

	/**
	 * Compute the absolute error on each control marginals of each specified layers
	 *
	 * @param errorMatrix
	 * @param objectives
	 * @return
	 */
	public Map<Integer, INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> getAbsoluteErrors(
			final Map<Integer, AFullNDimensionalMatrix<Integer>> errorMatrix,
			final Map<Integer, Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>>> objectives) {
		return objectives.entrySet().stream().collect(Collectors.toMap(Entry::getKey,
				entry -> getAbsoluteErrors(errorMatrix.get(entry.getKey()), entry.getValue())));
	}

	/**
	 * Return the fitness for all given layers
	 *
	 * @param objectives
	 * @return
	 */
	public Map<Integer, Double> getFitness(
			final Map<Integer, Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>>> objectives) {
		return objectives.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, entry -> getFitness(entry.getKey(), entry.getValue())));
	}

	@Override
	public GosplMultitypePopulation<ADemoEntity> getSolution() { return population; }

}
