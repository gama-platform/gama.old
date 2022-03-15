package gospl.algo.co;

import core.metamodel.entity.ADemoEntity;
import gospl.GosplMultitypePopulation;
import gospl.sampler.ISampler;
import gospl.sampler.multilayer.co.ICOMultiLayerSampler;

public class MultiLayerSampleBasedAlgorithm<M extends ICOMultiLayerSampler> implements ICombinatorialOptimizationAlgo<GosplMultitypePopulation<ADemoEntity>, M> {

	@Override
	public ISampler<ADemoEntity> setupCOSampler(
			GosplMultitypePopulation<ADemoEntity> sample,
			boolean withWeights, M sampler) {
		sampler.setSample(sample,withWeights);
		return sampler;
	}
	
	/**
	 * Will draw individual from given level with potential constraints on every layer
	 * @param layer
	 * @param multiLayerSample
	 * @param withWeights
	 * @param sampler
	 * @return
	 */
	public ISampler<ADemoEntity> setupCOSampler(int layer,
			GosplMultitypePopulation<ADemoEntity> multiLayerSample,
			boolean withWeights, M sampler) {
		sampler.setSample(multiLayerSample,withWeights,layer);
		return sampler;
	}

}
