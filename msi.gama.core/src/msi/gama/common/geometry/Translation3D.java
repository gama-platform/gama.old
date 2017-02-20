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
