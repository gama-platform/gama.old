/*******************************************************************************************************
 *
 * MultiLayerSampleBasedAlgorithm.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.co;

import core.metamodel.entity.ADemoEntity;
import gospl.GosplMultitypePopulation;
import gospl.sampler.ISampler;
import gospl.sampler.multilayer.co.ICOMultiLayerSampler;

/**
 * The Class MultiLayerSampleBasedAlgorithm.
 *
 * @param <M> the generic type
 */
public class MultiLayerSampleBasedAlgorithm<M extends ICOMultiLayerSampler>
		implements ICombinatorialOptimizationAlgo<GosplMultitypePopulation<ADemoEntity>, M> {

	@Override
	public ISampler<ADemoEntity> setupCOSampler(final GosplMultitypePopulation<ADemoEntity> sample,
			final boolean withWeights, final M sampler) {
		sampler.setSample(sample, withWeights);
		return sampler;
	}

	/**
	 * Will draw individual from given level with potential constraints on every layer
	 *
	 * @param layer
	 * @param multiLayerSample
	 * @param withWeights
	 * @param sampler
	 * @return
	 */
	public ISampler<ADemoEntity> setupCOSampler(final int layer,
			final GosplMultitypePopulation<ADemoEntity> multiLayerSample, final boolean withWeights, final M sampler) {
		sampler.setSample(multiLayerSample, withWeights, layer);
		return sampler;
	}

}
