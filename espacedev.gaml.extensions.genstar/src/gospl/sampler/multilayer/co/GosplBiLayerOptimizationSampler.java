package gospl.sampler.multilayer.co;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;
import gospl.GosplMultitypePopulation;
import gospl.algo.co.metamodel.AMultiLayerOptimizationAlgorithm;
import gospl.algo.co.metamodel.solution.MultiLayerSPSolution;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.sampler.co.MicroDataSampler;

/**
 * Draw multi layered entities according to layer marginals
 *
 * @author kevinchapuis
 *
 * @param <A>
 */
public class GosplBiLayerOptimizationSampler<A extends AMultiLayerOptimizationAlgorithm>
		implements ICOMultiLayerSampler {

	private Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> parentObjectives;
	private Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer>> childObjectives;

	private int parentSizeConstraint;
	private int childSizeConstraint;

	private final A algorithm;
	private final MicroDataSampler childSampler;
	private final MicroDataSampler parentSampler;

	public GosplBiLayerOptimizationSampler(final A algorithm) {
		this.algorithm = algorithm;
		this.childSampler = new MicroDataSampler(true);
		this.parentSampler = new MicroDataSampler(true);
	}

	@Override
	public void setSample(final GosplMultitypePopulation<ADemoEntity> sample, final boolean withWeigths) {
		this.childSampler.setSample(sample.getSubPopulation(0), withWeigths);
		this.algorithm.setSample(sample);
	}

	@Override
	public void setSample(final GosplMultitypePopulation<ADemoEntity> sample, final boolean withWeights,
			final int layer) {
		this.checkLayer(layer);

		if (!sample.getEntityLevel().contains(0) || !sample.getEntityLevel().contains(1))
			throw new IllegalArgumentException(
					"Cannot setup a by-layered optimization process without two layered sample");

		IPopulation<ADemoEntity, Attribute<? extends IValue>> childSample = sample.getSubPopulation(0);
		IPopulation<ADemoEntity, Attribute<? extends IValue>> parentSample = sample.getSubPopulation(1);

		this.childSampler.setSample(childSample, withWeights);
		this.parentSampler.setSample(parentSample, withWeights);

		this.algorithm.setSample(sample);
		this.algorithm.setSampledLayer(layer == 1 ? layer : 0);

	}

	@Override
	public void addObjectives(final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives) {
		if ((childObjectives == null && parentObjectives == null)
				|| (childObjectives != null && childObjectives.stream().flatMap(ob -> ob.getDimensions().stream())
						.anyMatch(dim -> objectives.getDimensions().contains(dim)))) {
			this.addObjectives(objectives, 0);
		} else if (parentObjectives != null && parentObjectives.stream().flatMap(ob -> ob.getDimensions().stream())
				.anyMatch(dim -> objectives.getDimensions().contains(dim))) {
			this.addObjectives(objectives, 1);
		}
		throw new IllegalArgumentException(
				"Try to setup an objectif for " + GosplBiLayerOptimizationSampler.class.getCanonicalName()
						+ " sampler but cannot fit it to child or parent layer");
	}

	@Override
	public void addObjectives(final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives,
			final int layer) {
		this.checkLayer(layer);
		this.algorithm.addObjectives(layer, objectives);
		if (layer == 0) {
			if (childObjectives == null) { this.childObjectives = new HashSet<>(); }
			this.childObjectives.add(objectives);
			this.childSizeConstraint = objectives.getVal().getValue();
		}
		if (layer == 1) {
			if (parentObjectives == null) { this.parentObjectives = new HashSet<>(); }
			this.parentObjectives.add(objectives);
			this.parentSizeConstraint = objectives.getVal().getValue();
		}
	}

	@Override
	public ADemoEntity draw() {
		return this.algorithm.getSampledLayer() == 0 ? this.childSampler.draw() : this.parentSampler.draw();
	}

	@Override
	public ADemoEntity drawFromLayer(final int layer) {
		this.checkLayer(layer);
		return layer == 0 ? this.childSampler.draw() : this.parentSampler.draw();
	}

	@Override
	public Collection<ADemoEntity> draw(int numberOfDraw) {

		GSPerformanceUtil gspu = new GSPerformanceUtil("Generating initial random solution from sample", Level.DEBUG);
		Collection<ADemoEntity> startingSolution;

		if (this.algorithm.getSampledLayer() == 0) {
			numberOfDraw = childSizeConstraint == 0 ? numberOfDraw : numberOfDraw < childSizeConstraint ? numberOfDraw
					: childSizeConstraint;
			startingSolution = this.childSampler.draw(numberOfDraw);
		} else if (parentSizeConstraint == 0 && childSizeConstraint > 0) {
			startingSolution = this.parentSampler.drawWithChildrenNumber(numberOfDraw);
		} else if (parentSizeConstraint > 0) {
			startingSolution =
					this.parentSampler.draw(numberOfDraw > parentSizeConstraint ? parentSizeConstraint : numberOfDraw);
		} else {
			startingSolution = this.parentSampler.draw(numberOfDraw);
		}

		gspu.sysoStempMessage("Init solution ok !");

		MultiLayerSPSolution mlSolution = new MultiLayerSPSolution(startingSolution, algorithm.getSampledLayer(), true);

		if (childObjectives != null) {
			this.childObjectives.stream().forEach(cObjectif -> this.algorithm.addObjectives(0, cObjectif));
		}
		if (parentObjectives != null) {
			this.parentObjectives.stream().forEach(pObjectif -> this.algorithm.addObjectives(1, pObjectif));
		}

		gspu.sysoStempMessage("Start running the algorithm");
		return this.algorithm.run(mlSolution).getSolution();
	}

	@Override
	public Collection<ADemoEntity> drawFromLayer(final int layer, final int numberOfDraw) {
		this.checkLayer(layer);
		Collection<ADemoEntity> startingSolution =
				layer == 0 ? this.childSampler.draw(numberOfDraw) : this.parentSampler.draw(numberOfDraw);
		return this.algorithm.run(new MultiLayerSPSolution(startingSolution, algorithm.getSampledLayer(), true))
				.getSolution();
	}

	@Override
	public String toCsv(final String csvSeparator) {
		
		return null;
	}

	@Override
	public boolean checkLayer(final int layer) {
		if (layer == 0 || layer == 1) return true;
		throw new IllegalArgumentException(
				"GosplBiLayerSampler accepts 0 (child) or 1 (parent) layer but not " + layer);
	}

}
