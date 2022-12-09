/*******************************************************************************************************
 *
 * AabbUtil2.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.linearmath;

import static com.bulletphysics.Pools.MATRICES;
import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

/**
 * Utility functions for axis aligned bounding boxes (AABB).
 *
 * @author jezek2
 */
public class AabbUtil2 {

	/**
	 * Aabb expand.
	 *
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 * @param expansionMin the expansion min
	 * @param expansionMax the expansion max
	 */
	public static void aabbExpand(final Vector3f aabbMin, final Vector3f aabbMax, final Vector3f expansionMin,
			final Vector3f expansionMax) {
		aabbMin.add(expansionMin);
		aabbMax.add(expansionMax);
	}

	/**
	 * Outcode.
	 *
	 * @param p the p
	 * @param halfExtent the half extent
	 * @return the int
	 */
	public static int outcode(final Vector3f p, final Vector3f halfExtent) {
		return (p.x < -halfExtent.x ? 0x01 : 0x0) | (p.x > halfExtent.x ? 0x08 : 0x0)
				| (p.y < -halfExtent.y ? 0x02 : 0x0) | (p.y > halfExtent.y ? 0x10 : 0x0)
				| (p.z < -halfExtent.z ? 0x4 : 0x0) | (p.z > halfExtent.z ? 0x20 : 0x0);
	}

	/**
	 * Ray aabb.
	 *
	 * @param rayFrom the ray from
	 * @param rayTo the ray to
	 * @param aabbMin the aabb min
	 * @param aabbMax the aabb max
	 * @param param the param
	 * @param normal the normal
	 * @return true, if successful
	 */
	public static boolean rayAabb(final Vector3f rayFrom, final Vector3f rayTo, final Vector3f aabbMin,
			final Vector3f aabbMax, final float[] param, final Vector3f normal) {
		Vector3f aabbHalfExtent = VECTORS.get();
		Vector3f aabbCenter = VECTORS.get();
		Vector3f source = VECTORS.get();
		Vector3f target = VECTORS.get();
		Vector3f r = VECTORS.get();
		Vector3f hitNormal = VECTORS.get();
		VECTORS.release(aabbCenter, aabbHalfExtent, source, target, r, hitNormal);

		aabbHalfExtent.sub(aabbMax, aabbMin);
		aabbHalfExtent.scale(0.5f);

		aabbCenter.add(aabbMax, aabbMin);
		aabbCenter.scale(0.5f);

		source.sub(rayFrom, aabbCenter);
		target.sub(rayTo, aabbCenter);

		int sourceOutcode = outcode(source, aabbHalfExtent);
		int targetOutcode = outcode(target, aabbHalfExtent);
		if ((sourceOutcode & targetOutcode) == 0x0) {
			float lambda_enter = 0f;
			float lambda_exit = param[0];
			r.sub(target, source);

			float normSign = 1f;
			hitNormal.set(0f, 0f, 0f);
			int bit = 1;

			for (int j = 0; j < 2; j++) {
				for (int i = 0; i != 3; ++i) {
					if ((sourceOutcode & bit) != 0) {
						float lambda =
								(-VectorUtil.getCoord(source, i) - VectorUtil.getCoord(aabbHalfExtent, i) * normSign)
										/ VectorUtil.getCoord(r, i);
						if (lambda_enter <= lambda) {
							lambda_enter = lambda;
							hitNormal.set(0f, 0f, 0f);
							VectorUtil.setCoord(hitNormal, i, normSign);
						}
					} else if ((targetOutcode & bit) != 0) {
						float lambda =
								(-VectorUtil.getCoord(source, i) - VectorUtil.getCoord(aabbHalfExtent, i) * normSign)
										/ VectorUtil.getCoord(r, i);
						// btSetMin(lambda_exit, lambda);
						lambda_exit = Math.min(lambda_exit, lambda);
					}
					bit <<= 1;
				}
				normSign = -1f;
			}
			if (lambda_enter <= lambda_exit) {
				param[0] = lambda_enter;
				normal.set(hitNormal);
				VECTORS.release(aabbCenter, aabbHalfExtent, source, target, r, hitNormal);
				return true;
			}
		}
		VECTORS.release(aabbCenter, aabbHalfExtent, source, target, r, hitNormal);
		return false;
	}

	/**
	 * Conservative test for overlap between two AABBs.
	 */
	public static boolean testAabbAgainstAabb2(final Vector3f aabbMin1, final Vector3f aabbMax1,
			final Vector3f aabbMin2, final Vector3f aabbMax2) {
		boolean overlap = true;
		overlap = aabbMin1.x > aabbMax2.x || aabbMax1.x < aabbMin2.x ? false : overlap;
		overlap = aabbMin1.z > aabbMax2.z || aabbMax1.z < aabbMin2.z ? false : overlap;
		overlap = aabbMin1.y > aabbMax2.y || aabbMax1.y < aabbMin2.y ? false : overlap;
		return overlap;
	}

