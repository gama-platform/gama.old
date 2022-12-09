/*******************************************************************************************************
 *
 * DistributionBasedGenerator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.generator;

import java.util.stream.Collectors;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import gospl.GosplEntity;
import gospl.GosplPopulation;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.sampler.IDistributionSampler;
import gospl.sampler.IHierarchicalSampler;
import gospl.sampler.ISampler;

/**
 * A generator that will draw record from a distribution -- i.e. a n dimensional matrix or {@link INDimensionalMatrix}
 * <p>
 * The literature referred to this type of generator to be based on Synthetic reconstruction procedure
 *
 * @see INDimensionalMatrix
 *
 * @author kevinchapuis
 *
 */
public class DistributionBasedGenerator implements ISyntheticGosplPopGenerator {

	/** The sampler. */
	private final ISampler<ACoordinate<Attribute<? extends IValue>, IValue>> sampler;

	/**
	 * Must be constructed with a sampler of {@link ACoordinate}
	 *
	 * @see IDistributionSampler
	 * @see IHierarchicalSampler
	 *
	 * @param sampler
	 */
	public DistributionBasedGenerator(final ISampler<ACoordinate<Attribute<? extends IValue>, IValue>> sampler) {
		this.sampler = sampler;
	}

	@Override
	public GosplPopulation generate(final int numberOfIndividual) {
		return new GosplPopulation(sampler.draw(numberOfIndividual).stream()
				.map(coord -> new GosplEntity(coord.getMap())).collect(Collectors.toSet()));
	}

}
