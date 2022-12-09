/*******************************************************************************************************
 *
 * ClipPolygon.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.linearmath.VectorUtil;
import com.bulletphysics.util.ArrayPool;
import java.util.ArrayList;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 *
 * @author jezek2
 */
class ClipPolygon {
	
	/**
	 * Distance point plane.
	 *
	 * @param plane the plane
	 * @param point the point
	 * @return the float
	 */
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
	public static void plane_clip_polygon_collect(Vector3f point0, Vector3f point1, float dist0, float dist1, ArrayList<Vector3f> clipped, int[] clipped_count) {
		boolean _prevclassif = (dist0 > BulletGlobals.SIMD_EPSILON);
		boolean _classif = (dist1 > BulletGlobals.SIMD_EPSILON);
		if (_classif != _prevclassif) {
			float blendfactor = -dist0 / (dist1 - dist0);
			vec_blend(clipped.get(clipped_count[0]), point0, point1, blendfactor);
			clipped_count[0]++;
		}
		if (!_classif) {
			clipped.get(clipped_count[0]).set(point1);
			clipped_count[0]++;
		}
	}

	/**
	 * Clips a polygon by a plane.
	 * 
	 * @return The count of the clipped counts
	 */
	public static int plane_clip_polygon(Vector4f plane, ArrayList<Vector3f> polygon_points, int polygon_point_count, ArrayList<Vector3f> clipped) {
		ArrayPool<int[]> intArrays = ArrayPool.get(int.class);

		int[] clipped_count = intArrays.getFixed(1);
		clipped_count[0] = 0;

		// clip first point
		float firstdist = distance_point_plane(plane, polygon_points.get(0));
		if (!(firstdist > BulletGlobals.SIMD_EPSILON)) {
			clipped.get(clipped_count[0]).set(polygon_points.get(0));
			clipped_count[0]++;
		}

		float olddist = firstdist;
		for (int i=1; i<polygon_point_count; i++) {
			float dist = distance_point_plane(plane, polygon_points.get(i));

			plane_clip_polygon_collect(
					polygon_points.get(i - 1), polygon_points.get(i),
					olddist,
					dist,
					clipped,
					clipped_count);


			olddist = dist;
		}

		// RETURN TO FIRST point

		plane_clip_polygon_collect(
				polygon_points.get(polygon_point_count - 1), polygon_points.get(0),
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
	public static int plane_clip_triangle(Vector4f plane, Vector3f point0, Vector3f point1, Vector3f point2, ArrayList<Vector3f> clipped) {
		ArrayPool<int[]> intArrays = ArrayPool.get(int.class);

		int[] clipped_count = intArrays.getFixed(1);
		clipped_count[0] = 0;

		// clip first point0
		float firstdist = distance_point_plane(plane, point0);
		if (!(firstdist > BulletGlobals.SIMD_EPSILON)) {
			clipped.get(clipped_count[0]).set(point0);
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
