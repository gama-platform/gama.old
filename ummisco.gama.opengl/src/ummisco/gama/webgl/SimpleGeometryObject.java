/*********************************************************************************************
 *
 * 'SimpleGeometryObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.webgl;

import java.awt.Color;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.util.GamaPair;
import msi.gaml.types.Types;

/**
 * A simplified representation of a GeometryObject
 * 
 * @author drogoul
 *
 */
@SuppressWarnings ({ "rawtypes" })
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
			final Double angle, final GamaPoint axis, final GamaPoint location, final GamaPoint size, final Type type,
			final Boolean empty, final List textures) {
		this.geometry = geometry;
		this.color = color;
		this.border = border;
		this.depth = depth;
		this.rotation = new GamaPair<Double, GamaPoint>(angle, axis, Types.FLOAT, Types.POINT);
		this.location = location;
		this.size = size;
		this.type = type;
		this.empty = empty;
		this.textures = textures;
	}

}
