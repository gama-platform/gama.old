package ummisco.gama.webgl;

import java.awt.Color;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.util.GamaPair;

/**
 * A simplified representation of a GeometryObject
 * 
 * @author drogoul
 *
 */
@SuppressWarnings({ "rawtypes" })
public class SimpleGeometryObject {

	Geometry geometry;
	Color color;
	Color border;
	Double depth;
	GamaPair<Double, GamaPoint> rotation;
	GamaPoint location;
	GamaPoint size;
	IShape.Type type;
	Boolean empty;
	List textures;

	public SimpleGeometryObject(final Geometry geometry, final Color color, final Color border, final Double depth,
			final GamaPair<Double, GamaPoint> rotation, final GamaPoint location, final GamaPoint size, final Type type,
			final Boolean empty, final List textures) {
		this.geometry = geometry;
		this.color = color;
		this.border = border;
		this.depth = depth;
		this.rotation = rotation;
		this.location = location;
		this.size = size;
		this.type = type;
		this.empty = empty;
		this.textures = textures;
	}

}
