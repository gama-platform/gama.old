/*******************************************************************************************************
 *
 * IPhysicalEntity.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.extensions.physics.common;

import msi.gama.metamodel.shape.GamaPoint;

/**
 * The Interface IPhysicalEntity.
 *
 * @param <VectorType> the generic type
 */
public interface IPhysicalEntity<VectorType> extends IPhysicalConstants {

	/**
	 * To vector.
	 *
	 * @param v the v
	 * @return the vector type
	 */
	VectorType toVector(final GamaPoint v);

	/**
	 * To gama point.
	 *
	 * @param v the v
	 * @return the gama point
	 */
	GamaPoint toGamaPoint(VectorType v);

}
