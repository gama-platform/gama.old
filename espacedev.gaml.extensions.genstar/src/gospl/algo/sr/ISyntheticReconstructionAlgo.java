/*******************************************************************************************************
 *
 * ISyntheticReconstructionAlgo.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.sr;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.MappedAttribute;
import core.metamodel.value.IValue;
import gospl.algo.IGosplConcept;
import gospl.algo.IGosplConcept.EGosplGenerationConcept;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.sampler.ISampler;

/**
 * Generic interface of Synthetic Reconstruction algorithm. Basic principle is simple and clear: estimate or approximate
 * the underlying joint distribution of attribute of a single level entity to be drawn from.
 *
 * @see e.g. Müller, K., & Axhausen, K. W. (2010). Population synthesis for microsimulation: State of the art.
 *      Arbeitsberichte Verkehrs-und Raumplanung, 638.
 *
 * @author kevinchapuis
 *
 * @param <SamplerType>
 */
public interface ISyntheticReconstructionAlgo<SamplerType extends ISampler<ACoordinate<Attribute<? extends IValue>, IValue>>> {

	/**
	 * This method must provide a way to build a Synthetic Reconstructive (SR) sampler. SR is known in the literature as
	 * the method to generate synthetic population using probability distribution and monte carlo draws
	 * <p>
	 * WARNING: should provide answers to question like, how to deal with {@link MappedAttribute} & how to deal with
	 * limited information about relationship between attributes
	 * </p>
	 *
	 * @param matrix
	 * @return
	 * @throws IllegalDistributionCreation
	 * @throws GosplSamplerException
	 */
	ISampler<ACoordinate<Attribute<? extends IValue>, IValue>>
			inferSRSampler(INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> matrix, SamplerType sampler)
					throws IllegalDistributionCreation;

	/**
	 * Gets the concept.
	 *
	 * @return the concept
	 */
	default EGosplGenerationConcept getConcept() { return IGosplConcept.EGosplGenerationConcept.SR; }

}
