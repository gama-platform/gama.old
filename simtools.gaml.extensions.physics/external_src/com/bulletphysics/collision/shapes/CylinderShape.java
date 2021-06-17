/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library Copyright (c) 2003-2008 Erwin Coumans
 * http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */

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

	protected int upAxis;

	public CylinderShape(final Vector3f halfExtents) {
		super(halfExtents);
		upAxis = 1;
		recalcLocalAabb();
	}

	protected CylinderShape(final Vector3f halfExtents, final boolean unused) {
		super(halfExtents);
	}

	@Override
	public void getAabb(final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		_PolyhedralConvexShape_getAabb(t, aabbMin, aabbMax);
	}

	protected Vector3f cylinderLocalSupportX(final Vector3f halfExtents, final Vector3f v, final Vector3f out) {
		return cylinderLocalSupport(halfExtents, v, 0, 1, 0, 2, out);
	}

	protected Vector3f cylinderLocalSupportY(final Vector3f halfExtents, final Vector3f v, final Vector3f out) {
		return cylinderLocalSupport(halfExtents, v, 1, 0, 1, 2, out);
	}

	protected Vector3f cylinderLocalSupportZ(final Vector3f halfExtents, final Vector3f v, final Vector3f out) {
		return cylinderLocalSupport(halfExtents, v, 2, 0, 2, 1, out);
	}

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

	public int getUpAxis() {
		return upAxis;
	}

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
