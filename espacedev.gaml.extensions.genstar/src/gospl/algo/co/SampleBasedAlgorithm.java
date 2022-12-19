/*******************************************************************************************************
 *
 * SampleBasedAlgorithm.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.co;

import gospl.GosplPopulation;
import gospl.sampler.IEntitySampler;

/**
 * The Class SampleBasedAlgorithm.
 */
public class SampleBasedAlgorithm
		implements ICombinatorialOptimizationAlgo<GosplPopulation, IEntitySampler<GosplPopulation>> {

	@Override
	public IEntitySampler<GosplPopulation> setupCOSampler(final GosplPopulation sample, final boolean withWeights,
			final IEntitySampler<GosplPopulation> sampler) {
		sampler.setSample(sample, withWeights);
		return sampler;
	}

}
