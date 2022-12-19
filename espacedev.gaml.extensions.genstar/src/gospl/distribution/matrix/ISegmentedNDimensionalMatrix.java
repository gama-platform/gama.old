/*******************************************************************************************************
 *
 * ISegmentedNDimensionalMatrix.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.distribution.matrix;

import java.util.Collection;
import java.util.Set;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;

/**
 * The Interface ISegmentedNDimensionalMatrix.
 *
 * @param <T> the generic type
 */
public interface ISegmentedNDimensionalMatrix<T extends Number>
		extends INDimensionalMatrix<Attribute<? extends IValue>, IValue, T> {

	/**
	 * Return the partitioned view of this matrix, i.e. the collection of inner full matrices
	 *
	 * @return
	 */
	Collection<INDimensionalMatrix<Attribute<? extends IValue>, IValue, T>> getMatrices();

	/**
	 * Returns the matrices which involve this val
	 *
	 * @param val
	 */
	Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, T>>
			getMatricesInvolving(Attribute<? extends IValue> att);

}