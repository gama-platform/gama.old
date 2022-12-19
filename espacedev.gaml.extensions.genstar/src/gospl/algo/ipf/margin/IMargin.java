/*******************************************************************************************************
 *
 * IMargin.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.algo.ipf.margin;

import java.util.Collection;

import gospl.distribution.matrix.ASegmentedNDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;

/**
 * Higher order abstraction that describes marginal of a n dimensional matrix, which can be a segmented matrix in the
 * sens of {@link ASegmentedNDimensionalMatrix}.
 * <p>
 * A marginal is constructed as follow: we have a referent dimension of type A, and a set of n other related dimension.
 * Marginal of A is the set of all margin composed of set of values from the other n dimension: a margin is a
 * combination of values, one for each dimensions, and margins being all possible conjonction of values for the n
 * dimensions
 *
 * @author kevinchapuis
 *
 * @param <A>
 * @param <V>
 * @param <T>
 */
public interface IMargin<A, V, T extends Number> {

	/**
	 * Gives the dimension that refereed to control aspect of IPF
	 *
	 * @return
	 */
	A getControlDimension();

	/**
	 * Gives the dimension that refereed to seed aspect of IPF
	 *
	 * @return
	 */
	A getSeedDimension();

	/**
	 * Retrieves all marginal descriptors for this dimension
	 *
	 * @param controlMargin
	 * @return
	 */
	Collection<MarginDescriptor> getMarginDescriptors();

	/**
	 * Gives the collection of controls
	 *
	 * @return
	 */
	Collection<AControl<T>> getControls();

	/**
	 * Retrieves abstract control number associated to this seed margin descriptor
	 *
	 * @param seedMargin
	 * @return
	 */
	AControl<T> getControl(MarginDescriptor descriptor);

	/**
	 * Marginal size
	 *
	 * @return
	 */
	int size();

}
