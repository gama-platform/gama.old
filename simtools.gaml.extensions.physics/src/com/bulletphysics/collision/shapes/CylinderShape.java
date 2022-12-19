/*******************************************************************************************************
 *
 * CylinderShape.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;

/**
 * CylinderShape class implements a cylinder shape primitive, centered around the origin. Its central axis aligned with
 * the Y axis. {@link CylinderShapeX} is aligned with the X axis and {@link CylinderShapeZ} around the Z axis.
 *
 * @author jezek2
 */
public class CylinderShape extends BoxShape {

	/** The up axis. */
	protected int upAxis;

	/**
	 * Instantiates a new cylinder shape.
	 *
	 * @param halfExtents the half extents
	 */
	public CylinderShape(final Vector3f halfExtents) {
		super(halfExtents);
		upAxis = 1;
		recalcLocalAabb();
	}

	/**
	 * Instantiates a new cylinder shape.
	 *
	 * @param halfExtents the half extents
	 * @param unused the unused
	 */
	protected CylinderShape(final Vector3f halfExtents, final boolean unused) {
		super(halfExtents);
	}

	@Override
	public void getAabb(final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		_PolyhedralConvexShape_getAabb(t, aabbMin, aabbMax);
	}

	/**
	 * Cylinder local support X.
	 *
	 * @param halfExtents the half extents
	 * @param v the v
	 * @param out the out
	 * @return the vector 3 f
	 */
	protected Vector3f cylinderLocalSupportX(final Vector3f halfExtents, final Vector3f v, final Vector3f out) {
		return cylinderLocalSupport(halfExtents, v, 0, 1, 0, 2, out);
	}

	/**
	 * Cylinder local support Y.
	 *
	 * @param halfExtents the half extents
	 * @param v the v
	 * @param out the out
	 * @return the vector 3 f
	 */
	protected Vector3f cylinderLocalSupportY(final Vector3f halfExtents, final Vector3f v, final Vector3f out) {
		return cylinderLocalSupport(halfExtents, v, 1, 0, 1, 2, out);
	}

	/**
	 * Cylinder local support Z.
	 *
	 * @param halfExtents the half extents
	 * @param v the v
	 * @param out the out
	 * @return the vector 3 f
	 */
	protected Vector3f cylinderLocalSupportZ(final Vector3f halfExtents, final Vector3f v, final Vector3f out) {
		return cylinderLocalSupport(halfExtents, v, 2, 0, 2, 1, out);
	}

	/**
	 * Cylinder local support.
	 *
	 * @param halfExtents the half extents
	 * @param v the v
	 * @param cylinderUpAxis the cylinder up axis
	 * @param XX the xx
	 * @param YY the yy
	 * @param ZZ the zz
	 * @param out the out
	 * @return the vector 3 f
	 */
	private Vector3f cylinderLocalSupport(final Vector3f halfExtents, final Vector3f v, final int cylinderUpAxis,
			final int XX, final int YY, final int ZZ, final Vector3f out) {
		// mapping depends on how cylinder local orientation is
		// extents of the cylinder is: X,Y is for radius, and Z for height

		float radius = VectorUtil.getCoord(halfExtents, XX);
		float halfHeight = VectorUtil.getCoord(halfExtents, cylinderUpAxis);

		float d;

		float s = (float) Math.sqrt(VectorUtil.getCoord(v, XX) * VectorUtil.getCoord(v, XX)
				+ VectorUtil.getCoord(v, ZZ) * VectorUtil.getCoord(v, ZZ));
		if (s != 0f) {
			d = radius / s;
			VectorUtil.setCoord(out, XX, VectorUtil.getCoord(v, XX) * d);
			VectorUtil.setCoord(out, YY, VectorUtil.getCoord(v, YY) < 0f ? -halfHeight : halfHeight);
			VectorUtil.setCoord(out, ZZ, VectorUtil.getCoord(v, ZZ) * d);
			return out;
		} else {
			VectorUtil.setCoord(out, XX, radius);
			VectorUtil.setCoord(out, YY, VectorUtil.getCoord(v, YY) < 0f ? -halfHeight : halfHeight);
			VectorUtil.setCoord(out, ZZ, 0f);
			return out;
		}
	}

	@Override
	public Vector3f localGetSupportingVertexWithoutMargin(final Vector3f vec, final Vector3f out) {
		Vector3f tmp = getHalfExtentsWithoutMargin(VECTORS.get());
		Vector3f result = cylinderLocalSupportY(tmp, vec, out);
		VECTORS.release(tmp);
		return result;
	}

	@Override
	public void batchedUnitVectorGetSupportingVertexWithoutMargin(final Vector3f[] vectors,
			final Vector3f[] supportVerticesOut, final int numVectors) {
		Vector3f tmp;
		for (int i = 0; i < numVectors; i++) {
			tmp = getHalfExtentsWithoutMargin(VECTORS.get());
			cylinderLocalSupportY(tmp, vectors[i], supportVerticesOut[i]);
			VECTORS.release(tmp);
		}
	}

	@Override
	public Vector3f localGetSupportingVertex(final Vector3f vec, final Vector3f out) {
		Vector3f supVertex = out;
		localGetSupportingVertexWithoutMargin(vec, supVertex);

		if (getMargin() != 0f) {
			Vector3f vecnorm = VECTORS.get(vec);
			if (vecnorm.lengthSquared() < BulletGlobals.SIMD_EPSILON * BulletGlobals.SIMD_EPSILON) {
				vecnorm.set(-1f, -1f, -1f);
			}
			vecnorm.normalize();
			supVertex.scaleAdd(getMargin(), vecnorm, supVertex);
			VECTORS.release(vecnorm);
		}
		return out;
	}

	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.CYLINDER_SHAPE_PROXYTYPE;
	}

	/**
	 * Gets the up axis.
	 *
	 * @return the up axis
	 */
	public int getUpAxis() {
		return upAxis;
	}

	/**
	 * Gets the radius.
	 *
	 * @return the radius
	 */
	public float getRadius() {
		Vector3f tmp = getHalfExtentsWithMargin(VECTORS.get());
		float result = tmp.x;
		VECTORS.release(tmp);
		return result;
	}

	@Override
	public String getName() {
		return "CylinderY";
	}

}
