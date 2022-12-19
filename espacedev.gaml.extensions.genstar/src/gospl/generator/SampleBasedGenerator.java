/*******************************************************************************************************
 *
 * SampleBasedGenerator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.generator;

import core.metamodel.entity.ADemoEntity;
import gospl.GosplPopulation;
import gospl.sampler.IEntitySampler;
import gospl.sampler.ISampler;
import gospl.sampler.co.CombinatorialOptimizationSampler;
import gospl.sampler.co.MicroDataSampler;

/**
 * Generator based on sample based growth methods: randomly draw individual entity from a sample Optionally drive by an
 * optimization process
 * <p>
 * {@code Gospl} provides {@link MicroDataSampler} and {@link CombinatorialOptimizationSampler}
 *
 * @see IEntitySampler
 *
 * @author kevinchapuis
 *
 */
public class SampleBasedGenerator implements ISyntheticGosplPopGenerator {

	/** The sampler. */
	private final ISampler<ADemoEntity> sampler;

	/**
	 * Must be created using a sampler of entity
	 *
	 * @param sampler
	 */
	public SampleBasedGenerator(final ISampler<ADemoEntity> sampler) {
		this.sampler = sampler;
	}

	@Override
	public GosplPopulation generate(final int numberOfIndividual) {
		return new GosplPopulation(sampler.draw(numberOfIndividual));
	}

}
