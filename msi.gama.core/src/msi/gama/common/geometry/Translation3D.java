/*******************************************************************************************************
 *
 * msi.gama.common.geometry.Translation3D.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.geometry;

import com.vividsolutions.jts.geom.Coordinate;

import msi.gama.metamodel.shape.GamaPoint;

@SuppressWarnings ("unchecked")
public class Translation3D extends GamaPoint implements Transformation3D {

	public Translation3D(final double x, final double y, final double z) {
		super(x, y, z);
	}

	@Override
	public void filter(final Coordinate coord) {
		coord.x += x;
		coord.y += y;
		coord.z += z;
	}

}
