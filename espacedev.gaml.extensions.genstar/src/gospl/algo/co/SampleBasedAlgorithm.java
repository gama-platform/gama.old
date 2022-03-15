package gospl.algo.co;

import gospl.GosplPopulation;
import gospl.sampler.IEntitySampler;

public class SampleBasedAlgorithm implements ICombinatorialOptimizationAlgo<GosplPopulation, IEntitySampler<GosplPopulation>> {

	@Override
	public IEntitySampler<GosplPopulation> setupCOSampler(GosplPopulation sample, 
			boolean withWeights, IEntitySampler<GosplPopulation> sampler) {
		sampler.setSample(sample,withWeights);
		return sampler;
	}

}
