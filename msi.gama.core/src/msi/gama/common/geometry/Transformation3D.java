/*******************************************************************************************************
 *
 * msi.gama.common.geometry.Transformation3D.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.geometry;

import org.locationtech.jts.geom.CoordinateFilter;

import msi.gama.metamodel.shape.GamaPoint;

public interface Transformation3D extends CoordinateFilter {

	default void applyTo(final GamaPoint vertex) {
		filter(vertex);
	}
}
