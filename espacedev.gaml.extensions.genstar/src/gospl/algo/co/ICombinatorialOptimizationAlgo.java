/*******************************************************************************************************
 *
 * ICombinatorialOptimizationAlgo.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.co;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import gospl.algo.IGosplConcept;
import gospl.sampler.IEntitySampler;
import gospl.sampler.ISampler;
import gospl.sampler.co.CombinatorialOptimizationSampler;

/**
 * Interface that defines combinatorial optimization general contract. This algorithm encapsulate entity sampler - that
 * {@link IEntitySampler} - which means that they will draw individual entity directly from a sample.
 * <p>
 * Implementing class: {@link SampleBasedAlgorithm}
 * <p>
 *
 * @author kevinchapuis
 *
 * @param <SamplerType>
 *            the type of sample. It should implement {@link IEntitySampler}
 *
 * @see CombinatorialOptimizationSampler
 * @see IGosplConcept.EGosplGenerationConcept#CO
 */
public interface ICombinatorialOptimizationAlgo<SampleType extends IPopulation<ADemoEntity, Attribute<? extends IValue>>, SamplerType extends ISampler<ADemoEntity>> {

	/**
	 * This method must provide a way to build a Combinatorial Optimization (CO) sampler. CO is known in the literature
	 * as the method to generate synthetic population growing a population sample using optimization algorithm
	 *
	 * @param sample
	 *            : the collection of individual entities to draw from
	 * @param withWeights
	 *            : use weights on individual to draw within the sample
	 * @param sampler
	 *            : the sampler
	 * @return
	 */
	ISampler<ADemoEntity> setupCOSampler(SampleType sample, boolean withWeights, SamplerType sampler);

}