	/**
	 * Conservative test for overlap between triangle and AABB.
	 */
	public static boolean testTriangleAgainstAabb2(final Vector3f[] vertices, final Vector3f aabbMin,
			final Vector3f aabbMax) {
		Vector3f p1 = vertices[0];
		Vector3f p2 = vertices[1];
		Vector3f p3 = vertices[2];

		if (Math.min(Math.min(p1.x, p2.x), p3.x) > aabbMax.x) return false;
		if (Math.max(Math.max(p1.x, p2.x), p3.x) < aabbMin.x) return false;

		if (Math.min(Math.min(p1.z, p2.z), p3.z) > aabbMax.z) return false;
		if (Math.max(Math.max(p1.z, p2.z), p3.z) < aabbMin.z) return false;

		if (Math.min(Math.min(p1.y, p2.y), p3.y) > aabbMax.y) return false;
		if (Math.max(Math.max(p1.y, p2.y), p3.y) < aabbMin.y) return false;

		return true;
	}

	/**
	 * Transform aabb.
	 *
	 * @param halfExtents the half extents
	 * @param margin the margin
	 * @param t the t
	 * @param aabbMinOut the aabb min out
	 * @param aabbMaxOut the aabb max out
	 */
	public static void transformAabb(final Vector3f halfExtents, final float margin, final Transform t,
			final Vector3f aabbMinOut, final Vector3f aabbMaxOut) {
		Vector3f halfExtentsWithMargin = VECTORS.get();
		halfExtentsWithMargin.x = halfExtents.x + margin;
		halfExtentsWithMargin.y = halfExtents.y + margin;
		halfExtentsWithMargin.z = halfExtents.z + margin;

		Matrix3f abs_b = MATRICES.get(t.basis);
		MatrixUtil.absolute(abs_b);

		Vector3f tmp = VECTORS.get();

		Vector3f center = VECTORS.get(t.origin);
		Vector3f extent = VECTORS.get();
		abs_b.getRow(0, tmp);
		extent.x = tmp.dot(halfExtentsWithMargin);
		abs_b.getRow(1, tmp);
		extent.y = tmp.dot(halfExtentsWithMargin);
		abs_b.getRow(2, tmp);
		extent.z = tmp.dot(halfExtentsWithMargin);

		aabbMinOut.sub(center, extent);
		aabbMaxOut.add(center, extent);
		MATRICES.release(abs_b);
		VECTORS.release(tmp, center, extent, halfExtentsWithMargin);
	}

	/**
	 * Transform aabb.
	 *
	 * @param localAabbMin the local aabb min
	 * @param localAabbMax the local aabb max
	 * @param margin the margin
	 * @param trans the trans
	 * @param aabbMinOut the aabb min out
	 * @param aabbMaxOut the aabb max out
	 */
	public static void transformAabb(final Vector3f localAabbMin, final Vector3f localAabbMax, final float margin,
			final Transform trans, final Vector3f aabbMinOut, final Vector3f aabbMaxOut) {
		assert localAabbMin.x <= localAabbMax.x;
		assert localAabbMin.y <= localAabbMax.y;
		assert localAabbMin.z <= localAabbMax.z;

		Vector3f localHalfExtents = VECTORS.get();
		localHalfExtents.sub(localAabbMax, localAabbMin);
		localHalfExtents.scale(0.5f);

		localHalfExtents.x += margin;
		localHalfExtents.y += margin;
		localHalfExtents.z += margin;

		Vector3f localCenter = VECTORS.get();
		localCenter.add(localAabbMax, localAabbMin);
		localCenter.scale(0.5f);

		Matrix3f abs_b = MATRICES.get(trans.basis);
		MatrixUtil.absolute(abs_b);

		Vector3f center = VECTORS.get(localCenter);
		trans.transform(center);

		Vector3f extent = VECTORS.get();
		Vector3f tmp = VECTORS.get();

		abs_b.getRow(0, tmp);
		extent.x = tmp.dot(localHalfExtents);
		abs_b.getRow(1, tmp);
		extent.y = tmp.dot(localHalfExtents);
		abs_b.getRow(2, tmp);
		extent.z = tmp.dot(localHalfExtents);

		aabbMinOut.sub(center, extent);
		aabbMaxOut.add(center, extent);
		MATRICES.release(abs_b);
		VECTORS.release(tmp, center, extent, localHalfExtents);
	}

}
