/*******************************************************************************************************
 *
 * BulletShapeConverter.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.extensions.physics.java_version;

import static com.bulletphysics.dom.HeightfieldTerrainShape.ZAXIS;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector3f;

import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;

import com.bulletphysics.collision.shapes.BU_Simplex1to4;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConeShapeZ;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.collision.shapes.CylinderShapeX;
import com.bulletphysics.collision.shapes.CylinderShapeZ;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.TriangleShape;
import com.bulletphysics.collision.shapes.UniformScalingShape;
import com.bulletphysics.dom.HeightfieldTerrainShape;

import gama.extensions.physics.common.IShapeConverter;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.matrix.IField;

/**
 * The Class BulletShapeConverter.
 */
public class BulletShapeConverter implements IShapeConverter<CollisionShape, Vector3f>, IBulletPhysicalEntity {

	/** The shapes. */
	static Map<IShape.Type, ConvexShape> shapes = new HashMap<>();
	
	/** The translations. */
	static Map<IShape.Type, Vector3f> translations = new HashMap<>();

	static {
		Vector3f transHalf = new Vector3f(0, 0, 0.5f);
		Vector3f transFull = new Vector3f(0, 0, 1f);
		Vector3f unity = new Vector3f(1, 1, 1);
		ConvexShape s = new SphereShape(1);
		shapes.put(IShape.Type.SPHERE, s);
		shapes.put(IShape.Type.CIRCLE, s);
		shapes.put(IShape.Type.POINT, s);
		translations.put(IShape.Type.SPHERE, transFull);
		translations.put(IShape.Type.POINT, transFull);
		translations.put(IShape.Type.CIRCLE, transFull);
		s = new BoxShape(unity);
		shapes.put(IShape.Type.CUBE, s);
		translations.put(IShape.Type.CUBE, transHalf);
	}

	@Override
	public void computeTranslation(final IAgent agent, final IShape.Type type, final float depth,
			final Vector3f resultingTranslation, final Vector3f visualTranslation) {
		if (type == IShape.Type.LINECYLINDER) {
			resultingTranslation.set(0, 0, 0);
			visualTranslation.set(0, 0, -depth);
		} else if (shapes.containsKey(type)) {
			resultingTranslation.scale(depth, translations.get(type));
			visualTranslation.set(0, 0, -resultingTranslation.z);
		} else {
			resultingTranslation.set(0, 0, depth / 2);
			visualTranslation.set(0, 0, -depth / 2);
		}
	}

	@Override
	public CollisionShape convertShape(final IShape shape, final IShape.Type type, final float depth) {

		switch (type) {
			case BOX:
			case PLAN:
			case SQUARE:
				return new BoxShape(new Vector3f(shape.getWidth().floatValue() / 2f,
						shape.getHeight().floatValue() / 2f, depth / 2f));
			case CONE:
				// always oriented on the Z axis
				return new ConeShapeZ(shape.getWidth().floatValue() / 2f, depth);
			case LINECYLINDER:
				// oriented on the Y or on the X (default) axis
				LineString line = (LineString) shape.getInnerGeometry();
				LineSegment seg = new LineSegment(line.getCoordinateN(0), line.getCoordinateN(1));
				if (seg.isVertical())
					return new CylinderShape(new Vector3f(depth, (float) seg.getLength() / 2f, depth));
				else
					return new CylinderShapeX(new Vector3f((float) seg.getLength() / 2f, depth, depth));
			case CYLINDER:
				// always oriented on the Z axis
				return new CylinderShapeZ(new Vector3f(shape.getWidth().floatValue() / 2f,
						shape.getHeight().floatValue() / 2f, depth / 2f));
			case PYRAMID:
				break;
			case SPHERE:
			case CIRCLE:
			case POINT:
				return new UniformScalingShape(shapes.get(type), depth);
			case CUBE:
				return new UniformScalingShape(shapes.get(type), depth / 2f);
			default:
				GamaPoint[] points = GeometryUtils.getPointsOf(shape);
				switch (points.length) {
					case 0:
						return null;
					case 1:
						return convertShape(shape, IShape.Type.POINT, depth);
					case 2:
						return new BU_Simplex1to4(toVector(points[0]), toVector(points[1]));
					case 3:
						return new TriangleShape(toVector(points[0]), toVector(points[1]), toVector(points[2]));
					case 4:
						return new BU_Simplex1to4(toVector(points[0]), toVector(points[1]), toVector(points[2]),
								toVector(points[3]));
					default:
						ConvexHullShape result = new ConvexHullShape();
						// ObjectArrayList<Vector3f> vertices = new ObjectArrayList<>(points.length);
						for (final GamaPoint p : points) {
							// vertices.add(toVector(p));
							result.addPoint(toVector(p));
						}
						// ConvexHullShape result = new ConvexHullShape(vertices);
						return result;
				}

		}
		return null;
	}

	@Override
	public CollisionShape convertTerrain(final IScope scope, final IField field, final Double width,
			final Double height, final float depth) {
		double[] minMax = field.getMinMax(null);
		float max = (float) minMax[1], min = (float) minMax[0];
		GamaPoint dim = field.getDimensions();
		float[] data = toFloats(field.getMatrix());

		// AD TODO: verify min and max
		float scale = max == min ? 1f : depth / (max - min);
		HeightfieldTerrainShape shape =
				new HeightfieldTerrainShape((int) dim.x, (int) dim.y, data, scale, min, max, ZAXIS, false);
		shape.setLocalScaling(
				new Vector3f(width.floatValue() / (float) dim.x, height.floatValue() / (float) dim.y, 1f));
		return shape;
	}

}
