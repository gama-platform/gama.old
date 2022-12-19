/*******************************************************************************************************
 *
 * MultiLayerGenerator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.generator;

import java.util.Collection;
import java.util.stream.Collectors;

import gospl.GosplEntity;
import gospl.GosplPopulation;
import gospl.distribution.matrix.coordinate.GosplMultiLayerCoordinate;
import gospl.sampler.ISampler;

/**
 * The Class MultiLayerGenerator.
 */
public class MultiLayerGenerator implements ISyntheticGosplPopGenerator {

	/** The sampler. */
	ISampler<GosplMultiLayerCoordinate> sampler;

	/**
	 * Instantiates a new multi layer generator.
	 *
	 * @param sampler the sampler
	 */
	public MultiLayerGenerator(final ISampler<GosplMultiLayerCoordinate> sampler) {
		this.sampler = sampler;
	}

	@Override
	public GosplPopulation generate(final int numberOfIndividual) {
		GosplPopulation pop = new GosplPopulation();
		Collection<GosplMultiLayerCoordinate> coords = sampler.draw(numberOfIndividual);
		for (GosplMultiLayerCoordinate coord : coords) {
			GosplEntity parent = new GosplEntity(coord.getMap());
			parent.addChildren(
					coord.getChilds().stream().map(c -> new GosplEntity(c.getMap())).collect(Collectors.toSet()));
			pop.add(parent);
		}
		return pop;
	}

}
