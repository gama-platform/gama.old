/*******************************************************************************************************
 *
 * NativeBulletShapeConverter.java, in simtools.gaml.extensions.physics, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.extensions.physics.native_version;

import static com.jme3.bullet.PhysicsSpace.AXIS_X;
import static com.jme3.bullet.PhysicsSpace.AXIS_Y;
import static com.jme3.bullet.PhysicsSpace.AXIS_Z;

import java.util.EnumSet;

import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.Box2dShape;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.SimplexCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.math.Vector3f;

import gama.extensions.physics.common.IShapeConverter;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.matrix.IField;

/**
 * The Class NativeBulletShapeConverter.
 */
public class NativeBulletShapeConverter
		implements IShapeConverter<CollisionShape, Vector3f>, INativeBulletPhysicalEntity {

	/** The spheres. */
	EnumSet<IShape.Type> SPHERES =
			EnumSet.of(IShape.Type.SPHERE, IShape.Type.POINT, IShape.Type.CIRCLE, IShape.Type.LINECYLINDER);

	@Override
	public void computeTranslation(final IAgent agent, final IShape.Type type, final float depth,
			final Vector3f aabbTranslation, final Vector3f visualTranslation) {
		if (type == IShape.Type.LINECYLINDER) {
			aabbTranslation.set(0, 0, 0);
			visualTranslation.set(0, 0, -depth);
		} else if (SPHERES.contains(type)) {
			aabbTranslation.set(0, 0, depth);
			visualTranslation.set(0, 0, -depth);
		} else {
			aabbTranslation.set(0, 0, depth / 2);
			visualTranslation.set(0, 0, -depth / 2);
		}
	}

	@Override
	public CollisionShape convertShape(final IShape shape, final IShape.Type type, final float depth) {

		switch (type) {
			case PLAN:
				// AD TODO Ambiguous geometrical description
				// Plane p = new Plane();
				// return new PlaneCollisionShape(plane);
			case BOX:
			case SQUARE:
				if (depth == 0)
					return new Box2dShape(shape.getWidth().floatValue() / 2f, shape.getHeight().floatValue() / 2f);
				else
					return new BoxCollisionShape(new Vector3f(shape.getWidth().floatValue() / 2f,
							shape.getHeight().floatValue() / 2f, depth / 2f));
			case CONE:
				// always oriented on the Z axis
				return new ConeCollisionShape(shape.getWidth().floatValue() / 2f, depth, PhysicsSpace.AXIS_Z);
			case LINECYLINDER:
				// oriented on the Y or on the X (default) axis
				LineString line = (LineString) shape.getInnerGeometry();
				LineSegment seg = new LineSegment(line.getCoordinateN(0), line.getCoordinateN(1));
				if (seg.isVertical())
					return new CylinderCollisionShape(new Vector3f(depth, (float) seg.getLength() / 2f, depth), AXIS_Y);
				else
					return new CylinderCollisionShape(new Vector3f((float) seg.getLength() / 2f, depth, depth), AXIS_X);
			case CYLINDER:
				// always oriented on the Z axis
				return new CylinderCollisionShape(
						new Vector3f(shape.getWidth().floatValue(), shape.getHeight().floatValue(), depth / 2), AXIS_Z);
			case PYRAMID:
				// Todo Should be a HullCollisionShape
				break;
			case SPHERE:
			case CIRCLE:
			case POINT:
				return new SphereCollisionShape(depth);
			case CUBE:
				return new BoxCollisionShape(depth / 2f);
			default:
				GamaPoint[] points = GeometryUtils.getPointsOf(shape);
				switch (points.length) {
					case 0:
						return null;
					case 1:
						return convertShape(shape, IShape.Type.POINT, depth);
					case 2:
						return new SimplexCollisionShape(toVector(points[0]), toVector(points[1]));
					case 3:
						return new SimplexCollisionShape(toVector(points[0]), toVector(points[1]), toVector(points[2]));
					case 4:
						return new SimplexCollisionShape(toVector(points[0]), toVector(points[1]), toVector(points[2]),
								toVector(points[3]));
					default:
						float[] vertices = new float[points.length * 3];
						int i = 0;
						for (final GamaPoint p : points) {
							vertices[i++] = (float) p.x;
							vertices[i++] = (float) p.y;
							vertices[i++] = (float) p.z;
						}
						return new HullCollisionShape(vertices);
				}

		}
		return null;
	}

	@Override
	public CollisionShape convertTerrain(final IScope scope, final IField field, final Double width,
			final Double height, final float depth) {
		double[] minMax = field.getMinMax();
		float max = (float) minMax[1], min = (float) minMax[0];
		GamaPoint dim = field.getDimensions();
		float[] data = toFloats(field.getMatrix());
		// We apply some "translation" here as the center is automatically computed and there is no way to translate the
		// shape. The data is a copy anyway
		for (int i = 0; i < data.length; i++) { data[i] = data[i] - (max - min) / 2; }

		// AD TODO: verify if it is necessary to compute min and max two times
		float scale = max == min ? 1f : depth / (max - min);
		return new HeightfieldCollisionShape((int) dim.y, (int) dim.x, data,
				new Vector3f(width.floatValue() / (float) dim.x, height.floatValue() / (float) dim.y, scale),
				PhysicsSpace.AXIS_Z, false, false, false, false);
	}

}
