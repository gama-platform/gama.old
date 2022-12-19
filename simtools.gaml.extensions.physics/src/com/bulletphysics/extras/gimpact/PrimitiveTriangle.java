/*******************************************************************************************************
 *
 * PrimitiveTriangle.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import static com.bulletphysics.Pools.CONTACTS;
import static com.bulletphysics.Pools.VECTORS;
import static com.bulletphysics.Pools.VECTORS4;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.linearmath.Transform;
import java.util.ArrayList;

/**
 *
 * @author jezek2
 */
public class PrimitiveTriangle {

	/** The tmp vec list 1. */
	private final ArrayList<Vector3f> tmpVecList1 = new ArrayList<>(TriangleContact.MAX_TRI_CLIPPING);
	
	/** The tmp vec list 2. */
	private final ArrayList<Vector3f> tmpVecList2 = new ArrayList<>(TriangleContact.MAX_TRI_CLIPPING);
	
	/** The tmp vec list 3. */
	private final ArrayList<Vector3f> tmpVecList3 = new ArrayList<>(TriangleContact.MAX_TRI_CLIPPING);

	{
		for (int i = 0; i < TriangleContact.MAX_TRI_CLIPPING; i++) {
			tmpVecList1.add(new Vector3f());
			tmpVecList2.add(new Vector3f());
			tmpVecList3.add(new Vector3f());
		}
	}

	/** The vertices. */
	public final Vector3f[] vertices = new Vector3f[3];
	
	/** The plane. */
	public final Vector4f plane = new Vector4f();
	
	/** The margin. */
	public float margin = 0.01f;

	/**
	 * Instantiates a new primitive triangle.
	 */
	public PrimitiveTriangle() {
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = new Vector3f();
		}
	}

	/**
	 * Sets the.
	 *
	 * @param tri the tri
	 */
	public void set(final PrimitiveTriangle tri) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Builds the tri plane.
	 */
	public void buildTriPlane() {
		Vector3f tmp1 = VECTORS.get();
		Vector3f tmp2 = VECTORS.get();

		Vector3f normal = VECTORS.get();
		tmp1.sub(vertices[1], vertices[0]);
		tmp2.sub(vertices[2], vertices[0]);
		normal.cross(tmp1, tmp2);
		normal.normalize();

		plane.set(normal.x, normal.y, normal.z, vertices[0].dot(normal));
		VECTORS.release(tmp1, tmp2, normal);
	}

	/**
	 * Test if triangles could collide.
	 */
	public boolean overlap_test_conservative(final PrimitiveTriangle other) {
		float total_margin = margin + other.margin;
		// classify points on other triangle
		float dis0 = ClipPolygon.distance_point_plane(plane, other.vertices[0]) - total_margin;

		float dis1 = ClipPolygon.distance_point_plane(plane, other.vertices[1]) - total_margin;

		float dis2 = ClipPolygon.distance_point_plane(plane, other.vertices[2]) - total_margin;

		if (dis0 > 0.0f && dis1 > 0.0f && dis2 > 0.0f) return false; // classify points on this triangle

		dis0 = ClipPolygon.distance_point_plane(other.plane, vertices[0]) - total_margin;

		dis1 = ClipPolygon.distance_point_plane(other.plane, vertices[1]) - total_margin;

		dis2 = ClipPolygon.distance_point_plane(other.plane, vertices[2]) - total_margin;

		if (dis0 > 0.0f && dis1 > 0.0f && dis2 > 0.0f) return false;
		return true;
	}

	/**
	 * Calcs the plane which is paralele to the edge and perpendicular to the triangle plane. This triangle must have
	 * its plane calculated.
	 */
	public void get_edge_plane(final int edge_index, final Vector4f plane) {
		Vector3f e0 = vertices[edge_index];
		Vector3f e1 = vertices[(edge_index + 1) % 3];

		Vector3f tmp = VECTORS.get();
		tmp.set(this.plane.x, this.plane.y, this.plane.z);

		GeometryOperations.edge_plane(e0, e1, tmp, plane);
		VECTORS.release(tmp);
	}

	/**
	 * Apply transform.
	 *
	 * @param t the t
	 */
	public void applyTransform(final Transform t) {
		t.transform(vertices[0]);
		t.transform(vertices[1]);
		t.transform(vertices[2]);
	}

	/**
	 * Clips the triangle against this.
	 *
	 * @param clipped_points
	 *            must have MAX_TRI_CLIPPING size, and this triangle must have its plane calculated.
	 * @return the number of clipped points
	 */
	public int clip_triangle(final PrimitiveTriangle other, final ArrayList<Vector3f> clipped_points) {
		// edge 0
		ArrayList<Vector3f> temp_points = tmpVecList1;

		Vector4f edgeplane = VECTORS4.get();

		get_edge_plane(0, edgeplane);

		int clipped_count = ClipPolygon.plane_clip_triangle(edgeplane, other.vertices[0], other.vertices[1],
				other.vertices[2], temp_points);

		if (clipped_count == 0) return 0;
		ArrayList<Vector3f> temp_points1 = tmpVecList2;

		// edge 1
		get_edge_plane(1, edgeplane);

		clipped_count = ClipPolygon.plane_clip_polygon(edgeplane, temp_points, clipped_count, temp_points1);

		if (clipped_count == 0) return 0; // edge 2
		get_edge_plane(2, edgeplane);

		clipped_count = ClipPolygon.plane_clip_polygon(edgeplane, temp_points1, clipped_count, clipped_points);
		VECTORS4.release(edgeplane);
		return clipped_count;
	}

	/**
	 * Find collision using the clipping method. This triangle and other must have their triangles calculated.
	 */
	public boolean find_triangle_collision_clip_method(final PrimitiveTriangle other, final TriangleContact contacts) {
		float margin = this.margin + other.margin;

		ArrayList<Vector3f> clipped_points = tmpVecList3;

		int clipped_count;
		// create planes
		// plane v vs U points

		TriangleContact contacts1 = CONTACTS.get();

		contacts1.separating_normal.set(plane);

		clipped_count = clip_triangle(other, clipped_points);

		if (clipped_count == 0) return false; // Reject

		// find most deep interval face1
		contacts1.merge_points(contacts1.separating_normal, margin, clipped_points, clipped_count);
		if (contacts1.point_count == 0) return false; // too far
		// Normal pointing to this triangle
		contacts1.separating_normal.x *= -1.f;
		contacts1.separating_normal.y *= -1.f;
		contacts1.separating_normal.z *= -1.f;

		// Clip tri1 by tri2 edges
		TriangleContact contacts2 = CONTACTS.get();
		contacts2.separating_normal.set(other.plane);

		clipped_count = other.clip_triangle(this, clipped_points);

		if (clipped_count == 0) return false; // Reject

		// find most deep interval face1
		contacts2.merge_points(contacts2.separating_normal, margin, clipped_points, clipped_count);
		if (contacts2.point_count == 0) return false; // too far
		if (contacts2.penetration_depth < contacts1.penetration_depth) {
			contacts.copy_from(contacts2);
		} else {
			contacts.copy_from(contacts1);
		}
		CONTACTS.release(contacts1, contacts2);
		return true;
	}

}
