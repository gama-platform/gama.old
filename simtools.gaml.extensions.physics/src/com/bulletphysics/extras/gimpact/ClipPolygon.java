/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This source file is part of GIMPACT Library.
 *
 * For the latest info, see http://gimpact.sourceforge.net/
 *
 * Copyright (c) 2007 Francisco Leon Najera. C.C. 80087371.
 * email: projectileman@yahoo.com
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.linearmath.VectorUtil;
import com.bulletphysics.util.ArrayPool;
import com.bulletphysics.util.ObjectArrayList;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 *
 * @author jezek2
 */
class ClipPolygon {
	
	public static float distance_point_plane(Vector4f plane, Vector3f point) {
		return VectorUtil.dot3(point, plane) - plane.w;
	}

	/**
	 * Vector blending. Takes two vectors a, b, blends them together.
	 */
	public static void vec_blend(Vector3f vr, Vector3f va, Vector3f vb, float blend_factor) {
		vr.scale(1f - blend_factor, va);
		vr.scaleAdd(blend_factor, vb, vr);
	}

	/**
	 * This function calcs the distance from a 3D plane.
	 */
	public static void plane_clip_polygon_collect(Vector3f point0, Vector3f point1, float dist0, float dist1, ObjectArrayList<Vector3f> clipped, int[] clipped_count) {
		boolean _prevclassif = (dist0 > BulletGlobals.SIMD_EPSILON);
		boolean _classif = (dist1 > BulletGlobals.SIMD_EPSILON);
		if (_classif != _prevclassif) {
			float blendfactor = -dist0 / (dist1 - dist0);
			vec_blend(clipped.getQuick(clipped_count[0]), point0, point1, blendfactor);
			clipped_count[0]++;
		}
		if (!_classif) {
			clipped.getQuick(clipped_count[0]).set(point1);
			clipped_count[0]++;
		}
	}

	/**
	 * Clips a polygon by a plane.
	 * 
	 * @return The count of the clipped counts
	 */
	public static int plane_clip_polygon(Vector4f plane, ObjectArrayList<Vector3f> polygon_points, int polygon_point_count, ObjectArrayList<Vector3f> clipped) {
		ArrayPool<int[]> intArrays = ArrayPool.get(int.class);

		int[] clipped_count = intArrays.getFixed(1);
		clipped_count[0] = 0;

		// clip first point
		float firstdist = distance_point_plane(plane, polygon_points.getQuick(0));
		if (!(firstdist > BulletGlobals.SIMD_EPSILON)) {
			clipped.getQuick(clipped_count[0]).set(polygon_points.getQuick(0));
			clipped_count[0]++;
		}

		float olddist = firstdist;
		for (int i=1; i<polygon_point_count; i++) {
			float dist = distance_point_plane(plane, polygon_points.getQuick(i));

			plane_clip_polygon_collect(
					polygon_points.getQuick(i - 1), polygon_points.getQuick(i),
					olddist,
					dist,
					clipped,
					clipped_count);


			olddist = dist;
		}

		// RETURN TO FIRST point

		plane_clip_polygon_collect(
				polygon_points.getQuick(polygon_point_count - 1), polygon_points.getQuick(0),
				olddist,
				firstdist,
				clipped,
				clipped_count);

		int ret = clipped_count[0];
		intArrays.release(clipped_count);
		return ret;
	}

	/**
	 * Clips a polygon by a plane.
	 * 
	 * @param clipped must be an array of 16 points.
	 * @return the count of the clipped counts
	 */
	public static int plane_clip_triangle(Vector4f plane, Vector3f point0, Vector3f point1, Vector3f point2, ObjectArrayList<Vector3f> clipped) {
		ArrayPool<int[]> intArrays = ArrayPool.get(int.class);

		int[] clipped_count = intArrays.getFixed(1);
		clipped_count[0] = 0;

		// clip first point0
		float firstdist = distance_point_plane(plane, point0);
		if (!(firstdist > BulletGlobals.SIMD_EPSILON)) {
			clipped.getQuick(clipped_count[0]).set(point0);
			clipped_count[0]++;
		}

		// point 1
		float olddist = firstdist;
		float dist = distance_point_plane(plane, point1);

		plane_clip_polygon_collect(
				point0, point1,
				olddist,
				dist,
				clipped,
				clipped_count);

		olddist = dist;


		// point 2
		dist = distance_point_plane(plane, point2);

		plane_clip_polygon_collect(
				point1, point2,
				olddist,
				dist,
				clipped,
				clipped_count);
		olddist = dist;



		// RETURN TO FIRST point0
		plane_clip_polygon_collect(
				point2, point0,
				olddist,
				firstdist,
				clipped,
				clipped_count);

		int ret = clipped_count[0];
		intArrays.release(clipped_count);
		return ret;
	}
	
}
