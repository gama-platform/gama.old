/*******************************************************************************************************
 *
 * YangComposerAlgo.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.composer.yang;

import java.util.HashSet;
import java.util.Set;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import gospl.GosplPopulation;
import gospl.distribution.matrix.INDimensionalMatrix;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class YangComposerAlgo.
 */
public class YangComposerAlgo {

	/** The pop parents. */
	protected GosplPopulation popParents;
	
	/** The pop children. */
	protected GosplPopulation popChildren;
	
	/** The p matching. */
	protected INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> pMatching;

	/**
	 * Instantiates a new yang composer algo.
	 *
	 * @param popParents the pop parents
	 * @param popChildren the pop children
	 * @param pMatching the matching
	 */
	public YangComposerAlgo(final GosplPopulation popParents, final GosplPopulation popChildren,
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> pMatching) {

		this.popParents = popChildren;
		this.popChildren = popChildren;
		this.pMatching = pMatching;
	}

	/**
	 * based on the properties of the parents population, and on the matching probabilities, construct a matrix of how
	 * many children having each probabilities are required
	 */
	public void computeExpectedChildrenProperties() {

		Set<Attribute<? extends IValue>> parentAttributes = new HashSet<>(popParents.getPopulationAttributes());
		parentAttributes.retainAll(pMatching.getDimensions());

		DEBUG.LOG("computing for the dimensions: {} : " + parentAttributes);

		/*
		 *
		 * Map<Set<Attribute<? extends IValue>>,Integer> parents2matchingCandidates = new HashMap<>(); for
		 * (Entry<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<Double>> e:
		 * pMatching.getMatrix().entrySet()) {
		 *
		 * }
		 *
		 * GosplContingencyTable c = new GosplContingencyTable(pMatching.getDimensions()); // for each coordinate of the
		 * probabilities table // 1) compute the
		 *
		 */

	}

}
