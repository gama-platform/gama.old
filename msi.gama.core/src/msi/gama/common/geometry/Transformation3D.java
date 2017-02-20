package msi.gama.common.geometry;

import com.vividsolutions.jts.geom.CoordinateFilter;

import msi.gama.metamodel.shape.GamaPoint;

public interface Transformation3D extends CoordinateFilter {

	default void applyTo(final GamaPoint vertex) {
		filter(vertex);
	}
}
