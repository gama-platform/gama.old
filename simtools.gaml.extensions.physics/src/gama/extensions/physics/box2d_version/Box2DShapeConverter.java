/*******************************************************************************************************
 *
 * Box2DShapeConverter.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.extensions.physics.box2d_version;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.locationtech.jts.geom.LineString;

import gama.extensions.physics.common.IShapeConverter;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.runtime.IScope;
import msi.gama.util.matrix.IField;

/**
 * The Class Box2DShapeConverter.
 */
public class Box2DShapeConverter implements IShapeConverter<Shape, Vec2>, IBox2DPhysicalEntity {

	@Override
	public void computeTranslation(final IAgent agent, final Type type, final float depth, final Vec2 aabbTranslation,
			final Vec2 visualTranslation) {
		// Normally not applicable.
	}

	@Override
	public Shape convertShape(final IShape shape, final Type type, final float depth) {

		switch (type) {
			case BOX:
			case PLAN:
			case SQUARE:
			case CUBE:
			case CONE:
			case PYRAMID:
				PolygonShape p = new PolygonShape();
				p.setAsBox(shape.getWidth().floatValue() / 2, shape.getHeight().floatValue() / 2);
				return p;
			case LINECYLINDER:
				// oriented on the Y or on the X (default) axis
				LineString line = (LineString) shape.getInnerGeometry();
				EdgeShape e = new EdgeShape();
				e.set(toVector((GamaPoint) line.getCoordinateN(0)), toVector((GamaPoint) line.getCoordinateN(1)));
				return e;
			case SPHERE:
			case CIRCLE:
			case POINT:
			case CYLINDER:
				CircleShape cc = new CircleShape();
				cc.setRadius(shape.getWidth().floatValue() / 2);
				return cc;
			default:
				GamaPoint[] points = GeometryUtils.getPointsOf(shape);
				switch (points.length) {
					case 0:
						return null;
					case 1:
						return convertShape(shape, IShape.Type.POINT, depth);
					case 2:
						EdgeShape l = new EdgeShape();
						l.set(toVector(points[0]), toVector(points[1]));
						return l;
					default:
						PolygonShape ps = new PolygonShape();
						Vec2[] vertices = new Vec2[points.length];
						for (int i = 0; i < points.length; i++) {
							vertices[i] = toVector(points[i]);
						}
						ps.set(vertices, vertices.length);
						return ps;
				}

		}
	}

	@Override
	public Shape convertTerrain(final IScope scope, final IField field, final Double width, final Double height,
			final float depth) {
		// No way to support "depth" here to build the shape :)
		PolygonShape rectangle = new PolygonShape();
		rectangle.setAsBox(width.floatValue(), height.floatValue());
		return rectangle;
	}

}
