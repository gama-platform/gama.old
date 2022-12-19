/*******************************************************************************************************
 *
 * TriangleRaycastCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.narrowphase;

import static com.bulletphysics.Pools.VECTORS;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DispatcherInfo;
import com.bulletphysics.collision.shapes.TriangleCallback;
import com.bulletphysics.linearmath.VectorUtil;

/**
 *
 * @author jezek2
 */
public abstract class TriangleRaycastCallback implements TriangleCallback {

	// protected final BulletStack stack = BulletStack.get();

	/** The from. */
	public final Vector3f from = new Vector3f();
	
	/** The to. */
	public final Vector3f to = new Vector3f();

	/** The hit fraction. */
	public float hitFraction;

	/**
	 * Instantiates a new triangle raycast callback.
	 *
	 * @param from the from
	 * @param to the to
	 */
	public TriangleRaycastCallback(final Vector3f from, final Vector3f to) {
		this.from.set(from);
		this.to.set(to);
		this.hitFraction = 1f;
	}

	@Override
	public void processTriangle( final Vector3f[] triangle, final int partId,
			final int triangleIndex) {
		Vector3f vert0 = triangle[0];
		Vector3f vert1 = triangle[1];
		Vector3f vert2 = triangle[2];
		Vector3f v10 = VECTORS.get();
		Vector3f v20 = VECTORS.get();
		Vector3f triangleNormal = VECTORS.get();

		try {
			v10.sub(vert1, vert0);
			v20.sub(vert2, vert0);
			triangleNormal.cross(v10, v20);
			float dist = vert0.dot(triangleNormal);
			float dist_a = triangleNormal.dot(from);
			dist_a -= dist;
			float dist_b = triangleNormal.dot(to);
			dist_b -= dist;
			if (dist_a * dist_b >= 0f) return; // same sign
			float proj_length = dist_a - dist_b;
			float distance = dist_a / proj_length;
			// Now we have the intersection point on the plane, we'll see if it's inside the triangle
			// Add an epsilon as a tolerance for the raycast,
			// in case the ray hits exacly on the edge of the triangle.
			// It must be scaled for the triangle size.
			if (distance < hitFraction) {
				float edge_tolerance = triangleNormal.lengthSquared();
				edge_tolerance *= -0.0001f;
				Vector3f point = new Vector3f();
				VectorUtil.setInterpolate3(point, from, to, distance);
				{
					Vector3f v0p = VECTORS.get();
					v0p.sub(vert0, point);
					Vector3f v1p = VECTORS.get();
					v1p.sub(vert1, point);
					Vector3f cp0 = VECTORS.get();
					cp0.cross(v0p, v1p);

					if (cp0.dot(triangleNormal) >= edge_tolerance) {
						Vector3f v2p = VECTORS.get();
						v2p.sub(vert2, point);
						Vector3f cp1 = VECTORS.get();
						cp1.cross(v1p, v2p);
						if (cp1.dot(triangleNormal) >= edge_tolerance) {
							Vector3f cp2 = VECTORS.get();
							cp2.cross(v2p, v0p);

							if (cp2.dot(triangleNormal) >= edge_tolerance) {

								if (dist_a > 0f) {
									hitFraction = reportHit(triangleNormal, distance, partId, triangleIndex);
								} else {
									Vector3f tmp = VECTORS.get();
									tmp.negate(triangleNormal);
									hitFraction = reportHit(tmp, distance, partId, triangleIndex);
									VECTORS.release(tmp);
								}
							}
							VECTORS.release(cp2);
						}
						VECTORS.release(cp1, v2p);
					}
					VECTORS.release(v0p, v1p, cp0);
				}
				VECTORS.release(point);
			}
		} finally {
			VECTORS.release(vert0, vert1, vert2, v10, v20, triangleNormal);
		}
	}

	/**
	 * Report hit.
	 *
	 * @param hitNormalLocal the hit normal local
	 * @param hitFraction the hit fraction
	 * @param partId the part id
	 * @param triangleIndex the triangle index
	 * @return the float
	 */
	public abstract float reportHit(Vector3f hitNormalLocal, float hitFraction, int partId, int triangleIndex);

}
