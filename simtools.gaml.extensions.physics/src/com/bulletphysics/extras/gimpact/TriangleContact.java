/*******************************************************************************************************
 *
 * TriangleContact.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.util.ArrayPool;
import java.util.ArrayList;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 *
 * @author jezek2
 */
public class TriangleContact {
	
	/** The int arrays. */
	private final ArrayPool<int[]> intArrays = ArrayPool.get(int.class);
	
	/** The Constant MAX_TRI_CLIPPING. */
	public static final int MAX_TRI_CLIPPING = 16;

    /** The penetration depth. */
    public float penetration_depth;
    
    /** The point count. */
    public int point_count;
    
    /** The separating normal. */
    public final Vector4f separating_normal = new Vector4f();
    
    /** The points. */
    public Vector3f[] points = new Vector3f[MAX_TRI_CLIPPING];

	/**
	 * Instantiates a new triangle contact.
	 */
	public TriangleContact() {
		for (int i=0; i<points.length; i++) {
			points[i] = new Vector3f();
		}
	}

	/**
	 * Instantiates a new triangle contact.
	 *
	 * @param other the other
	 */
	public TriangleContact(TriangleContact other) {
		copy_from(other);
	}

	/**
	 * Sets the.
	 *
	 * @param other the other
	 */
	public void set(TriangleContact other) {
		copy_from(other);
	}
	
	/**
	 * Copy from.
	 *
	 * @param other the other
	 */
	public void copy_from(TriangleContact other) {
		penetration_depth = other.penetration_depth;
		separating_normal.set(other.separating_normal);
		point_count = other.point_count;
		int i = point_count;
		while ((i--) != 0) {
			points[i].set(other.points[i]);
		}
	}
	
	/**
	 * Classify points that are closer.
	 */
	public void merge_points(Vector4f plane, float margin, ArrayList<Vector3f> points, int point_count) {
		this.point_count = 0;
		penetration_depth = -1000.0f;

		int[] point_indices = intArrays.getFixed(MAX_TRI_CLIPPING);

		for (int _k = 0; _k < point_count; _k++) {
			float _dist = -ClipPolygon.distance_point_plane(plane, points.get(_k)) + margin;

			if (_dist >= 0.0f) {
				if (_dist > penetration_depth) {
					penetration_depth = _dist;
					point_indices[0] = _k;
					this.point_count = 1;
				}
				else if ((_dist + BulletGlobals.SIMD_EPSILON) >= penetration_depth) {
					point_indices[this.point_count] = _k;
					this.point_count++;
				}
			}
		}

		for (int _k = 0; _k < this.point_count; _k++) {
			this.points[_k].set(points.get(point_indices[_k]));
		}
		
		intArrays.release(point_indices);
	}

}
