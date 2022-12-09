/*******************************************************************************************************
 *
 * TriangleShapeEx.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import static com.bulletphysics.Pools.AABBS;
import static com.bulletphysics.Pools.VECTORS;
import static com.bulletphysics.Pools.VECTORS4;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.collision.shapes.TriangleShape;
import com.bulletphysics.extras.gimpact.BoxCollision.AABB;
import com.bulletphysics.linearmath.Transform;

/**
 *
 * @author jezek2
 */
public class TriangleShapeEx extends TriangleShape {

	/**
	 * Instantiates a new triangle shape ex.
	 */
	public TriangleShapeEx() {
		super();
	}

	/**
	 * Instantiates a new triangle shape ex.
	 *
	 * @param p0 the p 0
	 * @param p1 the p 1
	 * @param p2 the p 2
	 */
	public TriangleShapeEx(final Vector3f p0, final Vector3f p1, final Vector3f p2) {
		super(p0, p1, p2);
	}

	@Override
	public void getAabb(final Transform t, final Vector3f aabbMin, final Vector3f aabbMax) {
		Vector3f tv0 = VECTORS.get(vertices1[0]);
		t.transform(tv0);
		Vector3f tv1 = VECTORS.get(vertices1[1]);
		t.transform(tv1);
		Vector3f tv2 = VECTORS.get(vertices1[2]);
		t.transform(tv2);

		AABB trianglebox = AABBS.get();
		trianglebox.init(tv0, tv1, tv2, collisionMargin);

		aabbMin.set(trianglebox.min);
		aabbMax.set(trianglebox.max);
		VECTORS.release(tv1, tv2, tv0);
	}

	/**
	 * Apply transform.
	 *
	 * @param t the t
	 */
	public void applyTransform(final Transform t) {
		t.transform(vertices1[0]);
		t.transform(vertices1[1]);
		t.transform(vertices1[2]);
	}

	/**
	 * Builds the tri plane.
	 *
	 * @param plane the plane
	 */
	public void buildTriPlane(final Vector4f plane) {
		Vector3f tmp1 = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();

		Vector3f normal = VECTORS.get();
		tmp1.sub(vertices1[1], vertices1[0]);
		tmp2.sub(vertices1[2], vertices1[0]);
		normal.cross(tmp1, tmp2);
		normal.normalize();

		plane.set(normal.x, normal.y, normal.z, vertices1[0].dot(normal));
		VECTORS.release(tmp1, tmp2, normal);
	}

	/**
	 * Overlap test conservative.
	 *
	 * @param other the other
	 * @return true, if successful
	 */
	public boolean overlap_test_conservative(final TriangleShapeEx other) {
		float total_margin = getMargin() + other.getMargin();

		Vector4f plane0 = VECTORS4.get();
		buildTriPlane(plane0);
		Vector4f plane1 = VECTORS4.get();
		other.buildTriPlane(plane1);

		// classify points on other triangle
		float dis0 = ClipPolygon.distance_point_plane(plane0, other.vertices1[0]) - total_margin;

		float dis1 = ClipPolygon.distance_point_plane(plane0, other.vertices1[1]) - total_margin;

		float dis2 = ClipPolygon.distance_point_plane(plane0, other.vertices1[2]) - total_margin;

		if (dis0 > 0.0f && dis1 > 0.0f && dis2 > 0.0f) return false; // classify points on this triangle
		dis0 = ClipPolygon.distance_point_plane(plane1, vertices1[0]) - total_margin;

		dis1 = ClipPolygon.distance_point_plane(plane1, vertices1[1]) - total_margin;

		dis2 = ClipPolygon.distance_point_plane(plane1, vertices1[2]) - total_margin;
		VECTORS4.release(plane0, plane1);
		if (dis0 > 0.0f && dis1 > 0.0f && dis2 > 0.0f) return false;
		return true;
	}

}
