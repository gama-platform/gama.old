/*******************************************************************************************************
 *
 * COIPFAlgo.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
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
import gospl.algo.co.ICombinatorialOptimizationAlgo;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.sampler.IEntitySampler;
import gospl.sampler.ISampler;

/**
 * The Class COIPFAlgo.
 */
public class COIPFAlgo extends AGosplIPF<Integer> implements
		ICombinatorialOptimizationAlgo<IPopulation<ADemoEntity, Attribute<? extends IValue>>, IEntitySampler<IPopulation<ADemoEntity, Attribute<? extends IValue>>>> {

	/**
	 * Instantiates a new COIPF algo.
	 *
	 * @param seed the seed
	 * @param matrix the matrix
	 */
	public COIPFAlgo(final IPopulation<ADemoEntity, Attribute<? extends IValue>> seed,
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> matrix) {
		super(seed);
		super.setMarginalMatrix(matrix);
	}

	/**
	 * Instantiates a new COIPF algo.
	 *
	 * @param seed the seed
	 * @param matrix the matrix
	 * @param step the step
	 * @param delta the delta
	 */
	public COIPFAlgo(final IPopulation<ADemoEntity, Attribute<? extends IValue>> seed,
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> matrix, final int step,
			final double delta) {
		super(seed, step, delta);
		super.setMarginalMatrix(matrix);
	}

	@Override
	public ISampler<ADemoEntity> setupCOSampler(final IPopulation<ADemoEntity, Attribute<? extends IValue>> sample,
			final boolean withWeights,
			final IEntitySampler<IPopulation<ADemoEntity, Attribute<? extends IValue>>> sampler) {

		sampler.setSample(sample, withWeights);
		sampler.addObjectives(process());

		return sampler;
	}

	@Override
	public AFullNDimensionalMatrix<Integer> process() {
		if (this.marginals == null || this.marginals.getMatrix().isEmpty()) throw new IllegalArgumentException(
				this.getClass().getSimpleName() + " must define a matrix to setup marginals");
		return process(new GosplNDimensionalMatrixFactory().createContingency(sampleSeed));
	}

}
