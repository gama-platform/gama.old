/*******************************************************************************************************
 *
 * Transformation3D.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.geometry;

import org.locationtech.jts.geom.CoordinateFilter;

import msi.gama.metamodel.shape.GamaPoint;

/**
 * The Interface Transformation3D.
 */
public interface Transformation3D extends CoordinateFilter {

	/**
	 * Apply to.
	 *
	 * @param vertex the vertex
	 */
	default void applyTo(final GamaPoint vertex) {
		filter(vertex);
	}
}
