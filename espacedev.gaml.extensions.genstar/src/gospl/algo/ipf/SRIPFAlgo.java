/*******************************************************************************************************
 *
 * SRIPFAlgo.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.ipf;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.algo.sr.ISyntheticReconstructionAlgo;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.sampler.IDistributionSampler;
import gospl.sampler.ISampler;

/**
 * The Class SRIPFAlgo.
 */
public class SRIPFAlgo extends AGosplIPF<Double> implements ISyntheticReconstructionAlgo<IDistributionSampler> {

	/**
	 * Instantiates a new SRIPF algo.
	 *
	 * @param seed the seed
	 */
	public SRIPFAlgo(final IPopulation<ADemoEntity, Attribute<? extends IValue>> seed) {
		super(seed);
	}

	/**
	 * Instantiates a new SRIPF algo.
	 *
	 * @param seed the seed
	 * @param step the step
	 * @param delta the delta
	 */
	public SRIPFAlgo(final IPopulation<ADemoEntity, Attribute<? extends IValue>> seed, final int step,
			final double delta) {
		super(seed, step, delta);
	}

	@Override
	public ISampler<ACoordinate<Attribute<? extends IValue>, IValue>> inferSRSampler(
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> matrix,
			final IDistributionSampler sampler) throws IllegalDistributionCreation {

		super.setMarginalMatrix(matrix);
		sampler.setDistribution(process());

		return sampler;
	}

	@Override
	public AFullNDimensionalMatrix<Double> process() {
		if (this.marginals == null || this.marginals.getMatrix().isEmpty()) throw new IllegalArgumentException(
				this.getClass().getSimpleName() + " must define a matrix to setup marginals");
		return process(new GosplNDimensionalMatrixFactory().createDistribution(sampleSeed));
	}

}
