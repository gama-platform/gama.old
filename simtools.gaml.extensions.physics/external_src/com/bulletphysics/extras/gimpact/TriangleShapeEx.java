/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This source file is part of GIMPACT Library.
 *
 * For the latest info, see http://gimpact.sourceforge.net/
 *
 * Copyright (c) 2007 Francisco Leon Najera. C.C. 80087371. email: projectileman@yahoo.com
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

	public TriangleShapeEx() {
		super();
	}

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

	public void applyTransform(final Transform t) {
		t.transform(vertices1[0]);
		t.transform(vertices1[1]);
		t.transform(vertices1[2]);
	}

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
